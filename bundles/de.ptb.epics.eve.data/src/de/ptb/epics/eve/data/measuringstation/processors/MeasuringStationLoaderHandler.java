package de.ptb.epics.eve.data.measuringstation.processors;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.PluginDataType;
import de.ptb.epics.eve.data.MethodTypes;
import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.DisplayGroup;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.PluginParameter;
import de.ptb.epics.eve.data.measuringstation.Access;
import de.ptb.epics.eve.data.measuringstation.Unit;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.event.MonitorEvent;

/**
 * This class is a SAX Handler to load a measuring station description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (- at -) ptb.de>
 * @author Marcus Michalsky
 */
public class MeasuringStationLoaderHandler extends DefaultHandler {

	// the measuring station object that is constructed by this handler
	private MeasuringStation measuringStation;

	// The current state of the handler
	private MeasuringStationLoaderStates state;

	// The current sub state of the handler
	private MeasuringStationLoaderSubStates subState;

	// The current plug in that is loaded by the handler
	private PlugIn currentPlugin;

	// The current plug in parameter that is loaded by the handler
	private PluginParameter currentPluginParameter;

	// The current detector that is loaded by the handler
	private Detector currentDetector;

	// The current option that is loaded by the handler
	private Option currentOption;

	// The current unit that is loaded by the handler
	private Unit currentUnit;

	// The current function that is loaded by the handler
	private Function currentFunction;

	// The current detector channel that is loaded by the handler
	private DetectorChannel currentDetectorChannel;

	// The current motor that is loaded by the handler
	private Motor currentMotor;

	// The current motor axis that is loaded by the handler
	private MotorAxis currentMotorAxis;

	// The current device that is loaded by the handler
	private Device currentDevice;

