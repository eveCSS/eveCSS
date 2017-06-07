package de.ptb.epics.eve.util.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author Marcus Michalsky
 * @since 1.28
 * @see https://dzone.com/articles/how-compress-and-uncompress
 */
public class ZipUtil {
	
	/**
	 * Decompresses the given byte array.
	 * 
	 * @param compressed the compressed data
	 * @return the uncompressed data
	 * @throws IOException if an I/O error occurs
	 * @throws DataFormatException if the compressed data format is invalid
	 */
	public static byte[] decompress(byte[] compressed) 
			throws IOException, DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(compressed);
		ByteArrayOutputStream outputStream = 
				new ByteArrayOutputStream(compressed.length);
		byte[] buffer = new byte[1024];
		while (!inflater.finished()) {
			int count = inflater.inflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		return outputStream.toByteArray();
	}
	
	/**
	 * Compresses the given byte array.
	 * 
	 * @param uncompressed the uncompressed data
	 * @return the compressed data
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] compress(byte[] uncompressed) throws IOException {
		Deflater deflater = new Deflater();
		deflater.setInput(uncompressed);
		ByteArrayOutputStream outputStream = 
				new ByteArrayOutputStream(uncompressed.length);
		deflater.finish();
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		return outputStream.toByteArray();
	}
}