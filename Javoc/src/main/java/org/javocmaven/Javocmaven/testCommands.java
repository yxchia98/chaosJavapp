package org.javocmaven.Javocmaven;

import picocli.CommandLine;

public class testCommands {

	public static void main(String[] args) {
		CLIFormatter arguments = CommandLine.populateCommand(new CLIFormatter(), args);
		CommandLine commandLine = new CommandLine(new CLIFormatter());
		commandLine.parseArgs(args);
		if (commandLine.isUsageHelpRequested()) {
			   commandLine.usage(System.out);
			   return;
			} else if (commandLine.isVersionHelpRequested()) {
			   commandLine.printVersionHelp(System.out);
			   return;
			}
		System.out.println(arguments.getUrl());
	}
}
