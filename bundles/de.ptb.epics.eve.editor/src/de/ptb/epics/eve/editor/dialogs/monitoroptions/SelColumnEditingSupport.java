package de.ptb.epics.eve.editor.dialogs.monitoroptions;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.ScanDescription;

/**
 * {@link org.eclipse.jface.viewers.EditingSupport} of the check box column.
 * 
 * @author Hartmut Scherr
 * @since 1.14
 */
public class SelColumnEditingSupport extends EditingSupport {
	private TableViewer viewer;
	private ScanDescription scanDescription;
	
	public SelColumnEditingSupport(TableViewer viewer, ScanDescription scanDescription) {
		super(viewer);
		this.viewer = viewer;
		this.scanDescription = scanDescription;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		final Option option = (Option)element;
		if (scanDescription.getMonitors().contains(option)) {
			scanDescription.removeMonitor(option);
		} else {
			scanDescription.addMonitor(option);
		}
		viewer.update(element, null);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
	}
}