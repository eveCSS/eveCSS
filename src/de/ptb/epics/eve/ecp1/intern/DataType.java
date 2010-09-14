package de.ptb.epics.eve.ecp1.intern;

public enum DataType {
	INT8,
	INT32,
	INT16,
	DOUBLE,
	FLOAT,
	STRING,
	DATETIME;
	
	public static byte dataTypeToByte( final DataType dataType ) {
		switch( dataType ) {
			case INT8:
				return 0x00;
				
			case INT32:
				return 0x05;
			
			case INT16:
				return 0x02;
				
			case DOUBLE:
				return 0x08;
				
			case FLOAT:
				return 0x07;
				
			case STRING :
				return 0x09;

			case DATETIME :
				return 0x0A;
		}
		return Byte.MAX_VALUE;
	}
	
	public static DataType byteTotDataType( final byte theByte ) {
		switch( theByte ) {
			case 0x00:
				return DataType.INT8;
				
			case 0x05:
				return DataType.INT32;
				
			case 0x02:
				return DataType.INT16;
				
			case 0x08:
				return DataType.DOUBLE;
				
			case 0x07:
				return DataType.FLOAT;
				
			case 0x09:
				return DataType.STRING;

			case 0x0A:
				return DataType.DATETIME;
}
		
		return null;
	}
}
