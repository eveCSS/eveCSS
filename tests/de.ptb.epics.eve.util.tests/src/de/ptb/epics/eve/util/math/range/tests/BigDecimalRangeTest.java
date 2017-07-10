package de.ptb.epics.eve.util.math.range.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.ptb.epics.eve.util.io.StringUtil;
import de.ptb.epics.eve.util.math.range.BigDecimalRange;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class BigDecimalRangeTest {
	@Test
	public void testGetValues() {
		assertEquals("1.0, 2.0, 3.0", StringUtil.buildCommaSeparatedString(
				new BigDecimalRange("1.0:3").getValues()));
		assertEquals("3.0, 2.0, 1.0", StringUtil.buildCommaSeparatedString(
				new BigDecimalRange("3.0:1").getValues()));
		assertEquals("1, 1.4, 1.8, 2.2, 2.6, 3.0, 3.2", StringUtil.
				buildCommaSeparatedString(new BigDecimalRange("1:0.4:3.2").getValues()));
	}
}