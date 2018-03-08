package net.nilsghesquiere.hooks;

import java.util.Map.Entry;

import net.nilsghesquiere.Main;
import net.nilsghesquiere.runnables.InfernalBotManagerRunnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GracefulExitHook extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger("GracefulShutdownHook");
	@Override
	public void run(){
		LOGGER.info("Shutting down all threads");
		boolean fail = false;
		for(Entry<Thread,Runnable> entry: Main.threadMap.entrySet()){
			if (entry.getValue() instanceof InfernalBotManagerRunnable){
				InfernalBotManagerRunnable infernalBotManagerRunnable =(InfernalBotManagerRunnable) entry.getValue();
				LOGGER.info("Gracefully shutting down "+ entry.getKey().getName() +", please don't close the program");
				infernalBotManagerRunnable.stop();
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
		}
	}
}
