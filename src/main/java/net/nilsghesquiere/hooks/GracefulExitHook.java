package net.nilsghesquiere.hooks;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.Main;
import net.nilsghesquiere.enums.ClientStatus;
import net.nilsghesquiere.runnables.AccountlistUpdaterRunnable;
import net.nilsghesquiere.runnables.InfernalBotCheckerRunnable;
import net.nilsghesquiere.runnables.ManagerMonitorRunnable;
import net.nilsghesquiere.runnables.ThreadCheckerRunnable;
import net.nilsghesquiere.runnables.UpdateCheckerRunnable;
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
			
			if (entry.getValue() instanceof AccountlistUpdaterRunnable){
				AccountlistUpdaterRunnable accountlistUpdaterRunnable =(AccountlistUpdaterRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down Accountlist Updater thread");
				accountlistUpdaterRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.error("Failure closing Accountlist Updater thread");
					LOGGER.debug(e.getMessage());
				}
			}
			
			if (entry.getValue() instanceof UpdateCheckerRunnable){
				UpdateCheckerRunnable updateCheckerRunnable =(UpdateCheckerRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down Update Checker threadd");
				updateCheckerRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.error("Failure closing Update Checker thread");
					LOGGER.debug(e.getMessage());
				}
			}
			
			if (entry.getValue() instanceof InfernalBotCheckerRunnable){
				InfernalBotCheckerRunnable infernalBotManagerRunnable =(InfernalBotCheckerRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down InfernalBotChecker");
				if(infernalBotManagerRunnable.isRebootFromManager()){
					this.rebootWindows = true;
					if(Main.managerMonitorRunnable.getClientStatus() != ClientStatus.UPDATE){
						Main.managerMonitorRunnable.setClientStatus(ClientStatus.CLOSE_REBOOT);
					}
				} else {
					if(Main.managerMonitorRunnable.getClientStatus() != ClientStatus.UPDATE){
						Main.managerMonitorRunnable.setClientStatus(ClientStatus.CLOSE);
					}
				}
				infernalBotManagerRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.error("Failure closing InfernalBotChecker thread");
					LOGGER.debug(e.getMessage());
				}
			}
			
			if (entry.getValue() instanceof ThreadCheckerRunnable){
				ThreadCheckerRunnable threadCheckerRunnable =(ThreadCheckerRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down Thread Checker");
				threadCheckerRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.error("Failure closing Thread Checker thread");
					LOGGER.debug(e.getMessage());
				}
			}

			if (entry.getValue() instanceof ManagerMonitorRunnable){
				ManagerMonitorRunnable managerMonitorRunnable =(ManagerMonitorRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down Manager Client Monitor");
				managerMonitorRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.error("Failure closing Manager Client Monitor thread");
					LOGGER.debug(e.getMessage());
				}
			}
		}
		//Stop the runnable without launching hook
		if (Main.exitWaitThread.isAlive()){
			Main.exitWaitRunnable.dontLaunchHook();
			Main.exitWaitRunnable.exit();
			Main.exitWaitThread.interrupt();
			try {
				Main.exitWaitThread.join();
			} catch (InterruptedException e) {
				fail = true;
				LOGGER.error("Failure closing Exit Wait thread");
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
		LOGGER.info("Closing InfernalBotManager Client");
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			LOGGER.debug(e.getMessage());
		}
		System.exit(0);
	}
}
