package de.ptb.epics.eve.viewer.handler.engine;

/**
 * @author Marcus Michalsky
 * @since 1.29
 */
public class ExitStatus {
	/** terminated without errors */
	public static final int SUCCESS = 0;
	
	/** exit due to external request */
	public static final int RECEIVED_SIGTERM = 101;
	
	/** chosen tcp port is out of range */
	public static final int TCP_PORT_RANGE_VIOLATION = 102;
	
	/** connection refused, port in use, etc. */
	public static final int TCP_CONNECTION_ERROR = 103;
	
	private ExitStatus() {
	}
}
