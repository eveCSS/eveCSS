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
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.PluginParameter;
import de.ptb.epics.eve.data.measuringstation.Access;
import de.ptb.epics.eve.data.measuringstation.Unit;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * This class is a SAX Handler to load a measuring station description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (- at -) ptb.de>
 *
 */
public class MeasuringStationLoaderHandler extends DefaultHandler {

	// the measuring station object that is constructed by this handler
	private MeasuringStation measuringStation;
	
	/**
	 * The current state of the handler.
	 */
	private MeasuringStationLoaderStates state;
	
	/**
	 * The current sub state of the handler.
	 */
	private MeasuringStationLoaderSubStates subState;
	
	/**
	 * The current plug in that is loaded by the handler.
	 */
	private PlugIn currentPlugin;
	
	/**
	 * The current plug in parameter that is loaded by the handler.
	 */
	private PluginParameter currentPluginParameter;
	
	/**
	 * The current detector that is loaded by the handler.
	 */
	private Detector currentDetector;
	
	/**
	 * The current option that is loaded by the handler.
	 */
	private Option currentOption;
	
	/**
	 * The current unit that is loaded by the handler.
	 */
	private Unit currentUnit;
	
	/**
	 * The current function that is loaded by the handler.
	 */
	private Function currentFunction;
	
	/**
	 * The current detector channel that is loaded by the handler.
	 */
	private DetectorChannel currentDetectorChannel;
	
	/**
	 * The current motor that is loaded by the handler.
	 */
	private Motor currentMotor;
	
	/**
	 * The current motor axis that is loaded by the handler.
	 */
	private MotorAxis currentMotorAxis;
	
	/**
	 * The current device that is loaded by the handler.
	 */
	private Device currentDevice;
	
