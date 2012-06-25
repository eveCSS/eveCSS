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
	public void playListHasChanged(final IPlayListController playListController);

	/**
	 * 
	 * @param playListController
	 */
	public void autoPlayHasChanged(final IPlayListController playListController);
}