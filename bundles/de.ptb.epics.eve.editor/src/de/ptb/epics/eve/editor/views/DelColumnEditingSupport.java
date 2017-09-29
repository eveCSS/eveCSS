package de.ptb.epics.eve.editor.views;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * {@link org.eclipse.jface.viewers.EditingSupport} for the delete column of 
 * action composites. Enables Removal ob table items by clicking on the first 
 * (del) column. The id of the command has to be passed to the constructor.
 * 
 * @author Marcus Michalsky
 * @since 1.5
 */
public class DelColumnEditingSupport extends EditingSupport {

	private static Logger logger = Logger
			.getLogger(DelColumnEditingSupport.class.getName());
	
	private ColumnViewer columnViewer;
	
	private String commandId;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer the viewer
	 * @param commandId the id of the remove command
	 */
	public DelColumnEditingSupport(ColumnViewer viewer, String commandId) {
		super(viewer);
		this.columnViewer = viewer;
		this.commandId = commandId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		// select the item that was clicked
		((TableViewer) this.columnViewer).setSelection(new StructuredSelection(
				element));
		
		/*
		 * Instead of answering the question if this column is editable (always
		 * returns false), the command to delete the currently selected item
		 * is executed.
		 */
		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getService(IHandlerService.class);
		try {
			handlerService.executeCommand(this.commandId, null);
		} catch (ExecutionException e) {
			logger.warn(e.getMessage(), e);
		} catch (NotDefinedException e) {
			logger.warn(e.getMessage(), e);
		} catch (NotEnabledException e) {
			logger.warn(e.getMessage(), e);
		} catch (NotHandledException e) {
			logger.warn(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
	}
}