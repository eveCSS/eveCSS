package de.ptb.epics.eve.ecp1.commands;

import de.ptb.epics.eve.ecp1.types.PauseStatus;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseStatusEntry {
	private PauseStatus pauseStatus;
	private String deviceId;
	
	public PauseStatusEntry(String deviceId, PauseStatus pauseStatus) {
		this.deviceId = deviceId;
		this.pauseStatus = pauseStatus;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public PauseStatus getPauseStatus() {
		return pauseStatus;
	}
}
