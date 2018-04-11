package net.nilsghesquiere.runnables;

import java.io.IOException;
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
	private volatile boolean stop = false;
	private volatile boolean rebootFromManager= false;
	private String processName = "";
	private String oldProcessName = "";
	
	public InfernalBotCheckerRunnable(InfernalBotManagerClient client) {
		super();
		this.client = client;
	}

	@Override
	public void run() {
		runInfernalbot();
		if (!stop) {
			LOGGER.info("Starting InfernalBot CrashChecker");
		}
		while (!stop){
			oldProcessName = processName;
			processName = ProgramUtil.getInfernalProcessname(client.getClientSettings().getInfernalMap());
			if(!processName.equals(oldProcessName)){
				LOGGER.info("Infernal bot process name updated to: " + processName);
			}
			if(!processName.isEmpty()){
				if(!ProgramUtil.isProcessRunning(processName) && !ProgramUtil.isProcessRunning(ProgramConstants.LEGACY_LAUNCHER_NAME)){ //Check legacy launchername aswel (launches this after updates)
					LOGGER.warn("InfernalBot process not found, restarting client");
					if(client.checkConnection() && client.exchangeAccounts()){
						runInfernalbot();
					} else {
						LOGGER.info("Retrying in 1 minute..");
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
							ProgramUtil.killProcessIfRunning(ProgramConstants.LEGACY_LAUNCHER_NAME);
						}
					} else {
						Main.managerMonitorRunnable.setClientStatus(ClientStatus.INFERNAL_RUNNING);
					}
				}
			} else {
				LOGGER.error("Failure finding current InfernalBot process name");
			}
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e1) {
				LOGGER.debug(e1.getMessage());
			}
		}
		client.setAccountsAsReadyForUse();
		LOGGER.info("Successfully closed InfernalBot CrashChecker");
	}

	private void runInfernalbot(){
		Main.managerMonitorRunnable.setClientStatus(ClientStatus.INFERNAL_STARTUP);
		startInfernalBot();
		LOGGER.info("Starting InfernalBot Crash Checker in 2 minutes");
		try {
			TimeUnit.MINUTES.sleep(2);
		} catch (InterruptedException e2) {
			LOGGER.debug(e2.getMessage());
		}
	}
	
	private boolean startInfernalBot(){
		try {
			@SuppressWarnings("unused")
			Process process = new ProcessBuilder(client.getClientSettings().getInfernalMap() + client.getClientSettings().getInfernalProgramName()).start();
			LOGGER.info("InfernalBot started");
		} catch (IOException e) {
			LOGGER.info("Error starting infernalbot");
			LOGGER.debug(e.getMessage());
			return false;
		}
		return true;
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
}
	
