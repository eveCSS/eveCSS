package de.ptb.epics.eve.viewer.views.engineview.enginestate;

/**
 * @author Marcus Michalsky
 * @since 1.29
 */
public class EngineTryingToConnect extends AbstractEngineState {
	private static EngineState instance;
	
	private EngineTryingToConnect() {
	}
	
	public static EngineState getInstance() {
		if (EngineTryingToConnect.instance == null) {
			EngineTryingToConnect.instance = new EngineTryingToConnect();
		}
		return EngineTryingToConnect.instance;
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
		return false;
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
		return false;
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