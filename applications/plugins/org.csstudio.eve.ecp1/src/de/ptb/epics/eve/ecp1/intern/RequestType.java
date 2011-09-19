package de.ptb.epics.eve.ecp1.intern;

public enum RequestType {
	YES_NO,
	OK_CANCEL,
	INT32,
	FLOAT32,
	TEXT,
	ERROR_TEXT,
	TRIGGER;
	
	public static byte requestTypeToByte( final RequestType requestType ) {
		switch( requestType ) {
			case YES_NO:
				return 0x00;
				
			case OK_CANCEL:
				return 0x01;
				
			case INT32:
				return 0x02;
				
			case FLOAT32:
				return 0x03;
				
			case TEXT:
				return 0x04;
				
			case ERROR_TEXT:
				return 0x05;

			case TRIGGER:
				return 0x10;
		}
		return Byte.MAX_VALUE; 
	}
	
	public static RequestType byteToRequestType( final byte theByte ) {
		switch( theByte ) {
			case 0x00:
				return RequestType.YES_NO;
			case 0x01:
				return RequestType.OK_CANCEL;
			case 0x02:
				return RequestType.INT32;
			case 0x03:
				return RequestType.FLOAT32;
			case 0x04:
				return RequestType.TEXT;
			case 0x05:
				return RequestType.ERROR_TEXT;
			case 0x10:
				return RequestType.TRIGGER;
		}
		return null;
	}

}
