package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.measuringstation.Access;

/**
 * This class represents an <code>Event</code> that is defined inside of a 
 * measuring station description. It also provides a mechanism to get connected 
 * with a scan module and represents a start event.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 */

public class Event {

	public enum ScheduleIncident {START, END};
	/**
	 * The name of the Event. Currently this is generated for 
	 * SCHEDULE and DETECTOR
	 */
	private String pname;		// parentname
	private String name;		
		
	/**
	 * Id of the Detector, if this is a detector event
	 */
	private String detectorId;
	/**
	 * The type of the event.
	 */
	private EventTypes type;
	/**
	 * chainId if event is of type schedule
	 */
	private int chainId;
	/**
	 * scanModuleId if event is of type schedule
	 */
	private int scanModuleId;
	/**
	 * Event trigger if event is of type schedule
	 */
	private ScheduleIncident incident;

	/**
	 * The monitor Event of this event.
	 */
	private MonitorEvent monitor;

	/**
	 * Constructs an <code>Event</code> with a <code>String</code> data type, 
	 * an empty monitor process variable and empty name. By default the Event 
	 * is of type schedule. 
	 *
	 */
	public Event() {
		this.type = EventTypes.SCHEDULE;
		chainId = 0;
		scanModuleId = 0;
		monitor = null;
		incident = ScheduleIncident.END;
	}
	
	/**
	 * Constructs an <code>Event</code>.
	 * 
	 * @param type (schedule/monitor)
	 */
	public Event(final EventTypes type) {
		this.type = type;
		if (this.type == EventTypes.MONITOR) {
			monitor = new MonitorEvent();
		}
		else {
			chainId = 0;
			scanModuleId = 0;
			monitor = null;
			incident = ScheduleIncident.END;
			if (this.type == EventTypes.SCHEDULE) name = getID();
		}
	}

	/**
	 * Constructor for detector event
	 * 
	 * @param detectorid
	 * @param parentname
	 * @param detectorname
	 * @param chainid
	 * @param smid
	 */
	public Event(String detectorid, String parentname, String detectorname, 
				  int chainid, int smid) {
		this.type = EventTypes.DETECTOR;
		pname = parentname;
		name = detectorname;
		detectorId = detectorid;
		chainId = chainid;
		scanModuleId = smid;
	}
	
	/**
	 * Constructor for schedule event
	 * 
	 * @param chainid
	 * @param smid
	 * @param incidenet
	 * 
	 */
	public Event(int chainid, int smid, ScheduleIncident incident) {
		this.type = EventTypes.SCHEDULE;
		chainId = chainid;
		scanModuleId = smid;
		monitor = null;
		this.incident = incident;
	}
	/**
	 * Constructs an <code>Event</code> of type Monitor with a given value 
	 * (dataType) and a given access. 
	 * 
	 * @param access specifies the access of the event. Must not be null and 
	 * 				  the the method type of the access must be 
	 * 				  <code>Monitor</code>
	 * @param dataType specifies the data type of this event. Must not be null.
	 */
	public Event(final Access access, final TypeValue dataType) {
		this.type = EventTypes.MONITOR;
		monitor = new MonitorEvent( access, dataType, "", "");
	}
	
	/**
	 * This constructor constructs an <code>Event</code> of type 
	 * <code>Monitor</code> with given data type (value), process variable,
	 * name, id.
	 * 
	 * @param dataType specifies the data type of this event. Must not be null.
	 * @param access specifies the <code>Access</code> of the event. Must not 
	 * 				  be null and the the method type of the <code>Access</code> 
	 * 				  must be <code>Monitor</code>
	 * @param name a <code>String</code> containing the name of the 
	 * 				<code>Event</code>.	Must not be null.
	 * @param id a <code>String</code> containing the id of the 
	 * 		   <code>Event</code>. Must not be null.
	 */
	public Event(final Access access, final TypeValue dataType, 
				  final String name, final String id) {
		this.type = EventTypes.MONITOR;
		monitor = new MonitorEvent(access, dataType, name, id);
	}
	
