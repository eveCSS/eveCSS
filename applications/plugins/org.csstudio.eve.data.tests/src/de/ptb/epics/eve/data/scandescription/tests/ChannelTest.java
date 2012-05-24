package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * @author Marcus Michalsky
 * @since 1.3
 */
public class ChannelTest implements PropertyChangeListener {

	private static Logger logger = Logger.getLogger(ChannelTest.class.getName());
	
	private Channel channel;
	
	// indicators for PropertyChangeSupportTest
	private boolean averageCount;
	private boolean confirmTrigger;
	private boolean maxAttempts;
	private boolean maxDeviation;
	private boolean minimum;
	private boolean repeatOnRedo;
	private boolean normalizeChannel;
	
	/**
	 * 
	 */
	@Test
	public void testPropertyChangeSupport() {
		// initialize indicators
		this.averageCount = false;
		this.confirmTrigger = false;
		this.maxAttempts = false;
		this.maxDeviation = false;
		this.minimum = false;
		this.repeatOnRedo = false;
		this.normalizeChannel = false;
		
		// listen to properties
		this.channel.addPropertyChangeListener("averageCount", this);
		this.channel.addPropertyChangeListener("confirmTrigger", this);
		this.channel.addPropertyChangeListener("maxAttempts", this);
		this.channel.addPropertyChangeListener("maxDeviation", this);
		this.channel.addPropertyChangeListener("minimum", this);
		this.channel.addPropertyChangeListener("repeatOnRedo", this);
		this.channel.addPropertyChangeListener("normalizeChannel", this);
		
		// manipulate properties
		this.channel.setAverageCount(0);
		this.channel.setConfirmTrigger(true);
		this.channel.setMaxAttempts(1);
		this.channel.setMaxDeviation(1.0);
		this.channel.setMinimum(1.0);
		this.channel.setRepeatOnRedo(true);
		this.channel.setNormalizeChannel(null);
		
		// check whether the manipulation was notified
		assertTrue(this.averageCount);
		assertTrue(this.confirmTrigger);
		assertTrue(this.maxAttempts);
		assertTrue(this.maxDeviation);
		assertTrue(this.minimum);
		assertTrue(this.repeatOnRedo);
		assertTrue(this.normalizeChannel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("averageCount")) {
			this.averageCount = true;
		} else if(evt.getPropertyName().equals("confirmTrigger")) {
			this.confirmTrigger = true;
		} else if(evt.getPropertyName().equals("maxAttempts")) {
			this.maxAttempts = true;
		} else if(evt.getPropertyName().equals("maxDeviation")) {
			this.maxDeviation = true;
		} else if(evt.getPropertyName().equals("minimum")) {
			this.minimum = true;
		} else if(evt.getPropertyName().equals("repeatOnRedo")) {
			this.repeatOnRedo = true;
		} else if(evt.getPropertyName().equals("normalizeChannel")) {
			this.normalizeChannel = true;
		}
	}
	
	// ***********************************************************************

	/**
	 * Initializes logging (Class wide setup method of the test).
	 */
	@BeforeClass
	public static void runBeforeClass() {
		Configurator.configureLogging();
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