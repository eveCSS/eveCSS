package de.ptb.epics.eve.editor.views.errorview;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;

import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>ContentProvider</code> is the content provider for the table viewer 
 * defined in the {@link de.ptb.epics.eve.editor.views.errorview.ErrorView}.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ContentProvider implements IStructuredContentProvider, IModelUpdateListener {
	
	private Viewer viewer;
	private ScanDescription scanDescription;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return ((ScanDescription) inputElement).getModelErrors().toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(this.scanDescription != null) {
			this.scanDescription.removeModelUpdateListener(this);
		}
		if(newInput != null) {
			this.scanDescription = (ScanDescription) newInput;
			this.viewer = viewer;
			this.scanDescription.addModelUpdateListener(this);
			setColumnWidth();
			return;
		} 
		this.scanDescription = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.viewer.refresh();
		this.setColumnWidth();
	}
	
	/*
	 * 
	 */
	private void setColumnWidth() {
		int typeColMaxWidth = 120;
		int descrColMaxWidth = 90;
		GC gc = new GC(((TableViewer)viewer).getTable());
		FontMetrics fm = gc.getFontMetrics();
		int charWidth = fm.getAverageCharWidth();
		for(IModelError ime : this.scanDescription.getModelErrors()) {
			if(typeColMaxWidth < ime.getErrorName().length() * charWidth + 8) {
				typeColMaxWidth = ime.getErrorName().length() * charWidth + 8;
			}
			if(descrColMaxWidth < ime.getErrorMessage().length() * charWidth + 8) {
				descrColMaxWidth = ime.getErrorMessage().length() * charWidth + 8;
			}
		}
		((TableViewer)viewer).getTable().getColumn(0).setWidth(typeColMaxWidth);
		((TableViewer)viewer).getTable().getColumn(1).setWidth(descrColMaxWidth);
	}
}