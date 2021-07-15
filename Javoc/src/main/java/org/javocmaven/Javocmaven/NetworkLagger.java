package org.javocmaven.Javocmaven;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.zip.ZipException;

public class NetworkLagger extends Loader {

	private int duration;
	private double utilization;
	private String type = "lag";
	private URI zipfile;
	private String zipfilepath, folder;

	public NetworkLagger(int duration, double utilization) {
		this.duration = duration;
		this.utilization = utilization;
	}

	public NetworkLagger(String[] arguments, String type, String durationType) {
		this.type = type;
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
		String operatingSystem = System.getProperty("os.name");
		if (operatingSystem.contains("Windows")) {
			try {
				File clumsyfolder = new File(ResourceFile.getJarDir() + "\\clumsy-0.3rc3-win64");
				if(!clumsyfolder.isDirectory()) {
					extractClumsy();
				}
			} catch (URISyntaxException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
			if (this.type.equals("lag")) {
				System.out.println("Injecting network latency of " + this.utilization + "ms, for " + this.duration
						+ "s (Windows machine)");
				this.lagWindows();
			} else if (this.type.equals("noise")) {
				System.out.println("Injecting network packet duplication of " + this.utilization + "%, for "
						+ this.duration + "s (Windows machine)");
				this.noiseWindows();
			} else if (this.type.equals("drop")) {
				System.out.println("Injecting network packet loss of " + this.utilization + "%, for " + this.duration
						+ "s (Windows machine)");
				this.dropWindows();
			} else if (this.type.equals("throttle")) {
				System.out.println("Throttling bandwith to " + this.utilization + "MBs, for " + this.duration
						+ "s (Windows machine)");
				this.throttleWindows();
			} else {
				System.out.println("Invalid Arguments entered");
			}
		} else if (operatingSystem.contains("Linux")) {
			if (this.type.equals("lag")) {
				System.out.println("Injecting network latency of " + this.utilization + "ms, for " + this.duration
						+ "s (Linux machine)");
				this.lagLinux();
			} else if (this.type.equals("noise")) {
				System.out.println("Injecting network packet duplication of " + this.utilization + "%, for "
						+ this.duration + "s (Linux machine)");
				this.noiseLinux();
			} else if (this.type.equals("drop")) {
				System.out.println("Injecting network packet loss of " + this.utilization + "%, for " + this.duration
						+ "s (Linux machine)");
				this.dropLinux();
			} else if (this.type.equals("throttle")) {
				System.out.println("Throttling bandwith to " + this.utilization + "MBs, for " + this.duration
						+ "s (Linux machine)");
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
					.getParent().toString();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String appPath = currentdir + "\\clumsy-0.3rc3-win64\\clumsy.exe";
		String stop = "Stop-Process -Name 'clumsy'";
		String lagtime = "Start-Sleep -s " + Integer.toString(this.duration);
		String arguments = "--filter \"\"ip.DstAddr >= 0.0.0.0 \"\"\" --lag on --lag-inbound off --lag-outbound on --lag-time "
				+ this.utilization;
		String start = "Start-Process -WindowStyle Hidden " + appPath + " -ArgumentList '" + arguments + "'";
//		System.out.println(dir);
		try {
			execCommand(new ProcessBuilder("powershell.exe", start, "\n", lagtime, "\n", stop));
		} catch (IOException e) {
			System.out.println("Unable to execute command.");
			e.printStackTrace();
		}
	}

	private void lagLinux() {
		String startcommand = "tc qdisc add dev ens192 root netem delay " + this.utilization + "ms";
		String endcommand = "tc qdisc del dev ens192 root netem delay " + this.utilization + "ms";
		try {
			execCommand(new ProcessBuilder("bash", "-c", startcommand));
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("bash", "-c", endcommand));
		} catch (IOException | InterruptedException e) {
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
		String lagtime = "Start-Sleep -s " + Integer.toString(this.duration);
		String arguments = "--filter \"\"ip.DstAddr >= 0.0.0.0 \"\"\" --duplicate on --duplicate-inbound off --duplicate-outbound on --duplicate-count 2 --duplicate-chance "
				+ this.utilization;
		String start = "Start-Process -WindowStyle Hidden " + appPath + " -ArgumentList '" + arguments + "'";
//		System.out.println(dir);
		try {
			execCommand(new ProcessBuilder("powershell.exe", start, "\n", lagtime, "\n", stop));
		} catch (IOException e) {
			System.out.println("Unable to execute command.");
			e.printStackTrace();
		}
	}

	private void noiseLinux() {
		String startcommand = "tc qdisc add dev ens192 root netem duplicate " + this.utilization + "%";
		String endcommand = "tc qdisc del dev ens192 root netem duplicate " + this.utilization + "%";
		try {
			execCommand(new ProcessBuilder("bash", "-c", startcommand));
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("bash", "-c", endcommand));
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
		String lagtime = "Start-Sleep -s " + Integer.toString(this.duration);
		String arguments = "--filter \"\"ip.DstAddr >= 0.0.0.0 \"\"\" --drop on --drop-outbound on --drop-inbound off --drop-chance "
				+ this.utilization;
		String start = "Start-Process -WindowStyle Hidden " + appPath + " -ArgumentList '" + arguments + "'";
//		System.out.println(dir);
		try {
			execCommand(new ProcessBuilder("powershell.exe", start, "\n", lagtime, "\n", stop));
		} catch (IOException e) {
			System.out.println("Unable to execute command.");
			e.printStackTrace();
		}
	}

	private void dropLinux() {
		String startcommand = "tc qdisc add dev ens192 root netem loss " + this.utilization + "%";
		String endcommand = "tc qdisc del dev ens192 root netem loss " + this.utilization + "%";
		try {
			execCommand(new ProcessBuilder("bash", "-c", startcommand));
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("bash", "-c", endcommand));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void throttleWindows() {
		String start = "New-NetQosPolicy -Name 'JavocPolicy' -Default -ThrottleRateActionBitsPerSecond "
				+ this.utilization + "MB";
		String stop = "Remove-NetQosPolicy -Name 'JavocPolicy' -Confirm:$false";
		String lagtime = "Start-Sleep -s " + Integer.toString(this.duration);
		try {
			execCommand(new ProcessBuilder("powershell.exe", start, "\n", lagtime, "\n", stop));
		} catch (IOException e) {
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
			execCommand(new ProcessBuilder("bash", "-c", startcommand));
			Thread.sleep(duration * 1000);
			execCommand(new ProcessBuilder("bash", "-c", endcommand));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void extractClumsy() throws URISyntaxException, ZipException, IOException, InterruptedException {
		this.zipfile = ResourceFile.getFile(ResourceFile.getJarURI(), "clumsy-0.3rc3-win64.zip");
		this.zipfilepath = new File(zipfile).getPath();
		this.folder = ResourceFile.getJarDir();
		ResourceFile.unzipFolder(this.zipfilepath, this.folder);
		Thread.sleep(5000);
	}

//	private void deleteClumsy() throws IOException {
//		File clumsyFolder = new File(this.folder);
//		FileDeleteStrategy.FORCE.delete(new File(this.zipfilepath));
//		for (File file : clumsyFolder.listFiles()) {
//			FileDeleteStrategy.FORCE.delete(file);
//		}
//	}

}
