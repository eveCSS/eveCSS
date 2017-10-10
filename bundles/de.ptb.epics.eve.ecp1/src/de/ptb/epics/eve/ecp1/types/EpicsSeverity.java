package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public enum EpicsSeverity {

	/** */
	NO_ALARM, 
	
	/** */
	MINOR_ALARM, 
	
	/** */
	MAJOR_ALARM, 
	
	/** */
	INVALID_ALARM;

	/**
	 * Returns the byte code of the given 
	 * {@link de.ptb.epics.eve.ecp1.types.EpicsSeverity}.
	 * 
	 * @param epicsServerity the epics severity
	 * @return the byte code of the given 
	 * 			{@link de.ptb.epics.eve.ecp1.types.EpicsSeverity}
	 */
	public static byte epicsSeverityToByte(final EpicsSeverity epicsServerity) {
		switch (epicsServerity) {
		case NO_ALARM:
			return 0x01;
		case MINOR_ALARM:
			return 0x02;
		case MAJOR_ALARM:
			return 0x03;
		case INVALID_ALARM:
			return 0x04;
		}
		return Byte.MAX_VALUE;
	}

	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.EpicsSeverity} of the 
	 * given byte code.
	 * 
	 * @param theByte the epics severity as a byte code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.EpicsSeverity} of the 
	 * 			given byte code
	 */
	public static EpicsSeverity byteToEpicsSeverity(final byte theByte) {
		switch (theByte) {
		case 0x01:
			return EpicsSeverity.NO_ALARM;
		case 0x02:
			return EpicsSeverity.MINOR_ALARM;
		case 0x03:
			return EpicsSeverity.MAJOR_ALARM;
		case 0x04:
			return EpicsSeverity.INVALID_ALARM;
		}
		return null;
	}
}