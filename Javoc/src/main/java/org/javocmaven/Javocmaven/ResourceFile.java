package org.javocmaven.Javocmaven;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ResourceFile {

	protected static String getJarDir() throws URISyntaxException {
		return new File(ResourceFile.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
	}

	protected static URI getJarURI() throws URISyntaxException {
		return ResourceFile.class.getProtectionDomain().getCodeSource().getLocation().toURI();
	}

	protected static URI getFile(final URI where, final String fileName) throws ZipException, IOException {
		File location;
		URI fileURI;

		location = new File(where);

		// not in a JAR, just return the path on disk
		if (location.isDirectory()) {
			fileURI = URI.create(where.toString() + fileName);
		} else {

			ZipFile zipFile = new ZipFile(location);

			try {
				fileURI = extractFile(zipFile, fileName);
			} finally {
				zipFile.close();
			}
		}
		return (fileURI);
	}

	protected static URI extractFile(final ZipFile zipFile, final String fileName) throws IOException {
		File tempFile;
		ZipEntry entry;
		InputStream zipStream;
		OutputStream fileStream;

//		tempFile = File.createTempFile(fileName, Long.toString(System.currentTimeMillis()));
//		tempFile = File.createTempFile(fileName, ".zip");
		tempFile = new File(fileName);
		entry = zipFile.getEntry(fileName);

		if (entry == null) {
			throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
		}

		zipStream = zipFile.getInputStream(entry);
		fileStream = null;

		try {
			byte[] buf;
			int i;

			fileStream = new FileOutputStream(tempFile);
			buf = new byte[1024];
			i = 0;

			while ((i = zipStream.read(buf)) != -1) {
				fileStream.write(buf, 0, i);
			}
		} finally {
			close(zipStream);
			close(fileStream);
		}
		tempFile.deleteOnExit();
		return (tempFile.toURI());
	}

	protected static void unzipFolder(String source, String target) throws IOException {

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(Paths.get(source).toFile()))) {

			// list files in zip
			ZipEntry zipEntry = zis.getNextEntry();

			while (zipEntry != null) {

				boolean isDirectory = false;
				// example 1.1
				// some zip stored files and folders separately
				// e.g data/
				// data/folder/
				// data/folder/file.txt
				if (zipEntry.getName().endsWith(File.separator)) {
					isDirectory = true;
				}

				Path newPath = zipSlipProtect(zipEntry, Paths.get(target));

				if (isDirectory) {
					Files.createDirectories(newPath);
				} else {

					// example 1.2
					// some zip stored file path only, need create parent directories
					// e.g data/folder/file.txt
					if (newPath.getParent() != null) {
						if (Files.notExists(newPath.getParent())) {
							Files.createDirectories(newPath.getParent());
						}
					}

					// copy files, nio
					Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);

					// copy files, classic
					/*
					 * try (FileOutputStream fos = new FileOutputStream(newPath.toFile())) { byte[]
					 * buffer = new byte[1024]; int len; while ((len = zis.read(buffer)) > 0) {
					 * fos.write(buffer, 0, len); } }
					 */
				}
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		}

	}

	// protect zip slip attack
	protected static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) throws IOException {

		// test zip slip vulnerability
		// Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());

		Path targetDirResolved = targetDir.resolve(zipEntry.getName());

		// make sure normalized file still has targetDir as its prefix
		// else throws exception
		Path normalizePath = targetDirResolved.normalize();
		if (!normalizePath.startsWith(targetDir)) {
			throw new IOException("Bad zip entry: " + zipEntry.getName());
		}

		return normalizePath;
	}

//	protected static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
//		File destFile = new File(destinationDir, zipEntry.getName());
//
//		String destDirPath = destinationDir.getCanonicalPath();
//		String destFilePath = destFile.getCanonicalPath();
//
//		if (!destFilePath.startsWith(destDirPath + File.separator)) {
//			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
//		}
//
//		return destFile;
//	}

	protected static void close(final Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
