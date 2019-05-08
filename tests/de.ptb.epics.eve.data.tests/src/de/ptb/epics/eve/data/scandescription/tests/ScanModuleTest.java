package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyMode;
import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChainMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChannelMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ControlEventMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.PauseEventMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ScanModuleMother;

/**
 * ScanModule related Unit Testing.
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class ScanModuleTest implements IModelUpdateListener, PropertyChangeListener {
	private ScanModule scanModule;
	private Axis axis1;
	private Axis axis2;
	private boolean mainAxis;
	
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
	
	/** @since 1.31 */
	@Test
	public void testRemovePauseEvents() {
		Chain chain = ChainMother.createNewChain();
		chain.add(this.scanModule);
		Channel channel = ChannelMother.createNewChannel(scanModule);
		scanModule.add(channel);
		this.scanModule.addPauseEvent(
				PauseEventMother.createNewDetectorReadyEvent(channel));
		assert(!this.scanModule.getPauseEvents().isEmpty());
		this.scanModule.removePauseEvents();
		assert(this.scanModule.getPauseEvents().isEmpty());
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

	// TODO older tests should be moved to e.g. ScanModuleClassicTest
	// and one Test File for each scanmodule type should be created
	
	/**
	 * Inserts two axes into a scan module, sets one as main axes and validates 
	 * that the step width is adjusted.
	 * 
	 * @since 1.23
	 */
	@Test
	public void testSetMainAxis() {
		this.mockAxes();
		
		axis1.setStepfunction(Stepfunctions.ADD);
		axis2.setStepfunction(Stepfunctions.ADD);
		
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		
		axis2.setStart(0.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0); // should be 2 after setting main axis
		
		this.scanModule.add(axis1);
		this.scanModule.add(axis2);
		
		axis1.setMainAxis(true);
		
		assertEquals(2.0, (double) axis2.getStepwidth(), 0);
	}
	
	/**
	 * Inserts an axis into a scan module, sets it as main axis and adds another 
	 * axis. The second axis' stepwidth should be adjusted.
	 * 
	 * @since 1.23
	 */
	@Test
	public void testAddAxisAfterSetMainAxis() {
		this.mockAxes();
		
		axis1.setStepfunction(Stepfunctions.ADD);
		axis2.setStepfunction(Stepfunctions.ADD);
		
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		
		this.scanModule.add(axis1);
		axis1.setMainAxis(true);
		
		axis2.setStart(0.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0); // should be 2 after setting main axis
		
		this.scanModule.add(axis2);
		
		assertEquals(2.0, (double) axis2.getStepwidth(), 0);
	}
	
	/**
	 * Adds an axis to a scan module and another one with the MainAxis Property
	 * enabled. The first axis' stepwidth should be adjusted.
	 * 
	 * @since 1.23
	 */
	@Test
	public void testAddMainAxis() {
		this.mockAxes();
		
		axis1.setStepfunction(Stepfunctions.ADD);
		axis2.setStepfunction(Stepfunctions.ADD);
		
		axis2.setStart(0.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0); // should be 2 after adding main axis
		
		this.scanModule.add(axis2);
		
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		
		axis1.setMainAxis(true);
		this.scanModule.add(axis1);
		
		assertEquals(2.0, (double) axis2.getStepwidth(), 0);
	}
	
	/**
	 * Set an Axis as main axis when another one is already set. The old one 
	 * should be replaced.
	 * 
	 * @since 1.24
	 */
	@Test
	public void testOverwriteMainAxis() {
		Axis axis1 = mockAxis();
		Axis axis2 = mockAxis();
		Axis axis3 = mockAxis();
		
		axis1.setStepfunction(Stepfunctions.ADD);
		axis2.setStepfunction(Stepfunctions.ADD);
		axis3.setStepfunction(Stepfunctions.ADD);
		
		axis1.setStart(0.0);
		axis1.setStop(10.0);
		axis1.setStepwidth(1.0);
		
		axis2.setStart(0.0);
		axis2.setStop(10.0);
		axis2.setStepwidth(2.0);
		
		axis3.setStart(0.0);
		axis3.setStop(10.0);
		axis3.setStepwidth(5.0);
		
		this.scanModule.add(axis1);
		this.scanModule.add(axis2);
		this.scanModule.add(axis3);
		
		axis1.setMainAxis(true);
		
		assertEquals(1.0, (double) axis2.getStepwidth(), 0);
		assertEquals(1.0, (double) axis3.getStepwidth(), 0);
		
		axis3.setMainAxis(true);
		
		assertTrue(axis3.isMainAxis());
		assertEquals(AdjustParameter.STEPCOUNT, 
				((AddMultiplyMode<?>)axis3.getMode()).getAdjustParameter());
		
		assertFalse(axis1.isMainAxis());
		assertEquals(AdjustParameter.STEPWIDTH, 
				((AddMultiplyMode<?>)axis1.getMode()).getAdjustParameter());
		
		axis3.setStepwidth(5.0);
		
		assertEquals(5.0, (double) axis1.getStepwidth(), 0);
		assertEquals(5.0, (double) axis2.getStepwidth(), 0);
	}
	
	/**
	 * Adds two axes to a scan module, sets one as main axis and removes it.
	 * The remaining axis should be editable. The ScanModule should not have 
	 * a main axis anymore.
	 * 
	 * @since 1.23
	 */
	@Test
	public void testRemoveMainAxis() {
		this.mockAxes();
		
		axis1.setStart(0.0);
		axis1.setStop(100.0);
		axis1.setStepwidth(10.0);
		
		axis2.setStart(0.0);
		axis2.setStop(20.0);
		axis2.setStepwidth(1.0); 
		
		this.scanModule.add(axis1);
		this.scanModule.add(axis2);
		
		this.mainAxis = false;
		
		assertNull(this.scanModule.getMainAxis());
		assertFalse(this.mainAxis);
		
		this.scanModule.addPropertyChangeListener(ScanModule.MAIN_AXIS_PROP, this);
		axis1.setMainAxis(true);
		assertTrue(this.mainAxis);
		assertEquals(axis1, this.scanModule.getMainAxis());
		
		this.scanModule.remove(axis1);
		
		assertFalse(this.mainAxis);
		assertNull(this.scanModule.getMainAxis());
	}
	
	@Test
	public void testIsUsedAsNormalizeChannel() {
		ScanModule scanModule = ScanModuleMother.createNewScanModule();
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
		ScanModule scanModule = ScanModuleMother.createNewScanModule();
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case ScanModule.MAIN_AXIS_PROP:
			if (evt.getNewValue() != null) {
				this.mainAxis = true;
			} else {
				this.mainAxis = false;
			}
			break;
		}
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
	
	/*
	 * @Deprecated - Should not be Used, use mockAxis():Axis instead
	 */
	private void mockAxes() {
		axis1 = new Axis(this.scanModule);
		axis2 = new Axis(this.scanModule);
		MotorAxis mockAxis = mock(MotorAxis.class);
		when(mockAxis.getType()).thenReturn(DataTypes.DOUBLE);
		when(mockAxis.getDefaultValue()).thenReturn("0");
		when(mockAxis.getName()).thenReturn("MockAxis");
		Function mockFunction = mock(Function.class);
		when(mockFunction.isDiscrete()).thenReturn(false);
		when(mockAxis.getGoto()).thenReturn(mockFunction);
		axis1.setMotorAxis(mockAxis);
		axis2.setMotorAxis(mockAxis);
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
		this.scanModule = new ScanModule(1);
		this.scanModule.addModelUpdateListener(this);
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest() {
		this.scanModule = null;
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass() {
	}
}