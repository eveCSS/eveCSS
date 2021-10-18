package de.ptb.epics.eve.ecp1.commands;

import de.ptb.epics.eve.ecp1.types.PauseStatus;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseStatusEntry {
	private PauseStatus pauseStatus;
	private int id;
	
	public PauseStatusEntry(int id, PauseStatus pauseStatus) {
		this.id = id;
		this.pauseStatus = pauseStatus;
	}
	
	public int getId() {
		return this.id;
	}
	
	public PauseStatus getPauseStatus() {
		return pauseStatus;
	}
}
