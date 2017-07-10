package de.ptb.epics.eve.util.math.range.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import de.ptb.epics.eve.util.io.StringUtil;
import de.ptb.epics.eve.util.math.range.IntegerRange;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class IntegerRangeTest {
	@Test
	public void testIntegerRange() {
		String range = "1, 2, 3, 4, 5";
		assertEquals(range, 
			new IntegerRange(1,1,5).toString());
	}
	
	@Test
	public void testIntegerRegexp() {
		Pattern p = Pattern.compile(IntegerRange.INTEGER_REGEXP);
		Matcher m = p.matcher("0");
		assertTrue(m.reset(Integer.toString(Integer.MIN_VALUE)).matches());
		assertTrue(m.reset(Integer.toString(Integer.MAX_VALUE)).matches());
		assertTrue(m.reset("+1").matches());
		assertTrue(m.reset("1").matches());
		assertTrue(m.reset("0").matches());
		assertTrue(m.reset("-1").matches());
		assertTrue(m.reset("1002030").matches());
		assertFalse(m.reset("0.1").matches());
		assertFalse(m.reset("01").matches());
	}
	
	@Test 
	public void testIntegerRangeRegexp() {
		Pattern p = Pattern.compile(IntegerRange.INTEGER_RANGE_REGEXP);
		Matcher m = p.matcher("0");
		assertTrue(m.reset("1:10").matches());
		assertTrue(m.reset("1:2:10").matches());
		assertTrue(m.reset("1:10/5").matches());
	}
	
	@Test
	public void testIntegerRangeRepeatedRegexp() {
		Pattern p = Pattern.compile(
				IntegerRange.INTEGER_RANGE_REPEATED_REGEXP);
		Matcher m = p.matcher("0");
		assertTrue(m.reset("1:10").matches());
		assertTrue(m.reset("1:1:10").matches());
		assertTrue(m.reset("1:1:10,0:2:50").matches());
		assertTrue(m.reset("1:1:10 ,0:2:50").matches());
		assertTrue(m.reset("1:1:10  ,0:2:50").matches());
		assertTrue(m.reset("1:1:10, 0:2:50").matches());
		assertTrue(m.reset("1:1:10,  0:2:50").matches());
		assertTrue(m.reset("1:1:10 , 0:2:50").matches());
		assertTrue(m.reset("1:1:10   ,   0:2:50").matches());
		assertTrue(m.reset("1:1:10,0:2:50,1:8:100").matches());
		
		assertFalse(m.reset("").matches());
		assertFalse(m.reset("2").matches());
		assertFalse(m.reset("1:").matches());
		assertFalse(m.reset("1:10,").matches());
		assertFalse(m.reset("1:10, 2").matches());
		assertFalse(m.reset("1:10, 12:").matches());
		assertFalse(m.reset("1:10, 12:20:").matches());
		assertFalse(m.reset("1:10, 12:20/").matches());
	}
	
	@Test
	public void testGetValues() {
		assertEquals("1, 2, 3, 4, 5", StringUtil.buildCommaSeparatedString(
				new IntegerRange("1:1:5").getValues()));
		assertEquals("9, 8, 7, 6", StringUtil.buildCommaSeparatedString(
				new IntegerRange("9:6").getValues()));
		assertEquals("10", StringUtil.buildCommaSeparatedString(
				new IntegerRange("10:10").getValues()));
	}
}