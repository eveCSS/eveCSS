package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class IdleNoXMLLoaded extends AbstractEngineState {
	private static EngineState instance;
	
	private IdleNoXMLLoaded() {
	}
	
	public static EngineState getInstance() {
		if (IdleNoXMLLoaded.instance == null) {
			IdleNoXMLLoaded.instance = new IdleNoXMLLoaded();
		}
		return IdleNoXMLLoaded.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return true;
	}
}