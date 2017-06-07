package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;

/**
 * <code>MeasuringStation</code> is the model of a measuring station. It 
 * contains all elements available at a measuring station (e.g. motors, 
 * detectors) and defines their functionality.
 *  
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class MeasuringStation extends AbstractMeasuringStation{

	// the version of the measuring station description
	private String version;
	
	// the filename containing the measuring station that is loaded
	private String loadedFileName;
	
	// the name of the schema file used for validation of the measuring station
	private String schemaFileName;
	
	// the name of the measuring station (e.g., sx700, qnim, ...)
	private String name;

	/**
	 * Constructs an empty <code>MeasuringStation</code>.
	 */
	public MeasuringStation() {
		super();
	}
	
	/**
	 * Sets the version of the measuring station description.
	 * 
	 * @param version the version that should be set
	 */
	public void setVersion(String version) {
		this.version = version; 
	}
	
	/**
	 * Sets the name of the file used to load the measuring station description.
	 * 
	 * @param filename the name of the file
	 */
	public void setLoadedFileName(String filename) {
		loadedFileName = filename;
	}

	/**
	 * Sets the name of the schema file used to validate the measuring station 
	 * description.
	 * 
	 * @param filename the name of the schema file
	 */
	public void setSchemaFileName(String filename) {
		schemaFileName = filename;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name the name that should be set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Adds a {@link de.ptb.epics.eve.data.measuringstation.Detector} 
	 * to the measuring station. 
	 * 
	 * @param detector the 
	 * 		  {@link de.ptb.epics.eve.data.measuringstation.Detector} that 
	 * 		  should be added. 
	 * @return <code>true</code> if the detector was added, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean add(final Detector detector) {
		classMapAdd(detector.getClassName(), detector);
		return detectors.add(detector);
	}

	/**
	 * Adds a {@link de.ptb.epics.eve.data.measuringstation.Device} to the 
	 * measuring station. 
	 * 
	 * @param device the {@link de.ptb.epics.eve.data.measuringstation.Device} 
	 * 		  that should be added.
	 * @return <code>true</code> if the device was added,
	 * 		   <code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean add(final Device device) {
		if(device == null) {
			throw new IllegalArgumentException(
					"The parameter 'device' must not be null!");
		}
		classMapAdd(device.getClassName(), device);
		this.prePostscanDeviceMap.put(device.getID(), device);
		return devices.add(device);
	}

	/**
	 * Adds an {@link de.ptb.epics.eve.data.measuringstation.event.Event} to the 
	 * measuring station. 
	 * 
	 * @param event the {@link de.ptb.epics.eve.data.measuringstation.event.Event} 
			  that should be added.
	 * @return <code>true</code> if the event was added, 
	 * 		   <code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean add(final Event event) {
		if(event == null) {
			throw new IllegalArgumentException(
					"The parameter 'event' must not be null!");
		}
		this.eventsMap.put(event.getId(), event);
		return events.add(event);
	}

	/**
	 * Adds a {@link de.ptb.epics.eve.data.measuringstation.Motor} to the 
	 * measuring station. 
	 * 
	 * @param motor the {@link de.ptb.epics.eve.data.measuringstation.Motor} 
	 * 		  that should be added.
	 * @return <code>true</code> if the motor was added,
	 * 		   <code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean add(final Motor motor) {
		if(motor == null) {
			throw new IllegalArgumentException(
					"The parameter 'motor' must not be null!");
		}
		classMapAdd(motor.getClassName(), motor);
		return motors.add(motor);
	}
	
	/**
	 * Adds a {@link de.ptb.epics.eve.data.measuringstation.PlugIn} to the 
	 * measuring station. 
	 * 
	 * @param plugIn the {@link de.ptb.epics.eve.data.measuringstation.PlugIn} 
	 * 		  that should be added.
	 * @return <code>true</code> if the plug in was added,
	 * 		   <code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	public boolean add(final PlugIn plugIn) {
		if(plugIn == null) {
			throw new IllegalArgumentException(
					"The parameter 'plugIn' must not be null!");
		}
		this.pluginsMap.put(plugIn.getName(), plugIn);
		return plugins.add(plugIn);
	}
	
	/**
	 * Registers a {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} at 
	 * the measuring station. An axis registered this way can later be 
	 * retrieved through {@link #getMotorAxisById(String)}. (Faster than 
	 * iterating through the list gained by {@link #getMotors()}.)
	 * 
	 * @param motorAxis the 
	 * 		  {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} that 
	 * 		  should be registered
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void registerMotorAxis(final MotorAxis motorAxis) {
		if(motorAxis == null) {
			throw new IllegalArgumentException(
					"The parameter 'motorAxis' must not be null!");
		}
		this.motorAxisMap.put(motorAxis.getID(), motorAxis);
	}
	
	/**
	 * Registers a {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} 
	 * at the measuring station. A channel registered this way can later be 
	 * retrieved through {@link #getDetectorChannelById(String)}. (Faster than 
	 * iterating through the list gained by {@link #getDetectors()}.)
	 * 
	 * @param detectorChannel the 
	 * 		  {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel}  
	 * 		  that should be registered
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void registerDetectorChannel(final DetectorChannel detectorChannel) {
		if(detectorChannel == null) {
			throw new IllegalArgumentException(
					"The parameter 'detectorChannel' must not be null!");
		}
		this.detectorChannelsMap.put(detectorChannel.getID(), detectorChannel);
	}

	/**
	 * Registers an {@link de.ptb.epics.eve.data.measuringstation.Option} at 
	 * the measuring station. An option registered this way can later be 
	 * retrieved through {@link #getPrePostscanDeviceById(String)}.
	 * 
	 * @param option the {@link de.ptb.epics.eve.data.measuringstation.Option} 
	 * 		  that should be registered
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void registerOption(final Option option) {
		if(option == null) {
			throw new IllegalArgumentException(
					"The parameter 'option' must not be null!");
		}
		this.prePostscanDeviceMap.put(option.getID(), option);
	}
	
	/**
	 * Adds an {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} 
	 * with class name to the class hash.
	 * 
	 * @param className the name of the class the device is part of
	 * @param absdevice the 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} that 
	 * 		should be added
	 */
	private void classMapAdd(String className, AbstractDevice absdevice) {
		if(absdevice instanceof Motor) {
			final List<MotorAxis> axis = ((Motor)absdevice).getAxes();
			for(final MotorAxis a : axis) {
				this.classMapAdd(a.getClassName(), a);
			}
		} else if(absdevice instanceof Detector) {
			final List<DetectorChannel> channels = ((Detector)absdevice).getChannels();
			for(final DetectorChannel c : channels) {
				this.classMapAdd(c.getClassName(), c);
			}
		} 
		
		if ((className == null) || className.length() < 1) {
			return;
		}
		
		List<AbstractDevice> adlist;
		if (classMap.containsKey(className)){
			adlist = classMap.get(className);
		}
		else {
			adlist = new ArrayList<AbstractDevice>();
		}
		adlist.add(absdevice);
		classMap.put(className, adlist);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getVersion() {
		return this.version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLoadedFileName() {
		return loadedFileName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSchemaFileName() {
		return schemaFileName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MotorAxis getMotorAxisByName(String name) {
		for (MotorAxis axis : this.getMotorAxes().values()) {
			if (axis.getName().equals(name)) {
				return axis;
			}
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getAxisFullIdentifyer() {
		final List<String> identifier = new ArrayList<String>();
		final Iterator<Motor> motorIterator = this.motors.iterator();

		Motor currentMotor = null;
		MotorAxis currentAxis = null;
		Iterator<MotorAxis> axisIterator = null;
		while (motorIterator.hasNext()) {
			currentMotor = motorIterator.next();
			axisIterator = currentMotor.axisIterator();
			while (axisIterator.hasNext()) {
				currentAxis = axisIterator.next();
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); iterator.hasNext();) {
					final String test = iterator.next();
					if (currentAxis.getFullIdentifyer().compareToIgnoreCase(test) > 0) {
						i++;
					} else {
						break;
					}
				}
				identifier.add(i, currentAxis.getFullIdentifyer());
			}
		}
		return identifier;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getChannelsFullIdentifyer() {
		List<String> identifier = new ArrayList<String>();
		Iterator<Detector> detectorIterator = this.detectors.iterator();
		
		while(detectorIterator.hasNext()) {
			Detector currentDetector = detectorIterator.next();
			Iterator<DetectorChannel> channelIterator = 
					currentDetector.channelIterator();
			while(channelIterator.hasNext()) {
				DetectorChannel currentChannel = channelIterator.next();
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); 
					 iterator.hasNext();) {
					final String test = iterator.next();
					if (currentDetector.getFullIdentifyer().
							compareToIgnoreCase(test) > 0) {
						i++;
					} else {
						break;
					}
				}
				identifier.add(i, currentChannel.getFullIdentifyer());
			}
		}
		return identifier;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getPrePostScanDevicesFullIdentifyer() {
		List<String> identifier = new ArrayList<String>();
		Iterator<Device> deviceIterator = this.devices.iterator();
		while(deviceIterator.hasNext()) {
			Device currentDevice = deviceIterator.next();
			int i = 0;
			for (Iterator<String> iterator = identifier.iterator(); 
				 iterator.hasNext();) {
				final String test = iterator.next();
				if (currentDevice.getFullIdentifyer().
						compareToIgnoreCase(test) > 0) {
					i++;
				} else {
					break;
				}
			}
			identifier.add(i, currentDevice.getFullIdentifyer());
		}
		
		Iterator<Motor> motorIterator = this.motors.iterator();
		while(motorIterator.hasNext()) {
			Motor currentMotor = motorIterator.next();
			
			Iterator<Option> optionIterator = currentMotor.optionIterator();
			while(optionIterator.hasNext()) {
				Option currentOption = optionIterator.next();
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); 
					 iterator.hasNext();) {
					final String test = iterator.next();
					if (currentOption.getFullIdentifyer().
							compareToIgnoreCase(test) > 0) {
						i++;
					} else {
						break;
					}
				}
				identifier.add(i, currentOption.getFullIdentifyer());
			}
			
			Iterator<MotorAxis> axisIterator = currentMotor.axisIterator();
			while(axisIterator.hasNext()) {
				MotorAxis currentAxis = axisIterator.next();
				
				Iterator<Option> optionIterator2 = 
						currentAxis.getOptions().iterator();
				while( optionIterator2.hasNext() ) {
					Option currentOption = optionIterator2.next();
					int i = 0;
					for (Iterator<String> iterator = identifier.iterator(); 
						 iterator.hasNext();) {
						final String test = iterator.next();
						if (currentOption.getFullIdentifyer().
								compareToIgnoreCase(test) > 0) {
							i++;
						} else {
							break;
						}
					}
					identifier.add(i, currentOption.getFullIdentifyer());
				}
			}
		}
		
		Iterator<Detector> detectorIterator = this.detectors.iterator();
		while(detectorIterator.hasNext()) {
			Detector currentDetector = detectorIterator.next();
			
			Iterator<Option> optionIterator = 
				currentDetector.getOptions().iterator();
			while(optionIterator.hasNext()) {
				Option currentOption = optionIterator.next();
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); 
					 iterator.hasNext();) {
					final String test = iterator.next();
					if (currentOption.getFullIdentifyer().
							compareToIgnoreCase(test) > 0) {
						i++;
					} else {
						break;
					}
				}
				identifier.add(i, currentOption.getFullIdentifyer());
			}
			
			Iterator<DetectorChannel> channelsIterator = 
				currentDetector.channelIterator();
			while(channelsIterator.hasNext()) {
				DetectorChannel currentChannel = channelsIterator.next();
				
				Iterator<Option> optionIterator2 = 
						currentChannel.getOptions().iterator();
				while(optionIterator2.hasNext()) {
					Option currentOption = optionIterator2.next();
					int i = 0;
					for (Iterator<String> iterator = identifier.iterator(); 
						 iterator.hasNext();) {
						final String test = iterator.next();
						if (currentOption.getFullIdentifyer().
								compareToIgnoreCase(test) > 0) {
							i++;
						} else {
							break;
						}
					}
					identifier.add(i, currentOption.getFullIdentifyer());
				}
			}
		}
		return identifier;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractDevice> getDeviceList(String classname) {
		if (classMap.containsKey(classname)) {
			return classMap.get(classname);
		} else {
			return null;
		}
	}
	
	// ************************************************************************
	// *********** methods inherited from IModelUpdateProvider ****************
	// ************************************************************************
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return false;
	}

}