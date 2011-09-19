package de.ptb.epics.eve.ecp1.intern;

import java.io.IOException;

public interface IECP1Command {
	
	public static final int START_TAG = 0x0d0f0d0a;
	public static final char VERSION = 0x0100;
	public static final int PLAYLISTMAXENTRIES = 500;
	public static final String STRING_ENCODING = "UTF-16BE";
	public byte[] getByteArray() throws IOException;
	
}
