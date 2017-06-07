package de.ptb.epics.eve.util.zip.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.Test;

import de.ptb.epics.eve.util.zip.ZipUtil;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class ZipUtilTest {
	
	@Test
	public void testCompressUncompress() {
		byte[] testData = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 15, 30, 28};
		try {
			assertArrayEquals(testData, 
					ZipUtil.decompress(
							ZipUtil.compress(testData)));
		} catch (IOException | DataFormatException e) {
			fail(e.getMessage());
		}
	}
}