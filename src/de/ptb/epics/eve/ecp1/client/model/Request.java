package de.ptb.epics.eve.ecp1.client.model;

import de.ptb.epics.eve.ecp1.client.ECP1Client;
import de.ptb.epics.eve.ecp1.intern.AnswerRequestCommand;
import de.ptb.epics.eve.ecp1.intern.GenericRequestCommand;
import de.ptb.epics.eve.ecp1.intern.RequestType;

public class Request {
	
	private final int requestId;
	private final RequestType requestType;
	private final String requestText;
	private final ECP1Client ecp1Client;
	private RequestState requestState;
	
	public Request( final GenericRequestCommand genericRequestCommand, final ECP1Client ecp1Client ) {
		if( genericRequestCommand == null ) {
			throw new IllegalArgumentException( "The parameter 'genericRequestCommand' must not be null!" );
		}
		if( ecp1Client == null ) {
			throw new IllegalArgumentException( "The parameter 'ecp1Client' must not be null!" );
		}
		this.requestId = genericRequestCommand.getRequestId();
		this.requestType = genericRequestCommand.getRequestType();
		this.requestText = genericRequestCommand.getRequestText();
		this.ecp1Client = ecp1Client;
		this.requestState = RequestState.OPEN;
	}

	public int getRequestId() {
		return requestId;
	}

	public String getRequestText() {
		return requestText;
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public void sendYesNoAnswer( final boolean yes ) {
		if( this.requestType != RequestType.YES_NO ) {
			throw new UnsupportedOperationException( "This Request is not a " + RequestType.YES_NO + " it is a " + this.requestType + "!" );
		}
		if( this.requestState != RequestState.CANELED ) {
			this.ecp1Client.addToOutQueue( new AnswerRequestCommand( this.requestId, this.requestType, yes ) );
			this.requestState = RequestState.ANSWERED;
		}
	}
	
	public void sendOkCancelAnswer( final boolean ok ) {
		if( this.requestType != RequestType.OK_CANCEL ) {
			throw new UnsupportedOperationException( "This Request is not a " + RequestType.OK_CANCEL + " it is a " + this.requestType + "!" );
		}
		if( this.requestState != RequestState.CANELED ) {
			this.ecp1Client.addToOutQueue( new AnswerRequestCommand( this.requestId, this.requestType, ok ) );
			this.requestState = RequestState.ANSWERED;
		}
	}
	
	public void sendInt32Answer( final int value ) {
		if( this.requestType != RequestType.INT32 ) {
			throw new UnsupportedOperationException( "This Request is not a " + RequestType.INT32 + " it is a " + this.requestType + "!" );
		}
		if( this.requestState != RequestState.CANELED ) {
			this.ecp1Client.addToOutQueue( new AnswerRequestCommand( this.requestId, value ) );
			this.requestState = RequestState.ANSWERED;
		}
	}
	
	public void sendFloat32Answer( final float value ) {
		if( this.requestType != RequestType.FLOAT32 ) {
			throw new UnsupportedOperationException( "This Request is not a " + RequestType.FLOAT32 + " it is a " + this.requestType + "!" );
		}
		if( this.requestState != RequestState.CANELED ) {
			this.ecp1Client.addToOutQueue( new AnswerRequestCommand( this.requestId, value ) );
			this.requestState = RequestState.ANSWERED;
		}
	}
	
	public void sendTextAnswer( final String text ) {
		if( this.requestType != RequestType.TEXT ) {
			throw new UnsupportedOperationException( "This Request is not a " + RequestType.TEXT + " it is a " + this.requestType + "!" );
		}
		if( this.requestState != RequestState.CANELED ) {
			this.ecp1Client.addToOutQueue( new AnswerRequestCommand( this.requestId, this.requestType, text ) );
			this.requestState = RequestState.ANSWERED;
		}
	}
	
	public void cancelMessage() {
		this.requestState = RequestState.CANELED;
	}
}
