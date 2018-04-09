package net.nilsghesquiere.monitor;

import java.text.DecimalFormat;

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
		CentralProcessor processor = systemInfo.getHardware().getProcessor();
		double cpuLoad = processor.getSystemCpuLoadBetweenTicks() * 100;
		return format.format(cpuLoad) + "%";
	}
}
