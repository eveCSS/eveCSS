package de.ptb.epics.eve.editor.views.batchupdateview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.editor.views.batchupdateview.FileEntry;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class FileListContentProvider implements IStructuredContentProvider, PropertyChangeListener {
	private List<FileEntry> fileEntries;
	private TableViewer viewer;
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (this.fileEntries != null) {
			for (FileEntry entry : this.fileEntries) {
				entry.removePropertyChangeListener(this);
			}
		}
		this.viewer = (TableViewer)viewer;
		this.fileEntries = (List<FileEntry>)newInput;
		if (this.fileEntries != null) {
			for (FileEntry entry : this.fileEntries) {
				entry.addPropertyChangeListener(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<FileEntry>)inputElement).toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (this.fileEntries != null) {
			for (FileEntry entry : this.fileEntries) {
				entry.removePropertyChangeListener(this);
			}
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FileEntry.STATUS_PROP) ||
				evt.getPropertyName().equals(FileEntry.VERSION_PROP)) {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					viewer.update(evt.getSource(), new String[] {
							FileEntry.STATUS_PROP, FileEntry.VERSION_PROP});
				}
			});
		}
	}
}