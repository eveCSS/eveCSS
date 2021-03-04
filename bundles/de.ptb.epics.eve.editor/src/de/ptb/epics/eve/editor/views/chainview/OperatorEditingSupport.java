package de.ptb.epics.eve.editor.views.chainview;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class OperatorEditingSupport extends EditingSupport {
	private TableViewer viewer;
	
	/*
	 * Due to
	 * - use of Java prior 8
	 * - unfavorable enum order
	 * - use of String[] in ComboBoxEditor
	 * some "Hacking" seems necessary
	 */
	private String[] items = {
			ComparisonTypes.EQ.toString(), ComparisonTypes.NE.toString(), 
			ComparisonTypes.GT.toString(), ComparisonTypes.LT.toString()};
	private String[] discreteItems = {
			ComparisonTypes.EQ.toString(), ComparisonTypes.NE.toString()};
	
	public OperatorEditingSupport(TableViewer viewer) {
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
			return new MyComboBoxCellEditor(viewer.getTable().getParent(), 
					discreteItems);
		} else {
			return new MyComboBoxCellEditor(viewer.getTable().getParent(), 
					items);
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
		return pauseCondition.getOperator().ordinal();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		PauseCondition pauseCondition = (PauseCondition)element;
		switch ((Integer)value) {
		case 0:
			pauseCondition.setOperator(ComparisonTypes.EQ);
			break;
		case 1:
			pauseCondition.setOperator(ComparisonTypes.NE);
			break;
		case 2:
			pauseCondition.setOperator(ComparisonTypes.GT);
			break;
		case 3:
			pauseCondition.setOperator(ComparisonTypes.LT);
			break;
		}
	}
}
