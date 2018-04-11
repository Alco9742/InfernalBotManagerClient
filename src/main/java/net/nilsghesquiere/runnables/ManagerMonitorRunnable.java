package net.nilsghesquiere.runnables;

import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.enums.ClientStatus;
import net.nilsghesquiere.monitoring.SystemMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagerMonitorRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ManagerMonitorRunnable.class);
	private InfernalBotManagerClient client;
	public final SystemMonitor monitor;
	private volatile boolean stop = false;
	private volatile ClientStatus status;
	
	public ManagerMonitorRunnable(SystemMonitor monitor) {
		super();
		this.monitor = monitor;
	}
	
	@Override
	public void run() {
		if (!stop) {
			LOGGER.info("Starting Manager Client Monitor");
		}
		while (!stop){
			uploadClientData();
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
			}
		}
		uploadClientData();
		LOGGER.info("Successfully closed ClientData Updater thread");
	}

	private void uploadClientData(){
		switch (status){
			case INIT:
				break;
			case CONNECTED:
				client.getClientDataService().sendData("Client Connected", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case UPDATE:
				client.getClientDataService().sendData("Client Updating", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case INFERNAL_RUNNING:
				client.getClientDataService().sendData("Infernalbot Running", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case CLOSE:
				client.getClientDataService().sendData("Client Closing", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case CLOSE_REBOOT:
				client.getClientDataService().sendData("Windows Rebooting", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case ERROR:
				client.getClientDataService().sendData("Client Error", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			default:
				client.getClientDataService().sendData("Client Error", monitor.getRamUsage(), monitor.getCpuUsage());
		}
		client.sendData("InfernalBotManager Running", monitor.getRamUsage(), monitor.getCpuUsage());
	}
	
	public void stop(){
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}
	
	public void setClient(InfernalBotManagerClient client){
		this.client = client;
	}
	
	public void setClientStatus(ClientStatus status){
		this.status = status;
	}
}
