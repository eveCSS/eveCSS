package de.ptb.epics.eve.data.measuringstation.filter.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DetectorChannelMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DetectorMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MeasuringStationMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.OptionMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChainMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ScanDescriptionMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ScanModuleMother;

/**
 * We define the following variables and states:
 * A: Option is a Detector Option (A1) or a Channel Option (A2)
 * B: Option has autoacquire attribute measurement (B1), snapshot (B2) or no (B3)
 * C: Channel is used in a snapshot module (C1), classic module (C2), both (C3) or none (C4)
 * 
 * Therefore 24 states have to be implemented (|A x B x C| = 2 * 3 * 4  = 24
 * 
 * method names will be according the the combinations, e.g. testXyzA1B1C1.
 * 
 * @author Marcus Michalsky
 * @since 1.37
 */
public class ExcludeFilterAutoAcquireTest {
	private MeasuringStation measuringStation;
	private ExcludeFilter filteredMeasuringStation;
	private ScanDescription scanDescription;
	private Chain chain;
	private ScanModule snapshotModule;
	private ScanModule classicModule;
	
	/**
	 * A1: Option is a Detector Option
	 * B1: Option has autoacquire attribute measurement
	 * C1: Channel is used in a snapshot module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B1C1() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewMeasurementAcquireOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B1: Option has autoacquire attribute measurement
	 * C2: Channel is used in a classic module
	 * Option should be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B1C2() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewMeasurementAcquireOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNotNull("option should be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B1: Option has autoacquire attribute measurement
	 * C3: Channel is used in a snapshot and classic module
	 * Option should be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B1C3() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewMeasurementAcquireOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNotNull("option should be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B1: Option has autoacquire attribute measurement
	 * C4: Channel is not used in any module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B1C4() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewMeasurementAcquireOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B1: Option has autoacquire attribute measurement
	 * C1: Channel is used in a snapshot module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B1C1() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewMeasurementAcquireOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B1: Option has autoacquire attribute measurement
	 * C2: Channel is used in a classic module
	 * Option should be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B1C2() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewMeasurementAcquireOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNotNull("option should be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B1: Option has autoacquire attribute measurement
	 * C3: Channel is used in a snapshot and classic module
	 * Option should be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B1C3() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewMeasurementAcquireOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNotNull("option should be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B1: Option has autoacquire attribute measurement
	 * C4: Channel is not used in any module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B1C4() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewMeasurementAcquireOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B2: Option has autoacquire attribute snapshot
	 * C1: Channel is used in a snapshot module
	 * Option should be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B2C1() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewSnapshotAcquireOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNotNull("option should be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B2: Option has autoacquire attribute snapshot
	 * C2: Channel is used in a classic module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B2C2() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewSnapshotAcquireOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B2: Option has autoacquire attribute snapshot
	 * C3: Channel is used in a snapshot and classic module
	 * Option should be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B2C3() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewSnapshotAcquireOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNotNull("option should be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B2: Option has autoacquire attribute snapshot
	 * C4: Channel is not used in any module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B2C4() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewSnapshotAcquireOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B3: Option has no autoacquire attribute
	 * C1: Channel is used in a snapshot module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B3C1() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B3: Option has no autoacquire attribute
	 * C2: Channel is used in a classic module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B3C2() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B3: Option has no autoacquire attribute
	 * C3: Channel is used in a snapshot and classic module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B3C3() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A1: Option is a Detector Option
	 * B3: Option has no autoacquire attribute
	 * C4: Channel is not used in any module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA1B3C4() {
		Detector detector = DetectorMother.createNewDetector();
		Option option = OptionMother.createNewOption();
		detector.add(option);
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B2: Option has autoacquire snapshot attribute
	 * C1: Channel is used in a snapshot module
	 * Option should be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B2C1() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewSnapshotAcquireOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNotNull("option should be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B2: Option has autoacquire snapshot attribute
	 * C2: Channel is used in a classic module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B2C2() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewSnapshotAcquireOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B2: Option has autoacquire snapshot attribute
	 * C3: Channel is used in a snapshot and classic module
	 * Option should be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B2C3() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewSnapshotAcquireOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNotNull("option should be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B2: Option has autoacquire snapshot attribute
	 * C4: Channel is not used in any module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B2C4() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewSnapshotAcquireOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B3: Option has no autoacquire attribute
	 * C1: Channel is used in a snapshot module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B3C1() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B3: Option has no autoacquire attribute
	 * C2: Channel is used in a classic module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B3C2() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B3: Option has no autoacquire attribute
	 * C3: Channel is used in a snapshot and classic module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B3C3() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		Channel channel = new Channel(this.snapshotModule, detectorChannel);
		this.snapshotModule.add(channel);
		channel = new Channel(this.classicModule, detectorChannel);
		this.classicModule.add(channel);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	/**
	 * A2: Option is a Channel Option
	 * B3: Option has no autoacquire attribute
	 * C4: Channel is not used in any module
	 * Option should not be in the result.
	 */
	@Test
	public void testExcludeUnusedDevicesAutoAcquireA2B3C4() {
		Detector detector = DetectorMother.createNewDetector();
		DetectorChannel detectorChannel = DetectorChannelMother.
				createNewDetectorChannel();
		Option option = OptionMother.createNewOption();
		detectorChannel.add(option);
		detector.add(detectorChannel);
		this.measuringStation.add(detector);
		
		this.filteredMeasuringStation.excludeUnusedDevices(scanDescription);
		assertNull("option should not be found", filteredMeasuringStation.
				getAbstractDeviceById(option.getID()));
	}
	
	@Before
	public void beforeTest() {
		this.measuringStation = MeasuringStationMother.
				createNewEmptyMeasuringStation();
		this.filteredMeasuringStation = new ExcludeFilter();
		this.filteredMeasuringStation.setSource(this.measuringStation);
		
		this.scanDescription = ScanDescriptionMother.createScanDescription(
				measuringStation);
		this.chain = ChainMother.createNewChain();
		this.scanDescription.add(chain);
		this.snapshotModule = ScanModuleMother.createNewScanModule();
		this.snapshotModule.setType(ScanModuleTypes.SAVE_CHANNEL_VALUES);
		this.chain.add(snapshotModule);
		this.classicModule = ScanModuleMother.createNewScanModule();
		this.classicModule.setType(ScanModuleTypes.CLASSIC);
		this.chain.add(classicModule);
	}
}
