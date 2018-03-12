package net.nilsghesquiere;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.hooks.GracefulExitHook;
import net.nilsghesquiere.runnables.InfernalBotManagerRunnable;
import net.nilsghesquiere.util.ProgramConstants;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static InfernalBotManagerClient client;
	public static Map<Thread, Runnable> threadMap = new HashMap<>();
	
	public static void main(String[] args){
		System.out.println("---    InfernalBotManager (BETA) by Alco    ---");
		System.out.println("---PRESS CTRL + C TO SAFELY CLOSE THE CLIENT---");
		LOGGER.info("Starting InfernalBotManager Client");
		Runtime.getRuntime().addShutdownHook(new GracefulExitHook());
		client = buildClient();
		//program();
		test();
	}
	
	private static void test(){
		client.queuertest();
	}
	
	private static void program(){
		if (client != null){
			boolean upToDate = true;
			boolean connected = false;
			boolean killSwitchOff = true;
			while(!connected){
				connected = client.checkConnection();
				if (connected){
					if(client.checkKillSwitch()){
						killSwitchOff = false;
					} else {
						if(!client.checkVersion()){
							upToDate = false;
						}
					}
				}
				if(!connected){
					LOGGER.info("Retrying in 1 minute..");
					try {
						TimeUnit.MINUTES.sleep(1);
					} catch (InterruptedException e2) {
						LOGGER.error("Failure during sleep");
						LOGGER.debug(e2.getMessage());
					}
				}
			}
			if (killSwitchOff){
				if (upToDate){
					client.scheduleReboot();
					//check for update
					//initial checks
					//Attempt to get accounts, retry if fail
					boolean initDone = client.checkConnection() &&  client.backUpInfernalDatabase() && client.setInfernalSettings() && client.exchangeAccounts();
					while (!initDone){
						try {
							LOGGER.info("Retrying in 1 minute...");
							TimeUnit.MINUTES.sleep(1);
							initDone = (client.checkConnection() && client.backUpInfernalDatabase() && client.setInfernalSettings() && client.exchangeAccounts());
						} catch (InterruptedException e) {
							LOGGER.error("Failure during sleep");
							LOGGER.debug(e.getMessage());
						}
					}
					//start infernalbot checker in a thread
					InfernalBotManagerRunnable infernalRunnable = new InfernalBotManagerRunnable(client);
					Thread infernalThread = new Thread(infernalRunnable);
					threadMap.put(infernalThread, infernalRunnable);
					infernalThread.setDaemon(false); 
					infernalThread.start();
				} else {
					client.updateClient();
					LOGGER.info("Closing InfernalBotManager Client");
					System.exit(0);
				}
			} else {
				LOGGER.info("Closing InfernalBotManager Client");
				System.exit(0);
			}
		} else {
			LOGGER.info("Closing InfernalBotManager Client");
			System.exit(0);
		}
	}
	private static InfernalBotManagerClient buildClient(){
		String iniFile = System.getProperty("user.dir") + "\\" + ProgramConstants.INI_NAME;
		Path iniFilePath = Paths.get(iniFile);
		InfernalBotManagerClient client = null;
		if(Files.exists(iniFilePath)){
			try {
				Wini ini = new Wini(new File(iniFile));
				ClientSettings settings = ClientSettings.buildFromIni(ini);
				if (settings != null){
					client = new InfernalBotManagerClient(settings);
				}
			} catch (InvalidFileFormatException e) {
				LOGGER.error("Error reading settings.ini file");
				LOGGER.debug(e.getMessage());
			} catch (IOException e) {
				LOGGER.error("Error reading settings.ini file");
				LOGGER.debug(e.getMessage());;
			}
		} else {
			LOGGER.error("settings.ini file not found at path: " + iniFilePath);
		}
		return client;
	}
}
