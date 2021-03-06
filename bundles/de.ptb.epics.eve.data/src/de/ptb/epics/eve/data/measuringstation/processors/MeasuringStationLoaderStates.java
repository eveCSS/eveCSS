package de.ptb.epics.eve.data.measuringstation.processors;

/**
 * Possible states of the SAX handler to load a device definition file.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (- at -) ptb.de>
 * @author Marcus Michalsky
 */
public enum MeasuringStationLoaderStates {
	
	/**
	 * The initial and end state of the handler.
	 */
	ROOT,
	
	/**
	 * 
	 */
	LOCATION_NEXT,

	/**
	 * 
	 */
	LOCATION_READ,
	
	/**
	 * The begin tag of version has been read.
	 */
	VERSION_NEXT,
	
	/**
	 * The version string has been read and the handler is waiting for the 
	 * close version tag.
	 */
	VERSION_READ,
	
	/**
	 * The begin tag of the plug ins section has been read.
	 */
	PLUGINS_LOADING,

	/**
	 * The begin of a plug in has been read.
	 */
	PLUGIN_LOADING,
	
	/**
	 * The begin tag of the plug in name has been read.
	 */
	PLUGIN_NAME_NEXT,
	
	/**
	 * The name of the plug in has been read and the handler is waiting for the 
	 * close tag.
	 */
	PLUGIN_NAME_READ,
	
	/**
	 * The begin tag of the plug in location has been read.
	 */
	PLUGIN_LOCATION_NEXT,
	
	/**
	 * The location of the plug in has been read and the handler is waiting for 
	 * the close tag.
	 */
	PLUGIN_LOCATION_READ,
	
	/**
	 * The begin tag of a plug in parameter has been read.
	 */
	PLUGIN_PARAMETER_NEXT,
	
	/**
	 * A parameter of the plugin has been read and the handler is waiting for 
	 * the close tag.
	 */
	PLUGIN_PARAMETER_READ,
	
	/**
	 * The begin tag of the detectors section has been read.
	 */
	DETECTORS_LOADING,
	
	/**
	 * The begin of a detector has been read.
	 */
	DETECTOR_LOADING,
	
	/**
	 * The begin tag of the detector class name has been read.
	 */
	DETECTOR_CLASSNAME_NEXT,
	
	/**
	 * The class name of the detector has been read and the handler is waiting 
	 * for the close tag.
	 */
	DETECTOR_CLASSNAME_READ,
	
	/**
	 * The begin tag of the detector id has been read.
	 */
	DETECTOR_ID_NEXT,
	
	/**
	 * The id of the detector has been read and the handler is waiting for 
	 * the close tag.
	 */
	DETECTOR_ID_READ,
	
	/**
	 * The begin tag of the detector name has been read.
	 */
	DETECTOR_NAME_NEXT,
	
	/**
	 * The name of the detector has been read and the handler is waiting for 
	 * the close tag.
	 */
	DETECTOR_NAME_READ,
	
	/**
	 * The option of a detector is loading. Processing is done in sub states.
	 */
	DETECTOR_OPTION,
	
	/**
	 * The unit of a detector is loading. Processing is done in sub states.
	 */
	DETECTOR_UNIT,
	
	/**
	 * The trigger of a detector is loading. Processing is done in sub states.
	 */
	DETECTOR_TRIGGER_LOADING,
	
	/**
	 * stop of detector loading (processing is done in sub states)
	 */
	DETECTOR_STOP_LOADING,
	
	/**
	 * status of detector loading (processing is done in sub states)
	 */
	DETECTOR_STATUS_LOADING,
	
	/**
	 * A channel of a detector channel is loading.
	 */
	DETECTOR_CHANNEL_LOADING,
	
	/**
	 * The begin tag of the detector channel class name has been read.
	 */
	DETECTOR_CHANNEL_CLASSNAME_NEXT,
	
	/**
	 * The begin tag of the detector channel class name has been read.
	 */
	DETECTOR_CHANNEL_CLASSNAME_READ,
	
	/**
	 * The begin tag of the detector channel name has been read.
	 */
	DETECTOR_CHANNEL_NAME_NEXT,
	
	/**
	 * The name of the detector channel has been read and the handler is 
	 * waiting for the close tag.
	 */
	DETECTOR_CHANNEL_NAME_READ,
	
