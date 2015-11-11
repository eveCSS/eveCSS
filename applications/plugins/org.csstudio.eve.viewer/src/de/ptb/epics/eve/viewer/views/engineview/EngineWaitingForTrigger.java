package de.ptb.epics.eve.viewer.views.engineview;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public class EngineWaitingForTrigger extends AbstractEngineState {
	private static EngineState instance;
	
	private EngineWaitingForTrigger() {
	}
	
	public static EngineState getInstance() {
		if (EngineWaitingForTrigger.instance == null) {
			EngineWaitingForTrigger.instance = new EngineWaitingForTrigger();
		}
		return EngineWaitingForTrigger.instance;
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
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSkip() {
		return true;
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
		return true;
	}
}