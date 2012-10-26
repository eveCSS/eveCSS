package de.ptb.epics.eve.editor.views.eventcomposite;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.scandescription.ControlEvent;

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
		
		DataTypes type = ((ControlEvent)element).getEvent().getMonitor().
				getDataType().getType();
		this.comparisonTypes = new ArrayList<String>();
		for(String s : ComparisonTypes.
				typeToString(DataTypes.getPossibleComparisonTypes(type))) {
			comparisonTypes.add(s);
		}
		return new ComboBoxCellEditor(this.viewer.getTable(), 
				this.comparisonTypes.toArray(new String[0]), SWT.READ_ONLY) {
			@Override protected void focusLost() {
				if(isActivated()) {
					fireCancelEditor();
				}
				deactivate();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		EventTypes type = ((ControlEvent)element).getEvent().getType();
		if (type == EventTypes.SCHEDULE || type == EventTypes.DETECTOR) {
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