package org.javocmaven.Javocmaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Loader {
	public abstract void load();
	protected void execCommand(ProcessBuilder builder) throws IOException {
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
			System.out.println(line);
		}
		stderr.close();
		p.destroy();
	}
}
