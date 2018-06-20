package net.nilsghesquiere.runnables;

import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.util.InternetAvailabilityChecker;
import net.nilsghesquiere.util.enums.ClientAction;
import net.nilsghesquiere.util.enums.ClientStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientActionCheckerRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientActionCheckerRunnable.class);
	private InfernalBotManagerClient ibmClient;
	private volatile boolean stop = false;
	
	private boolean finalized = false;
	private boolean connectedToServer = true;
	private boolean connectedToInternet = true;
	private ClientStatus status;
	private ClientAction action;
	
	public ClientActionCheckerRunnable(InfernalBotManagerClient ibmClient) {
		super();
		this.ibmClient = ibmClient;
		this.status = ClientStatus.CONNECTED;
	}
	
	@Override
	public void run() {
		if (!stop) {
			LOGGER.debug("Starting Client Action Checker");
		}
		while (!stop){
			//get action by pinging
			action = ping();
			
			//old ping used boolean, now we use disconnected for this
			if(!action.equals(ClientAction.DISCONNECTED)){
				connectedToServer = true;
			} else {
				connectedToServer = false;
			}
			
			if (connectedToServer){
				ibmClient.getGui().changeTitle("IBMC - Connected");
				ibmClient.getGui().setIconConnected();
			} else {
				connectedToInternet = InternetAvailabilityChecker.isInternetAvailable();
				if(connectedToInternet){
					ibmClient.getGui().changeTitle("IBMC - Disconnected - Server Down");
					ibmClient.getGui().setIconDisconnected();
				} else {
					ibmClient.getGui().changeTitle("IBMC - Disconnected - Internet Down");
					ibmClient.getGui().setIconDisconnected();
				}
			}
			
			performAction(action);
			
			try {
				TimeUnit.SECONDS.sleep(30);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
				Thread.currentThread().interrupt();
			}
		}		
		if (!finalized){
			finishTasks();
		}
		LOGGER.debug("Successfully closed Client Action Checker");
	}

	public void stop(){
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}
	
	public void setClientStatus(ClientStatus status){
		this.status = status;
	}
	
	public ClientStatus getClientStatus(){
		return this.status;
	}
	private void finishTasks(){
		this.status = ClientStatus.OFFLINE;
		ping();
		this.finalized = true;
	}
	
	private ClientAction ping(){
		return ibmClient.getClientService().ping(ibmClient.getClient().getUser().getId(), ibmClient.getClient().getId(), status);
	}
	
	
	//figure out how exactly were going to do this
	private void performAction(ClientAction action) {
		switch(action){
			case DISCONNECTED:
				//DO nothing
				break;
			case RUN:
				//Do nothing
				break;
			case SAFESTOP:
				//Command safestop, set action on no queuer to nothing, return run?
				break;
			case SAFESTOP_REBOOT:
				//Command safestop, set action on no queuer to reboot, return run?
				break;
			case SAFESTOP_RESTART_INFERNAL:
				//Command safestop, set action on no queuer to restart infernal, return run?
				break;
			case STOP:
				//Close program
				break;
			case STOP_REBOOT:
				//Close program with reboot
				break;
			default:
				break;
		}
	}
}
