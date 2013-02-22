package de.ptb.epics.eve.editor.views.motoraxisview.filecomposite;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.util.math.statistics.DescriptiveStats;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class FileNameTableContentProvider implements IStructuredContentProvider{
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<DescriptiveStats>)inputElement).toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}
}