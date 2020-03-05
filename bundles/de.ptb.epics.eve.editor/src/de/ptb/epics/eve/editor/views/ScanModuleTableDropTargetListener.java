package de.ptb.epics.eve.editor.views;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

import de.ptb.epics.eve.data.scandescription.AbstractBehavior;
import de.ptb.epics.eve.util.collection.ListUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public abstract class ScanModuleTableDropTargetListener extends ViewerDropAdapter {
	private static final Logger LOGGER = Logger.getLogger(
			ScanModuleTableDropTargetListener.class.getName());
	
	private TableViewer viewer;
	
	public ScanModuleTableDropTargetListener(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validateDrop(Object target, int operation, 
			TransferData transferType) {
		if (!TextTransfer.getInstance().isSupportedType(transferType) ||
				operation != DND.DROP_MOVE) {
			return false;
		}
		if (LOGGER.isDebugEnabled()) {
			String targetName = "";
			if (target instanceof AbstractBehavior) {
				targetName = ((AbstractBehavior)target).
						getAbstractDevice().getName();
			}
			switch (this.getCurrentLocation()) {
			case ViewerDropAdapter.LOCATION_BEFORE:
				LOGGER.debug("DnD before " + targetName);
				break;
			case ViewerDropAdapter.LOCATION_ON:
				LOGGER.debug("DnD on " + targetName);
				break;
			case ViewerDropAdapter.LOCATION_AFTER:
				LOGGER.debug("DnD after " + targetName);
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
		
		LOGGER.debug("Drop data is: " + data.toString());
		
		AbstractBehavior dragItem = (AbstractBehavior) viewer.getTable().
				getSelection()[0].getData();
		int sourceIndex = getModel().indexOf(dragItem);
		int targetIndex = getModel().indexOf(target);
		
		if (sourceIndex == -1) {
			LOGGER.error("drag item not found");
			return false;
		} else if (targetIndex == -1) {
			LOGGER.error("target not found");
			return false;
		}
		
		LOGGER.debug("perform drop: source index = " + sourceIndex + 
				", target index = " + targetIndex);
		
		if (location == ViewerDropAdapter.LOCATION_AFTER || 
				location == ViewerDropAdapter.LOCATION_ON) {
			LOGGER.debug("perform drop: insert source after target");
			ListUtil.move(getModel(), sourceIndex, targetIndex);
		} else if (location == ViewerDropAdapter.LOCATION_BEFORE) {
			LOGGER.debug("perform drop: insert source before target");
			ListUtil.move(getModel(), sourceIndex, targetIndex - 1);
		}
		return false;
	}
	
	public abstract List<? extends AbstractBehavior> getModel();
}
