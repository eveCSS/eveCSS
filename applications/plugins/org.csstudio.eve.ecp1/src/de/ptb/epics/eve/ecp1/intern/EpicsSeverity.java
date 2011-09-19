package de.ptb.epics.eve.ecp1.intern;

public enum EpicsSeverity {

	NO_ALARM,
	MINOR_ALARM,
	MAJOR_ALARM,
	INVALID_ALARM;
	
	public static byte epicsSeverityToByte( final EpicsSeverity epicsServerity ) {
		switch( epicsServerity ) {
			case NO_ALARM:
				return 0x01;
				
			case MINOR_ALARM:
				return 0x02;
				
			case MAJOR_ALARM:
				return 0x03;
				
			case INVALID_ALARM:
				return 0x04;
		}
		return Byte.MAX_VALUE;
	}
	
	public static EpicsSeverity byteToEpicsSeverity( final byte theByte ) {
		
		switch( theByte ) {
			case 0x01:
				return EpicsSeverity.NO_ALARM;
			case 0x02:
				return EpicsSeverity.MINOR_ALARM;
			case 0x03:
				return EpicsSeverity.MAJOR_ALARM;
			case 0x04:
				return EpicsSeverity.INVALID_ALARM;
		}
		
		return null;
		
	}
	
}
