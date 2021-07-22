package org.javocmaven.Javocmaven;

import java.util.ArrayList;
import java.util.Timer;

import picocli.CommandLine;

public class MainMenu {

	private static void executeLoad(Loader loadObj) {
		loadObj.load();
	}
	
	public static void main(String[] args) {
		ArrayList<BusyThread> threadArray = new ArrayList<BusyThread>();

		//start timer for interval loggings
		Timer timer = new Timer();
		timer.schedule(new Logger(), 0, 60000);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		CLIFormatter arguments = CommandLine.populateCommand(new CLIFormatter(), args);
		String[] cpuval = arguments.getCpuvalues();
		String[] memval = arguments.getMemvalues();
		String[] diskval = arguments.getDiskvalues();
		String[] netlagval = arguments.getNetlagvalues();
		String[] netdropval = arguments.getNetdropvalues();
		String[] netnoiseval = arguments.getNetnoisevalues();
		String[] netlimitval = arguments.getNetlimitvalues();
		String[] rebootval = arguments.getRebootvalues();
 		int multiplier = 60;
		if(arguments.isSeconds()) {
			multiplier = 1;
		}
		if(!(cpuval == null)) {
			CpuLoader cpuloader = new CpuLoader(cpuval, multiplier);
			executeLoad(cpuloader);
			threadArray = cpuloader.getThreadArray();
		}
		if(!(memval == null)) {
			executeLoad(new MemoryLeaker(memval, multiplier));
		}
		if(!(diskval == null)) {
			executeLoad(new DiskWriter(diskval, multiplier));
		}
		if(!(netlagval == null)) {
			executeLoad(new NetworkLagger(netlagval, "lag", multiplier));
		}
		if(!(netdropval == null)) {
			System.out.println("-netdrop entered, " + (Integer.parseInt(netdropval[0]) * multiplier) + ", " + Integer.parseInt(netdropval[1]));
			executeLoad(new NetworkLagger(netdropval, "drop", multiplier));
		}
		if(!(netnoiseval == null)) {
			executeLoad(new NetworkLagger(netnoiseval, "noise", multiplier));
		}
		if(!(netlimitval == null)) {
			executeLoad(new NetworkLagger(netlimitval, "throttle", multiplier));
		}
		if(!(rebootval == null)) {
			executeLoad(new MachineReboot(rebootval, multiplier));
		}
		
		
		//join all busythreads before termination
		for (int i = 0; i < threadArray.size(); i++) {
			try {
				threadArray.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//stop logging jobs
		try {
			timer.schedule(new Logger(), 1000);
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timer.cancel();
		timer.purge();
		System.out.println("Executed.");
	}
}
