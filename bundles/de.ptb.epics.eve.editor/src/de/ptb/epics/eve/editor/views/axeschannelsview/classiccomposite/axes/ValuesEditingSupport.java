package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.ui.AddDateTimeDialogCellEditor;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.ui.AddIntDoubleDialogCellEditor;
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
	private static final Logger LOGGER = Logger.getLogger(
			ValuesEditingSupport.class.getName());
	
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
				// return new AddIntDoubleTextCellEditor(viewer.getTable(), axis);
				return new AddIntDoubleDialogCellEditor(viewer.getTable(), axis);
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
			LOGGER.debug("requesting Cell Editor for position list editing");
			//return new PositionlistTextCellEditor(viewer.getTable(), axis);
			return new PositionlistDialogCellEditor(viewer.getTable(), axis);
			//return new PositionListDialogCellEditor2(viewer.getTable(), axis);
			//return new PositionlistDialogCellEditor3(viewer.getTable(), axis);
		case RANGE:
			return new RangeTextCellEditor(viewer.getTable(), axis);
		default:
			break;
		}
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
		LOGGER.debug("getValue: " + element);
		Axis axis = (Axis) element;
		// in some cases, the label differs from the editing text:
		switch (axis.getStepfunction()) {
		case ADD:
		case MULTIPLY:
			return axis.getStart().toString() + " / " +
			axis.getStop().toString() + " / " + 
			axis.getStepwidth().toString() + " / " + 
			axis.getStepcount();
		case FILE:
			// show full path
			if (axis.getFile() != null) {
				return axis.getFile().getAbsolutePath();
			} else {
				return "";
			}
		default:
			break;
		}
		// for all other cases, use same text as for the label provider
		return ValuesColumnStringFormatter.getValuesString(axis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		LOGGER.debug("setValue: Element=" + element + ", Value=" + value);
		Axis axis = (Axis)element;
		switch (axis.getStepfunction()) {
		case ADD:
			break;
		case MULTIPLY:
			break;
		case FILE:
			if (value == null) {
				axis.setFile(null);
			} else {
				if (!value.equals(axis.getFile())) {
					axis.setFile(new File(value.toString()));
				}
			}
			break;
		case PLUGIN:
			break;
		case POSITIONLIST:
			if (value == null) {
				axis.setPositionlist(null);
			} else {
				if (!value.equals(axis.getPositionlist())) {
					axis.setPositionlist(value.toString());
				}
			}
			break;
		case RANGE:
			if (value == null) {
				axis.setRange(null);
			} else {
				axis.setRange(value.toString());
			}
			break;
		default:
			break;
		}
	}
}
