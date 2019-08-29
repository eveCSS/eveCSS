package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.motoraxiscomposite;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.32
 */
public class ValueColumnEditingSupport extends EditingSupport {
	private TableViewer viewer;
	
	public ValueColumnEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		final Axis axis = (Axis) element;
		CellEditor editor = new TextCellEditor(this.viewer.getTable()) {
			@Override
			protected void setErrorMessage(String message) {
				text.setToolTipText(message);
				super.setErrorMessage(message);
			}
		};
		editor.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				final String errorMsg = "Given position list is invalid.";
				switch (axis.getType()) {
				case DOUBLE:
					return StringUtil.isPositionList(value.toString(), 
							Double.class) ? null : errorMsg;
				case INT:
					return StringUtil.isPositionList(value.toString(), 
							Integer.class) ? null : errorMsg;
				case STRING:
					return StringUtil.isPositionList(value.toString(), 
							String.class) ? null : errorMsg;
				default:
					return "Data type is invalid.";
				}
			}
		});
		return editor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		Axis axis = (Axis) element;
		return axis.getStepfunction().equals(Stepfunctions.POSITIONLIST);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		return ((Axis)element).getPositionlist();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		((Axis)element).setPositionlist(value.toString());
	}
}
