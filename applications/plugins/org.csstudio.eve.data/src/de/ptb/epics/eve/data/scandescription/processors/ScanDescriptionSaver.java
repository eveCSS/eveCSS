package de.ptb.epics.eve.data.scandescription.processors;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import javax.xml.datatype.Duration;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.swt.graphics.RGB;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.PluginDataType;
import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.MethodTypes;
import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.PluginParameter;
import de.ptb.epics.eve.data.measuringstation.Access;
import de.ptb.epics.eve.data.measuringstation.Selections;
import de.ptb.epics.eve.data.measuringstation.Unit;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.data.scandescription.PositionMode;

/**
 * This class saves a scan description to a file.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class ScanDescriptionSaver implements
		IScanDescriptionSaver {
	private static Logger logger = Logger
			.getLogger(ScanDescriptionSaver.class.getName());
	
	private IMeasuringStation measuringStation;
	private ScanDescription scanDescription;

	private String version;
	
	/* 
	 * An output stream to the destination where the scan description should be
	 * written to.
	 */
	private OutputStream destination;

	private ContentHandler contentHandler;

	// The XML attributes.
	private AttributesImpl atts;

	// TODO: use non-deprecated API
	private XMLSerializer serializer;

	/**
	 * This constructor create a new saver.
	 * 
	 * @param destination
	 *            The output stream to the destination.
	 * @param measuringStation
	 *            The measuring station description.
	 * @param scanDescription
	 *            The scan description.
	 */
	public ScanDescriptionSaver(final OutputStream destination,
			final IMeasuringStation measuringStation,
			final ScanDescription scanDescription) {
		if (measuringStation == null) {
			throw new IllegalArgumentException(
					"The parameter 'measuringStation' must not be null!");
		}
		this.destination = destination;
		this.measuringStation = measuringStation;
		this.scanDescription = scanDescription;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMeasuringStation getMeasuringStationDescription() {
		return this.measuringStation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScanDescription getScanDescription() {
		return this.scanDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMeasuringStationDescription(
			final IMeasuringStation measuringStation) {
		if (measuringStation == null) {
			throw new IllegalArgumentException(
					"The parameter 'measuringStation' must not be null!");
		}
		this.measuringStation = measuringStation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScanDescription(final ScanDescription scanDescription) {
		if (scanDescription == null) {
			throw new IllegalArgumentException(
					"The parameter 'scanDescription' must not be null!");
		}
		this.scanDescription = scanDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean save() {
		boolean successful = true;
		this.atts = new AttributesImpl();
		OutputFormat outputFormat = new OutputFormat("XML", "UTF-8", true);
		outputFormat.setIndent(1);
		outputFormat.setIndenting(true);

		// Die Versuche mit dem Setzen von Elemente welche dann nicht in z.B.
		// &gt;
		// umgewandelt werden, hatten keinen Erfolg. Warum auch immer.
		// String[] noEscape = {"value", "prescan"};
		// outputFormat.setNonEscapingElements(noEscape);
		// Auch die Versuche mit serializer.startNonEscaping(); sind
		// fehlgeschlagen
		// Hartmut Scherr, 10.11.10

		serializer = new XMLSerializer(this.destination, outputFormat);

		this.contentHandler = null;

		try {
			this.contentHandler = serializer.asContentHandler();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			successful = false;
		}

		try {
			this.contentHandler.startDocument();

			this.atts.clear();
			this.atts.addAttribute("xmlns", "tns", "xmlns:tns", Literals.CHARACTER_DATA,
					"http://www.ptb.de/epics/SCML");
			this.atts.addAttribute("xmlns", "xsi", "xmlns:xsi", Literals.CHARACTER_DATA,
					"http://www.w3.org/2001/XMLSchema-instance");
			this.atts.addAttribute("xsi", "schemaLocation",
					"xsi:schemaLocation", Literals.CHARACTER_DATA,
					"http://www.ptb.de/epics/SCML scml.xsd");

			this.contentHandler.startElement("tns", "scml", "tns:scml", atts);

			if (this.scanDescription.getMeasuringStation().getName() != null
					&& !this.scanDescription.getMeasuringStation().getName()
							.isEmpty()) {
				this.atts.clear();
				this.contentHandler.startElement("", "", Literals.XML_ELEMENT_NAME_LOCATION, atts);
				this.contentHandler.characters(this.scanDescription
						.getMeasuringStation().getName().toCharArray(), 0,
						this.scanDescription.getMeasuringStation().getName()
								.toCharArray().length);
				this.contentHandler.endElement("", "", Literals.XML_ELEMENT_NAME_LOCATION);
			}

			this.atts.clear();
			this.contentHandler.startElement("", "", "version", atts);
			this.contentHandler.characters(this.version.toCharArray(), 0,
					this.version.length());
			this.contentHandler.endElement("", "", "version");
			
			this.atts.clear();
			this.contentHandler.startElement("", "", "scan", atts);

			this.atts.clear();
			this.contentHandler.startElement("", "", "repeatcount", atts);
			this.contentHandler.characters(
					("" + this.scanDescription.getRepeatCount()).toCharArray(),
					0, ("" + this.scanDescription.getRepeatCount()).length());
			this.contentHandler.endElement("", "", "repeatcount");

			for(Chain chain : this.scanDescription.getChains()) {
				this.writeChain(chain);
			}

			successful = this.writeMonitorDevices();
			
			this.contentHandler.endElement("", "", "scan");
			
			successful = this.writePlugins();
			successful = this.writeDetectors();
			successful = this.writeMotors();
			successful = this.writeDevices();
			successful = this.writeSelections(this.measuringStation.getSelections());

			this.contentHandler.endElement("tns", "scml", "tns:scml");
			this.contentHandler.endDocument();
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			successful = false;
		}
		return successful;
	}

	/**
	 * Returns whether writing was successful.
	 * 
	 * @return whether writing was successful
	 */
	private boolean writeMonitorDevices() {
		boolean successful = true;
		try {
			this.atts.clear();
			this.atts.addAttribute("", Literals.XML_ATTRIBUTE_NAME_TYPE, Literals.XML_ATTRIBUTE_NAME_TYPE, Literals.CHARACTER_DATA,
					"" + scanDescription.getMonitorOption().toString());
			this.contentHandler.startElement("", "monitoroptions",
					"monitoroptions", this.atts);
								
			for (Option o : this.scanDescription.getMonitors()) {
				successful = this.writeId(o.getID());
			}
			
			this.contentHandler.endElement("", "monitordevices",
					"monitordevices");
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return successful;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private boolean writeId(String id) {
		this.atts.clear();
		try {
			this.contentHandler.startElement("", "id", "id", this.atts);
			this.contentHandler.characters(id.toCharArray(), 0, id.length());
			this.contentHandler.endElement("", "id", "id");
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	/**
	 * This method writes all devices of the current measuring station.
	 * 
	 * @return Returns if the write process was successful.
	 */
	private boolean writeDevices() {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_DEVICES, Literals.XML_ELEMENT_NAME_DEVICES,
					this.atts);
			for(Device device : this.measuringStation.getDevices()) {
				this.writeDevice(device);
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_DEVICES, Literals.XML_ELEMENT_NAME_DEVICES);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a device.
	 * 
	 * @param device
	 *            The device to write.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeDevice(final Device device) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_DEVICE, Literals.XML_ELEMENT_NAME_DEVICE, this.atts);
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_CLASS, Literals.XML_ELEMENT_NAME_CLASS, this.atts);
			if (device.getClassName() != null) {
				this.contentHandler.characters(device.getClassName()
						.toCharArray(), 0, device.getClassName().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_CLASS, Literals.XML_ELEMENT_NAME_CLASS);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME, this.atts);
			if (device.getName() != null) {
				this.contentHandler.characters(device.getName().toCharArray(),
						0, device.getName().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME);

			this.atts.clear();
			this.contentHandler.startElement("", "id", "id", this.atts);
			this.contentHandler.characters(device.getID().toCharArray(), 0,
					device.getID().length());
			this.contentHandler.endElement("", "id", "id");

			if (device.getValue() != null) {
				this.writeFunction(device.getValue(), "value");
			}
			if (device.getUnit() != null) {
				this.writeUnit(device.getUnit());
			}

			if (device.getDisplaygroup() != null) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_DISPLAYGROUP,
						Literals.XML_ELEMENT_NAME_DISPLAYGROUP, this.atts);
				this.contentHandler.characters(device.getDisplaygroup()
						.toString().toCharArray(), 0, device.getDisplaygroup()
						.toString().length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_DISPLAYGROUP,
						Literals.XML_ELEMENT_NAME_DISPLAYGROUP);
			}

			for(Option o : device.getOptions()) {
				this.writeOption(o);
			}

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_DEVICE, Literals.XML_ELEMENT_NAME_DEVICE);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a motors of a measuring station.
	 * 
	 * @return Returns if the write process was was successful.
	 */
	private boolean writeMotors() {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_MOTORS, Literals.XML_ELEMENT_NAME_MOTORS, this.atts);
			for(Motor motor : this.measuringStation.getMotors()) {
				this.writeMotor(motor);
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_MOTORS, Literals.XML_ELEMENT_NAME_MOTORS);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a motor.
	 * 
	 * @param motor
	 *            The motor to write.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeMotor(final Motor motor) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_MOTOR, Literals.XML_ELEMENT_NAME_MOTOR, this.atts);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_CLASS, Literals.XML_ELEMENT_NAME_CLASS, this.atts);
			if (motor.getClassName() != null) {
				this.contentHandler.characters(motor.getClassName()
						.toCharArray(), 0, motor.getClassName().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_CLASS, Literals.XML_ELEMENT_NAME_CLASS);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME, this.atts);
			if (motor.getName() != null) {
				this.contentHandler.characters(motor.getName().toCharArray(),
						0, motor.getName().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME);

			this.atts.clear();
			this.contentHandler.startElement("", "id", "id", this.atts);
			this.contentHandler.characters(motor.getID().toCharArray(), 0,
					motor.getID().length());
			this.contentHandler.endElement("", "id", "id");

			if (motor.getTrigger() != null) {
				this.writeFunction(motor.getTrigger(), "trigger");
			}
			if (motor.getUnit() != null) {
				this.writeUnit(motor.getUnit());
			}

			for(MotorAxis ma : motor.getAxes()) {
				this.writeMotorAxis(ma);
			}

			for(Option o : motor.getOptions()) {
				this.writeOption(o);
			}

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_MOTOR, Literals.XML_ELEMENT_NAME_MOTOR);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a motor axis.
	 * 
	 * @param axis
	 *            The motor axis to write.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeMotorAxis(final MotorAxis axis) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_AXIS, Literals.XML_ELEMENT_NAME_AXIS, this.atts);
			
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_CLASS, Literals.XML_ELEMENT_NAME_CLASS, this.atts);
			if (axis.getClassName() != null) {
				this.contentHandler.characters(axis.getClassName()
						.toCharArray(), 0, axis.getClassName().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_CLASS, Literals.XML_ELEMENT_NAME_CLASS);

			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME, this.atts);
			if (axis.getName() != null) {
				this.contentHandler.characters(axis.getName().toCharArray(), 0,
						axis.getName().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME);

			this.atts.clear();
			this.contentHandler.startElement("", "id", "id", this.atts);
			this.contentHandler.characters(axis.getID().toCharArray(), 0, axis
					.getID().length());
			this.contentHandler.endElement("", "id", "id");

			if (axis.getGoto() != null) {
				this.writeFunction(axis.getGoto(), "goto");
			}
			if (axis.getPosition() != null) {
				this.writeFunction(axis.getPosition(), "position");
			}
			if (axis.getStop() != null) {
				this.writeFunction(axis.getStop(), "stop");
			}
			if (axis.getSoftHighLimit() != null) {
				this.writeFunction(axis.getSoftHighLimit(), "highlimit");
			}
			if (axis.getSoftLowLimit() != null) {
				this.writeFunction(axis.getSoftLowLimit(), "lowlimit");
			}
			if (axis.getStatus() != null) {
				this.writeFunction(axis.getStatus(), "status");
			}
			if (axis.getMoveDone() != null) {
				this.writeFunction(axis.getMoveDone(), "movedone");
			}
			if (axis.getTrigger() != null) {
				this.writeFunction(axis.getTrigger(), "trigger");
			}
			if (axis.getUnit() != null) {
				this.writeUnit(axis.getUnit());
			}
			if (axis.getDeadband() != null) {
				this.writeFunction(axis.getDeadband(), "deadband");
			}
			if (axis.getOffset() != null) {
				this.writeFunction(axis.getOffset(), "offset");
			}
			if (axis.getTweakValue() != null) {
				this.writeFunction(axis.getTweakValue(), "tweakvalue");
			}
			if (axis.getTweakForward() != null) {
				this.writeFunction(axis.getTweakForward(), "tweakforward");
			}
			if (axis.getTweakReverse() != null) {
				this.writeFunction(axis.getTweakReverse(), "tweakreverse");
			}

			for(Option o : axis.getOptions()) {
				this.writeOption(o);
			}

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_AXIS, Literals.XML_ELEMENT_NAME_AXIS);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes all detectors.
	 * 
	 * @return Returns if the write process was successful.
	 */
	private boolean writeDetectors() {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_DETECTORS, Literals.XML_ELEMENT_NAME_DETECTORS,
					this.atts);
			for(Detector detector : this.measuringStation.getDetectors()) {
				this.writeDetector(detector);
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_DETECTORS, Literals.XML_ELEMENT_NAME_DETECTORS);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a detector.
	 * 
	 * @param detector
	 *            The detector that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeDetector(final Detector detector) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_DETECTOR, Literals.XML_ELEMENT_NAME_DETECTOR,
					this.atts);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_CLASS, Literals.XML_ELEMENT_NAME_CLASS, this.atts);
			if (detector.getClassName() != null) {
				this.contentHandler.characters(detector.getClassName()
						.toCharArray(), 0, detector.getClassName().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_CLASS, Literals.XML_ELEMENT_NAME_CLASS);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME, this.atts);
			if (detector.getName() != null) {
				this.contentHandler.characters(
						detector.getName().toCharArray(), 0, detector.getName()
								.length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME);

			this.atts.clear();
			this.contentHandler.startElement("", "id", "id", this.atts);
			this.contentHandler.characters(detector.getID().toCharArray(), 0,
					detector.getID().length());
			this.contentHandler.endElement("", "id", "id");

			if (detector.getUnit() != null) {
				this.writeUnit(detector.getUnit());
			}
			if (detector.getTrigger() != null) {
				this.writeFunction(detector.getTrigger(), "trigger");
			}

			for(DetectorChannel ch : detector.getChannels()) {
				this.writeDetectorChannel(ch);
			}

			for(Option o : detector.getOptions()) {
				this.writeOption(o);
			}

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_DETECTOR, Literals.XML_ELEMENT_NAME_DETECTOR);

		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a detector channel
	 * 
	 * @param channel
	 *            The detector channel that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeDetectorChannel(final DetectorChannel channel) {
		try {
			this.atts.clear();
			/*this.atts.addAttribute("", "", "deferred",
					Literals.CHARACTER_DATA,
					Boolean.toString(channel.isDeferred()));
			this.atts.addAttribute("", "", "saveValue",
					Literals.CHARACTER_DATA,
					Boolean.toString(channel.isSaveValue()));*/
			this.contentHandler.startElement("", "channel", "channel",
					this.atts);
			this.atts.clear();

			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_CLASS, Literals.XML_ELEMENT_NAME_CLASS, this.atts);
			if (channel.getClassName() != null) {
				this.contentHandler.characters(channel.getClassName()
						.toCharArray(), 0, channel.getClassName().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_CLASS, Literals.XML_ELEMENT_NAME_CLASS);

			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME, this.atts);
			if (channel.getName() != null) {
				this.contentHandler.characters(channel.getName().toCharArray(),
						0, channel.getName().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME);

			this.atts.clear();
			this.contentHandler.startElement("", "id", "id", this.atts);
			this.contentHandler.characters(channel.getID().toCharArray(), 0,
					channel.getID().length());
			this.contentHandler.endElement("", "id", "id");

			this.writeFunction(channel.getRead(), "read");
			if (channel.getUnit() != null) {
				this.writeUnit(channel.getUnit());
			}
			if (channel.getTrigger() != null) {
				this.writeFunction(channel.getTrigger(), "trigger");
			}

			for (Option o : channel.getOptions()) {
				writeOption(o);
			}

			this.contentHandler.endElement("", "detector", "detector");
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a function.
	 * 
	 * @param trigger
	 *            The function that should be written.
	 * @param name
	 *            The name of the function, like start, stop, trigger.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeFunction(final Function trigger, final String name) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", name, name, this.atts);
			if (trigger.getAccess() != null) {
				this.writeAccess(trigger.getAccess(), "access");
			}
			if (trigger.getValue() != null) {
				this.writeTypeValue(trigger.getValue(), Literals.XML_ELEMENT_NAME_VALUE);
			}
			this.contentHandler.endElement("", name, name);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a unit.
	 * 
	 * @param unit
	 *            The unit to write.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeUnit(final Unit unit) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_UNIT, Literals.XML_ELEMENT_NAME_UNIT, this.atts);
			if (unit.isAccess()) {
				this.writeAccess(unit.getAccess(), "access");
			} else {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_UNITSTRING,
						Literals.XML_ELEMENT_NAME_UNITSTRING, this.atts);
				this.contentHandler.characters(unit.getValue().toCharArray(),
						0, unit.getValue().length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_UNITSTRING, Literals.XML_ELEMENT_NAME_UNITSTRING);
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_UNIT, Literals.XML_ELEMENT_NAME_UNIT);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a option.
	 * 
	 * @param option
	 *            This option that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeOption(final Option option) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_OPTION, Literals.XML_ELEMENT_NAME_OPTION, this.atts);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME, this.atts);
			if (option.getName() != null) {
				this.contentHandler.characters(option.getName().toCharArray(),
						0, option.getName().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME);

			this.atts.clear();
			this.contentHandler.startElement("", "id", "id", this.atts);
			this.contentHandler.characters(option.getID().toCharArray(), 0,
					option.getID().length());
			this.contentHandler.endElement("", "id", "id");

			this.writeFunction(option.getValue(), "value");

			if (option.getDisplaygroup() != null) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_DISPLAYGROUP,
						Literals.XML_ELEMENT_NAME_DISPLAYGROUP, this.atts);
				this.contentHandler.characters(option.getDisplaygroup()
						.toString().toCharArray(), 0, option.getDisplaygroup()
						.toString().length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_DISPLAYGROUP,
						Literals.XML_ELEMENT_NAME_DISPLAYGROUP);
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_OPTION, Literals.XML_ELEMENT_NAME_OPTION);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a type value.
	 * 
	 * @param typeValue
	 *            The TypeValue that should be written.
	 * @param name
	 *            A name for the tag.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeTypeValue(final TypeValue typeValue, final String name) {
		try {
			this.atts.clear();
			this.atts.addAttribute("", "", Literals.XML_ATTRIBUTE_NAME_TYPE, Literals.CHARACTER_DATA,
					DataTypes.typeToString(typeValue.getType()));
			this.contentHandler.startElement("", name, name, this.atts);
			this.contentHandler.characters(
					((typeValue.getValues() != null) ? typeValue.getValues()
							: "").toCharArray(), 0,
					((typeValue.getValues() != null) ? typeValue.getValues()
							: "").length());
			this.contentHandler.endElement("", name, name);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes an access.
	 * 
	 * @param access
	 *            The access that should be written.
	 * @param name
	 *            The name for the tag.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeAccess(final Access access, final String name) {
		try {
			this.atts.clear();
			this.atts.addAttribute("", "", "method",
					Literals.CHARACTER_DATA,
					MethodTypes.typeToString(access.getMethod()));
			if (access.getType() != null) {
				this.atts.addAttribute("", "", Literals.XML_ATTRIBUTE_NAME_TYPE,
						Literals.CHARACTER_DATA,
						DataTypes.typeToString(access.getType()));
			}
			if (access.getCount() != 0) {
				this.atts.addAttribute("", "", "count",
						Literals.CHARACTER_DATA, "" + access.getCount());
			}
			if (access.getTransport() != null) {
				this.atts.addAttribute("", "", "transport",
						Literals.CHARACTER_DATA,
						TransportTypes.typeToString(access.getTransport()));
			}
			if (access.getMonitor()) {
				this.atts.addAttribute("", "", "monitor",
						Literals.CHARACTER_DATA, Boolean.TRUE.toString());
			}
			if (access.getTimeout() != 0.0) {
				this.atts.addAttribute("", "", "timeout",
						Literals.CHARACTER_DATA, "" + access.getTimeout());
			}
			this.contentHandler.startElement("", name, name, this.atts);
			this.contentHandler.characters(
					access.getVariableID().toCharArray(), 0, access
							.getVariableID().length());
			this.contentHandler.endElement("", name, name);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes all plug ins.
	 * 
	 * @return Returns if the write process was successful.
	 */
	private boolean writePlugins() {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_PLUGINS, Literals.XML_ELEMENT_NAME_PLUGINS,
					this.atts);
			for(PlugIn plugin : this.measuringStation.getPlugins()) {
				this.writePlugin(plugin);
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_PLUGINS, Literals.XML_ELEMENT_NAME_PLUGINS);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a plug in.
	 * 
	 * @param plugin
	 *            The plugin that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writePlugin(final PlugIn plugin) {
		try {
			this.atts.clear();
			this.atts.addAttribute("", "", Literals.XML_ATTRIBUTE_NAME_TYPE, Literals.CHARACTER_DATA,
					PluginTypes.typeToString(plugin.getType()));
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_PLUGIN, Literals.XML_ELEMENT_NAME_PLUGIN, this.atts);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME, this.atts);
			this.contentHandler.characters(plugin.getName().toCharArray(), 0,
					plugin.getName().length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_LOCATION, Literals.XML_ELEMENT_NAME_LOCATION,
					this.atts);
			this.contentHandler.characters(plugin.getLocation().toCharArray(),
					0, plugin.getLocation().length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_LOCATION, Literals.XML_ELEMENT_NAME_LOCATION);

			for(PluginParameter pluginparam : plugin.getParameters()) {
				this.writePluginParameter(pluginparam);
			}

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_PLUGIN, Literals.XML_ELEMENT_NAME_PLUGIN);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a plug in parameter
	 * 
	 * @param pluginParameter
	 *            The plug in parameter that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writePluginParameter(final PluginParameter pluginParameter) {
		try {
			this.atts.clear();
			this.atts.addAttribute("", "", Literals.XML_ATTRIBUTE_NAME_NAME, Literals.CHARACTER_DATA,
					pluginParameter.getName());
			this.atts.addAttribute("", "", "datatype",
					Literals.CHARACTER_DATA,
					PluginDataType.typeToString(pluginParameter.getType()));
			if (pluginParameter.getDefaultValue() != null) {
				this.atts.addAttribute("", "", "default",
						Literals.CHARACTER_DATA,
						pluginParameter.getDefaultValue());
			}
			this.atts.addAttribute("", "", "mandatory",
					Literals.CHARACTER_DATA,
					"" + pluginParameter.isMandatory());
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_PARAMETER, Literals.XML_ELEMENT_NAME_PARAMETER,
					this.atts);

			if (pluginParameter.getValues() != null) {
				this.contentHandler
						.characters(pluginParameter.getValues().toCharArray(),
								0, pluginParameter.getValues().length());
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_PARAMETER, Literals.XML_ELEMENT_NAME_PARAMETER);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a chain.
	 * 
	 * @param chain
	 *            The chain that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeChain(final Chain chain) {
		try {
			this.atts.clear();
			this.atts.addAttribute("", "id", "id", Literals.CHARACTER_DATA, "" + chain.getId());

			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_CHAIN, Literals.XML_ELEMENT_NAME_CHAIN, this.atts);

			if (!chain.getComment().isEmpty()) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_COMMENT, Literals.XML_ELEMENT_NAME_COMMENT,
						this.atts);
				this.contentHandler.characters(
						chain.getComment().toCharArray(), 0, chain.getComment()
								.length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_COMMENT, Literals.XML_ELEMENT_NAME_COMMENT);
			}

			if (chain.getSaveFilename() != null
					&& !chain.getSaveFilename().isEmpty()) {
				this.atts.clear();

				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SAVEFILENAME,
						Literals.XML_ELEMENT_NAME_SAVEFILENAME, this.atts);
				this.contentHandler.characters(chain.getSaveFilename()
						.toCharArray(), 0, chain.getSaveFilename().length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SAVEFILENAME,
						Literals.XML_ELEMENT_NAME_SAVEFILENAME);
			}

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_CONFIRMSAVE, Literals.XML_ELEMENT_NAME_CONFIRMSAVE,
					this.atts);
			this.contentHandler.characters(
					("" + chain.isConfirmSave()).toCharArray(), 0,
					("" + chain.isConfirmSave()).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_CONFIRMSAVE, Literals.XML_ELEMENT_NAME_CONFIRMSAVE);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_AUTONUMBER, Literals.XML_ELEMENT_NAME_AUTONUMBER,
					this.atts);
			this.contentHandler.characters(
					("" + chain.isAutoNumber()).toCharArray(), 0,
					("" + chain.isAutoNumber()).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_AUTONUMBER, Literals.XML_ELEMENT_NAME_AUTONUMBER);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SAVESCANDESCRIPTION,
					Literals.XML_ELEMENT_NAME_SAVESCANDESCRIPTION, this.atts);
			this.contentHandler.characters(
					("" + chain.isSaveScanDescription()).toCharArray(), 0,
					("" + chain.isSaveScanDescription()).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SAVESCANDESCRIPTION,
					Literals.XML_ELEMENT_NAME_SAVESCANDESCRIPTION);

			if (chain.getSavePluginController().getPlugin() != null) {
				this.atts.clear();

				this.writePluginController(chain.getSavePluginController(),
						"saveplugin");
			}

			// all events except the default start event are in startevent
			for (ControlEvent event : chain.getStartEvents()) {
				this.writeControlEvent(event, "startevent");
			}

			for (ControlEvent event : chain.getPauseEvents()) {
				this.writeControlEvent(event, "pauseevent");
			}

			for (ControlEvent event : chain.getRedoEvents()) {
				this.writeControlEvent(event, "redoevent");
			}

			for (ControlEvent event : chain.getBreakEvents()) {
				this.writeControlEvent(event, "breakevent");
			}

			for (ControlEvent event : chain.getStopEvents()) {
				this.writeControlEvent(event, "stopevent");
			}

			this.atts.clear();

			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SCANMODULES, Literals.XML_ELEMENT_NAME_SCANMODULES,
					this.atts);
			for(ScanModule sm : chain.getScanModules()) {
				this.writeScanModule(sm);
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SCANMODULES, Literals.XML_ELEMENT_NAME_SCANMODULES);

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_CHAIN, Literals.XML_ELEMENT_NAME_CHAIN);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a scan module.
	 * 
	 * @param scanModule
	 *            The scan module that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeScanModule(final ScanModule scanModule) {
		try {
			this.atts.clear();
			this.atts.addAttribute("", "id", "id", Literals.CHARACTER_DATA,
					"" + scanModule.getId());
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SCANMODULE, Literals.XML_ELEMENT_NAME_SCANMODULE,
					this.atts);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_TYPE, Literals.XML_ELEMENT_NAME_TYPE, this.atts);
			this.contentHandler.characters(scanModule.getType().toString().toCharArray(),
					0, scanModule.getType().toString().length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_TYPE, Literals.XML_ELEMENT_NAME_TYPE);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME, this.atts);
			this.contentHandler.characters(scanModule.getName().toCharArray(),
					0, scanModule.getName().length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_XPOS, Literals.XML_ELEMENT_NAME_XPOS, this.atts);
			this.contentHandler.characters(
					("" + scanModule.getX()).toCharArray(), 0,
					("" + scanModule.getX()).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_XPOS, Literals.XML_ELEMENT_NAME_XPOS);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_YPOS, Literals.XML_ELEMENT_NAME_YPOS, this.atts);
			this.contentHandler.characters(
					("" + scanModule.getY()).toCharArray(), 0,
					("" + scanModule.getY()).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_YPOS, Literals.XML_ELEMENT_NAME_YPOS);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_PARENT, Literals.XML_ELEMENT_NAME_PARENT, this.atts);
			if (scanModule.getParent() != null) {
				if (scanModule.getParent().getParentScanModule() != null) {
				this.contentHandler.characters(("" + scanModule.getParent()
						.getParentScanModule().getId()).toCharArray(), 0,
						("" + scanModule.getParent().getParentScanModule()
								.getId()).length());
				} else if (scanModule.getParent().getParentEvent() != null) {
					this.contentHandler.characters("0".toCharArray(), 0, 1);
				}
			} else {
				this.contentHandler.characters("-1".toCharArray(), 0, 2);
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_PARENT, Literals.XML_ELEMENT_NAME_PARENT);

			this.atts.clear();
			if (scanModule.getNested() != null
					&& scanModule.getNested().getChildScanModule() != null) {
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NESTED, Literals.XML_ELEMENT_NAME_NESTED,
						this.atts);
				this.contentHandler.characters(("" + scanModule.getNested()
						.getChildScanModule().getId()).toCharArray(), 0,
						("" + scanModule.getNested().getChildScanModule()
								.getId()).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NESTED, Literals.XML_ELEMENT_NAME_NESTED);
			}

			this.atts.clear();
			if (scanModule.getAppended() != null
					&& scanModule.getAppended().getChildScanModule() != null) {
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_APPENDED, Literals.XML_ELEMENT_NAME_APPENDED,
						this.atts);
				this.contentHandler.characters(("" + scanModule.getAppended()
						.getChildScanModule().getId()).toCharArray(), 0,
						("" + scanModule.getAppended().getChildScanModule()
								.getId()).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_APPENDED, Literals.XML_ELEMENT_NAME_APPENDED);
			}

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_STORAGE, Literals.XML_ELEMENT_NAME_STORAGE,
					this.atts);
			this.contentHandler.characters(scanModule.getStorage().toString()
					.toCharArray(), 0, scanModule.getStorage().toString()
					.length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_STORAGE, Literals.XML_ELEMENT_NAME_STORAGE);
			
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_VALUECOUNT, Literals.XML_ELEMENT_NAME_VALUECOUNT,
					this.atts);
			this.contentHandler.characters(
					("" + scanModule.getValueCount()).toCharArray(), 0,
					("" + scanModule.getValueCount()).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_VALUECOUNT, Literals.XML_ELEMENT_NAME_VALUECOUNT);

			if (scanModule.getSettleTime() != Double.NEGATIVE_INFINITY) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SETTLETIME,
						Literals.XML_ELEMENT_NAME_SETTLETIME, this.atts);
				this.contentHandler.characters(
						("" + scanModule.getSettleTime()).toCharArray(), 0,
						("" + scanModule.getSettleTime()).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SETTLETIME, Literals.XML_ELEMENT_NAME_SETTLETIME);
			}

			if (scanModule.getTriggerDelay() != Double.NEGATIVE_INFINITY) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_TRIGGERDELAY,
						Literals.XML_ELEMENT_NAME_TRIGGERDELAY, this.atts);
				this.contentHandler.characters(
						("" + scanModule.getTriggerDelay()).toCharArray(), 0,
						("" + scanModule.getTriggerDelay()).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_TRIGGERDELAY,
						Literals.XML_ELEMENT_NAME_TRIGGERDELAY);
			}

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMAXIS,
					Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMAXIS, this.atts);
			this.contentHandler.characters(
					("" + scanModule.isTriggerConfirmAxis()).toCharArray(), 0,
					("" + scanModule.isTriggerConfirmAxis()).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMAXIS,
					Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMAXIS);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMCHANNEL,
					Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMCHANNEL, this.atts);
			this.contentHandler.characters(
					("" + scanModule.isTriggerConfirmChannel()).toCharArray(), 0,
					("" + scanModule.isTriggerConfirmChannel()).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMCHANNEL,
					Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMCHANNEL);
			
			for (ControlEvent event : scanModule.getTriggerEvents()) {
				this.writeControlEvent(event, "triggerevent");
			}
			
			for (ControlEvent event : scanModule.getPauseEvents()) {
				this.writeControlEvent(event, "pauseevent");
			}
			
			for (ControlEvent event : scanModule.getRedoEvents()) {
				this.writeControlEvent(event, "redoevent");
			}
			
			for (ControlEvent event : scanModule.getBreakEvents()) {
				this.writeControlEvent(event, "breakevent");
			}
			
			Prescan[] prescans = scanModule.getPrescans();
			for (int i = 0; i < prescans.length; ++i) {
				this.writePrescan(prescans[i]);
			}

			Axis[] axis = scanModule.getAxes();
			for (int i = 0; i < axis.length; ++i) {
				try {
					this.writeAxis(axis[i]);
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}

			Channel[] channels = scanModule.getChannels();
			for (int i = 0; i < channels.length; ++i) {
				this.writeChannel(channels[i]);
			}

			Postscan[] postscans = scanModule.getPostscans();
			for (int i = 0; i < postscans.length; ++i) {
				this.writePostscan(postscans[i]);
			}

			Positioning[] positioning = scanModule.getPositionings();
			for (int i = 0; i < positioning.length; ++i) {
				this.writePositioning(positioning[i]);
			}

			PlotWindow[] plotWindows = scanModule.getPlotWindows();
			for (int i = 0; i < plotWindows.length; ++i) {
				if (plotWindows[i].getXAxis() != null) {
					this.writePlotWindow(plotWindows[i]);
				}
			}

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SCANMODULE, Literals.XML_ELEMENT_NAME_SCANMODULE);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a positioning.
	 * 
	 * @param positioning
	 *            The positioning that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writePositioning(final Positioning positioning) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_POSITIONING, Literals.XML_ELEMENT_NAME_POSITIONING,
					this.atts);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_AXISID, Literals.XML_ELEMENT_NAME_AXISID,
					this.atts);
			this.contentHandler.characters(positioning.getMotorAxis().getID()
					.toCharArray(), 0, positioning.getMotorAxis().getID()
					.length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_AXISID, Literals.XML_ELEMENT_NAME_AXISID);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_CHANNELID, Literals.XML_ELEMENT_NAME_CHANNELID,
					this.atts);
			this.contentHandler.characters(positioning.getDetectorChannel()
					.getID().toCharArray(), 0, positioning.getDetectorChannel()
					.getID().length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_CHANNELID, Literals.XML_ELEMENT_NAME_CHANNELID);

			if (positioning.getNormalization() != null) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NORMALIZEID,
						Literals.XML_ELEMENT_NAME_NORMALIZEID, this.atts);
				this.contentHandler.characters(positioning.getNormalization()
						.getID().toCharArray(), 0, positioning
						.getNormalization().getID().length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NORMALIZEID,
						Literals.XML_ELEMENT_NAME_NORMALIZEID);
			}

			this.writePluginController(positioning.getPluginController(),
					"plugin");

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_POSITIONING, Literals.XML_ELEMENT_NAME_POSITIONING);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a plug in controller.
	 * 
	 * @param pluginController
	 *            The plug in controller that should be written.
	 * @param tagName
	 *            The name of the tag.
	 * @return Returns if the write process was successful.
	 */
	private boolean writePluginController(
			final PluginController pluginController, final String tagName) {
		try {
			this.atts.clear();
			this.atts.addAttribute("", Literals.XML_ATTRIBUTE_NAME_NAME, Literals.XML_ATTRIBUTE_NAME_NAME, Literals.CHARACTER_DATA,
					pluginController.getPlugin().getName());
			this.contentHandler.startElement("", tagName, tagName, this.atts);

			this.atts.clear();
			this.atts.addAttribute("", Literals.XML_ATTRIBUTE_NAME_NAME, Literals.XML_ATTRIBUTE_NAME_NAME, Literals.CHARACTER_DATA, "location");
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_PARAMETER, Literals.XML_ELEMENT_NAME_PARAMETER,
					this.atts);
			this.contentHandler.characters(pluginController.getPlugin()
					.getLocation().toCharArray(), 0, pluginController
					.getPlugin().getLocation().length());
			this.contentHandler.endElement("", tagName, tagName);

			Iterator<PluginParameter> it = pluginController.getPlugin()
					.getParameters().iterator();

			while (it.hasNext()) {
				final PluginParameter actPlugin = it.next();
				final String parameterName = actPlugin.getName();

				if (actPlugin.isMandatory()) {
					// Parameter ist ein Pflichtfeld und muß auf jeden Fall
					// gespeichert werden
					this.atts.clear();
					this.atts.addAttribute("", Literals.XML_ATTRIBUTE_NAME_NAME, Literals.XML_ATTRIBUTE_NAME_NAME, Literals.CHARACTER_DATA,
							parameterName);
					this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_PARAMETER,
							Literals.XML_ELEMENT_NAME_PARAMETER, this.atts);
					this.contentHandler.characters(
							pluginController.get(parameterName).toCharArray(),
							0, pluginController.get(parameterName).length());
					this.contentHandler.endElement("", tagName, tagName);
				} else {
					// Parameter ist freiwilliger Eintrag und wird nur
					// gespeichert, wenn ein Wert vorhanden ist.
					if (pluginController.get(parameterName) != null) {
						// Wert vorhanden, Parameter wird gespeichert
						this.atts.clear();
						this.atts.addAttribute("", Literals.XML_ATTRIBUTE_NAME_NAME, Literals.XML_ATTRIBUTE_NAME_NAME, Literals.CHARACTER_DATA,
								parameterName);
						this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_PARAMETER,
								Literals.XML_ELEMENT_NAME_PARAMETER, this.atts);
						this.contentHandler.characters(
								pluginController.get(parameterName)
										.toCharArray(), 0, pluginController
										.get(parameterName).length());
						this.contentHandler.endElement("", tagName, tagName);
					} else {
						// kein Mandatory, kein Wert vorhanden, nichts wird
						// gespeichert
					}
				}
			}

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_PLUGIN, Literals.XML_ELEMENT_NAME_PLUGIN);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a plot window.
	 * 
	 * @param plotWindow
	 *            The plot window that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writePlotWindow(final PlotWindow plotWindow) {
		try {
			this.atts.clear();
			this.atts.addAttribute("", "id", "id", Literals.CHARACTER_DATA,
					"" + plotWindow.getId());
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_PLOT, Literals.XML_ELEMENT_NAME_PLOT, this.atts);

			if (!plotWindow.getName().isEmpty()) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME, this.atts);
				this.contentHandler.characters(plotWindow.getName()
						.toCharArray(), 0, plotWindow.getName().length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NAME, Literals.XML_ELEMENT_NAME_NAME);
			}

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_XAXIS, Literals.XML_ELEMENT_NAME_XAXIS, this.atts);

			this.atts.clear();
			this.contentHandler.startElement("", "id", "id", this.atts);
			this.contentHandler.characters(plotWindow.getXAxis().getID()
					.toCharArray(), 0, plotWindow.getXAxis().getID().length());
			this.contentHandler.endElement("", "id", "id");

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_MODE, Literals.XML_ELEMENT_NAME_MODE, this.atts);
			this.contentHandler.characters((PlotModes.modeToString(plotWindow
					.getMode())).toCharArray(), 0, (PlotModes
					.modeToString(plotWindow.getMode())).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_MODE, Literals.XML_ELEMENT_NAME_MODE);
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_XAXIS, Literals.XML_ELEMENT_NAME_XAXIS);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_INIT, Literals.XML_ELEMENT_NAME_INIT, this.atts);
			this.contentHandler.characters(
					("" + plotWindow.isInit()).toCharArray(), 0,
					("" + plotWindow.isInit()).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_INIT, Literals.XML_ELEMENT_NAME_INIT);

			for(YAxis yaxis : plotWindow.getYAxes()) {
				this.writeYAxis(yaxis);
			}

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_PLOT, Literals.XML_ELEMENT_NAME_PLOT);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes an y-axis.
	 * 
	 * @param yaxis
	 *            The y-axis that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeYAxis(final YAxis yaxis) {
		if (yaxis.getDetectorChannel() != null) {
			try {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_YAXIS, Literals.XML_ELEMENT_NAME_YAXIS,
						this.atts);
				this.atts.clear();
				this.contentHandler.startElement("", "id", "id", this.atts);
				this.contentHandler.characters(yaxis.getDetectorChannel()
						.getID().toCharArray(), 0, yaxis.getDetectorChannel()
						.getID().length());
				this.contentHandler.endElement("", "id", "id");
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_MODE, Literals.XML_ELEMENT_NAME_MODE, this.atts);
				this.contentHandler
						.characters((PlotModes.modeToString(yaxis.getMode()))
								.toCharArray(), 0, (PlotModes
								.modeToString(yaxis.getMode())).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_MODE, Literals.XML_ELEMENT_NAME_MODE);

				if (yaxis.getNormalizeChannel() != null) {
					this.atts.clear();
					this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NORMALIZEID,
							Literals.XML_ELEMENT_NAME_NORMALIZEID, this.atts);
					this.contentHandler.characters(yaxis.getNormalizeChannel()
							.getID().toCharArray(), 0, yaxis
							.getNormalizeChannel().getID().length());
					this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NORMALIZEID,
							Literals.XML_ELEMENT_NAME_NORMALIZEID);
				}

				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_LINESTYLE, Literals.XML_ELEMENT_NAME_LINESTYLE,
						this.atts);
				this.contentHandler.characters(yaxis.getLinestyle().toString()
						.toCharArray(), 0, yaxis.getLinestyle().toString()
						.length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_LINESTYLE, Literals.XML_ELEMENT_NAME_LINESTYLE);

				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_COLOR, Literals.XML_ELEMENT_NAME_COLOR,
						this.atts);

				RGB color = yaxis.getColor();
				String red = Integer.toHexString(color.red);
				String green = Integer.toHexString(color.green);
				String blue = Integer.toHexString(color.blue);
				if (red.length() == 1) {
					red = "0" + red;
				}
				if (green.length() == 1) {
					green = "0" + green;
				}
				if (blue.length() == 1) {
					blue = "0" + blue;
				}

				String sColor = red + green + blue;
				this.contentHandler.characters(sColor.toCharArray(), 0,
						sColor.length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_COLOR, Literals.XML_ELEMENT_NAME_COLOR);

				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_MARKSTYLE, Literals.XML_ELEMENT_NAME_MARKSTYLE,
						this.atts);
				try {
					String encoded = URLEncoder.encode(yaxis.getMarkstyle()
							.toString(), "UTF-8");
					this.contentHandler.characters(encoded.toCharArray(), 0,
							encoded.length());
				} catch (UnsupportedEncodingException e) {
					logger.warn(e.getMessage(), e);
				}
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_MARKSTYLE, Literals.XML_ELEMENT_NAME_MARKSTYLE);

				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_YAXIS, Literals.XML_ELEMENT_NAME_YAXIS);
			} catch (SAXException e) {
				logger.error(e.getMessage(), e);
				return false;
			}
		}
		return true;
	}

	/**
	 * This method writes a post scan.
	 * 
	 * @param postscan
	 *            The post scan that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writePostscan(final Postscan postscan) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_POSTSCAN, Literals.XML_ELEMENT_NAME_POSTSCAN,
					this.atts);
			this.atts.clear();
			this.contentHandler.startElement("", "id", "id", this.atts);
			this.contentHandler.characters(postscan.getAbstractDevice().getID()
					.toCharArray(), 0, postscan.getAbstractDevice().getID()
					.length());
			this.contentHandler.endElement("", "id", "id");

			if (postscan.isReset()) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_RESETORIGINALVALUE,
						Literals.XML_ELEMENT_NAME_RESETORIGINALVALUE, this.atts);
				this.contentHandler.characters(
						("" + postscan.isReset()).toCharArray(), 0,
						("" + postscan.isReset()).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_RESETORIGINALVALUE,
						Literals.XML_ELEMENT_NAME_RESETORIGINALVALUE);
			} else {
				this.atts.clear();
				this.atts.addAttribute(
						"",
						Literals.XML_ATTRIBUTE_NAME_TYPE,
						Literals.XML_ATTRIBUTE_NAME_TYPE,
						Literals.CHARACTER_DATA,
						DataTypes.typeToString(((postscan
								.getAbstractPrePostscanDevice().getValue()
								.getValue() != null) ? postscan
								.getAbstractPrePostscanDevice().getValue()
								.getValue().getType() : postscan
								.getAbstractPrePostscanDevice().getValue()
								.getAccess().getType())));
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_VALUE, Literals.XML_ELEMENT_NAME_VALUE,
						this.atts);
				if (postscan.getValue() != null) {
					this.contentHandler.characters(postscan.getValue()
							.toCharArray(), 0, postscan.getValue().length());
				}
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_VALUE, Literals.XML_ELEMENT_NAME_VALUE);
			}

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_POSTSCAN, Literals.XML_ELEMENT_NAME_POSTSCAN);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes channel.
	 * 
	 * @param channel
	 *            The channel that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeChannel(final Channel channel) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SMCHANNEL, Literals.XML_ELEMENT_NAME_SMCHANNEL,
					this.atts);
			this.atts.clear();
			this.contentHandler.startElement("", "channelid", "channelid",
					this.atts);
			this.contentHandler.characters(channel.getAbstractDevice().getID()
					.toCharArray(), 0, channel.getAbstractDevice().getID()
					.length());
			this.contentHandler.endElement("", "channelid", "channelid");

			if (channel.getAverageCount() > 1) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_AVERAGECOUNT,
						Literals.XML_ELEMENT_NAME_AVERAGECOUNT, this.atts);
				this.contentHandler.characters(
						("" + channel.getAverageCount()).toCharArray(), 0,
						("" + channel.getAverageCount()).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_AVERAGECOUNT,
						Literals.XML_ELEMENT_NAME_AVERAGECOUNT);
			}

			if (channel.getMaxDeviation() != Double.NEGATIVE_INFINITY) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_MAXDEVIATION,
						Literals.XML_ELEMENT_NAME_MAXDEVIATION, this.atts);
				this.contentHandler.characters(
						("" + channel.getMaxDeviation()).toCharArray(), 0,
						("" + channel.getMaxDeviation()).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_MAXDEVIATION,
						Literals.XML_ELEMENT_NAME_MAXDEVIATION);
			}
			if (channel.getMinimum() != Double.NEGATIVE_INFINITY) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_MINIMUM, Literals.XML_ELEMENT_NAME_MINIMUM,
						this.atts);
				this.contentHandler.characters(
						("" + channel.getMinimum()).toCharArray(), 0,
						("" + channel.getMinimum()).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_MINIMUM, Literals.XML_ELEMENT_NAME_MINIMUM);
			}

			if (channel.getMaxAttempts() != Integer.MIN_VALUE) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_MAXATTEMPTS,
						Literals.XML_ELEMENT_NAME_MAXATTEMPTS, this.atts);
				this.contentHandler.characters(
						("" + channel.getMaxAttempts()).toCharArray(), 0,
						("" + channel.getMaxAttempts()).length());
				this.contentHandler
						.endElement("", Literals.XML_ELEMENT_NAME_MAXATTEMPTS, Literals.XML_ELEMENT_NAME_MAXATTEMPTS);
			}

			if (channel.getNormalizeChannel() != null) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_NORMALIZEID, 
						Literals.XML_ELEMENT_NAME_NORMALIZEID, this.atts);
				this.contentHandler.characters(
						channel.getNormalizeChannel().getID().toCharArray(), 
						0, 
						channel.getNormalizeChannel().getID().length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_NORMALIZEID, 
						Literals.XML_ELEMENT_NAME_NORMALIZEID);
			}

			if (scanDescription.isUsedAsEvent(channel)) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SENDREADYEVENT,
						Literals.XML_ELEMENT_NAME_SENDREADYEVENT, this.atts);
				String trueString = Boolean.TRUE.toString();
				this.contentHandler.characters(trueString.toCharArray(), 0,
						trueString.length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SENDREADYEVENT,
						Literals.XML_ELEMENT_NAME_SENDREADYEVENT);
			}

			for (ControlEvent event : channel.getRedoEvents()) {
				this.writeControlEvent(event, "redoevent");
			}

			if (channel.isDeferred()) {
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_DEFERREDTRIGGER, 
						Literals.XML_ELEMENT_NAME_DEFERREDTRIGGER, this.atts);
				this.contentHandler.characters("true".toCharArray(), 0,
						"true".length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_DEFERREDTRIGGER,
						Literals.XML_ELEMENT_NAME_DEFERREDTRIGGER);
			}
			
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SMCHANNEL, Literals.XML_ELEMENT_NAME_SMCHANNEL);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes an axis.
	 * 
	 * @param axis
	 *            The axis that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeAxis(final Axis axis) {
		try {
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SMAXIS, Literals.XML_ELEMENT_NAME_SMAXIS, this.atts);
			this.atts.clear();
			this.contentHandler.startElement("", "axisid", "axisid", this.atts);
			this.contentHandler.characters(axis.getAbstractDevice().getID()
					.toCharArray(), 0, axis.getAbstractDevice().getID()
					.length());
			this.contentHandler.endElement("", "axisid", "axisid");

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_STEPFUNCTION,
					Literals.XML_ELEMENT_NAME_STEPFUNCTION, this.atts);
			this.contentHandler.characters(axis.getStepfunction().toString()
					.toCharArray(), 0, axis.getStepfunction().toString().length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_STEPFUNCTION, Literals.XML_ELEMENT_NAME_STEPFUNCTION);

			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_POSITIONMODE,
					Literals.XML_ELEMENT_NAME_POSITIONMODE, this.atts);
			this.contentHandler.characters(
					PositionMode.typeToString(axis.getPositionMode())
							.toCharArray(), 0,
					PositionMode.typeToString(axis.getPositionMode()).length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_POSITIONMODE, Literals.XML_ELEMENT_NAME_POSITIONMODE);
			
			switch (axis.getStepfunction()) {
			case ADD:
			case MULTIPLY:
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_STARTSTOPSTEP,
						Literals.XML_ELEMENT_NAME_STARTSTOPSTEP, this.atts);
				if (axis.getStart() != null) {
					this.atts.clear();
					this.atts.addAttribute("", Literals.XML_ATTRIBUTE_NAME_TYPE, Literals.XML_ATTRIBUTE_NAME_TYPE, Literals.CHARACTER_DATA,
							DataTypes.typeToString(axis.getType()));
					this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_START, Literals.XML_ELEMENT_NAME_START,
							this.atts);
					if (axis.getType().equals(DataTypes.DATETIME)) {
						switch (axis.getPositionMode()) {
						case ABSOLUTE:
							String date = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss.SSS").format(axis
									.getStart());
							this.contentHandler.characters(date.toCharArray(), 0,
									date.length());
							break;
						case RELATIVE:
							String duration = ((Duration)axis.getStart()).
									toString();
							this.contentHandler.characters(
									duration.toCharArray(), 0,
											duration.length());
							break;
						}
					} else {
					this.contentHandler.characters(axis.getStart().toString()
						.toCharArray(), 0, axis.getStart().toString().length());
					}
					this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_START, Literals.XML_ELEMENT_NAME_START);
				}
				if (axis.getStop() != null) {
					this.atts.clear();
					this.atts.addAttribute("", Literals.XML_ATTRIBUTE_NAME_TYPE, Literals.XML_ATTRIBUTE_NAME_TYPE, Literals.CHARACTER_DATA,
							DataTypes.typeToString(axis.getType()));
					this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_STOP, Literals.XML_ELEMENT_NAME_STOP,
							this.atts);
					if (axis.getType().equals(DataTypes.DATETIME)) {
						switch (axis.getPositionMode()) {
						case ABSOLUTE:
							String date = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss.SSS").format(axis
									.getStop());
							this.contentHandler.characters(date.toCharArray(), 0,
									date.length());
							break;
						case RELATIVE:
							String duration = ((Duration)axis.getStop()).
									toString();
							this.contentHandler.characters(
									duration.toCharArray(), 0,
											duration.length());
							break;
						}
					} else {
						this.contentHandler.characters(
								axis.getStop().toString().toCharArray(), 0, 
										axis.getStop().toString().length());
					}
					this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_STOP, Literals.XML_ELEMENT_NAME_STOP);
				}
				if (axis.getStepwidth() != null) {
					this.atts.clear();
					this.atts.addAttribute("", Literals.XML_ATTRIBUTE_NAME_TYPE, Literals.XML_ATTRIBUTE_NAME_TYPE, Literals.CHARACTER_DATA,
							DataTypes.typeToString(axis.getType()));
					this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_STEPWIDTH,
							Literals.XML_ELEMENT_NAME_STEPWIDTH, this.atts);
					if (axis.getType().equals(DataTypes.DATETIME)) {
						switch (axis.getPositionMode()) {
						case ABSOLUTE:
							String date = new SimpleDateFormat(
									"HH:mm:ss.SSS").format(axis
									.getStepwidth());
							this.contentHandler.characters(date.toCharArray(), 0,
									date.length());
							break;
						case RELATIVE:
							String duration = ((Duration)axis.getStepwidth()).
									toString();
							this.contentHandler.characters(
									duration.toCharArray(), 0,
											duration.length());
							break;
						}
					} else {
					this.contentHandler.characters(axis.getStepwidth()
							.toString().toCharArray(), 0, axis.getStepwidth()
							.toString().length());
					}
					this.contentHandler
							.endElement("", Literals.XML_ELEMENT_NAME_STEPWIDTH, Literals.XML_ELEMENT_NAME_STEPWIDTH);
				}
				this.atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_ISMAINAXIS,
						Literals.XML_ELEMENT_NAME_ISMAINAXIS, this.atts);
				this.contentHandler.characters(
						Boolean.toString(axis.isMainAxis()).toCharArray(), 0,
						Boolean.toString(axis.isMainAxis()).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_ISMAINAXIS, Literals.XML_ELEMENT_NAME_ISMAINAXIS);
				this.atts.clear();
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_STARTSTOPSTEP,
						Literals.XML_ELEMENT_NAME_STARTSTOPSTEP);
				break;
			case FILE: 
				if (axis.getFile() != null) {
					this.atts.clear();
					this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_STEPFILENAME,
							Literals.XML_ELEMENT_NAME_STEPFILENAME, this.atts);
					this.contentHandler.characters(axis.getFile()
							.getAbsolutePath().toCharArray(), 0, axis.getFile()
							.getAbsolutePath().length());
					this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_STEPFILENAME,
							Literals.XML_ELEMENT_NAME_STEPFILENAME);
				}
				break;
			case PLUGIN:
				if (axis.getPluginController() != null) {
					this.atts.clear();
					this.writePluginController(
							axis.getPluginController(), "plugin");
				}
				break;
			case POSITIONLIST:
				if (axis.getPositionlist() != null) {
					this.atts.clear();
					this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_POSITIONLIST,
							Literals.XML_ELEMENT_NAME_POSITIONLIST, this.atts);
					this.contentHandler.characters(axis.getPositionlist()
							.toCharArray(), 0, axis.getPositionlist().length());
					this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_POSITIONLIST,
							Literals.XML_ELEMENT_NAME_POSITIONLIST);
				}
				break;
			default: 
				break;
			}
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SMAXIS, Literals.XML_ELEMENT_NAME_SMAXIS);
		} catch (final SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes a pre scan.
	 * 
	 * @param prescan
	 *            The prescan that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writePrescan(final Prescan prescan) {
		this.atts.clear();
		try {
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_PRESCAN, Literals.XML_ELEMENT_NAME_PRESCAN,
					this.atts);

			this.atts.clear();
			this.contentHandler.startElement("", "id", "id", this.atts);
			this.contentHandler.characters(prescan
					.getAbstractPrePostscanDevice().getID().toCharArray(), 0,
					prescan.getAbstractPrePostscanDevice().getID().length());
			this.contentHandler.endElement("", "id", "id");

			this.atts.clear();
			this.atts.addAttribute(
					"",
					Literals.XML_ATTRIBUTE_NAME_TYPE,
					Literals.XML_ATTRIBUTE_NAME_TYPE,
					Literals.CHARACTER_DATA,
					DataTypes.typeToString(((prescan
							.getAbstractPrePostscanDevice().getValue()
							.getValue() != null) ? prescan
							.getAbstractPrePostscanDevice().getValue()
							.getValue().getType() : prescan
							.getAbstractPrePostscanDevice().getValue()
							.getAccess().getType())));
			this.contentHandler.startElement(Literals.XML_ELEMENT_NAME_VALUE, Literals.XML_ELEMENT_NAME_VALUE, Literals.XML_ELEMENT_NAME_VALUE,
					this.atts);
			this.contentHandler.characters(prescan.getValue().toCharArray(), 0,
					prescan.getValue().length());
			this.contentHandler.endElement(Literals.XML_ELEMENT_NAME_VALUE, Literals.XML_ELEMENT_NAME_VALUE, Literals.XML_ELEMENT_NAME_VALUE);

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_PRESCAN, Literals.XML_ELEMENT_NAME_PRESCAN);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * Returns if the write process was successful.
	 * 
	 * @param controlEvent
	 *            This method writes a control event.
	 * @param name
	 *            The name of the tag.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeControlEvent(final ControlEvent controlEvent,
			final String name) {
		this.atts.clear();
		try {
			this.contentHandler.startElement("", name, name, this.atts);
			this.atts.clear();

			if (controlEvent.getEventType() == EventTypes.MONITOR) {
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_MONITOREVENT,
						Literals.XML_ELEMENT_NAME_MONITOREVENT, this.atts);
				this.contentHandler.startElement("", "id", "id", this.atts);
				this.contentHandler.characters(controlEvent.getEvent().getId()
						.toCharArray(), 0, controlEvent.getEvent().getId()
						.length());
				this.contentHandler.endElement("", "id", "id");

				this.atts.clear();
				this.atts.addAttribute(
						"",
						Literals.XML_ATTRIBUTE_NAME_TYPE,
						Literals.XML_ATTRIBUTE_NAME_TYPE,
						Literals.CHARACTER_DATA,
						""
								+ DataTypes.typeToString(controlEvent
										.getLimit().getType()));
				this.atts.addAttribute(
						"",
						"comparison",
						"comparison",
						Literals.CHARACTER_DATA,
						""
								+ ComparisonTypes.typeToString(controlEvent
										.getLimit().getComparison()));
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_LIMIT, Literals.XML_ELEMENT_NAME_LIMIT,
						this.atts);
				this.contentHandler.characters(controlEvent.getLimit()
						.getValue().toCharArray(), 0, controlEvent.getLimit()
						.getValue().length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_LIMIT, Literals.XML_ELEMENT_NAME_LIMIT);
			} else if (controlEvent.getEventType() == EventTypes.DETECTOR) {
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_DETECTOREVENT,
						Literals.XML_ELEMENT_NAME_DETECTOREVENT, this.atts);
				this.contentHandler.startElement("", "id", "id", this.atts);
				this.contentHandler.characters(controlEvent.getEvent().getId()
						.toCharArray(), 0, controlEvent.getEvent().getId()
						.length());
				this.contentHandler.endElement("", "id", "id");
			} else if (controlEvent.getEventType() == EventTypes.SCHEDULE) {
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SCHEDULEEVENT,
						Literals.XML_ELEMENT_NAME_SCHEDULEEVENT, this.atts);
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_INCIDENT, Literals.XML_ELEMENT_NAME_INCIDENT,
						this.atts);
				ScheduleEvent scheduleEvent = (ScheduleEvent) controlEvent
						.getEvent();
				String incident = scheduleEvent.getScheduleTime().getXmlValue();
				this.contentHandler.characters(incident.toCharArray(), 0,
						incident.length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_INCIDENT, Literals.XML_ELEMENT_NAME_INCIDENT);

				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_CHAINID, Literals.XML_ELEMENT_NAME_CHAINID,
						this.atts);
				String tag = Integer.toString(scheduleEvent.getScanModule()
						.getChain().getId());
				this.contentHandler.characters(tag.toCharArray(), 0,
						tag.length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_CHAINID, Literals.XML_ELEMENT_NAME_CHAINID);

				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SMID, Literals.XML_ELEMENT_NAME_SMID, this.atts);
				tag = Integer.toString(scheduleEvent.getScanModule().getId());
				this.contentHandler.characters(tag.toCharArray(), 0,
						tag.length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SMID, Literals.XML_ELEMENT_NAME_SMID);
			}
			this.atts.clear();
			switch (controlEvent.getEventType()) {
			case DETECTOR:
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_DETECTOREVENT,
						Literals.XML_ELEMENT_NAME_DETECTOREVENT);
				break;
			case MONITOR:
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_MONITOREVENT,
						Literals.XML_ELEMENT_NAME_MONITOREVENT);
				break;
			case SCHEDULE:
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SCHEDULEEVENT,
						Literals.XML_ELEMENT_NAME_SCHEDULEEVENT);
				break;
			default:
				break;
			}
			if (name.equals("pauseevent")) {
				atts.clear();
				this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_ACTION,
						Literals.XML_ELEMENT_NAME_ACTION, this.atts);
				this.contentHandler.characters(
						("" + ((PauseEvent) controlEvent).getEventAction()
								.toString()).toCharArray(), 0,
						("" + ((PauseEvent) controlEvent).getEventAction()
								.toString()).length());
				this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_ACTION, Literals.XML_ELEMENT_NAME_ACTION);
			}
			this.atts.clear();
			this.contentHandler.endElement("", name, name);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This method writes the selections.
	 * 
	 * @param selections
	 *            The selections that should be written.
	 * @return Returns if the write process was successful.
	 */
	private boolean writeSelections(final Selections selections) {
		this.atts.clear();
		try {
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SMSELECTION, Literals.XML_ELEMENT_NAME_SMSELECTION,
					this.atts);
			this.atts.clear();
			final StringBuffer stringBuffer = new StringBuffer();
			String[] stringArray = selections.getStepfunctions();
			for (int i = 0; i < stringArray.length; ++i) {
				stringBuffer.append(stringArray[i]);
				if (i != stringArray.length - 1) {
					stringBuffer.append(',');
				}
			}
			stringBuffer.toString().toCharArray();
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_STEPFUNCTION,
					Literals.XML_ELEMENT_NAME_STEPFUNCTION, this.atts);
			this.contentHandler.characters(stringBuffer.toString()
					.toCharArray(), 0, stringBuffer.toString().length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_STEPFUNCTION, Literals.XML_ELEMENT_NAME_STEPFUNCTION);

			stringBuffer.delete(0, stringBuffer.length());
			stringArray = selections.getSmtypes();
			for (int i = 0; i < stringArray.length; ++i) {
				stringBuffer.append(stringArray[i]);
				if (i != stringArray.length - 1) {
					stringBuffer.append(',');
				}
			}
			this.atts.clear();
			this.contentHandler.startElement("", Literals.XML_ELEMENT_NAME_SMTYPE, Literals.XML_ELEMENT_NAME_SMTYPE, this.atts);
			this.contentHandler.characters(stringBuffer.toString()
					.toCharArray(), 0, stringBuffer.toString().length());
			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SMTYPE, Literals.XML_ELEMENT_NAME_SMTYPE);

			stringBuffer.delete(0, stringBuffer.length());

			this.contentHandler.endElement("", Literals.XML_ELEMENT_NAME_SMSELECTION, Literals.XML_ELEMENT_NAME_SMSELECTION);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
}
