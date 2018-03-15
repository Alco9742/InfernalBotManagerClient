package net.nilsghesquiere.hooks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;

import net.nilsghesquiere.Main;
import net.nilsghesquiere.runnables.ClientDataManagerRunnable;
import net.nilsghesquiere.runnables.InfernalBotManagerRunnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
				clientDataManagerRunnable.stop();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.error("Failure closing threads");
					LOGGER.debug(e.getMessage());
				}
			}
		}
		if(!fail){
			LOGGER.info("Closed all threads, ending program");
			if (this.rebootWindows){
				LOGGER.info("Rebooting windows");
				try {
					ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", "shutdown -r -t 0");
					builder.redirectErrorStream(true);
					Process p = builder.start();
					BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					while (true) {
						line = r.readLine();
						if (line == null) { break; }
						LOGGER.debug(line);
					}
				} catch (IOException e) {
					LOGGER.error("Failure rebooting Windows");
					LOGGER.debug(e.getMessage());
				}
			}
		}
	}
}
