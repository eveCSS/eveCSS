package de.ptb.epics.eve.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
	
	/**
	 * 
	 * 
	 * Thanks to http://stackoverflow.com/a/326440 (referenced 2013-02-13)
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			if (Charset.isSupported("UTF-8")) {
				return Charset.forName("UTF-8").decode(bb).toString();
			}
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<String> readLines(String path) throws IOException {
		List<String> result = new ArrayList<String>();
		FileInputStream stream = new FileInputStream(new File(path));
		BufferedReader br = new BufferedReader(new InputStreamReader(stream,
				Charset.forName("UTF-8")));
		String line;
		while((line = br.readLine()) != null) {
			result.add(line);
		}
		return result;
	}
}