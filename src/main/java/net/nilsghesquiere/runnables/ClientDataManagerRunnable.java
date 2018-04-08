package net.nilsghesquiere.runnables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.Main;
import net.nilsghesquiere.util.ProgramConstants;
import net.nilsghesquiere.util.ProgramUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDataManagerRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataManagerRunnable.class);
	private final InfernalBotManagerClient client;
	private volatile boolean stop = false;
	private volatile boolean rebootFromClientDataManagerClient = false;
	
	public ClientDataManagerRunnable(InfernalBotManagerClient client) {
		super();
		this.client = client;
	}

	@Override
	public void run() {
		LOGGER.info("Starting ClientData Updater in 2 minutes and 30 seconds");
		try {
			TimeUnit.MINUTES.sleep(2);
			TimeUnit.SECONDS.sleep(30);
		} catch (InterruptedException e2) {
			LOGGER.debug(e2.getMessage());
		}
		if (!stop) {
			LOGGER.info("Starting InfernalBot ClientData Updater");
		}
		while (!stop){
			client.getClientDataService().sendData("ClientData Update");
			//Only do the checks if the infernalbot process is running (infernal now empties queuers on exit).
			Boolean proccessIsRunning = ProgramUtil.isProcessRunning(ProgramUtil.getInfernalProcessname(client.getClientSettings().getInfernalMap())) || ProgramUtil.isProcessRunning(ProgramConstants.LEGACY_LAUNCHER_NAME);
			if(proccessIsRunning && !client.getClientDataService().hasActiveQueuer()){
				if(client.getClientSettings().getRebootFromManager()){
					LOGGER.info("No active queuers found");
					rebootFromClientDataManagerClient = true;
					Main.exitWaitRunnable.exit();
				} else {
					LOGGER.info("No active queuers found, closing InfernalBot process");
					try {
						String processName = ProgramUtil.getInfernalProcessname(client.getClientSettings().getInfernalMap());
						//Check if process is running and kill it (generated name)
						if(!processName.isEmpty()){
							if(ProgramUtil.isProcessRunning(processName)){
								ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c", "taskkill /F /IM " + processName);
								builder.redirectErrorStream(true);
								Process p = builder.start();
								BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
								String line;
								while (true) {
									line = r.readLine();
									if (line == null) { break; }
									LOGGER.debug(line);
								}
							}
						} else {
							LOGGER.error("Failure finding current InfernalBot process name");
						}
						//Check if legacy process is running and kill it 
						if(ProgramUtil.isProcessRunning(ProgramConstants.LEGACY_LAUNCHER_NAME)){
							ProcessBuilder builder2 = new ProcessBuilder( "cmd.exe", "/c", "taskkill /F /IM " + ProgramConstants.LEGACY_LAUNCHER_NAME);
							builder2.redirectErrorStream(true);
							Process p2 = builder2.start();
							BufferedReader r2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
							String line2;
							while (true) {
								line2 = r2.readLine();
								if (line2 == null) { break; }
								LOGGER.debug(line2);
							}
						}
					} catch (IOException e){
						LOGGER.error("Failure trying to kill InfernalBot Process");
						LOGGER.debug(e.getMessage());
					}
				}
			}
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e1) {
				LOGGER.debug(e1.getMessage());
			}
		}
		client.getClientDataService().sendData("ClientDataUpdater Close");
		LOGGER.info("Successfully closed ClientData Updater thread");
	}

	public void stop(){
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}
	
	public boolean isRebootFromManager() {
		return rebootFromClientDataManagerClient;
	}
}
