package de.ptb.epics.eve.data.scandescription.processors;

/**
 * Possible states of the load handler for scan descriptions.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 * @author Marcus Michalsky
 */
public enum ScanDescriptionLoaderStates {

	/**
	 * The initial and end state of the load handler.
	 */
	ROOT,

	/**
	 * The begin tag of the file version has been read.
	 */
	VERSION_NEXT,

	/**
	 * The file version has been read and the handler waits for the closing tag.
	 */
	VERSION_READ,

	/**
	 * The begin tag of the repeat count has been read.
	 */
	REPEATCOUNT_NEXT,

	/**
	 * The repeat count has been read and the handler waits for the closing tag.
	 */
	REPEATCOUNT_READ,

	/**
	 * This state indicates the monitor options are loading.
	 */
	MONITOROPTIONS_LOADING,

	/**
	 * indicates that a scan is loading
	 */
	SCAN_LOADING,

	/**
	 * The begin tag of the comment has been read.
	 * @since 1.33
	 */
	SCAN_COMMENT_NEXT,

	/**
	 * the comment has been read and the handler waits for the closing tag.
	 * @since 1.33
	 */
	SCAN_COMMENT_READ,

	/**
	 * The begin tag of the save file name has been read.
	 * @since 1.33
	 */
	SCAN_SAVEFILENAME_NEXT,
	
	/**
	 * The save file name has been read and the handler waits for the closing
	 * tag.
	 * @since 1.33
	 */
	SCAN_SAVEFILENAME_READ,

	/**
	 * The begin tag of the confirm save has been read.
	 * @since 1.33
	 */
	SCAN_CONFIRMSAVE_NEXT,
	
	/**
	 * The confirm save has been read and the handler waits for the closing tag.
	 * @since 1.33
	 */
	SCAN_CONFIRMSAVE_READ,

	/**
	 * The begin tag of the auto number has been read.
	 * @since 1.33
	 */
	SCAN_AUTONUMBER_NEXT,
	
	/**
	 * The auto number has been read and the handler waits for the closing tag.
	 * @since 1.33
	 */
	SCAN_AUTONUMBER_READ,

	/**
	 * The begin tag of the save scan description has been read.
	 * @since 1.33
	 */
	SCAN_SAVESCANDESCRIPTION_NEXT,
	
	/**
	 * The save scan description has been read and the handler waits for the
	 * closing tag.
	 * @since 1.33
	 */
	SCAN_SAVESCANDESCRIPTION_READ,

	/**
	 * The plug in controller for saving is loading.
	 * @since 1.33
	 */
	SCAN_SAVEPLUGINCONTROLLER_LOADING,
	
	/**
	 * This state indicates the a chain is loading.
	 */
	CHAIN_LOADING,

	/**
	 * The begin tag of the start event has been read.
	 */
	CHAIN_STARTEVENT_LOADING,
	CHAIN_REDOEVENT_LOADING,
	CHAIN_BREAKEVENT_LOADING,
	CHAIN_STOPEVENT_LOADING,
	CHAIN_PAUSEEVENT_LOADING,

	/**
	 * The scan modules section ha begun.
	 */
	CHAIN_SCANMODULES_LOADING,

	/**
	 * A scan module is loading.
	 */
	CHAIN_SCANMODULE_LOADING,

	/**
	 * load elements specific to classic scan module
	 * @since 1.31
	 */
	CHAIN_SCANMODULE_CLASSIC_LOADING,
	
	/**
	 * load elements specific to save_axis_positions scan module
	 * @since 1.31
	 */
	CHAIN_SCANMODULE_SAVE_AXIS_POSITIONS_LOADING,
	
	/**
	 * load elements specific to save_channel_values scan module
	 * @since 1.31
	 */
	CHAIN_SCANMODULE_SAVE_CHANNEL_VALUES_LOADING,
	
	/**
	 * load elements specific to dynamic_axis_positions scan module
	 * @since 1.31
	 */
	CHAIN_SCANMODULE_DYNAMIC_AXIS_POSITIONS_LOADING,
	
	/**
	 * load elements specific to dynamic_channel_values scan module
	 * @since 1.31
	 */
	CHAIN_SCANMODULE_DYNAMIC_CHANNEL_VALUES_LOADING,
	
	/**
	 * The begin tag of the scan module name has been read.
	 */
	CHAIN_SCANMODULE_NAME_NEXT,

	/**
	 * The scan module name has been read and the handler waits for the closing
	 * tag.
	 */
	CHAIN_SCANMODULE_NAME_READ,

