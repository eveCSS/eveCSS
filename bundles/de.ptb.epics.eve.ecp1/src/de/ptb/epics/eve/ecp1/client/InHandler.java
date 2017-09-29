package de.ptb.epics.eve.ecp1.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import org.apache.log4j.Logger;

/**
 * 
 * @author ?
 */
public class InHandler implements Runnable {
	public static final Logger LOGGER = Logger.getLogger(
			InHandler.class.getName());
	
	private ECP1Client ecp1client;
	private InputStream inputStream;
	private Queue<byte[]> inQueue;
	private boolean keepRunning = true;

	public InHandler(final ECP1Client ecp1client,
			final InputStream inputStream, final Queue<byte[]> inQueue) {
		this.ecp1client = ecp1client;
		this.inputStream = inputStream;
		this.inQueue = inQueue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		final byte[] headerBuffer = new byte[12];
		final DataInputStream dataInputStream = new DataInputStream(
				this.inputStream);

		while (this.keepRunning) {
			try {
				dataInputStream.readFully(headerBuffer);
				final ByteArrayInputStream byteArrayInputStream = 
						new ByteArrayInputStream(headerBuffer);
				final DataInputStream dataInputStreamForLength = 
						new DataInputStream(byteArrayInputStream);
				dataInputStreamForLength.skip(8);
				final int length = dataInputStreamForLength.readInt();

				dataInputStreamForLength.close();
				byteArrayInputStream.close();

				final byte[] contentBuffer = new byte[length];
				dataInputStream.readFully(contentBuffer);

				final byte[] dataPackage = new byte[length + 12];
				System.arraycopy(headerBuffer, 0, dataPackage, 0, 12);
				System.arraycopy(contentBuffer, 0, dataPackage, 12, length);

				this.inQueue.add(dataPackage);
			} catch (final IOException e) { // should use finally instead ?
				try {
					this.ecp1client.disconnect();
				} catch (final IOException e1) {
					LOGGER.error(e1.getMessage(), e1);
				}
			}
		}
	}

	public void quit() {
		this.keepRunning = false;
	}
}