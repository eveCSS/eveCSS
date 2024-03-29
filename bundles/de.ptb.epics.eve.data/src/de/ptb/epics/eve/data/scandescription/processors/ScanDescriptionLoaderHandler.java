package de.ptb.epics.eve.data.scandescription.processors;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.util.Pair;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.swt.graphics.RGB;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleTime;
import de.ptb.epics.eve.data.measuringstation.util.NameProvider;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.MonitorOption;
import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.data.scandescription.YAxisModifier;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.data.scandescription.channelmode.StandardMode;
import de.ptb.epics.eve.data.scandescription.processors.adaptees.DetectorEventAdaptee;
import de.ptb.epics.eve.data.scandescription.processors.adaptees.DetectorEventAdapter;
import de.ptb.epics.eve.data.scandescription.processors.adaptees.PauseConditionAdaptee;
import de.ptb.epics.eve.data.scandescription.processors.adaptees.PauseConditionAdapter;
import de.ptb.epics.eve.data.scandescription.processors.adaptees.ScheduleEventAdaptee;
import de.ptb.epics.eve.data.scandescription.processors.adaptees.ScheduleEventAdapter;
import de.ptb.epics.eve.util.collection.ListUtil;

/**
 * This class represents a load handler for SAX that loads a scan description
 * from a XML file.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class ScanDescriptionLoaderHandler extends DefaultHandler {
	private static final Logger LOGGER = Logger
			.getLogger(ScanDescriptionLoaderHandler.class.getName());

	private boolean warnings;
	private boolean errors;
	private boolean fatalErrors;
	
	// The measuring station that contains the devices
	private final IMeasuringStation measuringStation;

	// a name provider translating ids into names
	private final NameProvider nameProvider;
	
	// The loaded scan description
	private ScanDescription scanDescription;

	// The current state
	private ScanDescriptionLoaderStates state;

	// The current sub state
	private ScanDescriptionLoaderSubStates subState;

	// The currently constructed chain
	private Chain currentChain;

	// The currently constructed control event
	private ControlEvent currentControlEvent;
	
	private List<Pair<ControlEvent,ScheduleEventAdaptee>> scheduleEventPairs;
	private List<Pair<ControlEvent,DetectorEventAdaptee>> detectorEventPairs;
	
	private ScheduleEventAdapter scheduleEventAdapter;
	private ScheduleEventAdaptee currentScheduleEventAdaptee;
	private DetectorEventAdapter detectorEventAdapter;
	private DetectorEventAdaptee currentDetectorEventAdaptee;
	
	private List<PauseConditionAdaptee> pauseConditionList;
	private PauseConditionAdaptee currentPauseConditionAdaptee;
	
	// The currently constructed scan module
	private ScanModule currentScanModule;
	// channel graph determines the order in which channel have to be added 
	// to the scan module to preserve normalize channel dependencies
	private List<Channel> smChannels;
	private Map<Channel, DetectorChannel> normalizationMap;
	
	// The currently constructed prescan
	private Prescan currentPrescan;

	// The currently constructed postscan
	private Postscan currentPostscan;

	// The currently constructed positioning
	private Positioning currentPositioning;

	// The currently constructed axis
	private Axis currentAxis;

	// The currently constructed channel
	private Channel currentChannel;
	private int channelLoadTime;

	// The currently constructed plot window
	private PlotWindow currentPlotWindow;

	// The currently constructed y axis
	private YAxis currentYAxis;

	// The currently constructed scan module relation reminder
	private ScanModulRelationReminder currentRelationReminder;

	// The list of all scan module relation reminder
	private List<ScanModulRelationReminder> relationReminders;

	// The list of all chains
	private List<Chain> chainList;

	// A map from scan module id to scan module
	private Map<Integer, ScanModule> idToScanModulMap;

	// A map from scan module to chain
	private Map<ScanModule, Chain> scanModulChainMap;

	// The currently constructed plug in controller
	private PluginController currentPluginController;

	// A buffer for the current parameter name
	private String currentParameterName;

	// A general purpose buffer for strings
	private StringBuffer textBuffer;

	private List<ScanDescriptionLoaderDeviceMessage> deviceMessages;
	
	/* 
	 * temporarily save IDs of defined options when 
	 *   "as in device definition" or
	 *   "used in scan"
	 * is the selected monitoring type for later comparison with current 
	 * device definition.
	 */
	private List<String> optionsDefinedInScan;

	private boolean invalidPlugin;
	
	/**
	 * This constructor creates a new SAX handler to load a scan description.
	 * 
	 * @param measuringStation
	 *            The measuring station description that contains the devices.
	 */
	public ScanDescriptionLoaderHandler(final IMeasuringStation measuringStation, final NameProvider nameProvider) {
		if (measuringStation == null) {
			throw new IllegalArgumentException(
					"The parameter 'measuringStation' must not be null!");
		}
		this.warnings = false;
		this.errors = false;
		this.fatalErrors = false;
		this.measuringStation = measuringStation;
		this.nameProvider = nameProvider;
	}
	
	/**
	 * @return the warnings
	 */
	public boolean hasWarnings() {
		return warnings;
	}

	/**
	 * @return the errors
	 */
	public boolean hasErrors() {
		return errors;
	}

	/**
	 * @return the fatalErrors
	 */
	public boolean hasFatalErrors() {
		return fatalErrors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void error(SAXParseException e) throws SAXException {
		this.errors = true;
		LOGGER.error(e.getMessage(), e);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		this.fatalErrors = true;
		LOGGER.fatal(e.getMessage(), e);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void warning(SAXParseException e) throws SAXException {
		this.warnings = true;
		LOGGER.warn(e.getMessage(), e);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startDocument() throws SAXException {
		this.scanDescription = new ScanDescription(this.measuringStation);
		this.state = ScanDescriptionLoaderStates.ROOT;
		this.subState = ScanDescriptionLoaderSubStates.NONE;
		this.chainList = new ArrayList<Chain>();
		this.relationReminders = new ArrayList<ScanModulRelationReminder>();

		this.idToScanModulMap = new HashMap<Integer, ScanModule>();
		this.scanModulChainMap = new HashMap<ScanModule, Chain>();

		this.deviceMessages = new ArrayList<ScanDescriptionLoaderDeviceMessage>();
		
		this.scheduleEventAdapter = new ScheduleEventAdapter(
				this.scanDescription);
		this.currentScheduleEventAdaptee = null;
		this.scheduleEventPairs = 
				new ArrayList<Pair<ControlEvent, ScheduleEventAdaptee>>();
		this.detectorEventAdapter = new DetectorEventAdapter(
				this.scanDescription);
		this.currentDetectorEventAdaptee = null;
		this.detectorEventPairs = 
				new ArrayList<Pair<ControlEvent, DetectorEventAdaptee>>();
		
		LOGGER.debug("Start Document");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement(final String namespaceURI, final String localName,
			final String qName, final Attributes atts) throws SAXException {

		textBuffer = new StringBuffer();

		switch (this.state) {
		case ROOT:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VERSION)) {
				this.state = ScanDescriptionLoaderStates.VERSION_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SCAN)) {
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_EVENTS)) {
				this.state = ScanDescriptionLoaderStates.EVENTS_LOADING;
			}
			break;

		case SCAN_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_REPEATCOUNT)) {
				this.state = ScanDescriptionLoaderStates.REPEATCOUNT_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_COMMENT)) {
				this.state = ScanDescriptionLoaderStates.SCAN_COMMENT_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SAVEFILENAME)) {
				this.state = ScanDescriptionLoaderStates.SCAN_SAVEFILENAME_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_CONFIRMSAVE)) {
				this.state = ScanDescriptionLoaderStates.SCAN_CONFIRMSAVE_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_AUTONUMBER)) {
				this.state = ScanDescriptionLoaderStates.SCAN_AUTONUMBER_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SAVESCANDESCRIPTION)) {
				this.state = ScanDescriptionLoaderStates.SCAN_SAVESCANDESCRIPTION_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SAVEPLUGIN)) {
				this.invalidPlugin = false;
				this.state = ScanDescriptionLoaderStates.SCAN_SAVEPLUGINCONTROLLER_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_LOADING;
				if (this.measuringStation.getPluginByName(atts.getValue(Literals.XML_ATTRIBUTE_NAME_NAME)) == null) {
					this.invalidPlugin = true;
					this.deviceMessages.add(new ScanDescriptionLoaderDeviceMessage(
							ScanDescriptionLoaderMessageType.PLUGIN_NOT_FOUND,
							"Plugin '" + atts.getValue(Literals.XML_ATTRIBUTE_NAME_NAME) + "' has been removed!"));
				}
				this.currentPluginController = this.scanDescription.getSavePluginController();
				String savePluginName = atts.getValue(Literals.XML_ATTRIBUTE_NAME_NAME);
				PlugIn savePlugin = this.measuringStation.getPluginByName(savePluginName);
				if (savePlugin != null && savePlugin.getType() == PluginTypes.SAVE) {
					this.scanDescription.getSavePluginController().setPlugin(savePlugin);
				}
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_CHAIN)) {
				this.currentChain = new Chain(Integer.parseInt(atts
						.getValue(Literals.XML_ATTRIBUTE_NAME_ID)));
				this.chainList.add(this.currentChain);
				this.pauseConditionList = new ArrayList<>();
				this.currentPauseConditionAdaptee = null;
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_MONITOROPTIONS)) {
				if (atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE) == null) {
					this.scanDescription.setMonitorOption(MonitorOption.CUSTOM);
				} else {
					this.scanDescription.setMonitorOption(
							MonitorOption.stringToType(atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE)));
					this.optionsDefinedInScan = new ArrayList<>();
				}
				this.state = ScanDescriptionLoaderStates.MONITOROPTIONS_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.MONITOROPTIONS_ID_LOADING;
			}
			break;

		case CHAIN_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STARTEVENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_STARTEVENT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_REDOEVENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_REDOEVENT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_BREAKEVENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_BREAKEVENT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STOPEVENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_STOPEVENT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_PAUSECONDITIONS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITIONS_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SCANMODULES)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULES_LOADING;
			}
			break;

		case CHAIN_PAUSECONDITIONS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PAUSECONDITION)) {
				this.currentPauseConditionAdaptee = new PauseConditionAdaptee();
				this.currentPauseConditionAdaptee.setId(Integer.parseInt(
						atts.getValue(Literals.XML_ATTRIBUTE_NAME_ID)));
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_LOADING;
			}
			break;

		case CHAIN_PAUSECONDITION_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_DEVICEID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_DEVICEID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_OPERATOR)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_OPERATOR_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_PAUSELIMIT)) {
				this.currentPauseConditionAdaptee.setPauseType(
						DataTypes.stringToType(atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE)));
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_PAUSELIMIT_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_CONTINUELIMIT)) {
				this.currentPauseConditionAdaptee.setContinueType(
						DataTypes.stringToType(atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE)));
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_CONTINUELIMIT_NEXT;
			}
			break;

		case CHAIN_SCANMODULES_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SCANMODULE)) {
				this.currentScanModule = new ScanModule(Integer.parseInt(atts
						.getValue(Literals.XML_ATTRIBUTE_NAME_ID)));
				this.currentScanModule.smLoading = true;
				this.currentRelationReminder = new ScanModulRelationReminder(
						this.currentScanModule);
				this.smChannels = new ArrayList<Channel>();
				this.normalizationMap = new HashMap<Channel, DetectorChannel>();
				this.channelLoadTime = 0;
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_NAME_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_XPOS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_XPOS_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_YPOS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_YPOS_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_PARENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PARENT_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_NESTED)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_NESTED_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_APPENDED)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_APPENDED_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SCANMODULE_CLASSIC)) {
				this.currentScanModule.setType(ScanModuleTypes.CLASSIC);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SCANMODULE_SAVE_AXIS_POSITIONS)) {
				this.currentScanModule.setType(ScanModuleTypes.SAVE_AXIS_POSITIONS);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SAVE_AXIS_POSITIONS_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SCANMODULE_SAVE_CHANNEL_VALUES)) {
				this.currentScanModule.setType(ScanModuleTypes.SAVE_CHANNEL_VALUES);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SAVE_CHANNEL_VALUES_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SCANMODULE_DYNAMIC_AXIS_POSITIONS)) {
				this.currentScanModule.setType(ScanModuleTypes.DYNAMIC_AXIS_POSITIONS);
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SCANMODULE_DYNAMIC_CHANNEL_VALUES)) {
				this.currentScanModule.setType(ScanModuleTypes.DYNAMIC_CHANNEL_VALUES);
			}
			break;
			
		case CHAIN_SCANMODULE_CLASSIC_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VALUECOUNT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_VALUECOUNT_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SETTLETIME)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SETTLETIME_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGERDELAY)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGERDELAY_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMAXIS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGERCONFIRMAXIS_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMCHANNEL)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGERCONFIRMCHANNEL_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGEREVENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGEREVENT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_BREAKEVENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_BREAKEVENT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_REDOEVENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_REDOEVENT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_PRESCAN)) {
				this.currentPrescan = new Prescan();
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_POSTSCAN)) {
				this.currentPostscan = new Postscan();
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SMAXIS)) {
				this.currentAxis = new Axis(this.currentScanModule);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNEL)) {
				this.currentChannel = new Channel(this.currentScanModule);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_POSITIONING)) {
				this.currentPositioning = new Positioning(this.currentScanModule);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_PLOT)) {
				this.currentPlotWindow = new PlotWindow(this.currentScanModule);
				this.currentPlotWindow.setId(Integer.parseInt(atts
						.getValue(Literals.XML_ATTRIBUTE_NAME_ID)));
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_SAVE_AXIS_POSITIONS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SMAXIS)) {
				this.currentAxis = new Axis(this.currentScanModule);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_SAVE_CHANNEL_VALUES_LOADING:
			if (qName.equals("smchannel")) {
				this.currentChannel = new Channel(this.currentScanModule);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_POSITIONING_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_AXISID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_AXIS_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_CHANNELID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_CHANNEL_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_NORMALIZEID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_NORMALIZE_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_PLUGIN)) {
				this.invalidPlugin = false;
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_CONTROLLER_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_LOADING;
				if (this.measuringStation.getPluginByName(
						atts.getValue(Literals.XML_ATTRIBUTE_NAME_NAME)) == null) {
					// plugin not found
					this.invalidPlugin = true;
					this.deviceMessages.add(new ScanDescriptionLoaderDeviceMessage(
							ScanDescriptionLoaderMessageType.PLUGIN_NOT_FOUND,
							"Plugin '" + atts.getValue(Literals.XML_ATTRIBUTE_NAME_NAME)+ "' has been removed!"));
				}
				this.currentPluginController = this.currentPositioning
						.getPluginController();
				this.currentPluginController.setPlugin(this.measuringStation
						.getPluginByName(atts.getValue(Literals.XML_ATTRIBUTE_NAME_NAME)));
			}
			break;

		case CHAIN_SCANMODULE_PRESCAN_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_VALUE_NEXT;
			}
			break;

		case CHAIN_SCANMODULE_POSTSCAN_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_VALUE_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_RESETORIGINALVALUE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_RESET_NEXT;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_LOADING:
			if (qName.equals("axisid")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_AXISID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STEPFUNCTION)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_POSITIONMODE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_START)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_START_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STOP)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STOP_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STEPWIDTH)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_STEPFILENAME)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_POSITIONLIST)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_RANGE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_EXPRESSION_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_ISMAINAXIS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_PLUGIN)) {
				if (this.currentAxis.getAbstractDevice() != null) {
					this.invalidPlugin = false;
					this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_CONTROLLER_LOADING;
					this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_LOADING;
					this.currentPluginController = new PluginController();
					if (this.measuringStation.getPluginByName(atts
							.getValue(Literals.XML_ATTRIBUTE_NAME_NAME)) == null) {
						// plugin not found
						this.invalidPlugin = true;
						this.deviceMessages
								.add(new ScanDescriptionLoaderDeviceMessage(
										ScanDescriptionLoaderMessageType.PLUGIN_NOT_FOUND,
										"Plugin '" + atts.getValue(Literals.XML_ATTRIBUTE_NAME_NAME)
												+ "' has been removed!"));
					}
					this.currentAxis
							.setPluginController(this.currentPluginController);
					this.currentPluginController
							.setPlugin(this.measuringStation
									.getPluginByName(atts.getValue(Literals.XML_ATTRIBUTE_NAME_NAME)));
				}
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNELID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_CHANNELID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_NORMALIZEID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_NORMALIZECHANNEL_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNEL_STANDARD)) {
				this.currentChannel.setChannelMode(ChannelModes.STANDARD);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNEL_INTERVAL)) {
				this.currentChannel.setChannelMode(ChannelModes.INTERVAL);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_INTERVAL_LOADING;
			} 
			break;

		case CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_AVERAGECOUNT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_AVERAGECOUNT_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_MAXDEVIATION)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_MAX_DEVIATION_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_MINIMUM)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_MINIMUM_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_MAXATTEMPTS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_MAX_ATTEMPTS_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SENDREADYEVENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_NEXT;
			}  else if (qName.equals(Literals.XML_ELEMENT_NAME_REDOEVENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_REDOEVENT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_DEFERREDTRIGGER)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_DEFERRED_NEXT;
			}
			break;
			
		case CHAIN_SCANMODULE_DETECTOR_INTERVAL_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNEL_TRIGGERINTERVAL)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_TRIGGERINTERVAL_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNEL_STOPPEDBY)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_STOPPEDBY_NEXT;
			}
			break;
		
		case CHAIN_STARTEVENT_LOADING:
		case CHAIN_REDOEVENT_LOADING:
		case CHAIN_BREAKEVENT_LOADING:
		case CHAIN_STOPEVENT_LOADING:
		case CHAIN_SCANMODULE_TRIGGEREVENT_LOADING:
		case CHAIN_SCANMODULE_BREAKEVENT_LOADING:
		case CHAIN_SCANMODULE_REDOEVENT_LOADING:
		case CHAIN_SCANMODULE_DETECTOR_REDOEVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_DETECTOREVENT)) {
				this.currentControlEvent = new ControlEvent(EventTypes.DETECTOR);
				this.currentDetectorEventAdaptee = new DetectorEventAdaptee();
				this.subState = ScanDescriptionLoaderSubStates.DETECTOREVENT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_MONITOREVENT)) {
				this.currentControlEvent = new ControlEvent(EventTypes.MONITOR);
				this.currentDetectorEventAdaptee = new DetectorEventAdaptee();
				this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SCHEDULEEVENT)) {
				this.currentControlEvent = new ControlEvent(EventTypes.SCHEDULE);
				this.currentScheduleEventAdaptee = new ScheduleEventAdaptee();
				this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_PLOT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_NAME_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_XAXIS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_LOADING;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_INIT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_INIT_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_YAXIS)) {
				this.currentYAxis = new YAxis();
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_YAXIS_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_AXIS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_MODE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_MODE_NEXT;
			}
		}

		switch (this.subState) {
		
		case DETECTOREVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.subState = ScanDescriptionLoaderSubStates.EVENT_ID_NEXT;
			}
			break;
		
		case MONITOREVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.subState = ScanDescriptionLoaderSubStates.EVENT_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_LIMIT)) {
				this.currentControlEvent.getLimit().setComparison(
						ComparisonTypes.stringToType(atts
								.getValue(Literals.XML_ATTRIBUTE_NAME_COMPARISON)));
				this.currentControlEvent.getLimit().setType(
						DataTypes.stringToType(atts.getValue(Literals.XML_ATTRIBUTE_NAME_TYPE)));
				this.subState = ScanDescriptionLoaderSubStates.EVENT_LIMIT_NEXT;
			}
			break;
			
		case SCHEDULEEVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_INCIDENT)) {
				this.subState = ScanDescriptionLoaderSubStates.EVENT_INCIDENT_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_CHAINID)) {
				this.subState = ScanDescriptionLoaderSubStates.EVENT_CHAINID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_SMID)) {
				this.subState = ScanDescriptionLoaderSubStates.EVENT_SCANMODULEID_NEXT;
			}
			break;

		case YAXIS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_MODE)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_MODE_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_MODIFIER)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_MODIFIER_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_NORMALIZEID)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_NORMALIZE_ID_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_LINESTYLE)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LINESTYLE_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_MARKSTYLE)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_MARKSTYLE_NEXT;
			} else if (qName.equals(Literals.XML_ELEMENT_NAME_COLOR)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_COLOR_NEXT;
			}
			break;

		case PLUGIN_CONTROLLER_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PARAMETER)) {
				this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_PARAMETER_NEXT;
				this.currentParameterName = atts.getValue(Literals.XML_ATTRIBUTE_NAME_NAME);
			}
			break;

		case MONITOROPTIONS_ID_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.subState = ScanDescriptionLoaderSubStates.MONITOROPTIONS_ID_NEXT;
			}
			break;
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Start Element: " + qName + " --> State: " + 
					this.state + " (Substate: " + this.subState + ")");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void characters(final char[] ch, final int start, final int length) {
		String s = new String(ch, start, length);
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

		if (textBuffer == null) {
			textBuffer = new StringBuffer();
		}
		
		String temp = textBuffer.toString().trim();
		textBuffer = new StringBuffer(temp);

		switch (this.state) {
		case VERSION_NEXT:
			this.scanDescription.setVersion(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.VERSION_READ;
			break;

		case REPEATCOUNT_NEXT:
			this.scanDescription.setRepeatCount(Integer.parseInt(textBuffer
					.toString()));
			this.state = ScanDescriptionLoaderStates.REPEATCOUNT_READ;
			break;

		case SCAN_COMMENT_NEXT:
			this.scanDescription.setComment(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.SCAN_COMMENT_READ;
			break;

		case SCAN_SAVEFILENAME_NEXT:
			this.scanDescription.setSaveFilename(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.SCAN_SAVEFILENAME_READ;
			break;

		case SCAN_CONFIRMSAVE_NEXT:
			this.scanDescription.setConfirmSave(Boolean.parseBoolean(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.SCAN_CONFIRMSAVE_READ;
			break;

		case SCAN_AUTONUMBER_NEXT:
			this.scanDescription.setAutoNumber(Boolean.parseBoolean(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.SCAN_AUTONUMBER_READ;
			break;

		case SCAN_SAVESCANDESCRIPTION_NEXT:
			this.scanDescription.setSaveScanDescription(Boolean.parseBoolean(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.SCAN_SAVESCANDESCRIPTION_READ;
			break;

		case SCAN_SAVEPLUGINCONTROLLER_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SAVEPLUGIN)) {
				this.currentPluginController = null;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			}
			break;

		case CHAIN_PAUSECONDITION_DEVICEID_NEXT:
			this.currentPauseConditionAdaptee.setDeviceId(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_DEVICEID_READ;
			break;

		case CHAIN_PAUSECONDITION_OPERATOR_NEXT:
			this.currentPauseConditionAdaptee.setOperator(ComparisonTypes.stringToType(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_OPERATOR_READ;
			break;

		case CHAIN_PAUSECONDITION_PAUSELIMIT_NEXT:
			this.currentPauseConditionAdaptee.setPauseLimit(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_PAUSELIMIT_READ;
			break;

		case CHAIN_PAUSECONDITION_CONTINUELIMIT_NEXT:
			this.currentPauseConditionAdaptee.setContinueLimit(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_CONTINUELIMIT_READ;
			break;

		case CHAIN_SCANMODULE_NAME_NEXT:
			this.currentScanModule.setName(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_NAME_READ;
			break;

		case CHAIN_SCANMODULE_PARENT_NEXT:
			this.currentRelationReminder.setParent(Integer.parseInt(textBuffer
					.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PARENT_READ;
			break;

		case CHAIN_SCANMODULE_APPENDED_NEXT:
			this.currentRelationReminder.setAppended(Integer
					.parseInt(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_APPENDED_READ;
			break;

		case CHAIN_SCANMODULE_NESTED_NEXT:
			this.currentRelationReminder.setNested(Integer.parseInt(textBuffer
					.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_NESTED_READ;
			break;

		case CHAIN_SCANMODULE_VALUECOUNT_NEXT:
			this.currentScanModule.setValueCount(Integer.parseInt(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_VALUECOUNT_READ;
			break;
			
		case CHAIN_SCANMODULE_SETTLETIME_NEXT:
			double settletime = Double.NEGATIVE_INFINITY;
			try {
				settletime = Double.parseDouble(textBuffer.toString());
			} catch (final NumberFormatException e) {
				LOGGER.error(e.getMessage(), e);
			}
			this.currentScanModule.setSettleTime(settletime);
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SETTLETIME_READ;
			break;

		case CHAIN_SCANMODULE_TRIGGERDELAY_NEXT:
			double triggerdelay = Double.NEGATIVE_INFINITY;
			try {
				triggerdelay = Double.parseDouble(textBuffer.toString());
			} catch (final NumberFormatException e) {
				LOGGER.error(e.getMessage(), e);
			}
			this.currentScanModule.setTriggerDelay(triggerdelay);
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGERDELAY_READ;
			break;

		case CHAIN_SCANMODULE_TRIGGERCONFIRMAXIS_NEXT:
			this.currentScanModule.setTriggerConfirmAxis(Boolean
					.parseBoolean(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGERCONFIRMAXIS_READ;
			break;

		case CHAIN_SCANMODULE_TRIGGERCONFIRMCHANNEL_NEXT:
			this.currentScanModule.setTriggerConfirmChannel(Boolean
					.parseBoolean(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGERCONFIRMCHANNEL_READ;
			break;
			
		case CHAIN_SCANMODULE_PRESCAN_ID_NEXT:
			if (this.measuringStation.getPrePostscanDeviceById(textBuffer
					.toString()) != null) {
				this.currentPrescan
						.setAbstractPrePostscanDevice(this.measuringStation
								.getPrePostscanDeviceById(textBuffer.toString()));
			} else {
				this.deviceMessages.add(new ScanDescriptionLoaderDeviceMessage(
						ScanDescriptionLoaderMessageType.
						PRE_POST_SCAN_DEVICE_ID_NOT_FOUND, 
						"Prescan-Device '" + nameProvider.translatePrePostScanDeviceId(textBuffer.toString()) + 
						"' has been removed."));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_ID_READ;
			break;
			
		case CHAIN_SCANMODULE_PRESCAN_VALUE_NEXT:
			this.currentPrescan.setValue(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_VALUE_READ;
			break;

		case CHAIN_SCANMODULE_POSTSCAN_ID_NEXT:
			if (this.measuringStation.getPrePostscanDeviceById(textBuffer.toString()) != null) {
				this.currentPostscan
				.setAbstractPrePostscanDevice(this.measuringStation
						.getPrePostscanDeviceById(textBuffer.toString()));
			} else {
				this.deviceMessages.add(new ScanDescriptionLoaderDeviceMessage(
						ScanDescriptionLoaderMessageType.
						PRE_POST_SCAN_DEVICE_ID_NOT_FOUND, 
						"Postscan-Device '" + nameProvider.translatePrePostScanDeviceId(textBuffer.toString()) + 
						"' has been removed."));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_ID_READ;
			break;

		case CHAIN_SCANMODULE_POSTSCAN_VALUE_NEXT:
			this.currentPostscan.setValue(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_VALUE_READ;
			break;

		case CHAIN_SCANMODULE_POSTSCAN_RESET_NEXT:
			if (this.currentPostscan.getAbstractDevice() != null) {
				this.currentPostscan.setReset(Boolean.parseBoolean(textBuffer
						.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_RESET_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_AXISID_NEXT:
			if (this.measuringStation.getMotorAxisById(textBuffer.toString()) != null) {
				// MotorAxis ist am Messplatz vorhanden
				this.currentAxis.setMotorAxis(this.measuringStation
						.getMotorAxisById(textBuffer.toString()));
			} else {
				this.deviceMessages.add(new ScanDescriptionLoaderDeviceMessage(
						ScanDescriptionLoaderMessageType.MOTOR_AXIS_ID_NOT_FOUND,
						"Axis '" + nameProvider.translateMotorAxisId(textBuffer.toString()) + "' has been removed."));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_AXISID_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_NEXT:
			if (this.currentAxis.getAbstractDevice() != null) {
				this.currentAxis.setStepfunction(Stepfunctions.getEnum(textBuffer.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_NEXT:
			if (this.currentAxis.getAbstractDevice() != null) {
				this.currentAxis.setPositionMode(PositionMode.stringToType(textBuffer.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_START_NEXT:
			String startValue = textBuffer.toString();
			if (this.currentAxis.getAbstractDevice() != null) {
				switch (this.currentAxis.getType()) {
				case DATETIME:
					switch (this.currentAxis.getPositionMode()) {
					case ABSOLUTE:
						try {
							this.currentAxis.setStart(new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss.SSS")
									.parse(startValue));
						} catch (ParseException e) {
							LOGGER.error(e.getMessage(), e);
						}
						break;
					case RELATIVE:
						try {
							DatatypeFactory factory = DatatypeFactory
									.newInstance();
							this.currentAxis.setStart(factory
									.newDuration(textBuffer.toString()));
						} catch (DatatypeConfigurationException e2) {
							LOGGER.error(e2.getMessage(), e2);
						}
						break;
					}
					break;
				case DOUBLE:
					this.currentAxis.setStart(Double.parseDouble(startValue));
					break;
				case INT:
					this.currentAxis.setStart(Integer.parseInt(startValue));
					break;
				default:
					break;
				}
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_START_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STOP_NEXT:
			String stopValue = textBuffer.toString();
			if (this.currentAxis.getAbstractDevice() != null) {
				switch (this.currentAxis.getType()) {
				case DATETIME:
					switch (this.currentAxis.getPositionMode()) {
					case ABSOLUTE:
						try {
							this.currentAxis
									.setStop(new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss.SSS")
											.parse(stopValue));
						} catch (ParseException e1) {
							LOGGER.error(e1.getMessage(), e1);
						}
						break;
					case RELATIVE:
						try {
							DatatypeFactory factory = DatatypeFactory
									.newInstance();
							this.currentAxis.setStop(factory
									.newDuration(textBuffer.toString()));
						} catch (DatatypeConfigurationException e2) {
							LOGGER.error(e2.getMessage(), e2);
						}
						break;
					}
					break;
				case DOUBLE:
					this.currentAxis.setStop(Double.parseDouble(stopValue));
					break;
				case INT:
					this.currentAxis.setStop(Integer.parseInt(stopValue));
					break;
				default:
					break;
				}
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STOP_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_NEXT:
			String stepwidthValue = textBuffer.toString();
			if (this.currentAxis.getAbstractDevice() != null) {
				switch (this.currentAxis.getType()) {
				case DATETIME:
					switch (this.currentAxis.getPositionMode()) {
					case ABSOLUTE:
						try {
							this.currentAxis.setStepwidth(new SimpleDateFormat(
									"HH:mm:ss.SSS").parse(stepwidthValue));
						} catch (ParseException e) {
							LOGGER.error(e.getMessage(), e);
						}
						break;
					case RELATIVE:
						try {
							DatatypeFactory factory = DatatypeFactory
									.newInstance();
							this.currentAxis.setStepwidth(factory
									.newDuration(textBuffer.toString()));
						} catch (DatatypeConfigurationException e2) {
							LOGGER.error(e2.getMessage(), e2);
						}
						break;
					}
					break;
				case DOUBLE:
					this.currentAxis.setStepwidth(Double
							.parseDouble(stepwidthValue));
					break;
				case INT:
					this.currentAxis.setStepwidth(Integer
							.parseInt(stepwidthValue));
					break;
				default:
					break;
				}
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_NEXT:
			if (this.currentAxis.getAbstractDevice() != null) {
				this.currentAxis.setFile(new File(textBuffer.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_NEXT:
			if (this.currentAxis.getAbstractDevice() != null) {
				this.currentAxis.setPositionlist(textBuffer.toString());
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_EXPRESSION_NEXT:
			if (this.currentAxis.getAbstractDevice() != null) {
				this.currentAxis.setRange(textBuffer.toString());
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_EXPRESSION_READ;
			break;
			
		case CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_NEXT:
			if (this.currentAxis.getAbstractDevice() != null) {
				this.currentAxis.setMainAxis(Boolean.parseBoolean(textBuffer.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_CHANNELID_NEXT:
			if (this.measuringStation.getDetectorChannelById(textBuffer
					.toString()) != null) {
				this.currentChannel.setDetectorChannel(this.measuringStation
						.getDetectorChannelById(textBuffer.toString()));
			} else {
				this.deviceMessages.add(new ScanDescriptionLoaderDeviceMessage(
					ScanDescriptionLoaderMessageType.DETECTOR_CHANNEL_ID_NOT_FOUND,
					"Channel '" + nameProvider.translateDetectorChannelId(textBuffer.toString()) + "' has been removed."));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_CHANNELID_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_AVERAGECOUNT_NEXT:
			int averageCount = StandardMode.AVERAGE_COUNT_DEFAULT_VALUE;
			try {
				averageCount = Integer.parseInt(textBuffer.toString());
			} catch (NumberFormatException e) {
				LOGGER.error(e.getMessage(), e);
			}

			if (this.currentChannel.getAbstractDevice() != null) {
				this.currentChannel.setAverageCount(averageCount);
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_AVERAGECOUNT_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_MAX_DEVIATION_NEXT:
			if (this.currentChannel.getAbstractDevice() != null) {
				this.currentChannel.setMaxDeviation(Double
						.parseDouble(textBuffer.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_MAX_DEVIATION_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_MINIMUM_NEXT:
			if (this.currentChannel.getAbstractDevice() != null) {
				this.currentChannel.setMinimum(Double.parseDouble(textBuffer
						.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_MINIMUM_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_MAX_ATTEMPTS_NEXT:
			if (this.currentChannel.getAbstractDevice() != null) {
				this.currentChannel.setMaxAttempts(Integer.parseInt(textBuffer
						.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_MAX_ATTEMPTS_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_NORMALIZECHANNEL_NEXT:
			if (this.currentChannel.getAbstractDevice() != null) {
				this.normalizationMap.put(this.currentChannel,
						this.measuringStation.getDetectorChannelById(textBuffer
								.toString()));
				/*this.currentChannel.setNormalizeChannel(
						this.measuringStation.getDetectorChannelById(
								textBuffer.toString()));*/
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_NORMALIZECHANNEL_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_NEXT:
			if (this.currentChannel.getAbstractDevice() != null) {
				// this.createEventPair(); ??
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_READ;
			break;
			
		case CHAIN_SCANMODULE_DETECTOR_DEFERRED_NEXT:
			if (this.currentChannel.getAbstractDevice() != null) {
				if (Boolean.parseBoolean(textBuffer.toString())) {
					this.currentChannel.setDeferred(true);
				}
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_DEFERRED_READ;
			break;
			
		case CHAIN_SCANMODULE_DETECTOR_TRIGGERINTERVAL_NEXT:
			try  {
				this.currentChannel.setTriggerInterval(Double.parseDouble(textBuffer.toString()));
			} catch (NumberFormatException e) {
				LOGGER.error(e.getMessage(), e);
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_TRIGGERINTERVAL_READ;
			break;
			
		case CHAIN_SCANMODULE_DETECTOR_STOPPEDBY_NEXT:
			if (this.measuringStation.getDetectorChannelById(textBuffer
					.toString()) != null) {
				this.currentChannel.setStoppedBy(this.measuringStation
						.getDetectorChannelById(textBuffer.toString()));
			} else {
				this.deviceMessages.add(new ScanDescriptionLoaderDeviceMessage(
					ScanDescriptionLoaderMessageType.DETECTOR_CHANNEL_ID_NOT_FOUND,
					"Channel '" + nameProvider.translateDetectorChannelId(textBuffer.toString()) + "' has been removed."));
			}
			// TODO analogy to normalize channel map (to check whether stopped by channel is present in scan module) 
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_STOPPEDBY_READ;
			break;
			
		case CHAIN_SCANMODULE_PLOT_NAME_NEXT:
			this.currentPlotWindow.setName(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_NAME_READ;
			break;


		case CHAIN_SCANMODULE_PLOT_AXIS_ID_NEXT:
			this.currentPlotWindow.setXAxis(this.measuringStation
					.getMotorAxisById(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_ID_READ;
			break;

		case CHAIN_SCANMODULE_PLOT_AXIS_MODE_NEXT:
			PlotModes plotMode = PlotModes.stringToMode(textBuffer.toString());
			this.currentPlotWindow.setMode(plotMode);

			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_MODE_READ;
			break;

		case CHAIN_SCANMODULE_PLOT_INIT_NEXT:
			this.currentPlotWindow.setInit(Boolean.parseBoolean(textBuffer
					.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_INIT_READ;
			break;

		case CHAIN_SCANMODULE_XPOS_NEXT:
			this.currentScanModule.setX(Integer.parseInt(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_XPOS_READ;
			break;

		case CHAIN_SCANMODULE_YPOS_NEXT:
			this.currentScanModule.setY(Integer.parseInt(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_YPOS_READ;
			break;

		case CHAIN_SCANMODULE_POSITIONING_AXIS_ID_NEXT:
			if (this.measuringStation.getMotorAxisById(textBuffer.toString()) != null) {
				// MotorAxis ist am Messplatz vorhanden
				this.currentPositioning.setMotorAxis(this.measuringStation
						.getMotorAxisById(textBuffer.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_AXIS_ID_READ;
			break;

		case CHAIN_SCANMODULE_POSITIONING_CHANNEL_ID_NEXT:
			if (this.measuringStation.getDetectorChannelById(textBuffer
					.toString()) != null) {
				// DetectorChannel ist am Messplatz vorhanden
				this.currentPositioning
						.setDetectorChannel(this.measuringStation
								.getDetectorChannelById(textBuffer.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_CHANNEL_ID_READ;
			break;

		case CHAIN_SCANMODULE_POSITIONING_NORMALIZE_ID_NEXT:
			if (this.measuringStation.getDetectorChannelById(textBuffer
					.toString()) != null) {
				// DetectorChannel ist am Messplatz vorhanden
				this.currentPositioning.setNormalization(this.measuringStation
						.getDetectorChannelById(textBuffer.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_NORMALIZE_ID_READ;
			break;

		case MONITOROPTIONS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_MONITOROPTIONS)) {
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			}
			break;
		}

		switch (this.subState) {

		case EVENT_ID_NEXT:
			if (this.currentControlEvent.getEventType().equals(
					EventTypes.DETECTOR)) {
				this.currentDetectorEventAdaptee.setId(textBuffer.toString());
			} else if (this.currentControlEvent.getEventType().equals(
					EventTypes.MONITOR)) {
				this.currentControlEvent.setEvent(this.measuringStation.
						getEventById(textBuffer.toString()));
			}
			this.currentControlEvent.setId(textBuffer.toString());
			this.subState = ScanDescriptionLoaderSubStates.EVENT_ID_READ;
			break;

		case EVENT_LIMIT_NEXT:
			this.currentControlEvent.getLimit().setValue(textBuffer.toString());
			this.subState = ScanDescriptionLoaderSubStates.EVENT_LIMIT_READ;
			break;

		case EVENT_INCIDENT_NEXT:
			this.currentScheduleEventAdaptee.setScheduleTime(ScheduleTime
					.stringToEnum(textBuffer.toString()));
			this.subState = ScanDescriptionLoaderSubStates.EVENT_INCIDENT_READ;
			break;

		case EVENT_CHAINID_NEXT:
			this.currentScheduleEventAdaptee.setChainId(
					Integer.parseInt(textBuffer.toString()));
			this.subState = ScanDescriptionLoaderSubStates.EVENT_CHAINID_READ;
			break;

		case EVENT_SCANMODULEID_NEXT:
			this.currentScheduleEventAdaptee.setScanModuleId(
					Integer.parseInt(textBuffer.toString()));
			this.subState = ScanDescriptionLoaderSubStates.EVENT_SCANMODULEID_READ;
			break;

		case YAXIS_ID_NEXT:
			if (this.measuringStation.getDetectorChannelById(textBuffer
					.toString()) != null) {
				// DetectorChannel ist am Messplatz vorhanden
				this.currentYAxis.setDetectorChannel(this.measuringStation
						.getDetectorChannelById(textBuffer.toString()));
			}
			this.subState = ScanDescriptionLoaderSubStates.YAXIS_ID_READ;
			break;

		case YAXIS_MODE_NEXT:
			PlotModes plotMode = PlotModes.stringToMode(textBuffer.toString());
			this.currentYAxis.setMode(plotMode);
			this.subState = ScanDescriptionLoaderSubStates.YAXIS_MODE_READ;
			break;

		case YAXIS_MODIFIER_NEXT:
			this.currentYAxis.setModifier(YAxisModifier.valueOf(
					textBuffer.toString().trim().toUpperCase()));
			this.subState = ScanDescriptionLoaderSubStates.YAXIS_MODIFIER_READ;
			break;
			
		case YAXIS_NORMALIZE_ID_NEXT:
			if (this.measuringStation.getDetectorChannelById(textBuffer
					.toString()) != null) {
				// DetectorChannel ist am Messplatz vorhanden
				this.currentYAxis.setNormalizeChannel(this.measuringStation
						.getDetectorChannelById(textBuffer.toString()));
			}
			this.subState = ScanDescriptionLoaderSubStates.YAXIS_NORMALIZE_ID_READ;
			break;

		case YAXIS_LINESTYLE_NEXT:
			final String linestyleBuffer = textBuffer.toString();
			boolean foundLinestyle = false;
			TraceType[] tracetypes = TraceType.values();
			for (int i = 0; i < tracetypes.length; i++) {
				if (tracetypes[i].toString().equals(linestyleBuffer)) {
					foundLinestyle = true;
				}
			}
			if (foundLinestyle) {
				for (int i = 0; i < tracetypes.length; i++) {
					if (tracetypes[i].toString().equals(linestyleBuffer)) {
						this.currentYAxis.setLinestyle(tracetypes[i]);
					}
				}
			}
			this.subState = ScanDescriptionLoaderSubStates.YAXIS_LINESTYLE_READ;
			break;

		case YAXIS_MARKSTYLE_NEXT:
			final String markstyleBuffer = textBuffer.toString();
			boolean foundMarkstyle = false;
			PointStyle[] markstyles = PointStyle.values();
			for (int i = 0; i < markstyles.length; i++) {
				try {
					if (markstyles[i].toString().equals(
							URLDecoder.decode(markstyleBuffer, "UTF-8"))) {
						foundMarkstyle = true;
					}
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if (foundMarkstyle) {
				for (int i = 0; i < markstyles.length; i++) {
					try {
						if (markstyles[i].toString().equals(
								URLDecoder.decode(markstyleBuffer, "UTF-8"))) {
							this.currentYAxis.setMarkstyle(markstyles[i]);
						}
					} catch (UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
			this.subState = ScanDescriptionLoaderSubStates.YAXIS_MARKSTYLE_READ;
			break;

		case YAXIS_COLOR_NEXT:
			final String colorBuffer = textBuffer.toString();
			String redHex = colorBuffer.substring(0, 2);
			int red = Integer.parseInt(redHex, 16);
			String greenHex = colorBuffer.substring(2, 4);
			int green = Integer.parseInt(greenHex, 16);
			String blueHex = colorBuffer.substring(4);
			int blue = Integer.parseInt(blueHex, 16);

			if (colorBuffer.length() == 6) {
				this.currentYAxis.setColor(new RGB(red, green, blue));
			} else {
				this.currentYAxis.setColor(new RGB(0, 0, 0));
			}

			this.subState = ScanDescriptionLoaderSubStates.YAXIS_COLOR_READ;
			break;

		case PLUGIN_CONTROLLER_PARAMETER_NEXT:
			if (!this.invalidPlugin) {
				if (this.currentParameterName.equals("location")) {
					// location is no parameter of the plugin
					if (textBuffer.toString().equals(
							this.currentPluginController.getPlugin()
									.getLocation())) {
					} else {
						// Hinweise ausgeben, daß Location überschrieben wurde!
						this.deviceMessages
							.add(new ScanDescriptionLoaderDeviceMessage(
							ScanDescriptionLoaderMessageType.PLUGIN_LOCATION_CHANGED,
							textBuffer.toString()
							+ " replaced by "
							+ this.currentPluginController.getPlugin()
									.getLocation()));
					}
					this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_PARAMETER_READ;
					break;
				}
				this.currentPluginController.set(this.currentParameterName,
						textBuffer.toString());
			}
			this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_PARAMETER_READ;
			break;

		case MONITOROPTIONS_ID_NEXT:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				switch (this.scanDescription.getMonitorOption()) {
				case CUSTOM:
					AbstractPrePostscanDevice monOption = this.measuringStation
							.getPrePostscanDeviceById(textBuffer.toString());
					if (monOption != null && monOption instanceof Option) {
						this.scanDescription.addMonitor((Option) monOption);
					} else {
						this.deviceMessages
								.add(new ScanDescriptionLoaderDeviceMessage(
										ScanDescriptionLoaderMessageType.MONITOR_OPTION_ID_NOT_FOUND,
										"Monitor-Device '" + nameProvider
												.translatePrePostScanDeviceId(
														textBuffer.toString())
												+ "' has been removed."));
					}
					break;
				case AS_IN_DEVICE_DEFINITION:
				case USED_IN_SCAN:
					this.optionsDefinedInScan.add(textBuffer.toString());
					break;
				default:
					break;
				}
			}
			this.subState = ScanDescriptionLoaderSubStates.MONITOROPTIONS_ID_READ;
			break;
		}

		textBuffer = null;

		/* ***************************************************************** */
		/* ***************************************************************** */

		switch (this.state) {
		case VERSION_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VERSION)) {
				this.state = ScanDescriptionLoaderStates.ROOT;
			}
			break;

		case SCAN_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SCAN)) {
				this.state = ScanDescriptionLoaderStates.ROOT;

				for (ScanModule sm : this.currentChain.getScanModules()) {
					sm.smLoading = false; // TODO public attribute ?!
				}
			}
			break;
			
		case REPEATCOUNT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_REPEATCOUNT)) {
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			}
			break;

		case SCAN_COMMENT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_COMMENT)) {
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			}
			break;

		case SCAN_SAVEFILENAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SAVEFILENAME)) {
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			}
			break;

		case SCAN_CONFIRMSAVE_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CONFIRMSAVE)) {
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			}
			break;

		case SCAN_AUTONUMBER_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_AUTONUMBER)) {
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			}
			break;

		case SCAN_SAVESCANDESCRIPTION_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SAVESCANDESCRIPTION)) {
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			}
			break;

		case SCAN_SAVEPLUGINCONTROLLER_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SAVEPLUGIN)) {
				this.currentPluginController = null;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			}
			break;

		case CHAIN_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CHAIN)) {
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
				Iterator<ScanModulRelationReminder> it = this.relationReminders
						.iterator();
				while (it.hasNext()) {
					ScanModulRelationReminder reminder = it.next();
					ScanModule scanModul = reminder.getScanModul();

					ScanModule appendedScanModul = this.currentChain
							.getScanModuleById(reminder.getAppended());
					ScanModule nestedScanModul = this.currentChain
							.getScanModuleById(reminder.getNested());

					Connector connector = new Connector();
					if (appendedScanModul != null) {
						connector.setParentScanModule(scanModul);
						connector.setChildScanModule(appendedScanModul);
						scanModul.setAppended(connector);
						appendedScanModul.setParent(connector);
						connector = new Connector();
					}
					if (nestedScanModul != null) {
						connector.setParentScanModule(scanModul);
						connector.setChildScanModule(nestedScanModul);
						scanModul.setNested(connector);
						nestedScanModul.setParent(connector);
					}
				}
				this.scanDescription.add(this.currentChain);
				
				this.marshalPauseConditions();
			}
			break;

		case CHAIN_STARTEVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STARTEVENT)) {
				this.currentChain.addStartEvent(this.currentControlEvent);
				this.createEventPair();
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;
			
		case CHAIN_REDOEVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_REDOEVENT)) {
				this.currentChain.addRedoEvent(this.currentControlEvent);
				this.createEventPair();
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;
			
		case CHAIN_BREAKEVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_BREAKEVENT)) {
				this.currentChain.addBreakEvent(this.currentControlEvent);
				this.createEventPair();
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;
				
		case CHAIN_STOPEVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STOPEVENT)) {
				this.currentChain.addStopEvent(this.currentControlEvent);
				this.createEventPair();
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;

		case CHAIN_PAUSECONDITION_DEVICEID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_DEVICEID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_LOADING;
			}
			break;
			
		case CHAIN_PAUSECONDITION_OPERATOR_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_OPERATOR)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_LOADING;
			}
			break;
			
		case CHAIN_PAUSECONDITION_PAUSELIMIT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PAUSELIMIT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_LOADING;
			}
			break;
			
		case CHAIN_PAUSECONDITION_CONTINUELIMIT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CONTINUELIMIT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITION_LOADING;
			}
			break;
			
		case CHAIN_PAUSECONDITIONS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PAUSECONDITIONS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			}
			break;
			
		case CHAIN_PAUSECONDITION_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PAUSECONDITION)) {
				this.pauseConditionList.add(this.currentPauseConditionAdaptee);
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSECONDITIONS_LOADING;
			}
			break;

		case CHAIN_SCANMODULES_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SCANMODULES)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SCANMODULE)) {
				this.relationReminders.add(this.currentRelationReminder);
				this.currentChain.add(this.currentScanModule);
				this.scanModulChainMap.put(this.currentScanModule,
						this.currentChain);

				if (!this.idToScanModulMap.containsKey(this.currentScanModule
						.getId())) {
					this.idToScanModulMap.put(this.currentScanModule.getId(),
							this.currentScanModule);
				}
				// topological insert only necessary for classic modules
				if (this.currentScanModule.getType().equals(ScanModuleTypes.CLASSIC)) {
					this.currentScanModule.addAll(this.smChannels,
						this.normalizationMap);
				} else {
					for (Channel channel : this.smChannels) {
						this.currentScanModule.add(channel);
					}
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULES_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_CLASSIC_LOADING:
		case CHAIN_SCANMODULE_SAVE_AXIS_POSITIONS_LOADING:
		case CHAIN_SCANMODULE_SAVE_CHANNEL_VALUES_LOADING:
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			break;
		
		case CHAIN_SCANMODULE_NAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PARENT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PARENT)) {
				if (this.currentRelationReminder.getParent() == 0) {
					// TODO
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_APPENDED_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_APPENDED)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_NESTED_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NESTED)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;
	
		case CHAIN_SCANMODULE_VALUECOUNT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VALUECOUNT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_SETTLETIME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SETTLETIME)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_TRIGGERDELAY_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGERDELAY)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_TRIGGERCONFIRMAXIS_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMAXIS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_TRIGGERCONFIRMCHANNEL_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMCHANNEL)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_TRIGGEREVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_TRIGGEREVENT)) {
				this.currentScanModule.addTriggerEvent(this.currentControlEvent);
				this.createEventPair();
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;
			
		case CHAIN_SCANMODULE_REDOEVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_REDOEVENT)) {
				this.currentScanModule.addRedoEvent(this.currentControlEvent);
				this.createEventPair();
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;
			
		case CHAIN_SCANMODULE_BREAKEVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_BREAKEVENT)) {
				this.currentScanModule.addBreakEvent(this.currentControlEvent);
				this.createEventPair();
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;

		case CHAIN_SCANMODULE_PRESCAN_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PRESCAN)) {
				if (this.currentPrescan.getAbstractPrePostscanDevice() != null) {
					this.currentScanModule.add(this.currentPrescan);
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSTSCAN_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_POSTSCAN)) {
				if (this.currentPostscan.getAbstractPrePostscanDevice() != null) {
					this.currentScanModule.add(this.currentPostscan);
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PRESCAN_ID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PRESCAN_VALUE_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSTSCAN_ID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSTSCAN_VALUE_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_VALUE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSTSCAN_RESET_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_RESETORIGINALVALUE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SMAXIS)) {
				
				if (this.currentAxis.getAbstractDevice() != null) {
					this.currentScanModule.add(this.currentAxis);
					TypeValue tv = null;

					if (this.currentAxis.getMotorAxis() != null) {

						if (this.currentAxis.getMotorAxis().getGoto() != null) {
							if (this.currentAxis.getMotorAxis().getGoto()
									.getValue() != null) {
								tv = this.currentAxis.getMotorAxis().getGoto()
										.getValue();
							} else {
								tv = new TypeValue(this.currentAxis
										.getMotorAxis().getGoto().getAccess()
										.getType());
							}
						} else {
							tv = new TypeValue(this.currentAxis.getMotorAxis()
									.getPosition().getAccess().getType());
						}
					}
				}
				switch (this.currentScanModule.getType()) {
				case CLASSIC:
					this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
					break;
				case SAVE_AXIS_POSITIONS:
					this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SAVE_AXIS_POSITIONS_LOADING;
					break;
				default:
					break;
				
				}
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNEL)) {
				if (this.currentChannel.getAbstractDevice() != null) {
					this.smChannels.add(currentChannel);
					/*Vertex<Channel> channelVertex = new VertexImpl<Channel>(
							this.currentChannel);
					this.currentChannelGraph.addVertex(channelVertex);*/ // TODO
					this.currentChannel.setLoadTime(this.channelLoadTime++);
				}
				switch (this.currentScanModule.getType()) {
				case CLASSIC:
					this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
					break;
				case SAVE_CHANNEL_VALUES:
					this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SAVE_CHANNEL_VALUES_LOADING;
					break;
				default:
					break;
				}
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNEL_STANDARD)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_DETECTOR_INTERVAL_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNEL_INTERVAL)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;
		
		case CHAIN_SCANMODULE_DETECTOR_REDOEVENT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_REDOEVENT)) {
				this.currentChannel.addRedoEvent(this.currentControlEvent);
				this.createEventPair();
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_POSITIONING)) {
				// nur wenn Achse des CurrentPositioning OK, wird es geladen!!
				if (this.currentPositioning.getAbstractDevice() != null) {
					this.currentScanModule.add(this.currentPositioning);
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_AXISID_READ:
			if (qName.equals("axisid")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STEPFUNCTION)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_POSITIONMODE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_START_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_START)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_SMMOTOR_STOP_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STOP)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STEPWIDTH)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_STEPFILENAME)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ISMAINAXIS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_POSITIONLIST)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_EXPRESSION_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_RANGE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_SMMOTOR_CONTROLLER_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PLUGIN)) {
				this.currentAxis.setPluginController(this.currentPluginController);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_CHANNELID_READ:
			if (qName.equals("channelid")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_AVERAGECOUNT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_AVERAGECOUNT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_MAX_DEVIATION_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_MAXDEVIATION)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_MINIMUM_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_MINIMUM)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_MAX_ATTEMPTS_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_MAXATTEMPTS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_NORMALIZECHANNEL_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NORMALIZEID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SENDREADYEVENT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_DEFERRED_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_DEFERREDTRIGGER)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_STANDARD_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_DETECTOR_TRIGGERINTERVAL_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNEL_TRIGGERINTERVAL)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_INTERVAL_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_DETECTOR_STOPPEDBY_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SMCHANNEL_STOPPEDBY)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_INTERVAL_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_PLOT_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PLOT)) {
				this.currentScanModule.add(this.currentPlotWindow);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_CLASSIC_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_NAME_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NAME)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_AXIS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_XAXIS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_AXIS_ID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_AXIS_MODE_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_MODE)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_INIT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_INIT)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_YAXIS_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_YAXIS)) {
				if (this.currentYAxis.getDetectorChannel() != null) {
					this.currentPlotWindow.addYAxis(this.currentYAxis);
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;

		case CHAIN_SCANMODULE_XPOS_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_XPOS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_YPOS_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_YPOS)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_AXIS_ID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_AXISID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_CHANNEL_ID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CHANNELID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_NORMALIZE_ID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NORMALIZEID)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_CONTROLLER_LOADING:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PLUGIN)) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;
		}

		switch (this.subState) {
		case EVENT_ID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
			}
			break;

		case EVENT_LIMIT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_LIMIT)) {
				this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
			}
			break;

		case EVENT_INCIDENT_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_INCIDENT)) {
				this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
			}
			break;

		case EVENT_CHAINID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_CHAINID)) {
				this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
			}
			break;

		case EVENT_SCANMODULEID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_SMID)) {
				this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
			}
			break;

		case YAXIS_ID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case YAXIS_MODE_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_MODE)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case YAXIS_MODIFIER_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_MODIFIER)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;
			
		case YAXIS_NORMALIZE_ID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_NORMALIZEID)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case YAXIS_LINESTYLE_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_LINESTYLE)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case YAXIS_MARKSTYLE_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_MARKSTYLE)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case YAXIS_COLOR_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_COLOR)) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case PLUGIN_CONTROLLER_PARAMETER_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_PARAMETER)) {
				this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_LOADING;
			}
			break;

		case MONITOROPTIONS_ID_READ:
			if (qName.equals(Literals.XML_ELEMENT_NAME_ID)) {
				this.subState = ScanDescriptionLoaderSubStates.MONITOROPTIONS_ID_LOADING;
			}
			break;
}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("End Element: " + qName + " --> State: " + 
					this.state + " (Substate: " + this.subState + ")");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endDocument() throws SAXException {
		this.marshalEvents();
		
		if (this.getScanDescription().getMonitorOption().equals(
				MonitorOption.AS_IN_DEVICE_DEFINITION) ||
			this.getScanDescription().getMonitorOption().equals(
				MonitorOption.USED_IN_SCAN)) {
			this.checkMonitors();
		}

		// set the start event or
		// define a default start event for chains without startevent tag
		// add connectors
		for (Chain loopChain : this.chainList) {
			Event startEvent;
			List<ControlEvent> startEvents = loopChain.getStartEvents();
			if (!startEvents.isEmpty()) {
				startEvent = startEvents.get(0).getEvent();
			} else {
				startEvent = this.scanDescription.getDefaultStartEvent();
			}
			if (startEvent == null) {
				LOGGER.fatal("unable to create start event!");
				startEvent = this.scanDescription.getDefaultStartEvent();
			}

			StartEvent se = new StartEvent();
			se.setChain(loopChain);
			se.setEvent(startEvent);
			loopChain.setStartEvent(se);
			
			// determine the scan module with parent 0 if any
			// which is appended to the start event
			for(ScanModulRelationReminder reminder : this.relationReminders) {
				if (reminder.getParent() == 0) {
					Connector connector = new Connector();
					connector.setParentEvent(se);
					connector.setChildScanModule(reminder.getScanModul());
					se.setConnector(connector);
					reminder.getScanModul().setParent(connector);
				}
			}
		}

		// --- Looking for chains with the same id
		Chain[] chains = this.scanDescription.getChains().toArray(new Chain[0]);
		for (int i = 0; i < chains.length; ++i) {
			if (chains[i] == null) {
				continue;
			}
			int id = chains[i].getId();
			for (int j = i + 1; j < chains.length; ++j) {
				if ((chains[j] != null) && chains[j].getId() == id) {
					chains[j] = null;
				}
			}

		}
		// --- End of looking for chains with the same id

		// --- Checking for multiple command to the same device
		Iterator<Chain> chainIterator = this.scanDescription.getChains()
				.iterator();
		while (chainIterator.hasNext()) {
			Chain currentChain = chainIterator.next();
			Iterator<ScanModule> scanModulIterator = currentChain
					.getScanModules().iterator();

			while (scanModulIterator.hasNext()) {
				ScanModule currentScanModul = scanModulIterator.next();

				Prescan[] prescans = currentScanModul.getPrescans();
				for (int i = 0; i < prescans.length; ++i) {
					if (prescans[i] != null) {
						AbstractDevice device = null;
						for (int j = i + 1; j < prescans.length; ++j) {
							if (prescans[j] != null
									&& prescans[i].getAbstractDevice().getID() == prescans[j]
											.getAbstractDevice().getID()) {
								device = prescans[i].getAbstractDevice();
								prescans[j] = null;
							}
						}
					}
				}

				Axis[] axis = currentScanModul.getAxes();
				for (int i = 0; i < axis.length; ++i) {
					if (axis[i] != null) {
						AbstractDevice device = null;
						for (int j = i + 1; j < axis.length; ++j) {
							if (axis[j] != null
									&& axis[i].getAbstractDevice() != null
									&& axis[j].getAbstractDevice() != null
									&& axis[i].getAbstractDevice().getID() == axis[j]
											.getAbstractDevice().getID()) {
								device = axis[i].getAbstractDevice();
								axis[j] = null;
							}
						}
					}
				}

				Channel[] channels = currentScanModul.getChannels();
				for (int i = 0; i < channels.length; ++i) {
					if (channels[i] != null) {
						AbstractDevice device = null;
						for (int j = i + 1; j < channels.length; ++j) {
							if (channels[j] != null
									&& channels[i].getAbstractDevice().getID() == channels[j]
											.getAbstractDevice().getID()) {
								device = channels[i].getAbstractDevice();
								channels[j] = null;
							}
						}
					}
				}

				Postscan[] postscans = currentScanModul.getPostscans();
				for (int i = 0; i < postscans.length; ++i) {
					if (postscans[i] != null) {
						AbstractDevice device = null;
						for (int j = i + 1; j < postscans.length; ++j) {
							if (postscans[j] != null
									&& postscans[i].getAbstractDevice().getID() == postscans[j]
											.getAbstractDevice().getID()) {
								device = postscans[i].getAbstractDevice();
								postscans[j] = null;
							}
						}
					}
				}
			}
		}
		
		LOGGER.debug("End Document");
	}

	/*
	 * For monitor types 
	 *   "as in device definition" and
	 *   "used in scan"
	 * the loaded monitor list (from scml) has to be compared to the monitor
	 * options set in the MonitorDelegate of ScanDescription.
	 * When the attribute is set to one of the mentioned monitor types the 
	 * type is set in the delegate. The delegate then adds the appropriate 
	 * monitors based on the current device definition. Multiple things can 
	 * be inconsistent:
	 *  - as in device definition
	 *    # monitors contained in the SCML were added based on a potentially 
	 *      different device definition (in the past).The device definition 
	 *      could have changed. Additional monitors could be present and/or old
	 *      ones not be there anymore.
	 *  - used in scan
	 *    # analog to custom all monitors added based on "used in scan" are
	 *      invalid if the corresponding device is not present in the current
	 *      device definition.
	 * During parsing the ids are remembered in "optionsDefinedInScan". They 
	 * now have to be compared as mentioned and inconsistencies have to be 
	 * added to the "deviceMessages" list.
	 * @since 1.29
	 * @see Redmine #3735
	 */
	private void checkMonitors() {
		// TODO: could be more elegant with Java 8 Lambdas and Streams
		List<String> newOptionIDs = new ArrayList<>(
				this.getScanDescription().getMonitors().size());
		for (Option o : this.getScanDescription().getMonitors()) {
			newOptionIDs.add(o.getID());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Monitor Option Type is: "
					+ this.getScanDescription().getMonitorOption());
			LOGGER.debug(
					"current monitor list is: ");
			LOGGER.debug(ListUtil.ListAsString(newOptionIDs));
		}
		
		List<String> obsoleteMonitors = new ArrayList<>(optionsDefinedInScan);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("monitor list of saved file is: ");
			LOGGER.debug(ListUtil.ListAsString(obsoleteMonitors));
		}
		
		obsoleteMonitors.removeAll(newOptionIDs);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("monitors present in old but not new list are: ");
			LOGGER.debug(ListUtil.ListAsString(obsoleteMonitors));
		}
		
		for (String s : obsoleteMonitors) {
			String message = "Monitor '" + 
					nameProvider.translatePrePostScanDeviceId(s) + 
					"' has been removed (not present in current device definition).";
			LOGGER.info(message);
			this.deviceMessages
			.add(new ScanDescriptionLoaderDeviceMessage(
					ScanDescriptionLoaderMessageType.MONITOR_OPTION_REMOVED,
					message));
		}
		
		List<String> newMonitors = new ArrayList(newOptionIDs);
		newMonitors.removeAll(optionsDefinedInScan);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("monitors present in new but not old list are: ");
			LOGGER.debug(ListUtil.ListAsString(newMonitors));
		}
		
		for (String s : newMonitors) {
			String message = "Monitor '" + 
					nameProvider.translatePrePostScanDeviceId(s) + 
					"' has been added (present in current device definition).";
			LOGGER.info(message);
			this.deviceMessages
			.add(new ScanDescriptionLoaderDeviceMessage(
					ScanDescriptionLoaderMessageType.MONITOR_OPTION_ADDED,
					message));
		}
	}
	
	/**
	 * This method returns the loaded scan description.
	 * 
	 * @return The loaded scan description.
	 */
	public ScanDescription getScanDescription() {
		return this.scanDescription;
	}

	/**
	 * This method returns the PVs which not found in the messplatz.xml file.
	 * 
	 * @return The loaded scan description.
	 */
	public List<ScanDescriptionLoaderDeviceMessage> getLostDevices() {
		if (this.deviceMessages != null) {
			return this.deviceMessages;
		} else {
			return null;
		}
	}
	
	/*
	 * During Execution it is not possible to create the event object itself
	 * (schedule or detector) due to the eventually not loaded event source, 
	 * i.e. a schedule event in scan module 1 should be triggered by scan 
	 * module 2 but scan module 2 appears after scan module 1 in the XML file 
	 * and thus couldn't be referenced at that time because it is not created 
	 * by now.
	 */
	private void createEventPair() {
		switch (this.currentControlEvent.getEventType()) {
		case DETECTOR:
			this.detectorEventPairs
					.add(new Pair<ControlEvent, DetectorEventAdaptee>(
							this.currentControlEvent,
							this.currentDetectorEventAdaptee));
			this.currentDetectorEventAdaptee = null;
			break;
		case MONITOR:
			break;
		case SCHEDULE:
			this.scheduleEventPairs
					.add(new Pair<ControlEvent, ScheduleEventAdaptee>(
							this.currentControlEvent,
							this.currentScheduleEventAdaptee));
			this.currentScheduleEventAdaptee = null;
			break;
		default:
			break;
		}
	}
	
	/*
	 * 
	 * 
	 * @since 1.19
	 * @see Redmine #1401
	 */
	private void marshalEvents() {
		for (Pair<ControlEvent, ScheduleEventAdaptee> pair : this.scheduleEventPairs) {
			try {
				pair.getKey().setEvent(
						this.scheduleEventAdapter.marshal(pair.getValue()));
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		for (Pair<ControlEvent, DetectorEventAdaptee> pair : this.detectorEventPairs) {
			try {
				pair.getKey().setEvent(
						this.detectorEventAdapter.marshal(pair.getValue()));
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
		this.removeInvalidEvents();
		
		// register events (see Redmine #1401 Comments #16,#17)
		for (Chain chain : this.scanDescription.getChains()) {
			chain.registerEventValidProperties();
			for (ScanModule sm : chain.getScanModules()) {
				sm.registerEventValidProperties();
				for (Channel channel : sm.getChannels()) {
					if (!channel.getChannelMode().equals(ChannelModes.STANDARD)) {
						continue;
					}
					channel.registerEventValidProperties();
				}
			}
		}
	}
	
	/*
	 * Due to the sequence of event loading a control event is created and 
	 * added to the scan (Chain, SM or Channel) before it is connected with 
	 * a schedule or detector event. 
	 * When the schedule and detector events are marshaled it could occur that 
	 * the corresponding detector or scan module is missing. Then no event is 
	 * created and the control event remains "empty". In this case it has to 
	 * be removed.
	 * 
	 * @since 1.19
	 * @see Redmine #1401
	 */
	private void removeInvalidEvents() {
		for (Chain chain : this.scanDescription.getChains()) {
			for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
					chain.getRedoEvents())) {
				if (controlEvent.getEvent() == null) {
					this.addLostDeviceEventEntry(controlEvent,
							"Removing Redo Event of Chain " + chain.getId());
					chain.removeRedoEvent(controlEvent);
				}
			}
			for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
					chain.getBreakEvents())) {
				if (controlEvent.getEvent() == null) {
					this.addLostDeviceEventEntry(controlEvent,
							"Removing Break Event of Chain " + chain.getId());
					chain.removeBreakEvent(controlEvent);
				}
			}
			for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
					chain.getStopEvents())) {
				if (controlEvent.getEvent() == null) {
					this.addLostDeviceEventEntry(controlEvent,
							"Removing Stop Event of Chain " + chain.getId());
					chain.removeStopEvent(controlEvent);
				}
			}
			for (ScanModule sm : chain.getScanModules()) {
				for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
						sm.getRedoEvents())) {
					if (controlEvent.getEvent() == null) {
						this.addLostDeviceEventEntry(
								controlEvent,
								"Removing Redo Event of Scanmodule "
										+ sm.getName() + " of Chain "
										+ chain.getId());
						sm.removeRedoEvent(controlEvent);
					}
				}
				for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
						sm.getBreakEvents())) {
					if (controlEvent.getEvent() == null) {
						this.addLostDeviceEventEntry(
								controlEvent,
								"Removing Break Event of Scanmodule "
										+ sm.getName() + " of Chain "
										+ chain.getId());
						sm.removeBreakEvent(controlEvent);
					}
				}
				for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
						sm.getTriggerEvents())) {
					if (controlEvent.getEvent() == null) {
						this.addLostDeviceEventEntry(
								controlEvent,
								"Removing Trigger Event of Scanmodule "
										+ sm.getName() + " of Chain "
										+ chain.getId());
						sm.removeTriggerEvent(controlEvent);
					}
				}
				for (Channel channel : sm.getChannels()) {
					if (!channel.getChannelMode().equals(ChannelModes.STANDARD)) {
						continue;
					}
					for (ControlEvent controlEvent : new CopyOnWriteArrayList<ControlEvent>(
							channel.getRedoEvents())) {
						if (controlEvent.getEvent() == null) {
							this.addLostDeviceEventEntry(
									controlEvent,
									"Removing Redo Event of Detector "
											+ channel.getDetectorChannel()
													.getName() + " in SM "
											+ sm.getName() + " of Chain "
											+ chain.getId());
							channel.removeRedoEvent(controlEvent);
						}
					}
				}
			}
		}
	}
	
	/*
	 * 
	 * @since 1.19
	 */
	private void addLostDeviceEventEntry(ControlEvent controlEvent,
			String message) {
		switch (controlEvent.getEventType()) {
		case DETECTOR:
			this.deviceMessages
					.add(new ScanDescriptionLoaderDeviceMessage(
							ScanDescriptionLoaderMessageType.DETECTOR_OF_DETECTOREVENT_NOT_FOUND,
							"Channel of Detector Event is missing. " + message));
			break;
		case MONITOR:
			this.deviceMessages.add(new ScanDescriptionLoaderDeviceMessage(
					ScanDescriptionLoaderMessageType.DEVICE_OF_MONITOREVENT_NOT_FOUND, 
					"Device of Monitor Event is missing. " + message));
			break;
		case SCHEDULE:
			this.deviceMessages
					.add(new ScanDescriptionLoaderDeviceMessage(
							ScanDescriptionLoaderMessageType.SCANMODULE_OF_SCHEDULEEVENT_NOT_FOUND,
							"Scanmodule of Schedule Event is missing. "
									+ message));
			break;
		default:
			break;
		}
	}
	
	/*
	 * PauseConditions are saved into a list of java beans (adaptees). Adaptees
	 * have to be converted and added to the chain as "real" PauseCondition 
	 * objects.
	 * 
	 * @since 1.36
	 * @see #5742
	 */
	private void marshalPauseConditions() {
		PauseConditionAdapter adapter = new PauseConditionAdapter(this.measuringStation);
		for (PauseConditionAdaptee adaptee : this.pauseConditionList) {
			PauseCondition pauseCondition;
			if (this.measuringStation.getAbstractDeviceById(adaptee.getDeviceId()) != null) {
				try {
					pauseCondition = adapter.marshal(adaptee);
					this.currentChain.addPauseCondition(pauseCondition);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			} else {
				this.deviceMessages.add(new ScanDescriptionLoaderDeviceMessage(
					ScanDescriptionLoaderMessageType.PAUSECONDITION_DEVICE_NOT_FOUND, 
					this.nameProvider.translateAbstractDeviceId(adaptee.getDeviceId())));
			}
		}
	}
}