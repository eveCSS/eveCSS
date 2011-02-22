/* 
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class MathTableContentProvider implements IStructuredContentProvider {

	private TableViewer tableViewer;
	private List<MathTableElement> elements;

	/**
	 * Constructs a <code>MathTableContentProvider</code>.
	 * 
	 * @param tableViewer
	 */
	public MathTableContentProvider(TableViewer tableViewer) {
		elements = new ArrayList<MathTableElement>();
		this.tableViewer = tableViewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return elements.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		elements.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	/**
	 * 
	 * @param element
	 */
	public void addElement(MathTableElement element){
		elements.add(element);
		tableViewer.add(element);
	}

	/**
	 * 
	 */
	public void clear() {
		if (elements.size() > 0) {
			tableViewer.remove(elements.toArray());
			elements.clear();
		}
	}
}