package de.ptb.epics.eve.data.scandescription.processors;

/**
 * @author Marcus Michalsky
 * @since 1.5
 */
public enum ScanDescriptionLoaderLostDeviceType {
	/** */
	MOTOR_AXIS_ID_NOT_FOUND, 
	
	/** */
	DETECTOR_CHANNEL_ID_NOT_FOUND, 
	
	/**
	 * @since 1.15
	 */
	PRE_POST_SCAN_DEVICE_ID_NOT_FOUND,
	MONITOR_OPTION_ID_NOT_FOUND,
	MONITOR_OPTION_ID_FAILED_IN_LIST,

	/** */
	PLUGIN_NOT_FOUND, 
	
	/** */
	PLUGIN_LOCATION_CHANGED
}