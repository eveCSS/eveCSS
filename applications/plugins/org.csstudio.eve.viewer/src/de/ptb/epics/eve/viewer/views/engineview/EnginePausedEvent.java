package de.ptb.epics.eve.viewer.views.engineview;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public class EnginePausedEvent extends AbstractEngineState {
	private static EngineState instance;
	
	private EnginePausedEvent() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EngineState getInstance() {
		if (EnginePausedEvent.instance == null) {
			EnginePausedEvent.instance = new EnginePausedEvent();
		}
		return EnginePausedEvent.instance;
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