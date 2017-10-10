package de.ptb.epics.eve.ecp1.types;

/**
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public enum ErrorType {

	/** */
	NETWORK_ERROR,
	
	/** */
	FEATURE_NOT_IMPLEMENTED,
	
	/** */
	UNABLE_TO_EXECUTE_COMMAND,
	
	/** */
	UNABLE_TO_LOAD_PLUGIN,
	
	/** */
	XML_SYNTAX_ERROR,
	
	/** */
	MESSAGEFILTER_LOW_LIMIT,
	
	/** */
	MESSAGEFILTER_HIGH_LIMIT,
	
	/** */
	MALFORMED_COMMAND,
	
	/** */
	TIMEOUT,
	
	/** */
	FILENAME,
	
	/** 
	 * @since 1.10
	 */
	MAX_POS_COUNT;
	
	/**
	 * Returns the character code of the given 
	 * {@link de.ptb.epics.eve.ecp1.types.ErrorType}.
	 * 
	 * @param errorType the error type
	 * @return the character code of the given 
	 * 			{@link de.ptb.epics.eve.ecp1.types.ErrorType}
	 */
	public static char errorTypeToChar(final ErrorType errorType) {
		switch (errorType) {
			case NETWORK_ERROR:
				return 0x0001;
			case FEATURE_NOT_IMPLEMENTED:
				return 0x0002;
			case UNABLE_TO_EXECUTE_COMMAND:
				return 0x0003;
			case UNABLE_TO_LOAD_PLUGIN:
				return 0x0004;
			case XML_SYNTAX_ERROR:
				return 0x0005;
			case MESSAGEFILTER_LOW_LIMIT:
				return 0x0006;
			case MESSAGEFILTER_HIGH_LIMIT:
				return 0x0007;
			case MALFORMED_COMMAND:
				return 0x0008;
			case TIMEOUT:
				return 0x0009;
			case FILENAME:
				return 0x000A;
			case MAX_POS_COUNT:
				return 0x000B;
		}
		return Character.MAX_VALUE;
	}

	/**
	 * Returns the {@link de.ptb.epics.eve.ecp1.types.ErrorType} of the given 
	 * character code.
	 * 
	 * @param theChar the error type as a character code
	 * @return the {@link de.ptb.epics.eve.ecp1.types.ErrorType} of the given 
	 * 			character code
	 */
	public static ErrorType charToErrorType(final char theChar) {
		switch (theChar) {
			case 0x0001:
				return ErrorType.NETWORK_ERROR;
			case 0x0002:
				return ErrorType.FEATURE_NOT_IMPLEMENTED;
			case 0x0003:
				return ErrorType.UNABLE_TO_EXECUTE_COMMAND;
			case 0x0004:
				return ErrorType.UNABLE_TO_LOAD_PLUGIN;
			case 0x0005:
				return ErrorType.XML_SYNTAX_ERROR;
			case 0x0006:
				return ErrorType.MESSAGEFILTER_LOW_LIMIT;
			case 0x0007:
				return ErrorType.MESSAGEFILTER_HIGH_LIMIT;
			case 0x0008:
				return ErrorType.MALFORMED_COMMAND;
			case 0x0009:
				return ErrorType.TIMEOUT;
			case 0x000A:
				return ErrorType.FILENAME;
			case 0x000B:
				return ErrorType.MAX_POS_COUNT;
		}
		return null;
	}
}