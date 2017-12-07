package de.ptb.epics.eve.viewer.views.engineview.enginestate;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public class EngineExecuting extends AbstractEngineState {
	private static EngineState instance;
	
	private EngineExecuting() {
	}
	
	public static EngineState getInstance() {
		if (EngineExecuting.instance == null) {
			EngineExecuting.instance = new EngineExecuting();
		}
		return EngineExecuting.instance;
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
		return true;
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
		return false;
	}
}