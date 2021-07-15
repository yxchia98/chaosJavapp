package org.javocmaven.Javocmaven;

import java.util.ArrayList;
import java.util.Timer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MainMenu {

	private static void executeLoad(Loader loadObj) {
		loadObj.load();
	}

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption(Option.builder("cpu").desc("CPU Loader").hasArgs().build());
		options.addOption(Option.builder("cpus").desc("CPU Loader in Seconds").hasArgs().build());
		options.addOption(Option.builder("mem").desc("Memory Leaker").hasArgs().build());
		options.addOption(Option.builder("mems").desc("Memory Leaker in Seconds").hasArgs().build());
		options.addOption(Option.builder("disk").desc("Disk Hogger").hasArgs().build());
		options.addOption(Option.builder("disks").desc("Disk Hogger in Seconds").hasArgs().build());
		options.addOption(Option.builder("netlag").desc("Network Latency Injector").hasArgs().build());
		options.addOption(
				Option.builder("netlags").desc("Network Latency Injector in Seconds").hasArgs().build());
		options.addOption(Option.builder("netnoise").desc("Network Packet Duplicator").hasArgs().build());
		options.addOption(
				Option.builder("netnoises").desc("Network Packet Duplicator in seconds").hasArgs().build());
		options.addOption(Option.builder("netdrop").desc("Network Packet Dropper").hasArgs().build());
		options.addOption(
				Option.builder("netdrops").desc("Network Packet Dropper in seconds").hasArgs().build());
		options.addOption(Option.builder("netlimit").desc("Network Traffic Throttler").hasArgs().build());
		options.addOption(
				Option.builder("netlimits").desc("Network Traffic Throttler in seconds").hasArgs().build());
		options.addOption(Option.builder("reboot").desc("Reboot current machine").hasArgs().build());
		options.addOption(Option.builder("reboots").desc("Reboot current machine in seconds").hasArgs().build());
		ArrayList<BusyThread> threadArray = new ArrayList<BusyThread>();

		Timer timer = new Timer();
		timer.schedule(new Logger(), 0, 60000);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			Option[] parsedoptions = cmd.getOptions();
			for (Option a : parsedoptions) {
				if (a.getOpt().equals("cpu")) {
					CpuLoader cpuloader = new CpuLoader(a.getValues(), "minutes");
					executeLoad(cpuloader);
					threadArray = cpuloader.getThreadArray();
				} else if (a.getOpt().equals("cpus")) {
					CpuLoader cpuloader = new CpuLoader(a.getValues(), "seconds");
					executeLoad(cpuloader);
					threadArray = cpuloader.getThreadArray();
				} else if (a.getOpt().equals("mem")) {
					executeLoad(new MemoryLeaker(a.getValues(), "minutes"));
				} else if (a.getOpt().equals("mems")) {
					executeLoad(new MemoryLeaker(a.getValues(), "seconds"));
				} else if (a.getOpt().equals("disk")) {
					executeLoad(new DiskWriter(a.getValues(), "minutes"));
				} else if (a.getOpt().equals("disks")) {
					executeLoad(new DiskWriter(a.getValues(), "seconds"));
				} else if (a.getOpt().equals("netlag")) {
					executeLoad(new NetworkLagger(a.getValues(), "lag", "minutes"));
				} else if (a.getOpt().equals("netlags")) {
					executeLoad(new NetworkLagger(a.getValues(), "lag", "seconds"));
				} else if (a.getOpt().equals("netnoise")) {
					executeLoad(new NetworkLagger(a.getValues(), "noise", "minutes"));
				} else if (a.getOpt().equals("netnoises")) {
					executeLoad(new NetworkLagger(a.getValues(), "noise", "seconds"));
				} else if (a.getOpt().equals("netdrop")) {
					executeLoad(new NetworkLagger(a.getValues(), "drop", "minutes"));
				} else if (a.getOpt().equals("netdrops")) {
					executeLoad(new NetworkLagger(a.getValues(), "drop", "seconds"));
				} else if (a.getOpt().equals("netlimit")) {
					executeLoad(new NetworkLagger(a.getValues(), "throttle", "minutes"));
				} else if (a.getOpt().equals("netlimits")) {
					executeLoad(new NetworkLagger(a.getValues(), "throttle", "seconds"));
				} else if (a.getOpt().equals("reboot")) {
					executeLoad(new MachineReboot(a.getValues(), "minutes"));
				} else if (a.getOpt().equals("reboots")) {
					executeLoad(new MachineReboot(a.getValues(), "seconds"));
				} else {
					System.out.println("Not enough arguments entered.");
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < threadArray.size(); i++) {
			try {
				threadArray.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			timer.schedule(new Logger(), 1000);
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timer.cancel();
		timer.purge();

	}
}
