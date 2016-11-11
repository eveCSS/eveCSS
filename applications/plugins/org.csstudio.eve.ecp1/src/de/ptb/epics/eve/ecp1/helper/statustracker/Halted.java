package de.ptb.epics.eve.ecp1.helper.statustracker;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class Halted extends AbstractEngineState {
	private static EngineState instance;
	
	private Halted() {
	}
	
	public static EngineState getInstance() {
		if (Halted.instance == null) {
			Halted.instance = new Halted();
		}
		return Halted.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return true;
	}
}