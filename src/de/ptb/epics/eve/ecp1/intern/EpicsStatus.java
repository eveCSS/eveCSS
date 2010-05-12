package de.ptb.epics.eve.ecp1.intern;

public enum EpicsStatus {

	OK,
	WARNING,
	ERROR,
	FATAL;
	
	public static byte epicsStatusToByte( final EpicsStatus epicsStatus ) {
		
		switch( epicsStatus ) {
			case OK:
				return 0x01;
			case WARNING:
				return 0x02;
			case ERROR:
				return 0x03;
			case FATAL:
				return 0x04;
		}
		
		return Byte.MAX_VALUE;
	}
	
	public static EpicsStatus byteToEpicsStatus( final byte theByte ) {
		
		switch( theByte ) {
			case 0x01:
				return EpicsStatus.OK;
			case 0x02:
				return EpicsStatus.WARNING;
			case 0x03:
				return EpicsStatus.ERROR;
			case 0x04:
				return EpicsStatus.FATAL;
		}
		
		return null;
	}
	
}
