package de.ptb.epics.eve.ecp1.client;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

/**
 * @author Marcus Michalsky
 * @since 1.25.2
 */
public class ThreadExceptionHandler implements UncaughtExceptionHandler {
	private static final Logger LOGGER = Logger.getLogger(
			ThreadExceptionHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LOGGER.error(t.getName() + " (" + t.getState() + ") : " + 
				e.getMessage(), e);
	}
}