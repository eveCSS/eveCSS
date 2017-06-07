package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
final class Invalid extends AbstractEngineState {
	private static EngineState instance;
	
	private Invalid() {
	}
	
	public static EngineState getInstance() {
		if (Invalid.instance == null) {
			Invalid.instance = new Invalid();
		}
		return Invalid.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return true;
	}
}