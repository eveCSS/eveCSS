package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyMode;
import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.datetime.ui.AddDateTimeDialogCellEditor;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleTextConverter;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.AddIntDoubleValues;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble.ui.AddIntDoubleDialogCellEditor;
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
				return new AddIntDoubleDialogCellEditor(viewer.getTable(), axis);
			}
			break;
		case FILE:
			return new FileDialogCellEditor(viewer.getTable(), axis);
		case MULTIPLY:
			if (DataTypes.INT.equals(axis.getType()) || 
					DataTypes.DOUBLE.equals(axis.getType())) {
				return new AddIntDoubleDialogCellEditor(viewer.getTable(), axis);
			}
			break;
		case PLUGIN:
			return new PluginDialogCellEditor(viewer.getTable(), axis);
		case POSITIONLIST:
			LOGGER.debug("requesting Cell Editor for position list editing");
			return new PositionlistDialogCellEditor(viewer.getTable(), axis);
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
			if (axis.getType().equals(DataTypes.DATETIME)) {
				break;
			}
			// show a / b / c / d instead of a -> b / c
			// if a main axis is set (which is not this axis) show a / b / c only
			// the field the adjust parameter is set to is replaced with "-"
			return this.getAddAxisValue(axis);
		case FILE:
			// show full path (instead of file name only)
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
		case MULTIPLY:
			// TODO if value == "" ? (dialog return value)
			if (value != null) {
				this.setAddAxisValue(axis, value);
			}
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
	
	private String getAddAxisValue(Axis axis) {
		AdjustParameter adjustParameter = ((AddMultiplyMode<?>)axis.getMode()).
				getAdjustParameter();
		if (axis.getScanModule().getMainAxis() != null && 
				!axis.getScanModule().getMainAxis().equals(axis)) {
			switch (adjustParameter) {
			case START:
				return "- / " + 
				axis.getStop() + " / " + 
				axis.getStepwidth();
			case STEPWIDTH:
				return axis.getStart() + " / " + 
				axis.getStop() + " / -";
			case STOP:
				return axis.getStart() + " / " + 
				"- / " + 
				axis.getStepwidth();
			default:
				break;
			}
			LOGGER.error("adjust parameter (with main axis) could not be determined");
			return "adjust parameter error (main axis active)";
		}
		switch (adjustParameter) {
		case START:
			return "- / " + 
			axis.getStop() + " / " + 
			axis.getStepwidth() + " / " + 
			axis.getStepcount();
		case STEPCOUNT:
			return axis.getStart() + " / " + 
			axis.getStop() + " / " + 
			axis.getStepwidth() + " / -";
		case STEPWIDTH:
			return axis.getStart() + " / " + 
			axis.getStop() + " / " + 
			"- / " + 
			axis.getStepcount();
		case STOP:
			return axis.getStart() + " / " + 
			"- / " + 
			axis.getStepwidth() + " / " + 
			axis.getStepcount();
		default:
			break;
		}
		LOGGER.error("adjust parameter could not be determined");
		return "adjust parameter error";
	}
	
	@SuppressWarnings("unchecked")
	private void setAddAxisValue(Axis axis, Object value) {
		if (axis.getType().equals(DataTypes.INT)) {
			AddIntDoubleValues<Integer> intValues = 
				AddIntDoubleTextConverter.convertInt((String)value);
			((AddMultiplyMode<Integer>)axis.getMode()).
				setAdjustParameter(intValues.getAdjustParameter());
			switch (intValues.getAdjustParameter()) {
			case START:
				axis.setStop(intValues.getStop());
				axis.setStepwidth(intValues.getStepwidth());
				if (!intValues.getStepcount().equals(Double.NaN)) {
					axis.setStepcount(intValues.getStepcount());
				}
				break;
			case STEPCOUNT:
				axis.setStart(intValues.getStart());
				axis.setStop(intValues.getStop());
				axis.setStepwidth(intValues.getStepwidth());
				break;
			case STEPWIDTH:
				axis.setStart(intValues.getStart());
				axis.setStop(intValues.getStop());
				if (!intValues.getStepcount().equals(Double.NaN)) {
					axis.setStepcount(intValues.getStepcount());
				}
				break;
			case STOP:
				axis.setStart(intValues.getStart());
				axis.setStepwidth(intValues.getStepwidth());
				if (!intValues.getStepcount().equals(Double.NaN)) {
					axis.setStepcount(intValues.getStepcount());
				}
				break;
			default:
				break;
			}
		} else if (axis.getType().equals(DataTypes.DOUBLE)) {
			AddIntDoubleValues<Double> doubleValues = 
				AddIntDoubleTextConverter.convertDouble((String)value);
			((AddMultiplyMode<Double>)axis.getMode()).
				setAdjustParameter(doubleValues.getAdjustParameter());
			switch (doubleValues.getAdjustParameter()) {
			case START:
				axis.setStop(doubleValues.getStop());
				axis.setStepwidth(doubleValues.getStepwidth());
				if (!doubleValues.getStepcount().equals(Double.NaN)) {
					axis.setStepcount(doubleValues.getStepcount());
				}
				break;
			case STEPCOUNT:
				axis.setStart(doubleValues.getStart());
				axis.setStop(doubleValues.getStop());
				axis.setStepwidth(doubleValues.getStepwidth());
				break;
			case STEPWIDTH:
				axis.setStart(doubleValues.getStart());
				axis.setStop(doubleValues.getStop());
				if (!doubleValues.getStepcount().equals(Double.NaN)) {
					axis.setStepcount(doubleValues.getStepcount());
				}
				break;
			case STOP:
				axis.setStart(doubleValues.getStart());
				axis.setStepwidth(doubleValues.getStepwidth());
				if (!doubleValues.getStepcount().equals(Double.NaN)) {
					axis.setStepcount(doubleValues.getStepcount());
				}
				break;
			default:
				break;
			}
		}
	}
}
