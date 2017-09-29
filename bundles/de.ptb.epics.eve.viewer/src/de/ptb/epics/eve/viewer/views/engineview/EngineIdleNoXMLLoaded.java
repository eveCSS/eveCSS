package de.ptb.epics.eve.viewer.views.engineview;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public class EngineIdleNoXMLLoaded extends AbstractEngineState {
	private static EngineState instance;
	
	private EngineIdleNoXMLLoaded() {
	}
	
	public static EngineState getInstance() {
		if (EngineIdleNoXMLLoaded.instance == null) {
			EngineIdleNoXMLLoaded.instance = new EngineIdleNoXMLLoaded();
		}
		return EngineIdleNoXMLLoaded.instance;
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
		return false;
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
		return false;
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
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTrigger() {
		return false;
	}
}