package de.ptb.epics.eve.ecp1.intern;

public enum ChainStatus {

	UNKNOWN,
	IDLE,
	STARTING_SM,
	EXECUTING_SM,
	SM_PAUSED,
	WAITING_FOR_MANUAL_TRIGGER,
	EXITING_SM,
	EXITING_CHAIN,
	STORAGE_DONE;
	
	public static byte chainStatusToByte( final ChainStatus chainStatus ) {
		switch( chainStatus ) {
			
			case IDLE:
				return 0x01;
	
			case STARTING_SM:
				return 0x02;
				
			case EXECUTING_SM:
				return 0x03;
			
			case SM_PAUSED:
				return 0x04;
			
			case WAITING_FOR_MANUAL_TRIGGER:
				return 0x05;
			
			case EXITING_SM:
				return 0x06;
				
			case EXITING_CHAIN:
				return 0x07;
				
			case STORAGE_DONE:
				return 0x08;
				
		}
		return Byte.MAX_VALUE;
	}
	
	public static ChainStatus byteToChainStatus( final byte theByte ) {
		switch( theByte ) {
			case 0x01:
				return ChainStatus.IDLE;
			case 0x02:
				return ChainStatus.STARTING_SM;
			case 0x03:
				return ChainStatus.EXECUTING_SM;
			case 0x04:
				return ChainStatus.SM_PAUSED;
			case 0x05:
				return ChainStatus.WAITING_FOR_MANUAL_TRIGGER;
			case 0x06:
				return ChainStatus.EXITING_SM;
			case 0x07:
				return ChainStatus.EXITING_CHAIN;
			case 0x08:
				return ChainStatus.STORAGE_DONE;
		}
		
		return ChainStatus.UNKNOWN;
	}
	
}
