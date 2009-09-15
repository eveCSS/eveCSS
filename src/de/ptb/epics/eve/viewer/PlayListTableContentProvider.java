package de.ptb.epics.eve.viewer;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;

public class PlayListTableContentProvider implements IStructuredContentProvider {

	public Object[] getElements( final Object inputElement ) {
		final List< PlayListEntry > entries = (List< PlayListEntry >)inputElement;
		return entries.toArray();
	}

	public void dispose() {

	}

	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput ) {

	}

}
