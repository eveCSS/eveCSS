package de.ptb.epics.eve.util.data.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import de.ptb.epics.eve.util.data.Version;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class VersionTest {
	
	@Test
	public void testCompareTo() {
		Version previous = new Version(1, 1);
		Version next = new Version(2, 2);
		assertTrue(previous.compareTo(next) < 0);
		assertTrue(next.compareTo(previous) > 0);
		
		previous = new Version(2, 2);
		next = new Version(1, 1);
		assertTrue(previous.compareTo(next) > 0);
		assertTrue(next.compareTo(previous) < 0);
		
		previous = new Version(1, 1);
		next = new Version(1, 2);
		assertTrue(previous.compareTo(next) < 0);
		assertTrue(next.compareTo(previous) > 0);
		
		previous = new Version(1, 2);
		next = new Version(1, 1);
		assertTrue(previous.compareTo(next) > 0);
		assertTrue(next.compareTo(previous) < 0);
		
		previous = new Version(3, 3);
		next = new Version(3, 3);
		assertTrue(previous.compareTo(next) == 0);
	}
}