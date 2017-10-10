package de.ptb.epics.eve.util.io.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class StringUtilTest {
	
	/**
	 * Tests whether {@link StringUtil#isPositionList(String)} is acting 
	 * correctly for a selection of Strings.
	 */
	@Test
	public void testIsPositionList() {
		assertTrue(StringUtil.isPositionList("1,2,3,4,5", Integer.class));
		assertTrue(StringUtil.isPositionList("1,2,3.0,4,5", Double.class));
		assertTrue(StringUtil.isPositionList("1e4,2.4", Double.class));
		assertTrue(StringUtil.isPositionList("2e5,3e-7", Double.class));
		assertTrue(StringUtil.isPositionList("2e-5,3E-7", Double.class));
		assertTrue(StringUtil.isPositionList("2e+5,3E+7", Double.class));
		assertFalse(StringUtil.isPositionList("1,2,3,4,5,", Integer.class));
		assertFalse(StringUtil.isPositionList("1a,2,3,4,5", Integer.class));
		assertFalse(StringUtil.isPositionList("x,2,3,4,5b", Integer.class));
		assertFalse(StringUtil.isPositionList("xyz", Double.class));
		
		// TODO more test cases for different data types
	}
	
	/**
	 * Tests whether {@link StringUtil#buildCommaSeparatedString(List)} is 
	 * returning strings as expected.
	 */
	@Test
	public void testBuildCommaSeparatedString() {
		List<Double> values = new ArrayList<Double>();
		values.add(3.0);
		values.add(4.0);
		values.add(5.0);
		values.add(6.0);
		assertEquals("3.0, 4.0, 5.0, 6.0",
				StringUtil.buildCommaSeparatedString(values));
	}
}