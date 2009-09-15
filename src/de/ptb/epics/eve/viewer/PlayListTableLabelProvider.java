package de.ptb.epics.eve.viewer;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;

public class PlayListTableLabelProvider implements ITableLabelProvider {

	public Image getColumnImage( final Object element, final int columnIndex ) {
		return null;
	}

	public String getColumnText( final Object element, final int columnIndex) {
		PlayListEntry entry = (PlayListEntry)element;
		if( columnIndex == 0 ) {
			return entry.getName();
		}
		return entry.getAuthor();
	}

	public void addListener( final ILabelProviderListener listener ) {
		
	}

	public void dispose() {
		
	}

	public boolean isLabelProperty( final Object element, final String property) {
		return false;
	}

	public void removeListener( final ILabelProviderListener listener) {
		
	}

}
