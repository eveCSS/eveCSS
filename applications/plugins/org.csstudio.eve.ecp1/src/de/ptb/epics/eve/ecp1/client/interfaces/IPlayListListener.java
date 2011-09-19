package de.ptb.epics.eve.ecp1.client.interfaces;

public interface IPlayListListener {
	public void playListHasChanged( final IPlayListController playListController );
	public void autoPlayHasChanged( final IPlayListController playListController );
}
