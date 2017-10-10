package de.ptb.epics.eve.editor.dialogs.updater;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;

import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class ContentProvider implements IStructuredContentProvider {
	
	private Viewer viewer;
	private List<Patch> patches;
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		List<Modification> modifications = new LinkedList<Modification>();
		for (Patch patch : (List<Patch>)inputElement) {
			for (Modification mod : patch.getModifications()) {
				modifications.add(mod);
			}
		}
		return modifications.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		this.patches = (List<Patch>)newInput;
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
		int changeLogColMaxWidth = 255;
		GC gc = new GC(((TableViewer)viewer).getTable());
		FontMetrics fm = gc.getFontMetrics();
		int charWidth = fm.getAverageCharWidth();
		
		for (Patch patch : this.patches) {
			for (Modification mod : patch.getModifications()) {
				if (changeLogColMaxWidth < mod.getChangeLog().length()
						* charWidth + 8) {
					changeLogColMaxWidth = mod.getChangeLog().length()
							* charWidth + 8; 
				}
			}
		}
		
		((TableViewer) viewer).getTable().getColumn(2)
				.setWidth(changeLogColMaxWidth);
	}
}