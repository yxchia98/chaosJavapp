package org.javocmaven.Javocmaven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;
import com.google.gson.GsonBuilder;

import de.siegmar.fastcsv.writer.CsvWriter;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class Logger extends TimerTask {
	private String date, time, cpuload = "", os, totalmem_string, usedmem_string, usedpercentmem_string,
			totalspace_string, usedspace_string, usedpercentdisk_string, httpstatus = "", url = "", loadType,
			loadDuration, loadUtilization, service = "", validateService = "";

	public void run() {

		File disklog = new File("/"); // Specify root folder to get total amount of disk space
		Timestamp ts = new Timestamp(System.currentTimeMillis()); // Get current time

		DecimalFormat df = new DecimalFormat("0.00"); // Used to convert other types to double

		// Get information on current chaos
		this.loadType = MainMenu.loadType;
		this.loadDuration = df.format(MainMenu.loadDuration);
		this.loadUtilization = df.format(MainMenu.loadUtilization);

		// Get current log's date and time
		Date datetime = new Date(ts.getTime());
		DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
		this.date = dateformat.format(datetime);
		this.time = timeformat.format(datetime);

		// Get current disk utilization
		long totalspace = disklog.getTotalSpace();
		long freespace = disklog.getUsableSpace();
		long usedspace = totalspace - freespace;
		double usedpercentdisk = (double) usedspace / totalspace * 100;
		this.totalspace_string = df.format(totalspace / Math.pow(2, 20));
		this.usedspace_string = df.format(usedspace / Math.pow(2, 20));
		this.usedpercentdisk_string = df.format(usedpercentdisk);

		// Oshi to capture current memory utilization
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();

		long totalmem = hal.getMemory().getTotal();
		long freemem = hal.getMemory().getAvailable();
		long usedmem = totalmem - freemem;
		double usedpercentmem = (double) usedmem / totalmem * 100;
		this.totalmem_string = df.format(totalmem / Math.pow(2, 20));
		this.usedmem_string = df.format(usedmem / Math.pow(2, 20));
		this.usedpercentmem_string = df.format(usedpercentmem);

		this.os = System.getProperty("os.name"); // Get operating system name, to execute respective windows/linux
													// commands

		// capture current cpu utilization
		try {
			this.cpuload = getCPU(os);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Check if there is a need for HTTP validation, and get HTTP status code for
		// windows/linux
		if (MainMenu.HTTPExperiment) {
			this.url = MainMenu.url;
			this.httpstatus = checkHTTPResponse(os, this.url);
			if (this.httpstatus.equals("Connection Failed.") || this.httpstatus.charAt(0) == '4'
					|| this.httpstatus.charAt(0) == '5') { // validation fails if connection failed or an HTTP error
															// response is received
				System.out.println("Failed to establish connection to \"" + this.url + "\".");
				revertAndExit(); // revert current tests and exit
			}
		}
		// Check if there is a need for service validation
		if (MainMenu.ServiceExperiment) {
			this.service = MainMenu.service;
			ArrayList<String> services = getSerivces(os); // instantiate and get array list of services
			for (String str : services) {
				if (processRegionMatches(str, this.service)) { // check if requested service exists
					this.validateService = "Success";
					break;
				}
			}
			if (!this.validateService.equals("Success")) {
				this.validateService = "Failed"; // mark validation as failed if no matches found
				System.out.println("Requested service \"" + this.service + "\" is not running on the system.");
				revertAndExit(); // Revert current tests and exit
			}
		}

		// Export logs
		exportTXT();
		exportCSV();
		exportJSON();
	}

	// Faster string.contains() method
	private boolean processRegionMatches(String str, String substr) {
		for (int i = str.length() - substr.length(); i >= 0; i--)
			if (str.regionMatches(true, i, substr, 0, substr.length()))
				return true;
		return false;
	}

	// Method to export logs to CSV, CSV will be placed in the javoc_log folder.
	private void exportCSV() {

		List<String[]> csvData = new ArrayList<>();
		String path = "." + File.separator + "javoc_log" + File.separator + "javoclog.csv";
		File file = new File(path);
		file.getParentFile().mkdirs();

		// Parse in currently captured details into CSV row.
		String[] csvLog = { this.date, this.time, this.os, this.loadType, this.loadUtilization, this.loadDuration,
				this.cpuload, this.totalmem_string, this.usedmem_string, this.usedpercentmem_string,
				this.totalspace_string, this.usedspace_string, this.usedpercentdisk_string, this.url, this.httpstatus, this.service, this.validateService };
		if (file.exists()) {
			// append to file
			try {
				CsvWriter csv = CsvWriter.builder().build(file.toPath(), StandardCharsets.UTF_8,
						StandardOpenOption.APPEND);
				csvData.add(csvLog);
				for (String[] data : csvData) {
					csv.writeRow(data);
				}
				csv.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			// create new file and add headers before logging data.
			String[] header = { "Log Date", "Log Time", "Platform", "Load Type", "Load Utilization", "Load Duration",
					"CPU Load(%)", "Total Memory(MB)", "Used Memory(MB)", "Used Memory(%)", "Total Space(MB)",
					"Used Space(MB)", "Used Space(%)", "HTTP Validation URL", "HTTP Response", "Service",
					"Service Validation" };
			try {
				CsvWriter csv = CsvWriter.builder().build(file.toPath(), StandardCharsets.UTF_8,
						StandardOpenOption.CREATE);
				csvData.add(header);
				csvData.add(csvLog);
				for (String[] data : csvData) {
					csv.writeRow(data);
				}
				csv.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// Method to export logs to JSON format, logs will be placed in the javoc_log
	// folder
	private void exportJSON() {

		LinkedHashMap<String, Object> valuesmap = new LinkedHashMap<>(); // create hashmap to map key:value pair for
																			// JSON

		String path = "." + File.separator + "javoc_log" + File.separator + "javoclog.json";
		File file = new File(path);
		file.getParentFile().mkdirs();

		// Adding key:value fields into the hash map
		valuesmap.put("Log Date", this.date);
		valuesmap.put("Log Time", this.time);
		valuesmap.put("Platform", this.os);
		valuesmap.put("Load Type", this.loadType);
		valuesmap.put("Load Utlization", Double.parseDouble(this.loadUtilization));
		valuesmap.put("Load Duration", Math.round(Double.parseDouble(this.loadDuration)));
		valuesmap.put("CPU Load(%)", Double.parseDouble(this.cpuload));
		valuesmap.put("Total Memory(MB)", Double.parseDouble(this.totalmem_string));
		valuesmap.put("Used Memory(MB)", Double.parseDouble(this.usedmem_string));
		valuesmap.put("Used Memory(%)", Double.parseDouble(this.usedpercentmem_string));
		valuesmap.put("Total Disk(MB)", Double.parseDouble(this.totalspace_string));
		valuesmap.put("Used Disk(MB)", Double.parseDouble(this.usedspace_string));
		valuesmap.put("Used Disk(%)", Double.parseDouble(this.usedpercentdisk_string));
		valuesmap.put("HTTP Validation URL", this.url);
		valuesmap.put("HTTP Response", this.httpstatus);
		valuesmap.put("Service", this.service);
		valuesmap.put("Service Validation", this.validateService);
		GsonBuilder builder = new GsonBuilder(); // build JSON using GSON library
		builder.setPrettyPrinting();
		String jsonData = builder.create().toJson(valuesmap);

		try {
			FileWriter writer = new FileWriter(path, true);
			writer.write(jsonData);
			writer.write(System.getProperty("line.separator"));
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Method to export logs to TXT format, logs will be placed in the javoc_log
	// folder
	private void exportTXT() {

		String path = "." + File.separator + "javoc_log" + File.separator + "javoclog.txt";
		File file = new File(path);
		file.getParentFile().mkdirs();

		String logtxt = "LOG DATE: " + this.date + "\n";
		logtxt += "LOG TIME: " + this.time + "\n";

		logtxt += "Platform: " + this.os + "\n";

		logtxt += "Load Type: " + this.loadType + "\nLoad Utilization: " + this.loadUtilization + "\nLoad Duration: "
				+ this.loadDuration + "\n";

		logtxt += "CPU load: " + this.cpuload + "%\n";

		logtxt += "Total Memory(100%): " + this.totalmem_string + "MB\nUsed Memory(" + this.usedpercentmem_string
				+ "%): " + this.usedmem_string + "MB\n";

//		logtxt += "JVM Allocated Memory: " + jvmmem / Math.pow(2, 20) + "MB\n";

		logtxt += "Total Space(100%): " + this.totalspace_string + "MB\nUsed Space(" + this.usedpercentdisk_string
				+ "%): " + this.usedspace_string + "MB\n";

		logtxt += "HTTP Validation URL: " + this.url + "\nHTTP Response: " + this.httpstatus + "\n";

		logtxt += "Service: " + this.service + "\nService Validation: " + this.validateService + "\n\n";

		File javoclog = new File(path);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(javoclog, true);
			fos.write(logtxt.getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to get current CPU utilization for windows/linux
	private String getCPU(String operatingSystem) throws IOException {
		String line = null;
		ProcessBuilder builder = null;
		if (operatingSystem.contains("Windows")) {
			builder = new ProcessBuilder("powershell.exe",
					"Get-WmiObject -class win32_processor | Measure-Object -property LoadPercentage -Average | Select-Object -ExpandProperty Average"); // new
																																						// processbuilder
																																						// using
																																						// powershell
			builder.redirectErrorStream(true);
			Process p = builder.start();
			p.getOutputStream().close();
			// Standard Output
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = stdout.readLine()) != null) {
				stdout.close();
				p.destroy();
				return line;
			}
			stdout.close();
			// Standard Error
			BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = stderr.readLine()) != null) {
				stderr.close();
				p.destroy();
				return line;
			}
		} else if (operatingSystem.contains("Linux")) {
			builder = new ProcessBuilder("bash", "-c",
					"top -bn1 | grep -Po \"[0-9.]*(?=( id,))\" | awk '{print 100 - $1}'");	// new processbuilder using bash
//			builder = new ProcessBuilder("bash", "-c",
//					"top -bn1 | grep -Po \"[0-9.]*(?=( id,))\" | awk '{print 100 - $1\"%\"}'");
			builder.redirectErrorStream(true);
			Process p = builder.start();
			p.getOutputStream().close();
			// Standard Output
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = stdout.readLine()) != null) {
				stdout.close();
				p.destroy();
				return line;
			}
			stdout.close();
			// Standard Error
			BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = stderr.readLine()) != null) {
				stderr.close();
				p.destroy();
				return line;
			}
		}
		return line;
	}

	// Method to get current date time
	public static String getCurrentDateTime() {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		Date datetime = new Date(ts.getTime());
		DateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetimestring = datetimeformat.format(datetime);
		return datetimestring;
	}

	// Method to validify HTTP response of the url parsed in
	private String checkHTTPResponse(String type, String url) {
		String command;
		String line = "";
		ProcessBuilder builder;
		if (type.contains("Windows")) { // execute the following string on powershell if the system is windows
			try {
				command = "add-type @\"\"\r\n" + "    using System.Net;\r\n"
						+ "    using System.Security.Cryptography.X509Certificates;\r\n"
						+ "    public class TrustAllCertsPolicy : ICertificatePolicy {\r\n"
						+ "        public bool CheckValidationResult(\r\n"
						+ "            ServicePoint srvPoint, X509Certificate certificate,\r\n"
						+ "            WebRequest request, int certificateProblem) {\r\n"
						+ "            return true;\r\n" + "        }\r\n" + "    }\r\n" + "\"\"\"@\r\n"
						+ "[System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy\r\n"
						+ "\r\n" + "try{\r\n" + "$result = Invoke-WebRequest -Uri \"" + url + "\" -TimeoutSec 12\r\n"
						+ "$statusCode = [int]$result.StatusCode\r\n" + "}\r\n" + "catch [System.Net.WebException]{\r\n"
						+ "$statusCode = [int]$_.Exception.Response.StatusCode\r\n" + "}\r\n" + "echo $statusCode";
				builder = new ProcessBuilder("powershell.exe", command); // create new process builder, specificing to
																			// use powershell.exe
				builder.redirectErrorStream(true);
				Process p = builder.start(); // execute the command
				p.getOutputStream().close();
				BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = stdout.readLine()) != null) {
					stdout.close();
					p.destroy();
					if (Objects.equals(line, "") || Objects.equals(line, "0")) {
						line = "Connection Failed."; // if the returned output is empty or 0, connection has failed
					}
					return line;
				}
				stdout.close();
				BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = stderr.readLine()) != null) {
					stderr.close();
					p.destroy();
					if (Objects.equals(line, "") || Objects.equals(line, "0")) {
						line = "Connection Failed."; // mark connection as failed if an error occurred
					}
					return line;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try { // execute the following string on bash if system is linux
				command = "curl -I -k -s --max-time 12 " + url + " | grep HTTP | awk '{print $2}'";
				builder = new ProcessBuilder("bash", "-c", command); // instantiate process builder, using bash
				builder.redirectErrorStream(true);
				Process p = builder.start();
				p.getOutputStream().close();
				BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = stdout.readLine()) != null) {
					stdout.close();
					p.destroy();
					if (Objects.equals(line, "") || Objects.equals(line, "0")) {
						line = "Connection Failed."; // mark connection as failed if console output is of 0 or ""
					}
					return line;
				}
				stdout.close();
				BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = stderr.readLine()) != null) {
					stderr.close();
					p.destroy();
					if (Objects.equals(line, "") || Objects.equals(line, "0")) {
						line = "Connection Failed."; // mark connection as failed if there is an error
					}
					return line;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (Objects.equals(line, "") || Objects.equals(line, "0") || Objects.equals(line, null)) {
			line = "Connection Failed."; // if the console has no output and the return value is still null, mark
											// connection as failed
		}
		return line;
	}

	private ArrayList<String> getSerivces(String operatingSystem) {
		ArrayList<String> serviceList = new ArrayList<>(); // instantiate array list to contain all the retrived
															// services
		if (operatingSystem.contains("Windows")) {
			String command = "Get-Service | Where-Object {$_.Status -eq \"\"Running\"\"\"} | Format-Table -Property Name -HideTableHeaders";
			try {
				ProcessBuilder builder = new ProcessBuilder("powershell.exe", command); // instantiate new process
																						// builder using powershell.exe
				builder.redirectErrorStream(true);
				Process p = builder.start();
				p.getOutputStream().close();
				String line;
				// Standard Output
				BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = stdout.readLine()) != null) {
					if (!line.equals("")) {
						serviceList.add(line); // if console output is not empty, add current line into the services
												// array
					}

				}
				stdout.close();
				// Standard Error
				BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = stderr.readLine()) != null) {
					System.out.println(line); // print out error
				}
				stderr.close();
				p.destroy();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (operatingSystem.contains("Linux")) {
			String command = "systemctl --type=service --no-legend | grep running | awk '{print $1}'";
			try {
				ProcessBuilder builder = new ProcessBuilder("bash", "-c", command); // instantitate new processbuilder
																					// using bash
				builder.redirectErrorStream(true);
				Process p = builder.start();
				p.getOutputStream().close();
				String line;
				// Standard Output
				BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = stdout.readLine()) != null) {
					if (!line.equals("")) {
						serviceList.add(line); // if console output is not empty, add it to the services array
					}

				}
				stdout.close();
				// Standard Error
				BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = stderr.readLine()) != null) {
					System.out.println(line); // print out error
				}
				stderr.close();
				p.destroy();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return serviceList;
	}

	private void revertAndExit() {
		// export current logs before exiting.
		exportTXT();
		exportCSV();
		exportJSON();
		// print test fail info to console
		System.out.println("Validation failed, refer to logs for further information.\nLoad Type: " + this.loadType
				+ "\nLoad Utilization: " + this.loadUtilization + "\nLoad Duration: " + this.loadDuration
				+ "\nReverting and exiting...");
		// revert any currently applied policies or configurations from the chaos applied
		if (MainMenu.loadType.equals("Network Packet Delay")) {
			NetworkEmulator.stopLag(this.os, MainMenu.loadUtilization);
		}
		if (MainMenu.loadType.equals("Network Packet Duplicate")) {
			NetworkEmulator.stopNoise(this.os, MainMenu.loadUtilization);
		}
		if (MainMenu.loadType.equals("Network Packet Drop")) {
			NetworkEmulator.stopDrop(this.os, MainMenu.loadUtilization);
		}
		if (MainMenu.loadType.equals("Network Bandwidth Throttling")) {
			NetworkEmulator.stopThrottle(this.os, MainMenu.loadUtilization);
		}
		// terminate the java application
		System.exit(0);
		return;
	}
}
