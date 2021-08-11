package org.javocmaven.Javocmaven;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true, version = "1.0.1")
public class CLIFormatter implements Runnable {

	@Option(names = { "-seconds", "--seconds" }, description = "Evaluate duration as seconds instead of minutes")
	private boolean seconds = false;

	@Option(names = { "-cpu", "--cpu" }, arity = "*", description = "Load CPU for <duration> <utilization%>")
	private String[] cpuvalues;

	@Option(names = { "-mem", "--mem" }, arity = "*", description = "Load Memory for <duration> <utilization%>")
	private String[] memvalues;

	@Option(names = { "-disk",
			"--disk" }, arity = "*", description = "Load Disk for <duration> <utilization%> on root volume")
	private String[] diskvalues;

	@Option(names = { "-netlag",
			"--netlag" }, arity = "*", description = "Network Packet Delay for <duration> <utilization in ms>")
	private String[] netlagvalues;

	@Option(names = { "-netnoise",
			"--netnoise" }, arity = "*", description = "Duplicate Network Packets for <duration> <duplication%>")
	private String[] netnoisevalues;

	@Option(names = { "-netdrop",
			"--netdrop" }, arity = "*", description = "Drop Network Packets for <duration> <packetloss%>")
	private String[] netdropvalues;

	@Option(names = { "-netlimit",
			"--netlimit" }, arity = "*", description = "Network Bandwidth Throttling for <duration> <utilization in MB/s>")
	private String[] netlimitvalues;

	@Option(names = { "-reboot", "--reboot" }, arity = "*", description = "Reboot system after <duration>")
	private String[] rebootvalues;

	@Option(names = { "-url", "--url" }, arity = "*", description = "URL for validation of Experiments <url>")
	private String[] url;
	
	@Option(names = { "-service", "--service" }, arity = "*", description = "Service name for validation of Experiements <service name>")
	private String[] service;

	public boolean isSeconds() {
		return seconds;
	}

	public String[] getCpuvalues() {
		return cpuvalues;
	}

	public String[] getMemvalues() {
		return memvalues;
	}

	public String[] getDiskvalues() {
		return diskvalues;
	}

	public String[] getNetlagvalues() {
		return netlagvalues;
	}

	public String[] getNetnoisevalues() {
		return netnoisevalues;
	}

	public String[] getNetdropvalues() {
		return netdropvalues;
	}

	public String[] getNetlimitvalues() {
		return netlimitvalues;
	}

	public String[] getRebootvalues() {
		return rebootvalues;
	}

	public String getUrl() {
		return ((url == null) ? "" : url[0]);
	}
	
	public String getService() {
		return ((service == null) ? "" : service[0]);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
