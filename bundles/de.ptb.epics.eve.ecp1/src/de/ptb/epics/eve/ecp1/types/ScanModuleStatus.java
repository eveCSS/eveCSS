package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 */
public enum ScanModuleStatus {

	/** not (yet) started*/
	NOT_STARTED,
	
	/** inititalizing */
	INITIALIZING,
	
	/** executing */
	EXECUTING,
	
	/** paused */
	PAUSED,
	
	/** waiting for trigger */
	TRIGGER_WAIT,

	/** done, appended scan module still executing  */
	DONE_APPENDED,

	/** done */
	DONE,
	
	/**  */
	UNKNOWN;

	/**
	 * Returns the int code of the given 
	 * {@link de.ptb.epics.eve.ecp1.types.ScanModuleStatus}.
	 * 
	 * @param ScanModuleStatus the scan module status
	 * @return the int code of the given 
	 * 			{@link de.ptb.epics.eve.ecp1.types.ScanModuleStatus}
	 */
	public static int ScanModuleStatusToByte(final ScanModuleStatus scanModuleStatus) {
		switch (scanModuleStatus) {
			case NOT_STARTED:
				return 0x00;
			case INITIALIZING:
				return 0x01;
			case EXECUTING:
				return 0x02;
			case PAUSED:
				return 0x03;
			case TRIGGER_WAIT:
				return 0x04;
			case DONE_APPENDED:
				return 0x05;
			case DONE:
				return 0x06;
			case UNKNOWN:
				return Byte.MAX_VALUE;
		}
		return Byte.MAX_VALUE;
	}
	
	public static String toString(final ScanModuleStatus scanModuleStatus) {
		switch (scanModuleStatus) {
			case NOT_STARTED:
				return "not started";
			case INITIALIZING:
				return "initializing";
			case EXECUTING:
				return "executing";
			case PAUSED:
				return "paused";
			case TRIGGER_WAIT:
				return "triggerwait";
			case DONE_APPENDED:
				return "done";
			case DONE:
				return "done";
			case UNKNOWN:
				return "unknown";
		}
		return "unknown";
	}

	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.ScanModuleStatus} of the 
	 * given int code.
	 * 
	 * @param status scan module status as a int code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.ScanModuleStatus} of the 
	 * 			given int code
	 */
	public static ScanModuleStatus intToScanModuleStatus(final int status) {
		switch (status) {
			case 0x00:
				return ScanModuleStatus.NOT_STARTED;
			case 0x01:
				return ScanModuleStatus.INITIALIZING;
			case 0x02:
				return ScanModuleStatus.EXECUTING;
			case 0x03:
				return ScanModuleStatus.PAUSED;
			case 0x04:
				return ScanModuleStatus.TRIGGER_WAIT;
			case 0x05:
				return ScanModuleStatus.DONE_APPENDED;
			case 0x06:
				return ScanModuleStatus.DONE;
		}
		return ScanModuleStatus.UNKNOWN;
	}
}