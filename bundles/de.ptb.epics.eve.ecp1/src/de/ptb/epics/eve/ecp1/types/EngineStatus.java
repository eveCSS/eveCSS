package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public enum EngineStatus {
	
	/** engine has just been started or all current chains have been done */
	IDLE_NO_XML_LOADED,
	
	/** XML is loaded, but chains are not yet executing */
	IDLE_XML_LOADED,
	
	/** currently loading XML */
	LOADING_XML,
	
	/** at least one chain is executing (execution may be paused) */
	EXECUTING,
	
	/** pause has been activated */
	PAUSED,
	
	/** stop has been activated */
	STOPPED,
	
	/** halt has been activated */
	HALTED,
	
	/** Pause initiated by a user (pause button) */
	GUI_PAUSE,
	
	/** Pause initiated by an event used in a pause condition */
	CHAIN_PAUSE,
	
	/** engine is in an undefined state */
	INVALID;
	
	/**
	 * Returns the byte code corresponding to the given 
	 * {@link de.ptb.epics.eve.ecp1.types.EngineStatus}.
	 * 
	 * @param engineStatus the engine status
	 * @return the byte code corresponding to the given 
	 * 		{@link de.ptb.epics.eve.ecp1.types.EngineStatus}
	 */
	public static byte engineStatusToByte(final EngineStatus engineStatus) {
		switch (engineStatus) {
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
			case GUI_PAUSE:
				return 0x08;
			case CHAIN_PAUSE:
				return 0x09;
			case INVALID:
				return 0x00;
		}
		return 0x00;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.EngineStatus} 
	 * corresponding to the given byte code.
	 * 
	 * @param theByte the engine status as a byte code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.EngineStatus} 
	 *			corresponding to the given byte code
	 */
	public static EngineStatus byteToEngineStatus(final byte theByte) {
		switch (theByte) {
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
			case 0x08:
				return EngineStatus.GUI_PAUSE;
			case 0x09:
				return EngineStatus.CHAIN_PAUSE;
			default:
				return EngineStatus.INVALID;
		}
	}
}