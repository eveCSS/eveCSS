package de.ptb.epics.eve.ecp1.intern;

public enum AcquisitionStatus {
	OK,
	TIMEOUT,
	HIGH_RETRY_COUNT;
	
	public static byte acquisitionStatusToByte( final AcquisitionStatus acquisitionStatus ) {
		switch( acquisitionStatus ) {
			case OK:
				return 0x01;
			case TIMEOUT:
				return 0x02;
			case HIGH_RETRY_COUNT:
				return 0x03;
		}
		return Byte.MAX_VALUE;
	}
	
	public static AcquisitionStatus byteToAcquisitionStatus( final byte theByte ) {
		switch( theByte ) {
			case 0x01:
				return AcquisitionStatus.OK;
			case 0x02:
				return AcquisitionStatus.TIMEOUT;
			case 0x03:
				return AcquisitionStatus.HIGH_RETRY_COUNT;
		}
		return null;
	}
}
