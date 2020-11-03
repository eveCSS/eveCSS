package de.ptb.epics.eve.editor.views;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.data.scandescription.AbstractBehavior;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ScanModuleTableDragSourceListener implements DragSourceListener {
	private static final Logger LOGGER = Logger.getLogger(
			ScanModuleTableDragSourceListener.class.getName());
	
	private TableViewer viewer;
	
	public ScanModuleTableDragSourceListener(TableViewer viewer) {
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		if (viewer.getComparator() != null) {
			event.doit = false;
			LOGGER.debug("no DnD in sorted table!");
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			for (TableItem item : this.viewer.getTable().getSelection()) {
				String name = ((AbstractBehavior)item.getData()).
						getAbstractDevice().getName();
				LOGGER.debug("Drag Start: " + name);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragSetData(DragSourceEvent event) {
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			TableItem[] items = this.viewer.getTable().getSelection();
			StringBuilder data = new StringBuilder();
			int count = 0;
			// build string that is transfered to the drop target
			// add prefix to mark for reordering
			for (TableItem item : items) {
				if (item.getData() instanceof AbstractBehavior) {
					data.append(DragNDropPrefix.MOVE.toString());
					data.append(((AbstractBehavior)item.getData()).
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
			LOGGER.error("DnD not supported on current platform.");
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
