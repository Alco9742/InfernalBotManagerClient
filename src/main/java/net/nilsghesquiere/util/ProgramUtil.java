package net.nilsghesquiere.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.nilsghesquiere.entities.IniSettings;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


public class ProgramUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgramUtil.class);

	public static boolean killAllInfernalProcesses(Path path){
		List<String> processNames = ProgramUtil.getExecutablesInDirectory(path);
		processNames.addAll(ProgramConstants.programsToClose);
		processNames.removeAll(ProgramConstants.programsToKeepOpen);
		List<String> processesToKill = getPidsByList(processNames);
		return ProgramUtil.killProcessByPidList(processesToKill);
	}
	
	public static boolean killLegacyInfernalLauncher(){
		return killProcessIfRunning(ProgramConstants.LEGACY_LAUNCHER_NAME);
	}
	
	public static boolean killInfernalLauncher(Path infernalPath){
		String infernalProcessName = getInfernalProcessname(infernalPath);
		return killProcessIfRunning(infernalProcessName);
	}
	
	public static boolean killProcessByPidList(List<String> pids){
		boolean success = true;
		for(String pid : pids){
			success = killProcessByPid(pid);
		}
		return success;
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
			LOGGER.debug("Handled exception: " + e.getClass().getSimpleName());
			LOGGER.debug("Failed to kill proccess '" + processName + "'");
			return false;
		}
		return true;
	}
	
	public static boolean killProcessByPid(String pid){
		try {
			ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", "taskkill", "/PID", pid, "/F");
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
			LOGGER.debug("Failure killing process with PID " + pid);
			LOGGER.debug("Exception executing command:",e);
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
	
	public static String getInfernalProcessname(Path infernalPath){
		Path infernalIni = infernalPath.resolve("configs").resolve("settings.ini");
		String newProcessName = "";
		try {
			Wini ini = new Wini(infernalIni.toFile());
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

	public static String buildBearerTokenRequestUrl(Path infernalPath){
		Path infernalIni = infernalPath.resolve("configs").resolve("settings.ini");
		String email = "";
		String password ="";
		try {
			Wini ini = new Wini(infernalIni.toFile());
			email = ini.get("Account", "Email", String.class);
			password = ini.get("Account", "Password", String.class);
		} catch (InvalidFileFormatException e2) {
			LOGGER.debug("Failure reading the infernal settings.ini");
			LOGGER.debug(e2.getMessage());
		} catch (IOException e2) {
			LOGGER.debug("Failure reading the infernal settings.ini");
			LOGGER.debug(e2.getMessage());
		}
	
		String bearerUrl = ProgramConstants.INFERNAL_REST_BASE + "/auth/v1/token?UserEmail=" + email + "&Password=" + password;
		return bearerUrl;
	}
	
	public static void emptyInfernalConfigsFile(Path infernalPath){
		Path infernalIni = infernalPath.resolve("configs").resolve("settings.ini");
		String newProcessName = "";
		try {
			Wini ini = new Wini(infernalIni.toFile());
			ini.put("Programs", "Launcher", "");
			ini.put("Programs", "Queuer", "");
			ini.store();
		} catch (InvalidFileFormatException e2) {
			LOGGER.debug("Failure reading the infernal settings.ini");
			LOGGER.debug(e2.getMessage());
		} catch (IOException e2) {
			LOGGER.debug("Failure reading the infernal settings.ini");
			LOGGER.debug(e2.getMessage());
		}
	}
	
	public static List<String> getPidsByList(List<String> names){
		List<String> pids = new ArrayList<>();
		Map<String,String> processMap = getProcessMap();
		for(String name : names){
			for(Entry<String, String> entry : processMap.entrySet()){
				if (entry.getValue().toLowerCase().trim().equals(name.toLowerCase().trim())){
					pids.add(entry.getKey());
				}
			}
		}
		return pids;
	}
	
	public static Map<String,String> getProcessMap(){
		Map<String,String> output = new HashMap<>();
		List<String> processList = getProcessList();
		if (!processList.isEmpty()){
			for (int i = 0; i < processList.size(); i= i+2){
				String pid = processList.get(i+1).split("=")[1];
				String caption = processList.get(i).split("=")[1];
				output.put(pid,caption);
			}
		}
		return output;
	}
	public static List<String> getProcessList(){
		String line ="";
		List<String> output = new ArrayList<>();
		try {
			ProcessBuilder builder = new ProcessBuilder( "wmic.exe", "PROCESS", "get" , "ProcessId,Caption", "/format:LIST");
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				if(!line.isEmpty()){
					output.add(line);
				} 
			}
			input.close();
		} catch (IOException e){
			LOGGER.error("Failure retrieving active processes");
			LOGGER.debug("Exception executing command:",e);
		}
		return output;
	}

	public static List<String> getExecutablesInDirectory(Path directory) {
		List<String> fileNames = new ArrayList<>();
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.exe");
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
			for (Path path : directoryStream) {
				if (matcher.matches(path)) {
					fileNames.add(path.getFileName().toString());
				}
			}
		} catch (IOException ex) {}
		return fileNames;
	}
	
	public static String getCapitalizedString(boolean bool){
		String boolString = String.valueOf(bool);
		return boolString.substring(0, 1).toUpperCase() + boolString.substring(1);
	}
	
	public static boolean downloadFileFromUrl(IniSettings iniSettings, String filename) {
		if(createDownloadsDir()){
			String managerMap = System.getProperty("user.dir");
			String filePath = managerMap + "\\downloads\\" + filename;
			// Sample Url Location
			String url = "";
			if(iniSettings.getPort().equals("")){
				url = iniSettings.getWebServer() + "/downloads/" + filename; 
			} else {
				url = iniSettings.getWebServer() + ":" + iniSettings.getPort() + "/downloads/" + filename; 
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
	
	public static HttpHeaders buildHttpHeaders(){
		HttpHeaders headers = new HttpHeaders();
		//accept
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		//contenttype
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}	
	
	public static HttpHeaders buildInfernalRestHeaders(String bearerToken){
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", bearerToken);
		return headers;
	}	
}
