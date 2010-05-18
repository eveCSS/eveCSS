package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class MathTableContentProvider implements IStructuredContentProvider {

	private TableViewer tableViewer;
	private List<MathTableElement> elements;

	public MathTableContentProvider(TableViewer tableViewer) {
		elements = new ArrayList<MathTableElement>();
		this.tableViewer = tableViewer;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return elements.toArray();
	}

	@Override
	public void dispose() {
		for (MathTableElement element : elements) {
			// element.dispose();
		}
		elements.clear();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	public void addElement(MathTableElement element){
		elements.add(element);
		tableViewer.add(element);
	}

	public void clear() {
		if (elements.size() > 0) {
			tableViewer.remove(elements.toArray());
			elements.clear();
		}
	}

}
