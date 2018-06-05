package net.nilsghesquiere.runnables;

import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.Main;
import net.nilsghesquiere.util.ProgramVariables;
import net.nilsghesquiere.util.enums.ClientStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCheckerRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCheckerRunnable.class);
	private InfernalBotManagerClient client;
	private volatile boolean stop = false;
	
	public UpdateCheckerRunnable(InfernalBotManagerClient client) {
		super();
		this.client = client;
	}
	
	@Override
	public void run() {
		if (!stop) {
			LOGGER.debug("Starting Update Checker in 2 minutes");
			try {
			 	TimeUnit.MINUTES.sleep(2);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
				Thread.currentThread().interrupt();
			}
			LOGGER.debug("Starting Update Checker");
		}
		while (!stop){
			ProgramVariables.serverUpToDate = client.checkServerVersion();
			try{
				if(client.checkUpdateNow()){
					if(!client.checkClientVersion(false)){
						LOGGER.info("Update found, commencing updater");
						Main.managerMonitorRunnable.setClientStatus(ClientStatus.UPDATE);
						ProgramVariables.softStop = true;
						client.updateClient();
						Main.exitWaitRunnable.exit();
					}
				}
			} catch (NullPointerException e){
				LOGGER.warn("Failure connecting to the server");
			}
			try {
				TimeUnit.MINUTES.sleep(2);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
				Thread.currentThread().interrupt();
			}
		}
		LOGGER.info("Successfully closed Update Checker");
	}

	
	public void stop(){
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}
}
