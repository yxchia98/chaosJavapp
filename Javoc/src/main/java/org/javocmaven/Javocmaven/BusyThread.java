package org.javocmaven.Javocmaven;

import java.time.LocalDateTime;

public class BusyThread extends Thread {
	private LocalDateTime endtime;
	private double utilization;

	public BusyThread(String name, LocalDateTime endtime, double utilization) {
		super(name);
		this.endtime = endtime;
		this.utilization = utilization / 100;
	}

	@Override
	public void run() {
		super.run();
		System.out.println(this.getName());
		try {
			while (LocalDateTime.now().isBefore(endtime)) {
				// Every 100ms, sleep for the percentage of unladen time
				if (System.currentTimeMillis() % 100 == 0) {
					Thread.sleep((long) Math.floor((1 - this.utilization) * 100));
				}
			}
		} catch (InterruptedException e) {
			System.out.println(this.getName() + " cannot be put to sleep.");
			e.printStackTrace();
		}
		System.out.println(this.getName() + " Successfully executed and terminated.");
	}
}
