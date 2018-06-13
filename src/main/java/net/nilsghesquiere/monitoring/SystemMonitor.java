package net.nilsghesquiere.monitoring;

import java.text.DecimalFormat;

import net.nilsghesquiere.util.ProgramConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.NetworkIF;

public class SystemMonitor {
	SystemInfo systemInfo;
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemMonitor.class);
	public SystemMonitor(){
		systemInfo = new SystemInfo();
	}
	
	public String getRamUsage(){
		GlobalMemory memory = systemInfo.getHardware().getMemory();
		long totalRam = memory.getTotal();
		long freeRam = memory.getAvailable();
		long usedRam = totalRam - freeRam;
		long totalRamMB = totalRam / 1024 / 1024;
		long usedRamMB = usedRam / 1024 /1024;
		return usedRamMB + " MB / " + totalRamMB + " MB";
	}
	
	public String getCpuUsage(){
		DecimalFormat format = new DecimalFormat("##0.00");
		if (ProgramConstants.enableOshiCPUCheck){
			CentralProcessor processor = systemInfo.getHardware().getProcessor();
			double cpuLoad = processor.getSystemCpuLoad() * 100;
			return format.format(cpuLoad) + "%";
		} else {
			return "-";
		}
	}
	
	public String getHWID(){
		//reworked to bypass problems with macAddr 00:00:00:00:00:00:00:e0
		NetworkIF[] networkIFs = systemInfo.getHardware().getNetworkIFs();
		for(NetworkIF nIF : networkIFs){
			String macAddr = nIF.getMacaddr();
			if(macAddr != "00:00:00:00:00:00:00:e0"){
				return macAddr;
			}
		}
		return "invalidMAC";
	}
}
