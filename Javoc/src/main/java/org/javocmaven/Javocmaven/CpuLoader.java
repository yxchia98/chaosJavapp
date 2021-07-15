package org.javocmaven.Javocmaven;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class CpuLoader extends Loader {
	int duration = 5;
	double utilization = 50;
	ArrayList<BusyThread> threadArray = new ArrayList<BusyThread>();

	public CpuLoader(int duration, double utilization) {
		this.duration = duration;
		this.utilization = utilization;
	}

	public CpuLoader(String arguments[], String durationType) {
		if (durationType.equals("seconds")) {
			if (arguments.length >= 2) {
				this.duration = Integer.parseInt(arguments[0]);
				this.utilization = Double.parseDouble(arguments[1]);
			} else if (arguments.length == 1) {
				this.duration = Integer.parseInt(arguments[0]);
			} else {
			}
		}
		else {
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
