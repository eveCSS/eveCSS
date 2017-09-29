package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
final class Connected extends AbstractEngineState {
	private static EngineState instance;
	
	private Connected() {
	}
	
	public static EngineState getInstance() {
		if (Connected.instance == null) {
			Connected.instance = new Connected();
		}
		return Connected.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return true;
	}
}