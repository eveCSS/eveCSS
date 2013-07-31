package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;

/**
 * <code>CommonTableContentProvider</code> is a content provider for the table 
 * viewers defined in 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class CommonTableContentProvider implements IStructuredContentProvider {

	private List<CommonTableElement> elements;
	private TableViewer viewer;
	private final List<AbstractDevice> devices;

	/**
	 * Constructs a <code>CommonTableContentProvider</code>.
	 * 
	 * @param viewer the table viewer the content provider should be attached to
	 * @param devices
	 */
	public CommonTableContentProvider(TableViewer viewer, final List<AbstractDevice> devices) {
		this.viewer = viewer;
		elements = new ArrayList<CommonTableElement>();
		this.devices = devices;
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
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		for (CommonTableElement element : elements) {
			element.dispose();
		}
		elements.clear();
	}
	
	/**
	 * Adds the given element if it is not already present.
	 * 
	 * @precondition <code>element</code> must not be <code>null</code>
	 * @param element the element that should be added
	 * @return <code>true</code> if the element was added, 
	 * 			<code>false</code> otherwise
	 */
	public boolean addElement(CommonTableElement element) {
		if (element == null) {
			return false;
		}
		for (CommonTableElement cte : elements) {
			if (cte.getAbstractDevice().equals(element.getAbstractDevice())) {
				element.dispose();
				return false;
			}
		}
		elements.add(element);
		viewer.add(element);
		return true;
	}
	
	/**
	 * Removes the given element.
	 * 
	 * @param element the element that should be removed
	 */
	public void removeElement(Object element) {
		if (element == null) {
			return;
		}
		((CommonTableElement)element).dispose();
		if (elements.contains(element)) {
			elements.remove(element);
			viewer.remove(element);
			this.devices.remove(((CommonTableElement)element).getAbstractDevice());
		}
	}
}