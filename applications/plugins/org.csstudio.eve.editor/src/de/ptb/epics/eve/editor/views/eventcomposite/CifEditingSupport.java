package de.ptb.epics.eve.editor.views.eventcomposite;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class CifEditingSupport extends EditingSupport {

	public CifEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}

	private static Logger logger = 
			Logger.getLogger(CifEditingSupport.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return new CheckboxCellEditor();
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
		PauseEvent ce = (PauseEvent)element;
		return ce.isContinueIfFalse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		PauseEvent ce = (PauseEvent)element;
		ce.setContinueIfFalse((Boolean)value);
		if (logger.isDebugEnabled()) {
			logger.debug("set CIF of PauseEvent " + ce.getEvent().getName() +
						" to " + (Boolean)value);
		}
	}
}