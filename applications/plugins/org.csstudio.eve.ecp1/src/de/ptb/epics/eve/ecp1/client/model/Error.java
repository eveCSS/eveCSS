package de.ptb.epics.eve.ecp1.client.model;

import de.ptb.epics.eve.ecp1.commands.ErrorCommand;
import de.ptb.epics.eve.ecp1.types.ErrorFacility;
import de.ptb.epics.eve.ecp1.types.ErrorSeverity;
import de.ptb.epics.eve.ecp1.types.ErrorType;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public final class Error {
	
	private final int gerenalTimeStamp;
	private final int nanoseconds;
	private final ErrorSeverity errorSeverity;
	private final ErrorFacility errorFacility;
	private final ErrorType errorType;
	private final String text;
	
	/**
	 * Constructor.
	 * 
	 * @param errorCommand the error command
	 */
	public Error(final ErrorCommand errorCommand) {
		if (errorCommand == null) {
			throw new IllegalArgumentException(
					"The parameter 'errorCommand' must not be null!");
		}
		this.gerenalTimeStamp = errorCommand.getGerenalTimeStamp();
		this.nanoseconds = errorCommand.getNanoseconds();
		this.errorSeverity = errorCommand.getErrorSeverity();
		this.errorFacility = errorCommand.getErrorFacility();
		this.errorType = errorCommand.getErrorType();
		this.text = errorCommand.getText();
	}
	
	/**
	 * Returns the error facility as in 
	 * {@link de.ptb.epics.eve.ecp1.types.ErrorFacility}.
	 * 
	 * @return the facility of the error
	 */
	public final ErrorFacility getErrorFacility() {
		return this.errorFacility;
	}

	/**
	 * Returns the error severity as in 
	 * {@link de.ptb.epics.eve.ecp1.types.ErrorSeverity}.
	 * 
	 * @return the error severity as in 
	 * 		{@link de.ptb.epics.eve.ecp1.types.ErrorSeverity}
	 */
	public final ErrorSeverity getErrorSeverity() {
		return this.errorSeverity;
	}

	/**
	 * Returns the error type as in 
	 * {@link de.ptb.epics.eve.ecp1.types.ErrorType}.
	 * 
	 * @return the error type as in 
	 * 			{@link de.ptb.epics.eve.ecp1.types.ErrorType}
	 */
	public final ErrorType getErrorType() {
		return this.errorType;
	}

	/**
	 * Returns the error text.
	 * 
	 * @return the error text
	 */
	public final String getText() {
		return this.text;
	}
	
	/**
	 * Returns the time stamp.
	 * 
	 * @return the time stamp
	 */
	public final int getGerenalTimeStamp() {
		return this.gerenalTimeStamp;
	}

	/**
	 * Returns the nanoseconds.
	 * 
	 * @return the nanoseconds
	 */
	public final int getNanoseconds() {
		return this.nanoseconds;
	}
}