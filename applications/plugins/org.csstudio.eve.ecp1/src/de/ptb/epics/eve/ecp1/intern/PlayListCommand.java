 package de.ptb.epics.eve.ecp1.intern;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.ecp1.intern.exceptions.AbstractRestoreECP1CommandException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongStartTagException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongTypeIdException;
import de.ptb.epics.eve.ecp1.intern.exceptions.WrongVersionException;

public class PlayListCommand implements IECP1Command {

	public static final char COMMAND_TYPE_ID = 0x0301;
	
	private List< PlayListEntry > entries;
	
	public PlayListCommand() {
		this.entries = new ArrayList< PlayListEntry >();
	}
	
	public PlayListCommand( final byte[] byteArray ) throws IOException, AbstractRestoreECP1CommandException {
		if( byteArray == null ) {
			throw new IllegalArgumentException( "The parameter 'byteArray' must not be null!" );
		}
		this.entries = new ArrayList< PlayListEntry >();
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
		if( commandTypeID != PlayListCommand.COMMAND_TYPE_ID ) {
			throw new WrongTypeIdException( byteArray, commandTypeID, PlayListCommand.COMMAND_TYPE_ID );
		}
		
		final int length = dataInputStream.readInt();
		final int amount = dataInputStream.readInt();
		for( int i = 0; i < amount; ++i ) {
			final int id = dataInputStream.readInt();
			String name = "";
			String author = "";
			final int namelength = dataInputStream.readInt();
			if( namelength != 0xffffffff ) {
				final byte[] bytes = new byte[ namelength ];
				dataInputStream.readFully( bytes );
				name = new String( bytes, IECP1Command.STRING_ENCODING );
			}
			final int authorlength = dataInputStream.readInt();
			if( authorlength != 0xffffffff ) {
				final byte[] bytes = new byte[ authorlength ];
				dataInputStream.readFully( bytes );
				author = new String( bytes, IECP1Command.STRING_ENCODING );
			}
			this.entries.add( new PlayListEntry( id, name, author ) );
		}
		
	}
	
	public byte[] getByteArray() throws IOException {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final DataOutputStream dataOutputStream = new DataOutputStream( byteArrayOutputStream );
		
		dataOutputStream.writeInt( IECP1Command.START_TAG );
		dataOutputStream.writeChar( IECP1Command.VERSION );;
		dataOutputStream.writeChar( PlayListCommand.COMMAND_TYPE_ID );
		int length = 4;
		final byte[][] entriesBytes = new byte[this.entries.size()][];
		for( int i = 0; i < entriesBytes.length; ++i ) {
			entriesBytes[ i ] = entries.get( i ).toByteArray();
			length += entriesBytes[ i ].length;
		}
		
		dataOutputStream.writeInt( length );
		dataOutputStream.writeInt( this.entries.size() );
		
		for( int i = 0; i < entriesBytes.length; ++i ) {
			dataOutputStream.write( entriesBytes[i] );
		}
		
		dataOutputStream.close();
		
		return byteArrayOutputStream.toByteArray();
	}

	public boolean add(PlayListEntry arg0) {
		return entries.add(arg0);
	}

	public void clear() {
		entries.clear();
	}

	public Iterator<PlayListEntry> iterator() {
		return entries.iterator();
	}

	public boolean remove(Object arg0) {
		return entries.remove(arg0);
	}

	
	
	
}
