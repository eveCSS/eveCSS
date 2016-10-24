package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.channelmode.IntervalMode;
import de.ptb.epics.eve.data.scandescription.channelmode.StandardMode;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DetectorChannelMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChannelMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ScanModuleMother;

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
	
	private boolean triggerInterval;
	private boolean stoppedBy;
	
	@Test(expected = IllegalStateException.class)
	public void testStandardModeSetTriggerInterval() {
		this.channel.setTriggerInterval(2.0);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testStandardModeSetStoppedBy() {
		this.channel.setStoppedBy(null);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testIntervalModeSetAverageCount() {
		this.channel.setChannelMode(ChannelModes.INTERVAL);
		this.channel.setAverageCount(1);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testIntervalModeSetMaxAttempts() {
		this.channel.setChannelMode(ChannelModes.INTERVAL);
		this.channel.setMaxAttempts(1);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testIntervalModeSetMaxDeviation() {
		this.channel.setChannelMode(ChannelModes.INTERVAL);
		this.channel.setMaxDeviation(2.0);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testIntervalModeSetMinimum() {
		this.channel.setChannelMode(ChannelModes.INTERVAL);
		this.channel.setMinimum(2.0);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testIntervalModeSetDeferred() {
		this.channel.setChannelMode(ChannelModes.INTERVAL);
		this.channel.setDeferred(true);
	}
	
	@Test
	public void testNormalize() {
		ScanModule scanModule = ScanModuleMother.createNewScanModule();
		Channel smChannel = ChannelMother.createNewChannel(scanModule);
		scanModule.add(smChannel);
		Channel normalizeChannel = ChannelMother.createNewChannel(scanModule);
		scanModule.add(normalizeChannel);
		assertNull(smChannel.getNormalizeChannel());
		
		smChannel.setNormalizeChannel(normalizeChannel.getDetectorChannel());
		assertEquals(normalizeChannel.getDetectorChannel(), smChannel.getNormalizeChannel());
		
		scanModule.remove(normalizeChannel);
		assertNull(smChannel.getNormalizeChannel());
	}

	@Test
	public void testAverageCount() {
		assertEquals(StandardMode.AVERAGE_COUNT_DEFAULT_VALUE, this.channel.getAverageCount());
		this.channel.setAverageCount(5);
		assertEquals(5, this.channel.getAverageCount());
	}
	
	@Test
	public void testMaxAttempts() {
		assertEquals(null, this.channel.getMaxAttempts());
		this.channel.setMaxAttempts(1);
		assertEquals(1, this.channel.getMaxAttempts().intValue());
		this.channel.setMaxAttempts(null);
		assertEquals(null, this.channel.getMaxAttempts());
	}
	
	@Test
	public void testMaxDeviation() {
		assertEquals(null, this.channel.getMaxDeviation());
		this.channel.setMaxDeviation(2.0);
		assertEquals(2.0, this.channel.getMaxDeviation(), 0);
		this.channel.setMaxDeviation(null);
		assertEquals(null, this.channel.getMaxDeviation());
	}
	
	@Test
	public void testMinimum() {
		assertEquals(null, this.channel.getMinimum());
		this.channel.setMinimum(2.0);
		assertEquals(2.0, this.channel.getMinimum(), 0);
		this.channel.setMinimum(null);
		assertEquals(null, this.channel.getMinimum());
	}
	
	@Test
	public void testDeferred() {
		assertEquals(false, this.channel.isDeferred());
		this.channel.setDeferred(true);
		assertEquals(true, this.channel.isDeferred());
	}
	
	@Test
	public void testTriggerInterval() {
		this.channel.setChannelMode(ChannelModes.INTERVAL);
		assertEquals(IntervalMode.TRIGGER_INTERVAL_DEFAULT_VALUE, this.channel.getTriggerInterval(), 0);
		this.channel.setTriggerInterval(2.0);
		assertEquals(2.0, this.channel.getTriggerInterval(), 0);
	}
	
	@Test
	public void testStoppedBy() {
		this.channel.setChannelMode(ChannelModes.INTERVAL);
		assertEquals(null, this.channel.getStoppedBy());
		DetectorChannel detectorChannel = DetectorChannelMother.createNewDetectorChannel();
		this.channel.setStoppedBy(detectorChannel);
		assertEquals(detectorChannel, this.channel.getStoppedBy());
	}
	
	@Test
	public void testResetStandard() {
		this.channel.setAverageCount(2);
		this.channel.setMaxAttempts(2);
		this.channel.setMaxDeviation(2.0);
		this.channel.setMinimum(2.0);
		this.channel.setDeferred(true);
		this.channel.setNormalizeChannel(DetectorChannelMother.createNewDetectorChannel());
		// TODO set RedoEvent (ObjectMother ?)
		this.channel.reset();
		assertEquals(1, this.channel.getAverageCount());
		assertEquals(null, this.channel.getMaxAttempts());
		assertEquals(null, this.channel.getMaxDeviation());
		assertEquals(null, this.channel.getMinimum());
		assertEquals(false, this.channel.isDeferred());
		assertEquals(null, this.channel.getNormalizeChannel());
		// TODO check redo events
	}
	
	@Test
	public void testResetInterval() {
		this.channel.setChannelMode(ChannelModes.INTERVAL);
		this.channel.setTriggerInterval(2.0);
		this.channel.setStoppedBy(DetectorChannelMother.createNewDetectorChannel());
		this.channel.setNormalizeChannel(DetectorChannelMother.createNewDetectorChannel());
		this.channel.reset();
		assertEquals(IntervalMode.TRIGGER_INTERVAL_DEFAULT_VALUE, this.channel.getTriggerInterval(), 0);
		assertEquals(null, this.channel.getStoppedBy());
		assertEquals(null, this.channel.getNormalizeChannel());
	}
	
	/**
	 * @since 1.3
	 */
	@Test
	public void testStandardPropertyChangeSupport() {
		// initialize indicators
		this.averageCount = false;
		this.maxAttempts = false;
		this.maxDeviation = false;
		this.minimum = false;
		this.normalizeChannel = false;
		
		// listen to properties
		this.channel.addPropertyChangeListener(StandardMode.AVERAGE_COUNT_PROP, this);
		this.channel.addPropertyChangeListener(StandardMode.MAX_ATTEMPTS_PROP, this);
		this.channel.addPropertyChangeListener(StandardMode.MAX_DEVIATION_PROP, this);
		this.channel.addPropertyChangeListener(StandardMode.MINIMUM_PROP, this);
		this.channel.addPropertyChangeListener(Channel.NORMALIZE_CHANNEL_PROP, this);
		
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
	 * @since 1.27
	 */
	@Test
	public void testIntervalPropertyChangeSupport() {
		this.triggerInterval = false;
		this.stoppedBy = false;
		
		this.channel.setChannelMode(ChannelModes.INTERVAL);
		
		this.channel.addPropertyChangeListener(IntervalMode.STOPPED_BY_PROP, this);
		this.channel.addPropertyChangeListener(IntervalMode.TRIGGER_INTERVAL_PROP, this);
		
		this.channel.setTriggerInterval(2.0);
		this.channel.setStoppedBy(DetectorChannelMother.createNewDetectorChannel());
		
		assertTrue(this.triggerInterval);
		assertTrue(this.stoppedBy);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(StandardMode.AVERAGE_COUNT_PROP)) {
			this.averageCount = true;
		} else if(evt.getPropertyName().equals(StandardMode.MAX_ATTEMPTS_PROP)) {
			this.maxAttempts = true;
		} else if(evt.getPropertyName().equals(StandardMode.MAX_DEVIATION_PROP)) {
			this.maxDeviation = true;
		} else if(evt.getPropertyName().equals(StandardMode.MINIMUM_PROP)) {
			this.minimum = true;
		} else if(evt.getPropertyName().equals(Channel.NORMALIZE_CHANNEL_PROP)) {
			this.normalizeChannel = true;
		} else if(evt.getPropertyName().equals(IntervalMode.TRIGGER_INTERVAL_PROP)) {
			this.triggerInterval = true;
		} else if(evt.getPropertyName().equals(IntervalMode.STOPPED_BY_PROP)) {
			this.stoppedBy = true;
		}
	}
	
	@Test
	public void testStandardChannelNewInstance() {
		Channel channel = ChannelMother.createNewChannel();
		channel.setChannelMode(ChannelModes.STANDARD);
		channel.setAverageCount(2);
		channel.setMaxAttempts(1);
		channel.setMaxDeviation(4.0);
		channel.setMinimum(3.0);
		channel.setDeferred(true);
		
		Channel newChannel = Channel.newInstance(channel, channel.getScanModule());
		assertEquals(2, newChannel.getAverageCount());
		assertEquals(Integer.valueOf(1), newChannel.getMaxAttempts());
		assertEquals(4.0, newChannel.getMaxDeviation(), 0);
		assertEquals(3.0, newChannel.getMinimum(), 0);
		assertEquals(true, newChannel.isDeferred());
	}
	
	@Test
	public void testIntervalChannelNewInstance() {
		Channel channel = ChannelMother.createNewChannel();
		channel.setChannelMode(ChannelModes.INTERVAL);
		channel.setTriggerInterval(2.0);
		DetectorChannel detectorChannel = DetectorChannelMother.createNewDetectorChannel();
		channel.setStoppedBy(detectorChannel);
		
		Channel newChannel = Channel.newInstance(channel, channel.getScanModule());
		assertEquals(2.0, newChannel.getTriggerInterval(), 0);
		assertEquals(detectorChannel, newChannel.getStoppedBy());
	}

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
		this.channel = ChannelMother.createNewChannel();
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