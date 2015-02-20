package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.3
 */
public class ChannelTest implements PropertyChangeListener {
	private Channel channel;
	
	// indicators for PropertyChangeSupportTest
	private boolean averageCount;
	private boolean maxAttempts;
	private boolean maxDeviation;
	private boolean minimum;
	private boolean normalizeChannel;
	
	/**
	 * 
	 */
	@Test
	public void testPropertyChangeSupport() {
		// initialize indicators
		this.averageCount = false;
		this.maxAttempts = false;
		this.maxDeviation = false;
		this.minimum = false;
		this.normalizeChannel = false;
		
		// listen to properties
		this.channel.addPropertyChangeListener("averageCount", this);
		this.channel.addPropertyChangeListener("confirmTrigger", this);
		this.channel.addPropertyChangeListener("maxAttempts", this);
		this.channel.addPropertyChangeListener("maxDeviation", this);
		this.channel.addPropertyChangeListener("minimum", this);
		this.channel.addPropertyChangeListener("normalizeChannel", this);
		
		// manipulate properties
		this.channel.setAverageCount(0);
		this.channel.setMaxAttempts(1);
		this.channel.setMaxDeviation(1.0);
		this.channel.setMinimum(1.0);
		this.channel.setNormalizeChannel(null);
		
		// check whether the manipulation was notified
		assertTrue(this.averageCount);
		assertTrue(this.maxAttempts);
		assertTrue(this.maxDeviation);
		assertTrue(this.minimum);
		assertTrue(this.normalizeChannel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("averageCount")) {
			this.averageCount = true;
		} else if(evt.getPropertyName().equals("maxAttempts")) {
			this.maxAttempts = true;
		} else if(evt.getPropertyName().equals("maxDeviation")) {
			this.maxDeviation = true;
		} else if(evt.getPropertyName().equals("minimum")) {
			this.minimum = true;
		} else if(evt.getPropertyName().equals("normalizeChannel")) {
			this.normalizeChannel = true;
		}
	}
	
	// ***********************************************************************

	/**
	 * Class wide setup method of the test
	 */
	@BeforeClass
	public static void runBeforeClass() {
	}

	/**
	 * test wide set up method
	 */
	@Before
	public void beforeEveryTest() {
		ScanModule scanModule = new ScanModule(1);
		this.channel = new Channel(scanModule);
	}

	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest() {
		this.channel = null;
	}

	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass() {
	}
}