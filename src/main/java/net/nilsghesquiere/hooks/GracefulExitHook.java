package net.nilsghesquiere.hooks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.nilsghesquiere.Main;
import net.nilsghesquiere.runnables.ClientDataManagerRunnable;
import net.nilsghesquiere.runnables.InfernalBotManagerRunnable;
import net.nilsghesquiere.runnables.ThreadCheckerRunnable;

public class GracefulExitHook extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger("GracefulShutdownHook");
	private boolean rebootWindows = false;
	@Override
	public void run(){
		LOGGER.info("Shutting down all threads");
		boolean fail = false;
		for(Entry<Thread,Runnable> entry: Main.threadMap.entrySet()){
			if (entry.getValue() instanceof InfernalBotManagerRunnable){
				InfernalBotManagerRunnable infernalBotManagerRunnable =(InfernalBotManagerRunnable) entry.getValue();
				LOGGER.info("Gracefully shutting down InfernalBot CrashChecker, please don't close the program");
				entry.getKey().interrupt();
				infernalBotManagerRunnable.stop();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.error("Failure closing thread");
					LOGGER.debug(e.getMessage());
				}
			}
			if (entry.getValue() instanceof ClientDataManagerRunnable){
				ClientDataManagerRunnable clientDataManagerRunnable =(ClientDataManagerRunnable) entry.getValue();
				LOGGER.info("Gracefully shutting down ClientData Updater, please don't close the program");
				if(clientDataManagerRunnable.isRebootFromManager()){
					this.rebootWindows = true;
				}
				entry.getKey().interrupt();
				clientDataManagerRunnable.stop();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.error("Failure closing threads");
					LOGGER.debug(e.getMessage());
				}
			}
			
			if (entry.getValue() instanceof ThreadCheckerRunnable){
				ThreadCheckerRunnable threadCheckerRunnable =(ThreadCheckerRunnable) entry.getValue();
				LOGGER.info("Gracefully shutting down ThreadChecker, please don't close the program");
				entry.getKey().interrupt();
				threadCheckerRunnable.stop();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.error("Failure closing threads");
					LOGGER.debug(e.getMessage());
				}
			}
		}
		//Stop the runnable without launching hook
		if (Main.exitWaitThread.isAlive()){
			Main.exitWaitThread.interrupt();
			Main.exitWaitRunnable.dontLaunchHook();
			Main.exitWaitRunnable.exit();
			try {
				Main.exitWaitThread.join();
			} catch (InterruptedException e) {
				fail = true;
				LOGGER.error("Failure closing threads");
				LOGGER.debug(e.getMessage());
			}
		}
		if(!fail){
			LOGGER.info("Closed all threads, ending program");
			if (this.rebootWindows){
				//Reboot windows
				LOGGER.info("Rebooting windows");
				try {
					//remove old reboot schedule
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
					//initiate reboot
					ProcessBuilder builder2 = new ProcessBuilder( "cmd.exe", "/c", "shutdown -r -t 20");
					builder2.redirectErrorStream(true);
					Process p2 = builder2.start();
					BufferedReader r2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
					String line2;
					while (true) {
						line2 = r2.readLine();
						if (line2 == null) { break; }
						LOGGER.debug(line2);
					}
				} catch (IOException e) {
					LOGGER.error("Failure rebooting Windows");
					LOGGER.debug(e.getMessage());
				}
			}
		}
		LOGGER.info("Closing InfernalBotManager Client in 5 seconds");
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			LOGGER.error("Failure during sleep");
			LOGGER.debug(e.getMessage());
		}
		System.exit(0);
	}
}