	/**
	 * The begin tag of the scan module xpos has been read.
	 */
	CHAIN_SCANMODULE_XPOS_NEXT,

	/**
	 * The scan module xpos has been read and the handler waits for the closing
	 * tag.
	 */
	CHAIN_SCANMODULE_XPOS_READ,

	/**
	 * The begin tag of the scan module ypos has been read.
	 */
	CHAIN_SCANMODULE_YPOS_NEXT,

	/**
	 * The scan module ypos has been read and the handler waits for the closing
	 * tag.
	 */
	CHAIN_SCANMODULE_YPOS_READ,

	/**
	 * The begin tag of the scan module parent has been read.
	 */
	CHAIN_SCANMODULE_PARENT_NEXT,

	/**
	 * The scan module parent has been read and the handler waits for the
	 * closing tag.
	 */
	CHAIN_SCANMODULE_PARENT_READ,

	/**
	 * The begin tag of the scan module nested has been read.
	 */
	CHAIN_SCANMODULE_NESTED_NEXT,

	/**
	 * The scan module nested has been read and the handler waits for the
	 * closing tag.
	 */
	CHAIN_SCANMODULE_NESTED_READ,

	/**
	 * The begin tag of the scan module appended has been read.
	 */
	CHAIN_SCANMODULE_APPENDED_NEXT,

	/**
	 * The scan module appended has been read and the handler waits for the
	 * closing tag.
	 */
	CHAIN_SCANMODULE_APPENDED_READ,

	/**
	 * The begin tag of the value count has been read.
	 */
	CHAIN_SCANMODULE_VALUECOUNT_NEXT,
	
	/**
	 * The value count has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_VALUECOUNT_READ,
	
	/**
	 * The begin tag of the scan module settle time has been read.
	 */
	CHAIN_SCANMODULE_SETTLETIME_NEXT,

	/**
	 * The scan module settle time has been read and the handler waits for the
	 * closing tag.
	 */
	CHAIN_SCANMODULE_SETTLETIME_READ,

	/**
	 * The begin tag of the scan module trigger delay has been read.
	 */
	CHAIN_SCANMODULE_TRIGGERDELAY_NEXT,

	/**
	 * The scan module trigger delay has been read and the handler waits for the
	 * closing tag.
	 */
	CHAIN_SCANMODULE_TRIGGERDELAY_READ,

	/**
	 * The begin tag of the scan module trigger confirm has been read.
	 */
	CHAIN_SCANMODULE_TRIGGERCONFIRMAXIS_NEXT,

	/**
	 * The scan module trigger confirm has been read and the handler waits for
	 * the closing tag.
	 */
	CHAIN_SCANMODULE_TRIGGERCONFIRMAXIS_READ,

	/**
	 * The begin tag of the scan module trigger confirm has been read.
	 */
	CHAIN_SCANMODULE_TRIGGERCONFIRMCHANNEL_NEXT,
	
	/**
	 * The scan module trigger confirm has been read and the handler waits for
	 * the closing tag.
	 */
	CHAIN_SCANMODULE_TRIGGERCONFIRMCHANNEL_READ,
	
	CHAIN_SCANMODULE_TRIGGEREVENT_LOADING,
	CHAIN_SCANMODULE_BREAKEVENT_LOADING,
	CHAIN_SCANMODULE_REDOEVENT_LOADING,
	CHAIN_SCANMODULE_PAUSEEVENT_LOADING,

	/**
	 * A Prescan is loading.
	 */
	CHAIN_SCANMODULE_PRESCAN_LOADING,

	/**
	 * The begin tag of the prescans id has been read.
	 */
	CHAIN_SCANMODULE_PRESCAN_ID_NEXT,

	/**
	 * The prescans id has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_PRESCAN_ID_READ,

	/**
	 * The begin tag of the prescans value has been read.
	 */
	CHAIN_SCANMODULE_PRESCAN_VALUE_NEXT,

	/**
	 * The prescans value has been read and the handler waits for the closing
	 * tag.
	 */
	CHAIN_SCANMODULE_PRESCAN_VALUE_READ,

	/**
	 * A Postscan is loading.
	 */
	CHAIN_SCANMODULE_POSTSCAN_LOADING,

	/**
	 * The begin tag of the postcans id has been read.
	 */
	CHAIN_SCANMODULE_POSTSCAN_ID_NEXT,

