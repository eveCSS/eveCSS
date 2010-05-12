package de.ptb.epics.eve.viewer;

import de.ptb.epics.eve.ecp1.intern.ErrorFacility;

public enum MessageSource {
	VIEWER,
	UNKNOWN,
	MFILTER,
	CPARSER,
	NETWORK,
	MHUB,
	PLAYLIST,
	MANAGER,
	XMLPARSE,
	SC_CHAIN,
	POSCALC,
	SMDEVICE,
	CA_TRANS,
	SCANMOD,
	STORAGE,
	EVENT,
	LTIMER;

	public static MessageSource convertFromErrorFacility(ErrorFacility errorFacility) {
		
		switch( errorFacility ) {
		
		case MFILTER:
			return MessageSource.MFILTER;
		case CPARSER:
			return MessageSource.CPARSER;
		case NETWORK:
			return MessageSource.NETWORK;			
		case MHUB:
			return MessageSource.MHUB;
		case PLAYLIST:
			return MessageSource.PLAYLIST;	
		case MANAGER:
			return MessageSource.MANAGER;	
		case XMLPARSE:
			return MessageSource.XMLPARSE;	
		case SC_CHAIN:
			return MessageSource.SC_CHAIN;	
		case POSCALC:
			return MessageSource.POSCALC;	
		case SMDEVICE:
			return MessageSource.SMDEVICE;	
		case CA_TRANS:
			return MessageSource.CA_TRANS;	
		case SCANMOD:
			return MessageSource.SCANMOD;	
		case STORAGE:
			return MessageSource.STORAGE;	
		case EVENT:
			return MessageSource.EVENT;	
		case LTIMER:
			return MessageSource.LTIMER;	
		}
		return UNKNOWN;
	}
}
