package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class Paused extends AbstractEngineState {
	private static EngineState instance;
	
	private Paused() {
	}
	
	public static EngineState getInstance() {
		if (Paused.instance == null) {
			Paused.instance = new Paused();
		}
		return Paused.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return true;
	}
}