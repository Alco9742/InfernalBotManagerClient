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
	private boolean finalized = false;
	private volatile boolean stop = false;
	private ClientStatus status;
	
	public ManagerMonitorRunnable(SystemMonitor monitor, InfernalBotManagerClient client) {
		super();
		this.monitor = monitor;
		this.client = client;
		this.status = ClientStatus.INIT;
	}
	
	@Override
	public void run() {
		if (!stop) {
			LOGGER.info("Starting Manager Client Monitor");
		}
		while (!stop){
			sendClientData();
			try {
				TimeUnit.SECONDS.sleep(30);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
			}
		}
		if (!finalized){
			finishTasks();
		}
		LOGGER.info("Successfully closed Manager Client Monitor thread");
	}

	private void sendClientData(){
		switch (status){
			case INIT:
				break;
			case CONNECTED:
				client.getClientDataService().sendData("Manager Client Connected", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case UPDATE:
				client.getClientDataService().sendData("Manager Client Updating", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case INFERNAL_STARTUP:
				client.getClientDataService().sendData("Infernalbot Startup", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case INFERNAL_RUNNING:
				client.getClientDataService().sendData("Infernalbot Running", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case CLOSE:
				client.getClientDataService().sendData("Manager Client Closing", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case CLOSE_REBOOT:
				client.getClientDataService().sendData("Windows Rebooting", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			case ERROR:
				client.getClientDataService().sendData("Manager Client Error", monitor.getRamUsage(), monitor.getCpuUsage());
				break;
			default:
				client.getClientDataService().sendData("Manager Client Error", monitor.getRamUsage(), monitor.getCpuUsage());
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
	
	private void finishTasks(){
		sendClientData();
		this.finalized = true;
	}
}