	/**
	 * The begin tag of the detector channel id has been read.
	 */
	DETECTOR_CHANNEL_ID_NEXT,
	
	/**
	 * The name of the detector channel has been read and the handler is 
	 * waiting for the close tag.
	 */
	DETECTOR_CHANNEL_ID_READ,
	
	/**
	 * The read function of a detector is loading. Processing is done in 
	 * sub states.
	 */
	DETECTOR_CHANNEL_READ_LOADING,
	
	/**
	 * The option of a detector channel is loading. Processing is done in 
	 * sub states.
	 */
	DETECTOR_CHANNEL_OPTION,
	
	/**
	 * The unit of a detector channel is loading. Processing is done in sub 
	 * states.
	 */
	DETECTOR_CHANNEL_UNIT,
	
	/**
	 * The trigger of a detector channel is loading. Processing is done in sub 
	 * states.
	 */
	DETECTOR_CHANNEL_TRIGGER_LOADING,

	/**
	 * 
	 */
	DETECTOR_CHANNEL_STOP_LOADING,
	
	/**
	 * 
	 */
	DETECTOR_CHANNEL_STATUS_LOADING,
	
	/**
	 * The begin tag of the motors section has been read.
	 */
	MOTORS_LOADING,
	
	/**
	 * The begin of a motor has been read.
	 */
	MOTOR_LOADING,

	/**
	 * The begin tag of the motor class name has been read.
	 */
	MOTOR_CLASSNAME_NEXT,
	
	/**
	 * The class name of the motor has been read and the handler is waiting for 
	 * the close tag.
	 */
	MOTOR_CLASSNAME_READ,
	
	/**
	 * The begin tag of the motor id has been read.
	 */
	MOTOR_ID_NEXT,
	
	/**
	 * The id of the motor has been read and the handler is waiting for the 
	 * close tag.
	 */
	MOTOR_ID_READ,
	
	/**
	 * The begin tag of the motor name has been read.
	 */
	MOTOR_NAME_NEXT,
	
	/**
	 * The name of the motor has been read and the handler is waiting for the 
	 * close tag.
	 */
	MOTOR_NAME_READ,
	
	/**
	 * The option of a motor is loading. Processing is done in sub states.
	 */
	MOTOR_OPTION,
	
	/**
	 * The unit of a motor is loading. Processing is done in sub states.
	 */
	MOTOR_UNIT,
	
	/**
	 * The trigger of a motor is loading. Processing is done in sub states.
	 */
	MOTOR_TRIGGER_LOADING,
	
	/**
	 * The begin of a motor axis has been read.
	 */
	MOTOR_AXIS_LOADING,
	
	/**
	 * The begin tag of the motor axis class name has been read.
	 */
	MOTOR_AXIS_CLASSNAME_NEXT,
	
	/**
	 * The begin tag of the motor axis class name has been read.
	 */
	MOTOR_AXIS_CLASSNAME_READ,
	
	/**
	 * The begin tag of the motor axis name has been read.
	 */
	MOTOR_AXIS_NAME_NEXT,
	
	/**
	 * The name of the motor axis has been read and the handler is waiting for 
	 * the close tag.
	 */
	MOTOR_AXIS_NAME_READ,
	
	/**
	 * The begin tag of the motor axis id has been read.
	 */
	MOTOR_AXIS_ID_NEXT,
	
	/**
	 * The id of the motor axis has been read and the handler is waiting for 
	 * the close tag.
	 */
	MOTOR_AXIS_ID_READ,
	
	/**
	 * The status of a motor axis is loading. Processing is done in sub states.
	 */
	MOTOR_AXIS_STATUS_LOADING,
	
	/**
	 * The moveDone flag of the motor axis is loading. Processing is done in 
	 * sub states.
	 */
	MOTOR_AXIS_MOVEDONE_LOADING,
	
	/**
	 * The position of a motor axis is loading. Processing is done in sub 
	 * states.
	 */
	MOTOR_AXIS_POSITION_LOADING,
	
	/**
	 * The option of a motor axis is loading. Processing is done in sub states.
	 */
	MOTOR_AXIS_OPTION,
	
