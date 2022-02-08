package de.ptb.epics.eve.data.scandescription.processors;

/**
 * @author Marcus Michalsky
 * @since 1.5
 */
public enum ScanDescriptionLoaderMessageType {
	/** */
	MOTOR_AXIS_ID_NOT_FOUND, 
	
	/** */
	DETECTOR_CHANNEL_ID_NOT_FOUND, 
	
	/**
	 * @since 1.15
	 */
	PRE_POST_SCAN_DEVICE_ID_NOT_FOUND,
	
	/**
	 * Indicates that the device referenced by the id of the monitor option
	 * could not be found in the current device definition.
	 * 
	 * @since 1.15
	 */
	MONITOR_OPTION_ID_NOT_FOUND,

	/**
	 * Indicates that a monitor option in the current device definition has 
	 * been added to the monitor list of the scan. At the time the scan was 
	 * created the added option was not part of the then available device 
	 * definition.
	 * 
	 * @since 1.29
	 */
	MONITOR_OPTION_ADDED,
	
	/**
	 * Indicates that a monitor option was removed from the list of the scan.
	 * This happens if a monitor that was present in the device definition at 
	 * the time the scan was saved is not available in the current device
	 * definition.
	 * 
	 * @since 1.29
	 */
	MONITOR_OPTION_REMOVED,
	
	/** */
	PLUGIN_NOT_FOUND, 
	
	/** */
	PLUGIN_LOCATION_CHANGED, 
	
	/** 
	 *
	 * @since 1.19
	 */
	DETECTOR_OF_DETECTOREVENT_NOT_FOUND,
	
	/** 
	 * 
	 * @since 1.19
	 */
	SCANMODULE_OF_SCHEDULEEVENT_NOT_FOUND,
	
	/**
	 * @since 1.36.1
	 */
	DEVICE_OF_MONITOREVENT_NOT_FOUND,
	
	/**
	 * @since 1.36
	 */
	PAUSECONDITION_DEVICE_NOT_FOUND
}