package de.ptb.epics.eve.util.hdf5.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import de.ptb.epics.eve.util.hdf5.HDF5Util;

public class HDF5UtilTest {
	@Test
	public void testIsHDF5() {
		File testFile;
		try {
			testFile = File.createTempFile("JUnit-testIsHDF5", null);
			testFile.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(testFile);
			fos.write(HDF5Util.HDF5_SIGNATURE, 0, HDF5Util.HDF5_SIGNATURE.length);
			fos.close();
			assertTrue(HDF5Util.isHDF5(testFile));
			assertFalse(HDF5Util.isEveSCML(testFile));
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testIsEveSCML() {
		File testFile;
		try {
			testFile = File.createTempFile("JUnit-testIsEveSCML", null);
			testFile.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(testFile);
			fos.write(HDF5Util.EVESCML_SIGNATURE, 0, HDF5Util.EVESCML_SIGNATURE.length);
			fos.close();
			assertTrue(HDF5Util.isEveSCML(testFile));
			assertFalse(HDF5Util.isHDF5(testFile));
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}