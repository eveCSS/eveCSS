package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public enum ErrorSeverity {

	/** */
	DEBUG,
	
	/** */
	INFO,
	
	/** */
	MINOR,
	
	/** */
	ERROR,
	
	/** */
	FATAL,
	
	/** */
	SYSTEM;
	
	/**
	 * Returns the byte code of the given 
	 * {@link de.ptb.epics.eve.ecp1.types.ErrorSeverity}.
	 * 
	 * @param errorSeverity the error severity
	 * @return the byte code of the given 
	 * 		{@link de.ptb.epics.eve.ecp1.types.ErrorSeverity}
	 */
	public static byte errorSeverityToByte(final ErrorSeverity errorSeverity) {
		switch (errorSeverity) {
			case DEBUG:
				return 0x05;
			case INFO:
				return 0x04;
			case MINOR:
				return 0x03;
			case ERROR:
				return 0x02;
			case FATAL:
				return 0x01;
			case SYSTEM:
				return 0x00;
		}
		return Byte.MAX_VALUE;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.ErrorSeverity} of the 
	 * given byte code.
	 * 
	 * @param theByte the error severity as a byte code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.ErrorSeverity} of the 
	 * given byte code
	 */
	public static ErrorSeverity byteToErrorSeverity(final byte theByte) {
		switch (theByte) {
			case 0x05:
				return ErrorSeverity.DEBUG;
			case 0x04:
				return ErrorSeverity.INFO;
			case 0x03:
				return ErrorSeverity.MINOR;
			case 0x02:
				return ErrorSeverity.ERROR;
			case 0x01:
				return ErrorSeverity.FATAL;
			case 0x00:
				return ErrorSeverity.SYSTEM;
		}
		return null;
	}
}