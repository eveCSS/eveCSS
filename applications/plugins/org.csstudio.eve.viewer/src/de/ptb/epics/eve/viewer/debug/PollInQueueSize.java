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
	
	private boolean finished = false;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		while (!finished) {
			int queueSize = Activator.getDefault().getEcp1Client()
					.getInQueueSize();
			if (queueSize > 0) {
				LOGGER.debug("In Queue Size: " + queueSize);
			}
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