package de.ptb.epics.eve.ecp1.intern;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.ptb.epics.eve.ecp1.intern.exceptions.AbstractRestoreECP1CommandException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongStartTagException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongTypeIdException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongVersionException;

public class GenericRequestCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0108;
	
	private int requestId;
	private RequestType requestType;
	private String requestText;
	
	public GenericRequestCommand( final int requestId, final RequestType requestType, final String requestText ) {
		if( requestText == null ) {
			throw new IllegalArgumentException( "The parameter 'requestText' must not be null!" );
		}
		this.requestId = requestId;
		this.requestType = requestType;
		this.requestText = requestText;
	}
	
	public GenericRequestCommand( final byte[] byteArray ) throws IOException, AbstractRestoreECP1CommandException {
		if( byteArray == null ) {
			throw new IllegalArgumentException( "The parameter 'byteArray' must not be null!" );
		}
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( byteArray );
		final DataInputStream dataInputStream = new DataInputStream( byteArrayInputStream );
		
		final int startTag = dataInputStream.readInt(); 
		if( startTag != IECP1Command.START_TAG ) {
			throw new WrongStartTagException( byteArray, startTag );
		}
		
		final char version = dataInputStream.readChar(); 
		if( version != IECP1Command.VERSION ) {
			throw new WrongVersionException( byteArray, version );
		}
		
		final char commandTypeID = dataInputStream.readChar();
		if( commandTypeID != GenericRequestCommand.COMMAND_TYPE_ID ) {
			throw new WrongTypeIdException( byteArray, commandTypeID, GenericRequestCommand.COMMAND_TYPE_ID );
		}
		
		final int length = dataInputStream.readInt();
		
		this.requestId = dataInputStream.readInt();
		dataInputStream.readChar();
		dataInputStream.readByte();
		this.requestType = RequestType.byteToRequestType( dataInputStream.readByte() );
		
		final int lengthOfRequestText = dataInputStream.readInt();
		
		if( lengthOfRequestText != 0xffffffff) {
			final byte[] requestTextBuffer = new byte[ lengthOfRequestText ];
			dataInputStream.readFully( requestTextBuffer );
			this.requestText = new String( requestTextBuffer, IECP1Command.STRING_ENCODING );
		} else {
			this.requestText = "";
		}
		
	}
	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream( byteArrayOutputStream );
		
		dataOutputStream.writeInt( IECP1Command.START_TAG );
		dataOutputStream.writeChar( IECP1Command.VERSION );;
		dataOutputStream.writeChar( GenericRequestCommand.COMMAND_TYPE_ID );
		
		final byte[] requestTextBuffer = this.requestText.getBytes( IECP1Command.STRING_ENCODING );
		
		dataOutputStream.writeInt( (this.requestText.length()!=0)?12+requestTextBuffer.length:12 );
		dataOutputStream.writeInt( this.requestId );
		dataOutputStream.writeChar( 0 );
		dataOutputStream.writeByte( 0 );
		
		dataOutputStream.writeByte( RequestType.requestTypeToByte( this.requestType ) );
		
		if( this.requestText.length() != 0 ) {
			dataOutputStream.writeInt( this.requestText.length() );
			dataOutputStream.write( requestTextBuffer );
		} else {
			dataOutputStream.writeInt( 0xffffffff );
		}
		
		dataOutputStream.close();
		
		return byteArrayOutputStream.toByteArray();
	}

	public int getRequestId() {
		return this.requestId;
	}

	public void setRequestId( final int requestId ) {
		this.requestId = requestId;
	}

	public String getRequestText() {
		return this.requestText;
	}

	public void setRequestText( final String requestText ) {
		if( requestText == null ) {
			throw new IllegalArgumentException( "The parameter 'requestText' must not be null!" );
		}
		this.requestText = requestText;
	}

	public RequestType getRequestType() {
		return this.requestType;
	}

	public void setRequestType( final RequestType requestType ) {
		this.requestType = requestType;
	}

	
}