	// The current text buffer that is used for some operations
	private StringBuffer textBuffer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startDocument() throws SAXException {
		this.measuringStation = new MeasuringStation();
		this.state = MeasuringStationLoaderStates.ROOT;
		this.subState = MeasuringStationLoaderSubStates.NONE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement(final String namespaceURI, final String localName,
			final String qName, final Attributes atts) throws SAXException {

		switch (this.state) {

		case ROOT:
			if (qName.equals("location")) {
				this.state = MeasuringStationLoaderStates.LOCATION_NEXT;
			} else if (qName.equals("version")) {
				this.state = MeasuringStationLoaderStates.VERSION_NEXT;
			} else if (qName.equals("plugins")) {
				this.state = MeasuringStationLoaderStates.PLUGINS_LOADING;
			} else if (qName.equals("detectors")) {
				this.state = MeasuringStationLoaderStates.DETECTORS_LOADING;
			} else if (qName.equals("motors")) {
				this.state = MeasuringStationLoaderStates.MOTORS_LOADING;
			} else if (qName.equals("devices")) {
				this.state = MeasuringStationLoaderStates.DEVICES_LOADING;
			} else if (qName.equals("smselection")) {
				this.state = MeasuringStationLoaderStates.SELECTIONS_LOADING;
			}
			break;

		case PLUGINS_LOADING:
			if (qName.equals("plugin")) {

				final String pluginType = atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE);
				PluginTypes type = null;
				if (pluginType.equals("position")) {
					type = PluginTypes.POSITION;
				} else if (pluginType.equals("save")) {
					type = PluginTypes.SAVE;
				} else if (pluginType.equals("display")) {
					type = PluginTypes.DISPLAY;
				} else if (pluginType.equals("postscanpositioning")) {
					type = PluginTypes.POSTSCANPOSITIONING;
				}
				this.currentPlugin = new PlugIn(type, measuringStation);
				this.state = MeasuringStationLoaderStates.PLUGIN_LOADING;
			}
			break;

		case PLUGIN_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = MeasuringStationLoaderStates.PLUGIN_NAME_NEXT;
			} else if (qName.equals("location")) {
				this.state = MeasuringStationLoaderStates.PLUGIN_LOCATION_NEXT;
			} else if (qName.equals("parameter")) {
				this.state = MeasuringStationLoaderStates.PLUGIN_PARAMETER_NEXT;
				this.currentPluginParameter = new PluginParameter(this.currentPlugin,
						atts.getValue(Literals.XML_ATTRIBUTE_NAME_NAME), 
							PluginDataType.stringToType(atts
								.getValue("datatype")),
						atts.getValue("default"), Boolean.parseBoolean(atts
								.getValue("mandatory")));
			}
			break;

		case DETECTORS_LOADING:
			if (qName.equals("detector")) {
				this.currentDetector = new Detector();
				this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
			}
			break;

		case DETECTOR_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CLASS)) {
				this.state = MeasuringStationLoaderStates.DETECTOR_CLASSNAME_NEXT;
			} else if (qName.equals("id")) {
				this.state = MeasuringStationLoaderStates.DETECTOR_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = MeasuringStationLoaderStates.DETECTOR_NAME_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_OPTION)) {
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR) != null) {
					this.currentOption = new Option(Boolean.parseBoolean(atts
							.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR)));
				} else {
					this.currentOption = new Option();
				}
				this.state = MeasuringStationLoaderStates.DETECTOR_OPTION;
				this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_UNIT)) {
				this.currentUnit = new Unit();
				this.state = MeasuringStationLoaderStates.DETECTOR_UNIT;
				this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGER)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.DETECTOR_TRIGGER_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STOP)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.DETECTOR_STOP_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STATUS)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.DETECTOR_STATUS_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("channel")) {
				this.currentDetectorChannel = new DetectorChannel();
				if (atts.getValue("deferred") != null) {
					this.currentDetectorChannel.setDeferred(Boolean
							.parseBoolean(atts.getValue("deferred")));
				}
				if (atts.getValue("saveValue") != null) {
					this.currentDetectorChannel.setSaveValue(Boolean
							.parseBoolean(atts.getValue("saveValue")));
				}
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
			}
			break;

		case DETECTOR_CHANNEL_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CLASS)) {
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_CLASSNAME_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_NAME_NEXT;
			} else if (qName.equals("id")) {
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_ID_NEXT;
			} else if (qName.equals("read")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_READ_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_OPTION)) {
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR) != null) {
					this.currentOption = new Option(Boolean.parseBoolean(atts
							.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR)));
				} else {
					this.currentOption = new Option();
				}
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_OPTION;
				this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_UNIT)) {
				this.currentUnit = new Unit();
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_UNIT;
				this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGER)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_TRIGGER_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STOP)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_STOP_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STATUS)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_STATUS_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			}
			break;

		case MOTORS_LOADING:
			if (qName.equals("motor")) {
				this.currentMotor = new Motor();
				this.state = MeasuringStationLoaderStates.MOTOR_LOADING;

			}
			break;

		case MOTOR_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = MeasuringStationLoaderStates.MOTOR_NAME_NEXT;
			} else if (qName.equals("id")) {
				this.state = MeasuringStationLoaderStates.MOTOR_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_CLASS)) {
				this.state = MeasuringStationLoaderStates.MOTOR_CLASSNAME_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_UNIT)) {
				this.currentUnit = new Unit();
				this.state = MeasuringStationLoaderStates.MOTOR_UNIT;
				this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_OPTION)) {
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR) != null) {
					this.currentOption = new Option(Boolean.parseBoolean(atts
							.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR)));
				} else {
					this.currentOption = new Option();
				}
				this.state = MeasuringStationLoaderStates.MOTOR_OPTION;
				this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGER)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_TRIGGER_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("axis")) {
				this.currentMotorAxis = new MotorAxis();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CLASS)) {
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_CLASSNAME_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_NAME_NEXT;
			} else if (qName.equals("id")) {
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_ID_NEXT;
			} else if (qName.equals("goto")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_GOTO_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("position")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_POSITION_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STOP)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_STOP_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("highlimit")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_HIGHLIMIT_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("lowlimit")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOWLIMIT_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("limitviolation")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LIMITVIOLATION_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGER)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_TRIGGER_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_OPTION)) {
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR) != null) {
					this.currentOption = new Option(Boolean.parseBoolean(atts
							.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR)));
				} else {
					this.currentOption = new Option();
				}
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_OPTION;
				this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_UNIT)) {
				this.currentUnit = new Unit();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_UNIT;
				this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STATUS)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_STATUS_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("movedone")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_MOVEDONE_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("deadband")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_DEADBAND_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("offset")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_OFFSET_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("tweakvalue")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_TWEAKVALUE_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("tweakforward")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_TWEAKFORWARD_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			} else if (qName.equals("tweakreverse")) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_TWEAKREVERSE_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			}
			break;

		case DEVICES_LOADING:
			if (qName.equals("device")) {
				this.currentDevice = new Device();
				this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
			}
			break;

		case DEVICE_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = MeasuringStationLoaderStates.DEVICE_NAME_NEXT;
			} else if (qName.equals("id")) {
				this.state = MeasuringStationLoaderStates.DEVICE_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_CLASS)) {
				this.state = MeasuringStationLoaderStates.DEVICE_CLASSNAME_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.currentFunction = new Function();
				this.state = MeasuringStationLoaderStates.DEVICE_VALUE_LOADING;
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				return;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_DISPLAYGROUP)) {
				this.state = MeasuringStationLoaderStates.DEVICE_DISPLAYGROUP_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_UNIT)) {
				this.currentUnit = new Unit();
				this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
				this.state = MeasuringStationLoaderStates.DEVICE_UNIT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_OPTION)) {
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR) != null) {
					this.currentOption = new Option(Boolean.parseBoolean(atts
							.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR)));
				} else {
					this.currentOption = new Option();
				}
				this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
				this.state = MeasuringStationLoaderStates.DEVICE_OPTION;
			}
			break;

		case SELECTIONS_LOADING:
			if (qName.equals("stepfunction")) {
				this.state = MeasuringStationLoaderStates.SELECTIONS_STEPFUNCTION_NEXT;
			} else if (qName.equals("smtype")) {
				this.state = MeasuringStationLoaderStates.SELECTIONS_SMTYPE_NEXT;
			}
			break;

		}

		switch (this.subState) {

		case OPTION_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.subState = MeasuringStationLoaderSubStates.OPTION_NAME_NEXT;
			} else if (qName.equals("id")) {
				this.subState = MeasuringStationLoaderSubStates.OPTION_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.currentFunction = new Function();
				this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_DISPLAYGROUP)) {
				this.subState = MeasuringStationLoaderSubStates.OPTION_DISPLAYGROUP_NEXT;
			}
			break;

		case UNIT_LOADING:
			if (qName.equals("unitstring")) {
				this.subState = MeasuringStationLoaderSubStates.UNIT_VALUE_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_ACCESS)) {
				String methodType = atts.getValue("method");
				this.currentUnit.setAccess(new Access(MethodTypes
						.stringToType(methodType)));
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE) != null) {
					this.currentUnit.getAccess().setType(
							DataTypes.stringToType(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_TYPE)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_COUNT) != null) {
					this.currentUnit.getAccess().setCount(
							Integer.parseInt(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_COUNT)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_TRANSPORT) != null) {
					this.currentUnit.getAccess().setTransport(
							TransportTypes.stringToType(atts
									.getValue(Literals.XML_ATTRIBUTE_NAME_TRANSPORT)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_TIMEOUT) != null) {
					this.currentUnit.getAccess().setTimeout(
							Double.parseDouble(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_TIMEOUT)));
				}
				this.subState = MeasuringStationLoaderSubStates.UNIT_ACCESS_NEXT;
			}
			break;

		case FUNCTION_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.currentFunction.setValue(new TypeValue(DataTypes
						.stringToType(atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE))));

				this.subState = MeasuringStationLoaderSubStates.FUNCTION_VALUE_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_ACCESS)) {
				String methodType = atts.getValue("method");
				this.currentFunction.setAccess(new Access(MethodTypes
						.stringToType(methodType)));
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE) != null) {
					this.currentFunction.getAccess().setType(
							DataTypes.stringToType(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_TYPE)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_COUNT) != null) {
					this.currentFunction.getAccess().setCount(
							Integer.parseInt(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_COUNT)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_TRANSPORT) != null) {
					this.currentFunction.getAccess().setTransport(
							TransportTypes.stringToType(atts
									.getValue(Literals.XML_ATTRIBUTE_NAME_TRANSPORT)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_TIMEOUT) != null) {
					this.currentFunction.getAccess().setTimeout(
							Double.parseDouble(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_TIMEOUT)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR) != null) {
					this.currentFunction.getAccess().setMonitor(
							Boolean.parseBoolean(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_MONITOR)));
				}
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_ACCESS_NEXT;
			}
			break;

		case OPTION_VALUE_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.currentFunction.setValue(new TypeValue(DataTypes
						.stringToType(atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE))));
				this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_VALUE_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_ACCESS)) {
				String methodType = atts.getValue("method");
				this.currentFunction.setAccess(new Access(MethodTypes
						.stringToType(methodType)));
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE) != null) {
					this.currentFunction.getAccess().setType(
							DataTypes.stringToType(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_TYPE)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_COUNT) != null) {
					this.currentFunction.getAccess().setCount(
							Integer.parseInt(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_COUNT)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_TRANSPORT) != null) {
					this.currentFunction.getAccess().setTransport(
							TransportTypes.stringToType(atts
									.getValue(Literals.XML_ATTRIBUTE_NAME_TRANSPORT)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_TIMEOUT) != null) {
					this.currentFunction.getAccess().setTimeout(
							Double.parseDouble(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_TIMEOUT)));
				}
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_MONITOR) != null) {
					this.currentFunction.getAccess().setMonitor(
							Boolean.parseBoolean(atts.getValue(
									Literals.XML_ATTRIBUTE_NAME_MONITOR)));
				}
				this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_ACCESS_NEXT;
			}
			break;

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void characters(final char[] ch, final int start, final int length) {

		String s = new String(ch, start, length).trim();
		if (textBuffer == null) {
			textBuffer = new StringBuffer(s);
		} else {
			textBuffer.append(s);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {

		switch (this.state) {
		case LOCATION_NEXT:
			this.measuringStation.setName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.LOCATION_READ;
			break;
		
		case VERSION_NEXT:
			this.measuringStation.setVersion(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.VERSION_READ;
			break;

		case PLUGIN_NAME_NEXT:
			this.currentPlugin.setName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.PLUGIN_NAME_READ;
			break;

		case PLUGIN_LOCATION_NEXT:
			this.currentPlugin.setLocation(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.PLUGIN_LOCATION_READ;
			break;

		case PLUGIN_PARAMETER_NEXT:
			this.currentPluginParameter.setValues(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.PLUGIN_PARAMETER_READ;
			break;

		case DETECTOR_CLASSNAME_NEXT:
			this.currentDetector.setClassName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.DETECTOR_CLASSNAME_READ;
			break;

		case DETECTOR_ID_NEXT:
			this.currentDetector.setId(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.DETECTOR_ID_READ;
			break;

		case DETECTOR_NAME_NEXT:
			this.currentDetector.setName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.DETECTOR_NAME_READ;
			break;

		case DETECTOR_CHANNEL_CLASSNAME_NEXT:
			this.currentDetectorChannel.setClassName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_CLASSNAME_READ;
			break;

		case DETECTOR_CHANNEL_ID_NEXT:
			this.currentDetectorChannel.setId(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_ID_READ;
			break;

		case DETECTOR_CHANNEL_NAME_NEXT:
			this.currentDetectorChannel.setName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
			break;

		case MOTOR_NAME_NEXT:
			this.currentMotor.setName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
			break;
		case MOTOR_ID_NEXT:
			this.currentMotor.setId(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.MOTOR_ID_READ;
			break;

		case MOTOR_CLASSNAME_NEXT:
			this.currentMotor.setClassName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.MOTOR_CLASSNAME_READ;
			break;

		case MOTOR_AXIS_CLASSNAME_NEXT:
		case MOTOR_AXIS_CLASSNAME_READ:
			this.currentMotorAxis.setClassName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			break;

		case MOTOR_AXIS_NAME_NEXT:
			this.currentMotorAxis.setName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			break;

		case MOTOR_AXIS_ID_NEXT:
			this.currentMotorAxis.setId(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.MOTOR_AXIS_ID_READ;
			break;

		case DEVICE_NAME_NEXT:
			this.currentDevice.setName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
			break;

		case DEVICE_ID_NEXT:
			this.currentDevice.setId(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.DEVICE_ID_READ;
			break;

		case DEVICE_CLASSNAME_NEXT:
			this.currentDevice.setClassName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.DEVICE_CLASSNAME_READ;
			break;

		case DEVICE_DISPLAYGROUP_NEXT:
			this.currentDevice.setDisplaygroup(DisplayGroup.valueOf(textBuffer
					.toString().toUpperCase()));
			this.state = MeasuringStationLoaderStates.DEVICE_DISPLAYGROUP_READ;
			break;

		case SELECTIONS_STEPFUNCTION_NEXT:
			this.measuringStation.getSelections().setStepfunctions(
					textBuffer.toString().split(","));
			this.state = MeasuringStationLoaderStates.SELECTIONS_STEPFUNCTION_READ;
			break;

		case SELECTIONS_SMTYPE_NEXT:
			this.measuringStation.getSelections().setSmtypes(
					textBuffer.toString().split(","));
			this.state = MeasuringStationLoaderStates.SELECTIONS_SMTYPE_READ;
			break;

		}

		switch (this.subState) {

		case OPTION_NAME_NEXT:
			this.currentOption.setName(textBuffer.toString().intern());
			this.subState = MeasuringStationLoaderSubStates.OPTION_NAME_READ;
			break;
		case OPTION_ID_NEXT:
			this.currentOption.setId(textBuffer.toString());
			this.subState = MeasuringStationLoaderSubStates.OPTION_ID_READ;
			break;

		case OPTION_DISPLAYGROUP_NEXT:
			this.currentOption.setDisplaygroup(DisplayGroup.valueOf(textBuffer
					.toString().toUpperCase()));
			this.subState = MeasuringStationLoaderSubStates.OPTION_DISPLAYGROUP_READ;
			break;

		case UNIT_VALUE_NEXT:
			this.currentUnit.setValue(textBuffer.toString());
			this.subState = MeasuringStationLoaderSubStates.UNIT_VALUE_READ;
			break;
		case UNIT_ACCESS_NEXT:
			this.currentUnit.getAccess().setVariableID(textBuffer.toString());
			this.subState = MeasuringStationLoaderSubStates.UNIT_ACCESS_READ;
			break;

		case FUNCTION_VALUE_NEXT:
			this.currentFunction.getValue().setValues(textBuffer.toString());
			this.subState = MeasuringStationLoaderSubStates.FUNCTION_VALUE_READ;
			break;
		case FUNCTION_ACCESS_NEXT:
			this.currentFunction.getAccess().setVariableID(
					textBuffer.toString());
			this.subState = MeasuringStationLoaderSubStates.FUNCTION_ACCESS_READ;
			break;

		case OPTION_VALUE_VALUE_NEXT:
			this.currentFunction.getValue().setValues(textBuffer.toString());
			this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_VALUE_READ;
			break;

		case OPTION_VALUE_ACCESS_NEXT:
			this.currentFunction.getAccess().setVariableID(
					textBuffer.toString());
			this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_ACCESS_READ;
			break;

		}

		textBuffer = null;

		/* ***************************************************************** */
		/* ***************************************************************** */

		switch (this.state) {
		case LOCATION_READ:
			if (qName.equals("location")) {
				this.state = MeasuringStationLoaderStates.ROOT;
			}
			break;
			
		case VERSION_READ:
			if (qName.equals("version")) {
				this.state = MeasuringStationLoaderStates.ROOT;
			}
			break;

		case PLUGINS_LOADING:
			if (qName.equals("plugins")) {
				this.state = MeasuringStationLoaderStates.ROOT;
			}
			break;

		case PLUGIN_LOADING:
			if (qName.equals("plugin")) {
				this.measuringStation.add(this.currentPlugin);
				this.state = MeasuringStationLoaderStates.PLUGINS_LOADING;
			}
			break;

		case PLUGIN_NAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = MeasuringStationLoaderStates.PLUGIN_LOADING;
			}
			break;

		case PLUGIN_LOCATION_READ:
			if (qName.equals("location")) {
				this.state = MeasuringStationLoaderStates.PLUGIN_LOADING;
			}
			break;

		case PLUGIN_PARAMETER_NEXT:
		case PLUGIN_PARAMETER_READ:
			if (qName.equals("parameter")) {
				this.currentPlugin.add(this.currentPluginParameter);
				this.state = MeasuringStationLoaderStates.PLUGIN_LOADING;
			}

		case DETECTORS_LOADING:
			if (qName.equals("detectors")) {
				this.state = MeasuringStationLoaderStates.ROOT;
			}
			break;

		case DETECTOR_LOADING:
			if (qName.equals("detector")) {
				this.measuringStation.add(this.currentDetector);
				this.state = MeasuringStationLoaderStates.DETECTORS_LOADING;
			}
			break;

		case DETECTOR_CLASSNAME_NEXT:
		case DETECTOR_CLASSNAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CLASS)) {
				this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
			}
			break;
		case DETECTOR_ID_READ:
			if (qName.equals("id")) {
				this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
			}
			break;
		case DETECTOR_NAME_NEXT:
		case DETECTOR_NAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
			}
			break;

		case DETECTOR_OPTION:
			if (qName.equals(Literals.XML_ELEMENT_NAME_OPTION)) {
				this.currentDetector.add(this.currentOption);
				this.measuringStation.registerOption(this.currentOption);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
			}
			break;

		case DETECTOR_UNIT:
			if (qName.equals(Literals.XML_ELEMENT_NAME_UNIT)) {
				this.currentDetector.setUnit(this.currentUnit);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
			}
			break;

		case DETECTOR_TRIGGER_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGER)) {
				this.currentDetector.setTrigger(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
			}
			break;

		case DETECTOR_STOP_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STOP)) {
				this.currentDetector.setStop(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
			}
			break;
		
		case DETECTOR_STATUS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STATUS)) {
				this.currentDetector.setStatus(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
			}
			break;
		
		case DETECTOR_CHANNEL_LOADING:
			if (qName.equals("channel")) {
				this.currentDetector.add(this.currentDetectorChannel);
				this.measuringStation
						.registerDetectorChannel(this.currentDetectorChannel);
				this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
			}
			break;

		case DETECTOR_CHANNEL_CLASSNAME_NEXT:
		case DETECTOR_CHANNEL_CLASSNAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CLASS)) {
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
			}
			break;

		case DETECTOR_CHANNEL_ID_READ:
			if (qName.equals("id")) {
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
			}
			break;
		case DETECTOR_CHANNEL_READ_LOADING:
			if (qName.equals("read")) {
				this.currentDetectorChannel.setRead(this.currentFunction);
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
				this.subState = MeasuringStationLoaderSubStates.NONE;
			}
			break;

		case DETECTOR_CHANNEL_OPTION:
			if (qName.equals(Literals.XML_ELEMENT_NAME_OPTION)) {
				this.currentDetectorChannel.add(this.currentOption);
				this.measuringStation.registerOption(this.currentOption);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
			}
			break;
			
		case DETECTOR_CHANNEL_UNIT:
			if (qName.equals(Literals.XML_ELEMENT_NAME_UNIT)) {
				this.currentDetectorChannel.setUnit(this.currentUnit);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
			}
			break;
			
		case DETECTOR_CHANNEL_TRIGGER_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGER)) {
				this.currentDetectorChannel.setTrigger(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
			}
			break;
		
		case DETECTOR_CHANNEL_STOP_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STOP)) {
				this.currentDetectorChannel.setStop(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
			}
			break;
		
		case DETECTOR_CHANNEL_STATUS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STATUS)) {
				this.currentDetectorChannel.setStatus(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
			}
			break;
		
		case MOTORS_LOADING:
			if (qName.equals("motors")) {
				this.state = MeasuringStationLoaderStates.ROOT;
			}
			break;

		case MOTOR_LOADING:
			if (qName.equals("motor")) {
				this.measuringStation.add(this.currentMotor);
				this.state = MeasuringStationLoaderStates.MOTORS_LOADING;
			}
			break;
		case MOTOR_ID_READ:
			if (qName.equals("id")) {
				this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
			}
			break;
		case MOTOR_CLASSNAME_NEXT:
		case MOTOR_CLASSNAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CLASS)) {
				this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
			}
			break;

		case MOTOR_UNIT:
			if (qName.equals(Literals.XML_ELEMENT_NAME_UNIT)) {
				this.currentMotor.setUnit(this.currentUnit);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
			}
			break;
		case MOTOR_OPTION:
			if (qName.equals(Literals.XML_ELEMENT_NAME_OPTION)) {
				this.currentMotor.add(this.currentOption);
				this.measuringStation.registerOption(this.currentOption);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
			}
			break;
		case MOTOR_TRIGGER_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGER)) {
				this.currentMotor.setTrigger(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
			}
			break;

		case MOTOR_AXIS_LOADING:
			if (qName.equals("axis")) {
				this.currentMotor.add(this.currentMotorAxis);
				this.measuringStation.registerMotorAxis(this.currentMotorAxis);
				this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
			}
			break;

		case MOTOR_AXIS_ID_READ:
			if (qName.equals("id")) {
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_STOP_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STOP)) {
				this.currentMotorAxis.setStop(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_TRIGGER_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGER)) {
				this.currentMotorAxis.setTrigger(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_OPTION:
			if (qName.equals(Literals.XML_ELEMENT_NAME_OPTION)) {
				this.currentMotorAxis.add(this.currentOption);
				this.measuringStation.registerOption(this.currentOption);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_UNIT:
			if (qName.equals(Literals.XML_ELEMENT_NAME_UNIT)) {
				this.currentMotorAxis.setUnit(this.currentUnit);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_STATUS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STATUS)) {
				this.currentMotorAxis.setStatus(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_MOVEDONE_LOADING:
			if (qName.equals("movedone")) {
				this.currentMotorAxis.setMoveDone(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_POSITION_LOADING:
			if (qName.equals("position")) {
				this.currentMotorAxis.setPosition(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_DEADBAND_LOADING:
			if (qName.equals("deadband")) {
				this.currentMotorAxis.setDeadband(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_OFFSET_LOADING:
			if (qName.equals("offset")) {
				this.currentMotorAxis.setOffset(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_TWEAKVALUE_LOADING:
			if (qName.equals("tweakvalue")) {
				this.currentMotorAxis.setTweakValue(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_TWEAKFORWARD_LOADING:
			if (qName.equals("tweakforward")) {
				this.currentMotorAxis.setTweakForward(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_TWEAKREVERSE_LOADING:
			if (qName.equals("tweakreverse")) {
				this.currentMotorAxis.setTweakReverse(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			}
			break;

		case MOTOR_AXIS_GOTO_LOADING:
			if (qName.equals("goto")) {
				this.currentMotorAxis.setGoto(this.currentFunction);
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				this.subState = MeasuringStationLoaderSubStates.NONE;
			}
			break;

		case MOTOR_AXIS_HIGHLIMIT_LOADING:
			if (qName.equals("highlimit")) {
				this.currentMotorAxis.setSoftHighLimit(this.currentFunction);
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				this.subState = MeasuringStationLoaderSubStates.NONE;
			}
			break;

		case MOTOR_AXIS_LOWLIMIT_LOADING:
			if (qName.equals("lowlimit")) {
				this.currentMotorAxis.setSoftLowLimit(this.currentFunction);
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				this.subState = MeasuringStationLoaderSubStates.NONE;
			}
			break;

		case MOTOR_AXIS_LIMITVIOLATION_LOADING:
			if (qName.equals("limitviolation")) {
				this.currentMotorAxis.setLimitViolation(this.currentFunction);
				this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				this.subState = MeasuringStationLoaderSubStates.NONE;
			}
			break;
			
		case DEVICES_LOADING:
			if (qName.equals("devices")) {
				this.state = MeasuringStationLoaderStates.ROOT;
			}
			break;

		case DEVICE_LOADING:
			if (qName.equals("device")) {
				this.measuringStation.add(this.currentDevice);
				this.state = MeasuringStationLoaderStates.DEVICES_LOADING;
			}
			break;

		case DEVICE_ID_READ:
			if (qName.equals("id")) {
				this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
			}
			break;

		case DEVICE_CLASSNAME_NEXT:
		case DEVICE_CLASSNAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CLASS)) {
				this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
			}
			break;

		case DEVICE_VALUE_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.currentDevice.setValue(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
			}
			break;

		case DEVICE_DISPLAYGROUP_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_DISPLAYGROUP)) {
				this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
			}
			break;

		case DEVICE_UNIT:
			if (qName.equals(Literals.XML_ELEMENT_NAME_UNIT)) {
				this.currentDevice.setUnit(this.currentUnit);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
			}
			break;

		case DEVICE_OPTION:
			if (qName.equals(Literals.XML_ELEMENT_NAME_OPTION)) {
				this.currentDevice.add(this.currentOption);
				this.measuringStation.registerOption(this.currentOption);
				this.subState = MeasuringStationLoaderSubStates.NONE;
				this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
			}
			break;

		case SELECTIONS_LOADING:
			if (qName.equals("smselection")) {
				this.state = MeasuringStationLoaderStates.ROOT;
			}
			break;

		case SELECTIONS_STEPFUNCTION_READ:
			if (qName.equals("stepfunction")) {
				this.state = MeasuringStationLoaderStates.SELECTIONS_LOADING;
			}
			break;

		case SELECTIONS_SMTYPE_READ:
			if (qName.equals("smtype")) {
				this.state = MeasuringStationLoaderStates.SELECTIONS_LOADING;
			}
			break;

		}

		switch (this.subState) {
		case OPTION_NAME_NEXT:
		case OPTION_NAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
			}
			break;
		case OPTION_ID_READ:
			if (qName.equals("id")) {
				this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
			}
			break;

		case OPTION_VALUE_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.currentOption.setValue(this.currentFunction);
				this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
			}
			break;
		case OPTION_DISPLAYGROUP_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_DISPLAYGROUP)) {
				this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
			}
			break;

		case UNIT_VALUE_READ:
			if (qName.equals("unitstring")) {
				this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
			}
			break;
		case UNIT_ACCESS_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ACCESS)) {
				this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
			}
			break;

		case FUNCTION_VALUE_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			}
			break;
		case FUNCTION_ACCESS_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ACCESS)) {
				this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
			}
			break;

		case OPTION_VALUE_VALUE_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_LOADING;
			}
			break;
		case OPTION_VALUE_ACCESS_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ACCESS)) {
				this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_LOADING;
			}
			break;

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endDocument() throws SAXException {

		// loop through axes and create events for every
		// position access if monitor equals true
		// Create events for options if monitor equals true.

		for (Motor m : this.measuringStation.getMotors()) {
			for (MotorAxis ma : m.getAxes()) {
				if (ma.getPosition().getAccess().getMonitor()) {
					this.measuringStation.add(new MonitorEvent(ma));
				}
				for (Option o : ma.getOptions()) {
					if (o.getValue().getAccess().getMonitor()) {
						this.measuringStation.add(new MonitorEvent(o));
					}
				}
			}
			for (Option o : m.getOptions()) {
				if (o.getValue().getAccess().getMonitor()) {
					this.measuringStation.add(new MonitorEvent(o));
				}
			}
		}

		// loop through detector channels and create events for every
		// read access with monitor equals true
		// Create events for options if monitor equals true.
		for (Detector det : this.measuringStation.getDetectors()) {
			for (DetectorChannel ch : det.getChannels()) {
				if (ch.getRead().getAccess().getMonitor()) {
					this.measuringStation.add(new MonitorEvent(ch));
				}
				for (Option o : ch.getOptions()) {
					if (o.getValue().getAccess().getMonitor()) {
						this.measuringStation.add(new MonitorEvent(o));
					}
				}
			}
			for (Option o : det.getOptions()) {
				if (o.getValue().getAccess().getMonitor()) {
					this.measuringStation.add(new MonitorEvent(o));
				}
			}
		}

		// loop through devices and create events for every
		// value access with monitor equals true
		for (Device dev : this.measuringStation.getDevices()) {
			if (dev.getValue().getAccess().getMonitor()) {
				this.measuringStation.add(new MonitorEvent(dev));
			}
		}
	}

	/**
	 * Returns the loaded measuring station.
	 * 
	 * @return the loaded measuring station
	 */
	public MeasuringStation getMeasuringStation() {
		return this.measuringStation;
	}
}