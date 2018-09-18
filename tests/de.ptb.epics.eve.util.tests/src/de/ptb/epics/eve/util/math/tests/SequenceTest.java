package de.ptb.epics.eve.util.math.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.ptb.epics.eve.util.math.Sequence;

public class SequenceTest {
	@Test
	public void testStepcountInt() {
		assertEquals(4, Sequence.getStepcount(1, 5, 1), 0.0001);
		assertEquals(4, Sequence.getStepcount(5, 1, 1), 0.0001);
		assertEquals(5, Sequence.getStepcount(1, 10, 2), 0.0001);
		assertEquals(0.4, Sequence.getStepcount(0, 20, 50), 0.0001);
		assertEquals(10, Sequence.getStepcount(-5, 5, 1), 0.0001);
		assertEquals(10, Sequence.getStepcount(5, -5, 1), 0.0001);
	}
	
	@Test
	public void testStepcountDouble() {
		assertEquals(175.0d, Sequence.getStepcount(130.0, 60.0, -0.4), 0.0001);
		assertEquals(175.0d, Sequence.getStepcount(130.0, 60.0, 0.4), 0.0001);
		assertEquals(175.0d, Sequence.getStepcount(60.0, 130.0, -0.4), 0.0001);
		assertEquals(175.0d, Sequence.getStepcount(60.0, 130.0, 0.4), 0.0001);
	}
}
