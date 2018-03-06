package net.nilsghesquiere.runnables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.entities.InfernalBotManagerClientSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckInfernalRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalBot Process Checker");
	private final InfernalBotManagerClient client;
	private volatile boolean cancelled;
	
	public CheckInfernalRunnable(InfernalBotManagerClient client) {
		super();
		this.client = client;
	}

	@Override
	public void run() {
		runInfernalbot();
		LOGGER.info("Sleeping for 10 minutes");
		try {
			TimeUnit.MINUTES.sleep(10);
		} catch (InterruptedException e2) {
			LOGGER.info("Error during sleep");
			LOGGER.debug(e2.getMessage());
		}
		LOGGER.info("Starting InfernalBot crash checker");
		while (!cancelled){
			String line ="";
			String pidInfo ="";
			Process p;
			try {
				p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
				BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = input.readLine()) != null) {
					pidInfo+=line; 
				}
				input.close();
				if(!pidInfo.contains(client.getClientSettings().getInfernalProgname())){
					LOGGER.info("InfernalBot process not found, restarting client");
					if(client.checkConnection() && client.accountExchange()){
						runInfernalbot();
					} else {
						LOGGER.info("Error requesting new accounts, trying again in 1 minute");
					}
				}
				try {
					TimeUnit.MINUTES.sleep(1);
				} catch (InterruptedException e1) {
					LOGGER.info("Error during sleep");
					LOGGER.debug(e1.getMessage());
				}
			} catch (IOException e) {
				LOGGER.info("Error checking task list");
				LOGGER.debug(e.getMessage());
			}
		}
	}

	private void runInfernalbot(){
		try {
			Process process = new ProcessBuilder(client.getClientSettings().getInfernalMap() + client.getClientSettings().getInfernalProgname()).start();
			LOGGER.info("InfernalBot started succesfully");
		} catch (IOException e) {
			LOGGER.info("Error starting infernalbot");
			LOGGER.debug(e.getMessage());
		}
	}
	
	public void cancel(){
		cancelled = true;
	}

	public boolean isCancelled() {
		return cancelled;
	}
}
