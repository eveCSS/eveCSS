package de.ptb.epics.eve.editor.views;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

import de.ptb.epics.eve.util.collection.ListUtil;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public abstract class ViewerTableDropTargetListener extends ViewerDropAdapter {
	private TableViewer viewer;
	
	protected ViewerTableDropTargetListener(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		if (!TextTransfer.getInstance().isSupportedType(transferType) ||
				operation != DND.DROP_MOVE) {
			return false;
		}
		if (getLogger().isDebugEnabled()) {
			String targetName = getModelName(target);
			switch (this.getCurrentLocation()) {
			case ViewerDropAdapter.LOCATION_BEFORE:
				getLogger().debug("DnD before " + targetName);
				break;
			case ViewerDropAdapter.LOCATION_ON:
				getLogger().debug("DnD on " + targetName);
				break;
			case ViewerDropAdapter.LOCATION_AFTER:
				getLogger().debug("DnD after " + targetName);
				break;
			}
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performDrop(Object data) {
		Object target = this.getCurrentTarget();
		int location = this.getCurrentLocation();
		
		getLogger().debug("Drop data is: " + data.toString());
		
		Object dragItem = viewer.getTable().getSelection()[0].getData();
		int sourceIndex = getModel().indexOf(dragItem);
		int targetIndex = getModel().indexOf(target);
		
		if (sourceIndex == -1) {
			getLogger().error("drag item not found");
			return false;
		} else if (targetIndex == -1) {
			getLogger().error("target not found");
			return false;
		}
		
		getLogger().debug("perform drop: source index = " + sourceIndex + 
				", target index = " + targetIndex);
		
		if (location == ViewerDropAdapter.LOCATION_AFTER || 
				location == ViewerDropAdapter.LOCATION_ON) {
			getLogger().debug("perform drop: insert source after target");
			ListUtil.move(getModel(), sourceIndex, targetIndex);
		} else if (location == ViewerDropAdapter.LOCATION_BEFORE) {
			getLogger().debug("perform drop: insert source berfore target");
			ListUtil.move(getModel(), sourceIndex, targetIndex - 1);
		}
		return false;
	}

	/**
	 * Returns a logger instance logs should be written to.
	 * 
	 * @return a logger instance logs should be written to
	 */
	public abstract Logger getLogger();
	
	/**
	 * Returns a human readable name of the element used for drag and drop.
	 * 
	 * @param item the item to be moved
	 * @return a human readable name of the element used for drag and drop
	 */
	public abstract String getModelName(Object item);
	
	/**
	 * 
	 * @return
	 */
	public abstract List<? extends Object> getModel();
}
