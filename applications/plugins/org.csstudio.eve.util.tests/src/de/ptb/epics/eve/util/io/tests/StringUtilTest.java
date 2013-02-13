package de.ptb.epics.eve.util.io.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class StringUtilTest {
	
	/**
	 * 
	 */
	@Ignore
	@Test
	public void testRemoveCarriageReturn() {
		assertEquals("Hello World!\n",
				StringUtil.removeCarriageReturn("Hello World!\r\n"));
	}
}