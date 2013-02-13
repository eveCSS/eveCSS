package de.ptb.epics.eve.util.io.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.ptb.epics.eve.util.io.FileUtil;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class FileUtilTest {
	
	/**
	 * 
	 */
	@Test
	public void testReadFile() {
		try {
			String file = FileUtil.readFile("files/file.txt");
			assertEquals("This is a UTF-8 encoded file.", file);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testReadFileFail() {
		String file;
		try {
			file = FileUtil.readFile("files/file-nonUTF8.txt");
			assertFalse(file.equals("This isn't UTF-8. ÄÖÜ"));
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}