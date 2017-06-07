package de.ptb.epics.eve.ecp1.client.interfaces;

/**
 * @author ?
 * @since 1.0
 */
public interface IPlayListListener {
	
	/**
	 * 
	 * @param playListController
	 */
	void playListHasChanged(final IPlayListController playListController);

	/**
	 * 
	 * @param playListController
	 */
	void autoPlayHasChanged(final IPlayListController playListController);
}