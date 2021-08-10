package org.javocmaven.Javocmaven;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class CpuLoader extends Loader {
	private int duration = 5;
	private double utilization = 50;
	private ArrayList<BusyThread> threadArray = new ArrayList<BusyThread>();

	public CpuLoader(int duration, double utilization) {
		this.duration = duration;
		this.utilization = utilization;
	}

	public CpuLoader(String arguments[], int multiplier) {
		if (arguments.length >= 2) {
			this.duration = Integer.parseInt(arguments[0]) * multiplier;
			this.utilization = Double.parseDouble(arguments[1]);
		} else if (arguments.length == 1) {
			this.duration = Integer.parseInt(arguments[0]) * multiplier;

		} else {

		}
		MainMenu.loadType = "CPU";
		MainMenu.loadUtilization = String.valueOf(this.utilization);
		MainMenu.loadDuration = String.valueOf(this.duration);
	}

	public void load() {
		System.out.println("Loading CPU for " + this.duration + "s at " + this.utilization + "%. ("
				+ Logger.getCurrentDateTime() + ")");
		int numCores = Runtime.getRuntime().availableProcessors();
		LocalDateTime endtime = LocalDateTime.now().plusSeconds(this.duration);
		for (int i = 0; i < numCores; i++) {
			BusyThread thread = new BusyThread("Thread" + Integer.toString(i + 1), endtime, this.utilization);
			threadArray.add(thread);
			thread.start();
		}
	}

	public ArrayList<BusyThread> getThreadArray() {
		return this.threadArray;
	}
}
