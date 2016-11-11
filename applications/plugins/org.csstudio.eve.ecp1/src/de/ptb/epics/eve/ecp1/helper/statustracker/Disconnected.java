package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
final class Disconnected extends AbstractEngineState {
	private static EngineState instance;
	
	private Disconnected() {
	}
	
	public static EngineState getInstance() {
		if (Disconnected.instance == null) {
			Disconnected.instance = new Disconnected();
		}
		return Disconnected.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return false;
	}
}