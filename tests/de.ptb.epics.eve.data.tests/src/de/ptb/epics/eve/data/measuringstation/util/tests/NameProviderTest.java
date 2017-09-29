package de.ptb.epics.eve.data.measuringstation.util.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.util.NameProvider;

public class NameProviderTest {

	@Test
	public void testIdentityMode() {
		NameProvider nameProvider = new NameProvider(null, null);
		
		String testString = "testId";
		assertEquals(testString, nameProvider.translateMotorAxisId(testString));
		assertEquals(testString, nameProvider.translateDetectorChannelId(testString));
		assertEquals(testString, nameProvider.translatePrePostScanDeviceId(testString));
	}
}