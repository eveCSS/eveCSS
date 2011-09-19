package de.ptb.epics.eve.ecp1.client.model;

import de.ptb.epics.eve.ecp1.intern.ErrorCommand;
import de.ptb.epics.eve.ecp1.intern.ErrorFacility;
import de.ptb.epics.eve.ecp1.intern.ErrorSeverity;
import de.ptb.epics.eve.ecp1.intern.ErrorType;

public final class Error {
	
	private final int gerenalTimeStamp;
	private final int nanoseconds;
	private final ErrorSeverity errorSeverity;
	private final ErrorFacility errorFacility;
	private final ErrorType errorType;
	private final String text;
	
	public Error( final ErrorCommand errorCommand ) {
		if( errorCommand == null ) {
			throw new IllegalArgumentException( "The parameter 'errorCommand' must not be null!" );
		}
		this.gerenalTimeStamp = errorCommand.getGerenalTimeStamp();
		this.nanoseconds = errorCommand.getNanoseconds();
		this.errorSeverity = errorCommand.getErrorSeverity();
		this.errorFacility = errorCommand.getErrorFacility();
		this.errorType = errorCommand.getErrorType();
		this.text = errorCommand.getText();
	}
	
	public final ErrorFacility getErrorFacility() {
		return this.errorFacility;
	}

	public final ErrorSeverity getErrorSeverity() {
		return this.errorSeverity;
	}

	public final ErrorType getErrorType() {
		return this.errorType;
	}

	public final String getText() {
		return this.text;
	}
	
	public final int getGerenalTimeStamp() {
		return this.gerenalTimeStamp;
	}

	public final int getNanoseconds() {
		return this.nanoseconds;
	}
	
}
