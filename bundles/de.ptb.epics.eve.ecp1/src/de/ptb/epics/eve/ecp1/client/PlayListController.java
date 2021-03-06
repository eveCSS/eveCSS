package de.ptb.epics.eve.ecp1.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.ecp1.client.interfaces.INewXMLFileListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayListController;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayListListener;
import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;
import de.ptb.epics.eve.ecp1.commands.AddToPlayListCommand;
import de.ptb.epics.eve.ecp1.commands.AutoPlayCommand;
import de.ptb.epics.eve.ecp1.commands.CurrentXMLCommand;
import de.ptb.epics.eve.ecp1.commands.PlayListCommand;
import de.ptb.epics.eve.ecp1.commands.RemoveFromPlayListCommand;
import de.ptb.epics.eve.ecp1.commands.ReorderPlayListCommand;
import de.ptb.epics.eve.ecp1.commands.RepeatCountCommand;

public class PlayListController implements IPlayListController {

	private ECP1Client ecp1Client;
	private List<IPlayListListener> playListListener;
	private boolean autoplay;
	private List<PlayListEntry> playListEntries;
	private List<INewXMLFileListener> newXMLFileListener;

	protected PlayListController(final ECP1Client ecp1Client) {
		if (ecp1Client == null) {
			throw new IllegalArgumentException(
					"The parameter 'ecp1Client' must noch be null!");
		}
		this.ecp1Client = ecp1Client;
		this.autoplay = false;
		this.playListEntries = new ArrayList<PlayListEntry>();
		this.playListListener = new ArrayList<IPlayListListener>();
		this.newXMLFileListener = new ArrayList<INewXMLFileListener>();
	}

	public void addFromInputStream(final InputStream inputStream,
			final String name) throws IOException {
		if (inputStream == null) {
			throw new IllegalArgumentException(
					"The parameter 'inputStream' must not be null!");
		}
		if (name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final byte[] buffer = new byte[512];
		int read = inputStream.read(buffer);
		while (read != -1) {
			byteArrayOutputStream.write(buffer, 0, read);
			read = inputStream.read(buffer);
		}
		this.ecp1Client.addToOutQueue(new AddToPlayListCommand(name,
				this.ecp1Client.getLoginName(), byteArrayOutputStream
						.toByteArray()));
		byteArrayOutputStream.close();
	}

	public void addLocalFile(final File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException(
					"The parameter 'file' must not be null!");
		}
		this.addFromInputStream(new FileInputStream(file), file.getName());
	}

	public void addLocalFile(final String filename) throws IOException {
		if (filename == null) {
			throw new IllegalArgumentException(
					"The parameter 'filename' must not be null!");
		}
		this.addLocalFile(new File(filename));

	}

	public synchronized void addPlayListListener(final IPlayListListener playListListener) {
		this.playListListener.add(playListListener);
	}

	public void addNewXMLFileListener(
			final INewXMLFileListener newXMLFileListener) {
		this.newXMLFileListener.add(newXMLFileListener);
	}

	public void removeNewXMLFileListener(
			final INewXMLFileListener newXMLFileListener) {
		this.newXMLFileListener.remove(newXMLFileListener);
	}

	public void addXMLString(final String string) {
		throw new UnsupportedOperationException(
				"Function has not been implemented yet");
	}

	public List<PlayListEntry> getEntries() {
		return new ArrayList<PlayListEntry>(this.playListEntries);
	}

	public boolean isAutoplay() {
		return this.autoplay;
	}

	public void movePlayListEntry(final PlayListEntry playListEntry,
			final int steps) {
		this.ecp1Client.addToOutQueue(new ReorderPlayListCommand(playListEntry
				.getId(), steps));
	}

	public void removePlayListEntry(final PlayListEntry playListEntry) {
		this.ecp1Client.addToOutQueue(new RemoveFromPlayListCommand(
				playListEntry.getId()));
	}

	public synchronized void removePlayListListener(final IPlayListListener playListListener) {
		this.playListListener.remove(playListListener);
	}

	public void setAutoplay(final boolean autoplay) {
		final AutoPlayCommand autoPlayCommand = new AutoPlayCommand(autoplay);
		this.ecp1Client.addToOutQueue(autoPlayCommand);
	}

	protected void reportAutoplay(final boolean autoplay) {
		if (autoplay != this.autoplay) {
			this.autoplay = autoplay;
			final Iterator<IPlayListListener> it = this.playListListener
					.iterator();
			while (it.hasNext()) {
				// it.next().playListHasChanged( this );
				it.next().autoPlayHasChanged(this);
			}
		}
	}
	
	public void setRepeatCount(final int repeatCount) {
		final RepeatCountCommand repeatCountCommand = new RepeatCountCommand(
				repeatCount);
		this.ecp1Client.addToOutQueue(repeatCountCommand);
	}
	
	protected void reportCurrentXMLCommand(
			final CurrentXMLCommand currentXMLCommand) {
		final Iterator<INewXMLFileListener> it = this.newXMLFileListener
				.iterator();
		while (it.hasNext()) {
			it.next().newXMLFileReceived(currentXMLCommand.getXmlData());
		}
	}

	protected void reportPlayListCommand(final PlayListCommand playListCommand) {
		Iterator<de.ptb.epics.eve.ecp1.commands.PlayListEntry> it = playListCommand
				.iterator();
		this.playListEntries.clear();
		while (it.hasNext()) {
			this.playListEntries.add(new PlayListEntry(it.next()));
		}
		synchronized (this) {
			for (IPlayListListener listener : this.playListListener) {
				listener.playListHasChanged(this);
			}
		}
	}
}