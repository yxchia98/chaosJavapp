package org.javocmaven.Javocmaven;

import picocli.CommandLine.Option;

public class CLIFormatter {

	@Option(names = { "-seconds", "--seconds" })
	private boolean seconds = false;

	@Option(names = { "-cpu", "--cpu" }, arity = "*")
	private String[] cpuvalues;

	@Option(names = { "-mem", "--mem" }, arity = "*")
	private String[] memvalues;

	@Option(names = { "-disk", "--disk" }, arity = "*")
	private String[] diskvalues;

	@Option(names = { "-netlag", "--netlag" }, arity = "*")
	private String[] netlagvalues;

	@Option(names = { "-netnoise", "--netnoise" }, arity = "*")
	private String[] netnoisevalues;

	@Option(names = { "-netdrop", "--netdrop" }, arity = "*")
	private String[] netdropvalues;

	@Option(names = { "-netlimit", "--netlimit" }, arity = "*")
	private String[] netlimitvalues;

	@Option(names = { "-reboot", "--reboot" }, arity = "*")
	private String[] rebootvalues;

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

}
