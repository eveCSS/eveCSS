package de.ptb.epics.eve.util.hdf5;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.util.zip.ZipUtil;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class HDF5Util {
	/**
	 * Eight byte signature a HDF5 file (without any super block information)
	 * begins with.
	 */
	public static final byte[] HDF5_SIGNATURE = {-119, 72, 68, 70, 13, 10, 26, 10};
	
	/**
	 * Eight byte signature an EveSCML file begins with.
	 */
	public static final byte[] EVESCML_SIGNATURE = {69, 86, 69, 99, 83, 67, 77, 76};
	
	private static final int INTEGER_NUMBER_OF_BYTES = 4;
	
	private static final Logger LOGGER = 
			Logger.getLogger(HDF5Util.class.getName());
	
	/**
	 * Returns whether the given file is a HDF5 file 
	 * without any super block (i.e. contains the HDF5 
	 * signature at the beginning).
	 * 
	 * @param file the file that should be validated
	 * @return whether the given file is a HDF5 file 
	 * 	without any super block
	 */
	public static boolean isHDF5(File file) {
		return HDF5Util.beginsWithSignature(file, HDF5_SIGNATURE);
	}
	
	/**
	 * Returns whether the given file is an EveSCML file 
	 * (i.e. contains the EveSCML signature at the beginning).
	 * 
	 * @param file the file that should be validated
	 * @return whether the given file is an EveSCML file
	 */
	public static boolean isEveSCML(File file) {
		return HDF5Util.beginsWithSignature(file, EVESCML_SIGNATURE);
	}
	
	private static boolean beginsWithSignature(File file, byte[] signature) {
		DataInputStream input = null;
		try {
			input = new DataInputStream(
					new BufferedInputStream(
							new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		try {
			for (int i = 0; i < signature.length; i++) {
				if (input.readByte() != signature[i]) {
					return false;
				}
			}
		} catch (EOFException e) {
			return false;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return true;
	}
	
	/**
	 * Extracts the embedded SCML data of the given HDF5 file into a temporary
	 * file.
	 * 
	 * @param h5File
	 *            the HDF5 file containing SCML data
	 * @return the temporary file containing the SCML data
	 * @throws SCMLNotFoundException
	 *             if the given file does not contain SCML data
	 * @throws SCMLExtractionException
	 *             if an error occurred during extraction
	 */
	public static File getSCMLTempFile(File h5File)
			throws SCMLNotFoundException, SCMLExtractionException {
		File scmlFile = null;
		FileOutputStream fos = null;
		try {
			scmlFile = File.createTempFile(h5File.getName() + "-", ".scml",
					new File(System.getProperty("java.io.tmpdir") + "/eve-"
							+ System.getProperty("user.name")));
			scmlFile.deleteOnExit();
			fos = new FileOutputStream(scmlFile);
			fos.write(HDF5Util.getSCML(h5File));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SCMLExtractionException(e.getMessage());
		} catch (SCMLNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SCMLNotFoundException(e.getMessage());
		} catch (DataFormatException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SCMLExtractionException(e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return scmlFile;
	}
	
	/**
	 * Returns the SCML data embedded in the given file
	 * @param file the file the scml data should be extracted from
	 * @return a byte array containing the scml data
	 * @throws SCMLNotFoundException if the file does not contain SCML data
	 * @throws IOException if file not found or an I/O error occurs
	 * @throws DataFormatException if the compressed data format is invalid
	 */
	public static byte[] getSCML(File file) throws SCMLNotFoundException, 
			IOException, DataFormatException {
		if (!HDF5Util.isEveSCML(file)) {
			throw new SCMLNotFoundException("No Embedded SCML data found!");
		}
		try (
			FileInputStream fileInputStream = new FileInputStream(file);
			DataInputStream dataInputStream = new DataInputStream(
				new BufferedInputStream(fileInputStream)); 
		) {
		
			// skip signature part
			for (int i = 0; i < HDF5Util.EVESCML_SIGNATURE.length; i++) {
				dataInputStream.readByte();
			}
		
			ByteBuffer buffer = ByteBuffer.allocate(
					HDF5Util.INTEGER_NUMBER_OF_BYTES);
			buffer.order(ByteOrder.BIG_ENDIAN);
			buffer.putInt(dataInputStream.readInt());
			long lengthCompressed = intToUnsignedLong(buffer.getInt(0)); 
				// Integer.toUnsignedLong(buffer.getInt(0));
		
			buffer.clear();
			buffer.putInt(dataInputStream.readInt());
			long lengthUncompressed = intToUnsignedLong(buffer.getInt(0));
				// Integer.toUnsignedLong(buffer.getInt(0));
		
			byte[] compressedSCML = new byte[(int)lengthCompressed];
			for (int i = 0; i < lengthCompressed; i++) {
				compressedSCML[i] = dataInputStream.readByte();
			}
		
			byte[] uncompressedSCML = ZipUtil.decompress(compressedSCML);
		
			if (uncompressedSCML.length != lengthUncompressed) {
				LOGGER.error(
					"uncompressed length field differs from actual size");
			}
		
			return uncompressedSCML;
		}
	}
	
	/*
	 * helper method for as long as java 7 is used.
	 * In java 8 use Integer.toUnsignedLong instead!
	 */
	private static long intToUnsignedLong(int i) {
		return i & 0x00000000ffffffffL;
	}
}