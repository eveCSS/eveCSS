package de.ptb.epics.eve.ecp1.intern;

public enum DataModifier {

	UNMODIFIED,
	CENTER,
	EDGE,
	MIN,
	MAX,
	FWHM,
	MEAN_VALUE,
	STANDARD_DEVIATION,
	SUM;
	
	public static byte dataModifyerToByte( final DataModifier dataModifyer ) {
		switch( dataModifyer ) {
			case UNMODIFIED:
				return 0x00;
			case CENTER:
				return 0x01;
			case EDGE:
				return 0x02;
			case MIN:
				return 0x03;
			case MAX:
				return 0x04;
			case FWHM:
				return 0x05;
			case MEAN_VALUE:
				return 0x06;
			case STANDARD_DEVIATION:
				return 0x07;
			case SUM:
				return 0x08;
				
		}
		return Byte.MAX_VALUE;
	}
	
	public static DataModifier byteTotDataModifyer( final byte theByte ) {
		switch( theByte ) {
			case 0x00:
				return DataModifier.UNMODIFIED;
			case 0x01:
				return DataModifier.CENTER;
			case 0x02:
				return DataModifier.EDGE;
			case 0x03:
				return DataModifier.MIN;
			case 0x04:
				return DataModifier.MAX;
			case 0x05:
				return DataModifier.FWHM;
			case 0x06:
				return DataModifier.MEAN_VALUE;			
			case 0x07:
				return DataModifier.STANDARD_DEVIATION;
			case 0x08:
				return DataModifier.SUM;
		}
		return null;
	}
	
}
