package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PlotContentProvider implements IStructuredContentProvider {

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
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return ((ScanModule)inputElement).getPlotWindows();
	}
}
