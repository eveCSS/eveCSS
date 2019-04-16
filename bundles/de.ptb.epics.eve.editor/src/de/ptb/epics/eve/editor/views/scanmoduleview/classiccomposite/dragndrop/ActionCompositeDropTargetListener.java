package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.dragndrop;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

import de.ptb.epics.eve.data.scandescription.AbstractBehavior;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.ActionComposite;
import de.ptb.epics.eve.util.collection.ListUtil;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public class ActionCompositeDropTargetListener extends ViewerDropAdapter {
	private static final Logger LOGGER = Logger.getLogger(
		ActionCompositeDropTargetListener.class.getName());

	private ActionComposite actionComposite;
	
	public ActionCompositeDropTargetListener(Viewer viewer, 
			ActionComposite actionComposite) {
		super(viewer);
		this.actionComposite = actionComposite;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validateDrop(Object target, int operation, 
			TransferData transferType) {
		if (!TextTransfer.getInstance().isSupportedType(transferType)) {
			return false;
		}
		if (operation != DND.DROP_MOVE) {
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
	 * @see {@link java.util.Collections#rotate(List, int)}
	 */
	@Override
	public boolean performDrop(Object data) {
		List<? extends AbstractBehavior> model = this.actionComposite.getModel();
		Object target = this.getCurrentTarget();
		int location = this.getCurrentLocation();
		
		LOGGER.debug("drop data is: " + data.toString());
		
		AbstractBehavior dragItem = (AbstractBehavior) ((TableViewer) 
				this.getViewer()).getTable().getSelection()[0].getData();
		
		int sourceIndex = model.indexOf(dragItem);
		int targetIndex = model.indexOf(target);
		
		if (sourceIndex == -1) {
			LOGGER.error("drag item not found");
			return false;
		} else if (targetIndex == -1) {
			LOGGER.error("target not found");
			return false;
		}
		
		LOGGER.debug("perform drop: source index = " + sourceIndex);
		LOGGER.debug("perform drop: target index = " + targetIndex);
		
		if (location == ViewerDropAdapter.LOCATION_AFTER) {
			LOGGER.debug("perform drop: insert source after target");
			this.actionComposite.disableSelectionService();
			ListUtil.move(model, sourceIndex, targetIndex);
			this.actionComposite.enableSelectionService();
			this.actionComposite.setSelection(dragItem);
		} else if (location == ViewerDropAdapter.LOCATION_BEFORE) {
			LOGGER.debug("perform drop: insert source before target");
			this.actionComposite.disableSelectionService();
			ListUtil.move(model, sourceIndex, targetIndex - 1);
			this.actionComposite.enableSelectionService();
			this.actionComposite.setSelection(dragItem);
		}
		return false;
	}
}