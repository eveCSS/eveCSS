package de.ptb.epics.eve.data.scandescription.processors;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.swt.graphics.RGB;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.EventActions;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MonitorEvent;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.data.scandescription.YAxis;

/**
 * This class represents a load handler for SAX that loads a scan description
 * from a XML file.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class ScanDescriptionLoaderHandler extends DefaultHandler {

	// logging
	private static Logger logger = Logger
			.getLogger(ScanDescriptionLoaderHandler.class.getName());

	// The measuring station that contains the devices
	private final IMeasuringStation measuringStation;

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

	// The currently constructed scan module
	private ScanModule currentScanModul;

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

	// The list of all control events
	private List<ControlEvent> controlEventList;

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

	private List<ScanDescriptionLoaderLostDeviceMessage> lostDevices;

	private boolean invalidPlugin;
	
	/**
	 * This constructor creates a new SAX handler to load a scan description.
	 * 
	 * @param measuringStation
	 *            The measuring station description that contains the devices.
	 */
	public ScanDescriptionLoaderHandler(final IMeasuringStation measuringStation) {
		if (measuringStation == null) {
			throw new IllegalArgumentException(
					"The parameter 'measuringStation' must not be null!");
		}
		this.measuringStation = measuringStation;
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
		this.controlEventList = new ArrayList<ControlEvent>();
		this.relationReminders = new ArrayList<ScanModulRelationReminder>();

		this.idToScanModulMap = new HashMap<Integer, ScanModule>();
		this.scanModulChainMap = new HashMap<ScanModule, Chain>();

		this.lostDevices = new ArrayList<ScanDescriptionLoaderLostDeviceMessage>();
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
			if (qName.equals("version")) {
				this.state = ScanDescriptionLoaderStates.VERSION_NEXT;
			} else if (qName.equals("scan")) {
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			} else if (qName.equals("events")) {
				this.state = ScanDescriptionLoaderStates.EVENTS_LOADING;
			}
			break;

		case SCAN_LOADING:
			if (qName.equals("repeatcount")) {
				this.state = ScanDescriptionLoaderStates.REPEATCOUNT_NEXT;
			} else if (qName.equals("chain")) {
				this.currentChain = new Chain(Integer.parseInt(atts
						.getValue("id")));
				this.chainList.add(this.currentChain);
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			}
			
		case CHAIN_LOADING:
			if (qName.equals("comment")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_COMMENT_NEXT;
			} else if (qName.equals("savefilename")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SAVEFILENAME_NEXT;
			} else if (qName.equals("confirmsave")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_CONFIRMSAVE_NEXT;
			} else if (qName.equals("autonumber")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_AUTONUMBER_NEXT;
			} else if (qName.equals("saveplugin")) {
				this.invalidPlugin = false;
				this.state = ScanDescriptionLoaderStates.CHAIN_SAVEPLUGINCONTROLLER_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_LOADING;
				if (this.measuringStation.getPluginByName(
						atts.getValue("name")) == null) {
					this.invalidPlugin = true;
					this.lostDevices.add(new ScanDescriptionLoaderLostDeviceMessage(
							ScanDescriptionLoaderLostDeviceType.PLUGIN_NOT_FOUND,
							"Plugin '" + atts.getValue("name") + "' has been removed!"));
				}
				this.currentPluginController = this.currentChain
						.getSavePluginController();
				String savePluginName = atts.getValue("name");
				PlugIn savePlugin = this.measuringStation
						.getPluginByName(savePluginName);
				if (savePlugin != null) {
					if (savePlugin.getType() == PluginTypes.SAVE) {
						this.currentChain.getSavePluginController().setPlugin(
								savePlugin);
					}
				}
			} else if (qName.equals("savescandescription")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SAVESCANDESCRIPTION_NEXT;
			} else if (qName.equals("startevent")) {
				this.currentControlEvent = new ControlEvent(
						EventTypes.stringToType(atts.getValue("type")));
				if (atts.getValue("type").equals("schedule")) {
					this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
				} else { // Detector and Monitor
					this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_STARTEVENT;
			} else if (qName.equals("pauseevent")) {
				this.currentControlEvent = new PauseEvent(
						EventTypes.stringToType(atts.getValue("type")));
				if (atts.getValue("type").equals("schedule")) {
					this.subState = ScanDescriptionLoaderSubStates.PAUSESCHEDULEEVENT_LOADING;
				} else { // Detector and Monitor
					this.subState = ScanDescriptionLoaderSubStates.PAUSEMONITOREVENT_LOADING;
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_PAUSEEVENT;
			} else if (qName.equals("redoevent")) {
				this.currentControlEvent = new ControlEvent(
						EventTypes.stringToType(atts.getValue("type")));
				if (atts.getValue("type").equals("schedule")) {
					this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
				} else { // Detector and Monitor
					this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_REDOEVENT;
			} else if (qName.equals("breakevent")) {
				this.currentControlEvent = new ControlEvent(
						EventTypes.stringToType(atts.getValue("type")));
				if (atts.getValue("type").equals("schedule")) {
					this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
				} else { // Detector and Monitor
					this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_BREAKEVENT;
			} else if (qName.equals("stopevent")) {
				this.currentControlEvent = new ControlEvent(
						EventTypes.stringToType(atts.getValue("type")));
				if (atts.getValue("type").equals("schedule")) {
					this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
				} else { // Detector and Monitor
					this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_STOPEVENT;
			} else if (qName.equals("scanmodules")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULES_LOADING;
			}
			break;

		case CHAIN_SCANMODULES_LOADING:
			if (qName.equals("scanmodule")) {
				this.currentScanModul = new ScanModule(Integer.parseInt(atts
						.getValue("id")));
				this.currentRelationReminder = new ScanModulRelationReminder(
						this.currentScanModul);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_LOADING:
			if (qName.equals("type")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TYPE_NEXT;
			} else if (qName.equals("name")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_NAME_NEXT;
			} else if (qName.equals("xpos")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_XPOS_NEXT;
			} else if (qName.equals("ypos")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_YPOS_NEXT;
			} else if (qName.equals("parent")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PARENT_NEXT;
			} else if (qName.equals("nested")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_NESTED_NEXT;
			} else if (qName.equals("appended")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_APPENDED_NEXT;
			} else if (qName.equals("valuecount")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_VALUECOUNT_NEXT;
			} else if (qName.equals("settletime")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SETTLETIME_NEXT;
			} else if (qName.equals("triggerdelay")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGERDELAY_NEXT;
			} else if (qName.equals("triggerconfirm")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGERCONFIRM_NEXT;
			} else if (qName.equals("triggerevent")) {
				this.currentControlEvent = new ControlEvent(
						EventTypes.stringToType(atts.getValue("type")));
				if (atts.getValue("type").equals("schedule")) {
					this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
				} else { // Detector and Monitor
					this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGEREVENT;
			} else if (qName.equals("breakevent")) {
				this.currentControlEvent = new ControlEvent(
						EventTypes.stringToType(atts.getValue("type")));
				if (atts.getValue("type").equals("schedule")) {
					this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
				} else { // Detector and Monitor
					this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_BREAKEVENT;
			} else if (qName.equals("redoevent")) {
				this.currentControlEvent = new ControlEvent(
						EventTypes.stringToType(atts.getValue("type")));
				if (atts.getValue("type").equals("schedule")) {
					this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
				} else { // Detector and Monitor
					this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_REDOEVENT;
			} else if (qName.equals("pauseevent")) {
				this.currentControlEvent = new PauseEvent(
						EventTypes.stringToType(atts.getValue("type")));
				if (atts.getValue("type").equals("schedule")) {
					this.subState = ScanDescriptionLoaderSubStates.PAUSESCHEDULEEVENT_LOADING;
				} else { // Detector and Monitor
					this.subState = ScanDescriptionLoaderSubStates.PAUSEMONITOREVENT_LOADING;
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PAUSEEVENT;
			} else if (qName.equals("prescan")) {
				this.currentPrescan = new Prescan();
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_LOADING;
			} else if (qName.equals("postscan")) {
				this.currentPostscan = new Postscan();
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_LOADING;
			} else if (qName.equals("smaxis")) {
				this.currentAxis = new Axis(this.currentScanModul);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			} else if (qName.equals("smchannel")) {
				this.currentChannel = new Channel(this.currentScanModul);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			} else if (qName.equals("positioning")) {
				this.currentPositioning = new Positioning(this.currentScanModul);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_LOADING;
			} else if (qName.equals("plot")) {
				this.currentPlotWindow = new PlotWindow(this.currentScanModul);
				this.currentPlotWindow.setId(Integer.parseInt(atts
						.getValue("id")));
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_LOADING:
			if (qName.equals("axis_id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_AXIS_ID_NEXT;
			} else if (qName.equals("channel_id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_CHANNEL_ID_NEXT;
			} else if (qName.equals("normalize_id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_NORMALIZE_ID_NEXT;
			} else if (qName.equals("plugin")) {
				this.invalidPlugin = false;
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_CONTROLLER_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_LOADING;
				if (this.measuringStation.getPluginByName(
						atts.getValue("name")) == null) {
					// plugin not found
					this.invalidPlugin = true;
					this.lostDevices.add(new ScanDescriptionLoaderLostDeviceMessage(
							ScanDescriptionLoaderLostDeviceType.PLUGIN_NOT_FOUND,
							"Plugin '" + atts.getValue("name")+ "' has been removed!"));
				}
				this.currentPluginController = this.currentPositioning
						.getPluginController();
				this.currentPluginController.setPlugin(this.measuringStation
						.getPluginByName(atts.getValue("name")));
			}

		case CHAIN_SCANMODULE_PRESCAN_LOADING:
			if (qName.equals("id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_ID_NEXT;
			} else if (qName.equals("value")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_VALUE_NEXT;
			}
			break;

		case CHAIN_SCANMODULE_POSTSCAN_LOADING:
			if (qName.equals("id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_ID_NEXT;
			} else if (qName.equals("value")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_VALUE_NEXT;
			} else if (qName.equals("reset_originalvalue")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_RESET_NEXT;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_LOADING:
			if (qName.equals("axisid")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_AXISID_NEXT;
			} else if (qName.equals("stepfunction")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_NEXT;
			} else if (qName.equals("positionmode")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_NEXT;
			} else if (qName.equals("start")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_START_NEXT;
			} else if (qName.equals("stop")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STOP_NEXT;
			} else if (qName.equals("stepwidth")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_NEXT;
			} else if (qName.equals("stepfilename")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_NEXT;
			} else if (qName.equals("positionlist")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_NEXT;
			} else if (qName.equals("ismainaxis")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_NEXT;
			} else if (qName.equals("plugin")) {
				this.invalidPlugin = false;
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_CONTROLLER_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_LOADING;
				this.currentPluginController = new PluginController();
				if (this.measuringStation.getPluginByName(
						atts.getValue("name")) == null) {
					// plugin not found
					this.invalidPlugin = true;
					this.lostDevices.add(new ScanDescriptionLoaderLostDeviceMessage(
							ScanDescriptionLoaderLostDeviceType.PLUGIN_NOT_FOUND,
							"Plugin '" + atts.getValue("name")+ "' has been removed!"));
				}
				this.currentAxis
						.setPositionPluginController(this.currentPluginController);
				this.currentPluginController.setPlugin(this.measuringStation
						.getPluginByName(atts.getValue("name")));
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_LOADING:
			if (qName.equals("channelid")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_CHANNELID_NEXT;
			} else if (qName.equals("averagecount")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_AVERAGECOUNT_NEXT;
			} else if (qName.equals("maxdeviation")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_MAX_DEVIATION_NEXT;
			} else if (qName.equals("minimum")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_MINIMUM_NEXT;
			} else if (qName.equals("maxattempts")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_MAX_ATTEMPTS_NEXT;
			} else if (qName.equals("normalize_id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_NORMALIZECHANNEL_NEXT;
			} else if (qName.equals("repeatonredo")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_REPEATONREDO_NEXT;
			} else if (qName.equals("sendreadyevent")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_NEXT;
			} else if (qName.equals("redoevent")) {
				this.currentControlEvent = new ControlEvent(
						EventTypes.stringToType(atts.getValue("type")));
				if (atts.getValue("type").equals("schedule")) {
					this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
				} else { // Detector and Monitor
					this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_REDOEVENT;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_LOADING:
			if (qName.equals("name")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_NAME_NEXT;
			} else if (qName.equals("xaxis")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_LOADING;
			} else if (qName.equals("init")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_INIT_NEXT;
			} else if (qName.equals("yaxis")) {
				this.currentYAxis = new YAxis();
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_YAXIS_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_AXIS_LOADING:
			if (qName.equals("id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_ID_NEXT;
			} else if (qName.equals("mode")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_MODE_NEXT;
			}
		}

		switch (this.subState) {

		case MONITOREVENT_LOADING:
			if (qName.equals("id")) {
				this.subState = ScanDescriptionLoaderSubStates.EVENT_ID_NEXT;
			} else if (qName.equals("limit")) {
				this.currentControlEvent.getLimit().setComparison(
						ComparisonTypes.stringToType(atts
								.getValue("comparison")));
				this.currentControlEvent.getLimit().setType(
						DataTypes.stringToType(atts.getValue("type")));
				this.subState = ScanDescriptionLoaderSubStates.EVENT_LIMIT_NEXT;
			}
			break;

		case SCHEDULEEVENT_LOADING:
			if (qName.equals("incident")) {
				this.subState = ScanDescriptionLoaderSubStates.EVENT_INCIDENT_NEXT;
			} else if (qName.equals("chainid")) {
				this.subState = ScanDescriptionLoaderSubStates.EVENT_CHAINID_NEXT;
			} else if (qName.equals("smid")) {
				this.subState = ScanDescriptionLoaderSubStates.EVENT_SCANMODULEID_NEXT;
			}
			break;

		case PAUSEMONITOREVENT_LOADING:
			if (qName.equals("id")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSEEVENT_ID_NEXT;
			} else if (qName.equals("limit")) {
				this.currentControlEvent.getLimit().setComparison(
						ComparisonTypes.stringToType(atts
								.getValue("comparison")));
				this.currentControlEvent.getLimit().setType(
						DataTypes.stringToType(atts.getValue("type")));
				this.subState = ScanDescriptionLoaderSubStates.PAUSEEVENT_LIMIT_NEXT;
			} else if (qName.equals("action")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSEMONITOREVENT_ACTION_NEXT;
			}
			break;

		case PAUSESCHEDULEEVENT_LOADING:
			if (qName.equals("incident")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSEEVENT_INCIDENT_NEXT;
			} else if (qName.equals("chainid")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSEEVENT_CHAINID_NEXT;
			} else if (qName.equals("smid")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSEEVENT_SCANMODULEID_NEXT;
			} else if (qName.equals("action")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSESCHEDULEEVENT_ACTION_NEXT;
			}
			break;

		case YAXIS_LOADING:
			if (qName.equals("id")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_ID_NEXT;
			} else if (qName.equals("mode")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_MODE_NEXT;
			} else if (qName.equals("normalize_id")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_NORMALIZE_ID_NEXT;
			} else if (qName.equals("linestyle")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LINESTYLE_NEXT;
			} else if (qName.equals("markstyle")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_MARKSTYLE_NEXT;
			} else if (qName.equals("color")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_COLOR_NEXT;
			}
			break;

		case PLUGIN_CONTROLLER_LOADING:
			if (qName.equals("parameter")) {
				this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_PARAMETER_NEXT;
				this.currentParameterName = atts.getValue("name");
			}
			break;
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

		case CHAIN_COMMENT_NEXT:
			this.currentChain.setComment(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_COMMENT_READ;
			break;

		case CHAIN_SAVEFILENAME_NEXT:
			this.currentChain.setSaveFilename(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SAVEFILENAME_READ;
			break;

		case CHAIN_CONFIRMSAVE_NEXT:
			this.currentChain.setConfirmSave(Boolean.parseBoolean(textBuffer
					.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_CONFIRMSAVE_READ;
			break;

		case CHAIN_AUTONUMBER_NEXT:
			this.currentChain.setAutoNumber(Boolean.parseBoolean(textBuffer
					.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_AUTONUMBER_READ;
			break;

		case CHAIN_SAVESCANDESCRIPTION_NEXT:
			this.currentChain.setSaveScanDescription(Boolean
					.parseBoolean(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SAVESCANDESCRIPTION_READ;
			break;

		case CHAIN_SCANMODULE_TYPE_NEXT:
			this.currentScanModul.setType(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TYPE_READ;
			break;

		case CHAIN_SCANMODULE_NAME_NEXT:
			this.currentScanModul.setName(textBuffer.toString());
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
			this.currentScanModul.setValuecount(Integer.parseInt(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_VALUECOUNT_READ;
			break;
			
		case CHAIN_SCANMODULE_SETTLETIME_NEXT:
			double settletime = Double.NEGATIVE_INFINITY;
			try {
				settletime = Double.parseDouble(textBuffer.toString());
			} catch (final NumberFormatException e) {
				logger.error(e.getMessage(), e);
			}
			this.currentScanModul.setSettletime(settletime);
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SETTLETIME_READ;
			break;

		case CHAIN_SCANMODULE_TRIGGERDELAY_NEXT:
			double triggerdelay = Double.NEGATIVE_INFINITY;
			try {
				triggerdelay = Double.parseDouble(textBuffer.toString());
			} catch (final NumberFormatException e) {
				logger.error(e.getMessage(), e);
			}
			this.currentScanModul.setTriggerdelay(triggerdelay);
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGERDELAY_READ;
			break;

		case CHAIN_SCANMODULE_TRIGGERCONFIRM_NEXT:
			this.currentScanModul.setTriggerconfirm(Boolean
					.parseBoolean(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_TRIGGERCONFIRM_READ;
			break;

		case CHAIN_SCANMODULE_PRESCAN_ID_NEXT:
			try {
				this.currentPrescan
						.setAbstractPrePostscanDevice(this.measuringStation
								.getPrePostscanDeviceById(textBuffer.toString()));
			} catch (IllegalArgumentException e) {
				logger.error(e.getMessage(), e);
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_ID_READ;
			break;

		case CHAIN_SCANMODULE_PRESCAN_VALUE_NEXT:
			this.currentPrescan.setValue(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_VALUE_READ;
			break;

		case CHAIN_SCANMODULE_POSTSCAN_ID_NEXT:
			try {
				this.currentPostscan
						.setAbstractPrePostscanDevice(this.measuringStation
								.getPrePostscanDeviceById(textBuffer.toString()));
			} catch (IllegalArgumentException e) {
				logger.error(e.getMessage(), e);
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
				this.lostDevices.add(new ScanDescriptionLoaderLostDeviceMessage(
						ScanDescriptionLoaderLostDeviceType.MOTOR_AXIS_ID_NOT_FOUND,
						"Axis '" + textBuffer.toString() + "' has been removed."));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_AXISID_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_NEXT:
			this.currentAxis.setStepfunction(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_NEXT:
			this.currentAxis.setPositionMode(PositionMode
					.stringToType(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_START_NEXT:
			this.currentAxis.setStart(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_START_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STOP_NEXT:
			this.currentAxis.setStop(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STOP_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_NEXT:
			this.currentAxis.setStepwidth(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_NEXT:
			this.currentAxis.setPositionfile(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_NEXT:
			this.currentAxis.setPositionlist(textBuffer.toString());
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_READ;
			break;

		case CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_NEXT:
			this.currentAxis.setMainAxis(Boolean.parseBoolean(textBuffer
					.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_CHANNELID_NEXT:
			if (this.measuringStation.getDetectorChannelById(textBuffer
					.toString()) != null) {
				this.currentChannel.setDetectorChannel(this.measuringStation
						.getDetectorChannelById(textBuffer.toString()));
			} else {
				this.lostDevices.add(new ScanDescriptionLoaderLostDeviceMessage(
						ScanDescriptionLoaderLostDeviceType.DETECTOR_CHANNEL_ID_NOT_FOUND,
						"Channel '" + textBuffer.toString() + "' has been removed!"));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_CHANNELID_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_AVERAGECOUNT_NEXT:
			int averageCount = 1;
			try {
				averageCount = Integer.parseInt(textBuffer.toString());
			} catch (NumberFormatException e) {
				logger.error(e.getMessage(), e);
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
				this.currentChannel.setNormalizeChannel(
						this.measuringStation.getDetectorChannelById(
								textBuffer.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_NORMALIZECHANNEL_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_REPEATONREDO_NEXT:
			if (this.currentChannel.getAbstractDevice() != null) {
				this.currentChannel.setRepeatOnRedo(Boolean
						.parseBoolean(textBuffer.toString()));
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_REPEATONREDO_READ;
			break;

		case CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_NEXT:
			if (this.currentChannel.getAbstractDevice() != null) {
				if (Boolean.parseBoolean(textBuffer.toString())) {
					if (this.currentChannel.getAbstractDevice() != null) {
						Event readyEvent = new Event(this.currentChannel
								.getAbstractDevice().getID(),
								this.currentChannel.getAbstractDevice()
										.getParent().getName(),
								this.currentChannel.getAbstractDevice()
										.getName(), this.currentChain.getId(),
								this.currentScanModul.getId());
						this.currentChannel.setDetectorReadyEvent(readyEvent);
						this.scanDescription.add(readyEvent);
					}
				}
			}
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_READ;
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
			this.currentScanModul.setX(Integer.parseInt(textBuffer.toString()));
			this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_XPOS_READ;
			break;

		case CHAIN_SCANMODULE_YPOS_NEXT:
			this.currentScanModul.setY(Integer.parseInt(textBuffer.toString()));
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

		}

		switch (this.subState) {

		case EVENT_ID_NEXT:
			this.currentControlEvent.setId(textBuffer.toString());
			this.subState = ScanDescriptionLoaderSubStates.EVENT_ID_READ;
			break;

		case EVENT_LIMIT_NEXT:
			this.currentControlEvent.getLimit().setValue(textBuffer.toString());
			this.subState = ScanDescriptionLoaderSubStates.EVENT_LIMIT_READ;
			break;

		case EVENT_INCIDENT_NEXT:
			this.currentControlEvent.getEvent().setScheduleIncident(
					textBuffer.toString());
			this.subState = ScanDescriptionLoaderSubStates.EVENT_INCIDENT_READ;
			break;

		case EVENT_CHAINID_NEXT:
			this.currentControlEvent.getEvent().setChainId(
					Integer.parseInt(textBuffer.toString()));
			this.subState = ScanDescriptionLoaderSubStates.EVENT_CHAINID_READ;
			break;

		case EVENT_SCANMODULEID_NEXT:
			this.currentControlEvent.getEvent().setScanModuleId(
					Integer.parseInt(textBuffer.toString()));
			this.subState = ScanDescriptionLoaderSubStates.EVENT_SCANMODULEID_READ;
			break;

		case PAUSEEVENT_ID_NEXT:
			this.currentControlEvent.setId(textBuffer.toString());
			this.subState = ScanDescriptionLoaderSubStates.PAUSEEVENT_ID_READ;
			break;

		case PAUSEEVENT_LIMIT_NEXT:
			((PauseEvent) this.currentControlEvent).getLimit().setValue(
					textBuffer.toString());
			this.subState = ScanDescriptionLoaderSubStates.PAUSEEVENT_LIMIT_READ;
			break;

		case PAUSEEVENT_INCIDENT_NEXT:
			this.currentControlEvent.getEvent().setScheduleIncident(
					textBuffer.toString());
			this.subState = ScanDescriptionLoaderSubStates.PAUSEEVENT_INCIDENT_READ;
			break;

		case PAUSEEVENT_CHAINID_NEXT:
			this.currentControlEvent.getEvent().setChainId(
					Integer.parseInt(textBuffer.toString()));
			this.subState = ScanDescriptionLoaderSubStates.PAUSEEVENT_CHAINID_READ;
			break;

		case PAUSEEVENT_SCANMODULEID_NEXT:
			this.currentControlEvent.getEvent().setScanModuleId(
					Integer.parseInt(textBuffer.toString()));
			this.subState = ScanDescriptionLoaderSubStates.PAUSEEVENT_SCANMODULEID_READ;
			break;

		case PAUSEMONITOREVENT_ACTION_NEXT:
			((PauseEvent) this.currentControlEvent).setEventAction(EventActions
					.valueOf(textBuffer.toString()));
			this.subState = ScanDescriptionLoaderSubStates.PAUSEMONITOREVENT_ACTION_READ;
			break;

		case PAUSESCHEDULEEVENT_ACTION_NEXT:
			((PauseEvent) this.currentControlEvent).setEventAction(EventActions
					.valueOf(textBuffer.toString()));
			this.subState = ScanDescriptionLoaderSubStates.PAUSESCHEDULEEVENT_ACTION_READ;
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
					logger.error(e.getMessage(), e);
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
						logger.error(e.getMessage(), e);
					}
				}
			}

			this.subState = ScanDescriptionLoaderSubStates.YAXIS_MARKSTYLE_READ;
			break;

		case YAXIS_COLOR_NEXT:
			final String colorBuffer = textBuffer.toString();

			String red_hex = colorBuffer.substring(0, 2);
			int red = Integer.parseInt(red_hex, 16);
			String green_hex = colorBuffer.substring(2, 4);
			int green = Integer.parseInt(green_hex, 16);
			String blue_hex = colorBuffer.substring(4);
			int blue = Integer.parseInt(blue_hex, 16);

			if (colorBuffer.length() == 6)
				this.currentYAxis.setColor(new RGB(red, green, blue));
			else
				this.currentYAxis.setColor(new RGB(0, 0, 0));

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
						this.lostDevices
							.add(new ScanDescriptionLoaderLostDeviceMessage(
							ScanDescriptionLoaderLostDeviceType.PLUGIN_LOCATION_CHANGED,
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
		}

		textBuffer = null;

		/* ***************************************************************** */
		/* ***************************************************************** */

		switch (this.state) {
		case VERSION_READ:
			if (qName.equals("version")) {
				this.state = ScanDescriptionLoaderStates.ROOT;
			}
			break;

		case SCAN_LOADING:
			if (qName.equals("scan")) {
				this.state = ScanDescriptionLoaderStates.ROOT;
			}
			
		case REPEATCOUNT_READ:
			if (qName.equals("repeatcount")) {
				this.state = ScanDescriptionLoaderStates.SCAN_LOADING;
			}
			break;

		case CHAIN_LOADING:
			if (qName.equals("chain")) {
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
						connector.setParentScanModul(scanModul);
						connector.setChildScanModul(appendedScanModul);
						scanModul.setAppended(connector);
						appendedScanModul.setParent(connector);
						connector = new Connector();
					}
					if (nestedScanModul != null) {
						connector.setParentScanModul(scanModul);
						connector.setChildScanModul(nestedScanModul);
						scanModul.setNested(connector);
						nestedScanModul.setParent(connector);
					}
				}
				this.scanDescription.add(this.currentChain);
			}
			break;

		case CHAIN_COMMENT_READ:
			if (qName.equals("comment")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			}
			break;

		case CHAIN_SAVEFILENAME_READ:
			if (qName.equals("savefilename")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			}
			break;

		case CHAIN_CONFIRMSAVE_READ:
			if (qName.equals("confirmsave")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			}
			break;

		case CHAIN_AUTONUMBER_READ:
			if (qName.equals("autonumber")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			}
			break;

		case CHAIN_SAVEPLUGINCONTROLLER_LOADING:
			if (qName.equals("saveplugin")) {
				this.currentPluginController = null;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			}
			break;

		case CHAIN_SAVESCANDESCRIPTION_READ:
			if (qName.equals("savescandescription")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			}
			break;

		case CHAIN_STARTEVENT:
			if (qName.equals("startevent")) {
				this.currentChain.addStartEvent(this.currentControlEvent);
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				controlEventList.add(this.currentControlEvent);
			}
			break;

		case CHAIN_PAUSEEVENT:
			if (qName.equals("pauseevent")) {
				boolean idOK = false;

				idOK = eventIdAvailable(this.currentControlEvent);
				if (idOK == true) {
					this.currentChain
							.addPauseEvent((PauseEvent) this.currentControlEvent);
				}

				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				if (idOK == true) {
					controlEventList.add(this.currentControlEvent);
				}
			}
			break;

		case CHAIN_REDOEVENT:
			if (qName.equals("redoevent")) {
				boolean idOK = false;

				idOK = eventIdAvailable(this.currentControlEvent);
				if (idOK == true) {
					this.currentChain.addRedoEvent(this.currentControlEvent);
				}

				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				if (idOK == true) {
					controlEventList.add(this.currentControlEvent);
				}
			}
			break;

		case CHAIN_BREAKEVENT:
			if (qName.equals("breakevent")) {
				boolean idOK = false;

				idOK = eventIdAvailable(this.currentControlEvent);
				if (idOK == true) {
					this.currentChain.addBreakEvent(this.currentControlEvent);
				}

				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				if (idOK == true) {
					controlEventList.add(this.currentControlEvent);
				}
			}
			break;

		case CHAIN_STOPEVENT:
			if (qName.equals("stopevent")) {
				boolean idOK = false;

				idOK = eventIdAvailable(this.currentControlEvent);
				if (idOK == true) {
					this.currentChain.addStopEvent(this.currentControlEvent);
				}

				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				if (idOK == true) {
					controlEventList.add(this.currentControlEvent);
				}
			}
			break;

		case CHAIN_SCANMODULES_LOADING:
			if (qName.equals("scanmodules")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_LOADING:
			if (qName.equals("scanmodule")) {
				this.relationReminders.add(this.currentRelationReminder);
				this.currentChain.add(this.currentScanModul);
				this.scanModulChainMap.put(this.currentScanModul,
						this.currentChain);

				if (this.idToScanModulMap.containsKey(this.currentScanModul
						.getId())) {

				} else {
					this.idToScanModulMap.put(this.currentScanModul.getId(),
							this.currentScanModul);
				}

				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULES_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_TYPE_READ:
			if (qName.equals("type")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_NAME_READ:
			if (qName.equals("name")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PARENT_READ:
			if (qName.equals("parent")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_APPENDED_READ:
			if (qName.equals("appended")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_NESTED_READ:
			if (qName.equals("nested")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_VALUECOUNT_READ:
			if (qName.equals("valuecount")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;
			
		case CHAIN_SCANMODULE_SETTLETIME_READ:
			if (qName.equals("settletime")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_TRIGGERDELAY_READ:
			if (qName.equals("triggerdelay")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_TRIGGERCONFIRM_READ:
			if (qName.equals("triggerconfirm")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_TRIGGEREVENT:
			if (qName.equals("triggerevent")) {
				boolean idOK = false;

				idOK = eventIdAvailable(this.currentControlEvent);
				if (idOK == true) {
					this.currentScanModul
							.addTriggerEvent(this.currentControlEvent);
				}

				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				if (idOK == true) {
					controlEventList.add(this.currentControlEvent);
				}
			}
			break;

		case CHAIN_SCANMODULE_REDOEVENT:
			if (qName.equals("redoevent")) {
				boolean idOK = false;

				idOK = eventIdAvailable(this.currentControlEvent);
				if (idOK == true) {
					this.currentScanModul
							.addRedoEvent(this.currentControlEvent);
				}

				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				if (idOK == true) {
					controlEventList.add(this.currentControlEvent);
				}
			}
			break;

		case CHAIN_SCANMODULE_BREAKEVENT:
			if (qName.equals("breakevent")) {
				boolean idOK = false;

				idOK = eventIdAvailable(this.currentControlEvent);
				if (idOK == true) {
					this.currentScanModul
							.addBreakEvent(this.currentControlEvent);
				}

				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				if (idOK == true) {
					controlEventList.add(this.currentControlEvent);
				}
			}
			break;

		case CHAIN_SCANMODULE_PAUSEEVENT:
			if (qName.equals("pauseevent")) {
				boolean idOK = false;

				// ???? Muß das currentControlEvent erst noch gecastet werden in
				// PauseEvent???

				idOK = eventIdAvailable(this.currentControlEvent);
				if (idOK == true) {
					this.currentScanModul
							.addPauseEvent((PauseEvent) this.currentControlEvent);
				}

				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
				if (idOK == true) {
					controlEventList.add(this.currentControlEvent);
				}
			}
			break;

		case CHAIN_SCANMODULE_PRESCAN_LOADING:
			if (qName.equals("prescan")) {
				if (this.currentPrescan.getAbstractPrePostscanDevice() != null) {
					this.currentScanModul.add(this.currentPrescan);
					if (!this.currentPrescan.getAbstractPrePostscanDevice()
							.isValuePossible(this.currentPrescan.getValue())) {
					}
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSTSCAN_LOADING:
			if (qName.equals("postscan")) {
				if (this.currentPostscan.getAbstractPrePostscanDevice() != null) {
					this.currentScanModul.add(this.currentPostscan);
					if (!this.currentPostscan.getAbstractPrePostscanDevice()
							.isValuePossible(this.currentPostscan.getValue())) {
						if (!this.currentPostscan.isReset()) {

						}
					}
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PRESCAN_ID_READ:
			if (qName.equals("id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PRESCAN_VALUE_READ:
			if (qName.equals("value")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PRESCAN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSTSCAN_ID_READ:
			if (qName.equals("id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSTSCAN_VALUE_READ:
			if (qName.equals("value")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSTSCAN_RESET_READ:
			if (qName.equals("reset_originalvalue")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSTSCAN_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_LOADING:
			if (qName.equals("smaxis")) {
				if (this.currentAxis.getAbstractDevice() != null) {
					this.currentScanModul.add(this.currentAxis);
					TypeValue tv = null;

					if (this.currentAxis.getMotorAxis() != null) {
						boolean errorFree = true;
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

						if (this.currentAxis.getStart() != null
								&& !this.currentAxis.getMotorAxis()
										.isValuePossible(
												this.currentAxis.getStart())) {

						}

						if (this.currentAxis.getStop() != null
								&& !this.currentAxis.getMotorAxis()
										.isValuePossible(
												this.currentAxis.getStop())) {
						}

						if (this.currentAxis.getStepwidth() != null
								&& !DataTypes.isValuePossible(tv.getType(),
										this.currentAxis.getStepwidth())) {

						}

						if (errorFree) {
							if (this.currentAxis.getStepfunctionString()
									.equals("Add")) {
								if (tv.isDiscrete()) {
									try {
										List<String> values = tv
												.getDiscreteValues();

										final int start = values
												.indexOf(this.currentAxis
														.getStart());
										final int stop = values
												.indexOf(this.currentAxis
														.getStop());
										final int stepwidth = Integer
												.parseInt(this.currentAxis
														.getStepwidth());

										if ((start - stop == 0)
												|| (stepwidth == 0)) {
											this.currentAxis.setStepCount(0);
										} else {
											this.currentAxis
													.setStepCount(((stop - start) / stepwidth));
										}
									} catch (final NumberFormatException e) {
										logger.error(e.getMessage(), e);
									}
								} else {
									try {
										double start = 0;
										double stop = 0;
										double stepwidth = 0;

										switch (this.currentAxis.getMotorAxis()
												.getPosition().getType()) {
										case DATETIME:
											int addStart = 0;
											int addStop = 0;
											int addStepwidth = 0;

											int startJahr = 1;
											int stopJahr = 1;

											DateFormat startDate = DateFormat
													.getTimeInstance();
											DateFormat stopDate = DateFormat
													.getTimeInstance();
											DateFormat stepwidthDate = DateFormat
													.getTimeInstance();
											// Herausfinden welches Format die
											// übergebene Zeit hat
											if (this.currentAxis
													.getStart()
													.matches(
															"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}([.]\\d{1,3})?$")) {
												startDate = new SimpleDateFormat(
														"yyyy-MM-dd HH:mm:ss.SSS");
												startJahr = 1;
												// Nachkommazahl bestimmen
												// Stelle des Punktes
												int indexP = this.currentAxis
														.getStart()
														.indexOf('.');
												double nachkomma = Double
														.parseDouble(this.currentAxis
																.getStart()
																.substring(
																		indexP));
												int nachMinus = Integer
														.parseInt(this.currentAxis
																.getStart()
																.substring(
																		indexP + 1));
												addStart = (int) (nachkomma * 1000 - nachMinus);
											} else if (this.currentAxis
													.getStart().matches(
															"\\d+:\\d+:\\d+?$")) {
												startDate = new SimpleDateFormat(
														"HH:mm:ss");
												startJahr = 0;
											} else if (this.currentAxis
													.getStart()
													.matches(
															"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
												startDate = new SimpleDateFormat(
														"HH:mm:ss.SSS");
												startJahr = 0;
												// Nachkommazahl bestimmen
												// Stelle des Punktes
												int indexP = this.currentAxis
														.getStart()
														.indexOf('.');
												double nachkomma = Double
														.parseDouble(this.currentAxis
																.getStart()
																.substring(
																		indexP));
												int nachMinus = Integer
														.parseInt(this.currentAxis
																.getStart()
																.substring(
																		indexP + 1));
												addStart = (int) (nachkomma * 1000 - nachMinus);
											}

											if (this.currentAxis
													.getStop()
													.matches(
															"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}([.]\\d{1,3})?$")) {
												stopDate = new SimpleDateFormat(
														"yyyy-MM-dd HH:mm:ss.SSS");
												stopJahr = 1;
												// Nachkommazahl bestimmen
												// Stelle des Punktes
												int indexP = this.currentAxis
														.getStop().indexOf('.');
												double nachkomma = Double
														.parseDouble(this.currentAxis
																.getStop()
																.substring(
																		indexP));
												int nachMinus = Integer
														.parseInt(this.currentAxis
																.getStop()
																.substring(
																		indexP + 1));
												addStop = (int) (nachkomma * 1000 - nachMinus);
											} else if (this.currentAxis
													.getStop().matches(
															"\\d+:\\d+:\\d+?$")) {
												stopDate = new SimpleDateFormat(
														"HH:mm:ss");
												stopJahr = 0;
											} else if (this.currentAxis
													.getStop()
													.matches(
															"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
												stopDate = new SimpleDateFormat(
														"HH:mm:ss.SSS");
												stopJahr = 0;
												// Nachkommazahl bestimmen
												// Stelle des Punktes
												int indexP = this.currentAxis
														.getStop().indexOf('.');
												double nachkomma = Double
														.parseDouble(this.currentAxis
																.getStop()
																.substring(
																		indexP));
												int nachMinus = Integer
														.parseInt(this.currentAxis
																.getStop()
																.substring(
																		indexP + 1));
												addStop = (int) (nachkomma * 1000 - nachMinus);
											}

											if (this.currentAxis
													.getStepwidth()
													.matches("\\d+:\\d+:\\d+?$")) {
												stepwidthDate = new SimpleDateFormat(
														"HH:mm:ss");
											} else if (this.currentAxis
													.getStepwidth()
													.matches(
															"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
												stepwidthDate = new SimpleDateFormat(
														"HH:mm:ss.SSS");
												// Nachkommazahl bestimmen
												// Stelle des Punktes
												int indexP = this.currentAxis
														.getStepwidth()
														.indexOf('.');
												double nachkomma = Double
														.parseDouble(this.currentAxis
																.getStepwidth()
																.substring(
																		indexP));
												int nachMinus = Integer
														.parseInt(this.currentAxis
																.getStepwidth()
																.substring(
																		indexP + 1));
												addStepwidth = (int) (nachkomma * 1000 - nachMinus);
											}

											// Wenn startJahr != stopJahr dann
											// wird nicht weitergemacht, weil
											// die Formate nicht gleich sind
											if (startJahr != stopJahr)
												return;

											try {
												startDate.setLenient(false);
												startDate
														.parse(this.currentAxis
																.getStart());
												Calendar startTime = startDate
														.getCalendar();
												start = startTime
														.getTimeInMillis()
														+ addStart + 3600000;

												stopDate.setLenient(false);
												stopDate.parse(this.currentAxis
														.getStop());
												Calendar stopTime = stopDate
														.getCalendar();
												stop = stopTime
														.getTimeInMillis()
														+ addStop + 3600000;

												stepwidthDate.setLenient(false);
												stepwidthDate
														.parse(this.currentAxis
																.getStepwidth());
												Calendar stepwidthTime = stepwidthDate
														.getCalendar();
												stepwidth = stepwidthTime
														.getTimeInMillis()
														+ addStepwidth
														+ 3600000;

												// Wenn Start > Stop beenden
												if (start > stop)
													return;

												if ((start - stop == 0)
														|| (stepwidth == 0)) {
													this.currentAxis
															.setStepCount(0);
												} else {
													this.currentAxis
															.setStepCount(((stop - start) / stepwidth));
												}
											} catch (final ParseException ef) {
												logger.error(ef.getMessage(),
														ef);
											}

											break;
										default:
											start = Double
													.parseDouble(this.currentAxis
															.getStart());
											stop = Double
													.parseDouble(this.currentAxis
															.getStop());
											stepwidth = Double
													.parseDouble(this.currentAxis
															.getStepwidth());
											if ((start - stop == 0)
													|| (stepwidth == 0)) {
												this.currentAxis
														.setStepCount(0);
											} else {
												this.currentAxis
														.setStepCount(((stop - start) / stepwidth));
											}
											break;
										}
									} catch (final NumberFormatException e) {
										logger.error(e.getMessage(), e);
									}
								}
							}
						}
					}
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_LOADING:
			if (qName.equals("smchannel")) {
				if (this.currentChannel.getAbstractDevice() != null) {
					this.currentScanModul.add(this.currentChannel);
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_REDOEVENT:
			if (qName.equals("redoevent")) {

				boolean idOK = false;

				idOK = eventIdAvailable(this.currentControlEvent);
				if (idOK == true) {
					this.currentChannel.addRedoEvent(this.currentControlEvent);
				}

				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;

				if (idOK == true) {
					// RedoEvent wird nur hinzugefügt, wenn DetectorChannel auch
					// vorhanden
					controlEventList.add(this.currentControlEvent);
				}
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_LOADING:
			if (qName.equals("positioning")) {
				// nur wenn Achse des CurrentPositioning OK, wird es geladen!!
				if (this.currentPositioning.getAbstractDevice() != null) {
					this.currentScanModul.add(this.currentPositioning);
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_AXISID_READ:
			if (qName.equals("axisid")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STEPFUNCTION_READ:
			if (qName.equals("stepfunction")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_POSITIONMODE_READ:
			if (qName.equals("positionmode")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_START_READ:
			if (qName.equals("start")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;
		case CHAIN_SCANMODULE_SMMOTOR_STOP_READ:
			if (qName.equals("stop")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;
		case CHAIN_SCANMODULE_SMMOTOR_STEPWIDTH_READ:
			if (qName.equals("stepwidth")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_STEPFILENAME_READ:
			if (qName.equals("stepfilename")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_ISMAINAXIS_READ:
			if (qName.equals("ismainaxis")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_POSITIONLIST_READ:
			if (qName.equals("positionlist")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_SMMOTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_SMMOTOR_CONTROLLER_LOADING:
			if (qName.equals("plugin")) {
				this.currentAxis
						.setPositionPluginController(this.currentPluginController);
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
			if (qName.equals("averagecount")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_MAX_DEVIATION_READ:
			if (qName.equals("maxdeviation")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_MINIMUM_READ:
			if (qName.equals("minimum")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_MAX_ATTEMPTS_READ:
			if (qName.equals("maxattempts")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_NORMALIZECHANNEL_READ:
			if (qName.equals("normalize_id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_REPEATONREDO_READ:
			if (qName.equals("repeatonredo")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_DETECTOR_DETECTORREADYEVENT_READ:
			if (qName.equals("sendreadyevent")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_DETECTOR_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_LOADING:
			if (qName.equals("plot")) {
				this.currentScanModul.add(this.currentPlotWindow);
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_NAME_READ:
			if (qName.equals("name")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_AXIS_LOADING:
			if (qName.equals("xaxis")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_AXIS_ID_READ:
			if (qName.equals("id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_AXIS_MODE_READ:
			if (qName.equals("mode")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_AXIS_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_INIT_READ:
			if (qName.equals("init")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_PLOT_YAXIS_LOADING:
			if (qName.equals("yaxis")) {
				if (this.currentYAxis.getDetectorChannel() != null) {
					this.currentPlotWindow.addYAxis(this.currentYAxis);
				}
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_PLOT_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;

		case CHAIN_SCANMODULE_XPOS_READ:
			if (qName.equals("xpos")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_YPOS_READ:
			if (qName.equals("ypos")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_AXIS_ID_READ:
			if (qName.equals("axis_id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_CHANNEL_ID_READ:
			if (qName.equals("channel_id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_NORMALIZE_ID_READ:
			if (qName.equals("normalize_id")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_LOADING;
			}
			break;

		case CHAIN_SCANMODULE_POSITIONING_CONTROLLER_LOADING:
			if (qName.equals("plugin")) {
				this.state = ScanDescriptionLoaderStates.CHAIN_SCANMODULE_POSITIONING_LOADING;
				this.subState = ScanDescriptionLoaderSubStates.NONE;
			}
			break;
		}

		switch (this.subState) {
		case PAUSEEVENT_ID_READ:
			if (qName.equals("id")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSEMONITOREVENT_LOADING;
			}
			break;

		case PAUSEEVENT_LIMIT_READ:
			if (qName.equals("limit")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSEMONITOREVENT_LOADING;
			}
			break;

		case PAUSESCHEDULEEVENT_ACTION_READ:
			if (qName.equals("action")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSESCHEDULEEVENT_LOADING;
			}
			break;

		case PAUSEMONITOREVENT_ACTION_READ:
			if (qName.equals("action")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSEMONITOREVENT_LOADING;
			}
			break;

		case PAUSEEVENT_INCIDENT_READ:
			if (qName.equals("incident")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSESCHEDULEEVENT_LOADING;
			}
			break;

		case PAUSEEVENT_CHAINID_READ:
			if (qName.equals("chainid")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSESCHEDULEEVENT_LOADING;
			}
			break;

		case PAUSEEVENT_SCANMODULEID_READ:
			if (qName.equals("smid")) {
				this.subState = ScanDescriptionLoaderSubStates.PAUSESCHEDULEEVENT_LOADING;
			}
			break;

		case EVENT_ID_READ:
			if (qName.equals("id")) {
				this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
			}
			break;

		case EVENT_LIMIT_READ:
			if (qName.equals("limit")) {
				this.subState = ScanDescriptionLoaderSubStates.MONITOREVENT_LOADING;
			}
			break;

		case EVENT_INCIDENT_READ:
			if (qName.equals("incident")) {
				this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
			}
			break;

		case EVENT_CHAINID_READ:
			if (qName.equals("chainid")) {
				this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
			}
			break;

		case EVENT_SCANMODULEID_READ:
			if (qName.equals("smid")) {
				this.subState = ScanDescriptionLoaderSubStates.SCHEDULEEVENT_LOADING;
			}
			break;

		case YAXIS_ID_READ:
			if (qName.equals("id")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case YAXIS_MODE_READ:
			if (qName.equals("mode")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case YAXIS_NORMALIZE_ID_READ:
			if (qName.equals("normalize_id")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case YAXIS_LINESTYLE_READ:
			if (qName.equals("linestyle")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case YAXIS_MARKSTYLE_READ:
			if (qName.equals("markstyle")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case YAXIS_COLOR_READ:
			if (qName.equals("color")) {
				this.subState = ScanDescriptionLoaderSubStates.YAXIS_LOADING;
			}
			break;

		case PLUGIN_CONTROLLER_PARAMETER_READ:
			if (qName.equals("parameter")) {
				this.subState = ScanDescriptionLoaderSubStates.PLUGIN_CONTROLLER_LOADING;
			}
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endDocument() throws SAXException {
		// register schedule controlEvents with scanDescription and remove
		// double events
		for (ControlEvent controlEvent : this.controlEventList) {
			if (controlEvent.getEventType() == EventTypes.SCHEDULE) {
				this.scanDescription.add(controlEvent.getEvent());
				String eventId = controlEvent.getEvent().getID();
				controlEvent.setId(eventId);
				controlEvent.setEvent(this.scanDescription
						.getEventById(eventId));
			} else {
				controlEvent.setEvent(this.measuringStation
						.getEventById(controlEvent.getId()));
				if (controlEvent.getEvent() == null) {
					controlEvent.setEvent(this.scanDescription
							.getEventById(controlEvent.getId()));
				}
				if (controlEvent.getEvent() == null) {
					logger.fatal("can't find event for id!");
				}
			}
		}

		// set the start event or
		// define a default start event for chains without startevent tag
		// add connectors
		for (Chain loopChain : this.chainList) {
			Event startEvent;
			Iterator<ControlEvent> chainStartEventIt = loopChain
					.getStartEventsIterator();
			if (chainStartEventIt.hasNext()) {
				startEvent = chainStartEventIt.next().getEvent();
			} else {
				startEvent = this.scanDescription.getDefaultStartEvent();
			}
			if (startEvent == null) {
				logger.fatal("unable to create start event!");
				startEvent = this.scanDescription.getDefaultStartEvent();
			}

			StartEvent se = new StartEvent();
			se.setChain(loopChain);
			se.setEvent(startEvent);
			loopChain.setStartEvent(se);
			for (ScanModule loopScanModule : loopChain.getScanModules()) {
				if (loopScanModule.getParent() == null) {
					Connector connector = new Connector();
					connector.setParentEvent(se);
					connector.setChildScanModul(loopScanModule);
					se.setConnector(connector);
					loopScanModule.setParent(connector);
					break;
				}
			}
		}

		// TODO Tidy up
		// TODO events for the dectector list is not handled
		for (ControlEvent controlEvent : this.controlEventList) {
			if (controlEvent.getEventType() == EventTypes.SCHEDULE)
				break;
			if (controlEvent.getEventType() == EventTypes.DETECTOR)
				break;
			if (controlEvent.getEvent() == null)
				break;

			MonitorEvent monEvent = controlEvent.getEvent().getMonitor();

			if (!monEvent.getDataType().isValuePossible(
					controlEvent.getLimit().getValue())
					|| !DataTypes.isComparisonTypePossible(monEvent
							.getDataType().getType(), controlEvent.getLimit()
							.getComparison())) {
				Iterator<Chain> chainIterator = this.scanDescription
						.getChains().iterator();
				while (chainIterator.hasNext()) {
					Chain chain = chainIterator.next();
					if (chain.isAEventOfTheChain(controlEvent)) {
						break;
					}
					Iterator<ScanModule> scanModulIterator = chain
							.getScanModules().iterator();
					while (scanModulIterator.hasNext()) {
						ScanModule scanModul = scanModulIterator.next();
						if (scanModul.isAEventOfTheScanModul(controlEvent)) {
							break;
						}
					}
				}

				if (monEvent == null) {
				} else if (!DataTypes.isComparisonTypePossible(monEvent
						.getDataType().getType(), controlEvent.getLimit()
						.getComparison())) {
				} else {
					// TODO check location for detector events
				}
			}
		}

		// --- Looking for chains with the same id
		Chain[] chains = this.scanDescription.getChains().toArray(new Chain[0]);
		for (int i = 0; i < chains.length; ++i) {
			if (chains[i] == null)
				continue;
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
						if (device != null) {
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
						if (device != null) {
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
						if (device != null) {
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
						if (device != null) {
						}
					}
				}
			}
		}
		// --- Checking for multiple command to the same device
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
	public List<ScanDescriptionLoaderLostDeviceMessage> getLostDevices() {
		if (this.lostDevices != null)
			return this.lostDevices;
		else
			return null;
	}

	/**
	 * This method returns true if the eventId is available or false if not.
	 * 
	 * @param ControlEvent
	 *            The current Event which will be checked.
	 * @return true or false.
	 */
	public boolean eventIdAvailable(ControlEvent currentEvent) {

		boolean idOK = false;
		String checkNeu = null;
		int indexNeu;
		String checkString = null;

		switch (this.currentControlEvent.getEventType()) {
		case SCHEDULE:
			idOK = true;
			break;
		case DETECTOR:
			checkNeu = this.currentControlEvent.getId().replace("D-", "");
			indexNeu = checkNeu.indexOf('-', 1);
			indexNeu = checkNeu.indexOf('-', indexNeu + 1);
			checkString = checkNeu.substring(indexNeu + 1);
			if (this.measuringStation.getDetectorChannelById(checkString) != null) {
				// DetektorChannel ist vorhanden, Event hinzufügen
				idOK = true;
			}
			break;
		case MONITOR:
			checkNeu = this.currentControlEvent.getId();
			indexNeu = checkNeu.indexOf('.', 1);
			if (indexNeu > 0) {
				checkString = checkNeu.substring(0, indexNeu);
			} else {
				checkString = checkNeu;
			}

			if (this.measuringStation.getMotorAxisById(checkString) != null) {
				// MotorAxis ist vorhanden, Event hinzufügen
				idOK = true;
			} else if (this.measuringStation
					.getDetectorChannelById(checkString) != null) {
				// DetektorChannel ist vorhanden, Event hinzufügen
				idOK = true;
			} else {
				for (Device device : this.measuringStation.getDevices()) {
					if (device.getID().equals(checkString)) {
						// Device ist vorhanden, Event hinzufügen
						idOK = true;
					}
				}
			}
			break;
		}
		return idOK;
	}
}