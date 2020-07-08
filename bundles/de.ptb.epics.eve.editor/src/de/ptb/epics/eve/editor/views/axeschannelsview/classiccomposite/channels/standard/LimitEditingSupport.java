package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.measuringstation.event.MonitorEvent;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class LimitEditingSupport extends EditingSupport {
	private static final Logger LOGGER = Logger.getLogger(
			LimitEditingSupport.class.getName());
	
	private TableViewer viewer;
	private List<String> discreteValues;
	
	public LimitEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		ControlEvent controlEvent = (ControlEvent)element;
		final MonitorEvent monitorEvent = (MonitorEvent)controlEvent.getEvent();
		if (monitorEvent.getTypeValue().isDiscrete()) {
			this.discreteValues = new ArrayList<>();
			this.discreteValues.addAll(monitorEvent.getTypeValue().
					getDiscreteValues());
			return new MyComboBoxCellEditor(this.viewer.getTable(), 
					this.discreteValues.toArray(new String[0]));
		}
		TextCellEditor textCellEditor = new TextCellEditor(this.viewer.getTable()) {
			@Override 
			protected void focusLost() {
				if (isActivated()) {
					fireCancelEditor();
				}
				deactivate();
			}
		};
		textCellEditor.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				if (monitorEvent.getAccess().isValuePossible((String)value)) {
					return null;
				}
				return "Value not possible";
			}
		});
		return textCellEditor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		Event event = ((ControlEvent)element).getEvent();
		return !(event instanceof ScheduleEvent || event instanceof DetectorEvent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		ControlEvent controlEvent = (ControlEvent)element;
		MonitorEvent monitorEvent = (MonitorEvent)controlEvent.getEvent();
		if (monitorEvent.getTypeValue().isDiscrete()) {
			return discreteValues.indexOf(controlEvent.getLimit().getValue());
		} else {
			return controlEvent.getLimit().getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		ControlEvent controlEvent = (ControlEvent)element;
		MonitorEvent monitorEvent = (MonitorEvent)controlEvent.getEvent();
		if (monitorEvent.getTypeValue().isDiscrete()) {
			controlEvent.getLimit().setValue(discreteValues.get((Integer)value));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("set limit of " + controlEvent.getEvent().getName() + 
						" to " + discreteValues.get((Integer)value));
			}
		} else {
			controlEvent.getLimit().setValue((String)value);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("set limit of " + controlEvent.getEvent().getName() + 
						" to " + (String)value);
			}
		}
	}
}
