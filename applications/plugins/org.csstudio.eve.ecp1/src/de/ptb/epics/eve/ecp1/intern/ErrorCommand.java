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

public class ErrorCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0101;
	
	private int gerenalTimeStamp;
	private int nanoseconds;
	private ErrorSeverity errorSeverity;
	private ErrorFacility errorFacility;
	private ErrorType errorType;
	private String text;
	
	public ErrorCommand( final int generalTimeStamp, final int nanoseconds, final ErrorSeverity errorSeverity, final ErrorFacility errorFacility, final ErrorType errorType, final String text ) {
		this.gerenalTimeStamp = generalTimeStamp;
		this.nanoseconds = nanoseconds;
		this.errorSeverity = errorSeverity;
		this.errorFacility = errorFacility;
		this.errorType = errorType;
		this.text = text;
	}
	
	public ErrorCommand( final byte[] byteArray ) throws IOException, AbstractRestoreECP1CommandException {
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
		if( commandTypeID != ErrorCommand.COMMAND_TYPE_ID ) {
			throw new WrongTypeIdException( byteArray, commandTypeID, ErrorCommand.COMMAND_TYPE_ID );
		}
		
		final int length = dataInputStream.readInt();
		this.gerenalTimeStamp = dataInputStream.readInt();
		this.nanoseconds = dataInputStream.readInt();
		this.errorSeverity = ErrorSeverity.byteToErrorSeverity( dataInputStream.readByte() );
		this.errorFacility = ErrorFacility.byteToErrorFacility( dataInputStream.readByte() );
		this.errorType = ErrorType.charToErrorType( dataInputStream.readChar() );
		
		final int textLength = dataInputStream.readInt();
		if( textLength != 0xffffffff ) {
			final byte[] textBuffer = new byte[ textLength ];
			dataInputStream.readFully( textBuffer );
			this.text = new String( textBuffer, IECP1Command.STRING_ENCODING );
		} else {
			this.text = "";
		}
		
		
	}
	
	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream( byteArrayOutputStream );
		
		dataOutputStream.writeInt( IECP1Command.START_TAG );
		dataOutputStream.writeChar( IECP1Command.VERSION );;
		dataOutputStream.writeChar( ErrorCommand.COMMAND_TYPE_ID );
		final byte[] textBuffer = this.text.getBytes( IECP1Command.STRING_ENCODING );
		if( this.text.length() != 0 ) {
			dataOutputStream.writeInt( 16 + textBuffer.length );
		} else {
			dataOutputStream.writeInt( 16 );
		}
		
		dataOutputStream.writeInt( this.gerenalTimeStamp );
		dataOutputStream.writeInt( this.nanoseconds );
		dataOutputStream.writeByte( ErrorSeverity.errorSeverityToByte( this.errorSeverity ) );
		dataOutputStream.writeByte( ErrorFacility.errorFacilityToByte( this.errorFacility ) );
		dataOutputStream.writeChar( ErrorType.errorTypeToChar( this.errorType ) );
		
		if( this.text.length() != 0 ) {
			dataOutputStream.writeInt( textBuffer.length );
			dataOutputStream.write( textBuffer );
		} else {
			dataOutputStream.writeInt( 0xffffffff );
		}
		
		dataOutputStream.close();
		
		return byteArrayOutputStream.toByteArray();
	}

	public ErrorFacility getErrorFacility() {
		return this.errorFacility;
	}

	public void setErrorFacility( final ErrorFacility errorFacility ) {
		this.errorFacility = errorFacility;
	}

	public ErrorSeverity getErrorSeverity() {
		return this.errorSeverity;
	}

	public void setErrorSeverity( final ErrorSeverity errorSeverity ) {
		this.errorSeverity = errorSeverity;
	}

	public ErrorType getErrorType() {
		return this.errorType;
	}

	public void setErrorType( final ErrorType errorType ) {
		this.errorType = errorType;
	}

	public String getText() {
		return this.text;
	}

	public void setText( final String text ) {
		this.text = text;
	}

	public int getGerenalTimeStamp() {
		return this.gerenalTimeStamp;
	}

	public void setGerenalTimeStamp( final int gerenalTimeStamp ) {
		this.gerenalTimeStamp = gerenalTimeStamp;
	}

	public int getNanoseconds() {
		return this.nanoseconds;
	}

	public void setNanoseconds( final int nanoseconds ) {
		this.nanoseconds = nanoseconds;
	}

	

	
	
}
