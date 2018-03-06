package net.nilsghesquiere;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import net.nilsghesquiere.entities.InfernalBotManagerClientSettings;
import net.nilsghesquiere.hooks.GracefulExitHook;
import net.nilsghesquiere.runnables.InfernalBotManagerRunnable;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger("Main");
	private static final String INI_NAME = "settings.ini";
	public static Map<Thread, Runnable> threadMap = new HashMap<>();
	
	public static void main(String[] args){
		program();
		//test();
	}
	
	private static void test(){
		InfernalBotManagerClient client= buildClient();
		client.setInfernalSettings();
	}
	
	private static void program(){
		System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println("|||   InfernalBotManager (BETA) by NilsGhes   |||");
		System.out.println("||| PRESS CTRL + C TO SAFELY CLOSE THE CLIENT |||");
		System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||");
		LOGGER.info("Starting InfernalBotManager Client");
		Runtime.getRuntime().addShutdownHook(new GracefulExitHook());
		InfernalBotManagerClient client= buildClient();
		if (client != null){ 
			//start infernalbot checker in a thread
			InfernalBotManagerRunnable infernalRunnable = new InfernalBotManagerRunnable(client);
			Thread infernalThread = new Thread(infernalRunnable);
			threadMap.put(infernalThread, infernalRunnable);
			infernalThread.setDaemon(false); 
			infernalThread.start();
		} else {
			LOGGER.info("Closing InfernalBotManager Client");
			System.exit(0);
		}
	}
	private static InfernalBotManagerClient buildClient(){
		String iniFile = System.getProperty("user.dir") + "\\" + INI_NAME;
		Path iniFilePath = Paths.get(iniFile);
		InfernalBotManagerClient client = null;
		if(Files.exists(iniFilePath)){
			try {
				Wini ini = new Wini(new File(iniFile));
				InfernalBotManagerClientSettings settings = InfernalBotManagerClientSettings.buildFromIni(ini);
				if (settings != null){
					client = new InfernalBotManagerClient(settings);
				}
			} catch (InvalidFileFormatException e) {
				LOGGER.info("Error reading settings.ini file");
				LOGGER.debug(e.getMessage());
			} catch (IOException e) {
				LOGGER.info("Error reading settings.ini file");
				LOGGER.debug(e.getMessage());;
			}
		} else {
			LOGGER.info("Error: .ini file not found at path: " + iniFilePath);
		}
		return client;
	}
}
