package de.ptb.epics.eve.viewer.views.playlistview;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PlayListTableContentProvider implements IStructuredContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(final Object inputElement) {
		final List<PlayListEntry> entries = (List<PlayListEntry>)inputElement;
		return entries.toArray();
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
	public void inputChanged(final Viewer viewer, 
							 final Object oldInput, 
							 final Object newInput) {
	}
}