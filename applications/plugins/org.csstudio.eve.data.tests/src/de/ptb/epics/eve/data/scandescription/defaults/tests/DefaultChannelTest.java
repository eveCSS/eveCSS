package de.ptb.epics.eve.data.scandescription.defaults.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.defaults.DefaultChannel;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultChannelTest {

	private DefaultChannel channel;
	
	/**
	 * Tests whether a new instance of DefaultChannel has not set its average 
	 * count.
	 */
	@Test(expected=NullPointerException.class)
	public void testAverageCountNotSet() {
		this.channel.getAverageCount();
	}
	
	/**
	 * Tests whether a new instance of DefaultChannel has not set its max 
	 * deviation.
	 */
	@Test(expected=NullPointerException.class)
	public void testMaxDeviationNotSet() {
		this.channel.getMaxDeviation();
	}
	
	/**
	 * Tests whether a new instance of DefaultChannel has not set its minimum.
	 */
	@Test(expected=NullPointerException.class)
	public void testMinimumNotSet() {
		this.channel.getMinimum();
	}
	
	/**
	 * Tests whether a new instance of DefaultChannel has not set its max 
	 * attempts.
	 */
	@Test(expected=NullPointerException.class)
	public void testMaxAttemptsNotSet() {
		this.channel.getMaxAttempts();
	}
	
	/**
	 * Tests whether a new instance of DefaultChannel has not set its normalize 
	 * channel.
	 */
	@Test(expected=NullPointerException.class)
	public void testNormalizeChannelNotSet() {
		this.channel.getNormalizeId();
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
		this.channel = new DefaultChannel();
	}

	/**
	 * Test wide tear down.
	 */
	@After
	public void tearDown() {
	}
}