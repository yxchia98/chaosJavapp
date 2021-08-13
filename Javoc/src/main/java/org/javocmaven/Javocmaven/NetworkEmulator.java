package org.javocmaven.Javocmaven;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.zip.ZipException;

public class NetworkEmulator extends Loader {

	private int duration = 5;
	private double utilization = 50;
	private String type = "lag";
	private URI zipfile;
	private String zipfilepath, folder;

	public NetworkEmulator(int duration, double utilization) {
		this.duration = duration;
		this.utilization = utilization;
	}

	public NetworkEmulator(String[] arguments, String type, int multiplier) {
		this.type = type;
		if (arguments.length >= 2) {
			this.duration = Integer.parseInt(arguments[0]) * multiplier;
			this.utilization = Double.parseDouble(arguments[1]);
		} else if (arguments.length == 1) {
			this.duration = Integer.parseInt(arguments[0]) * multiplier;
		} else {
		}
		if (this.type.equals("lag")) {
			MainMenu.loadType = "Network Packet Delay";
		} else if (this.type.equals("noise")) {
			MainMenu.loadType = "Network Packet Duplicate";
		} else if (this.type.equals("drop")) {
			MainMenu.loadType = "Network Packet Drop";
		} else if (this.type.equals("throttle")) {
			MainMenu.loadType = "Network Bandwidth Throttling";
		}
		MainMenu.loadUtilization = this.utilization;
		MainMenu.loadDuration = this.duration;
	}

