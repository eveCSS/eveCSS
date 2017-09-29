package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
final class Executing extends AbstractEngineState {
	private static EngineState instance;
	
	private Executing() {
	}
	
	public static EngineState getInstance() {
		if (Executing.instance == null) {
			Executing.instance = new Executing();
		}
		return Executing.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return true;
	}
}