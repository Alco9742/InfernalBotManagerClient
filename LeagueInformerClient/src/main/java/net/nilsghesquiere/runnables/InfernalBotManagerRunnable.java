package net.nilsghesquiere.runnables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfernalBotManagerRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalBotManagerRunnable");
	private final InfernalBotManagerClient client;
	private volatile boolean stop = false;
	
	public InfernalBotManagerRunnable(InfernalBotManagerClient client) {
		super();
		this.client = client;
	}

	@Override
	public void run() {
		client.scheduleReboot();
		//Attempt to get accounts, retry if fail
		boolean connected = client.checkConnection() && client.backUpInfernalDatabase() && client.setInfernalSettings() && client.accountExchange();
		while (!connected && !stop){
			try {
				LOGGER.info("Retrying in 1 minute...");
				TimeUnit.MINUTES.sleep(1);
				connected = (client.checkConnection() && client.backUpInfernalDatabase() && client.setInfernalSettings() && client.accountExchange());
			} catch (InterruptedException e) {
				LOGGER.error("Failure during sleep");
				LOGGER.debug(e.getMessage());
			}
		}
		if (!stop){
			runInfernalbot();
			LOGGER.info("Sleeping for 5 minutes");
			try {
				TimeUnit.MINUTES.sleep(5);
			} catch (InterruptedException e2) {
				LOGGER.error("Failure during sleep");
				LOGGER.debug(e2.getMessage());
			}
			LOGGER.info("Starting InfernalBot crash checker");
		}
		while (!stop){
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
				if(!pidInfo.contains(client.getClientSettings().getInfernalProg())){
					LOGGER.warn("InfernalBot process not found, restarting client");
					if(client.checkConnection() && client.accountExchange()){
						runInfernalbot();
					} else {
						LOGGER.info("Retrying in 1 minute..");
					}
				}
				try {
					TimeUnit.MINUTES.sleep(1);
				} catch (InterruptedException e1) {
					LOGGER.error("Failure during sleep");
					LOGGER.debug(e1.getMessage());
				}
			} catch (IOException e) {
				LOGGER.error("Failure checking task list");
				LOGGER.debug(e.getMessage());
			}
		}
		client.setAccountsAsReadyForUse();
		LOGGER.info("Successfully closed thread");
	}

	private void runInfernalbot(){
		try {
			@SuppressWarnings("unused")
			Process process = new ProcessBuilder(client.getClientSettings().getInfernalMap() + client.getClientSettings().getInfernalProg()).start();
			LOGGER.info("InfernalBot started");
		} catch (IOException e) {
			LOGGER.info("Error starting infernalbot");
			LOGGER.debug(e.getMessage());
		}
	}
	
	public void stop(){
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}
}
