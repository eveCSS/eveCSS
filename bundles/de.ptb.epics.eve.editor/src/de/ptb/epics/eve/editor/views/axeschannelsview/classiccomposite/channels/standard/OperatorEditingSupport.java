package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
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
public class OperatorEditingSupport extends EditingSupport {
	private static final Logger LOGGER = 
			Logger.getLogger(OperatorEditingSupport.class.getName());
	
	private TableViewer viewer;
	private List<String> comparisonTypes;
	
	public OperatorEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		DataTypes dataType = ((MonitorEvent) ((ControlEvent) element).
				getEvent()).getTypeValue().getType();
		this.comparisonTypes = Arrays.asList(ComparisonTypes.typeToString(
				DataTypes.getPossibleComparisonTypes(dataType)));
		return new MyComboBoxCellEditor(this.viewer.getTable(), 
				this.comparisonTypes.toArray(new String[0]));
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
		return comparisonTypes.indexOf(ComparisonTypes.typeToString(
				controlEvent.getLimit().getComparison()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		ControlEvent controlEvent = (ControlEvent)element;
		controlEvent.getLimit().setComparison(ComparisonTypes.stringToType(
				comparisonTypes.get((Integer)value)));
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("set operator of " + controlEvent.getEvent().getName() 
					+ " to " + comparisonTypes.get((Integer)value));
		}
	}
}
