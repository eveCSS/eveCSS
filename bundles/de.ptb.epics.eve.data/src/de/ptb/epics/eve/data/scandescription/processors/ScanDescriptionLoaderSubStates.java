package de.ptb.epics.eve.data.scandescription.processors;

/**
 * 
 * 
 * @author Stephan Rehfeld <stephan.rehfed (-at-) ptb.de>
 * @author Marcus Michalsky
 */
public enum ScanDescriptionLoaderSubStates {
	NONE,
	
	SCHEDULEEVENT_LOADING,
	MONITOREVENT_LOADING,
	DETECTOREVENT_LOADING,
	EVENT_ID_NEXT,
	EVENT_ID_READ,
	EVENT_LIMIT_NEXT,
	EVENT_LIMIT_READ,
	EVENT_INCIDENT_NEXT,
	EVENT_INCIDENT_READ,
	EVENT_CHAINID_NEXT,
	EVENT_CHAINID_READ,
	EVENT_SCANMODULEID_NEXT,
	EVENT_SCANMODULEID_READ,
	
	PAUSEDETECTOREVENT_LOADING,
	PAUSEMONITOREVENT_LOADING,
	PAUSESCHEDULEEVENT_LOADING,
	PAUSEEVENT_ID_NEXT,
	PAUSEEVENT_ID_READ,
	PAUSEEVENT_LIMIT_NEXT,
	PAUSEEVENT_LIMIT_READ,
	PAUSESCHEDULEEVENT_ACTION_NEXT,
	PAUSESCHEDULEEVENT_ACTION_READ,
	PAUSEMONITOREVENT_ACTION_NEXT,
	PAUSEMONITOREVENT_ACTION_READ,
	PAUSEEVENT_INCIDENT_NEXT,
	PAUSEEVENT_INCIDENT_READ,
	PAUSEEVENT_CHAINID_NEXT,
	PAUSEEVENT_CHAINID_READ,
	PAUSEEVENT_SCANMODULEID_NEXT,
	PAUSEEVENT_SCANMODULEID_READ,
	
	YAXIS_LOADING,
	YAXIS_ID_NEXT,
	YAXIS_ID_READ,
	YAXIS_MODE_NEXT,
	YAXIS_MODE_READ,
	YAXIS_MODIFIER_NEXT,
	YAXIS_MODIFIER_READ,
	YAXIS_NORMALIZE_ID_NEXT,
	YAXIS_NORMALIZE_ID_READ,
	YAXIS_LINESTYLE_NEXT,
	YAXIS_LINESTYLE_READ,
	YAXIS_MARKSTYLE_NEXT,
	YAXIS_MARKSTYLE_READ,
	YAXIS_COLOR_NEXT,
	YAXIS_COLOR_READ,
	
	PLUGIN_CONTROLLER_LOADING,
	PLUGIN_CONTROLLER_PARAMETER_NEXT,
	PLUGIN_CONTROLLER_PARAMETER_READ,

	MONITOROPTIONS_ID_LOADING,
	MONITOROPTIONS_ID_NEXT,
	MONITOROPTIONS_ID_READ, 
}