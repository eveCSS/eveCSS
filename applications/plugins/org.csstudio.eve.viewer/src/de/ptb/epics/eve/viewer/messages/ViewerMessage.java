package de.ptb.epics.eve.viewer.messages;

import gov.aps.jca.dbr.TimeStamp;

import java.util.Calendar;

import de.ptb.epics.eve.ecp1.client.model.Error;
import de.ptb.epics.eve.viewer.MessageSource;

/**
 * <code>ViewerMessage</code> represents a message displayed in the Messages 
 * View of the Viewer. It is composed of  
 * <ul>
 * 	<li>a time stamp - indicating when the message occurred</li>
 *  <li>a source - indicating the origin of the message</li>
 *  <li>a type - indicating the type of the message (e.g. info or error)</li>
 *  <li>the content - the text part of the message</li>
 * </ul>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ViewerMessage {
	
		// another representation of the time
		private final TimeStamp timestamp;
		// the difference between epics and unix epoch in seconds
		// epics epoch is 1990/01/01 and unix is 1970/01/01
		private final long epoch_diff_secs = 631152000;
		// the source of the message		
		private final MessageSource messageSource;
		// the type of the message
		private final Levels messageType;
		// the contents of the message
		private final String message;
		
		
		/**
		 * Constructs a <code>ViewerMessage</code> which has the current date as 
		 * time.
		 * 
		 * @param messageSource the source of the message
		 * @param messageType the type of the message
		 * @param message the content of the message
		 */
		public ViewerMessage(final MessageSource messageSource, 
							 final Levels messageType, 
							 final String message) {
			this.timestamp = new TimeStamp();
			this.messageSource = messageSource;
			this.messageType = messageType;
			this.message = message;
		}
		
		/**
		 * Constructs a <code>ViewerMessage</code> which has the current date as 
		 * time and the Viewer as source.
		 * 
		 * @param messageType the type of the message
		 * @param message the content of the message
		 */
		public ViewerMessage(final Levels messageType, 
							 final String message) {
			this.timestamp = new TimeStamp();
			this.messageSource = MessageSource.VIEWER;
			this.messageType = messageType;
			this.message = message;
		}

		/**
		 * Constructs a <code>ViewerMessage</code>.
		 * 
		 * @param cal the date of the message 
		 * @param messageSource the source of the message
		 * @param messageType the type of the message
		 * @param message the content of the message
		 */
		public ViewerMessage(final Calendar cal, 
							 final MessageSource messageSource, 
							 final Levels messageType, 
							 final String message) {
			
			this.timestamp = new TimeStamp(
					cal.getTimeInMillis() - epoch_diff_secs*1000.0);
			this.messageSource = messageSource;
			this.messageType = messageType;
			this.message = message;
		}
		
		/**
		 * Constructs a <code>ViewerMessage</code> out of an 
		 * {@link de.ptb.epics.eve.ecp1.client.model.Error}. 
		 * 
		 * @param error the error as in 
		 * 		  {@link de.ptb.epics.eve.ecp1.client.model.Error}
		 */
		public ViewerMessage(final Error error) {
			
			this.timestamp = new TimeStamp(
					error.getGerenalTimeStamp() - epoch_diff_secs, 
					error.getNanoseconds());
			this.messageSource = MessageSource.convertFromErrorFacility(
					error.getErrorFacility());
			this.messageType = Levels.convertFromErrorSeverity(
					error.getErrorSeverity());
			this.message = error.getText();	
		}
		
		/**
		 * Returns the contents of the message.
		 * 
		 * @return the message contents
		 */
		public String getMessage() {
			return this.message;
		}

		/**
		 * Returns the source of the message.
		 * 
		 * @return the message' source
		 */
		public MessageSource getMessageSource() {
			return this.messageSource;
		}

		/**
		 * Returns the type of the message.
		 * 
		 * @return the message' type
		 */
		public Levels getMessageType() {
			return this.messageType;
		}		
		
		/**
		 * Returns the {@link gov.aps.jca.dbr.TimeStamp} of the message.
		 * 
		 * @return the time stamp
		 */
		public TimeStamp getTimeStamp() {
			return this.timestamp;
		}
}