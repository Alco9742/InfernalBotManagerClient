package net.nilsghesquiere.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;

import net.nilsghesquiere.entities.ClientSettings;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


public class ProgramUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgramUtil.class);
	
	public static String getCapitalizedString(boolean bool){
		String boolString = String.valueOf(bool);
		return boolString.substring(0, 1).toUpperCase() + boolString.substring(1);
	}
	
	public static boolean killProcessIfRunning(String processName){
		try {
			if(ProgramUtil.isProcessRunning(processName)){
				ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", "taskkill", "/F", "/IM", '"' + processName + '"');
				builder.redirectErrorStream(true);
				Process p = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while (true) {
					line = r.readLine();
					if (line == null) { break; }
					LOGGER.debug(line);
				}
			}
		} catch (IOException e){
			LOGGER.debug(e.getMessage());
			return false;
		}
		return true;
	}
	
	public static boolean isProcessRunning(String processName){
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
			if(pidInfo.contains(processName)){
				return true;
			}
		} catch (IOException e){
			LOGGER.error("Failure checking task list");
			LOGGER.debug(e.getMessage());
			return false;
		}
		return false;
	}
	
	public static boolean unscheduleReboot(){
		try {
			ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", "shutdown -a");
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while (true) {
				line = r.readLine();
				if (line == null) { break; }
				LOGGER.debug(line);
			}
		} catch (IOException e){
			LOGGER.error("Failure unscheduling reboot");
			LOGGER.debug(e.getMessage());
			return false;
		}
		return true;
	}
	
	public static boolean scheduleReboot(int timeInSeconds){
		try {
			ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", "shutdown -r -t " + timeInSeconds);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while (true) {
				line = r.readLine();
				if (line == null) { break; }
				LOGGER.debug(line);
			}
		} catch (IOException e){
			LOGGER.error("Failure scheduling reboot");
			LOGGER.debug(e.getMessage());
			return false;
		}
		return true;
	}
	
	public static String getInfernalProcessname(Path path){
		//TODO check for location
		String newProcessName = "";
		try {
			Wini ini = new Wini(new File(path + "\\configs\\settings.ini" ));
			newProcessName = ini.get("Programs", "Launcher", String.class);
		} catch (InvalidFileFormatException e2) {
			LOGGER.debug("Failure reading the infernal settings.ini");
			LOGGER.debug(e2.getMessage());
		} catch (IOException e2) {
			LOGGER.debug("Failure reading the infernal settings.ini");
			LOGGER.debug(e2.getMessage());
		}
		return newProcessName;
	}
	
	
	public static boolean downloadFileFromUrl(ClientSettings clientSettings, String filename) {
		if(createDownloadsDir()){
			String managerMap = System.getProperty("user.dir");
			String filePath = managerMap + "\\downloads\\" + filename;
			// Sample Url Location
			String url = "";
			if(clientSettings.getPort().equals("")){
				url = clientSettings.getWebServer() + "/downloads/" + filename; 
			} else {
				url = clientSettings.getWebServer() + ":" + clientSettings.getPort() + "/downloads/" + filename; 
			}
			URL urlObj = null;
			ReadableByteChannel rbcObj = null;
			FileOutputStream fOutStream  = null;
		
			// Checking If The File Exists At The Specified Location Or Not
			Path filePathObj = Paths.get(filePath);
			boolean fileExists = Files.exists(filePathObj);
			if(!fileExists) {
				File file = new File(filePath);
				try {
					file.createNewFile();
				} catch (IOException e) {
					LOGGER.error("Failure creating file");
					LOGGER.debug(e.getMessage());
				}
			}
			
			try {
				urlObj = new URL(url);
				rbcObj = Channels.newChannel(urlObj.openStream());
				fOutStream = new FileOutputStream(filePath);
			
				fOutStream.getChannel().transferFrom(rbcObj, 0, Long.MAX_VALUE);
				LOGGER.info("Update download complete");
			} catch (IOException e) {
				LOGGER.error("Problem occured while downloading " + filename);
				LOGGER.debug(e.getMessage());
				return false;
			} finally {
				try {
					if(fOutStream != null){
						fOutStream.close();
					}
					if(rbcObj != null) {
						rbcObj.close();
					}
				} catch (IOException e) {
					LOGGER.error("Problem occured while closing the object");
					LOGGER.debug(e.getMessage());
					return false;
				}				
			}
		} else {
			LOGGER.error("Failure locating backup folder");
			return false;
		}
		return true;
	}
	
	private static boolean createDownloadsDir(){
		Path backupDir = Paths.get(System.getProperty("user.dir") + "\\downloads\\");
		if(!Files.exists(backupDir)){
			try {
				Files.createDirectories(backupDir);
			} catch (IOException e1) {
				//Path exists, do nothing
			}
		}
		return Files.exists(backupDir);
	}


	public static HttpHeaders buildHttpHeaders(String username, String password){
		HttpHeaders headers = new HttpHeaders();
		//accept
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		//contenttype
		headers.setContentType(MediaType.APPLICATION_JSON);
		//authentication
		String plainCreds  = username + ":" + password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
		String authHeader = "Basic " + new String(base64CredsBytes);
		headers.add("Authorization", authHeader);
		return headers;
	}
	
	public static HttpHeaders buildHttpHeaders(){
		HttpHeaders headers = new HttpHeaders();
		//accept
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		//contenttype
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}	
}
