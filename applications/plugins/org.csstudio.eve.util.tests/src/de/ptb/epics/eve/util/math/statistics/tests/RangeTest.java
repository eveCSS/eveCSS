package de.ptb.epics.eve.util.math.statistics.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import de.ptb.epics.eve.util.math.statistics.Range;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class RangeTest {
	
	/**
	 * Checks whether
	 * {@link de.ptb.epics.eve.util.math.statistics.Range#isInRange(double, double, double)}
	 * works as expected.
	 */
	@Test
	public void testIsInRange() {
		assertTrue(Range.isInRange(1, 1, 5));
		assertTrue(Range.isInRange(2, 1, 5));
		assertTrue(Range.isInRange(3, 1, 5));
		assertTrue(Range.isInRange(4, 1, 5));
		assertTrue(Range.isInRange(5, 1, 5));
		assertFalse(Range.isInRange(-1, 1, 5));
		assertFalse(Range.isInRange(0, 1, 5));
		assertFalse(Range.isInRange(6, 1, 5));
	}
}