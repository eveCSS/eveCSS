package de.ptb.epics.eve.util.io.tests;

import static org.junit.Assert.*;
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
	
	/**
	 * Tests whether {@link StringUtil#isPositionList(String)} is acting 
	 * correctly for a selection of Strings.
	 */
	@Test
	public void testIsPositionList() {
		assertTrue(StringUtil.isPositionList("1,2,3,4,5"));
		assertTrue(StringUtil.isPositionList("1,2,3.0,4,5"));
		assertTrue(StringUtil.isPositionList("1e4,2.4"));
		assertTrue(StringUtil.isPositionList("2e5,3e-7"));
		assertTrue(StringUtil.isPositionList("2e-5,3E-7"));
		assertTrue(StringUtil.isPositionList("2e+5,3E+7"));
		assertFalse(StringUtil.isPositionList("1,2,3,4,5,"));
		assertFalse(StringUtil.isPositionList("1a,2,3,4,5"));
		assertFalse(StringUtil.isPositionList("x,2,3,4,5b"));
		assertFalse(StringUtil.isPositionList("xyz"));
	}
}