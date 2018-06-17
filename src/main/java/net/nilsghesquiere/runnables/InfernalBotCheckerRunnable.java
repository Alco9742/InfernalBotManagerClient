package net.nilsghesquiere.runnables;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.Main;
import net.nilsghesquiere.util.ProgramConstants;
import net.nilsghesquiere.util.ProgramUtil;
import net.nilsghesquiere.util.ProgramVariables;
import net.nilsghesquiere.util.enums.ClientDataStatus;

public class InfernalBotCheckerRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalBotCheckerRunnable.class);
	private final InfernalBotManagerClient infernalBotManagerClient;
	private boolean finalized = false;
	private volatile boolean stop = false;
	private volatile boolean rebootFromManager= false;
	private String processName = "";
	private String oldProcessName = "";
	public static Map<Thread, AccountlistUpdaterRunnable> accountListUpdaterThreadMap = new HashMap<>();
	
	public InfernalBotCheckerRunnable(InfernalBotManagerClient client) {
		super();
		this.infernalBotManagerClient = client;
	}

	@Override
	public void run() {
		firstRunInfernalbot();
		if (!stop) {
			LOGGER.info("Starting InfernalBot CrashChecker");
		}
		while (!stop){
			//recheck pragmas
			infernalBotManagerClient.checkTables();
			//After patches it launches under the legacy name for some reason, we do not want that
			if(ProgramUtil.isProcessRunning(ProgramConstants.LEGACY_LAUNCHER_NAME)){ 
				LOGGER.warn("InfernalBot process is running as 'Infernal Launcher.exe', killing the process");
				ProgramUtil.killLegacyInfernalLauncher();
				ProgramUtil.killAllInfernalProcesses(infernalBotManagerClient.getClient().getClientSettings().getInfernalPath());
			}
			oldProcessName = processName;
			processName = ProgramUtil.getInfernalProcessname(infernalBotManagerClient.getClient().getClientSettings().getInfernalPath());
			if(!processName.equals(oldProcessName)){
				LOGGER.info("Infernal bot process name updated to: " + processName);
			}
			if(!processName.isEmpty()){
				if(!ProgramUtil.isProcessRunning(processName)){
					LOGGER.warn("InfernalBot process not found, restarting client");
					ProgramUtil.killAllInfernalProcesses(infernalBotManagerClient.getClient().getClientSettings().getInfernalPath());
					try{
						if(infernalBotManagerClient.checkConnection() && infernalBotManagerClient.exchangeAccounts()){
							runInfernalbot();
						} else {
							LOGGER.info("Retrying in 1 minute..");
						}
					} catch (NullPointerException e){
						LOGGER.warn("Failure connecting to the server");
					}
				} else {
					//Infernal is running, perform queuer checks here
					if(!infernalBotManagerClient.getClientDataService().hasActiveQueuer()){
						switch(infernalBotManagerClient.getClient().getClientSettings().getActionOnNoQueuers()){
							case REBOOT_WINDOWS:{
								LOGGER.info("No active queuers found, rebooting windows");
								rebootFromManager = true;
								Main.exitWaitRunnable.exit();
								break;
							}
							case RESTART_INFERNALBOT:{
								LOGGER.info("No active queuers found, closing InfernalBot process");
								ProgramUtil.killAllInfernalProcesses(infernalBotManagerClient.getClient().getClientSettings().getInfernalPath());
								break;
							}
							case DO_NOTHING:
								break;
							default:
								break;
						}
					} else {
						if (!infernalBotManagerClient.queuersHaveEnoughAccounts()){
							LOGGER.debug("!infernalBotManagerClient.queuersHaveEnoughAccounts()");
							LOGGER.debug("Deleted this for now, I believe this causes unwanted behaviour");
							/*
							LOGGER.info("Not enough active accounts, closing InfernalBot process");
							infernalBotManagerClient.getClientDataService().deleteAllQueuers();
							ProgramUtil.killAllInfernalProcesses(infernalBotManagerClient.getClient().getClientSettings().getInfernalPath());
							*/
						} else {
							//Everything is running as it should
							Main.managerMonitorRunnable.setClientDataStatus(ClientDataStatus.INFERNAL_RUNNING);
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
			Main.managerMonitorRunnable.setClientDataStatus(ClientDataStatus.INFERNAL_STARTUP);
			if(!ProgramVariables.softStart){
				LOGGER.info("Closing all InfernalBot processes");
				ProgramUtil.killAllInfernalProcesses(infernalBotManagerClient.getClient().getClientSettings().getInfernalPath());
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
			Main.managerMonitorRunnable.setClientDataStatus(ClientDataStatus.INFERNAL_STARTUP);
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
			ProgramUtil.emptyInfernalConfigsFile(infernalBotManagerClient.getClient().getClientSettings().getInfernalPath());
			Path infernalProgramPath = infernalBotManagerClient.getClient().getClientSettings().getInfernalPath().resolve(infernalBotManagerClient.getIniSettings().getInfernalProgramName());
			@SuppressWarnings("unused")
			Process process = new ProcessBuilder(infernalProgramPath.toString()).start();
			LOGGER.info("InfernalBot started");
			startAccountListUpdaterThread();
		} catch (IOException e) {
			LOGGER.debug("Error starting infernalbot");
			LOGGER.debug(e.getMessage());
			return false;
		}
		return true;
	}
	
	private void setInfernalRESTAuth(){
		//Build ulr
		String resquestString = "http://localhost:100/API/auth/v1/token?UserEmail=" + infernalBotManagerClient.getIniSettings().getUsername() + "&Password=" + infernalBotManagerClient.getIniSettings().getPassword();
		//TODO
		String bearerToken = infernalBotManagerClient.getInfernalRestTemplate().getForObject(resquestString, String.class);
	}
	
	private void finishTasks(){
		if(!ProgramVariables.softStop){
			infernalBotManagerClient.setAccountsAsReadyForUse();
			LOGGER.info("Closing all InfernalBot processes");
			ProgramUtil.killAllInfernalProcesses(infernalBotManagerClient.getClient().getClientSettings().getInfernalPath());
		}
		this.finalized = true;
	}
	
	private void startAccountListUpdaterThread(){
		AccountlistUpdaterRunnable updaterRunnable = new AccountlistUpdaterRunnable(infernalBotManagerClient);
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
	
