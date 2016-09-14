package de.ptb.epics.eve.data.scandescription.defaults.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsChannel;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultChannelTest {

	private DefaultsChannel channel;
	
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