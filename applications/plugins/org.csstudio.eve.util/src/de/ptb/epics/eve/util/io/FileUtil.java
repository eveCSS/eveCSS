package de.ptb.epics.eve.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class FileUtil {
	/*
	 * Note: could be more elegant (replaced) with Java 7.
	 * See http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
	 */
	
	/**
	 * @param sourceFile the source
	 * @param destFile the target
	 * @throws IOException if source or destination file not found
	 */
	public static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}
}