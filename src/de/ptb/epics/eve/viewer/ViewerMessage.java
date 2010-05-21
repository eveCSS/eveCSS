package de.ptb.epics.eve.viewer;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.ptb.epics.eve.ecp1.client.model.Error;

public class ViewerMessage {
	
		private final Calendar messageDateTime;
		private final MessageSource messageSource;
		private final MessageTypes messageType;
		private final String message;
		
		public ViewerMessage( final MessageSource messageSource, final MessageTypes messageType, final String message ) {
			this.messageDateTime = new GregorianCalendar();
			this.messageSource = messageSource;
			this.messageType = messageType;
			this.message = message;
		}
		
		public ViewerMessage( final MessageTypes messageType, final String message ) {
			this.messageDateTime = new GregorianCalendar();
			this.messageSource = MessageSource.VIEWER;
			this.messageType = messageType;
			this.message = message;
		}

		public ViewerMessage( final Calendar messageDateTime, final MessageSource messageSource, final MessageTypes messageType, final String message ) {
			this.messageDateTime = messageDateTime;
			this.messageSource = messageSource;
			this.messageType = messageType;
			this.message = message;
		}
		
		public ViewerMessage( final Error error ) {
			
			this.messageDateTime = new GregorianCalendar( 1990, 0, 1, 0, 0 );
			this.messageDateTime.add( Calendar.SECOND, error.getGerenalTimeStamp() );
			this.messageDateTime.add(Calendar.MILLISECOND, error.getNanoseconds() / 1000000);
			this.messageSource = MessageSource.convertFromErrorFacility( error.getErrorFacility() );
			this.messageType = MessageTypes.convertFromErrorSeverity( error.getErrorSeverity() );
			this.message = error.getText();
			
		}

		public String getMessage() {
			return this.message;
		}

		public MessageSource getMessageSource() {
			return this.messageSource;
		}
		
		public Calendar getMessageDateTime() {
			return this.messageDateTime;
		}

		public MessageTypes getMessageType() {
			return this.messageType;
		}
		
		
}