	/**
	 * Returns the name of the <code>Event</code>.
	 * 
	 * @return a <code>String</code> containing the name of the 
	 * 			<code>event</code>. Never returns null.
	 */
	public String getName() {
		if (this.type == EventTypes.MONITOR)
			return getNameID();
		else if (this.type == EventTypes.DETECTOR)
			return getNameID();
		else if (this.type == EventTypes.SCHEDULE)
			return getNameID();
		else
			return this.name;
	}

	/**
	 * Returns the full id of the <code>Event</code>. If <code>Event</code> is a 
	 * schedule event, it is generated from chainId, scanModuleId and incident 
	 * S-<chainId>-<scanModuleId>-[S,E]
	 * If event is a detector event, it is generated from 
	 * chainId, scanModuleId and detector_id 
	 * D-<chainId>-<scanModuleId>-<detectorid>
	 * 
	 * @return the full identifier of the <code>Event</code>
	 */
	public String getNameID() {
		String returnString = null;
		if (this.type == EventTypes.MONITOR) {
			if (monitor != null) {
				returnString = monitor.getName();
				if (returnString != null) {
					returnString += " ( " + monitor.getID() + " )";
				}
				else
					returnString = "( " + monitor.getID() + " )";
			}
		}
		else if (this.type == EventTypes.SCHEDULE) {
			String incidentTag;
			if (incident == ScheduleIncident.END) 
				incidentTag = "E";
			else
				incidentTag = "S";
			return "Start ( S-" + String.valueOf(chainId) + "-" + 
					String.valueOf(scanModuleId) + "-" + incidentTag + " )";
		}
		else if (this.type == EventTypes.DETECTOR) {
			if (pname != null) {
				if (!pname.equals("")) {
					returnString = pname;
				}
			}
			if (name != null) {
				if (!name.equals("")) {
					if (returnString != null) {
						returnString += " ";
						returnString += name;
					}
					else
						returnString = name;
				}
			}
			if (returnString != null) {
				returnString += " ( D-";
			}
			else
				returnString = "( D-";
			returnString += String.valueOf(chainId) + "-" + 
						String.valueOf(scanModuleId) + "-" + detectorId + " )";
		}
		return returnString;
	}
	
	/**
	 * Returns the id of the <code>Event</code>. If <code>Event</code> is a 
	 * schedule event, it is generated from chainId, scanModuleId and incident 
	 * S-<chainId>-<scanModuleId>-[S,E]
	 * If <code>Event</code> is a detector event, it is generated from chainId, 
	 * scanModuleId and detector_id D-<chainId>-<scanModuleId>-<detectorid>
	 * 
	 * @return id
	 */
	public String getID() {
		String returnString = null;
		if (this.type == EventTypes.MONITOR){
			if (monitor != null) 
				return monitor.getID();
		}
		else if (this.type == EventTypes.SCHEDULE) {
			String incidentTag;
			if (incident == ScheduleIncident.END) 
				incidentTag = "E";
			else
				incidentTag = "S";
			return "S-" + String.valueOf(chainId) + "-" + 
					String.valueOf(scanModuleId) + "-" + incidentTag;
		}
		else if (this.type == EventTypes.DETECTOR) {
				return "D-" + String.valueOf(chainId) + "-" + 
						String.valueOf(scanModuleId) + "-" + detectorId;
		}
		return returnString;
	}
	