	/**
	 * The current text buffer that is used for some operations.
	 */
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
	public void startElement(final String namespaceURI, 
							  final String localName, 
							  final String qName, 
							  final Attributes atts) throws SAXException {
		
		/*System.err.println( qName + " open: " + this.state + " / " + this.subState);
		System.err.println( qName );
		System.err.println( this.state );
		System.err.println( this.subState );
		System.err.println( "----------------------\n" );*/
		
		switch(this.state) {
		
			case ROOT:
				if( qName.equals( "version" ) ) {
					this.state = MeasuringStationLoaderStates.VERSION_NEXT;
				} else if(qName.equals("measuringstation")) {
					this.state = MeasuringStationLoaderStates.MEASURINGSTATION_NEXT;
				} else if( qName.equals( "plugins" ) ) {
					this.state = MeasuringStationLoaderStates.PLUGINS_LOADING;
				} else if( qName.equals( "detectors" ) ) {
					this.state = MeasuringStationLoaderStates.DETECTORS_LOADING;
				} else if( qName.equals( "motors" ) ) {
					this.state = MeasuringStationLoaderStates.MOTORS_LOADING;
				} else if( qName.equals( "devices" ) ) {
					this.state = MeasuringStationLoaderStates.DEVICES_LOADING;
				} else if( qName.equals( "smselection" ) ) {
					this.state = MeasuringStationLoaderStates.SELECTIONS_LOADING;
				}
				break;
				
			case PLUGINS_LOADING:
				if( qName.equals( "plugin" ) ) {
					
					final String pluginType = atts.getValue( "type" );
					PluginTypes type = null;
					if( pluginType.equals( "position" ) ) {
						type = PluginTypes.POSITION;
					} else if( pluginType.equals( "save" ) ) {
						type = PluginTypes.SAVE;
					} else if( pluginType.equals( "display" ) ) {
						type = PluginTypes.DISPLAY;
					} else if( pluginType.equals( "postscanpositioning" ) ) {
						type = PluginTypes.POSTSCANPOSITIONING;
					}
					this.currentPlugin = new PlugIn(type);
					this.state = MeasuringStationLoaderStates.PLUGIN_LOADING;
				}
				break;
				
			case PLUGIN_LOADING:
				if( qName.equals( "name" ) ) {
					this.state = MeasuringStationLoaderStates.PLUGIN_NAME_NEXT;
				} else if( qName.equals( "location" ) ) {
					this.state = MeasuringStationLoaderStates.PLUGIN_LOCATION_NEXT;
				} else if( qName.equals( "parameter" ) ) {
					this.state = MeasuringStationLoaderStates.PLUGIN_PARAMETER_NEXT;
					this.currentPluginParameter = new PluginParameter( atts.getValue( "name" ), PluginDataType.stringToType( atts.getValue( "datatype" ) ), atts.getValue( "default" ), Boolean.parseBoolean( atts.getValue( "mandatory" ) ) );	
				}
				break;
				
			case DETECTORS_LOADING:
				if( qName.equals( "detector" ) ) {
					this.currentDetector = new Detector();
					this.state = MeasuringStationLoaderStates.DETECTOR_LOADING;
				}
				break;
				
			case DETECTOR_LOADING:
				if( qName.equals( "class" ) ) {
					this.state = MeasuringStationLoaderStates.DETECTOR_CLASSNAME_NEXT;
				} else if( qName.equals( "id" ) ) {
					this.state = MeasuringStationLoaderStates.DETECTOR_ID_NEXT;
				} else if( qName.equals( "name" ) ) {
					this.state = MeasuringStationLoaderStates.DETECTOR_NAME_NEXT;
				} else if( qName.equals( "option" ) ) {
					this.currentOption = new Option();
					this.state = MeasuringStationLoaderStates.DETECTOR_OPTION;
					this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
				} else if( qName.equals( "unit" ) ) {
					this.currentUnit = new Unit();
					this.state = MeasuringStationLoaderStates.DETECTOR_UNIT;
					this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
				} else if( qName.equals( "trigger" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.DETECTOR_TRIGGER_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "channel" ) ) {
					this.currentDetectorChannel = new DetectorChannel();
					this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
				}
				break;
				
			case DETECTOR_CHANNEL_LOADING:
				if( qName.equals( "class" ) ) {
					this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_CLASSNAME_NEXT;
				} else if( qName.equals( "name" ) ) {
					this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_NAME_NEXT;
				} else if( qName.equals( "id" ) ) {
					this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_ID_NEXT;
				} else if( qName.equals( "read" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_READ_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "option" ) ) {
					this.currentOption = new Option();
					this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_OPTION;
					this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
				} else if( qName.equals( "unit" ) ) {
					this.currentUnit = new Unit();
					this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_UNIT;
					this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
				} else if( qName.equals( "trigger" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_TRIGGER_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				}
				break;
				
			case MOTORS_LOADING:
				if( qName.equals( "motor" ) ) {
					this.currentMotor = new Motor();
					this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
					
				}
				break;
				
			case MOTOR_LOADING:
				if( qName.equals( "name" ) ) {
					this.state = MeasuringStationLoaderStates.MOTOR_NAME_NEXT;
				} else if( qName.equals( "id" ) ) {
					this.state = MeasuringStationLoaderStates.MOTOR_ID_NEXT;
				} else if( qName.equals( "class" ) ) {
					this.state = MeasuringStationLoaderStates.MOTOR_CLASSNAME_NEXT;
				} else if( qName.equals( "unit" ) ) {
					this.currentUnit = new Unit();
					this.state = MeasuringStationLoaderStates.MOTOR_UNIT;
					this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
				} else if( qName.equals( "option" ) ) {
					this.currentOption = new Option();
					this.state = MeasuringStationLoaderStates.MOTOR_OPTION;
					this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
				} else if( qName.equals( "trigger" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_TRIGGER_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "axis" ) ) {
					this.currentMotorAxis = new MotorAxis();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_LOADING:
				if( qName.equals( "class" ) ) {
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_CLASSNAME_NEXT;
				} else if( qName.equals( "name" ) ) {
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_NAME_NEXT;
				} else if( qName.equals( "id" ) ) {
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_ID_NEXT;
				} else if( qName.equals( "goto" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_GOTO_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "stop" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_STOP_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "trigger" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_TRIGGER_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "option" ) ) {
					this.currentOption = new Option();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_OPTION;
					this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
				} else if( qName.equals( "unit" ) ) {
					this.currentUnit = new Unit();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_UNIT;
					this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
				} else if( qName.equals( "status" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_STATUS_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "position" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_POSITION_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "deadband" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_DEADBAND_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "offset" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_OFFSET_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if(qName.equals("setmode")) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_SETMODE_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "tweakvalue" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_TWEAKVALUE_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "tweakforward" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_TWEAKFORWARD_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				} else if( qName.equals( "tweakreverse" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_TWEAKREVERSE_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				}
				break;
				
			case DEVICES_LOADING:
				if( qName.equals( "device" ) ) {
					this.currentDevice = new Device();
					this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
				}
				break;
				
			case DEVICE_LOADING:
				if( qName.equals( "name" ) ) {
					this.state = MeasuringStationLoaderStates.DEVICE_NAME_NEXT;
				} else if( qName.equals( "id" ) ) {
					this.state = MeasuringStationLoaderStates.DEVICE_ID_NEXT;
				} else if( qName.equals( "class" ) ) {
					this.state = MeasuringStationLoaderStates.DEVICE_CLASSNAME_NEXT;
				} else if( qName.equals( "value" ) ) {
					this.currentFunction = new Function();
					this.state = MeasuringStationLoaderStates.DEVICE_VALUE_LOADING;
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
					return;
				} else if( qName.equals( "displaygroup" ) ) {
					this.state = MeasuringStationLoaderStates.DEVICE_DISPLAYGROUP_NEXT;
				} else if( qName.equals( "unit" ) ) {
					this.currentUnit = new Unit();
					this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
					this.state = MeasuringStationLoaderStates.DEVICE_UNIT;
				} else if( qName.equals( "option" ) ) {
					this.currentOption = new Option();
					this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
					this.state = MeasuringStationLoaderStates.DEVICE_OPTION;
				}
				break;
				
			case SELECTIONS_LOADING:
				if( qName.equals( "stepfunction" ) ) {
					this.state = MeasuringStationLoaderStates.SELECTIONS_STEPFUNCTION_NEXT;
				} else if( qName.equals( "smtype" ) ) {
					this.state = MeasuringStationLoaderStates.SELECTIONS_SMTYPE_NEXT;
				}
				break;
				
		}
		
		switch( this.subState ) {
		
			case OPTION_LOADING:
				if( qName.equals( "name" ) ) {
					this.subState = MeasuringStationLoaderSubStates.OPTION_NAME_NEXT;
				} else if( qName.equals( "id" ) ) {
					this.subState = MeasuringStationLoaderSubStates.OPTION_ID_NEXT;
				} else if( qName.equals( "value" ) ) {
					this.currentFunction = new Function();
					this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_LOADING;
				} else if( qName.equals( "displaygroup" ) ) {
					this.subState = MeasuringStationLoaderSubStates.OPTION_DISPLAYGROUP_NEXT;
				}
				break;
				
			case UNIT_LOADING:
				if( qName.equals( "unitstring" ) ) {
					this.subState = MeasuringStationLoaderSubStates.UNIT_VALUE_NEXT;
				} else if( qName.equals( "access" ) ) {
					String methodType = atts.getValue( "method" );
					this.currentUnit.setAccess( new Access( MethodTypes.stringToType( methodType ) ) );
					if( atts.getValue( "type" ) != null) {
						this.currentUnit.getAccess().setType( DataTypes.stringToType( atts.getValue( "type" ) ) );
					}
					if( atts.getValue( "count" ) != null ) {
						this.currentUnit.getAccess().setCount( Integer.parseInt( atts.getValue( "count" ) ) );
					}
					if( atts.getValue( "transport" ) != null ) {
						this.currentUnit.getAccess().setTransport( TransportTypes.stringToType( atts.getValue( "transport" ) ) );
					}
					if( atts.getValue( "timeout" ) != null ) {
						this.currentUnit.getAccess().setTimeout( Double.parseDouble( atts.getValue( "timeout" ) ) );
					}
					this.subState = MeasuringStationLoaderSubStates.UNIT_ACCESS_NEXT;
				}
				break;
				
			case FUNCTION_LOADING:
				if( qName.equals( "value" ) ) {
					this.currentFunction.setValue( new TypeValue( DataTypes.stringToType( atts.getValue( "type" ) ) ) );
					
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_VALUE_NEXT;
				} else if( qName.equals( "access" ) ) {
					String methodType = atts.getValue( "method" );
					this.currentFunction.setAccess( new Access( MethodTypes.stringToType( methodType ) ) );
					if( atts.getValue( "type" ) != null) {
						this.currentFunction.getAccess().setType( DataTypes.stringToType( atts.getValue( "type" ) ) );
					}
					if( atts.getValue( "count" ) != null ) {
						this.currentFunction.getAccess().setCount( Integer.parseInt( atts.getValue( "count" ) ) );
					}
					if( atts.getValue( "transport" ) != null ) {
						this.currentFunction.getAccess().setTransport( TransportTypes.stringToType( atts.getValue( "transport" ) ) );
					}
					if( atts.getValue( "timeout" ) != null ) {
						this.currentFunction.getAccess().setTimeout( Double.parseDouble( atts.getValue( "timeout" ) ) );
					}
					if( atts.getValue( "monitor" ) != null ) {
						this.currentFunction.getAccess().setMonitor( Boolean.parseBoolean( atts.getValue( "monitor" ) ) );
					}
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_ACCESS_NEXT;
				}
				break;
				
			case OPTION_VALUE_LOADING:
				if( qName.equals( "value" ) ) {
					this.currentFunction.setValue( new TypeValue( DataTypes.stringToType( atts.getValue( "type" ) ) ) );
					this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_VALUE_NEXT;
				} else if( qName.equals( "access" ) ) {
					String methodType = atts.getValue( "method" );
					this.currentFunction.setAccess( new Access( MethodTypes.stringToType( methodType ) ) );
					if( atts.getValue( "type" ) != null) {
						this.currentFunction.getAccess().setType( DataTypes.stringToType( atts.getValue( "type" ) ) );
					}
					if( atts.getValue( "count" ) != null ) {
						this.currentFunction.getAccess().setCount( Integer.parseInt( atts.getValue( "count" ) ) );
					}
					if( atts.getValue( "transport" ) != null ) {
						this.currentFunction.getAccess().setTransport( TransportTypes.stringToType( atts.getValue( "transport" ) ) );
					}
					if( atts.getValue( "timeout" ) != null ) {
						this.currentFunction.getAccess().setTimeout( Double.parseDouble( atts.getValue( "timeout" ) ) );
					}
					if( atts.getValue( "monitor" ) != null ) {
						this.currentFunction.getAccess().setMonitor( Boolean.parseBoolean( atts.getValue( "monitor" ) ) );
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
	public void endElement(final String uri, 
							final String localName, 
							final String qName) throws SAXException {

		switch(this.state) {
		case VERSION_NEXT:
			this.measuringStation.setVersion( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.VERSION_READ;
			break;
		
		case MEASURINGSTATION_NEXT:
			this.measuringStation.setName(textBuffer.toString());
			this.state = MeasuringStationLoaderStates.MEASURINGSTATION_READ;
			break;
			
		case PLUGIN_NAME_NEXT:
			this.currentPlugin.setName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.PLUGIN_NAME_READ;
			break;
			
		case PLUGIN_LOCATION_NEXT:
			this.currentPlugin.setLocation( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.PLUGIN_LOCATION_READ;
			break;
			
		case PLUGIN_PARAMETER_NEXT:
			this.currentPluginParameter.setValues( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.PLUGIN_PARAMETER_READ;
			break;
			
		case DETECTOR_CLASSNAME_NEXT:
			this.currentDetector.setClassName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.DETECTOR_CLASSNAME_READ;
			break;
			
		case DETECTOR_ID_NEXT:
			this.currentDetector.setId( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.DETECTOR_ID_READ;
			break;
			
		case DETECTOR_NAME_NEXT:
			this.currentDetector.setName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.DETECTOR_NAME_READ;
			break;	
			
		case DETECTOR_CHANNEL_CLASSNAME_NEXT:
			this.currentDetectorChannel.setClassName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_CLASSNAME_READ;
			break;
			
		case DETECTOR_CHANNEL_ID_NEXT:
			this.currentDetectorChannel.setId( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_ID_READ;
			break;
			
		case DETECTOR_CHANNEL_NAME_NEXT:
			this.currentDetectorChannel.setName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
			break;
			
		case MOTOR_NAME_NEXT:
			this.currentMotor.setName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
			break;
		case MOTOR_ID_NEXT:
			this.currentMotor.setId( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.MOTOR_ID_READ;
			break;
			
		case MOTOR_CLASSNAME_NEXT:
			this.currentMotor.setClassName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.MOTOR_CLASSNAME_READ;
			break;
			
		case MOTOR_AXIS_CLASSNAME_NEXT:
		case MOTOR_AXIS_CLASSNAME_READ:
			this.currentMotorAxis.setClassName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			break;	
			
		case MOTOR_AXIS_NAME_NEXT:
			this.currentMotorAxis.setName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
			break;
			
		case MOTOR_AXIS_ID_NEXT:
			this.currentMotorAxis.setId( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.MOTOR_AXIS_ID_READ;
			break;	
			
		case DEVICE_NAME_NEXT:
			this.currentDevice.setName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
			break;
			
		case DEVICE_ID_NEXT:
			this.currentDevice.setId( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.DEVICE_ID_READ;
			break;
			
		case DEVICE_CLASSNAME_NEXT:
			this.currentDevice.setClassName( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.DEVICE_CLASSNAME_READ;
			break;
					
		case DEVICE_DISPLAYGROUP_NEXT:
			this.currentDevice.setDisplaygroup( textBuffer.toString() );
			this.state = MeasuringStationLoaderStates.DEVICE_DISPLAYGROUP_READ;
			break;
			
		case SELECTIONS_STEPFUNCTION_NEXT:
			this.measuringStation.getSelections().setStepfunctions( textBuffer.toString().split("," ) );
			this.state = MeasuringStationLoaderStates.SELECTIONS_STEPFUNCTION_READ;
			break;
		
		case SELECTIONS_SMTYPE_NEXT:
			this.measuringStation.getSelections().setSmtypes( textBuffer.toString().split("," ) );
			this.state = MeasuringStationLoaderStates.SELECTIONS_SMTYPE_READ;
			break;
			
	}
	
	switch( this.subState ) {
	
		case OPTION_NAME_NEXT:
			this.currentOption.setName( textBuffer.toString() );
			this.subState = MeasuringStationLoaderSubStates.OPTION_NAME_READ;
			break;
		case OPTION_ID_NEXT:
			this.currentOption.setId( textBuffer.toString() );
			this.subState = MeasuringStationLoaderSubStates.OPTION_ID_READ;
			break;
			
		case OPTION_DISPLAYGROUP_NEXT:
			this.currentOption.setDisplaygroup( textBuffer.toString() );
			this.subState = MeasuringStationLoaderSubStates.OPTION_DISPLAYGROUP_READ;
			break;
			
		case UNIT_VALUE_NEXT:
			this.currentUnit.setValue( textBuffer.toString() );
			this.subState = MeasuringStationLoaderSubStates.UNIT_VALUE_READ;
			break;
		case UNIT_ACCESS_NEXT:
			this.currentUnit.getAccess().setVariableID( textBuffer.toString() );
			this.subState = MeasuringStationLoaderSubStates.UNIT_ACCESS_READ;
			break;
			
		case FUNCTION_VALUE_NEXT:
			this.currentFunction.getValue().setValues( textBuffer.toString() );
			this.subState = MeasuringStationLoaderSubStates.FUNCTION_VALUE_READ;
			break;
		case FUNCTION_ACCESS_NEXT:
			this.currentFunction.getAccess().setVariableID( textBuffer.toString() );
			this.subState = MeasuringStationLoaderSubStates.FUNCTION_ACCESS_READ;
			break;
			
		case OPTION_VALUE_VALUE_NEXT:
			this.currentFunction.getValue().setValues( textBuffer.toString() );
			this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_VALUE_READ;
			break;
			
		case OPTION_VALUE_ACCESS_NEXT:
			this.currentFunction.getAccess().setVariableID( textBuffer.toString() );
			this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_ACCESS_READ;
			break;
			
	}

		
	textBuffer = null;

	/* ******************************************************************/
	/* ******************************************************************/
	
//	System.err.println( qName + " close: " + this.state + " / " + this.subState + "\n");
		
		switch( this.state ) {
			case VERSION_READ:
				if( qName.equals( "version" ) ) {
					this.state = MeasuringStationLoaderStates.ROOT;
				}
				break;
				
			case MEASURINGSTATION_READ:
				if(qName.equals("measuringstation")) {
					this.state = MeasuringStationLoaderStates.ROOT;
				}
				break;
				
			case PLUGINS_LOADING:
				if( qName.equals( "plugins" ) ) {
					this.state =  MeasuringStationLoaderStates.ROOT;
				}
				break;
				
			case PLUGIN_LOADING:
				if( qName.equals( "plugin" ) ) {
					this.measuringStation.add( this.currentPlugin );
					this.state =  MeasuringStationLoaderStates.PLUGINS_LOADING;
				}
				break;
				
			case PLUGIN_NAME_READ:
				if( qName.equals( "name" ) ) {
					this.state =  MeasuringStationLoaderStates.PLUGIN_LOADING;
				}
				break;
				
			case PLUGIN_LOCATION_READ:
				if( qName.equals( "location" ) ) {
					this.state =  MeasuringStationLoaderStates.PLUGIN_LOADING;
				}
				break;
				
			case PLUGIN_PARAMETER_NEXT:
			case PLUGIN_PARAMETER_READ:
				if( qName.equals( "parameter" ) ) {
					this.currentPlugin.add( this.currentPluginParameter );
					this.state =  MeasuringStationLoaderStates.PLUGIN_LOADING;
				}
				
				
			case DETECTORS_LOADING:
				if( qName.equals( "detectors" ) ) {
					this.state =  MeasuringStationLoaderStates.ROOT;
				}
				break;
				
			case DETECTOR_LOADING:
				if( qName.equals( "detector" ) ) {
					this.measuringStation.add( this.currentDetector );
					this.state =  MeasuringStationLoaderStates.DETECTORS_LOADING;
				}
				break;
				
			case DETECTOR_CLASSNAME_NEXT:
			case DETECTOR_CLASSNAME_READ:
				if( qName.equals( "class" ) ) {
					this.state =  MeasuringStationLoaderStates.DETECTOR_LOADING;
				}
				break;
			case DETECTOR_ID_READ:
				if( qName.equals( "id" ) ) {
					this.state =  MeasuringStationLoaderStates.DETECTOR_LOADING;
				}
				break;
			case DETECTOR_NAME_NEXT:
			case DETECTOR_NAME_READ:
				if( qName.equals( "name" ) ) {
					this.state =  MeasuringStationLoaderStates.DETECTOR_LOADING;
				}
				break;
				
				
			case DETECTOR_OPTION:
				if( qName.equals( "option" ) ) {
					this.currentDetector.add( this.currentOption );
					this.measuringStation.registerOption( this.currentOption );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.DETECTOR_LOADING;
				}
				break;
				
			case DETECTOR_UNIT:
				if( qName.equals( "unit" ) ) {
					this.currentDetector.setUnit( this.currentUnit );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.DETECTOR_LOADING;
				}
				break;
				
			case DETECTOR_TRIGGER_LOADING:
				if( qName.equals( "trigger" ) ) {
					this.currentDetector.setTrigger( this.currentFunction );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.DETECTOR_LOADING;
				}
				break;
				
			case DETECTOR_CHANNEL_LOADING:
				if( qName.equals( "channel" ) ) {
					this.currentDetector.add( this.currentDetectorChannel );
					this.measuringStation.registerDetectorChannel( this.currentDetectorChannel );
					this.state =  MeasuringStationLoaderStates.DETECTOR_LOADING;
				}
				break;
			
			case DETECTOR_CHANNEL_CLASSNAME_NEXT:
			case DETECTOR_CHANNEL_CLASSNAME_READ:
				if( qName.equals( "class" ) ) {
					this.state =  MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
				}
				break;
				
			case DETECTOR_CHANNEL_ID_READ:
				if( qName.equals( "id" ) ) {
					this.state =  MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
				}
				break;
			case DETECTOR_CHANNEL_READ_LOADING:
				if( qName.equals( "read" ) ) {
					this.currentDetectorChannel.setRead( this.currentFunction );
					this.state =  MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
					this.subState = MeasuringStationLoaderSubStates.NONE;
				}
				break;
				
			case DETECTOR_CHANNEL_OPTION:
				if( qName.equals( "option" ) ) {
					this.currentDetectorChannel.add( this.currentOption );
					this.measuringStation.registerOption( this.currentOption );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
				}
				break;
			case DETECTOR_CHANNEL_UNIT:
				if( qName.equals( "unit" ) ) {
					this.currentDetectorChannel.setUnit( this.currentUnit );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
				}
				break;
			case DETECTOR_CHANNEL_TRIGGER_LOADING:
				if( qName.equals( "trigger" ) ) {
					this.currentDetectorChannel.setTrigger( this.currentFunction );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.DETECTOR_CHANNEL_LOADING;
				}
				break;
				
			case MOTORS_LOADING:
				if( qName.equals( "motors" ) ) {
					this.state = MeasuringStationLoaderStates.ROOT;
				}
				break;
				
			case MOTOR_LOADING:
				if( qName.equals( "motor" ) ) {
					this.measuringStation.add( this.currentMotor );
					this.state = MeasuringStationLoaderStates.MOTORS_LOADING;
				}
				break;
			case MOTOR_ID_READ:
				if( qName.equals( "id" ) ) {
					this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
				}
				break;
			case MOTOR_CLASSNAME_NEXT:
			case MOTOR_CLASSNAME_READ:
				if( qName.equals( "class" ) ) {
					this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
				}
				break;
				
			case MOTOR_UNIT:
				if( qName.equals( "unit" ) ) {
					this.currentMotor.setUnit( this.currentUnit );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
				}
				break;
			case MOTOR_OPTION:
				if( qName.equals( "option" ) ) {
					this.currentMotor.add( this.currentOption );
					this.measuringStation.registerOption( this.currentOption );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
				}
				break;
			case MOTOR_TRIGGER_LOADING:
				if( qName.equals( "trigger" ) ) {
					this.currentMotor.setTrigger( this.currentFunction );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_LOADING;
				}
				break;
				
			case MOTOR_AXIS_LOADING:
				if( qName.equals( "axis" ) ) {
					this.currentMotor.add( this.currentMotorAxis );
					this.measuringStation.registerMotorAxis( this.currentMotorAxis );
					this.state =  MeasuringStationLoaderStates.MOTOR_LOADING;
				}
				break;
				
			case MOTOR_AXIS_ID_READ:
				if( qName.equals( "id" ) ) {
					this.state =  MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_STOP_LOADING:
				if( qName.equals( "stop" ) ) {
					this.currentMotorAxis.setStop( this.currentFunction );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_TRIGGER_LOADING:
				if( qName.equals( "trigger" ) ) {
					this.currentMotorAxis.setTrigger( this.currentFunction );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_OPTION:
				if( qName.equals( "option" ) ) {
					this.currentMotorAxis.add( this.currentOption );
					this.measuringStation.registerOption( this.currentOption );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_UNIT:
				if( qName.equals( "unit" ) ) {
					this.currentMotorAxis.setUnit( this.currentUnit );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_STATUS_LOADING:
				if( qName.equals( "status" ) ) {
					this.currentMotorAxis.setStatus( this.currentFunction );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_POSITION_LOADING:
				if( qName.equals( "position" ) ) {
					this.currentMotorAxis.setPosition( this.currentFunction );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_DEADBAND_LOADING:
				if( qName.equals( "deadband" ) ) {
					this.currentMotorAxis.setDeadband( this.currentFunction );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_OFFSET_LOADING:
				if( qName.equals( "offset" ) ) {
					this.currentMotorAxis.setOffset(this.currentFunction);
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_SETMODE_LOADING:
				if(qName.equals("setmode")) {
					this.currentMotorAxis.setSet(this.currentFunction);
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				
			case MOTOR_AXIS_TWEAKVALUE_LOADING:
				if( qName.equals( "tweakvalue" ) ) {
					this.currentMotorAxis.setTweakValue(this.currentFunction);
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_TWEAKFORWARD_LOADING:
				if( qName.equals( "tweakforward" ) ) {
					this.currentMotorAxis.setTweakForward(this.currentFunction);
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_TWEAKREVERSE_LOADING:
				if( qName.equals( "tweakreverse" ) ) {
					this.currentMotorAxis.setTweakReverse(this.currentFunction);
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
				}
				break;
				
			case MOTOR_AXIS_GOTO_LOADING:
				if( qName.equals( "goto" ) ) {
					this.currentMotorAxis.setGoto( this.currentFunction );
					this.state = MeasuringStationLoaderStates.MOTOR_AXIS_LOADING;
					this.subState = MeasuringStationLoaderSubStates.NONE;
				}
				break;
				
			case DEVICES_LOADING:
				if( qName.equals( "devices" ) ) {
					this.state = MeasuringStationLoaderStates.ROOT;
				}
				break;
				
			case DEVICE_LOADING:
				if( qName.equals( "device" ) ) {
					this.measuringStation.add( this.currentDevice );
					this.state = MeasuringStationLoaderStates.DEVICES_LOADING;
				}
				break;
				
			case DEVICE_ID_READ:
				if( qName.equals( "id" ) ) {
					this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
				}
				break;
				
			case DEVICE_CLASSNAME_NEXT:
			case DEVICE_CLASSNAME_READ:
				if( qName.equals( "class" ) ) {
					this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
				}
				break;
				
			case DEVICE_VALUE_LOADING:
				if( qName.equals( "value" ) ) {
					this.currentDevice.setValue( this.currentFunction );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
				}
				break;
				
			case DEVICE_DISPLAYGROUP_READ:
				if( qName.equals( "displaygroup" ) ) {
					this.state = MeasuringStationLoaderStates.DEVICE_LOADING;
				}
				break;
				
			case DEVICE_UNIT:
				if( qName.equals( "unit" ) ) {
					this.currentDevice.setUnit( this.currentUnit );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.DEVICE_LOADING;
				}
				break;
				
			case DEVICE_OPTION:
				if( qName.equals( "option" ) ) {
					this.currentDevice.add( this.currentOption );
					this.measuringStation.registerOption( this.currentOption );
					this.subState = MeasuringStationLoaderSubStates.NONE;
					this.state =  MeasuringStationLoaderStates.DEVICE_LOADING;
				}
				break;
				
			case SELECTIONS_LOADING:
				if( qName.equals( "smselection" ) ) {
					this.state =  MeasuringStationLoaderStates.ROOT;
				}
				break;
				
			case SELECTIONS_STEPFUNCTION_READ:
				if( qName.equals( "stepfunction" ) ) {
					this.state =  MeasuringStationLoaderStates.SELECTIONS_LOADING;
				}
				break;
				
			case SELECTIONS_SMTYPE_READ:
				if( qName.equals( "smtype" ) ) {
					this.state =  MeasuringStationLoaderStates.SELECTIONS_LOADING;
				}
				break;
					
		}
		
		switch( this.subState ) {
			case OPTION_NAME_NEXT:
			case OPTION_NAME_READ:
				if( qName.equals( "name" ) ) {
					this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
				}
				break;
			case OPTION_ID_READ:
				if( qName.equals( "id" ) ) {
					this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
				}
				break;
				
			case OPTION_VALUE_LOADING:
				if( qName.equals( "value" ) ) {
					this.currentOption.setValue( this.currentFunction );
					this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
				}
				break;
			case OPTION_DISPLAYGROUP_READ:
				if( qName.equals( "displaygroup" ) ) {
					this.subState = MeasuringStationLoaderSubStates.OPTION_LOADING;
				}
				break;
				
			case UNIT_VALUE_READ:
				if( qName.equals( "unitstring" ) ) {
					this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
				}
				break;
			case UNIT_ACCESS_READ:
				if( qName.equals( "access" ) ) {
					this.subState = MeasuringStationLoaderSubStates.UNIT_LOADING;
				}
				break;
				
			case FUNCTION_VALUE_READ:
				if( qName.equals( "value" ) ) {
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				}
				break;
			case FUNCTION_ACCESS_READ:
				if( qName.equals( "access" ) ) {
					this.subState = MeasuringStationLoaderSubStates.FUNCTION_LOADING;
				}
				break;
				
			case OPTION_VALUE_VALUE_READ:
				if( qName.equals( "value" ) ) {
					this.subState = MeasuringStationLoaderSubStates.OPTION_VALUE_LOADING;
				}
				break;
			case OPTION_VALUE_ACCESS_READ:
				if( qName.equals( "access" ) ) {
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
		// position and status access if monitor equals true
		// Create events for options if monitor equals true.
		for (Motor loopMotor : this.measuringStation.getMotors()) {
			for (MotorAxis loopAxis : loopMotor.getAxes()){
				String id = loopAxis.getID();
				if (loopAxis.getPosition().getAccess().getMonitor()){
					String name = loopAxis.getName() + ".Position";
					this.measuringStation.add(new Event(loopAxis.getPosition().getAccess(), loopAxis.getPosition().getValue(), name, id));
				}
				if ((loopAxis.getStatus() != null) && (loopAxis.getStatus().getAccess().getMonitor())){
					String name = loopAxis.getName() + ".Status";
					this.measuringStation.add(new Event(loopAxis.getStatus().getAccess(), loopAxis.getStatus().getValue(), name, id));
				}
				for (Option loopOption : loopAxis.getOptions()){
					if (loopOption.getValue().getAccess().getMonitor()){
						String name =  loopAxis.getName() + "." + loopOption.getName();
						id = loopOption.getID();
						this.measuringStation.add(new Event(loopOption.getValue().getAccess(), loopOption.getValue().getValue(), name, id));
					}				
				}
			}
			for (Option loopOption : loopMotor.getOptions()){
				if (loopOption.getValue().getAccess().getMonitor()){
					String name =  loopMotor.getName() + "." + loopOption.getName();
					String id = loopOption.getID();
					this.measuringStation.add(new Event(loopOption.getValue().getAccess(), loopOption.getValue().getValue(), name, id));
				}				
			}
		}

		// loop through detector channels and create events for every 
		// read access with monitor equals true
		// Create events for options if monitor equals true.
		for (Detector loopDetector : this.measuringStation.getDetectors()){
			for (DetectorChannel loopChannel : loopDetector.getChannels()){
				String name = loopChannel.getName();
				String id = loopChannel.getID();
				if (loopChannel.getRead().getAccess().getMonitor()){
					this.measuringStation.add(new Event(loopChannel.getRead().getAccess(), loopChannel.getRead().getValue(), name, id));
				}
				for (Option loopOption : loopChannel.getOptions()){
					if (loopOption.getValue().getAccess().getMonitor()){
						name =  loopChannel.getName() + "." + loopOption.getName();
						id = loopOption.getID();
						this.measuringStation.add(new Event(loopOption.getValue().getAccess(), loopOption.getValue().getValue(), name, id));
					}				
				}
			}
			for (Option loopOption : loopDetector.getOptions()){
				if (loopOption.getValue().getAccess().getMonitor()){
					String name =  loopDetector.getName() + "." + loopOption.getName();
					String id = loopOption.getID();
					this.measuringStation.add(new Event(loopOption.getValue().getAccess(), loopOption.getValue().getValue(), name, id));
				}				
			}
		}

		// loop through devices and create events for every 
		// value access with monitor equals true
		for (Device loopDevice : this.measuringStation.getDevices()){
			String name = loopDevice.getName();
			String id = loopDevice.getID();
			if (loopDevice.getValue().getAccess().getMonitor()){
				this.measuringStation.add(new Event(loopDevice.getValue().getAccess(), loopDevice.getValue().getValue(), name, id));
			}
		}
		
/*		// debug only
		System.err.println( "***** Motors *****" );
		Iterator< Motor > it = this.measuringStation.getMotors().iterator();
		while( it.hasNext() ) {
			Motor motor = it.next();
			Iterator< MotorAxis > it2 = motor.getAxis().iterator();
			while( it2.hasNext() ) {
				MotorAxis motorAxis = it2.next();
				System.err.println( "axis: " + motorAxis.getID() );
				Iterator<Option> optionIterator = motorAxis.getOptions().iterator();
				while( optionIterator.hasNext() ) {
					Option option = optionIterator.next();
					System.err.println("axis option: " + option.getID());
				}			
			}
			Iterator<Option> optionIterator = motor.getOptions().iterator();
			while( optionIterator.hasNext() ) {
				Option option = optionIterator.next();
				System.err.println("motor option: " + option.getID());
			}			
		}

		// debug only
		System.err.println( "***** Detectors *****" );
		Iterator<Detector> detectors = this.measuringStation.getDetectors().iterator();
		while( detectors.hasNext() ) {
			Detector detector = detectors.next();
			System.err.println( detector.getID() );
			Iterator< DetectorChannel > it2 = detector.getChannels().iterator();
			while( it2.hasNext() ) {
				DetectorChannel channel = it2.next();
				System.err.println( "channel: " + channel.getID() );
				Iterator<Option> optionIterator = channel.getOptions().iterator();
				while( optionIterator.hasNext() ) {
					Option option = optionIterator.next();
					System.err.println("channel option: " + option.getID());
				}			
			}
			Iterator<Option> optionIterator = detector.getOptions().iterator();
			while( optionIterator.hasNext() ) {
				Option option = optionIterator.next();
				System.err.println("detector option: " + option.getID());
			}			
		}
*/		

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