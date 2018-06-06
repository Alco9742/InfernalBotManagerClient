package net.nilsghesquiere.runnables;

import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountlistUpdaterRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountlistUpdaterRunnable.class);
	private InfernalBotManagerClient client;
	private volatile boolean stop = false;
	
	public AccountlistUpdaterRunnable(InfernalBotManagerClient client) {
		super();
		this.client = client;
	}
	
	@Override
	public void run() {
		if (!stop) {
			LOGGER.debug("Starting Accountlist Updater in 5 minutes");
			try {
				TimeUnit.MINUTES.sleep(5);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
				Thread.currentThread().interrupt();
			}
			LOGGER.debug("Starting Accountlist Updater");
		}
		while (!stop){
			client.updateAccountsOnServer();
			try {
				TimeUnit.MINUTES.sleep(5);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
				Thread.currentThread().interrupt();
			}
		}
		LOGGER.debug("Successfully closed Accountlist Updater");
	}

	
	public void stop(){
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}
	
}
