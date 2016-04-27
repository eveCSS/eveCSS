package de.ptb.epics.eve.util.datetime.tests;

import de.ptb.epics.eve.util.datetime.*;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class DateTimeTest {
	@Test
	public void testHumanReadable() {
		assertEquals("9s", DateTime.humanReadable(9));
		assertEquals("59s", DateTime.humanReadable(59));
		assertEquals("1min 00s", DateTime.humanReadable(60));
		assertEquals("10min 00s", DateTime.humanReadable(600));
		assertEquals("1h 00min 00s", DateTime.humanReadable(3600));
		assertEquals("10h 00min 00s", DateTime.humanReadable(36000));
		assertEquals("1d 00h 00min 00s", DateTime.humanReadable(24 * 3600));
	}
}