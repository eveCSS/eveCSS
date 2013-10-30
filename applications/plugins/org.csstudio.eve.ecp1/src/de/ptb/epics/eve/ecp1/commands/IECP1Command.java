package de.ptb.epics.eve.ecp1.commands;

import java.io.IOException;

public interface IECP1Command {
	
	/**
	 * 
	 */
	static final int START_TAG = 0x0d0f0d0a;
	
	/**
	 * 
	 */
	static final char VERSION = 0x0100;
	
	/**
	 * 
	 */
	static final int PLAYLISTMAXENTRIES = 500;
	
	/**
	 * 
	 */
	static final String STRING_ENCODING = "UTF-16BE";
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	byte[] getByteArray() throws IOException;
}