package de.ptb.epics.eve.data.scandescription.defaults;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.Duration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.measuringstation.event.MonitorEvent;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.axismode.RangeMode;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.defaults.axis.DefaultsAxis;
import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsChannel;
import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsChannelModeInterval;
import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsChannelModeStandard;
import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsControlEvent;
import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsDetectorEvent;
import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsMonitorEvent;
import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsRedoEvent;
import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsScheduleEvent;
import de.ptb.epics.eve.data.scandescription.defaults.updater.DefaultsUpdater;
import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;
import de.ptb.epics.eve.data.scandescription.updater.VersionTooNewException;
import de.ptb.epics.eve.util.data.Version;
import de.ptb.epics.eve.util.io.*;

/**
 * Tries to read a given XML file (as defined in 
 * de.ptb.epics.eve.resources/cfg/defaults.xsd) for default values.
 * Values for individual devices can later be requested via 
 * {@link #getAxis(String)} and {@link #getChannel(String)}.
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultsManager {
	private static final Logger LOGGER = Logger.getLogger(DefaultsManager.class
			.getName());
	
	private Defaults defaults;
	private boolean initialized;
	
	/** */
	public DefaultsManager() {
		this.defaults = null;
		this.initialized = false;
	}
	
	/**
	 * @param pathToDefaults the location of the defaults file
	 * @param schema the schema file
	 */
	public void init(File pathToDefaults, File schema) {
		if (!pathToDefaults.exists()) {
			LOGGER.debug("no defaults file found.");
			defaults = new Defaults();
			return;
		}
		
		this.backup(pathToDefaults);
		
		Document updated = this.update(pathToDefaults, schema);
		
		SchemaFactory schemaFactory = SchemaFactory.newInstance(
				XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schemaFile = schemaFactory.newSchema(schema);
			JAXBContext jaxbContext = JAXBContext.newInstance(Defaults.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setSchema(schemaFile);
			this.defaults = (Defaults) jaxbUnmarshaller
					.unmarshal(updated);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("found defaults: \n" + this.defaults.toString());
			} else {
				LOGGER.info("found defaults");
			}
			this.initialized = true;
			// TODO save if update was necessary
		} catch(JAXBException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void backup (File file) {
		File backup = new File(file.getParent() + "/"
				+ file.getName() + ".old");
		try {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("save copy before updating defaults");
			}
			FileUtil.copyFile(file, backup);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private Document update(File file, File schema) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			List<Patch> patches = new DefaultsUpdater().update(document, 
					getCurrentDefaultsSchemaVersion(schema));
			if (LOGGER.isInfoEnabled()) {
				if (patches.isEmpty()) {
					LOGGER.info("defaults up to date, no patches applied.");
				} else {
					for (Patch patch : patches) {
						LOGGER.info("applied " + patch.toString());
						for (Modification modification : patch.getModifications()) {
							LOGGER.info(modification.getChangeLog());
						}
					}
				}
			}
			return document;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (VersionTooNewException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	private Version getCurrentDefaultsSchemaVersion(File schema) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(schema);
			
			Node node = document.getElementsByTagName("xsd:schema").item(0);
			String versionString = node.getAttributes().getNamedItem("version")
					.getNodeValue();
			return new Version(
					Integer.parseInt(versionString.split("\\.")[0]), 
					Integer.parseInt(versionString.split("\\.")[1]));
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Returns whether the manager is initialized.
	 * 
	 * @return whether the manager is initialized
	 */
	public boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * ... {@link #isInitialized()} should be checked before.
	 * 
	 * @param targetFile the destination
	 * @param schemaFile the schema file
	 */
	public synchronized void save(File targetFile, File schemaFile) {
		File backup = new File(targetFile.getParent() + "/"
				+ targetFile.getName() + ".bup");
		if (targetFile.exists()) {
			try {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("target file already exists, creating backup");
				}
				FileUtil.copyFile(targetFile, backup);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
		this.defaults.sort();
		
		SchemaFactory schemaFactory = SchemaFactory.newInstance(
				XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Defaults.class);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setSchema(schema);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(this.defaults, targetFile);
			LOGGER.info("defaults saved");
			LOGGER.info("creating backup");
			FileUtil.copyFile(targetFile, backup);
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			try {
				LOGGER.info("saving defaults failed -> restoring backup.");
				FileUtil.copyFile(backup, targetFile);
			} catch (IOException e1) {
				LOGGER.error("restoring backup has failed.");
			}
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
			try {
				LOGGER.info("saving defaults failed -> restoring backup.");
				FileUtil.copyFile(backup, targetFile);
			} catch (IOException e1) {
				LOGGER.error("restoring backup has failed.");
			}
		} catch (IOException e) {
			LOGGER.error("creating backup has failed.");
		}
	}
	
	/**
	 * 
	 * @return
	 * @since 1.22
	 */
	public File getWorkingDirectory() {
		return new File(this.defaults.getWorkingDirectory());
	}
	
	/**
	 * 
	 * @since 1.22
	 */
	public void setWorkingDirectory(File workingDirectory) {
		this.defaults.setWorkingDirectory(workingDirectory.getPath());
	}
	
	/**
	 * Returns the axis with the given id or <code>null</code> if not found.
	 * 
	 * @param id the id of the axis to get defaults for
	 * @return the axis with the given id or <code>null</code> if not found.
	 */
	public DefaultsAxis getAxis(String id) {
		if (this.defaults == null) {
			return null;
		}
		for (DefaultsAxis axis : this.defaults.getAxes()) {
			if (axis.getId().equals(id)) {
				return axis;
			}
		}
		return null;
	}
	
	/**
	 * Returns the channel with the given id or <code>null</code> if not found.
	 * 
	 * @param id the id of the channel to get defaults for
	 * @return the channel with the given id or <code>null</code> if not found.
	 */
	public DefaultsChannel getChannel(String id) {
		if (this.defaults == null) {
			return null;
		}
		for (DefaultsChannel channel : this.defaults.getChannels()) {
			if (channel.getId().equals(id)) {
				return channel;
			}
		}
		return null;
	}
	
	/**
	 * Transfers the properties from the given default channel to the 
	 * target channel.
	 * If the default channel is normalized and the used normalize channel 
	 * is not part of the scan module then it is added.
	 * 
	 * @param from the source values (defaults)
	 * @param to the target channel
	 * @param measuringStation the device definition
	 */
	public static void transferDefaults(DefaultsChannel from, Channel to) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("transfering defaults for "
					+ to.getAbstractDevice().getID());
		}
		
		if (from.getNormalizeId() != null) {
			DetectorChannel normalizeChannel = to.getScanModule().getChain().
					getScanDescription().getMeasuringStation().
					getDetectorChannelById(from.getNormalizeId());
			if (normalizeChannel != null) {
				boolean found = false;
				for (Channel channel : to.getScanModule().getChannels()) {
					if (normalizeChannel.getID().equals(
							channel.getDetectorChannel().getID())) {
						found = true;
					}
				}
				if (!found) {
					to.getScanModule().add(
							new Channel(to.getScanModule(), normalizeChannel));
				}
				to.setNormalizeChannel(to.getScanModule().getChain()
						.getScanDescription().getMeasuringStation()
						.getDetectorChannelById(from.getNormalizeId()));
			} else {
				LOGGER.error("Normalize channel of channel '" + from.getId() + 
					"' saved in defaults not found in current device definition.");
			}
		}
		if (from.getMode() instanceof DefaultsChannelModeStandard) {
			DefaultsChannelModeStandard mode = (DefaultsChannelModeStandard)from.getMode();
			to.setChannelMode(ChannelModes.STANDARD);
			to.setAverageCount(mode.getAverageCount());
			if (mode.getMaxAttempts() != null) {
				to.setMaxAttempts(mode.getMaxAttempts());
			}
			if (mode.getMaxDeviation() != null) {
				to.setMaxDeviation(mode.getMaxDeviation());
			}
			if (mode.getMinimum() != null) {
				to.setMinimum(mode.getMinimum());
			}
			for (DefaultsRedoEvent redoEvent : mode.getRedoEvents()) {
				DefaultsControlEvent defaultsControlEvent = redoEvent
						.getControlEvent();
				if (defaultsControlEvent instanceof DefaultsDetectorEvent) {
					DefaultsDetectorEvent detectorEvent = 
							((DefaultsDetectorEvent) defaultsControlEvent);
					ScanModule sm;
					try {
						sm = to.getScanModule().getChain().getScanDescription()
								.getChain(detectorEvent.getChainId())
								.getScanModuleById(detectorEvent.getScanModuleId());
					} catch (NullPointerException e) {
						LOGGER.error("corresponding chain oder sm of detector event "
								+ detectorEvent.getId() + " could not be found.");
						continue;
					}
					Channel eventChannel = null;
					for (Channel channel : sm.getChannels()) {
						if (channel.getAbstractDevice().getID().equals(detectorEvent.getDetectorId())) {
							eventChannel = channel;
						}
					}
					if (eventChannel != null) {
						to.addRedoEvent(new ControlEvent(EventTypes.DETECTOR,
								new DetectorEvent(eventChannel), detectorEvent
										.getId()));
					} else {
						LOGGER.error("channel of detector event "
								+ detectorEvent.getId() + " could not be found.");
					}
				} else if (defaultsControlEvent instanceof DefaultsMonitorEvent) {
					DefaultsMonitorEvent monitorEvent = 
							(DefaultsMonitorEvent) defaultsControlEvent;
					Event event = to.getScanModule().getChain().getScanDescription()
							.getMeasuringStation()
							.getEventById(monitorEvent.getId());
					if (event == null) {
						LOGGER.error("device of monitor event "
								+ monitorEvent.getId() + " could not be found.");
						continue;
					}
					ControlEvent controlEvent = new ControlEvent(
							EventTypes.MONITOR, event, event.getId());
					controlEvent.getLimit().setComparison(
							monitorEvent.getLimit().getComparison());
					controlEvent.getLimit().setValue(
							monitorEvent.getLimit().getValue());
					to.addRedoEvent(controlEvent);
				} else if (defaultsControlEvent instanceof DefaultsScheduleEvent) {
					DefaultsScheduleEvent scheduleEvent = 
							(DefaultsScheduleEvent) defaultsControlEvent;
					ScanModule sm = null;
					try {
						sm = to.getScanModule().getChain().getScanDescription()
								.getChain(scheduleEvent.getChainId())
								.getScanModuleById(scheduleEvent.getScanModuleId());
					} catch (NullPointerException e) {
						LOGGER.error("scan module of schedule event "
								+ scheduleEvent.toString() + " could not be found.");
						continue;
					}
					to.addRedoEvent(new ControlEvent(EventTypes.SCHEDULE,
							new ScheduleEvent(sm), scheduleEvent.getEventId()));
				}
			}
			to.setDeferred(mode.isDeferred());
		} else if (from.getMode() instanceof DefaultsChannelModeInterval) {
			DefaultsChannelModeInterval mode = (DefaultsChannelModeInterval)from.getMode();
			to.setChannelMode(ChannelModes.INTERVAL);
			to.setTriggerInterval(mode.getTriggerInterval());
			if (mode.getStoppedBy() != null) {
				DetectorChannel detectorChannel = to.getScanModule().getChain().getScanDescription().
						getMeasuringStation().getDetectorChannelById(mode.getStoppedBy());
				if (detectorChannel != null) {
					boolean found = false;
					for (Channel channel : to.getScanModule().getChannels()) {
						if (detectorChannel.getID().equals(
								channel.getDetectorChannel().getID())) {
							found = true;
						}
					}
					if (!found) {
						to.getScanModule().add(
								new Channel(to.getScanModule(), detectorChannel));
					}
					to.setStoppedBy(detectorChannel);
				} else {
					LOGGER.error("channel used as stoppedby could not be found.");
				}
			}
		}
		
	}
	
	/**
	 * Transfers the properties from the given default axis to the 
	 * target axis.
	 * 
	 * @param from the source values (defaults)
	 * @param to the target axis
	 */
	public static void transferDefaults(DefaultsAxis from, Axis to) {
		to.setStepfunction(from.getStepfunction());
		to.setPositionMode(from.getPositionmode());
		switch (from.getStepfunction()) {
		case ADD:
		case MULTIPLY:
			// to.setMainAxis(from.isMainAxis());
			switch (to.getMotorAxis().getType()) {
			case DATETIME:
				if (to.getPositionMode().equals(PositionMode.ABSOLUTE)) {
					to.setStart((Date)from.getStart());
					to.setStop((Date)from.getStop());
					to.setStepwidth((Date)from.getStepwidth());
				} else {
					to.setStart((Duration)from.getStart());
					to.setStop((Duration)from.getStop());
					to.setStepwidth((Duration)from.getStepwidth());
				}
				break;
			case DOUBLE:
				to.setStart((Double)from.getStart());
				to.setStop((Double)from.getStop());
				to.setStepwidth((Double)from.getStepwidth());
				break;
			case INT:
				to.setStart((Integer)from.getStart());
				to.setStop((Integer)from.getStop());
				to.setStepwidth((Integer)from.getStepwidth());
				break;
			default:
				break;
			}
			break;
		case PLUGIN:
			/*
			 * plugin modifications have to be made elsewhere
			 * (not when the device is created, but when a plugin is selected)
			PluginController controller = to.getPluginController();
			for (DefaultsAxisPluginParameter param : from.getPlugin()
					.getParameters()) {
				if (controller.get(param.getName()) != null) {
					controller.set(param.getName(), param.getValue());
				}
			}
			*/
			// TODO
			break;
		case FILE:
			to.setFile(new File(from.getFile()));
			break;
		case POSITIONLIST:
			to.setPositionlist(from.getPositionList());
			break;
		case RANGE:
			to.setRange(from.getExpression());
			break;
		}
	}
	
	/**
	 * Returns a 
	 * {@link de.ptb.epics.eve.data.scandescription.defaults.axis.DefaultsAxis} for 
	 * the given {@link de.ptb.epics.eve.data.scandescription.Axis}.
	 * 
	 * @param axis the axis to convert
	 * @return the converted defaults axis
	 */
	public static DefaultsAxis getDefaultsAxis(Axis axis) {
		DefaultsAxis defaultsAxis = new DefaultsAxis();
		defaultsAxis.setId(axis.getMotorAxis().getID());
		defaultsAxis.setPositionmode(axis.getPositionMode());
		defaultsAxis.setStepfunction(axis.getStepfunction());
		switch(axis.getStepfunction()) {
		case ADD:
		case MULTIPLY:
			defaultsAxis.setMainAxis(axis.isMainAxis());
			switch(axis.getType()) {
			case DATETIME:
				if (axis.getPositionMode().equals(PositionMode.ABSOLUTE)) {
					defaultsAxis.setStartStopStepType(DataTypes.DATETIME);
					defaultsAxis.setStart((Date) axis.getStart());
					defaultsAxis.setStop((Date) axis.getStop());
					defaultsAxis.setStepwidth((Date) axis.getStepwidth());
				} else {
					defaultsAxis.setStartStopStepType(DataTypes.DATETIME);
					defaultsAxis.setStart((Duration)axis.getStart());
					defaultsAxis.setStop((Duration)axis.getStop());
					defaultsAxis.setStepwidth((Duration)axis.getStepwidth());
				}
				break;
			case DOUBLE:
				defaultsAxis.setStartStopStepType(DataTypes.DOUBLE);
				defaultsAxis.setStart((Double)axis.getStart());
				defaultsAxis.setStop((Double)axis.getStop());
				defaultsAxis.setStepwidth((Double)axis.getStepwidth());
				break;
			case INT:
				defaultsAxis.setStartStopStepType(DataTypes.INT);
				defaultsAxis.setStart((Integer)axis.getStart());
				defaultsAxis.setStop((Integer)axis.getStop());
				defaultsAxis.setStepwidth((Integer)axis.getStepwidth());
				break;
			default:
				break;
			}
			break;
		case FILE:
			defaultsAxis.setFile(axis.getFile().getAbsolutePath());
			break;
		case PLUGIN:
			// TODO
			break;
		case POSITIONLIST:
			defaultsAxis.setPositionList(axis.getPositionlist());
			break;
		case RANGE:
			defaultsAxis.setExpression(axis.getRange());
			defaultsAxis.setPositionList(((RangeMode)axis.getMode()).getPositions());
			break;
		}
		return defaultsAxis;
	}
	
	/**
	 * Returns a 
	 * {@link de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsChannel} 
	 * for the given 
	 * {@link de.ptb.epics.eve.data.scandescription.Channel}.
	 * 
	 * @param channel the channel to convert
	 * @return the converted defaults channel
	 */
	public static DefaultsChannel getDefaultsChannel(Channel channel) {
		DefaultsChannel defaultsChannel = new DefaultsChannel();
		defaultsChannel.setId(channel.getDetectorChannel().getID());
		if (channel.getNormalizeChannel() != null) {
			defaultsChannel.setNormalizeId(channel.getNormalizeChannel().getID());
		}
		switch (channel.getChannelMode()) {
		case STANDARD:
			DefaultsChannelModeStandard standardMode = new DefaultsChannelModeStandard();
			if (channel.getAverageCount() != 0) {
				standardMode.setAverageCount(channel.getAverageCount());
			}
			if (channel.getMaxAttempts() != null) {
				standardMode.setMaxAttempts(channel.getMaxAttempts());
			}
			if (channel.getMinimum() != null) {
				standardMode.setMinimum(channel.getMinimum());
			}
			if (channel.getMaxDeviation() != null) {
				standardMode.setMaxDeviation(channel.getMaxDeviation());
			}
			for (ControlEvent event : channel.getRedoEvents()) {
				switch (event.getEventType()) {
				case DETECTOR:
					DefaultsDetectorEvent defaultsDetectorEvent = 
							new DefaultsDetectorEvent();
					defaultsDetectorEvent.setId(((DetectorEvent) (event.getEvent()))
							.getId());
					standardMode.getRedoEvents().add(
							new DefaultsRedoEvent(defaultsDetectorEvent));
					break;
				case MONITOR:
					DefaultsMonitorEvent defaultsMonitorEvent = 
							new DefaultsMonitorEvent();
					MonitorEvent monitorEvent = (MonitorEvent) event.getEvent();
					defaultsMonitorEvent.setId(monitorEvent.getId());
					defaultsMonitorEvent.setLimit(event.getLimit());
					standardMode.getRedoEvents().add(
							new DefaultsRedoEvent(defaultsMonitorEvent));
					break;
				case SCHEDULE:
					DefaultsScheduleEvent defaultsScheduleEvent = 
							new DefaultsScheduleEvent();
					ScheduleEvent scheduleEvent = (ScheduleEvent) event.getEvent();
					defaultsScheduleEvent.setChainId(scheduleEvent.getScanModule()
							.getChain().getId());
					defaultsScheduleEvent.setScanModuleId(scheduleEvent
							.getScanModule().getId());
					defaultsScheduleEvent.setScheduleTime(scheduleEvent
							.getScheduleTime());
					standardMode.getRedoEvents().add(
							new DefaultsRedoEvent(defaultsScheduleEvent));
					break;
				}
			}
			standardMode.setDeferred(channel.isDeferred());
			defaultsChannel.setMode(standardMode);
			break;
		case INTERVAL:
			DefaultsChannelModeInterval intervalMode = new DefaultsChannelModeInterval();
			intervalMode.setTriggerInterval(channel.getTriggerInterval());
			if (channel.getStoppedBy() != null) {
				intervalMode.setStoppedBy(channel.getStoppedBy().getID());
			}
			defaultsChannel.setMode(intervalMode);
			break;
		}
		
		
		return defaultsChannel;
	}
	
	/**
	 * Updates the defaults of channels and axes contained in the given 
	 * scan description.
	 * 
	 * @param scanDescription the scan description containing axes and channels 
	 * 		that should be updated
	 */
	public synchronized void update(ScanDescription scanDescription) {
		if (!isInitialized()) {
			this.defaults = new Defaults();
			this.initialized = true;
		}
		for (Chain ch : scanDescription.getChains()) {
			for (ScanModule sm : ch.getScanModules()) {
				if (!sm.getType().equals(ScanModuleTypes.CLASSIC)) {
					LOGGER.debug("omitting scan module of type " + sm.getType());
					continue;
				}
				for (Axis axis : sm.getAxes()) {
					this.defaults.updateAxis(DefaultsManager
							.getDefaultsAxis(axis));
				}
				for (Channel channel : sm.getChannels()) {
					 this.defaults.updateChannel(DefaultsManager
							.getDefaultsChannel(channel));
				}
			}
		}
	}
}