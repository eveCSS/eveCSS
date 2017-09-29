package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 */
public enum ScanModuleReason {

	/** not (yet) started*/
	NONE,
	
	/** scan module redo */
	SM_REDO_ACTIVE,
	
	/** chain redo */
	CHAIN_REDO_ACTIVE,
	
	/** scan module pause */
	SM_PAUSE,
	
	/** chain pause */
	CHAIN_PAUSE,
	
	/** user pause */
	USER_PAUSE,
	
	/** scan module skip */
	SM_SKIP,
	
	/** chain skip */
	CHAIN_SKIP,
	
	/** user skip */
	USER_SKIP,
	
	/** chain stop */
	CHAIN_STOP,
	
	/** user stop */
	USER_STOP;
		
	/**
	 * Returns the int code of the given 
	 * {@link de.ptb.epics.eve.ecp1.types.ScanModuleReason}.
	 * 
	 * @param ScanModuleReason the reason of scan module status
	 * @return the int code of the given 
	 * 			{@link de.ptb.epics.eve.ecp1.types.ScanModuleReason}
	 */
	public static int ScanModuleReasonToInt(final ScanModuleReason scanModuleReason) {
		switch (scanModuleReason) {
			case NONE:
				return 0x00;
			case SM_REDO_ACTIVE:
				return 0x01;
			case CHAIN_REDO_ACTIVE:
				return 0x02;
			case SM_PAUSE:
				return 0x03;
			case CHAIN_PAUSE:
				return 0x04;
			case USER_PAUSE:
				return 0x05;
			case SM_SKIP:
				return 0x06;
			case CHAIN_SKIP:
				return 0x07;
			case USER_SKIP:
				return 0x08;
			case CHAIN_STOP:
				return 0x09;
			case USER_STOP:
				return 0x0a;
		}
		return Integer.MAX_VALUE;
	}

	public static String toString(final ScanModuleReason scanModuleReason) {
		switch (scanModuleReason) {
			case NONE:
				return " ";
			case SM_REDO_ACTIVE:
				return "sm redo";
			case CHAIN_REDO_ACTIVE:
				return "chain redo";
			case SM_PAUSE:
				return "sm pause";
			case CHAIN_PAUSE:
				return "ch pause";
			case USER_PAUSE:
				return "user pause";
			case SM_SKIP:
				return "sm skip";
			case CHAIN_SKIP:
				return "ch skip";
			case USER_SKIP:
				return "user skip";
			case CHAIN_STOP:
				return "ch stop";
			case USER_STOP:
				return "user stop";
		}
		return "unknown";
	}

	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.ScanModuleReason} of the 
	 * given int code.
	 * 
	 * @param reason the chain status as a int code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.ScanModuleReason} of the 
	 * 			given int code
	 */
	public static ScanModuleReason intToScanModuleReason(final int reason) {
		switch (reason) {
			case 0x00:
				return ScanModuleReason.NONE;
			case 0x01:
				return ScanModuleReason.SM_REDO_ACTIVE;
			case 0x02:
				return ScanModuleReason.CHAIN_REDO_ACTIVE;
			case 0x03:
				return ScanModuleReason.SM_PAUSE;
			case 0x04:
				return ScanModuleReason.CHAIN_PAUSE;
			case 0x05:
				return ScanModuleReason.USER_PAUSE;
			case 0x06:
				return ScanModuleReason.SM_SKIP;
			case 0x07:
				return ScanModuleReason.CHAIN_SKIP;
			case 0x08:
				return ScanModuleReason.USER_SKIP;
			case 0x09:
				return ScanModuleReason.CHAIN_STOP;
			case 0x0a:
				return ScanModuleReason.USER_STOP;
		}
		return ScanModuleReason.NONE;
	}
}