package org.javocmaven.Javocmaven;

import java.util.ArrayList;
import java.lang.management.*;

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
		com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();
//		System.out.println("Total Memory(MB): " + os.getTotalMemorySize() / Math.pow(2, 20));
//		System.out.println("Free Memory(MB): " + os.getFreeMemorySize() / Math.pow(2, 20));
//		System.out.println("Used Memory(MB): " + (os.getTotalMemorySize() - os.getFreeMemorySize()) / Math.pow(2, 20));
//		System.out.println("JVM Allocated Memory(MB): " + (Runtime.getRuntime().maxMemory()) / Math.pow(2, 20));

		double targetMemory = this.utilization / 100 * os.getTotalMemorySize();
//		System.out.println("Target Memory usage: " + targetMemory / Math.pow(2, 20));
		ArrayList<char[]> hog = new ArrayList<char[]>();
		Runtime.getRuntime().gc();
		while ((os.getTotalMemorySize() - os.getFreeMemorySize()) < targetMemory) {
			hog.add(new char[524288]);
		}
		System.out.println();
		System.out.println("Total Memory: " + os.getTotalMemorySize() / Math.pow(2, 20));
		System.out.println("Free Memory: " + os.getFreeMemorySize() / Math.pow(2, 20));
		System.out.println("Used Memory: " + (os.getTotalMemorySize() - os.getFreeMemorySize()) / Math.pow(2, 20));
		try {
			Thread.sleep(this.duration * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error putting thread to sleep");
		}
		System.out.println("Successfully executed.");

	}

	public static void testMem(int duration, double targetUtilization) {
		com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();
		System.out.println("Total Memory: " + os.getTotalMemorySize() / Math.pow(2, 20));
		System.out.println("Free Memory: " + os.getFreeMemorySize() / Math.pow(2, 20));
		System.out.println("Used Memory: " + (os.getTotalMemorySize() - os.getFreeMemorySize()) / Math.pow(2, 20));
		System.out.println("JVM Allocated Memory: " + (Runtime.getRuntime().maxMemory()) / Math.pow(2, 20));

		double targetMemory = targetUtilization / 100 * os.getTotalMemorySize();
		System.out.println("Target Memory usage: " + targetMemory / Math.pow(2, 20));
		ArrayList<char[]> hog = new ArrayList<char[]>();
		Runtime.getRuntime().gc();
		while ((os.getTotalMemorySize() - os.getFreeMemorySize()) < targetMemory) {
			hog.add(new char[524288]);
		}
		System.out.println();
		System.out.println("Total Memory: " + os.getTotalMemorySize() / Math.pow(2, 20));
		System.out.println("Free Memory: " + os.getFreeMemorySize() / Math.pow(2, 20));
		System.out.println("Used Memory: " + (os.getTotalMemorySize() - os.getFreeMemorySize()) / Math.pow(2, 20));
		try {
			Thread.sleep(duration * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error putting thread to sleep");
		}
		System.out.println("Successfully executed.");

	}
}