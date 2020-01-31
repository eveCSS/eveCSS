package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.ui.AddDateTimeDialogCellEditor;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleTextValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.ui.AddIntDoubleTextCellEditor;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.ui.FileDialogCellEditor;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.plugin.ui.PluginDialogCellEditor;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.positionlist.ui.PositionlistDialogCellEditor;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.range.ui.RangeTextCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ValuesEditingSupport extends EditingSupport {
	private TableViewer viewer;
	
	public ValuesEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		Axis axis = (Axis) element;
		switch (axis.getStepfunction()) {
		case ADD:
			if (DataTypes.DATETIME.equals(axis.getType())) {
				return new AddDateTimeDialogCellEditor(viewer.getTable(), axis);
			}
			if (DataTypes.INT.equals(axis.getType()) || 
					DataTypes.DOUBLE.equals(axis.getType())) {
				return new AddIntDoubleTextCellEditor(viewer.getTable(), axis);
			}
			break;
		case FILE:
			return new FileDialogCellEditor(viewer.getTable(), axis);
		case MULTIPLY:
			if (DataTypes.INT.equals(axis.getType()) || 
					DataTypes.DOUBLE.equals(axis.getType())) {
				return new AddIntDoubleTextCellEditor(viewer.getTable(), axis);
			}
			break;
		case PLUGIN:
			return new PluginDialogCellEditor(viewer.getTable(), axis);
		case POSITIONLIST:
			return new PositionlistDialogCellEditor(viewer.getTable(), axis);
		case RANGE:
			return new RangeTextCellEditor(viewer.getTable(), axis);
		default:
			break;
		}
		// TODO Auto-generated method stub
		return null;
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
		Axis axis = (Axis) element;
		switch (axis.getStepfunction()) {
		case ADD:
		case MULTIPLY:
			return axis.getStart().toString() + " / " +
			axis.getStop().toString() + " / " + 
			axis.getStepwidth().toString() + " / " + 
			axis.getStepcount();
		case FILE:
			return axis.getFile().getName();
		case PLUGIN:
			return "Plugin (" + axis.getPluginController().getPlugin().getName() + ")";
		case POSITIONLIST:
			return axis.getPositionlist();
		case RANGE:
			return axis.getRange();
		default:
			break;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		Axis axis = (Axis)element;
		switch (axis.getStepfunction()) {
		case ADD:
			break;
		case FILE:
			break;
		case MULTIPLY:
			break;
		case PLUGIN:
			break;
		case POSITIONLIST:
			// System.out.println("<<<<<<<<<<<<<<<<<<< set value: " + value.toString());
			// axis.setPositionlist(value.toString());
			break;
		case RANGE:
			axis.setRange(value.toString());
			break;
		default:
			break;
		}
		// TODO Auto-generated method stub
	}
}
