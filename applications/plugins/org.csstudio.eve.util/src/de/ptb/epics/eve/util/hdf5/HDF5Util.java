package de.ptb.epics.eve.util.hdf5;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

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
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return true;
	}
}