package org.javocmaven.Javocmaven;

import java.util.ArrayList;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class MemoryLeaker extends Loader {
	private int duration = 5;
	private double utilization = 50;

	public MemoryLeaker(int duration, double utilization) {
		this.duration = duration;
		this.utilization = utilization;
	}

	public MemoryLeaker(String arguments[], int multiplier) {
		if (arguments.length >= 2) {
			this.duration = Integer.parseInt(arguments[0]) * multiplier;
			this.utilization = Double.parseDouble(arguments[1]);
		} else if (arguments.length == 1) {
			this.duration = Integer.parseInt(arguments[0]) * multiplier;
		} else {
		}
		MainMenu.loadType = "Memory";
		MainMenu.loadUtilization = this.utilization;
		MainMenu.loadDuration = this.duration;

	}

	public void load() {
		System.out.println("Loading Memory for " + this.duration + "s at " + this.utilization + "%. (" + Logger.getCurrentDateTime() + ")");
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		double totalmem = hal.getMemory().getTotal();	// get total memory of the system, using oshi library

		double targetMemory = this.utilization / 100 * totalmem;	// calculate target memory

		ArrayList<char[]> hog = new ArrayList<char[]>();	// instantiate array to be loaded for memory load
		
		while ((totalmem - hal.getMemory().getAvailable()) < targetMemory) {
			hog.add(new char[52428800]);	// adds in a new char and consume 100MB incrementally till target memory is hit
		}
		try {
			Thread.sleep(this.duration * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error putting thread to sleep");
		}
		hog.clear();

	}
}