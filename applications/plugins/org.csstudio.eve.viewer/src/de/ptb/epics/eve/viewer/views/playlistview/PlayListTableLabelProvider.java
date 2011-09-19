package de.ptb.epics.eve.viewer.views.playlistview;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;

/**
 * <code>PlayListTableLabelProvider</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PlayListTableLabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText( final Object element, final int columnIndex) {
		PlayListEntry entry = (PlayListEntry)element;
		if( columnIndex == 0 ) {
			return entry.getName();
		}
		return entry.getAuthor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(final ILabelProviderListener listener) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(final ILabelProviderListener listener) {
	}
}