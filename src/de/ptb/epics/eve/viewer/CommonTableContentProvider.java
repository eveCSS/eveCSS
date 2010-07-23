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

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;

public class CommonTableContentProvider implements IStructuredContentProvider {

	private List<CommonTableElement> elements;
	private TableViewer viewer;
	private ExpandItem expandItem;
	private final List< AbstractDevice > devices;

	public CommonTableContentProvider (TableViewer viewer, ExpandItem item, final List< AbstractDevice > devices ) {
		this.viewer = viewer;
		this.expandItem = item;
		elements = new ArrayList<CommonTableElement>();
		this.devices = devices;
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
			this.devices.remove( ((CommonTableElement)element).getAbstractDevice() );
		}
		setSize();
	}
	
	private void setSize(){
		if (!expandItem.isDisposed() && !expandItem.getControl().isDisposed()){ 
			expandItem.setHeight( 33 + 25 * elements.size());
		}

	}
}
