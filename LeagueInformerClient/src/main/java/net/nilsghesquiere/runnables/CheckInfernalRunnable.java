package net.nilsghesquiere.runnables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckInfernalRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger("InfernalBot Process Checker");
	private final String infernalTaskName;
	private final String infernalMap;
	private volatile boolean cancelled;
	
	public CheckInfernalRunnable(String infernalMap, String infernalTaskName) {
		super();
		this.infernalMap = infernalMap;
		this.infernalTaskName = infernalTaskName;
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
				if(!pidInfo.contains(infernalTaskName)){
					LOGGER.info("InfernalBot process not found, restarting InfernalBot");
					runInfernalbot();
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
			Process process = new ProcessBuilder(infernalMap + infernalTaskName).start();
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
