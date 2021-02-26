package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChainMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChannelMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ControlEventMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ScanModuleMother;

/**
 * ScanModule related Unit Testing.
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class ScanModuleTest implements IModelUpdateListener {
	private ScanModule scanModule;
	
	private boolean modelUpdate;
	
	/** @since 1.31 */
	@Test
	public void testResetAttributes() {
		this.scanModule.setName("mockName");
		this.scanModule.setValueCount(5);
		this.scanModule.setSettleTime(2.0);
		this.scanModule.setTriggerDelay(2.0);
		this.scanModule.resetAttributes();
		assert(this.scanModule.getName().startsWith("SM"));
		assertEquals(ScanModule.DEFAULT_VALUE_VALUE_COUNT, 
				this.scanModule.getValueCount());
		assertEquals(ScanModule.DEFAULT_VALUE_SETTLE_TIME, 
				this.scanModule.getSettleTime(), 0.01);
		assertEquals(ScanModule.DEFALUT_VALUE_TRIGGER_DELAY, 
				this.scanModule.getTriggerDelay(), 0.01);
	}
	
	/** @since 1.31 */
	@Test
	public void testRemoveAllAxes() {
		this.scanModule.add(this.mockAxis());
		assert(!this.scanModule.getAxesList().isEmpty());
		this.scanModule.removeAllAxes();
		assert(this.scanModule.getAxesList().isEmpty());
	}
	
	/** @since 1.31 */
	@Test
	public void testRemoveAllChannels() {
		this.scanModule.add(ChannelMother.createNewChannel(scanModule));
		assert(!this.scanModule.getChannelList().isEmpty());
		this.scanModule.removeAllChannels();
		assert(this.scanModule.getChannelList().isEmpty());
	}
	
	@Test
	@Ignore
	public void testRemoveAllPrescans() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testRemoveAllPostscans() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testRemoveAllPositionings() {
		// TODO
	}
	
	@Test
	@Ignore
	public void testRemoveAllPlotWindows() {
		// TODO
	}
	
	/** @since 1.31 */
	@Test
	public void testRemoveBreakEvents() {
		Chain chain = ChainMother.createNewChain();
		chain.add(this.scanModule);
		Channel channel = ChannelMother.createNewChannel(scanModule);
		scanModule.add(channel);
		this.scanModule.addBreakEvent(
				ControlEventMother.createNewDetectorReadyEvent(channel));
		assert(!this.scanModule.getBreakEvents().isEmpty());
		this.scanModule.removeBreakEvents();
		assert(this.scanModule.getBreakEvents().isEmpty());
	}
	
	/** @since 1.31 */
	@Test
	public void testRemoveRedoEvents() {
		Chain chain = ChainMother.createNewChain();
		chain.add(this.scanModule);
		Channel channel = ChannelMother.createNewChannel(scanModule);
		scanModule.add(channel);
		this.scanModule.addRedoEvent(
				ControlEventMother.createNewDetectorReadyEvent(channel));
		assert(!this.scanModule.getRedoEvents().isEmpty());
		this.scanModule.removeRedoEvents();
		assert(this.scanModule.getRedoEvents().isEmpty());
	}
	
	/** @since 1.31 */
	@Test
	public void testRemoveTriggerEvents() {
		Chain chain = ChainMother.createNewChain();
		chain.add(this.scanModule);
		Channel channel = ChannelMother.createNewChannel(scanModule);
		scanModule.add(channel);
		this.scanModule.addTriggerEvent(
				ControlEventMother.createNewDetectorReadyEvent(channel));
		assert(!this.scanModule.getTriggerEvents().isEmpty());
		this.scanModule.removeTriggerEvents();
		assert(this.scanModule.getTriggerEvents().isEmpty());
	}
	
	@Test
	public void testIModelUpdateListenerNoOfMeasurements() {
		this.modelUpdate = false;
		this.scanModule.setValueCount(5);
		assertTrue(this.modelUpdate);
	}
	
	@Test
	public void testIModelUpdateListenerTriggerDelay() {
		this.modelUpdate = false;
		this.scanModule.setTriggerDelay(2.0);
		assertTrue(this.modelUpdate);
	}
	
	@Test
	public void testIModelUpdateListenerSettleTime() {
		this.modelUpdate = false;
		this.scanModule.setSettleTime(2.0);
		assertTrue(this.modelUpdate);
	}
	
	@Test
	public void testIModelUpdateListenerManualTriggerAxis() {
		this.modelUpdate = false;
		this.scanModule.setTriggerConfirmAxis(true);
		assertTrue(this.modelUpdate);
	}
	
	@Test
	public void testIModelUpdateListenerManualTriggerChannel() {
		this.modelUpdate = false;
		this.scanModule.setTriggerConfirmChannel(true);
		assertTrue(this.modelUpdate);
	}
	
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.modelUpdate = true;
	}
	
	@Test
	public void testIsUsedAsNormalizeChannel() {
		this.scanModule = ScanModuleMother.createNewScanModule();
		Channel channel1 = ChannelMother.createNewChannel(scanModule);
		scanModule.add(channel1);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		Channel channel2 = ChannelMother.createNewChannel(scanModule);
		scanModule.add(channel2);
		
		assertFalse(scanModule.isUsedAsNormalizeChannel(channel1));
		assertFalse(scanModule.isUsedAsNormalizeChannel(channel2));
		
		channel1.setNormalizeChannel(channel2.getDetectorChannel());
		assertTrue(scanModule.isUsedAsNormalizeChannel(channel2));
		
		channel1.setNormalizeChannel(null);
		assertFalse(scanModule.isUsedAsNormalizeChannel(channel2));
	}

	@Test
	public void testIsUsedAsStoppedByChannel() {
		this.scanModule = ScanModuleMother.createNewScanModule();
		Channel channel1 = ChannelMother.createNewChannel(scanModule);
		scanModule.add(channel1);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		Channel channel2 = ChannelMother.createNewChannel(scanModule);
		scanModule.add(channel2);
		
		assertFalse(scanModule.isUsedAsStoppedByChannel(channel1));
		assertFalse(scanModule.isUsedAsStoppedByChannel(channel2));
		
		channel1.setChannelMode(ChannelModes.INTERVAL);
		channel1.setStoppedBy(channel2.getDetectorChannel());
		assertTrue(scanModule.isUsedAsStoppedByChannel(channel2));
		
		channel1.setStoppedBy(null);
		assertFalse(scanModule.isUsedAsStoppedByChannel(channel2));
	}
	
	/*
	 * Creates a Mock Axis
	 */
	private Axis mockAxis() {
		Axis axis = new Axis(this.scanModule);
		MotorAxis mockAxis = mock(MotorAxis.class);
		when(mockAxis.getType()).thenReturn(DataTypes.DOUBLE);
		when(mockAxis.getDefaultValue()).thenReturn("0");
		when(mockAxis.getName()).thenReturn("MockAxis");
		Function mockFunction = mock(Function.class);
		when(mockFunction.isDiscrete()).thenReturn(false);
		when(mockAxis.getGoto()).thenReturn(mockFunction);
		axis.setMotorAxis(mockAxis);
		return axis;
	}
	
	@Before
	public void beforeTest() {
		this.scanModule = new ScanModule(1);
		this.scanModule.addModelUpdateListener(this);
	}
	
	@After
	public void afterTest() {
		this.scanModule = null;
	}
}