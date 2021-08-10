package org.javocmaven.Javocmaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class test {

	public static void main(String[] args) {
		String url = "https://ipinfo.io";
		String os = System.getProperty("os.name");
		String result = checkHTTPResponse(os, url);
		System.out.println(result);
	}
	
	private static String checkHTTPResponse(String type, String url) {
		String command;
		String line = "";
		ProcessBuilder builder;
		if (type.contains("Windows")) {
			try {
				command = "add-type @\"\"\r\n" + "    using System.Net;\r\n"
						+ "    using System.Security.Cryptography.X509Certificates;\r\n"
						+ "    public class TrustAllCertsPolicy : ICertificatePolicy {\r\n"
						+ "        public bool CheckValidationResult(\r\n"
						+ "            ServicePoint srvPoint, X509Certificate certificate,\r\n"
						+ "            WebRequest request, int certificateProblem) {\r\n"
						+ "            return true;\r\n" + "        }\r\n" + "    }\r\n" + "\"\"\"@\r\n"
						+ "[System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy\r\n"
						+ "\r\n" + "try{\r\n" + "$result = Invoke-WebRequest -Uri \"" + url + "\"\r\n"
						+ "$statusCode = [int]$result.StatusCode\r\n" + "}\r\n" + "catch [System.Net.WebException]{\r\n"
						+ "$statusCode = [int]$_.Exception.Response.StatusCode\r\n" + "}\r\n" + "echo $statusCode";
				builder = new ProcessBuilder("powershell.exe", command);
				builder.redirectErrorStream(true);
				Process p = builder.start();
				p.getOutputStream().close();
				BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = stdout.readLine()) != null) {
					stdout.close();
					p.destroy();
					if (Objects.equals(line, "") || Objects.equals(line, "0")) {
						line = "Connection Failed.";
					}
					return line;
				}
				stdout.close();
				BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = stderr.readLine()) != null) {
					stderr.close();
					p.destroy();
					if (Objects.equals(line, "") || Objects.equals(line, "0")) {
						line = "Connection Failed.";
					}
					return line;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				command = "curl -I -k -s --max-time 10 " + url + " | grep HTTP | awk '{print $2}'";
				builder = new ProcessBuilder("bash", "-c", command);
				builder.redirectErrorStream(true);
				Process p = builder.start();
				p.getOutputStream().close();
				BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = stdout.readLine()) != null) {
					stdout.close();
					p.destroy();
					if (Objects.equals(line, "") || Objects.equals(line, "0")) {
						line = "Connection Failed.";
					}
					return line;
				}
				stdout.close();
				BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while ((line = stderr.readLine()) != null) {
					stderr.close();
					p.destroy();
					if (Objects.equals(line, "") || Objects.equals(line, "0")) {
						line = "Connection Failed.";
					}
					return line;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (Objects.equals(line, "") || Objects.equals(line, "0")) {
			line = "Connection Failed.";
		}
		return line;
	}

	private static String execCommand(ProcessBuilder builder) throws IOException {
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
		return line;
	}
}
