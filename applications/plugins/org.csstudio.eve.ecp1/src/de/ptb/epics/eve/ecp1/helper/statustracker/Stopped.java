package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
final class Stopped extends AbstractEngineState {
	private static EngineState instance;
	
	private Stopped() {
	}
	
	public static EngineState getInstance() {
		if (Stopped.instance == null) {
			Stopped.instance = new Stopped();
		}
		return Stopped.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return true;
	}
}