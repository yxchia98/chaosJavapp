package org.javocmaven.Javocmaven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimerTask;

import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;

public class Logger extends TimerTask {
	private String date, time, cpuload = " ", os, totalmem_string, usedmem_string, usedpercentmem_string,
			totalspace_string, usedspace_string, usedpercentdisk_string;

	public void run() {
		File disklog = new File("/");
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		Date datetime = new Date(ts.getTime());
		DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
		this.date = dateformat.format(datetime);
		this.time = timeformat.format(datetime);

		long totalspace = disklog.getTotalSpace();
		long freespace = disklog.getUsableSpace();
		long usedspace = totalspace - freespace;
		double usedpercentdisk = (double) usedspace / totalspace * 100;
		this.totalspace_string = String.valueOf(Math.toIntExact((long) (totalspace / Math.pow(2, 20))));
		this.usedspace_string = String.valueOf(Math.toIntExact((long) (usedspace / Math.pow(2, 20))));
		this.usedpercentdisk_string = String.valueOf(Math.round(usedpercentdisk));

		com.sun.management.OperatingSystemMXBean oslog = (com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();

		long totalmem = oslog.getTotalMemorySize();
		long freemem = oslog.getFreeMemorySize();
		long usedmem = totalmem - freemem;
//		long jvmmem = Runtime.getRuntime().maxMemory();
		double usedpercentmem = (double) usedmem / totalmem * 100;
		this.totalmem_string = String.valueOf(Math.toIntExact((long) (totalmem / Math.pow(2, 20))));
		this.usedmem_string = String.valueOf(Math.toIntExact((long) (usedmem / Math.pow(2, 20))));
		this.usedpercentmem_string = String.valueOf(Math.round(usedpercentmem));

		this.os = System.getProperty("os.name");

		if (this.os.contains("Windows")) {
			try {
				this.cpuload = getCPU("Windows");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				this.cpuload = getCPU("Linux");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		exportTXT();
		exportCSV();
		exportJSON();

	}

	private void exportCSV() {

		List<String[]> csvData = new ArrayList<>();
		String path = "." + File.separator + "javoc_log" + File.separator + "javoclog.csv";
		File file = new File(path);
		file.getParentFile().mkdirs();

		String[] csvLog = { this.date, this.time, this.os, this.cpuload, this.totalmem_string, this.usedmem_string,
				this.usedpercentmem_string, this.totalspace_string, this.usedspace_string,
				this.usedpercentdisk_string };
		if (file.exists()) {
			// append to file
			try {
				csvData.add(csvLog);
				CSVWriter writer = new CSVWriter(new FileWriter(path, true));
				writer.writeAll(csvData, false);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				String[] header = { "Log Date", "Log Time", "Platform", "CPU Load(%)", "Total Memory(MB)",
						"Used Memory(MB)", "Used Memory(%)", "Total Space(MB)", "Used Space(MB)", "Used Space(%)" };
				csvData.add(header);
				csvData.add(csvLog);
				CSVWriter writer = new CSVWriter(new FileWriter(path));
				writer.writeAll(csvData, false);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void exportJSON() {

		LinkedHashMap<String, String> valuesmap = new LinkedHashMap<>();
		
		String path = "." + File.separator + "javoc_log" + File.separator + "javoclog.json";
		File file = new File(path);
		file.getParentFile().mkdirs();

		valuesmap.put("Log Date", this.date);
		valuesmap.put("Log Time", this.time);
		valuesmap.put("Platform", this.os);
		valuesmap.put("CPU Load(%)", this.cpuload);
		valuesmap.put("Total Memory(MB)", this.totalmem_string);
		valuesmap.put("Used Memory(MB)", this.usedmem_string);
		valuesmap.put("Used Memory(%)", this.usedpercentmem_string);
		valuesmap.put("Total Disk(MB)", this.totalspace_string);
		valuesmap.put("Used Disk(MB)", this.usedspace_string);
		valuesmap.put("Used Disk(%)", this.usedpercentdisk_string);
		GsonBuilder builder = new GsonBuilder();
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

//		File javoclog = new File("javoc_log.json");
//		FileOutputStream fos;
//		try {
//			fos = new FileOutputStream(javoclog, true);
//			fos.write(jsonData.getBytes());
//			fos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	private void exportTXT() {
		
		String path = "." + File.separator + "javoc_log" + File.separator + "javoclog.txt";
		File file = new File(path);
		file.getParentFile().mkdirs();

		String logtxt = "LOG DATE: " + this.date + "\n";
		logtxt += "LOG TIME: " + this.time + "\n";

		logtxt += "Platform: " + this.os + "\n";

		logtxt += "CPU load: " + this.cpuload + "%\n";

		logtxt += "Total Memory(100%): " + this.totalmem_string + "MB\nUsed Memory(" + this.usedpercentmem_string
				+ "%): " + this.usedmem_string + "MB\n";

//		logtxt += "JVM Allocated Memory: " + jvmmem / Math.pow(2, 20) + "MB\n";

		logtxt += "Total Space(100%): " + this.totalspace_string + "MB\nUsed Space(" + this.usedpercentdisk_string
				+ "%): " + this.usedspace_string + "MB\n\n";

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

	private String getCPU(String type) throws IOException {
		String line = null;
		ProcessBuilder builder = null;
		if (type.equals("Windows")) {
			builder = new ProcessBuilder("powershell.exe",
					"Get-WmiObject -class win32_processor | Measure-Object -property LoadPercentage -Average | Select-Object -ExpandProperty Average");
			builder.redirectErrorStream(true);
			Process p = builder.start();
			p.getOutputStream().close();
			// Standard Output
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = stdout.readLine()) != null) {
				stdout.close();
				return line;
			}
			stdout.close();
			// Standard Error
			BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = stderr.readLine()) != null) {
				stderr.close();
				return line;
			}
		} else {
			builder = new ProcessBuilder("bash", "-c",
					"top -bn1 | grep -Po \"[0-9.]*(?=( id,))\" | awk '{print 100 - $1}'");
//			builder = new ProcessBuilder("bash", "-c",
//					"top -bn1 | grep -Po \"[0-9.]*(?=( id,))\" | awk '{print 100 - $1\"%\"}'");
			builder.redirectErrorStream(true);
			Process p = builder.start();
			p.getOutputStream().close();
			// Standard Output
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = stdout.readLine()) != null) {
				stdout.close();
				return line;
			}
			stdout.close();
			// Standard Error
			BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = stderr.readLine()) != null) {
				stderr.close();
				return line;
			}
		}
		return line;
	}
}
