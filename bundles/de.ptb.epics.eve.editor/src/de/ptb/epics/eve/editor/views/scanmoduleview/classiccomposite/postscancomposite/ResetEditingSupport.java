package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.postscancomposite;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import de.ptb.epics.eve.data.scandescription.Postscan;

/**
 * {@link org.eclipse.jface.viewers.EditingSupport} of the reset column.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ResetEditingSupport extends EditingSupport {

	private TableViewer viewer;
	
	public ResetEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return new CheckboxCellEditor(this.viewer.getTable(), 
				SWT.CHECK | SWT.READ_ONLY);
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
		final Postscan postscan = (Postscan)element;
		return postscan.isReset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		final Postscan postscan = (Postscan)element;
		postscan.setReset((Boolean) value);
	}
}