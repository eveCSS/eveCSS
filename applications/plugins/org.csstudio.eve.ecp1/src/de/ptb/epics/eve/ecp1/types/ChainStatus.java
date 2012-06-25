package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public enum ChainStatus {

	/** */
	UNKNOWN,
	
	/** chain is idle */
	IDLE,
	
	/** initializing scan module (initialization may be done before starting) */
	STARTING_SM,
	
	/** executing scan module */
	EXECUTING_SM,
	
	/** scan module is paused */
	SM_PAUSED,
	
	/** waiting for manual trigger */
	WAITING_FOR_MANUAL_TRIGGER,
	
	/** exiting scan module */
	EXITING_SM,
	
	/** exiting chain */
	EXITING_CHAIN,
	
	/** all data of the chain has been saved */
	STORAGE_DONE;
	
	/**
	 * Returns the byte code of the given 
	 * {@link de.ptb.epics.eve.ecp1.types.ChainStatus}.
	 * 
	 * @param chainStatus the chain status
	 * @return the byte code of the given 
	 * 			{@link de.ptb.epics.eve.ecp1.types.ChainStatus}
	 */
	public static byte chainStatusToByte(final ChainStatus chainStatus) {
		switch (chainStatus) {
			case IDLE:
				return 0x01;
			case STARTING_SM:
				return 0x02;
			case EXECUTING_SM:
				return 0x03;
			case SM_PAUSED:
				return 0x04;
			case WAITING_FOR_MANUAL_TRIGGER:
				return 0x05;
			case EXITING_SM:
				return 0x06;
			case EXITING_CHAIN:
				return 0x07;
			case STORAGE_DONE:
				return 0x08;
			case UNKNOWN:
				return Byte.MAX_VALUE;
		}
		return Byte.MAX_VALUE;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.ChainStatus} of the 
	 * given byte code.
	 * 
	 * @param theByte the chain status as a byte code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.ChainStatus} of the 
	 * 			given byte code
	 */
	public static ChainStatus byteToChainStatus(final byte theByte) {
		switch (theByte) {
			case 0x01:
				return ChainStatus.IDLE;
			case 0x02:
				return ChainStatus.STARTING_SM;
			case 0x03:
				return ChainStatus.EXECUTING_SM;
			case 0x04:
				return ChainStatus.SM_PAUSED;
			case 0x05:
				return ChainStatus.WAITING_FOR_MANUAL_TRIGGER;
			case 0x06:
				return ChainStatus.EXITING_SM;
			case 0x07:
				return ChainStatus.EXITING_CHAIN;
			case 0x08:
				return ChainStatus.STORAGE_DONE;
		}
		return ChainStatus.UNKNOWN;
	}
}