	/**
	 * The postscans id has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_POSTSCAN_ID_READ,

	/**
	 * The begin tag of the postscans value has been read.
	 */
	CHAIN_SCANMODULE_POSTSCAN_VALUE_NEXT,

	/**
	 * The postscans value has been read and the handler waits for the closing
	 * tag.
	 */
	CHAIN_SCANMODULE_POSTSCAN_VALUE_READ,

	/**
	 * The begin tag of the postscans reset has been read.
	 */
	CHAIN_SCANMODULE_POSTSCAN_RESET_NEXT,

	/**
	 * The postscans reset has been read and the handler waits for the closing
	 * tag.
	 */
	CHAIN_SCANMODULE_POSTSCAN_RESET_READ,

	/**
	 * The a motor inside a scan module is loading.
	 */
	CHAIN_SCANMODULE_SMMOTOR_LOADING,

	/**
	 * The begin tag of the id of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_AXISID_NEXT,

	/**
	 * The id of the moved motor axis has been read and the handler waits for
	 * the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_AXISID_READ,

	/**
	 * The begin tag of the step function of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_NEXT,

	/**
	 * The step function of the moved motor axis has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_READ,

	/**
	 * The begin tag of the position mode of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_NEXT,

	/**
	 * The position mode of the moved motor axis has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_READ,

	/**
	 * The begin tag of the start value of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_START_NEXT,

	/**
	 * The start value of the moved motor axis has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_START_READ,

	/**
	 * The begin tag of the stop value of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STOP_NEXT,

	/**
	 * The stop value of the moved motor axis has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STOP_READ,

	/**
	 * The begin tag of the step width of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_NEXT,

	/**
	 * The step width of the moved motor axis has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_READ,

	/**
	 * The main axis flag of the step width of the moved motor axis has been
	 * read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_NEXT,

	/**
	 * The main axis flag of the moved motor axis has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_READ,

	/**
	 * The begin tag of the step file name of the moved motor axis has been
	 * read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_NEXT,

	/**
	 * The step file name of the moved motor axis has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_READ,

	/**
	 * The begin tag of the position list of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_NEXT,

	/**
	 * The position list of the moved motor axis has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_READ,

	CHAIN_SCANMODULE_SMMOTOR_EXPRESSION_NEXT,
	CHAIN_SCANMODULE_SMMOTOR_EXPRESSION_READ,
	
	/**
	 * The plug in controller of the motor axis is loading.
	 */
	CHAIN_SCANMODULE_SMMOTOR_CONTROLLER_LOADING,

	/**
	 * A after scan positioning is loading.
	 */
	CHAIN_SCANMODULE_POSITIONING_LOADING,

	/**
	 * The begin tag of the axis id of the positioning has been read.
	 */
	CHAIN_SCANMODULE_POSITIONING_AXIS_ID_NEXT,

	/**
	 * The motor axis id of the positioning has been read and the handler waits
	 * for the closing tag.
	 */
	CHAIN_SCANMODULE_POSITIONING_AXIS_ID_READ,

	/**
	 * The begin tag of the channel id of the positioning has been read.
	 */
	CHAIN_SCANMODULE_POSITIONING_CHANNEL_ID_NEXT,

	/**
	 * The detector channel id of the positioning has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_POSITIONING_CHANNEL_ID_READ,

	/**
	 * The begin tag of the channel id for normalization of the positioning has
	 * been read.
	 */
	CHAIN_SCANMODULE_POSITIONING_NORMALIZE_ID_NEXT,

	/**
	 * The detector channel id for normalization of the positioning has been
	 * read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_POSITIONING_NORMALIZE_ID_READ,

	/**
	 * The plug in controller of the positioning is loading.
	 */
	CHAIN_SCANMODULE_POSITIONING_CONTROLLER_LOADING,

	/**
	 * A detector channel of a scan module is loading.
	 */
	CHAIN_SCANMODULE_DETECTOR_LOADING,

	/**
	 * The begin tag of the channel id of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_CHANNELID_NEXT,

	/**
	 * The detector channel id of the detector channel has been read and the
	 * handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_CHANNELID_READ,

	/**
	 * 
	 * @since 1.27
	 */
	CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING,
	
	/**
	 * 
	 * @since 1.27
	 */
	CHAIN_SCANMODULE_DETECTOR_INTERVAL_LOADING,
	
