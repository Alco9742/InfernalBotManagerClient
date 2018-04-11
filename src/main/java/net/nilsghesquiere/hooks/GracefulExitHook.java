package net.nilsghesquiere.hooks;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.Main;
import net.nilsghesquiere.enums.ClientStatus;
import net.nilsghesquiere.runnables.InfernalBotCheckerRunnable;
import net.nilsghesquiere.runnables.ManagerMonitorRunnable;
import net.nilsghesquiere.runnables.ThreadCheckerRunnable;
import net.nilsghesquiere.util.ProgramUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GracefulExitHook extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(GracefulExitHook.class);
	private boolean rebootWindows = false;
	@Override
	public void run(){
		LOGGER.info("Shutting down all threads");
		boolean fail = false;
		for(Entry<Thread,Runnable> entry: Main.threadMap.entrySet()){
			if (entry.getValue() instanceof InfernalBotCheckerRunnable){
				InfernalBotCheckerRunnable infernalBotManagerRunnable =(InfernalBotCheckerRunnable) entry.getValue();
				LOGGER.info("Gracefully shutting down InfernalBotChecker, please don't close the program");
				if(infernalBotManagerRunnable.isRebootFromManager()){
					this.rebootWindows = true;
					Main.managerMonitorRunnable.setClientStatus(ClientStatus.CLOSE_REBOOT);
				} else {
					Main.managerMonitorRunnable.setClientStatus(ClientStatus.CLOSE);
				}
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

			if (entry.getValue() instanceof ManagerMonitorRunnable){
				ManagerMonitorRunnable managerMonitorRunnable =(ManagerMonitorRunnable) entry.getValue();
				LOGGER.info("Gracefully shutting down ClientData Updater, please don't close the program");
				entry.getKey().interrupt();
				managerMonitorRunnable.stop();
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
				ProgramUtil.unscheduleReboot();
				ProgramUtil.scheduleReboot(20);
			}
		}
		LOGGER.info("Closing InfernalBotManager Client in 5 seconds");
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			LOGGER.debug(e.getMessage());
		}
		System.exit(0);
	}
}
