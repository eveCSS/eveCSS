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
	
	/** executing */
	EXECUTING,
	
	/** chain done with executing scan modules */
	EXECUTING_DONE,
	
	/** all data of the chain has been saved */
	STORAGE_DONE,

	/** all math calculations have been done */
	MATH_DONE,

	/** chain is done with everything */
	CHAIN_DONE;
	
	public static String toString(final ChainStatus chainStatus) {
		switch (chainStatus) {
		case IDLE:
			return "idle";
		case EXECUTING:
			return "executing";
		case EXECUTING_DONE:
			return "exec done";
		case STORAGE_DONE:
			return "storage done";
		case MATH_DONE:
			return "math done";
		case CHAIN_DONE:
			return "done";
		case UNKNOWN:
			return "unknown";
	}
	return "Unknown";
	}
	/**
	 * Returns the byte code of the given 
	 * {@link de.ptb.epics.eve.ecp1.types.ChainStatus}.
	 * 
	 * @param chainStatus the chain status
	 * @return the byte code of the given 
	 * 			{@link de.ptb.epics.eve.ecp1.types.ChainStatus}
	 */
	public static int chainStatusToInt (final ChainStatus chainStatus) {
		switch (chainStatus) {
			case IDLE:
				return 0x01;
			case EXECUTING:
				return 0x02;
			case EXECUTING_DONE:
				return 0x03;
			case STORAGE_DONE:
				return 0x04;
			case MATH_DONE:
				return 0x05;
			case CHAIN_DONE:
				return 0x06;
			case UNKNOWN:
				return Integer.MAX_VALUE;
		}
		return Integer.MAX_VALUE;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.ChainStatus} of the 
	 * given byte code.
	 * 
	 * @param theByte the chain status as a byte code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.ChainStatus} of the 
	 * 			given byte code
	 */
	public static ChainStatus intToChainStatus(final int statusno) {
		switch (statusno) {
			case 0x01:
				return ChainStatus.IDLE;
			case 0x02:
				return ChainStatus.EXECUTING;
			case 0x03:
				return ChainStatus.EXECUTING_DONE;
			case 0x04:
				return ChainStatus.STORAGE_DONE;
			case 0x05:
				return ChainStatus.MATH_DONE;
			case 0x06:
				return ChainStatus.CHAIN_DONE;
		}
		return ChainStatus.UNKNOWN;
	}
}