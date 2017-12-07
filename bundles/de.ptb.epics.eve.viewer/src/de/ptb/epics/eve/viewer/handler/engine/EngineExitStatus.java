package de.ptb.epics.eve.viewer.handler.engine;

/**
 * @author Marcus Michalsky
 * @since 1.29
 */
public enum EngineExitStatus {
	/** exit due to external request */
	RECEIVED_SIGTERM(101),
	
	/** chosen tcp port is out of range */
	TCP_PORT_RANGE_VIOLATION(102),
	
	/** connection refused, port in use, etc. */
	TCP_CONNECTION_ERROR(103);
	
	private final int errorCode;
	
	EngineExitStatus(int errorCode) {
		this.errorCode = errorCode;
	}
	
	/**
	 * Returns the numerical error code
	 * @return the numerical error code
	 */
	int errorCode() {
		return this.errorCode;
	}
}