package de.ptb.epics.eve.editor.views.chainview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.util.ui.jface.TextCellEditorWithValidator;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class ContinueLimitEditingSupport extends EditingSupport {
	private static final Logger LOGGER = Logger.getLogger(
			ContinueLimitEditingSupport.class.getName());

	private TableViewer viewer;
	
	public ContinueLimitEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		PauseCondition pauseCondition = (PauseCondition)element;
		return new TextCellEditorWithValidator(this.viewer.getTable(), 
			new ContinueLimitTextCellEditorValidator(pauseCondition.getDevice()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		LOGGER.debug("can edit: " + ((PauseCondition)element).hasContinueLimit());
		return ((PauseCondition)element).hasContinueLimit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		PauseCondition pauseCondition = (PauseCondition)element;
		if (pauseCondition.getContinueLimit() == null) {
			return "";
		}
		return pauseCondition.getContinueLimit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		PauseCondition pauseCondition = (PauseCondition)element;
		if (value.toString().isEmpty()) {
			pauseCondition.setContinueLimit(null);
		} else {
			pauseCondition.setContinueLimit(value.toString());
		}
	}
}
