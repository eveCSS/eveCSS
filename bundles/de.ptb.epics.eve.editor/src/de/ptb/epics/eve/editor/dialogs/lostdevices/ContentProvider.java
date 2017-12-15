package de.ptb.epics.eve.editor.dialogs.lostdevices;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;

import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoaderDeviceMessage;

/**
 * @author Marcus Michalsky
 * @since 1.5
 */
public class ContentProvider implements IStructuredContentProvider {

	private List<ScanDescriptionLoaderDeviceMessage> messages;
	private Viewer viewer;
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<ScanDescriptionLoaderDeviceMessage>) inputElement)
				.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.messages = (List<ScanDescriptionLoaderDeviceMessage>) newInput;
		this.viewer = viewer;
		if (newInput == null) {
			return;
		}
		this.setColumnWidth();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {	
	}
	
	/*
	 * 
	 */
	private void setColumnWidth() {
		int typeColMaxWidth = 255;
		int actionColMaxWidth = 300;
		GC gc = new GC(((TableViewer)viewer).getTable());
		FontMetrics fm = gc.getFontMetrics();
		int charWidth = fm.getAverageCharWidth();
		
		for (ScanDescriptionLoaderDeviceMessage msg : this.messages) {
			if (typeColMaxWidth < msg.getType().toString().length() * charWidth + 8) {
				typeColMaxWidth = msg.getType().toString().length() * charWidth + 8;
			}
			if (actionColMaxWidth < msg.getAction().length() * charWidth + 8) {
				actionColMaxWidth = msg.getAction().length() * charWidth + 8;
			}
		}
		((TableViewer)viewer).getTable().getColumn(0).setWidth(typeColMaxWidth);
		((TableViewer)viewer).getTable().getColumn(1).setWidth(actionColMaxWidth);
	}
}