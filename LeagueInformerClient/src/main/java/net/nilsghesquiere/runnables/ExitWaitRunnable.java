package net.nilsghesquiere.runnables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.hooks.GracefulExitHook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExitWaitRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExitWaitRunnable.class);
	private volatile boolean exit = false;
	
	public ExitWaitRunnable() {
		super();
	}

	@Override
	public void run() {
		while(!exit){
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e2) {
				LOGGER.error("Failure during sleep");
				LOGGER.debug(e2.getMessage());
			}
		}
		Thread exitHook = new Thread(new GracefulExitHook());
		exitHook.start();
	}

	public void exit(){
		exit = true;
	}

}
