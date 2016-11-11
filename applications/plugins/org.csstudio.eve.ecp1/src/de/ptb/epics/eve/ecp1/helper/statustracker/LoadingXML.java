package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class LoadingXML extends AbstractEngineState {
	private static EngineState instance;
	
	private LoadingXML() {
	}
	
	public static EngineState getInstance() {
		if (LoadingXML.instance == null) {
			LoadingXML.instance = new LoadingXML();
		}
		return LoadingXML.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return true;
	}
}