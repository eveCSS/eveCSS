package de.ptb.epics.eve.viewer.debug;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.viewer.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.14
 */
public class PollInQueueSize implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(PollInQueueSize.class
			.getName());
	
	boolean finished = false;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		while (!finished) {
			LOGGER.debug("In Queue Size: " + Activator.getDefault().
					getEcp1Client().getInQueueSize());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				LOGGER.warn(e.getMessage(), e);
			}
		}
	}
	
	public void stop() {
		this.finished = true;
	}
}