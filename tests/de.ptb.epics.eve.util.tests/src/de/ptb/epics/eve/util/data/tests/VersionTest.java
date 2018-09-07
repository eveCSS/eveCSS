package de.ptb.epics.eve.util.data.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import de.ptb.epics.eve.util.data.Version;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class VersionTest {
	
	/**
	 * @since 1.30
	 */
	@Test
	public void testHashCode() {
		Version version = new Version(1,0);
		Version sameVersion = new Version(1,0);
		// if two objects are equal, hashCodes must be equal
		assertEquals(version.hashCode(), sameVersion.hashCode());
		// repeated invokes must be equal
		assertEquals(version.hashCode(), version.hashCode());
	}
	
	/**
	 * @since 1.30
	 */
	@Test
	public void testEquals() {
		Version version = new Version(1,0);
		Version sameVersion = new Version(1,0);
		Version sameVersion2 = new Version(1,0);
		Version otherVersion = new Version(2,0);
		// reflexive
		assertTrue(version.equals(version));
		// symmetric
		assertEquals(version.equals(sameVersion), sameVersion.equals(version));
		assertEquals(version.equals(otherVersion), otherVersion.equals(version));
		// transitive
		assertTrue(version.equals(sameVersion));
		assertTrue(sameVersion.equals(sameVersion2));
		assertTrue(version.equals(sameVersion2));
		// consistent
		assertTrue(version.equals(sameVersion));
		assertTrue(version.equals(sameVersion));
		assertTrue(version.equals(sameVersion));
		assertTrue(version.equals(sameVersion));
		assertTrue(version.equals(sameVersion));
		// null
		assertFalse(version.equals(null));
	}
	
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