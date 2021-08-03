package de.ptb.epics.eve.editor.views;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.TableItem;

/**
 * Abstract template for common elements of drag source for table viewer DnD.
 * 
 * @author Marcus Michalsky
 * @since 1.36
 */
public abstract class ViewerTableDragSourceListener implements DragSourceListener {
	private TableViewer viewer;
	
	protected ViewerTableDragSourceListener(TableViewer viewer) {
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		if (viewer.getComparator() != null) {
			event.doit = false;
			getLogger().debug("no DnD in sorted table!");
			return;
		}
		if (getLogger().isDebugEnabled()) {
			for (TableItem item : this.viewer.getTable().getSelection()) {
				getLogger().debug("Drag Start: " + getModelName(item));
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
				if (!getModelId(item).isEmpty()) {
					data.append(DragNDropPrefix.MOVE.toString());
					data.append(getModelId(item));
				}
				count++;
				if (count != items.length) {
					data.append(",");
				}
			}
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("DragSource: " + data.toString());
			}
			event.data = data.toString();
		} else {
			getLogger().error("DnD not supported on current platform.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragFinished(DragSourceEvent event) {
		this.viewer.getTable().deselectAll();
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
	public abstract String getModelName(TableItem item);
	
	/**
	 * Returns a unique identifier if the given item is of the correct type, 
	 * an empty string otherwise.
	 * 
	 * @param item the item the string id should be generated from
	 * @return a unique identifier if the given item is of the correct type, 
	 * 	an empty string otherwise
	 */
	public abstract String getModelId(TableItem item);
}
