package de.ptb.epics.eve.ecp1.client.model;

import de.ptb.epics.eve.ecp1.client.ECP1Client;
import de.ptb.epics.eve.ecp1.commands.AnswerRequestCommand;
import de.ptb.epics.eve.ecp1.commands.GenericRequestCommand;
import de.ptb.epics.eve.ecp1.types.RequestState;
import de.ptb.epics.eve.ecp1.types.RequestType;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public class Request {

	private final int requestId;
	private final RequestType requestType;
	private final String requestText;
	private final ECP1Client ecp1Client;
	private RequestState requestState;

	/**
	 * Constructor.
	 * 
	 * @param genericRequestCommand
	 *            the request command
	 * @param ecp1Client
	 *            the engine client
	 */
	public Request(final GenericRequestCommand genericRequestCommand,
			final ECP1Client ecp1Client) {
		if (genericRequestCommand == null) {
			throw new IllegalArgumentException(
					"The parameter 'genericRequestCommand' must not be null!");
		}
		if (ecp1Client == null) {
			throw new IllegalArgumentException(
					"The parameter 'ecp1Client' must not be null!");
		}
		this.requestId = genericRequestCommand.getRequestId();
		this.requestType = genericRequestCommand.getRequestType();
		this.requestText = genericRequestCommand.getRequestText();
		this.ecp1Client = ecp1Client;
		this.requestState = RequestState.OPEN;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 */
	public int getRequestId() {
		return requestId;
	}

	/**
	 * Returns the text.
	 * 
	 * @return the text
	 */
	public String getRequestText() {
		return requestText;
	}

	/**
	 * Returns the type.
	 * 
	 * @return the type
	 */
	public RequestType getRequestType() {
		return requestType;
	}

	/**
	 * Sends a boolean answer (i.e. yes or no).
	 * 
	 * @param yes
	 *            <code>true</code> to answer with <i>YES</i>,
	 *            <code>false</code> to answer with <i>NO</i>
	 */
	public void sendYesNoAnswer(final boolean yes) {
		if (this.requestType != RequestType.YES_NO) {
			throw new UnsupportedOperationException("This Request is not a "
					+ RequestType.YES_NO + " it is a " + this.requestType + "!");
		}
		if (this.requestState != RequestState.CANCELED) {
			this.ecp1Client.addToOutQueue(new AnswerRequestCommand(
					this.requestId, this.requestType, yes));
			this.requestState = RequestState.ANSWERED;
		}
	}

	/**
	 * Sends a boolean answer (i.e. approve or decline).
	 * 
	 * @param ok
	 *            <code>true</code> to answer <i>OK</i>, <code>false</code to
	 *            answer with <i>CANCEL</i>
	 */
	public void sendOkCancelAnswer(final boolean ok) {
		if (this.requestType != RequestType.OK_CANCEL) {
			throw new UnsupportedOperationException("This Request is not a "
					+ RequestType.OK_CANCEL + " it is a " + this.requestType
					+ "!");
		}
		if (this.requestState != RequestState.CANCELED) {
			this.ecp1Client.addToOutQueue(new AnswerRequestCommand(
					this.requestId, this.requestType, ok));
			this.requestState = RequestState.ANSWERED;
		}
	}

	/**
	 * Sends a trigger answer.
	 * 
	 * @param ok
	 *            unused
	 */
	public void sendTriggerAnswer(final boolean ok) {
		if (this.requestType != RequestType.TRIGGER) {
			throw new UnsupportedOperationException("This Request is not a "
					+ RequestType.TRIGGER + " it is a " + this.requestType
					+ "!");
		}
		if (this.requestState != RequestState.CANCELED) {
			final int value = 5;
			this.ecp1Client.addToOutQueue(new AnswerRequestCommand(
					this.requestId, this.requestType, value));
			this.requestState = RequestState.ANSWERED;
		}
	}

	/**
	 * Sends a (32 bit) integer answer.
	 * 
	 * @param value
	 *            the integer to send
	 */
	public void sendInt32Answer(final int value) {
		if (this.requestType != RequestType.INT32) {
			throw new UnsupportedOperationException("This Request is not a "
					+ RequestType.INT32 + " it is a " + this.requestType + "!");
		}
		if (this.requestState != RequestState.CANCELED) {
			this.ecp1Client.addToOutQueue(new AnswerRequestCommand(
					this.requestId, value));
			this.requestState = RequestState.ANSWERED;
		}
	}

	/**
	 * Sends a (32 bit) floating point answer.
	 * 
	 * @param value
	 *            the float to send
	 */
	public void sendFloat32Answer(final float value) {
		if (this.requestType != RequestType.FLOAT32) {
			throw new UnsupportedOperationException("This Request is not a "
					+ RequestType.FLOAT32 + " it is a " + this.requestType
					+ "!");
		}
		if (this.requestState != RequestState.CANCELED) {
			this.ecp1Client.addToOutQueue(new AnswerRequestCommand(
					this.requestId, value));
			this.requestState = RequestState.ANSWERED;
		}
	}

	/**
	 * Sends a text answer.
	 * 
	 * @param text
	 *            the text to send
	 */
	public void sendTextAnswer(final String text) {
		if (this.requestType != RequestType.TEXT) {
			throw new UnsupportedOperationException("This Request is not a "
					+ RequestType.TEXT + " it is a " + this.requestType + "!");
		}
		if (this.requestState != RequestState.CANCELED) {
			this.ecp1Client.addToOutQueue(new AnswerRequestCommand(
					this.requestId, this.requestType, text));
			this.requestState = RequestState.ANSWERED;
		}
	}
}