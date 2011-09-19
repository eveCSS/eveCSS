package de.ptb.epics.eve.ecp1.intern;

public enum EngineStatus {
	IDLE_NO_XML_LOADED,
	IDLE_XML_LOADED,
	LOADING_XML,
	EXECUTING,
	PAUSED,
	STOPPED,
	HALTED,
	INVALID;
	
	public static byte engineStatusToByte( final EngineStatus engineStatus ) {
		switch( engineStatus ) {
			case IDLE_NO_XML_LOADED:
				return 0x01;
			case IDLE_XML_LOADED:
				return 0x02;
			case LOADING_XML:
				return 0x03;
			case EXECUTING:
				return 0x04;
			case PAUSED:
				return 0x05;
			case STOPPED:
				return 0x06;
			case HALTED:
				return 0x07;
		}
		return 0x00;
	}
	
	public static EngineStatus byteToEngineStatus( final byte theByte ) {
		switch( theByte ) {
			case 0x01:
				return EngineStatus.IDLE_NO_XML_LOADED;
			case 0x02:
				return EngineStatus.IDLE_XML_LOADED;
			case 0x03:
				return EngineStatus.LOADING_XML;
			case 0x04:
				return EngineStatus.EXECUTING;
			case 0x05:
				return EngineStatus.PAUSED;
			case 0x06:
				return EngineStatus.STOPPED;
			case 0x07:
				return EngineStatus.HALTED;	
		}
		return EngineStatus.INVALID;
	}
}
