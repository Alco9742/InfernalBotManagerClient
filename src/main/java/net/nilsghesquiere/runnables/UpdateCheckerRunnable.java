package net.nilsghesquiere.runnables;

import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.Main;
import net.nilsghesquiere.enums.ClientStatus;

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
			Main.serverUpToDate = client.checkServerVersion();
			if(client.checkUpdateNow()){
				if(!client.checkClientVersion(false)){
					LOGGER.info("Update found, commencing updater");
					Main.managerMonitorRunnable.setClientStatus(ClientStatus.UPDATE);
					Main.softStop = true;
					client.updateClient();
					Main.exitWaitRunnable.exit();
				}
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
