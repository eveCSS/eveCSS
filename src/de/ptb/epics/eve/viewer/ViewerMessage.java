package de.ptb.epics.eve.viewer;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
		
		public ViewerMessage( final Calendar messageDateTime, final MessageSource messageSource, final MessageTypes messageType, final String message ) {
			this.messageDateTime = messageDateTime;
			this.messageSource = messageSource;
			this.messageType = messageType;
			this.message = message;
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
