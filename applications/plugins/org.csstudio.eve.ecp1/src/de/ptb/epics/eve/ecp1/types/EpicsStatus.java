package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public enum EpicsStatus {

	/** */
	OK, 
	
	/** */
	WARNING, 
	
	/** */
	ERROR, 
	
	/** */
	FATAL;

	/**
	 * Returns the byte code corresponding to the given 
	 * {@link de.ptb.epics.eve.ecp1.types.EpicsStatus}.
	 * 
	 * @param epicsStatus the epics status
	 * @return the byte code corresponding to the given 
	 * 			{@link de.ptb.epics.eve.ecp1.types.EpicsStatus}
	 */
	public static byte epicsStatusToByte(final EpicsStatus epicsStatus) {
		switch (epicsStatus) {
		case OK:
			return 0x01;
		case WARNING:
			return 0x02;
		case ERROR:
			return 0x03;
		case FATAL:
			return 0x04;
		}
		return Byte.MAX_VALUE;
	}

	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.EpicsStatus} 
	 * corresponding to the given byte code.
	 * 
	 * @param theByte the epics status as a byte code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.EpicsStatus} 
	 * 			corresponding to the given byte code
	 */
	public static EpicsStatus byteToEpicsStatus(final byte theByte) {
		switch (theByte) {
		case 0x01:
			return EpicsStatus.OK;
		case 0x02:
			return EpicsStatus.WARNING;
		case 0x03:
			return EpicsStatus.ERROR;
		case 0x04:
			return EpicsStatus.FATAL;
		}
		return null;
	}
}