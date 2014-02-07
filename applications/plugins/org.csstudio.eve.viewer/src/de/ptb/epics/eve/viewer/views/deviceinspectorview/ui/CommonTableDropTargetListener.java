package de.ptb.epics.eve.viewer.views.deviceinspectorview.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public abstract class CommonTableDropTargetListener implements
		DropTargetListener {

	private static final Logger LOGGER = Logger
			.getLogger(CommonTableDropTargetListener.class.getName());
	
	protected int tableSortState;
	protected TableViewer viewer;
	
	/**
	 * Constructor.
	 * 
	 * @param tableSortState the sort state of the table
	 */
	public CommonTableDropTargetListener(TableViewer viewer, int tableSortState) {
		this.viewer = viewer;
		this.tableSortState = tableSortState;
	}
	
	/**
	 * Updates the sort state.
	 * 
	 * @param sortState the sort state
	 */
	public void setSortState(int sortState) {
		this.tableSortState = sortState;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragEnter(DropTargetEvent event) {
		if ((event.operations & DND.DROP_COPY) != 0) {
			event.detail = DND.DROP_COPY;
			event.feedback = DND.FEEDBACK_SCROLL;
		} else if ((event.operations & DND.DROP_MOVE) != 0) {
			if (this.tableSortState != 0) {
				event.detail = DND.DROP_NONE;
				event.feedback = DND.FEEDBACK_NONE;
				return;
			}
			event.detail = DND.DROP_MOVE;
			event.feedback = DND.FEEDBACK_INSERT_AFTER;
		} else {
			event.detail = DND.DROP_NONE;
			event.feedback = DND.FEEDBACK_NONE;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragLeave(DropTargetEvent event) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragOperationChanged(DropTargetEvent event) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dragOver(DropTargetEvent event) {
		if ((event.operations & DND.DROP_COPY) != 0) {
			event.detail = DND.DROP_COPY;
			event.feedback = DND.FEEDBACK_SCROLL;
		} else if ((event.operations & DND.DROP_MOVE) != 0) {
			// check if "wrong" table
			if (event.item instanceof TableItem) {
				LOGGER.debug(((CommonTableElement) ((TableItem) event.item)
						.getData()).getAbstractDevice().getName());
				LOGGER.debug(this.viewer.getTable().indexOf((TableItem)event.item));
				if (this.viewer.getTable().indexOf((TableItem)event.item) == -1) {
					event.detail = DND.DROP_NONE;
					event.feedback = DND.FEEDBACK_NONE;
					return;
				}
				LOGGER.debug(event.getSource().getClass().getName());
				if (this.viewer.getSelection().isEmpty()) {
					event.detail = DND.DROP_NONE;
					event.feedback = DND.FEEDBACK_NONE;
					return;
				}
			}
			if (this.tableSortState != 0) {
				event.detail = DND.DROP_NONE;
				event.feedback = DND.FEEDBACK_NONE;
				return;
			}
			event.detail = DND.DROP_MOVE;
			event.feedback = DND.FEEDBACK_INSERT_AFTER | DND.FEEDBACK_SCROLL;
		} else {
			event.detail = DND.DROP_NONE;
			event.feedback = DND.FEEDBACK_NONE;
		}
	}
	
	protected void move(DropTargetEvent event) {
		TableItem insertAfter = (TableItem) event.item;
		CommonTableElement predecessor = (CommonTableElement) insertAfter
				.getData();
		CommonTableElement dragItem = (CommonTableElement) this.viewer
				.getTable().getSelection()[0].getData();
		((CommonTableContentProvider) this.viewer.getContentProvider())
				.moveItem(dragItem, predecessor);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Item: " + dragItem.getAbstractDevice().getName());
			LOGGER.debug("Destination: "
					+ predecessor.getAbstractDevice().getName());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dropAccept(DropTargetEvent event) {
	}
}