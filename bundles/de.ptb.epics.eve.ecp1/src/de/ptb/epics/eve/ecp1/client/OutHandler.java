package de.ptb.epics.eve.ecp1.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.ecp1.commands.IECP1Command;

/**
 * 
 * @author ?
 */
public class OutHandler implements Runnable {
	public static final Logger LOGGER = Logger.getLogger(
			OutHandler.class.getName());
	
	private OutputStream outputStream;
	private Queue<IECP1Command> outQueue;
	private boolean keepRunning;

	public OutHandler(final OutputStream outputStream,
			Queue<IECP1Command> outQueue) {
		super();
		this.outputStream = outputStream;
		this.outQueue = outQueue;
		this.keepRunning = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		while (keepRunning) {
			if (!outQueue.isEmpty()) {
				try {
					this.outputStream.write(outQueue.poll().getByteArray());
				} catch (final IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			} else {
				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	public void quit() {
		this.keepRunning = false;
	}
}