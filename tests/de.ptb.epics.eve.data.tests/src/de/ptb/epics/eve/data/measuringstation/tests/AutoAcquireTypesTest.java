package de.ptb.epics.eve.data.measuringstation.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.ptb.epics.eve.data.AutoAcquireTypes;

/**
 * @author Marcus Michalsky
 * @since 1.37
 */
public class AutoAcquireTypesTest {
	@Test
	public void testGetEnum() {
		assertEquals("passing null should return NO", 
				AutoAcquireTypes.NO, AutoAcquireTypes.getEnum(null));
		assertEquals("passing snapshot should return SNAPSHOT", 
				AutoAcquireTypes.SNAPSHOT, AutoAcquireTypes.getEnum("snapshot"));
		assertEquals("passing measurement should return MEASUREMENT", 
				AutoAcquireTypes.MEASUREMENT, AutoAcquireTypes.getEnum("measurement"));
		assertEquals("passing no should return NO", 
				AutoAcquireTypes.NO, AutoAcquireTypes.getEnum("no"));
	}
}
