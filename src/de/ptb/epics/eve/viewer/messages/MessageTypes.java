package de.ptb.epics.eve.viewer.messages;

import de.ptb.epics.eve.ecp1.intern.ErrorSeverity;

/**
 * <code>MessageTypes</code> contains all available types of of a message.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public enum MessageTypes {

	/**
	 * a debug level message (lowest level)
	 */
	DEBUG,
	
	/**
	 * an info level message
	 */
	INFO,
	
	/**
	 * a minor level message
	 */
	MINOR,
	
	/**
	 * an error level message
	 */
	ERROR,
	
	/**
	 * a fatal level message (highest level)
	 */
	FATAL;
	
	/**
	 * Converts an {@link de.ptb.epics.eve.ecp1.intern.ErrorSeverity} into its 
	 * corresponding {@link de.ptb.epics.eve.viewer.messages.MessageTypes}. 
	 * 
	 * @param errorSeverity the 
	 * 		  {@link de.ptb.epics.eve.ecp1.intern.ErrorSeverity}
	 * @return the corresponding 
	 *         {@link de.ptb.epics.eve.viewer.messages.MessageTypes}
	 */
	public static MessageTypes convertFromErrorSeverity(
										final ErrorSeverity errorSeverity) {
		
		switch(errorSeverity) {
		
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
		
		throw new UnsupportedOperationException(
				"Unsupported value: " + errorSeverity);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
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