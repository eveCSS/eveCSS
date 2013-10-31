package de.ptb.epics.eve.viewer.views.messages;

import de.ptb.epics.eve.ecp1.types.ErrorSeverity;

/**
 * <code>MessageTypes</code> contains all available types of of a message.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public enum Levels {

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
	FATAL,
	
	/**
	 * a system level message
	 */
	SYSTEM;
	
	/**
	 * Converts an {@link de.ptb.epics.eve.ecp1.types.ErrorSeverity} into its 
	 * corresponding {@link de.ptb.epics.eve.viewer.views.messages.Levels}. 
	 * 
	 * @param errorSeverity the 
	 * 		  {@link de.ptb.epics.eve.ecp1.types.ErrorSeverity}
	 * @return the corresponding 
	 *         {@link de.ptb.epics.eve.viewer.views.messages.Levels}
	 */
	public static Levels convertFromErrorSeverity(
			final ErrorSeverity errorSeverity) {
		switch(errorSeverity) {
			case DEBUG:
				return Levels.DEBUG;
				
			case INFO:
				return Levels.INFO;
				
			case MINOR:
				return Levels.MINOR;
				
			case ERROR:
				return Levels.ERROR;
				
			case FATAL:
				return Levels.FATAL;
		
			case SYSTEM:
				return Levels.SYSTEM;
		}
		throw new UnsupportedOperationException(
				"Unsupported value: " + errorSeverity);
	}
	
	/**
	 * Converts the <code>Enum</code> into a {@link java.lang.String} such that
	 * <ul>
	 *  <li><code>DEBUG</code> becomes "D",</li>
	 *  <li><code>INFO</code> becomes "I",</li>
	 *  <li><code>MINOR</code> becomes "M",</li>
	 *  <li><code>ERROR</code> becomes "E",</li>
	 *  <li><code>FATAL</code> becomes "F".</li>
	 * </ul>
	 * The default value is "U".
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