package de.ptb.epics.eve.ecp1.client.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;

public interface IPlayListController {

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	void addLocalFile(final File file) throws IOException;

	/**
	 * 
	 * @param filename
	 * @throws IOException
	 */
	void addLocalFile(final String filename) throws IOException;

	/**
	 * 
	 * @param inputStream
	 * @param name
	 * @throws IOException
	 */
	void addFromInputStream(final InputStream inputStream,
			final String name) throws IOException;

	/**
	 * 
	 * @param playListEntry
	 */
	void removePlayListEntry(final PlayListEntry playListEntry);

	/**
	 * 
	 * @return
	 */
	boolean isAutoplay();

	/**
	 * 
	 * @param autoplay
	 */
	void setAutoplay(final boolean autoplay);

	/**
	 * 
	 * @param repeatCount
	 */
	void setRepeatCount(final int repeatCount);
	
	/**
	 * 
	 * @param playListListener
	 */
	void addPlayListListener(final IPlayListListener playListListener);

	/**
	 * 
	 * @param playListListener
	 */
	void removePlayListListener(final IPlayListListener playListListener);

	/**
	 * 
	 * @param playListEntry
	 * @param steps
	 */
	void movePlayListEntry(final PlayListEntry playListEntry,
			final int steps);

	/**
	 * 
	 * @return
	 */
	List<PlayListEntry> getEntries();

	/**
	 * 
	 * @param newXMLFileListener
	 */
	void addNewXMLFileListener(
			final INewXMLFileListener newXMLFileListener);

	/**
	 * 
	 * @param newXMLFileListener
	 */
	void removeNewXMLFileListener(
			final INewXMLFileListener newXMLFileListener);
}