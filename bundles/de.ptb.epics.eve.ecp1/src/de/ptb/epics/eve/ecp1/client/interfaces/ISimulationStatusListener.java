package de.ptb.epics.eve.ecp1.client.interfaces;

/**
 * @since 1.37
 */
public interface ISimulationStatusListener {
	
	/**
	 * @param simulationButtonEnabled indicates whether the simulation button was enabled/disabled
	 * @param simulation indicated whether simulation mode is enabled/disabled
	 */
	void simulationStatusChanged(boolean simulationButtonEnabled, boolean simulation);
}
