package de.ptb.epics.eve.viewer.views.engineview;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public class EngineIdleXMLLoaded extends AbstractEngineState {
	private static EngineState instance;
	
	private EngineIdleXMLLoaded() {
	}
	
	public static EngineState getInstance() {
		if (EngineIdleXMLLoaded.instance == null) {
			EngineIdleXMLLoaded.instance = new EngineIdleXMLLoaded();
		}
		return EngineIdleXMLLoaded.instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStart() {
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isKill() {
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnect() {
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDisconnect() {
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPlay() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPause() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStop() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSkip() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHalt() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTrigger() {
		return false;
	}
}