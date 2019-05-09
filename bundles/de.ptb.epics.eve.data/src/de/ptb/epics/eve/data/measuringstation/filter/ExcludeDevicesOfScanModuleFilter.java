package de.ptb.epics.eve.data.measuringstation.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>ExcludeDevicesOfScanModuleFilter</code> is a 
 * {@link de.ptb.epics.eve.data.measuringstation.filter.MeasuringStationFilter} 
 * used in combination with a 
 * {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
 * <p>
 * The constructor takes arguments for each class of devices indicating whether 
 * they should be excluded. After construction a source measuring station should 
 * be set with 
 * {@link #setSource(de.ptb.epics.eve.data.measuringstation.IMeasuringStation)} 
 * and the related scan module via {@link #setScanModule(ScanModule)}.<br>
 * If a certain class of devices is set to be filtered (<code>true</code> 
 * argument in the constructor) and such a device is added to the scan module, 
 * the <code>ExcludeDevicesOfScanModuleFilter</code> will not return it via its 
 * getter methods.<br>
 * The choice of exclusion is final (could not be changed after construction).
 * <br><br>
 * An example application of this filter is 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * <p>
 * If this Filter is not used anymore (should be Garbage Collected) it is 
 * necessary to call {@link ExcludeDevicesOfScanModuleFilter#setScanModule(ScanModule)} 
 * with <code>null</code> argument.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class ExcludeDevicesOfScanModuleFilter extends MeasuringStationFilter {
	private ScanModule scanModule;

	// indicates whether motor axis are excluded
	private final boolean excludeAxes;
	// indicates whether detector channels are excluded
	private final boolean excludeChannels;
	// indicates whether prescans are exclued
	private final boolean excludePrescans;
	// indicates whether postscans are excluded
	private final boolean excludePostscans;
	// indicates whether positionings are excluded
	private final boolean excludePositionsings;
	
	/**
	 * Constructs an <code>ExcludeDevicesOfScanModuleFilter</code>.
	 * 
	 * @param excludeAxes indicates whether motor axis should be excluded
	 * @param excludeChannels indicates whether detector channels should be 
	 * 		  excluded
	 * @param excludePrescans indicates whether prescans should be excluded
	 * @param excludePostscans indicates whether postscans should be excluded
	 * @param excludePositionsings indicates whether positionings should be 
	 * 		  excluded
	 */
	public ExcludeDevicesOfScanModuleFilter(final boolean excludeAxes, 
											final boolean excludeChannels, 
											final boolean excludePrescans, 
											final boolean excludePostscans, 
											final boolean excludePositionsings) {
		super();
		this.excludeAxes = excludeAxes;
		this.excludeChannels = excludeChannels;
		this.excludePrescans = excludePrescans;
		this.excludePostscans = excludePostscans;
		this.excludePositionsings = excludePositionsings;
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
			if (this.excludeList.contains(currentMotor)) {
				continue;
			}
			axisIterator = currentMotor.axisIterator();
			while (axisIterator.hasNext()) {
				currentAxis = axisIterator.next();
				if (this.excludeList.contains(currentAxis)) {
					continue;
				}
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
		
		while( detectorIterator.hasNext() ) {
			Detector currentDetector = detectorIterator.next();
			if( this.excludeList.contains( currentDetector ) ) {
				continue;
			}
			Iterator<DetectorChannel> channelIterator = currentDetector
					.channelIterator();
			while (channelIterator.hasNext()) {
				DetectorChannel currentChannel = channelIterator.next();
				if (this.excludeList.contains(currentChannel)) {
					continue;
				}
				int i = 0;
				for (Iterator<String> iterator = identifier.iterator(); iterator.hasNext();){
					final String test = iterator.next();
					if (currentDetector.getFullIdentifyer().compareToIgnoreCase(test) > 0 ){
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
		while (deviceIterator.hasNext()) {
			Device currentDevice = deviceIterator.next();
			if (this.excludeList.contains(currentDevice)) {
				continue;
			}

			identifier.add(currentDevice.getFullIdentifyer());
		}

		Iterator<Motor> motorIterator = this.motors.iterator();
		while (motorIterator.hasNext()) {
			Motor currentMotor = motorIterator.next();
			if (this.excludeList.contains(currentMotor)) {
				continue;
			}
			Iterator<Option> optionIterator = currentMotor.optionIterator();
			while (optionIterator.hasNext()) {
				Option currentOption = optionIterator.next();
				if (this.excludeList.contains(currentOption)) {
					continue;
				}
				identifier.add(currentOption.getFullIdentifyer());
			}

			Iterator<MotorAxis> axisIterator = currentMotor.axisIterator();
			while (axisIterator.hasNext()) {
				MotorAxis currentAxis = axisIterator.next();
				if (this.excludeList.contains(currentAxis)) {
					continue;
				}
				Iterator<Option> optionIterator2 = currentAxis.getOptions()
						.iterator();
				while (optionIterator2.hasNext()) {
					Option currentOption = optionIterator2.next();
					if (this.excludeList.contains(currentOption)) {
						continue;
					}
					identifier.add(currentOption.getFullIdentifyer());
				}
			}
		}

		Iterator<Detector> detectorIterator = this.detectors.iterator();
		while (detectorIterator.hasNext()) {
			Detector currentDetector = detectorIterator.next();
			if (this.excludeList.contains(currentDetector)) {
				continue;
			}
			Iterator<Option> optionIterator = currentDetector.getOptions()
					.iterator();
			while (optionIterator.hasNext()) {
				Option currentOption = optionIterator.next();
				if (this.excludeList.contains(currentOption)) {
					continue;
				}
				identifier.add(currentOption.getFullIdentifyer());
			}

			Iterator<DetectorChannel> channelsIterator = currentDetector
					.channelIterator();
			while (channelsIterator.hasNext()) {
				DetectorChannel currentChannel = channelsIterator.next();
				if (this.excludeList.contains(currentChannel)) {
					continue;
				}
				Iterator<Option> optionIterator2 = currentChannel.getOptions()
						.iterator();
				while (optionIterator2.hasNext()) {
					Option currentOption = optionIterator2.next();
					identifier.add(currentOption.getFullIdentifyer());
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
			List<AbstractDevice> list = classMap.get(classname);
			Collections.sort(list);
			return list;
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		this.events.clear();
		this.plugins.clear();
		this.devices.clear();
		this.motors.clear();
		this.detectors.clear();
		this.pluginsMap.clear();
		this.motorAxisMap.clear();
		this.detectorChannelsMap.clear();
		this.prePostscanDeviceMap.clear();
		this.classMap.clear();
		this.eventsMap.clear();

		if(this.getSource() != null) {
			this.excludeList.clear();
			if (this.scanModule != null) {
				if (this.excludeAxes) {
					if (this.scanModule.getType().equals(ScanModuleTypes.SAVE_AXIS_POSITIONS)) {
						for (final Motor motor : this.getSource().getMotors()) {
							for (final MotorAxis motorAxis : motor.getAxes()) {
								if (!motorAxis.isSaveValue()) {
									this.excludeList.add(motorAxis);
								}
							}
						}
					}
					for (final Axis axis : this.scanModule.getAxes()) {
						this.excludeList.add(axis.getAbstractDevice());
					}
				}
				if (this.excludeChannels) {
					if (this.scanModule.getType().equals(ScanModuleTypes.SAVE_CHANNEL_VALUES)) {
						for (final Detector detector : this.getSource().getDetectors()) {
							for (final DetectorChannel detChannel : detector.getChannels()) {
								if (!detChannel.isSaveValue()) {
									this.excludeList.add(detChannel);
								}
							}
						}
					}
					for (final Channel channel : this.scanModule.getChannels()) {
						this.excludeList.add(channel.getAbstractDevice());
					}
				}
				if (this.excludePrescans) {
					for (final Prescan prescan : this.scanModule.getPrescans()) {
						this.excludeList.add(prescan.getAbstractDevice());
					}
				}
				if (this.excludePostscans) {
					for (final Postscan postscan : this.scanModule.getPostscans()) {
						this.excludeList.add(postscan.getAbstractDevice());
					}
				}
				if (this.excludePositionsings) {
					for (final Positioning positioning : this.scanModule.getPositionings()) {
						this.excludeList.add(positioning.getMotorAxis());
					}
				}
			}
			
			this.events.addAll(this.getSource().getEvents());
			
			this.plugins.addAll(this.getSource().getPlugins());
			
			this.devices.addAll(this.getSource().getDevices());
			this.devices.removeAll(this.excludeList);
			
			for( final Motor motor : this.getSource().getMotors() ) {
				if( !this.excludeList.contains( motor ) ) {  // wenn exclude List kein Motor enthält weitermachen
					final Motor m = (Motor)motor.clone();	 
					for( final AbstractDevice d : this.excludeList ) {  // Schleife über alle Einträge der excludeList
						if( d instanceof MotorAxis ) {				// ist Eintrag vom Typ MotorAxis
							m.remove( (MotorAxis)d );				// Wenn ja, Axis vom Motor entfernen
						}
					}
					this.motors.add(m);
				}
			}
			
			for (final Detector detector : this.getSource().getDetectors()) {
				if (!this.excludeList.contains(detector)) {
					final Detector m = (Detector) detector.clone();
					for (final AbstractDevice d : this.excludeList) {
						if (d instanceof DetectorChannel) {
							m.remove((DetectorChannel) d);
						}
					}
					this.detectors.add(m);
				}
			}

			for (final PlugIn plugIn : this.plugins) {
				this.pluginsMap.put(plugIn.getName(), plugIn);
			}

			for (final Motor motor : this.motors) {

				for (final Option option : motor.getOptions()) {
					if (!this.excludeList.contains(option)) {
						this.prePostscanDeviceMap.put(option.getID(), option);
					} else {
						motor.remove(option);
					}
				}

				for (final MotorAxis motorAxis : motor.getAxes()) {

					if (!this.excludeList.contains(motorAxis)) {
						this.motorAxisMap.put(motorAxis.getID(), motorAxis);
						for (final Option option : motorAxis.getOptions()) {
							if (!this.excludeList.contains(option)) {
								this.prePostscanDeviceMap.put(option.getID(),
										option);
							} else {
								motorAxis.remove(option);
							}
						}
					}
				}
			}

			for (final Detector detector : this.detectors) {
				for (final Option option : detector.getOptions()) {
					if (!this.excludeList.contains(option)) {
						this.prePostscanDeviceMap.put(option.getID(), option);
					} else {
						detector.remove(option);
					}
				}
				for (final DetectorChannel detectorChannel : detector
						.getChannels()) {
					if (!this.excludeList.contains(detectorChannel)) {
						this.detectorChannelsMap.put(detectorChannel.getID(),
								detectorChannel);
						for (final Option option : detectorChannel.getOptions()) {
							if (!this.excludeList.contains(option)) {
								this.prePostscanDeviceMap.put(option.getID(),
										option);
							} else {
								detectorChannel.remove(option);
							}
						}
					}
				}
			}

			for (final Device device : this.devices) {
				this.prePostscanDeviceMap.put(device.getID(), device);
			}

			buildClassMap();

			for (final Event event : this.events) {
				this.eventsMap.put(event.getId(), event);
			}

		}

		for (final IModelUpdateListener modelUpdateListener : this.modelUpdateListener) {
			modelUpdateListener.updateEvent(new ModelUpdateEvent(this, null));
		}
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule} 
	 * related to this filter.
	 * <p>
	 * If this Filter is not used anymore (should be Garbage Collected) it is 
	 * necessary to call this method with <code>null</code> argument.
	 * 
	 * @param scanModule the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.ScanModule} related 
	 * 		  to this filter
	 */
	public void setScanModule(final ScanModule scanModule) {
		if(this.scanModule != null) {
			this.scanModule.removeModelUpdateListener(this);
		}
		this.scanModule = scanModule;
		if(this.scanModule != null) {
			this.scanModule.addModelUpdateListener(this);
		}
		this.updateEvent(new ModelUpdateEvent(this, null));
	}

	/*
	 * 
	 */
	private void buildClassMap() {
		this.classMap.clear();

		for (final Motor motor : this.motors) {
			if (motor.getClassName() != null
					&& !motor.getClassName().isEmpty()) {
				List<AbstractDevice> devices = null;
				if (this.classMap.containsKey(motor.getClassName())) {
					devices = this.classMap.get(motor.getClassName());
				} else {
					devices = new ArrayList<AbstractDevice>();
					this.classMap.put(motor.getClassName(), devices);
				}
				devices.add(motor);
			}
			for (final MotorAxis motorAxis : motor.getAxes()) {
				List<AbstractDevice> devices = null;
				if (motorAxis.getClassName() != null
						&& !motorAxis.getClassName().isEmpty()) {
					if (this.classMap.containsKey(motorAxis.getClassName())) {
						devices = this.classMap.get(motorAxis.getClassName());
					} else {
						devices = new ArrayList<AbstractDevice>();
						this.classMap.put(motorAxis.getClassName(), devices);
					}
					devices.add(motorAxis);
				}
			}
		}

		for (final Detector detector : this.detectors) {
			if (detector.getClassName() != null
					&& !detector.getClassName().isEmpty()) {
				List<AbstractDevice> devices = null;
				if (this.classMap.containsKey(detector.getClassName())) {
					devices = this.classMap.get(detector.getClassName());
				} else {
					devices = new ArrayList<AbstractDevice>();
					this.classMap.put(detector.getClassName(), devices);
				}
				devices.add(detector);
			}
			for (final DetectorChannel detectorChannel : detector.getChannels()) {
				List<AbstractDevice> devices = null;
				if (detectorChannel.getClassName() != null
						&& !detectorChannel.getClassName().isEmpty()) {
					if (this.classMap.containsKey(detectorChannel
							.getClassName())) {
						devices = this.classMap.get(detectorChannel
								.getClassName());
					} else {
						devices = new ArrayList<AbstractDevice>();
						this.classMap.put(detectorChannel.getClassName(),
								devices);
					}
					devices.add(detectorChannel);
				}
			}
		}

		for (final Device device : this.devices) {
			if (device.getClassName() != null
					&& !device.getClassName().isEmpty()) {
				List<AbstractDevice> devices = null;
				if (this.classMap.containsKey(device.getClassName())) {
					devices = this.classMap.get(device.getClassName());
				} else {
					devices = new ArrayList<AbstractDevice>();
					this.classMap.put(device.getClassName(), devices);
				}
				devices.add(device);
			}
		}
	}
}