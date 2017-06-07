package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * TableViewer used in ActionComposites should use a label provider extended from this 
 * class. A common image for a delete icon and error indication are made available.
 * {@link #getColumnImage(Object, int)} and {@link #getColumnText(Object, int)}.have to 
 * be implemented. 
 * 
 * @author Marcus Michalsky
 * @since 1.28.10
 */
public abstract class ActionCompositeLabelProvider implements ITableLabelProvider {
	private static Image DELETE_IMG = PlatformUI.getWorkbench().getSharedImages().
			getImage(ISharedImages.IMG_TOOL_DELETE);
	private static Image ERROR_IMG = PlatformUI.getWorkbench().getSharedImages().
			getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
	
	/**
	 * Returns the image that should be used by subclasses as label image in a delete column
	 * @return the delete image
	 * @since 1.28.10
	 */
	protected static Image getDeleteImage() {
		return ActionCompositeLabelProvider.DELETE_IMG;
	}

	/**
	 * Returns the image that should be used by subclasses to indicate errors
	 * @return the error image
	 * @since 1.28.10
	 */
	protected static Image getErrorImage() {
		return ActionCompositeLabelProvider.ERROR_IMG;
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
}