	public void load() {
		String operatingSystem = System.getProperty("os.name");
		// execute respective network simulations for windows/linux
		if (operatingSystem.contains("Windows")) {
			try {
				File clumsyfolder = new File(ResourceFile.getJarDir() + "\\clumsy-0.3rc3-win64");	// check if the clumsy folder exists
				if (!clumsyfolder.isDirectory()) {
					extractClumsy(); // extract contents of the clumsy zip file
				}
			} catch (URISyntaxException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
			if (this.type.equals("lag")) {
				System.out.println("Injecting network latency of " + this.utilization + "ms, for " + this.duration
						+ "s (Windows machine). (" + Logger.getCurrentDateTime() + ")");
				this.lagWindows();
			} else if (this.type.equals("noise")) {
				System.out.println("Injecting network packet duplication of " + this.utilization + "%, for "
						+ this.duration + "s (Windows machine). (" + Logger.getCurrentDateTime() + ")");
				this.noiseWindows();
			} else if (this.type.equals("drop")) {
				System.out.println("Injecting network packet loss of " + this.utilization + "%, for " + this.duration
						+ "s (Windows machine). (" + Logger.getCurrentDateTime() + ")");
				this.dropWindows();
			} else if (this.type.equals("throttle")) {
				System.out.println("Throttling bandwith to " + this.utilization + "MBs, for " + this.duration
						+ "s (Windows machine). (" + Logger.getCurrentDateTime() + ")");
				this.throttleWindows();
			} else {
				System.out.println("Invalid Arguments entered");
			}
		} else if (operatingSystem.contains("Linux")) {
			if (this.type.equals("lag")) {
				System.out.println("Injecting network latency of " + this.utilization + "ms, for " + this.duration
						+ "s (Linux machine). (" + Logger.getCurrentDateTime() + ")");
				this.lagLinux();
			} else if (this.type.equals("noise")) {
				System.out.println("Injecting network packet duplication of " + this.utilization + "%, for "
						+ this.duration + "s (Linux machine). (" + Logger.getCurrentDateTime() + ")");
				this.noiseLinux();
			} else if (this.type.equals("drop")) {
				System.out.println("Injecting network packet loss of " + this.utilization + "%, for " + this.duration
						+ "s (Linux machine). (" + Logger.getCurrentDateTime() + ")");
				this.dropLinux();
			} else if (this.type.equals("throttle")) {
				System.out.println("Throttling bandwith to " + this.utilization + "MBs, for " + this.duration
						+ "s (Linux machine). (" + Logger.getCurrentDateTime() + ")");
				this.throttleLinux();
			} else {
				System.out.println("Invalid Arguments entered");
			}

		}

	}

	private void lagWindows() {
		String currentdir = "\\";
		try {
			currentdir = Paths.get(MainMenu.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent().toString();	// get current directory path
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String appPath = currentdir + "\\clumsy-0.3rc3-win64\\clumsy.exe";
		String stop = "Stop-Process -Name 'clumsy'";
//		String lagtime = "Start-Sleep -s " + Integer.toString(this.duration);
		String arguments = "--filter \"\"ip.DstAddr >= 0.0.0.0 \"\"\" --lag on --lag-inbound off --lag-outbound on --lag-time "
				+ this.utilization;
		String start = "Start-Process -WindowStyle Hidden " + appPath + " -ArgumentList '" + arguments + "'";
//		System.out.println(dir);
		try {
//			execCommand(new ProcessBuilder("powershell.exe", start, "\n", lagtime, "\n", stop));
			execCommand(new ProcessBuilder("powershell.exe", start));	// start clumsy.exe with parameters, using CLI
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("powershell.exe", stop));	// kill the clumsy process
		} catch (IOException | InterruptedException e) {
			System.out.println("Unable to execute command.");
			e.printStackTrace();
		}
	}

	private void lagLinux() {
		String startcommand = "tc qdisc add dev ens192 root netem delay " + this.utilization + "ms";
		String endcommand = "tc qdisc del dev ens192 root netem delay " + this.utilization + "ms";
		try {
			execCommand(new ProcessBuilder("bash", "-c", startcommand));	// add new queue discipline using cli
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("bash", "-c", endcommand));	// delete added qdisc
		} catch (IOException | InterruptedException e) {
			System.out.println("Unable to execute command.");
			e.printStackTrace();
		}
	}

	private void noiseWindows() {
		String currentdir = "\\";
		try {
			currentdir = Paths.get(MainMenu.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent().toString();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String appPath = currentdir + "\\clumsy-0.3rc3-win64\\clumsy.exe";
		String stop = "Stop-Process -Name 'clumsy'";
//		String lagtime = "Start-Sleep -s " + Integer.toString(this.duration);
		String arguments = "--filter \"\"ip.DstAddr >= 0.0.0.0 \"\"\" --duplicate on --duplicate-inbound off --duplicate-outbound on --duplicate-count 2 --duplicate-chance "
				+ this.utilization;
		String start = "Start-Process -WindowStyle Hidden " + appPath + " -ArgumentList '" + arguments + "'";
//		System.out.println(dir);
		try {
			execCommand(new ProcessBuilder("powershell.exe", start));	// start clumsy using CLI, parsing specified parameters
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("powershell.exe", stop));	// kill the clumsy process
		} catch (IOException | InterruptedException e) {
			System.out.println("Unable to execute command.");
			e.printStackTrace();
		}
	}

	private void noiseLinux() {
		String startcommand = "tc qdisc add dev ens192 root netem duplicate " + this.utilization + "%";
		String endcommand = "tc qdisc del dev ens192 root netem duplicate " + this.utilization + "%";
		try {
			execCommand(new ProcessBuilder("bash", "-c", startcommand));	// add new queue discipline using bash CLI
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("bash", "-c", endcommand));		// delete the added qdisc
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void dropWindows() {
		String currentdir = "\\";
		try {
			currentdir = Paths.get(MainMenu.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent().toString();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String appPath = currentdir + "\\clumsy-0.3rc3-win64\\clumsy.exe";
		String stop = "Stop-Process -Name 'clumsy'";
//		String lagtime = "Start-Sleep -s " + Integer.toString(this.duration);
		String arguments = "--filter \"\"ip.DstAddr >= 0.0.0.0 \"\"\" --drop on --drop-outbound on --drop-inbound off --drop-chance "
				+ this.utilization;
		String start = "Start-Process -WindowStyle Hidden " + appPath + " -ArgumentList '" + arguments + "'";
//		System.out.println(dir);
		try {
			execCommand(new ProcessBuilder("powershell.exe", start));	// start clumsy using CLI, parsing specified parameters
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("powershell.exe", stop));	// kill the clumsy process
		} catch (IOException | InterruptedException e) {
			System.out.println("Unable to execute command.");
			e.printStackTrace();
		}
	}

	private void dropLinux() {
		String startcommand = "tc qdisc add dev ens192 root netem loss " + this.utilization + "%";
		String endcommand = "tc qdisc del dev ens192 root netem loss " + this.utilization + "%";
		try {
			execCommand(new ProcessBuilder("bash", "-c", startcommand));	// add new queue discipline to drop packets
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("bash", "-c", endcommand));		// delete added qdisc
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void throttleWindows() {
		String start = "New-NetQosPolicy -Name 'JavocPolicy' -Default -ThrottleRateActionBitsPerSecond "
				+ this.utilization + "MB";
		String stop = "Remove-NetQosPolicy -Name 'JavocPolicy' -Confirm:$false";
//		String lagtime = "Start-Sleep -s " + Integer.toString(this.duration);
		try {
			execCommand(new ProcessBuilder("powershell.exe", start));	// Add new policy for all traffic to throttle bandwidth
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("powershell.exe", stop));	// delete the added policy
		} catch (IOException | InterruptedException e) {
			System.out.println("Unable to execute command.");
			e.printStackTrace();
		}
	}

	private void throttleLinux() {
		double Mbits = this.utilization * Math.pow(2, 20) / Math.pow(10, 6);
		String startcommand = "tc qdisc add dev ens192 root tbf rate " + Mbits + "Mbit burst " + Mbits
				+ "mb latency 1000ms";
		String endcommand = "tc qdisc del dev ens192 root tbf rate " + Mbits + "Mbit burst " + Mbits
				+ "mb latency 1000ms";
		try {
			execCommand(new ProcessBuilder("bash", "-c", startcommand));	// add new queue discipline to throttle bandwidth
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("bash", "-c", endcommand));		// delete the added qdisc
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void extractClumsy() throws URISyntaxException, ZipException, IOException, InterruptedException {
		this.zipfile = ResourceFile.getFile(ResourceFile.getJarURI(), "clumsy-0.3rc3-win64.zip");	// locate zip file inside the packaged jar
		this.zipfilepath = new File(zipfile).getPath();
		this.folder = ResourceFile.getJarDir();
		ResourceFile.unzipFolder(this.zipfilepath, this.folder);	// unzip file 
		Thread.sleep(5000);
	}

	protected static void stopLag(String operatingSystem, double utilization) {
		// commands to specifically stop current packet delay profiles
		String stop;
		if (operatingSystem.contains("Windows")) {
			stop = "Stop-Process -Name 'clumsy'";
			try {
				execCommand(new ProcessBuilder("powershell.exe", stop));
			} catch (IOException e) {
				System.out.println("Unable to execute command.");
				e.printStackTrace();
			}

		} else if (operatingSystem.contains("Linux")) {
			stop = "tc qdisc del dev ens192 root netem delay " + utilization + "ms";
			try {
				execCommand(new ProcessBuilder("bash", "-c", stop));
			} catch (IOException e) {
				System.out.println("Unable to execute command.");
				e.printStackTrace();
			}
		}
	}

	protected static void stopNoise(String operatingSystem, double utilization) {
		// commands to specifically stop current packet duplication profiles
		String stop;
		if (operatingSystem.contains("Windows")) {
			stop = "Stop-Process -Name 'clumsy'";
			try {
				execCommand(new ProcessBuilder("powershell.exe", stop));
			} catch (IOException e) {
				System.out.println("Unable to execute command.");
				e.printStackTrace();
			}
		} else if (operatingSystem.contains("Linux")) {
			stop = "tc qdisc del dev ens192 root netem duplicate " + utilization + "%";
			try {
				execCommand(new ProcessBuilder("bash", "-c", stop));
			} catch (IOException e) {
				System.out.println("Unable to execute command.");
				e.printStackTrace();
			}
		}
	}

	protected static void stopDrop(String operatingSystem, double utilization) {
		// commands to specifically stop current packet drop profiles
		String stop;
		if (operatingSystem.contains("Windows")) {
			stop = "Stop-Process -Name 'clumsy'";
			try {
				execCommand(new ProcessBuilder("powershell.exe", stop));
			} catch (IOException e) {
				System.out.println("Unable to execute command.");
				e.printStackTrace();
			}
		} else if (operatingSystem.contains("Linux")) {
			stop = "tc qdisc del dev ens192 root netem loss " + utilization + "%";
			try {
				execCommand(new ProcessBuilder("bash", "-c", stop));
			} catch (IOException e) {
				System.out.println("Unable to execute command.");
				e.printStackTrace();
			}
		}
	}

	protected static void stopThrottle(String operatingSystem, double utilization) {
		// commands to specifically stop current bandwidth throttling profiles
		String stop;
		if (operatingSystem.contains("Windows")) {
			stop = "Remove-NetQosPolicy -Name 'JavocPolicy' -Confirm:$false";
			try {
				execCommand(new ProcessBuilder("powershell.exe", stop));
			} catch (IOException e) {
				System.out.println("Unable to execute command.");
				e.printStackTrace();
			}
		} else if (operatingSystem.contains("Linux")) {
			double Mbits = utilization * Math.pow(2, 20) / Math.pow(10, 6);
			stop = "tc qdisc del dev ens192 root tbf rate " + Mbits + "Mbit burst " + Mbits + "mb latency 1000ms";
			try {
				execCommand(new ProcessBuilder("bash", "-c", stop));
			} catch (IOException e) {
				System.out.println("Unable to execute command.");
				e.printStackTrace();
			}
		}
	}

}
