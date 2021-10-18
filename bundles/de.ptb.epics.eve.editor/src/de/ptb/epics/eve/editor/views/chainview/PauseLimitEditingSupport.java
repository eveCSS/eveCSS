package de.ptb.epics.eve.editor.views.chainview;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.editor.StringLabels;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;
import de.ptb.epics.eve.util.ui.jface.TextCellEditorWithValidator;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseLimitEditingSupport extends EditingSupport {
	private static final Logger LOGGER = Logger.getLogger(
			PauseLimitEditingSupport.class.getName());
	
	private TableViewer viewer;
	
	public PauseLimitEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		PauseCondition pauseCondition = (PauseCondition)element;
		if (pauseCondition.isDiscrete()) {
			LOGGER.debug(pauseCondition.getDevice().getName() + " is discrete. " +
					StringLabels.RIGHT_ARROW + " ComboBoxEditor");
			List<String> discreteValues = pauseCondition.getValue().
					getDiscreteValues();
			return new MyComboBoxCellEditor(this.viewer.getTable(), 
					discreteValues.toArray(new String[0]));
		} else {
			LOGGER.debug(pauseCondition.getDevice().getName() + " is not discrete. " +
					StringLabels.RIGHT_ARROW + " TextEditor");
			return new TextCellEditorWithValidator(this.viewer.getTable(), 
					new LimitTextCellEditorValidator(pauseCondition.getDevice()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		PauseCondition pauseCondition = (PauseCondition)element;
		if (pauseCondition.isDiscrete()) {
			int index = pauseCondition.getValue().getDiscreteValues().
					indexOf(pauseCondition.getPauseLimit());
			return index == -1 ? 0 : index;
		} else {
			return pauseCondition.getPauseLimit();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		PauseCondition pauseCondition = (PauseCondition)element;
		if (pauseCondition.isDiscrete()) {
			int index = (Integer) value;
			List<String> discreteValues = pauseCondition.getValue().
					getDiscreteValues();
			pauseCondition.setPauseLimit(discreteValues.get(index));
		} else {
			pauseCondition.setPauseLimit(value.toString());
		}
	}
}
