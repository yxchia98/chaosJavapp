package org.javocmaven.Javocmaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MachineReboot extends Loader {

	private int duration = 0;
	
	public MachineReboot(int duration) {
		this.duration = duration;
	}

	public MachineReboot(String[] arguments, int multiplier) {
		if (arguments.length >= 1) {
			this.duration = Integer.parseInt(arguments[0]) * multiplier;
		}
	}

	public void load() {
		String operatingSystem = System.getProperty("os.name");
		if (operatingSystem.contains("Windows")) {
			rebootWindows();
		} else {
			rebootLinux();
		}
	}

	private void rebootWindows() {
		String command = "Restart-Computer -Force";
		try {
			System.out.println("Rebooting System in " + this.duration + "s. (Windows). (" + Logger.getCurrentDateTime() + ")");
			Thread.sleep(this.duration * 1000);
			execCommand(new ProcessBuilder("powershell.exe", command));
		} catch (IOException | InterruptedException e) {
			System.out.println("Unable to execute command.");
			e.printStackTrace();
		}
	}

	private void rebootLinux() {

		String command = "shutdown -r now";
		try {
			System.out.println("Rebooting System in " + this.duration + "s. (Linux). (" + Logger.getCurrentDateTime() + ")");
			Thread.sleep(this.duration * 1000);
			execCommand(new ProcessBuilder("bash", "-c", command));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected static void execCommand(ProcessBuilder builder) throws IOException {
		builder.redirectErrorStream(true);
		Process p = builder.start();
		p.getOutputStream().close();
		String line;
		// Standard Output
		BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = stdout.readLine()) != null) {
			System.out.println(line);
		}
		stdout.close();
		// Standard Error
		BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((line = stderr.readLine()) != null) {
		}
		stderr.close();
		p.destroy();
	}

}
