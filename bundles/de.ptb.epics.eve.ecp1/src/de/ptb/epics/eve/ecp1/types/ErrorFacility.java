package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public enum ErrorFacility {

	/** */
	UNKNOWN,
	
	/** message filter */
	MFILTER,
	
	/** command parser */
	CPARSER,
	
	/** network server */
	NETWORK,
	
	/** */
	MHUB,
	
	/** */
	PLAYLIST,
	
	/** */
	MANAGER,
	
	/** */
	XMLPARSE,
	
	/** */
	SC_CHAIN,
	
	/** */
	POSCALC,
	
	/** */
	SMDEVICE,
	
	/** */
	CA_TRANS,

	/** */
	SCANMOD,
	
	/** */
	STORAGE,

	/** */
	EVENT,
	
	/** */
	LTIMER,
	
	/** */
	MATH,
	
	/** */
	XMLVALIDATOR;
	
	/**
	 * Returns the byte code of the given error facility.
	 * 
	 * @param errorFacility the error facility
	 * @return the byte code of the given error facility
	 */
	public static byte errorFacilityToByte(final ErrorFacility errorFacility) {
		switch (errorFacility) {
			case UNKNOWN:
				return 0x00;
			case MFILTER:
				return 0x09;
			case CPARSER:
				return 0x0a;
			case NETWORK:
				return 0x0b;
			case MHUB:
				return 0x0c;
			case PLAYLIST:
				return 0x0d;
			case MANAGER:
				return 0x0e;
			case XMLPARSE:
				return 0x0f;
			case SC_CHAIN:
				return 0x10;
			case POSCALC:
				return 0x11;
			case SMDEVICE:
				return 0x12;
			case CA_TRANS:
				return 0x13;
			case SCANMOD:
				return 0x14;
			case STORAGE:
				return 0x15;
			case EVENT:
				return 0x16;
			case LTIMER:
				return 0x17;
			case MATH:
				return 0x18;
			case XMLVALIDATOR:
				return 0x19;
		}
		return Byte.MAX_VALUE;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.ErrorFacility} 
	 * corresponding to the given byte code.
	 * 
	 * @param theByte the error facility as a byte code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.ErrorFacility} 
	 * 		corresponding to the given byte code
	 */
	public static ErrorFacility byteToErrorFacility(final byte theByte) {
		switch (theByte) {
			case 0x00:
				return ErrorFacility.UNKNOWN;
			case 0x09:
				return ErrorFacility.MFILTER;
			case 0x0a:
				return ErrorFacility.CPARSER;
			case 0x0b:
				return ErrorFacility.NETWORK;
			case 0x0c:
				return ErrorFacility.MHUB;
			case 0x0d:
				return ErrorFacility.PLAYLIST;
			case 0x0e:
				return ErrorFacility.MANAGER;
			case 0x0f:
				return ErrorFacility.XMLPARSE;
			case 0x10:
				return ErrorFacility.SC_CHAIN;
			case 0x11:
				return ErrorFacility.POSCALC;
			case 0x12:
				return ErrorFacility.SMDEVICE;
			case 0x13:
				return ErrorFacility.CA_TRANS;
			case 0x14:
				return ErrorFacility.SCANMOD;
			case 0x15:
				return ErrorFacility.STORAGE;
			case 0x16:
				return ErrorFacility.EVENT;
			case 0x17:
				return ErrorFacility.LTIMER;
			case 0x18:
				return ErrorFacility.MATH;
			case 0x19:
				return ErrorFacility.XMLVALIDATOR;
		}
		return null;
	}
}