	/**
	 * Returns the type of the <code>Event</code>.
	 * 
	 * @return the event type
	 */
	public EventTypes getType() {
		return this.type;
	}
	
	
	/**
	 * Sets the id of this event. The id is a unique identifier of this event 
	 * inside a measuring station. Do not set the id of Schedule Events. It is 
	 * generated from chainId, scanModuleId and Incident
	 * 
	 * @param id A String object containing the name of the event.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setId(final String id) {
		if(id == null) {
			throw new IllegalArgumentException(
					"The parameter 'id' must not be null!");
		}
		if (this.type == EventTypes.MONITOR) monitor.setId(id);
	}

	/**
	 * Sets the name of this event.
	 * 
	 * @param name A String object containing the id of the event.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setName(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		if (this.type == EventTypes.MONITOR)
			monitor.setName(name);
		else
			this.name = name;
	}

	/**
	 * Sets the type of the event.
	 * 
	 * @param type a value of the Enum EventTypes. Must not be null.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setType(final EventTypes type) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameter 'type' must not be null!");
		}
		this.type = type;
		if (this.type == EventTypes.MONITOR){
			if (monitor == null) monitor = new MonitorEvent();
		}
		else {
			chainId = 0;
			scanModuleId = 0;
			monitor = null;
			incident = ScheduleIncident.END;
		}
	}
	
	/**
	 * Sets the Access of this Event.
	 * 
	 * @param access A Access object. Must not be null.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 * @throws IllegalArgumentException ?
	 */
	public void setAccess(final Access access) {
		// TODO Explain second clause of if in throws declaration
		if((access == null) || this.type != EventTypes.MONITOR) {
			throw new IllegalArgumentException(
					"unable to set Null Access or type is schedule");
		}
		if (monitor != null)
			monitor.setAccess(access);
	}

	/**
	 * Returns the monitor event of this event.
	 * 
	 * @return the monitor event of this event or null if the event isn't 
	 * 			a monitor event.
	 */
	public MonitorEvent getMonitor() {
		if (this.type == EventTypes.MONITOR)
			return monitor;
		else
			return null;
	}
	
	/**
	 * Sets the chain id.
	 * 
	 * @param id the new chain id.
	 */
	public void setChainId(final int id) {
		this.chainId = id;
	}
	
	/**
	 * Returns the chain id.
	 * 
	 * @return the chain id.
	 */
	public int getChainId() {
		return this.chainId; 
	}

	/**
	 * Sets the scan module id.
	 * 
	 * @param id the new scan module id
	 */
	public void setScanModuleId(final int id) {
		this.scanModuleId = id;
	}
	
	/**
	 * Returns the scan module id.
	 * 
	 * @return The scan module id.
	 */
	public int getScanModuleId() {
		return this.scanModuleId;
	}

	// TODO: Bitte dokumentieren.
	public void setScheduleIncident( final String incidentString ){
		if( incidentString.equals( "Start" ) ) {
			incident = ScheduleIncident.START;
		} else {
			incident = ScheduleIncident.END;
		}
	}
	
	// TODO: Bitte dokumentieren.	
	public ScheduleIncident getScheduleIncident() {
		return incident;
	}

	/**
	 * @return a hash
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + chainId;
		result = prime * result
				+ ((detectorId == null) ? 0 : detectorId.hashCode());
		result = prime * result
				+ ((incident == null) ? 0 : incident.hashCode());
		result = prime * result + ((monitor == null) ? 0 : monitor.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pname == null) ? 0 : pname.hashCode());
		result = prime * result + scanModuleId;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * Checks whether the argument and calling object are equal.
	 * 
	 * @param obj the <code>Object</code> that should be checked
	 * @return <code>true</code> if objects are equal,
	 * 			<code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Event other = (Event) obj;
		if (chainId != other.chainId) {
			return false;
		}
		if (detectorId == null) {
			if (other.detectorId != null) {
				return false;
			}
		} else if (!detectorId.equals(other.detectorId)) {
			return false;
		}
		if (incident == null) {
			if (other.incident != null) {
				return false;
			}
		} else if (!incident.equals(other.incident)) {
			return false;
		}
		if (monitor == null) {
			if (other.monitor != null) {
				return false;
			}
		} else if (!monitor.equals(other.monitor)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (pname == null) {
			if (other.pname != null) {
				return false;
			}
		} else if (!pname.equals(other.pname)) {
			return false;
		}
		if (scanModuleId != other.scanModuleId) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}	
}