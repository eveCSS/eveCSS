package de.ptb.epics.eve.ecp1.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.ptb.epics.eve.ecp1.intern.exceptions.AbstractRestoreECP1CommandException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongLengthException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongStartTagException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongTypeIdException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongVersionException;

public class ReorderPlayListCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0203;
	
	private int playListId;
	private int steps;
	
	public ReorderPlayListCommand( final int playListId, final int steps ) {
		this.playListId = playListId;
		this.steps = steps;
	}
	
	public ReorderPlayListCommand( final byte[] byteArray ) throws IOException, AbstractRestoreECP1CommandException {
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
		if( commandTypeID != ReorderPlayListCommand.COMMAND_TYPE_ID ) {
			throw new WrongTypeIdException( byteArray, commandTypeID, ReorderPlayListCommand.COMMAND_TYPE_ID );
		}
		
		final int length = dataInputStream.readInt();
		if( length != 8 ) {
			throw new WrongLengthException( byteArray, length, 8 );
		}
		
		this.playListId = dataInputStream.readInt();
		this.steps = dataInputStream.readInt();
		
	}
	
	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream( byteArrayOutputStream );
		
		dataOutputStream.writeInt( IECP1Command.START_TAG );
		dataOutputStream.writeChar( IECP1Command.VERSION );;
		dataOutputStream.writeChar( ReorderPlayListCommand.COMMAND_TYPE_ID );
		
		dataOutputStream.writeInt( 8 );
		dataOutputStream.writeInt( this.playListId );
		dataOutputStream.writeInt( this.steps );
		
		dataOutputStream.close();
		
		return byteArrayOutputStream.toByteArray();
	}

	public int getPlayListId() {
		return this.playListId;
	}

	public void setPlayListId(int playListId) {
		this.playListId = playListId;
	}

	public int getSteps() {
		return this.steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	
	
}
