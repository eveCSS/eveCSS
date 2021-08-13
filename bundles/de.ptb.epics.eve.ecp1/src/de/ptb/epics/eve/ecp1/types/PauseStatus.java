package de.ptb.epics.eve.ecp1.types;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public enum PauseStatus {
	PAUSE_INACTIVE_NOT_OVERRIDDEN {
		@Override
		public String toString() {
			return "pause inactive, not overridden";
		}
	},
	
	PAUSE_ACTIVE_NOT_OVERRIDDEN {
		@Override
		public String toString() {
			return "pause active, not overridden";
		}
	},
	
	PAUSE_INACTIVE_OVERRIDDEN {
		@Override
		public String toString() {
			return "you should not be able to read this. invalid state";
		}
	},
	
	PAUSE_ACTIVE_OVERRIDDEN {
		@Override
		public String toString() {
			return "pause active, overridden";
		}
	};
	
	/**
	 * 
	 * @param pauseStatus
	 * @return 
	 */
	public static int pauseStatusToInt(final PauseStatus pauseStatus) {
		switch (pauseStatus) {
		case PAUSE_INACTIVE_NOT_OVERRIDDEN:
			return 0x00;
		case PAUSE_ACTIVE_NOT_OVERRIDDEN:
			return 0x01;
		case PAUSE_INACTIVE_OVERRIDDEN:
			return 0x02;
		case PAUSE_ACTIVE_OVERRIDDEN:
			return 0x03;
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
		case 0x00: 
			return PauseStatus.PAUSE_INACTIVE_NOT_OVERRIDDEN;
		case 0x01: 
			return PauseStatus.PAUSE_ACTIVE_NOT_OVERRIDDEN;
		case 0x02: 
			return PauseStatus.PAUSE_INACTIVE_OVERRIDDEN;
		case 0x03:
			return PauseStatus.PAUSE_ACTIVE_OVERRIDDEN;
		default: 
			break;
		}
		throw new IllegalArgumentException("no pause type matching given byte: " + status);
	}
}
