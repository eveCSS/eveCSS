package de.ptb.epics.eve.viewer;

import de.ptb.epics.eve.ecp1.intern.ErrorSeverity;

public enum MessageTypes {

	DEBUG,
	INFO,
	MINOR,
	ERROR,
	FATAL;
	
	public static MessageTypes convertFromErrorSeverity( final ErrorSeverity errorSeverity ) {
		
		switch( errorSeverity ) {
		
			case DEBUG:
				return MessageTypes.DEBUG;
				
			case INFO:
				return MessageTypes.INFO;
				
			case MINOR:
				return MessageTypes.MINOR;
				
			case ERROR:
				return MessageTypes.ERROR;
				
			case FATAL:
				return MessageTypes.FATAL;
		
		}
		
		throw new UnsupportedOperationException( "Unsupported value: " + errorSeverity );
	}
	
	public String toString(){
		switch (this) {
			case DEBUG:
				return "D";
			case INFO:
				return "I";
			case MINOR:
				return "M";
			case ERROR:
				return "E";
			case FATAL:
				return "F";
			default:
				return "U";
		}
	}	
}
