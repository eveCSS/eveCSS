package de.ptb.epics.eve.util.math.range.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import de.ptb.epics.eve.util.io.StringUtil;
import de.ptb.epics.eve.util.math.range.DoubleRange;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class DoubleRangeTest {
	@Test
	public void testDoubleRange() {
		String range = "1.0, 2.0, 3.0, 4.0, 5.0";
		assertEquals(range, 
			new DoubleRange(1.0, 1.0, 5.0).toString());
	}
	
	@Test
	public void testDoubleRegexp() {
		Pattern p = Pattern.compile(DoubleRange.DOUBLE_REGEXP);
		Matcher m = p.matcher("0");
		assertTrue(m.reset(Double.toString(Double.MIN_VALUE)).matches());
		assertTrue(m.reset(Double.toString(Double.MAX_VALUE)).matches());
		assertTrue(m.reset("-0.9E-13").matches());
		assertTrue(m.reset("+1.4e+22").matches());
		assertTrue(m.reset(".1e0").matches());
		assertTrue(m.reset("0.0001").matches());
	}
	
	@Test
	public void testDoubleRangeRegexp() {
		Pattern p = Pattern.compile(DoubleRange.DOUBLE_RANGE_REGEXP);
		Matcher m = p.matcher("0");
		assertTrue(m.reset("0:0.2:8").matches());
		assertTrue(m.reset("0.0:0.2:8.0").matches());
	}
	
	@Test
	public void testDoubleRangeRepeatedRegexp() {
		Pattern p = Pattern.compile(
				DoubleRange.DOUBLE_RANGE_REPEATED_REGEXP);
		Matcher m = p.matcher("0");
		assertTrue(m.reset("1.0:10").matches());
		assertTrue(m.reset("1:1.2:10").matches());
		assertTrue(m.reset("1:1:10E3,0:2:50").matches());
		assertTrue(m.reset("1:1:10,  0:2.1:50").matches());
		assertTrue(m.reset("1:1:10,0:2.1:50.4,1:8:100").matches());
		
		assertFalse(m.reset("").matches());
		assertFalse(m.reset("2.1").matches());
		assertFalse(m.reset("1:").matches());
		assertFalse(m.reset("1:10,").matches());
		assertFalse(m.reset("1:10, 2.1").matches());
		assertFalse(m.reset("1:10, 1.2").matches());
		assertFalse(m.reset("1:10, 1.2:").matches());
		assertFalse(m.reset("1:10, 1.2/").matches());
	}
	
	@Test
	public void testGetValues() {
		assertEquals("1.0, 2.0, 3.0", StringUtil.buildCommaSeparatedString(
				new DoubleRange("1.0:3").getValues()));
		assertEquals("3.0, 2.0, 1.0", StringUtil.buildCommaSeparatedString(
				new DoubleRange("3.0:1").getValues()));
		/*
		assertEquals("1.0, 1.4, 1.8, 2.2, 2.6, 3.0, 3.2", StringUtil.
				buildCommaSeparatedString(new DoubleRange("1:0.4:3.2").getValues()));
				*/
	}
}