package de.ptb.epics.eve.ecp1.client.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;

public interface IPlayListController {
	
	public void addLocalFile( final File file ) throws IOException;
	public void addLocalFile( final String filename ) throws IOException;
	public void addFromInputStream( final InputStream inputStream, final String name ) throws IOException;
	public void removePlayListEntry( final PlayListEntry playListEntry );
	public boolean isAutoplay();
	public void setAutoplay( final boolean autoplay );
	public void addPlayListListener( final IPlayListListener playListListener );
	public void removePlayListListener( final IPlayListListener playListListener );
	public void movePlayListEntry( final PlayListEntry playListEntry, final int steps );
	public List< PlayListEntry > getEntries();
	
	public void addNewXMLFileListener( final INewXMLFileListener newXMLFileListener );
	
	public void removeNewXMLFileListener( final INewXMLFileListener newXMLFileListener );
	
}
