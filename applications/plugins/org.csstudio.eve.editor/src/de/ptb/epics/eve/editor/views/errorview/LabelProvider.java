package de.ptb.epics.eve.editor.views.errorview;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * <code>LabelProvider</code> is the label provider for the table viewer 
 * defined in the {@link de.ptb.epics.eve.editor.views.errorview.ErrorView}.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class LabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(columnIndex == 0) {
			return ((IModelError) element).getErrorName();
		} else if(columnIndex == 1) {
			return ((IModelError) element).getErrorMessage();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
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
	public void addListener(ILabelProviderListener listener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
	}
}