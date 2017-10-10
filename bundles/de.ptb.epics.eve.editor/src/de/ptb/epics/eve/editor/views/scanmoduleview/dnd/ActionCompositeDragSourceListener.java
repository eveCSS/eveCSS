package de.ptb.epics.eve.editor.views.scanmoduleview.dnd;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.data.scandescription.AbstractBehavior;
import de.ptb.epics.eve.editor.views.scanmoduleview.ActionComposite;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public class ActionCompositeDragSourceListener implements DragSourceListener {
	private static final Logger LOGGER = Logger.getLogger(
			ActionCompositeDragSourceListener.class.getName());
	
	private TableViewer viewer;
	private ActionComposite actionComposite;
	
	public ActionCompositeDragSourceListener(TableViewer viewer, 
			ActionComposite actionComposite) {
		this.viewer = viewer;
		this.actionComposite = actionComposite;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		if(actionComposite.getSortState() != 0) {
			event.doit = false;
			LOGGER.debug("no DnD in sorted view!");
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			for (TableItem item : this.viewer.getTable().getSelection()) {
				if (item.getData() instanceof AbstractBehavior) {
					String name = ((AbstractBehavior)item.getData()).
							getAbstractDevice().getName();
					LOGGER.debug("Drag Start: " + name);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragSetData(DragSourceEvent event) {
		// provide the data of the requested type
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			TableItem[] items = this.viewer.getTable().getSelection();
			StringBuffer data = new StringBuffer();
			int count = 0;
			// build the string that is transfered to the drop target

			// add prefix to mark for reordering
			for (TableItem item : items) {
				if (item.getData() instanceof AbstractBehavior) {
					data.append(DragNDropPrefix.MOVE.toString());
					data.append(((AbstractBehavior) item.getData()).
							getAbstractDevice().getID());
				}
				
				count++;
				if (count != items.length) {
					data.append(",");
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("DragSource: " + data.toString());
			}
			event.data = data.toString();
		} else {
			LOGGER.error("Drag n Drop not supported on used platform.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragFinished(DragSourceEvent event) {
		this.viewer.getTable().deselectAll();
	}
}