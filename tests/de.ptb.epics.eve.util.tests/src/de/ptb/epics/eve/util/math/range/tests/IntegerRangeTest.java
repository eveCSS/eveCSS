package de.ptb.epics.eve.util.math.range.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

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
	
	@Test(expected=IllegalArgumentException.class)
	public void testIntegerRangeIllegalArgumentException() {
		new IntegerRange("1:10/");
	}
	
	@Test
	public void testGetValues() {
		// case j:k (i=1), j < k
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(1,2,3)),
				new IntegerRange("1:3").getValues());
		// case j:k (i=1), j > k
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(9,8,7)),
				new IntegerRange("9:7").getValues());
		// case j:k (i=1), j = k
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(10)),
				new IntegerRange("10:10").getValues());
		
		// case j:i:k, j < k ^ (j + i) < k
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(1,3,5,7,9,10)),
				new IntegerRange("1:2:10").getValues());
		// case j:i:k, j < k ^ (j + i) > k
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(1,5)),
				new IntegerRange("1:5:5").getValues());
		// case j:i:k, j > k ^ (j - i) > k
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(10,8,6,4,2,1)),
				new IntegerRange("10:2:1").getValues());
		// case j:i:k, j > k ^ (j - i) < k
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(5,1)),
				new IntegerRange("5:5:1").getValues());
		// case j:i:k, j = k
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(1)),
				new IntegerRange("1:2:1").getValues());
		
		// case j:i:k, j < k ^ (j + i) < j
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(-5,-4,-3,-2,-1)),
				new IntegerRange("-5:-1:-1").getValues());
		// case j:i:k, j > k ^ (j - i) > j
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(5,4,3,2,1)),
				new IntegerRange("5:-1:1").getValues());
		
		// case j:k/N, j < k ^ (k - j) > N
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10)),
				new IntegerRange("1:10/5").getValues());
		// case j:k/N, j < k ^ (k - j) < N
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(1,10)),
				new IntegerRange("1:10/10").getValues());
		// case j:k/N, j > k ^ (j - k) > N
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(10,9,8,7,6,5,4,3,2,1)),
				new IntegerRange("10:1/5").getValues());
		// case j:k/N, j > k ^ (j - k) < N
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(10,1)),
				new IntegerRange("10:1/10").getValues());
		// case j:k/N, j = k
		assertEquals(
				new ArrayList<Integer>(Arrays.asList(1)),
				new IntegerRange("1:1/10").getValues());
	}
}