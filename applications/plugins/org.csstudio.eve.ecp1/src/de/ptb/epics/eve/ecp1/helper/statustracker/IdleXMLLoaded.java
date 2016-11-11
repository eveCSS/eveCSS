package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
final class IdleXMLLoaded extends AbstractEngineState {
	private static EngineState instance;
	
	private IdleXMLLoaded() {
	}
	
	public static EngineState getInstance() {
		if (IdleXMLLoaded.instance == null) {
			IdleXMLLoaded.instance = new IdleXMLLoaded();
		}
		return IdleXMLLoaded.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return true;
	}
}