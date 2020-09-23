package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.PrePostscanEntry;

/**
 * @author Marcus Michalsky
 * @since 1.35
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
		PrePostscanEntry entry = (PrePostscanEntry)element;
		return entry.getPostscan() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		PrePostscanEntry entry = (PrePostscanEntry)element;
		return entry.getPostscan().isReset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		PrePostscanEntry entry = (PrePostscanEntry)element;
		Postscan postscan = entry.getPostscan();
		postscan.setReset((Boolean)value);
	}
}