	/**
	 * The begin tag of the average count of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_AVERAGECOUNT_NEXT,

	/**
	 * The average count of the detector channel has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_AVERAGECOUNT_READ,

	/**
	 * The begin tag of max deviation of the average count of the detector
	 * channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_MAX_DEVIATION_NEXT,

	/**
	 * The max deviation of the detector channel has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_MAX_DEVIATION_READ,

	/**
	 * The begin tag of minimum of the average count of the detector channel has
	 * been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_MINIMUM_NEXT,

	/**
	 * The max deviation of the detector channel has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_MINIMUM_READ,

	/**
	 * The begin tag of max attempts of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_MAX_ATTEMPTS_NEXT,

	/**
	 * The max deviation of the detector channel has been read and the handler
	 * waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_MAX_ATTEMPTS_READ,

	/**
	 * 
	 */
	CHAIN_SCANMODULE_DETECTOR_NORMALIZECHANNEL_NEXT,

	/**
	 * 
	 */
	CHAIN_SCANMODULE_DETECTOR_NORMALIZECHANNEL_READ,

	/**
	 * The begin tag of detector ready event of the detector channel has been
	 * read.
	 */
	CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_NEXT,

	/**
	 * The detector ready event of the detector channel has been read and the
	 * handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_READ,

	/**
	 * The redo event to the detector channel is loading.
	 */
	CHAIN_SCANMODULE_DETECTOR_REDOEVENT_LOADING,

	CHAIN_SCANMODULE_DETECTOR_DEFERRED_NEXT,
	
	CHAIN_SCANMODULE_DETECTOR_DEFERRED_READ,
	
	CHAIN_SCANMODULE_DETECTOR_TRIGGERINTERVAL_NEXT,
	
	CHAIN_SCANMODULE_DETECTOR_TRIGGERINTERVAL_READ,
	
	CHAIN_SCANMODULE_DETECTOR_STOPPEDBY_NEXT,
	
	CHAIN_SCANMODULE_DETECTOR_STOPPEDBY_READ,
	
	/**
	 * A plot window is loading.
	 */
	CHAIN_SCANMODULE_PLOT_LOADING,

	/**
	 * // TODO
	 */
	CHAIN_SCANMODULE_PLOT_NAME_NEXT,

	/**
	 * // TODO
	 */
	CHAIN_SCANMODULE_PLOT_NAME_READ,

	/**
	 * The axis information of the plot window is loading.
	 */
	CHAIN_SCANMODULE_PLOT_AXIS_LOADING,

	/**
	 * The begin tag of the motor axis id of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_PLOT_AXIS_ID_NEXT,

	/**
	 * The axis id of the plot window has been read and the handler waits for
	 * the closing tag.
	 */
	CHAIN_SCANMODULE_PLOT_AXIS_ID_READ,

	/**
	 * The begin tag of the motor axis mode of the detector channel has been
	 * read.
	 */
	CHAIN_SCANMODULE_PLOT_AXIS_MODE_NEXT,

	/**
	 * The axis mode of the plot window has been read and the handler waits for
	 * the closing tag.
	 */
	CHAIN_SCANMODULE_PLOT_AXIS_MODE_READ,

	/**
	 * The begin tag of the init flag of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_PLOT_INIT_NEXT,

	/**
	 * The init flag of the plot window has been read and the handler waits for
	 * the closing tag.
	 */
	CHAIN_SCANMODULE_PLOT_INIT_READ,

	/**
	 * A y-axis of the plot window is loading.
	 */
	CHAIN_SCANMODULE_PLOT_YAXIS_LOADING,

	/**
	 * The events of the scan description are loading.
	 */
	EVENTS_LOADING,

	/**
	 * A event of the scan description is loading.
	 */
	EVENT_LOADING,

	/**
	 * The begin tag of the id of the event has been read.
	 */
	EVENT_ID_NEXT,

	/**
	 * The id of the event has been read and the handler waits for the closing
	 * tag.
	 */
	EVENT_ID_READ,

	/**
	 * The begin tag of the name of the event has been read.
	 */
	EVENT_NAME_NEXT,

	/**
	 * The name of the event has been read and the handler waits for the closing
	 * tag.
	 */
	EVENT_NAME_READ,

	/**
	 * The begin tag of the data type of the event has been read.
	 */
	EVENT_DATATYPE_NEXT,

	/**
	 * The data type of the event has been read and the handler waits for the
	 * closing tag.
	 */
	EVENT_DATATYPE_READ,

	/**
	 * The begin tag of the access of the event has been read.
	 */
	EVENT_ACCESS_NEXT,

	/**
	 * The access of the event has been read and the handler waits for the
	 * closing tag.
	 */
	EVENT_ACCESS_READ,
}