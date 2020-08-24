package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public abstract class CommonLabelProvider implements ITableLabelProvider {
	protected static final Image DELETE_IMG = PlatformUI.getWorkbench().
			getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
	protected static final Image ERROR_IMG = PlatformUI.getWorkbench().
			getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
	protected static final Image INFO_IMG = PlatformUI.getWorkbench().
			getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);

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
	public boolean isLabelProperty(Object element, String property) {
		return false;
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