package de.ptb.epics.eve.editor.views.eventcomposite;

import java.util.ArrayList;
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
import de.ptb.epics.eve.util.jface.MyComboBoxCellEditor;

/**
 * {@link org.eclipse.jface.viewers.EditingSupport} for the operator column.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class OperatorEditingSupport extends EditingSupport {

	private Logger logger = 
			Logger.getLogger(OperatorEditingSupport.class.getName());
	
	private TableViewer viewer;
	
	private List<String> comparisonTypes;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer the table viewer
	 */
	public OperatorEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		
		DataTypes type = ((MonitorEvent) ((ControlEvent) element).getEvent())
				.getTypeValue().getType();
		this.comparisonTypes = new ArrayList<String>();
		for(String s : ComparisonTypes.
				typeToString(DataTypes.getPossibleComparisonTypes(type))) {
			comparisonTypes.add(s);
		}
		return new MyComboBoxCellEditor(this.viewer.getTable(), 
				this.comparisonTypes.toArray(new String[0]));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		Event event = ((ControlEvent)element).getEvent();
		if (event instanceof ScheduleEvent || event instanceof DetectorEvent) {
				return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		ControlEvent ce = (ControlEvent)element;
		return comparisonTypes.indexOf(ComparisonTypes.typeToString(
				ce.getLimit().getComparison()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		ControlEvent ce = (ControlEvent)element;
		ce.getLimit().setComparison(ComparisonTypes.stringToType(
				comparisonTypes.get((Integer)value)));
		if (logger.isDebugEnabled()) {
			logger.debug("set operator of " + ce.getEvent().getName() + 
					" to " + comparisonTypes.get((Integer)value));
		}
	}
}