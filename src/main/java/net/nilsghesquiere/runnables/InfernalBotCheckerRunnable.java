package net.nilsghesquiere.runnables;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.Main;
import net.nilsghesquiere.enums.ClientStatus;
import net.nilsghesquiere.util.ProgramConstants;
import net.nilsghesquiere.util.ProgramUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfernalBotCheckerRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalBotCheckerRunnable.class);
	private final InfernalBotManagerClient client;
	private boolean finalized = false;
	private volatile boolean stop = false;
	private volatile boolean rebootFromManager= false;
	private String processName = "";
	private String oldProcessName = "";
	public static Map<Thread, AccountlistUpdaterRunnable> accountListUpdaterThreadMap = new HashMap<>();
	
	public InfernalBotCheckerRunnable(InfernalBotManagerClient client) {
		super();
		this.client = client;
	}

	@Override
	public void run() {
		firstRunInfernalbot();
		if (!stop) {
			LOGGER.info("Starting InfernalBot CrashChecker");
		}
		while (!stop){
			//recheck pragmas
			client.checkTables();
			//After patches it launches under the legacy name for some reason, we do not want that
			if(ProgramUtil.isProcessRunning(ProgramConstants.LEGACY_LAUNCHER_NAME)){ 
				LOGGER.warn("InfernalBot process is running as 'Infernal Launcher.exe', killing the process");
				ProgramUtil.killProcessIfRunning(ProgramConstants.LEGACY_LAUNCHER_NAME);
			}
			oldProcessName = processName;
			processName = ProgramUtil.getInfernalProcessname(client.getClientSettings().getInfernalMap());
			if(!processName.equals(oldProcessName)){
				LOGGER.info("Infernal bot process name updated to: " + processName);
			}
			if(!processName.isEmpty()){
				if(!ProgramUtil.isProcessRunning(processName)){
					LOGGER.warn("InfernalBot process not found, restarting client");
					try{
						if(client.checkConnection() && client.exchangeAccounts()){
							runInfernalbot();
						} else {
							LOGGER.info("Retrying in 1 minute..");
						}
					} catch (NullPointerException e){
						LOGGER.warn("Failure connecting to the server");
					}
				} else {
					//Infernal is running, perform queuer checks here
					if(!client.getClientDataService().hasActiveQueuer()){
						if(client.getClientSettings().getRebootFromManager()){
							LOGGER.info("No active queuers found, rebooting windows");
							rebootFromManager = true;
							Main.exitWaitRunnable.exit();
						} else {
							LOGGER.info("No active queuers found, closing InfernalBot process");
							ProgramUtil.killProcessIfRunning(processName);
						}
					} else {
						if (!client.queuersHaveEnoughAccounts()){
							LOGGER.info("Not enough active accounts, closing InfernalBot process");
							client.getClientDataService().deleteAllQueuers();
							ProgramUtil.killProcessIfRunning(processName);
						} else {
							//Everything is running as it should
							Main.managerMonitorRunnable.setClientStatus(ClientStatus.INFERNAL_RUNNING);
						}
					}
				}
			} else {
				LOGGER.error("Failure finding current InfernalBot process name");
			}
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e1) {
				LOGGER.debug(e1.getMessage());
				Thread.currentThread().interrupt();
			}
		}
		if(!finalized){
			finishTasks();
		}
		LOGGER.info("Successfully closed InfernalBot CrashChecker");
	}
	
	public void stop(){
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}
	
	public boolean isRebootFromManager() {
		return rebootFromManager;
	}

	private void firstRunInfernalbot(){
		//extra check for the stop here (just to be sure)
		if (!stop){
			Main.managerMonitorRunnable.setClientStatus(ClientStatus.INFERNAL_STARTUP);
			if(!Main.softStart){
				startInfernalBot();
			} else {
				LOGGER.debug("Not starting infernalbot (softstart)");
			}
			LOGGER.info("Starting InfernalBot Crash Checker in 2 minutes");
			try {
				TimeUnit.MINUTES.sleep(2);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
				Thread.currentThread().interrupt();
			}
		}
	}
	
	private void runInfernalbot(){
		//extra check for the stop here (just to be sure)
		if (!stop){
			Main.managerMonitorRunnable.setClientStatus(ClientStatus.INFERNAL_STARTUP);
			startInfernalBot();
			LOGGER.info("Starting InfernalBot Crash Checker in 2 minutes");
			try {
				TimeUnit.MINUTES.sleep(2);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
				Thread.currentThread().interrupt();
			}
		}
	}
	
	private boolean startInfernalBot(){
		try {
			if(!accountListUpdaterThreadMap.isEmpty()){
				stopAccountListUpdaterThread();
			}
			@SuppressWarnings("unused")
			Process process = new ProcessBuilder(client.getClientSettings().getInfernalMap() + client.getClientSettings().getInfernalProgramName()).start();
			LOGGER.info("InfernalBot started");
			startAccountListUpdaterThread();
		} catch (IOException e) {
			LOGGER.debug("Error starting infernalbot");
			LOGGER.debug(e.getMessage());
			return false;
		}
		return true;
	}
	
	private void finishTasks(){
		client.setAccountsAsReadyForUse();
		this.finalized = true;
	}
	
	private void startAccountListUpdaterThread(){
		AccountlistUpdaterRunnable updaterRunnable = new AccountlistUpdaterRunnable(client);
		Thread updaterThread = new Thread(updaterRunnable);
		accountListUpdaterThreadMap.put(updaterThread, updaterRunnable);
		Main.threadMap.put(updaterThread, updaterRunnable);
		updaterThread.setDaemon(false);
		updaterThread.setName("Accountlist Updater Thread");
		updaterThread.start();
	}
	
	private void stopAccountListUpdaterThread(){
		for(Entry<Thread, AccountlistUpdaterRunnable> entry : accountListUpdaterThreadMap.entrySet()){
			entry.getValue().stop();
			entry.getKey().interrupt();
			try {
				entry.getKey().join();
				accountListUpdaterThreadMap.remove(entry.getKey());
				Main.threadMap.remove(entry.getKey());
			} catch (InterruptedException e) {
				LOGGER.error("Failure closing thread");
				LOGGER.debug(e.getMessage());
			}
		}
	}
}
	
