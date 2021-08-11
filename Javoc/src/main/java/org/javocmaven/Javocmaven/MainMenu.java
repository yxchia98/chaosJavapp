package org.javocmaven.Javocmaven;

import java.util.ArrayList;
import java.util.Timer;

import picocli.CommandLine;

public class MainMenu {
	
	public static String currentState = "", url, loadType = "None";
	public static int loadDuration = 0;
	public static double loadUtilization = 0;
	public static boolean HTTPExperiment = false;

	private static void executeLoad(Loader loadObj) {
		loadObj.load();
	}

	public static void main(String[] args) {
		ArrayList<BusyThread> threadArray = new ArrayList<BusyThread>();

		// Get positional parameters from command line via CLIFormatter class.
		CLIFormatter arguments = CommandLine.populateCommand(new CLIFormatter(), args);
		String[] cpuval = arguments.getCpuvalues();
		String[] memval = arguments.getMemvalues();
		String[] diskval = arguments.getDiskvalues();
		String[] netlagval = arguments.getNetlagvalues();
		String[] netdropval = arguments.getNetdropvalues();
		String[] netnoiseval = arguments.getNetnoisevalues();
		String[] netlimitval = arguments.getNetlimitvalues();
		String[] rebootval = arguments.getRebootvalues();
		url = arguments.getUrl();
		if(!url.equals("")) {
			HTTPExperiment = true;
		}
		int multiplier = 60;

		// Display inline information for --help and --version commands.
		CommandLine commandLine = new CommandLine(new CLIFormatter());
		commandLine.parseArgs(args);
		if (commandLine.isUsageHelpRequested()) {
			commandLine.usage(System.out);
			return;
		} else if (commandLine.isVersionHelpRequested()) {
			commandLine.printVersionHelp(System.out);
			return;
		}
		
		// Start timer for interval loggings
		Timer timer = new Timer();
		timer.schedule(new Logger(), 0, 60000);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// Set duration multiplier from minutes to seconds if -seconds is parsed.
		if (arguments.isSeconds()) {
			multiplier = 1;
		}
		// Execute cpu load if -cpu option is parsed, along with duration and utilization positional parameters.
		if (!(cpuval == null)) {
			CpuLoader cpuloader = new CpuLoader(cpuval, multiplier);
			executeLoad(cpuloader);
			threadArray = cpuloader.getThreadArray();
		}
		// Execute memory load if -mem option is parsed, along with duration and utilization positional parameters
		if (!(memval == null)) {
			executeLoad(new MemoryLeaker(memval, multiplier));
		}
		//Execute disk load if -disk option is parsed, along with duration and utilization positional parameters.
		if (!(diskval == null)) {
			executeLoad(new DiskWriter(diskval, multiplier));
		}
		//Execute network packet delay if -netlag option is parsed, along with duration and delay positional parameters.
		if (!(netlagval == null)) {
			executeLoad(new NetworkEmulator(netlagval, "lag", multiplier));
		}
		// Execute network packet drop if -netdrop option is parsed, along with duration and %drop positional parameters.
		if (!(netdropval == null)) {
			System.out.println("-netdrop entered, " + (Integer.parseInt(netdropval[0]) * multiplier) + ", "
					+ Integer.parseInt(netdropval[1]));
			executeLoad(new NetworkEmulator(netdropval, "drop", multiplier));
		}
		// Execute network packet duplication if -netnoise option is parsed, along with duration and % of packets to be duplicated positional parameters.
		if (!(netnoiseval == null)) {
			executeLoad(new NetworkEmulator(netnoiseval, "noise", multiplier));
		}
		// Execute network bandwidth throtting if -netlimit option is parsed, along with duration and MB/s of bandwidth positional parameters.
		if (!(netlimitval == null)) {
			executeLoad(new NetworkEmulator(netlimitval, "throttle", multiplier));
		}
		//Execute system reboot if -reboot option is parsed, 
		if (!(rebootval == null)) {
			executeLoad(new MachineReboot(rebootval, multiplier));
		}

		// Join all busythreads before termination
		for (int i = 0; i < threadArray.size(); i++) {
			try {
				threadArray.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Do one last logging before termination, and cease logging jobs.
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
