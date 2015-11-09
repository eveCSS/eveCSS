package de.ptb.epics.eve.viewer.views.engineview;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public class EnginePausedUser extends AbstractEngineState {
	private static EngineState instance;
	
	private EnginePausedUser() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EngineState getInstance() {
		if (EnginePausedUser.instance == null) {
			EnginePausedUser.instance = new EnginePausedUser();
		}
		return EnginePausedUser.instance;
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