package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public enum AcquisitionStatus {
	
	/** */
	OK, 
	
	/** */
	TIMEOUT, 
	
	/** */
	HIGH_RETRY_COUNT;

	/**
	 * Returns the byte code of the corresponding 
	 * {@link de.ptb.epics.eve.ecp1.types.AcquisitionStatus}.
	 * 
	 * @param acquisitionStatus the acquisition status
	 * @return the byte code of the corresponding 
	 * 			{@link de.ptb.epics.eve.ecp1.types.AcquisitionStatus}
	 */
	public static byte acquisitionStatusToByte(
			final AcquisitionStatus acquisitionStatus) {
		switch (acquisitionStatus) {
		case OK:
			return 0x01;
		case TIMEOUT:
			return 0x02;
		case HIGH_RETRY_COUNT:
			return 0x03;
		}
		return Byte.MAX_VALUE;
	}

	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.AcquisitionStatus} 
	 * corresponding to the given byte code.
	 * 
	 * @param theByte the acquisition status as a byte code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.AcquisitionStatus} 
	 * 			corresponding to the given byte code
	 */
	public static AcquisitionStatus byteToAcquisitionStatus(final byte theByte) {
		switch (theByte) {
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