	/**
	 * The unit of a motor axis is loading. Processing is done in sub states.
	 */
	MOTOR_AXIS_UNIT,
	
	/**
	 * The trigger of a motor axis is loading. Processing is done in sub states.
	 */
	MOTOR_AXIS_TRIGGER_LOADING,
	
	/**
	 * The goto of a motor axis is loading. Processing is done in sub states.
	 */
	MOTOR_AXIS_GOTO_LOADING,
	
	/**
	 * The stop of a motor axis is loading. Processing is done in sub states.
	 */
	MOTOR_AXIS_STOP_LOADING,
	
	/**
	 *  soft high limit is loading
	 */
	MOTOR_AXIS_HIGHLIMIT_LOADING,
	
	/**
	 * soft low limit is loading
	 */
	MOTOR_AXIS_LOWLIMIT_LOADING,
	
	/**
	 * limit violation loading
	 */
	MOTOR_AXIS_LIMITVIOLATION_LOADING,
	
	/**
	 * The dead band of a motor axis is loading. Processing is done in sub 
	 * states.
	 */
	MOTOR_AXIS_DEADBAND_LOADING,
	
	/**
	 * The offset of a motor axis is loading. Processing is done in sub states.
	 */
	MOTOR_AXIS_OFFSET_LOADING,
	
	/**
	 * The tweak value of a motor axis is loading. Processing is done in sub 
	 * states.
	 */
	MOTOR_AXIS_TWEAKVALUE_LOADING,
	
	/**
	 * The tweak forward of a motor axis is loading. Processing is done in sub 
	 * states.
	 */
	MOTOR_AXIS_TWEAKFORWARD_LOADING,
	
	/**
	 * The tweak reverser of a motor axis is loading. Processing is done in sub 
	 * states.
	 */
	MOTOR_AXIS_TWEAKREVERSE_LOADING,
	
	/**
	 * The begin tag of the devices section has been read.
	 */
	DEVICES_LOADING,
	
	/**
	 * The begin tag of a device has been read.
	 */
	DEVICE_LOADING,
	
	/**
	 * The begin tag of the device class name has been read.
	 */
	DEVICE_CLASSNAME_NEXT,
	
	/**
	 * The class name of the device has been read and the handler is waiting for 
	 * the close tag.
	 */
	DEVICE_CLASSNAME_READ,
	
	/**
	 * The begin tag of the device name has been read.
	 */
	DEVICE_NAME_NEXT,
	
	/**
	 * The name of the device has been read and the handler is waiting for the 
	 * close tag.
	 */
	DEVICE_NAME_READ,
	
	/**
	 * The begin tag of the device id has been read.
	 */
	DEVICE_ID_NEXT,
	
	/**
	 * The id of the device has been read and the handler is waiting for the 
	 * close tag.
	 */
	DEVICE_ID_READ,
	
	/**
	 * The begin tag of the display group id has been read.
	 */
	DEVICE_DISPLAYGROUP_NEXT,
	
	/**
	 * The display group of the device has been read and the handler is waiting 
	 * for the close tag.
	 */
	DEVICE_DISPLAYGROUP_READ,
	
	/**
	 * The value of a device is loading. Processing is done in sub states.
	 */
	DEVICE_VALUE_LOADING,
	
	/**
	 * The unit of a device is loading. Processing is done in sub states.
	 */
	DEVICE_UNIT,
	
	/**
	 * The option of a device is loading. Processing is done in sub states.
	 */
	DEVICE_OPTION,
	
	/**
	 * The begin tag of the events section has been read.
	 */
	EVENTS_LOADING,
	
	/**
	 * The begin tag of an event has been read.
	 */
	EVENT_LOADING,
	
	/**
	 * The begin tag of the event id has been read.
	 */
	EVENT_ID_NEXT,
	
	/**
	 * The id of the event has been read and the handler is waiting for the 
	 * close tag.
	 */
	EVENT_ID_READ,
	
	/**
	 * The begin tag of the event name has been read.
	 */
	EVENT_NAME_NEXT,
	
	/**
	 * The name of the event has been read and the handler is waiting for the 
	 * close tag.
	 */
	EVENT_NAME_READ,
	
	/**
	 * The value of an event is loading. Processing is done in sub states.
	 */
	EVENT_VALUE_LOADING
}