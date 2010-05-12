package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ExpandItem;

public class CommonTableContentProvider implements IStructuredContentProvider {

	private List<CommonTableElement> elements;
	private TableViewer viewer;
	private ExpandItem expandItem;

	public CommonTableContentProvider (TableViewer viewer, ExpandItem item){
		this.viewer = viewer;
		this.expandItem = item;
		elements = new ArrayList<CommonTableElement>();
	}
	
	public boolean addElement(CommonTableElement element){
		if (element == null) return false;
		for (CommonTableElement cte : elements) {
			if (cte.getAbstractDevice() == element.getAbstractDevice()) return false;
		}		
		elements.add(element);
		viewer.add(element);
		setSize();
		return true;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return elements.toArray();
	}

	@Override
	public void dispose() {
		for (CommonTableElement element : elements) {
			element.dispose();
		}
		elements.clear();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		System.err.println("ContentProvider inputChanged");
	}

	public void removeElement(Object element) {
		if (element == null) return;
		((CommonTableElement)element).dispose();
		if (elements.contains(element)){
			elements.remove(element);
			viewer.remove(element);
		}
		setSize();
	}
	
	private void setSize(){
		Point point = viewer.getTable().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		if (!expandItem.isDisposed() && !expandItem.getControl().isDisposed()){ 
			expandItem.setHeight( point.y + 10 );
			//expandItem.getParent().layout(true);
			//expandItem.getParent().redraw();
			System.err.println("ContentProvider: set height to " + point.y);
		}

	}
}
