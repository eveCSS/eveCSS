package de.ptb.epics.eve.ecp1.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;

import de.ptb.epics.eve.ecp1.commands.IECP1Command;

public class OutHandler implements Runnable {

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

	public void run() {

		while (keepRunning) {
			if (!outQueue.isEmpty()) {
				try {
					this.outputStream.write(outQueue.poll().getByteArray());
				} catch (final IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void quit() {
		this.keepRunning = false;
	}
}