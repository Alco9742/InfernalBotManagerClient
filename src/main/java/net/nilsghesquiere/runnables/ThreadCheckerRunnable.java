package net.nilsghesquiere.runnables;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadCheckerRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadCheckerRunnable.class);
	private volatile boolean stop = false;

	public ThreadCheckerRunnable() {
		super();
	}

	@Override
	public void run() {
		while(!stop){
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e2) {
				LOGGER.error("Failure during sleep");
				LOGGER.debug(e2.getMessage());
			}
			Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
			LOGGER.debug("Currently running threads:");
			for (Thread thread : threadSet){
				LOGGER.debug(thread.getName() + " - " + thread.getClass() + " - " + thread.getState());
			}
		}
	}

	public void stop(){
		stop = true;
	}

	public boolean getStop(){
		return stop;
	}
}
