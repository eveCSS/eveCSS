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
	},
	
	PAUSE_TEMPORARY_FAKE_VALUE {
		@Override
		public String toString() {
			return "temp fake value to prevent crashes";
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
		case PAUSE_TEMPORARY_FAKE_VALUE:
			return 0x00;
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
		case 0x00: 
			return PauseStatus.PAUSE_TEMPORARY_FAKE_VALUE;
		default: 
			break;
		}
		throw new IllegalArgumentException("no pause type matching given byte: " + status);
	}
}
