package net.nilsghesquiere.runnables;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.Main;
import net.nilsghesquiere.util.ProgramUtil;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
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
		LOGGER.info("Starting InfernalBot Crash Checker in 2 minutes");
		try {
			TimeUnit.MINUTES.sleep(2);
		} catch (InterruptedException e2) {
			LOGGER.debug(e2.getMessage());
		}
		if (!stop) {
			LOGGER.info("Starting InfernalBot CrashChecker");
		}
		while (!stop){
			//get the process name from the infernal settings.configs file
			//TODO add checks here for available location etc
			//TODO fix try/catch
			try {
				Wini ini = new Wini(new File(client.getClientSettings().getInfernalMap() + "/configs/settings.ini" ));
				String newProcessName = ini.get("Programs", "Launcher", String.class);
				LOGGER.info("Process name set to " + newProcessName);
				client.getClientSettings().setInfernalProcessName(newProcessName);
			} catch (InvalidFileFormatException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			if(ProgramUtil.isProcessRunning(client.getClientSettings().getInfernalProcessName())){
				LOGGER.warn("InfernalBot process not found, restarting client");
				for(Entry<Thread,ClientDataManagerRunnable> entry : dataThreadMap.entrySet()){
					entry.getKey().interrupt();
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
				LOGGER.debug(e1.getMessage());
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
			dataThreadMap.put(dataThread, dataRunnable); // this is to close thread upon crash and reopen new ones
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
