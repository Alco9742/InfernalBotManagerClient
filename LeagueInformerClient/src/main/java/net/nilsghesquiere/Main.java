package net.nilsghesquiere;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.nilsghesquiere.entities.InfernalBotManagerClientSettings;
import net.nilsghesquiere.runnables.CheckInfernalRunnable;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger("Main");
	private static final String INI_NAME = "settings.ini";
	
	public static void main(String[] args){
		LOGGER.info("Starting InfernalBotManager Client");
		InfernalBotManagerClient client= buildClient();
		if (client != null && client.checkConnection() && client.accountExchange()){
			//start infernalbot checker in a thread
			CheckInfernalRunnable checkInfernalRunnable = new CheckInfernalRunnable(client);
			Thread checkInfernalThread = new Thread(checkInfernalRunnable);
			checkInfernalThread.setDaemon(true); //stop when main thread stops
			checkInfernalRunnable.run();
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
