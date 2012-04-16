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
	 * This state indicates the a chain is loading.
	 */
	CHAIN_LOADING,
	
	/**
	 * The begin tag of the start event has been read.
	 */
	CHAIN_STARTEVENT_NEXT,

	/**
	 * The start event has been read and the handler waits for the closing tag.
	 */
	CHAIN_STARTEVENT_READ,
	
	/**
	 * The begin tag of the comment has been read.
	 */
	CHAIN_COMMENT_NEXT,
	
	/**
	 * The comment has been read and the handler waits for the closing tag.
	 */
	CHAIN_COMMENT_READ,

	/**
	 * The begin tag of the save file name has been read.
	 */
	CHAIN_SAVEFILENAME_NEXT,
	
	/**
	 * The save file name has been read and the handler waits for the closing tag.
	 */
	CHAIN_SAVEFILENAME_READ,
	
	/**
	 * The begin tag of the confirm save has been read.
	 */
	CHAIN_CONFIRMSAVE_NEXT,
	
	/**
	 * The confirm save has been read and the handler waits for the closing tag.
	 */
	CHAIN_CONFIRMSAVE_READ,
	
	/**
	 * The begin tag of the auto number has been read.
	 */
	CHAIN_AUTONUMBER_NEXT,
	
	/**
	 * The auto number has been read and the handler waits for the closing tag.
	 */
	CHAIN_AUTONUMBER_READ,
	
	/**
	 * The plug in controller for saving is loading.
	 */
	CHAIN_SAVEPLUGINCONTROLLER_LOADING,

	/**
	 * The begin tag of the save scan description has been read.
	 */
	CHAIN_SAVESCANDESCRIPTION_NEXT,
	
	/**
	 * The save scan description has been read and the handler waits for the closing tag.
	 */
	CHAIN_SAVESCANDESCRIPTION_READ,
	
	/**
	 * The start events are loading.
	 */
	CHAIN_STARTEVENT,
	
	/**
	 * The pause events are loading.
	 */
	CHAIN_PAUSEEVENT,
	
	/**
	 * The redo events are loading.
	 */
	CHAIN_REDOEVENT,
	
	/**
	 * The break events are loading.
	 */
	CHAIN_BREAKEVENT,
	
	/**
	 * The stop events are loading.
	 */
	CHAIN_STOPEVENT,
	
	/**
	 * The scan modules section ha begun.
	 */
	CHAIN_SCANMODULES_LOADING,
	
	/**
	 * A scan module is loading.
	 */
	CHAIN_SCANMODULE_LOADING,
	
	/**
	 * The begin tag of the scan module type has been read.
	 */
	CHAIN_SCANMODULE_TYPE_NEXT,
	
	/**
	 * The scan module type has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_TYPE_READ,

	/**
	 * The begin tag of the scan module name has been read.
	 */
	CHAIN_SCANMODULE_NAME_NEXT,

	/**
	 * The scan module name has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_NAME_READ,
	
	/**
	 * The begin tag of the scan module xpos has been read.
	 */
	CHAIN_SCANMODULE_XPOS_NEXT,
	
	/**
	 * The scan module xpos has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_XPOS_READ,
	
	/**
	 * The begin tag of the scan module ypos has been read.
	 */
	CHAIN_SCANMODULE_YPOS_NEXT,
	
	/**
	 * The scan module ypos has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_YPOS_READ,
	
	/**
	 * The begin tag of the scan module parent has been read.
	 */
	CHAIN_SCANMODULE_PARENT_NEXT,
	
	/**
	 * The scan module parent has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_PARENT_READ,
	
	/**
	 * The begin tag of the scan module nested has been read.
	 */
	CHAIN_SCANMODULE_NESTED_NEXT,
	
	/**
	 * The scan module nested has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_NESTED_READ,
	
	/**
	 * The begin tag of the scan module appended has been read.
	 */
	CHAIN_SCANMODULE_APPENDED_NEXT,
	
	/**
	 * The scan module appended has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_APPENDED_READ,
	
	/**
	 * The begin tag of the scan module settle time has been read.
	 */
	CHAIN_SCANMODULE_SETTLETIME_NEXT,
	
	/**
	 * The scan module settle time has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SETTLETIME_READ,
	
	/**
	 * The begin tag of the scan module trigger delay has been read.
	 */
	CHAIN_SCANMODULE_TRIGGERDELAY_NEXT,
	
	/**
	 * The scan module trigger delay has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_TRIGGERDELAY_READ,
	
	/**
	 * The begin tag of the scan module trigger confirm has been read.
	 */
	CHAIN_SCANMODULE_TRIGGERCONFIRM_NEXT,
	
	/**
	 * The scan module trigger confirm has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_TRIGGERCONFIRM_READ,
	
	/**
	 * The begin tag of the scan module save axis positions has been read.
	 */
	CHAIN_SCANMODULE_SAVEAXISPOSITIONS_NEXT,
	
	/**
	 * The scan module save axis position has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SAVEAXISPOSITIONS_READ,
	
	/**
	 * The trigger events of the scan module are loading.
	 */
	CHAIN_SCANMODULE_TRIGGEREVENT,
	
	/**
	 * The break events of the scan module are loading.
	 */
	CHAIN_SCANMODULE_BREAKEVENT,
	
	/**
	 * The redo events of the scan module are loading.
	 */
	CHAIN_SCANMODULE_REDOEVENT,
	
	/**
	 * The pause events of the scan module are loading.
	 */
	CHAIN_SCANMODULE_PAUSEEVENT,
	
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
	 * The prescans value has been read and the handler waits for the closing tag.
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
	 * The postscans value has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_POSTSCAN_VALUE_READ,
	
	/**
	 * The begin tag of the postscans reset has been read.
	 */
	CHAIN_SCANMODULE_POSTSCAN_RESET_NEXT,
	
	/**
	 * The postscans reset has been read and the handler waits for the closing tag.
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
	 * The id of the moved motor axis has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_AXISID_READ,
	
	/**
	 * The begin tag of the step function of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_NEXT,
	
	/**
	 * The step function of the moved motor axis has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_READ,
	
	/**
	 * The begin tag of the position mode of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_NEXT,
	
	/**
	 * The position mode of the moved motor axis has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_READ,
	
	/**
	 * The begin tag of the start value of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_START_NEXT,
	
	/**
	 * The start value of the moved motor axis has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_START_READ,
	
	/**
	 * The begin tag of the stop value of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STOP_NEXT,
	
	/**
	 * The stop value of the moved motor axis has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STOP_READ,
	
	/**
	 * The begin tag of the step width of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_NEXT,
	
	/**
	 * The step width of the moved motor axis has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_READ,
	
	/**
	 * The main axis flag of the step width of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_NEXT,
	
	/**
	 * The main axis flag of the moved motor axis has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_READ,
	
	/**
	 * The begin tag of the step file name of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_NEXT,
	
	/**
	 * The step file name of the moved motor axis has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_READ,
	
	/**
	 * The begin tag of the position list of the moved motor axis has been read.
	 */
	CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_NEXT,
	
	/**
	 * The position list of the moved motor axis has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_READ,
	
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
	 * The motor axis id of the positioning has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_POSITIONING_AXIS_ID_READ,
	
	/**
	 * The begin tag of the channel id of the positioning has been read.
	 */
	CHAIN_SCANMODULE_POSITIONING_CHANNEL_ID_NEXT,
	
	/**
	 * The detector channel id of the positioning has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_POSITIONING_CHANNEL_ID_READ,
	
	/**
	 * The begin tag of the channel id for normalization of the positioning has been read.
	 */
	CHAIN_SCANMODULE_POSITIONING_NORMALIZE_ID_NEXT,
	
	/**
	 * The detector channel id for normalization of the positioning has been read and the handler waits for the closing tag.
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
	 * The detector channel id of the detector channel has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_CHANNELID_READ,
	
	/**
	 * The begin tag of the average count of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_AVERAGECOUNT_NEXT,
	
	/**
	 * The average count of the detector channel has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_AVERAGECOUNT_READ,
	
	/**
	 * The begin tag of confirm trigger of the average count of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_CONFIRMTRIGGER_NEXT,
	
	/**
	 * The confirm trigger of the detector channel has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_CONFIRMTRIGGER_READ,
	
	/**
	 * The begin tag of max deviation of the average count of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_MAX_DEVIATION_NEXT,
	
	/**
	 * The max deviation of the detector channel has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_MAX_DEVIATION_READ,
	
	/**
	 * The begin tag of minimum of the average count of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_MINIMUM_NEXT,
	
	/**
	 * The max deviation of the detector channel has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_MINIMUM_READ,
	
	/**
	 * The begin tag of max attempts of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_MAX_ATTEMPTS_NEXT,
	
	/**
	 * The max deviation of the detector channel has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_MAX_ATTEMPTS_READ,
	
	/**
	 * The begin tag of repeat on redo of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_REPEATONREDO_NEXT,
	
	/**
	 * The repeat on redo of the detector channel has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_REPEATONREDO_READ,
	
	/**
	 * The begin tag of detector ready event of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_NEXT,
	
	/**
	 * The detector ready event of the detector channel has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_READ,
	
	/**
	 * The redo event to the detector channel is loading.
	 */
	CHAIN_SCANMODULE_DETECTOR_REDOEVENT,
	
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
	 * The begin tag of the motor axis id  of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_PLOT_AXIS_ID_NEXT,

	/**
	 * The axis id of the plot window has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_PLOT_AXIS_ID_READ,
	
	/**
	 * The begin tag of the motor axis mode  of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_PLOT_AXIS_MODE_NEXT,
	
	/**
	 * The axis mode of the plot window has been read and the handler waits for the closing tag.
	 */
	CHAIN_SCANMODULE_PLOT_AXIS_MODE_READ,
	
	/**
	 * The begin tag of the init flag of the detector channel has been read.
	 */
	CHAIN_SCANMODULE_PLOT_INIT_NEXT,
	
	/**
	 * The init flag of the plot window has been read and the handler waits for the closing tag.
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
	 * The id of the event has been read and the handler waits for the closing tag.
	 */
	EVENT_ID_READ,
	
	/**
	 * The begin tag of the name of the event has been read.
	 */
	EVENT_NAME_NEXT,
	
	/**
	 * The name of the event has been read and the handler waits for the closing tag.
	 */
	EVENT_NAME_READ,
	
	/**
	 * The begin tag of the data type of the event has been read.
	 */
	EVENT_DATATYPE_NEXT,
	
	/**
	 * The data type of the event has been read and the handler waits for the closing tag.
	 */
	EVENT_DATATYPE_READ,
	
	/**
	 * The begin tag of the access of the event has been read.
	 */
	EVENT_ACCESS_NEXT,
	
	/**
	 * The access of the event has been read and the handler waits for the closing tag.
	 */
	EVENT_ACCESS_READ,
}