package de.ptb.epics.eve.data.measuringstation.filter.tests;

import java.util.List;

import static org.junit.Assert.*;

import org.junit.*;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.tests.internal.Configurator;

/**
 * <code>ExcludeFilterTest</code> contains 
 * <a href="http://www.junit.org/">JUnit</a>-Tests for 
 * {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter}.
 * Most of the tests just test each 
 * {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} by checking 
 * its presence, exclude it and check his absence, include it and check its 
 * presence again. The presence and absence of excluded/included sub devices 
 * (e.g. an axis of a motor or a channel of a detector or options) are also 
 * tested.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class ExcludeFilterTest {
	private static List<IMeasuringStation> stations;
	
	/**
	 * <code>testExcludeIncludeMotor</code> tries to exclude motors (each one 
	 * by one) and verifies their absence. It also checks the presence / absence 
	 * of their axis and options. Afterwards they are included and presence is 
	 * checked again.
	 */
	@Test
	public void testExcludeIncludeMotor() {
		for (IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);

			for (Motor m : measuringStation.getMotors()) {
				// the motor should be found
				assertTrue(isMotor(filteredMeasuringStation, m));
				// its options should be found
				for (Option o : m.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
				}
				// its axis should also be found
				for (MotorAxis ma : m.getAxes()) {
					assertTrue(isMotorAxis(filteredMeasuringStation, ma));
					// axis options should be found as well
					for (Option o : ma.getOptions()) {
						assertTrue(isOption(filteredMeasuringStation, o));
					}
				}
				// exclude the motor
				filteredMeasuringStation.exclude(m);
				// now the motor shouldn't be found anymore
				assertFalse(isMotor(filteredMeasuringStation, m));
				// its options shouldn't be found
				for (Option o : m.getOptions()) {
					assertFalse(isOption(filteredMeasuringStation, o));
				}
				// all axes of the motor also shouldn't be found
				for (MotorAxis ma : m.getAxes()) {
					assertFalse(isMotorAxis(filteredMeasuringStation, ma));
					// axis options shouldn't be found as well
					for (Option o : ma.getOptions()) {
						assertFalse(isOption(filteredMeasuringStation, o));
					}
				}
				// include the motor
				filteredMeasuringStation.include(m);
				// now the motor should be found again
				assertTrue(isMotor(filteredMeasuringStation, m));
				// its options should be found
				for (Option o : m.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
				}
				// all axes of the motor also should be back
				for (MotorAxis ma : m.getAxes()) {
					assertTrue(isMotorAxis(filteredMeasuringStation, ma));
					// axis options should be found as well
					for (Option o : ma.getOptions()) {
						assertTrue(isOption(filteredMeasuringStation, o));
					}
				}
			}
		}
	}
	
	/**
	 * <code>testExcludeIncludeMotorAxis</code> tries to exclude motor axis 
	 * (each one by one) and verifies their absence. It also checks the 
	 * presence / absence of their options. Afterwards they are included and 
	 * presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotorAxis() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Motor m : measuringStation.getMotors()) {
			assertTrue(isMotor(filteredMeasuringStation, m));
			
			for(MotorAxis ma : m.getAxes()) {
				// the motor axis should be found
				assertTrue(isMotorAxis(filteredMeasuringStation, ma));
				
				// axis options should be found as well
				for(Option o : ma.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
				}
				
				// ***
				
				// exclude it
				filteredMeasuringStation.exclude(ma);
				
				// now it shouldn't be found anymore
				assertFalse(isMotorAxis(filteredMeasuringStation, ma));
				
				// axis options shouldn't be found as well
				for(Option o : ma.getOptions()) {
					assertFalse(isOption(filteredMeasuringStation, o));
				}
				
				assertTrue(isMotor(filteredMeasuringStation, m));
				
				// ***
				
				// include it
				filteredMeasuringStation.include(ma);
				
				// now it should be found again
				assertNotNull(filteredMeasuringStation.
								getMotorAxisById(ma.getID()));
				
				// axis options should be found as well
				for(Option o : ma.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
				}
			}
		}
		}
	}
	
	/**
	 * <code>testExcludeIncludeMotorOption</code> tries to exclude motor options 
	 * (each one by one) and verifies their absence. Afterwards they are 
	 * included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotorOption() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Motor m : measuringStation.getMotors()) {
			for(Option o : m.getOptions()) {
				
				// test if the option is found
				assertTrue(isOption(filteredMeasuringStation, o));
				
				// ***"
				
				// exclude it
				filteredMeasuringStation.exclude(o);
				
				// test its absence
				assertFalse(isOption(filteredMeasuringStation, o));
				
				// check if the (parent) motor is still there
				assertTrue(isMotor(filteredMeasuringStation, m));
				
				// ***
				
				// include it
				filteredMeasuringStation.include(o);
				
				// test its presence
				assertNotNull(filteredMeasuringStation.
						getPrePostscanDeviceById(o.getID()));
			}
			if(m.getOptions().size() == 0) {
			}
		}
		}
	}
	
	/**
	 * <code>testExcludeIncludeMotorAxisOption</code> tries to exclude motor 
	 * axes options (each one by one) and verifies their absence. Afterwards 
	 * they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeMotorAxisOption() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Motor m : measuringStation.getMotors()) {
			for(MotorAxis ma : m.getAxes()) {
				for(Option o : ma.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
					
					filteredMeasuringStation.exclude(o);
					assertTrue(isMotorAxis(filteredMeasuringStation, ma));
					assertTrue(isMotor(filteredMeasuringStation, m));
					
					assertFalse(isOption(filteredMeasuringStation, o));
					
					filteredMeasuringStation.include(o);
					assertTrue(isOption(filteredMeasuringStation, o));
				}
			}
		}
		}
	}
	
	// **********************************************************
	
	/**
	 * <code>testExcludeIncludeDetector</code> tries to exclude detectors (each 
	 * one by one) and verifies their absence. It also checks the presence / 
	 * absence of their channels and options. Afterwards they are included and 
	 * presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDetector() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Detector d : measuringStation.getDetectors()) {
			// the detector should be found
			assertTrue(isDetector(filteredMeasuringStation, d));
			
			// its options should be found
			for(Option o : d.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
			}
			
			// its channels should also be found
			for(DetectorChannel ch : d.getChannels()) {
				assertTrue(isDetectorChannel(filteredMeasuringStation, ch));
				
				// channel options should be found as well
				for(Option o : ch.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
				}
			}
			
			// ***
			
			// exclude the detector
			filteredMeasuringStation.exclude(d);
			// now the detector shouldn't be found anymore
			assertFalse(isDetector(filteredMeasuringStation, d));

			// its options shouldn't be found
			for(Option o : d.getOptions()) {
				assertFalse(isOption(filteredMeasuringStation, o));
			}
			
			// all channels of the detector also shouldn't be found
			for(DetectorChannel ch : d.getChannels()) {
				assertFalse(isDetectorChannel(filteredMeasuringStation, ch));
				
				// channel options shouldn't be found as well
				for(Option o : ch.getOptions()) {
					assertFalse(isOption(filteredMeasuringStation, o));
				}
			}
			
			// ***
			
			// include the detector
			filteredMeasuringStation.include(d);
			
			// now the detector should be found again
			assertTrue(isDetector(filteredMeasuringStation, d));
			
			// its options should be found
			for(Option o : d.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
			}
			
			// all channels of the detector also should be back
			for(DetectorChannel ch : d.getChannels()) {
				assertTrue(isDetectorChannel(filteredMeasuringStation, ch));
				
				// channel options should be found as well
				for(Option o : ch.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
				}
			}
		}
		}
	}
	
	/**
	 * <code>testExcludeIncludeDetectorChannel</code> tries to exclude 
	 * detector channels (each one by one) and verifies their absence. 
	 * Afterwards they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDetectorChannel() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Detector d : measuringStation.getDetectors()) {
			for(DetectorChannel ch : d.getChannels()) {
				// the detector channel should be found
				assertTrue(isDetectorChannel(filteredMeasuringStation, ch));
				
				// its options should also be found
				for(Option o : ch.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
				}
				
				// exclude it
				filteredMeasuringStation.exclude(ch);
				// now it shouldn't be found anymore
				assertFalse(isDetectorChannel(filteredMeasuringStation, ch));
				
				// its options shouldn't be found
				for(Option o : ch.getOptions()) {
					assertFalse(isOption(filteredMeasuringStation, o));
				}
				
				// include it
				filteredMeasuringStation.include(ch);
				// now it should be found again
				assertTrue(isDetectorChannel(filteredMeasuringStation, ch));
				
				// its options should also be found
				for(Option o : ch.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
				}
			}
		}
		}
	}
	
	/**
	 * <code>testExcludeIncludeDetectorOption</code> tries to exclude detector 
	 * options (each one by one) and verifies their absence.Afterwards they are 
	 * included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDetectorOption() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Detector d : measuringStation.getDetectors()) {
			for(Option o : d.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));

				filteredMeasuringStation.exclude(o);
				
				assertFalse(isOption(filteredMeasuringStation, o));
				
				filteredMeasuringStation.include(o);
				
				assertTrue(isOption(filteredMeasuringStation, o));
			}
		}
		}
	}
	
	/**
	 * <code>testExcludeIncludeDetectorChannelOption</code> tries to exclude 
	 * detector channel options (each one by one) and verifies their absence. 
	 * Afterwards they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDetectorChannelOption() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Detector d : measuringStation.getDetectors()) {
			for(DetectorChannel ch : d.getChannels()) {
				for(Option o : ch.getOptions()) {
					assertTrue(isOption(filteredMeasuringStation, o));
					
					filteredMeasuringStation.exclude(o);
					assertFalse(isOption(filteredMeasuringStation, o));
					
					filteredMeasuringStation.include(o);
					assertTrue(isOption(filteredMeasuringStation, o));
				}
			}
		}
		}
	}
	
	/**
	 * <code>testExcludeIncludeDevice</code> tries to exclude devices 
	 * (each one by one) and verifies their absence (options of the devices 
	 * are also checked). Afterwards they are included and presence is checked 
	 * again.
	 */
	@Test
	public void testExcludeIncludeDevice() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Device dev : measuringStation.getDevices()) {
			// the device should be found
			assertTrue(isDevice(filteredMeasuringStation, dev));

			// its options should be found
			for(Option o : dev.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
			}
			
			// ***
			
			// exclude it
			filteredMeasuringStation.exclude(dev);
			// now it shouldn't be found anymore
			assertFalse(isDevice(filteredMeasuringStation, dev));
			
			// its options should be found
			for(Option o : dev.getOptions()) {
				assertFalse(isOption(filteredMeasuringStation, o));
			}
			
			// include it
			filteredMeasuringStation.include(dev);
			// now it should be found again
			assertTrue(isDevice(filteredMeasuringStation, dev));
			
			// its options should be found
			for(Option o : dev.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
			}
		}
		}
	}
	
	/**
	 * <code>testExcludeIncludeDeviceOption</code> tries to exclude 
	 * device options (each one by one) and verifies their absence. Afterwards 
	 * they are included and presence is checked again.
	 */
	@Test
	public void testExcludeIncludeDeviceOption() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		for(Device dev : measuringStation.getDevices()) {
			for(Option o : dev.getOptions()) {
				assertTrue(isOption(filteredMeasuringStation, o));
				
				filteredMeasuringStation.exclude(o);
				assertFalse(isOption(filteredMeasuringStation, o));
				
				filteredMeasuringStation.include(o);
				assertTrue(isOption(filteredMeasuringStation, o));
			}
		}
		}
	}
	
	/**
	 * Tests whether a device is being filtered (should not) if it is (only) 
	 * used as an event (Pause/Redo/Break/Stop/Trigger) in a chain/scan module 
	 * or detector.
	 */
	@Test
	public void testDevicePresenceIfEventPresent() {
		for(IMeasuringStation measuringStation : stations) {
			assertNotNull(measuringStation);
			
			// event in pause event manager of chain
			for(Event e : measuringStation.getEvents()) {
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).addPauseEvent(
						new PauseEvent(EventTypes.MONITOR, e, 
								e.getId()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getId()));
			}
			
			// event in redo event manager of chain
			for(Event e : measuringStation.getEvents()) {
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).addRedoEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getId()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getId()));
			}
			
			// event in break event manager of chain
			for(Event e : measuringStation.getEvents()) {
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).addBreakEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getId()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getId()));
			}
			
			// event in stop event manager of chain
			for(Event e : measuringStation.getEvents()) {
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).addStopEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getId()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getId()));
			}
			
			// event in pause event manager of scan module
			for(Event e : measuringStation.getEvents()) {
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getScanModuleById(1).addPauseEvent(
						new PauseEvent(EventTypes.MONITOR, e, 
								e.getId()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getId()));
			}
			
			// event in redo event manager of scan module
			for(Event e : measuringStation.getEvents()) {
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getScanModuleById(1).addRedoEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getId()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getId()));
			}
			
			// event in break event manager of scan module
			for(Event e : measuringStation.getEvents()) {
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getScanModuleById(1).addBreakEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getId()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getId()));
			}
			
			// event in trigger event manager of scan module
			for(Event e : measuringStation.getEvents()) {
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				sd.getChain(1).getScanModuleById(1).addTriggerEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getId()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getId()));
			}
			
			// event in redo event manager of detector
			for(Event e : measuringStation.getEvents()) {
				ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
				filteredMeasuringStation.setSource(measuringStation);
				assertNotNull(filteredMeasuringStation);
				
				ScanDescription sd = Configurator.getBasicScanDescription(
						measuringStation);
				
				Detector d = new Detector();
				d.setId("Det1");
				DetectorChannel ch = new DetectorChannel();
				ch.setId("DetCh1");
				d.add(ch);
				
				Channel chan = new Channel(sd.getChain(1).getScanModuleById(1));
				chan.setDetectorChannel(ch);
				sd.getChain(1).getScanModuleById(1).add(chan);
				
				chan.getRedoControlEventManager().addControlEvent(
						new ControlEvent(EventTypes.MONITOR, e, 
								e.getId()));
				
				filteredMeasuringStation.excludeUnusedDevices(sd);
				
				assertNotNull(filteredMeasuringStation.
						getAbstractDeviceById(e.getId()));
			}
		}
	}
	
	// *********************************************************************
	
	/**
	 * <code>testEqualityMotorListMap</code> checks whether each motor axis 
	 * contained in the motor list is also available via 
	 * {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter#getMotorAxisById(String)}.
	 */
	@Test
	public void testEqualityMotorListMap() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
		
		filteredMeasuringStation.updateEvent(null);
		
		for(Motor m : filteredMeasuringStation.getMotors()) {
			for(MotorAxis ma : m.getAxes()) {
				assertNotNull(filteredMeasuringStation.getMotorAxisById(ma.getID()));
			}
		}
		}
	}
	
	// ****************************************************************
	
	/**
	 * Test for {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter#excludeUnusedDevices(ScanDescription)}.
	 * Tests if the devices used in the scan description (obtained by 
	 * {@link de.ptb.epics.eve.data.tests.internal.Configurator#getScanDescription()}) 
	 * are available after executing 
	 * {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter#excludeUnusedDevices(ScanDescription)}. 
	 * Does NOT test (for now) whether unused devices aren't available.
	 */
	@Ignore("has to be updated")
	@Test
	public void testExcludeUnusedDevices() {
		/*
		for(Pair<IMeasuringStation, List<ScanDescription>> p : 
				Configurator.getScanDescriptions()) {
			
		}
		
		ScanDescription sd = Configurator.getScanDescription();
		assertNotNull(sd);
		
		IMeasuringStation measuringStation = Configurator.getMeasuringStation();
		ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
		filteredMeasuringStation.setSource(measuringStation);
		
		filteredMeasuringStation.excludeUnusedDevices(sd);
		logger.info("excluded unused devices (unused = not in the scan description)");
		*/
		
	}
	
	/**
	 * Takes a device from the measuring station and checks it for equality with 
	 * its counterpart got by 
	 * {@link de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter#getAbstractDeviceById(String)}.
	 */
	@Test
	public void testGetAbstractDeviceById() {
		for(IMeasuringStation measuringStation : stations) {
			ExcludeFilter filteredMeasuringStation = new ExcludeFilter();
			filteredMeasuringStation.setSource(measuringStation);
			assertNotNull(filteredMeasuringStation);
			
			for(Motor m : measuringStation.getMotors()) {
				AbstractDevice motorFromFilter = filteredMeasuringStation.
						getAbstractDeviceById(m.getID());
				assertEquals(m, motorFromFilter);
				
				for(Option o : m.getOptions()) {
					AbstractDevice optionFromFilter = filteredMeasuringStation.
							getAbstractDeviceById(o.getID());
					assertEquals(o, optionFromFilter);
				}
				
				for(MotorAxis ma : m.getAxes()) {
					AbstractDevice axisFromFilter = filteredMeasuringStation.
							getAbstractDeviceById(ma.getID());
					assertEquals(ma, axisFromFilter);
					
					for(Option o : ma.getOptions()) {
						AbstractDevice optionFromFilter = filteredMeasuringStation.
								getAbstractDeviceById(o.getID());
						assertEquals(o, optionFromFilter);
					}
				}
			}
			
			for(Detector d : measuringStation.getDetectors()) {
				AbstractDevice detectorFromFilter = filteredMeasuringStation.
						getAbstractDeviceById(d.getID());
				assertEquals(d, detectorFromFilter);
				
				for(Option o : d.getOptions()) {
					AbstractDevice optionFromFilter = filteredMeasuringStation.
							getAbstractDeviceById(o.getID());
					assertEquals(o, optionFromFilter);
				}
				
				for(DetectorChannel ch : d.getChannels()) {
					AbstractDevice channelFromFilter = filteredMeasuringStation.
							getAbstractDeviceById(ch.getID());
					assertEquals(ch, channelFromFilter);
					
					for(Option o : ch.getOptions()) {
						AbstractDevice optionFromFilter = filteredMeasuringStation.
								getAbstractDeviceById(o.getID());
						assertEquals(o, optionFromFilter);
					}
				}
			}
			
			for(Device dev : measuringStation.getDevices()) {
				AbstractDevice deviceFromFilter = filteredMeasuringStation.
						getAbstractDeviceById(dev.getID());
				assertEquals(dev, deviceFromFilter);
				
				for(Option o : dev.getOptions()) {
					AbstractDevice optionFromFilter = filteredMeasuringStation.
							getAbstractDeviceById(o.getID());
					assertEquals(o, optionFromFilter);
				}
			}
		}
	}
	
	// ****************************************************************
	
	/*
	 * 
	 */
	private boolean isMotor(IMeasuringStation measuringstation, Motor m) {
		Motor motor = (Motor) measuringstation.
				getAbstractDeviceByFullIdentifyer(m.getFullIdentifyer());
		return motor != null;
	}
	
	/*
	 * 
	 */
	private boolean isMotorAxis(IMeasuringStation measuringstation, 
								MotorAxis ma) {
		MotorAxis motoraxis = measuringstation.getMotorAxisById(ma.getID());
		return motoraxis != null;
	}
	
	/*
	 * 
	 */
	private boolean isDetector(IMeasuringStation measuringstation, Detector d) {
		Detector detector = (Detector) measuringstation.
				getAbstractDeviceByFullIdentifyer(d.getFullIdentifyer());
		return detector != null;
	}
	
	/*
	 * 
	 */
	private boolean isDetectorChannel(IMeasuringStation measuringstation, 
										DetectorChannel ch) {
		DetectorChannel channel = measuringstation.
				getDetectorChannelById(ch.getID());
		return channel != null;
	}
	
	/*
	 * 
	 */
	private boolean isDevice(IMeasuringStation measuringstation, 
								Device dev) {
		Device device = (Device) measuringstation.
				getAbstractDeviceByFullIdentifyer(dev.getFullIdentifyer());
		return device != null;
	}
	
	/*
	 * 
	 */
	private boolean isOption(IMeasuringStation measuringstation, Option o) {
		Option option = (Option) 
				measuringstation.getPrePostscanDeviceById(o.getID());
		return option != null;
	}

	// ***********************************************************************
	// ***********************************************************************
	// ***********************************************************************
	
	/**
	 * Initializes logging and loads the measuring station (Class wide setup 
	 * method of the test).
	 */
	@BeforeClass
	public static void beforeClass() {
		stations = Configurator.getMeasuringStations();
		for(IMeasuringStation ims : stations) {
			assertNotNull(ims);
		}
	}
	
	/**
	 * Test wide set up method.
	 */
	@Before
	public void beforeTest() {
	}
	
	/**
	 * Test wide tear down method.
	 */
	@After
	public void afterTest() {
	}
	
	/**
	 * Class wide tear down method.
	 */
	@AfterClass
	public static void afterClass() {;
	}
}