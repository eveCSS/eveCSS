package de.ptb.epics.eve.util.math.range.tests;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.ptb.epics.eve.util.math.range.BigDecimalRange;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class BigDecimalRangeTest {
	@Test
	public void testGetValues() {
		// case j:k (i=1), j < k
		List<BigDecimal> list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("1.0"));
		list.add(new BigDecimal("2.0"));
		list.add(new BigDecimal("3.0"));
		assertEquals(
				list,
				new BigDecimalRange("1.0:3").getValues());
		// case j:k (i=1), j > k
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("3.0"));
		list.add(new BigDecimal("2.0"));
		list.add(new BigDecimal("1.0"));
		assertEquals(
				list,
				new BigDecimalRange("3.0:1").getValues());
		// case j:k (i=1), j = k
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("1.0"));
		assertEquals(
				list,
				new BigDecimalRange("1.0:1").getValues());
		
		// case j:i:k, j < k ^ (j + i) < k
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("1"));
		list.add(new BigDecimal("1.4"));
		list.add(new BigDecimal("1.8"));
		list.add(new BigDecimal("2.2"));
		list.add(new BigDecimal("2.6"));
		list.add(new BigDecimal("3.0"));
		list.add(new BigDecimal("3.2"));
		assertEquals(
				list,
				new BigDecimalRange("1:0.4:3.2").getValues());
		// case j:i:k, j < k ^ (j + i) > k
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("1"));
		list.add(new BigDecimal("3.2"));
		assertEquals(
				list,
				new BigDecimalRange("1:4:3.2").getValues());
		// case j:i:k, j > k ^ (j - i) > k
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("3.2"));
		list.add(new BigDecimal("2.8"));
		list.add(new BigDecimal("2.4"));
		list.add(new BigDecimal("2.0"));
		list.add(new BigDecimal("1.6"));
		list.add(new BigDecimal("1.2"));
		list.add(new BigDecimal("1"));
		assertEquals(
				list,
				new BigDecimalRange("3.2:0.4:1").getValues());
		// case j:i:k, j > k ^ (j - i) < k
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("3.2"));
		list.add(new BigDecimal("1"));
		assertEquals(
				list,
				new BigDecimalRange("3.2:4:1").getValues());
		// case j:i:k, j = k
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("1"));
		assertEquals(
				list,
				new BigDecimalRange("1:4:1").getValues());
		
		// case j:i:k, j < k ^ (j + i) < j
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("-1"));
		list.add(new BigDecimal("-0.75"));
		list.add(new BigDecimal("-0.50"));
		assertEquals(
				list,
				new BigDecimalRange("-1:-0.25:-0.5").getValues());
		// case j:i:k, j > k ^ (j - i) > j
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("1"));
		list.add(new BigDecimal("0.75"));
		list.add(new BigDecimal("0.50"));
		assertEquals(
				list,
				new BigDecimalRange("1:-0.25:0.5").getValues());
		
		// case j:k/N, j < k ^ (k - j) > N
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("1"));
		list.add(new BigDecimal("2.8"));
		list.add(new BigDecimal("4.6"));
		list.add(new BigDecimal("6.4"));
		list.add(new BigDecimal("8.2"));
		list.add(new BigDecimal("10.0"));
		assertEquals(
				list,
				new BigDecimalRange("1:10.0/5").getValues());
		// case j:k/N, j < k ^ (k - j) < N
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("1"));
		list.add(new BigDecimal("1.8"));
		list.add(new BigDecimal("2.6"));
		list.add(new BigDecimal("3.4"));
		list.add(new BigDecimal("4.2"));
		list.add(new BigDecimal("5.0"));
		assertEquals(
				list,
				new BigDecimalRange("1:5.0/5").getValues());
		// case j:k/N, j > k ^ (j - k) > N
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("10.0"));
		list.add(new BigDecimal("8.2"));
		list.add(new BigDecimal("6.4"));
		list.add(new BigDecimal("4.6"));
		list.add(new BigDecimal("2.8"));
		list.add(new BigDecimal("1.0"));
		assertEquals(
				list,
				new BigDecimalRange("10.0:1/5").getValues());
		// same case, caused ArithmeticException before Rounding was added.
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("5.0"));
		list.add(new BigDecimal("3.7"));
		list.add(new BigDecimal("2.4"));
		list.add(new BigDecimal("1.1"));
		list.add(new BigDecimal("1"));
		assertEquals(
				list,
				new BigDecimalRange("5.0:1/3").getValues());
		// case j:k/N, j > k ^ (j - k) < N
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("5.0"));
		list.add(new BigDecimal("4.2"));
		list.add(new BigDecimal("3.4"));
		list.add(new BigDecimal("2.6"));
		list.add(new BigDecimal("1.8"));
		list.add(new BigDecimal("1.0"));
		assertEquals(
				list,
				new BigDecimalRange("5.0:1/5").getValues());
		// case j:k/N, j = k
		list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("1"));
		assertEquals(
				list,
				new BigDecimalRange("1:1.0/5").getValues());
	}
	
	@Test
	public void testGetValuesE() {
		List<BigDecimal> list = new ArrayList<BigDecimal>();
		list.add(new BigDecimal("0"));
		list.add(new BigDecimal("1E-5"));
		list.add(new BigDecimal("2E-5"));
		list.add(new BigDecimal("3E-5"));
		list.add(new BigDecimal("4E-5"));
		list.add(new BigDecimal("5E-5"));
		list.add(new BigDecimal("6E-5"));
		list.add(new BigDecimal("7E-5"));
		list.add(new BigDecimal("8E-5"));
		list.add(new BigDecimal("9E-5"));
		list.add(new BigDecimal("0.00010"));
		assertEquals(
				list,
				new BigDecimalRange("0:1E-5:1E-4").getValues());
		
		list.clear();
		list.add(new BigDecimal("0"));
		list.add(new BigDecimal("1E-10"));
		list.add(new BigDecimal("2E-10"));
		list.add(new BigDecimal("3E-10"));
		list.add(new BigDecimal("4E-10"));
		list.add(new BigDecimal("5E-10"));
		list.add(new BigDecimal("6E-10"));
		list.add(new BigDecimal("7E-10"));
		list.add(new BigDecimal("8E-10"));
		list.add(new BigDecimal("9E-10"));
		list.add(new BigDecimal("1.0E-9"));
		assertEquals(
				list,
				new BigDecimalRange("0:1E-10:1E-9").getValues());
		
	}
}