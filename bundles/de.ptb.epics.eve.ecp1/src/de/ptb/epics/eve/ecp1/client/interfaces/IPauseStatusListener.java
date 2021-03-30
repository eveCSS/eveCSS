package de.ptb.epics.eve.ecp1.client.interfaces;

import de.ptb.epics.eve.ecp1.commands.PauseStatusCommand;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public interface IPauseStatusListener {
	
	/**
	 * Called if the pause status has changed.
	 * 
	 * @param pauseStatus contains properties of the pause status
	 */
	public void pauseStatusChangedListener(PauseStatusCommand pauseStatus);
}
