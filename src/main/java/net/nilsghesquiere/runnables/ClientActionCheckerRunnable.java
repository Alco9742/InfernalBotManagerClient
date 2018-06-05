package net.nilsghesquiere.runnables;

import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.util.InternetAvailabilityChecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientActionCheckerRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientActionCheckerRunnable.class);
	private InfernalBotManagerClient ibmClient;
	private volatile boolean stop = false;
	private boolean connectedToServer = true;
	private boolean connectedToInternet = true;
	
	public ClientActionCheckerRunnable(InfernalBotManagerClient ibmClient) {
		super();
		this.ibmClient = ibmClient;
	}
	
	@Override
	public void run() {
		if (!stop) {
			LOGGER.debug("Starting Client Action Checker in 1 minute");
			try {
			 	TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
				Thread.currentThread().interrupt();
			}
			LOGGER.debug("Starting Client Action Checker");
		}
		while (!stop){
			connectedToServer = ibmClient.getClientService().ping(ibmClient.getClient().getUser().getId(), ibmClient.getClient().getId());
			if (connectedToServer){
				ibmClient.getGui().changeTitle("IBMC - Connected");
			} else {
				connectedToInternet = InternetAvailabilityChecker.isInternetAvailable();
				if(connectedToInternet){
					ibmClient.getGui().changeTitle("IBMC - Disconnected - Server Down");
					ibmClient.getGui().setIconDisconnected();
				} else {
					ibmClient.getGui().changeTitle("IBMC - Disconnected - Internet Down");
					ibmClient.getGui().setIconConnected();
				}
			}
			try {
				TimeUnit.SECONDS.sleep(30);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
				Thread.currentThread().interrupt();
			}
		}
		LOGGER.info("Successfully closed Client Action Checker");
	}

	
	public void stop(){
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}
}
