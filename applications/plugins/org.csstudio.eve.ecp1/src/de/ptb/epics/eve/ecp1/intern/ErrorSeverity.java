package de.ptb.epics.eve.ecp1.intern;

public enum ErrorSeverity {

	DEBUG,
	INFO,
	MINOR,
	ERROR,
	FATAL,
	SYSTEM;
	
	public static byte errorSeverityToByte( final ErrorSeverity errorSeverity ) {
		switch( errorSeverity ) {
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
	
	public static ErrorSeverity byteToErrorSeverity( final byte theByte ) {
		switch( theByte ) {
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
