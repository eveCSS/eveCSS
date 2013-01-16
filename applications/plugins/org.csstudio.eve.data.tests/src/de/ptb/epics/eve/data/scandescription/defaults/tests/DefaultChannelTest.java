package de.ptb.epics.eve.data.scandescription.defaults.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.defaults.DefaultsChannel;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultChannelTest {

	private DefaultsChannel channel;
	
	/**
	 * Tests whether a new instance of DefaultChannel has not set its average 
	 * count.
	 */
	public void testAverageCountNotSet() {
		assertNull(this.channel.getAverageCount());
	}
	
	/**
	 * Tests whether a new instance of DefaultChannel has not set its max 
	 * deviation.
	 */
	public void testMaxDeviationNotSet() {
		assertNull(this.channel.getMaxDeviation());
	}
	
	/**
	 * Tests whether a new instance of DefaultChannel has not set its minimum.
	 */
	public void testMinimumNotSet() {
		assertNull(this.channel.getMinimum());
	}
	
	/**
	 * Tests whether a new instance of DefaultChannel has not set its max 
	 * attempts.
	 */
	public void testMaxAttemptsNotSet() {
		assertNull(this.channel.getMaxAttempts());
	}
	
	/**
	 * Tests whether a new instance of DefaultChannel has not set its normalize 
	 * channel.
	 */
	public void testNormalizeChannelNotSet() {
		assertNull(this.channel.getNormalizeId());
	}
	
	/**
	 * Tests whether a new instance of DefaultChannel has initialized its 
	 * deferred trigger with false.
	 */
	@Test
	public void testDeferredTriggerSetToFalse() {
		assertFalse(this.channel.isDeferred());
	}
	
	// **********************************************************************
	// **************************** Setup ***********************************
	// **********************************************************************

	/**
	 * Class wide setup.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
	}

	/**
	 * Class wide tear down.
	 */
	@AfterClass
	public static void tearDownAfterClass() {
	}

	/**
	 * Test wide set up.
	 */
	@Before
	public void setUp() {
		this.channel = new DefaultsChannel();
	}

	/**
	 * Test wide tear down.
	 */
	@After
	public void tearDown() {
	}
}