package de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Postscan;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ValueEditingSupport extends EditingSupport {

	private TableViewer viewer;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer
	 */
	public ValueEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		final Postscan postscan = (Postscan)element;
		
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		final Postscan postscan = (Postscan)element;
		if (postscan.isReset()) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
	 */
	@Override
	protected Object getValue(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub

	}
}