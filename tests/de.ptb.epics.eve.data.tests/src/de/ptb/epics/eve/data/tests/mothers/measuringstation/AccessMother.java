package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import java.util.Calendar;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.MethodTypes;
import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.Access;

/**
 * Fabricates Access test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class AccessMother {
	
	public static Access createNewAccess() {
		return AccessMother.createNewIntTypeAccess();
	}
	
	/**
	 * @since 1.34
	 */
	public static Access createNewIntTypeAccess() {
		Access access = new Access(MethodTypes.GET);
		access.setVariableID(
				"Access-" + Calendar.getInstance().getTime().getTime());
		access.setType(DataTypes.INT);
		access.setCount(1);
		access.setTransport(TransportTypes.LOCAL);
		access.setTimeout(300);
		access.setMonitor(false);
		
		return access;
	}
	
	/**
	 * @since 1.34
	 */
	public static Access createNewDoubleTypeAccess() {
		Access access = new Access(MethodTypes.GET);
		access.setVariableID(
				"Access-" + Calendar.getInstance().getTime().getTime());
		access.setType(DataTypes.DOUBLE);
		access.setCount(1);
		access.setTransport(TransportTypes.LOCAL);
		access.setTimeout(300);
		access.setMonitor(false);
		
		return access;
	}
	
	/**
	 * @since 1.35
	 */
	public static Access createNewDateTypeAccess() {
		Access access = new Access(MethodTypes.GET);
		access.setVariableID(
				"Access-" + Calendar.getInstance().getTime().getTime());
		access.setType(DataTypes.DATETIME);
		access.setCount(1);
		access.setTransport(TransportTypes.LOCAL);
		access.setTimeout(300);
		access.setMonitor(false);
		
		return access;
	}
	
	private AccessMother() {
		// private
	}
}