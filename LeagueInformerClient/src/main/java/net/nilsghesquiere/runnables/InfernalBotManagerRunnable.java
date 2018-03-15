package net.nilsghesquiere.runnables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfernalBotManagerRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalBotManagerRunnable.class);
	private final InfernalBotManagerClient client;
	private volatile boolean stop = false;
	public static Map<Thread, ClientDataManagerRunnable> dataThreadMap = new HashMap<>();
	
	public InfernalBotManagerRunnable(InfernalBotManagerClient client) {
		super();
		this.client = client;
	}

	@Override
	public void run() {
		runInfernalbot();
		LOGGER.info("Starting InfernalBot Crash Checker in 5 minutes");
		try {
			TimeUnit.MINUTES.sleep(5);
		} catch (InterruptedException e2) {
			LOGGER.error("Failure during sleep");
			LOGGER.debug(e2.getMessage());
		}
		LOGGER.info("Starting InfernalBot CrashChecker");
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
				if(!pidInfo.contains(client.getClientSettings().getInfernalProgramName())){
					LOGGER.warn("InfernalBot process not found, restarting client");
					for(Entry<Thread,ClientDataManagerRunnable> entry : dataThreadMap.entrySet()){
						entry.getValue().stop();
						try {
							entry.getKey().join();
						} catch (InterruptedException e) {
							LOGGER.error("Failure closing thread");
							LOGGER.debug(e.getMessage());
						}
					}
					if(client.checkConnection() && client.exchangeAccounts()){
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
		LOGGER.info("Successfully closed InfernalBot CrashChecker");
	}

	private void runInfernalbot(){
		try {
			@SuppressWarnings("unused")
			Process process = new ProcessBuilder(client.getClientSettings().getInfernalMap() + client.getClientSettings().getInfernalProgramName()).start();
			LOGGER.info("InfernalBot started");
			ClientDataManagerRunnable dataRunnable = new ClientDataManagerRunnable(client);
			Thread dataThread = new Thread(dataRunnable);
			Main.threadMap.put(dataThread, dataRunnable);
			dataThreadMap.put(dataThread, dataRunnable); // this is to close thread upon crash and reopen new oness
			dataThread.setDaemon(false); 
			dataThread.start();
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
