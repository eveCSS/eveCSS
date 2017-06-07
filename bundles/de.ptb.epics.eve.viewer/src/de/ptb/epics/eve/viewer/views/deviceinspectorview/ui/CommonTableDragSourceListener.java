package de.ptb.epics.eve.viewer.views.deviceinspectorview.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.DragNDropPrefix;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class CommonTableDragSourceListener implements DragSourceListener {
	private static final Logger LOGGER = Logger
			.getLogger(CommonTableDragSourceListener.class.getName());
	
	private TableViewer viewer;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer the viewer
	 */
	public CommonTableDragSourceListener(TableViewer viewer) {
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		if (LOGGER.isDebugEnabled()) {
			for (TableItem item : this.viewer.getTable().getSelection()) {
				LOGGER.debug(item.getData() + " selected");
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
				if (item.getData() instanceof CommonTableElement) {
					data.append(DragNDropPrefix.MOVE.toString());
					data.append(((CommonTableElement) item.getData())
							.getAbstractDevice().getID());
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