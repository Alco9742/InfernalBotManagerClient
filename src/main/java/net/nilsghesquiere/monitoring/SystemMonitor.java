package net.nilsghesquiere.monitoring;

import java.text.DecimalFormat;

import net.nilsghesquiere.util.ProgramConstants;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

public class SystemMonitor {
	SystemInfo systemInfo;
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
		return systemInfo.getHardware().getNetworkIFs()[0].getMacaddr().toUpperCase();
	}
}
