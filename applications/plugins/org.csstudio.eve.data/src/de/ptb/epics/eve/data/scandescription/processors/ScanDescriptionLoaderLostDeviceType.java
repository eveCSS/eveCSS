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
	
	/** */
	PLUGIN_NOT_FOUND, 
	
	/** */
	PLUGIN_LOCATION_CHANGED
}