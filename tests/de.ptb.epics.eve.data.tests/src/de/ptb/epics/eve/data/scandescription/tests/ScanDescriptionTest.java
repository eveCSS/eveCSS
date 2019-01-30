package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.MonitorOption;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.MeasuringStationMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.OptionMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChainMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ChannelMother;
import de.ptb.epics.eve.data.tests.mothers.scandescription.ScanModuleMother;

public class ScanDescriptionTest implements PropertyChangeListener {
	private ScanDescription scanDescription;
	
	// indicators for PropertyChangeSupportTest
	private boolean repeatCount;
	private boolean fileName;
	private boolean monitorOption;
	private boolean monitorOptionsList;
	
	@Test
	public void testPropertyChangeSupportRepeatCount() {
		this.repeatCount = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.REPEAT_COUNT_PROP, this);
		this.scanDescription.setRepeatCount(1);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.REPEAT_COUNT_PROP, this);
		assertTrue(this.repeatCount);
	}
	
	@Test
	public void testPropertyChangeSupportFileName() {
		this.fileName = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.FILE_NAME_PROP, this);
		this.scanDescription.setFileName("myFileName");
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.FILE_NAME_PROP, this);
		assertTrue(this.fileName);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionNoneToNone() {
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.NONE);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertFalse("none -> none", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionNoneToCustom() {
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("none -> custom", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionNoneToUsedInScan() {
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("none -> used in scan", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionNoneToAsInDeviceDefinition() {
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("none -> as in device definition", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionCustomToNone() {
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.NONE);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("custom -> none", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionCustomToCustom() {
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertFalse("custom -> custom", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionCustomToUsedInScan() {
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("custom -> used in scan", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionCustomToAsInDeviceDefinition() {
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("custom -> as in device definition", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionUsedInScanToNone() {
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.NONE);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("used in scan -> none", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionUsedInScanToCustom() {
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("used in scan -> custom", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionUsedInScanToUsedInScan() {
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertFalse("used in scan -> used in scan", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionUsedInScanToAsInDeviceDefinition() {
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("used in scan -> as in device definition", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionAsInDeviceDefinitionToNone() {
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.NONE);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("as in device definition -> none", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionAsInDeviceDefinitionToCustom() {
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("as in device definition -> custom", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionAsInDeviceDefinitionToUsedInScan() {
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertTrue("as in device definition -> used in scan", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionAsInDeviceDefinitionToAsInDeviceDefinition() {
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.monitorOption = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTION_PROP, this);
		assertFalse("as in device definition -> as in device definition", this.monitorOption);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListNoneToNone() {
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.NONE);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertFalse("none -> none", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListNoneToCustom() {
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("none -> custom", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListNoneToUsedInScan() {
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("none -> used in scan", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListNoneToAsInDeviceDefinition() {
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("none -> as in device definition", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListCustomToNone() {
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.NONE);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("custom -> none", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListCustomToCustom() {
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertFalse("custom -> custom", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListCustomToUsedInScan() {
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("custom -> used in scan", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListCustomToAsInDeviceDefinition() {
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("custom -> as in device definition", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListUsedInScanToNone() {
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.NONE);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("used in scan -> none", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListUsedInScanToCustom() {
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("used in scan -> custom", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListUsedInScanToUsedInScan() {
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertFalse("used in scan -> used in scan", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListUsedInScanToAsInDeviceDefinition () {
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("used in scan -> as in device definition", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListAsInDeviceDefinitionToNone() {
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.NONE);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("as in device definition -> none", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListAsInDeviceDefinitionToCustom() {
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("as in device definition -> custom", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListAsInDeviceDefinitionToUsedInScan() {
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.USED_IN_SCAN);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue("as in device definition -> used in scan", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListAsInDeviceDefinitionToAsInDeviceDefinition() {
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.setMonitorOption(MonitorOption.AS_IN_DEVICE_DEFINITION);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertFalse("as in device definition -> as in device definition", this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListAddMonitor() {
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.addMonitor(OptionMother.createNewOption());
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue(this.monitorOptionsList);
	}
	
	@Test
	public void testPropertyChangeSupportMonitorOptionsListRemoveMonitor() {
		this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
		Option myOption = OptionMother.createNewOption();
		this.scanDescription.addMonitor(myOption);
		this.monitorOptionsList = false;
		this.scanDescription.addPropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		this.scanDescription.removeMonitor(myOption);
		this.scanDescription.removePropertyChangeListener(
				ScanDescription.MONITOR_OPTIONS_LIST_PROP, this);
		assertTrue(this.monitorOptionsList);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(ScanDescription.REPEAT_COUNT_PROP)) {
			this.repeatCount = true;
		} else if (e.getPropertyName().equals(ScanDescription.FILE_NAME_PROP)) {
			this.fileName = true;
		} else if (e.getPropertyName().equals(ScanDescription.MONITOR_OPTION_PROP)) {
			this.monitorOption = true;
		} else if (e.getPropertyName().equals(ScanDescription.MONITOR_OPTIONS_LIST_PROP)) {
			this.monitorOptionsList = true;
		}
	}
	
	/**
	 * Class wide setup method of the test
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
	}
	
	/**
	 * test wide set up method
	 */
	@Before
	public void setUp() {
		this.scanDescription = new ScanDescription(
				MeasuringStationMother.addDetectorWithChannelAndOptionWithMonitor(
					((MeasuringStation)MeasuringStationMother.createNewMeasuringStation())));
		Chain myChain = ChainMother.createNewChain();
		this.scanDescription.add(myChain);
		ScanModule myScanModule = ScanModuleMother.createNewScanModule();
		myChain.add(myScanModule);
		Channel myChannel = ChannelMother.createNewChannel(myScanModule);
		myScanModule.add(myChannel);
	}
	
	/**
	 * test wide tear down method
	 */
	@After
	public void tearDown() {
	}
	
	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void tearDownAfterClass() {
	}
}