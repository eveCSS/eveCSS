package de.ptb.epics.eve.ecp1.types;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public enum PauseStatus {
	PAUSE_ACTIVATED {
		@Override
		public String toString() {
			return "pause activated";
		}
	}, 
	
	PAUSE_OVERRIDE {
		@Override
		public String toString() {
			return "pause override";
		}
	};
	
	/**
	 * 
	 * @param pauseStatus
	 * @return 
	 */
	public static int pauseStatusToInt(final PauseStatus pauseStatus) {
		switch (pauseStatus) {
		case PAUSE_ACTIVATED:
			return 0x01;
		case PAUSE_OVERRIDE:
			return 0x02;
		default:
			break;
		}
		return 0xFF;
	}
	
	/**
	 * 
	 * @param status
	 * @return
	 */
	public static PauseStatus byteToPauseStatus(final byte status) {
		switch (status) {
		case 0x01: 
			return PauseStatus.PAUSE_ACTIVATED;
		case 0x02: 
			return PauseStatus.PAUSE_OVERRIDE;
		default: 
			break;
		}
		throw new IllegalArgumentException("no pause type matching given byte: " + status);
	}
}
