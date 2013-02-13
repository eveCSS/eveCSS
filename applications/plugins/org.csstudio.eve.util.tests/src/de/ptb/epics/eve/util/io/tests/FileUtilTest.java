package de.ptb.epics.eve.util.io.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import de.ptb.epics.eve.util.io.FileUtil;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class FileUtilTest {

	/* 
	 * Note that these tests only work if invoked by the ant script in the 
	 * data.tests plugin because of the file paths...
	 */
	
	/**
	 * Tests whether an UTF-8 encoded file is read.
	 */
	@Test
	public void testReadFile() {
		try {
			String file = FileUtil
					.readFile("../org.csstudio.eve.util.tests/files/file.txt");
			assertEquals("This is a UTF-8 encoded file.", file);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Tests whether reading a non-UTF-8 encoded file fails as expected.
	 */
	@Test
	public void testReadFileFail() {
		String file;
		try {
			file = FileUtil
					.readFile("../org.csstudio.eve.util.tests/files/file-nonUTF8.txt");
			assertFalse(file.equals("This isn't UTF-8. ÄÖÜ"));
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Tests whether the carriage return and line feed characters are
	 * recognized.
	 */
	@Test
	public void testReadLines() {
		try {
			List<String> windows = FileUtil
					.readLines("../org.csstudio.eve.util.tests/files/windows.txt");
			List<String> linux = FileUtil
					.readLines("../org.csstudio.eve.util.tests/files/linux.txt");
			assertTrue(windows.size() == 5);
			assertTrue(linux.size() == 5);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}