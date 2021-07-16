package org.javocmaven.Javocmaven;

import java.util.ArrayList;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class MemoryLeaker extends Loader {
	int duration = 5;
	double utilization = 50;

	public MemoryLeaker(int duration, double utilization) {
		this.duration = duration;
		this.utilization = utilization;
	}

	public MemoryLeaker(String arguments[], String durationType) {

		if (durationType.equals("seconds")) {
			if (arguments.length >= 2) {
				this.duration = Integer.parseInt(arguments[0]);
				this.utilization = Double.parseDouble(arguments[1]);
			} else if (arguments.length == 1) {
				this.duration = Integer.parseInt(arguments[0]);
			} else {
			}
		} else {
			if (arguments.length >= 2) {
				this.duration = Integer.parseInt(arguments[0]) * 60;
				this.utilization = Double.parseDouble(arguments[1]);
			} else if (arguments.length == 1) {
				this.duration = Integer.parseInt(arguments[0]) * 60;
			} else {
			}
		}

	}

	public void load() {
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		double totalmem = hal.getMemory().getTotal();

		double targetMemory = this.utilization / 100 * totalmem;
		System.out.println("Total Memory: " + totalmem / Math.pow(2, 20) + "\nTarget Memory usage: " + targetMemory / Math.pow(2, 20));
		ArrayList<char[]> hog = new ArrayList<char[]>();
		Runtime.getRuntime().gc();
		while ((totalmem - hal.getMemory().getAvailable()) < targetMemory) {
			System.out.println("Used Memory: " + (totalmem - hal.getMemory().getAvailable()) / Math.pow(2, 20));
			hog.add(new char[52428800]);
		}
		System.out.println();
		System.out.println("Total Memory: " + totalmem / Math.pow(2, 20));
		System.out.println("Free Memory: " + hal.getMemory().getAvailable() / Math.pow(2, 20));
		System.out.println("Used Memory: " + (totalmem - hal.getMemory().getAvailable()) / Math.pow(2, 20));
		try {
			Thread.sleep(this.duration * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error putting thread to sleep");
		}
		System.out.println("Successfully executed.");

	}
}