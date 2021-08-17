package de.ptb.epics.eve.ecp1.commands;

import java.io.IOException;

public interface IECP1Command {
	
	/**
	 * 
	 */
	int START_TAG = 0x0d0f0d0a;
	
	/**
	 * 
	 */
	char VERSION = 0x0400;
	
	/**
	 * 
	 */
	int PLAYLISTMAXENTRIES = 500;
	
	/**
	 * 
	 */
	String STRING_ENCODING = "UTF-16BE";
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	byte[] getByteArray() throws IOException;
}