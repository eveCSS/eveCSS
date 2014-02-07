package de.ptb.epics.eve.viewer.views.deviceinspectorview.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement;

/**
 * <code>CommonTableContentProvider</code> is a content provider for the table 
 * viewers defined in 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.ui.DeviceInspectorView}.
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
	
	/**
	 * Moves a given item to a given position.
	 * 
	 * @param dragItem the item to be moved
	 * @param predecessor the item after which the drag item should be inserted
	 * @throws IllegalArgumentException if either dragItem or predecessor are 
	 * 			not found
	 */
	public void moveItem(CommonTableElement dragItem,
			CommonTableElement predecessor) throws IllegalArgumentException {
		int dragIndex = this.elements.indexOf(dragItem);
		int predIndex = this.elements.indexOf(predecessor);
		if (dragIndex == -1) {
			throw new IllegalArgumentException("drag item not found.");
		}
		if (predIndex == -1) {
			throw new IllegalArgumentException("predecessor not found.");
		}
		int dragIndexDevice = this.devices
				.indexOf(dragItem.getAbstractDevice());
		int predIndexDevice = this.devices.indexOf(predecessor
				.getAbstractDevice());
		if (dragIndex < predIndex) {
			Collections.rotate(this.elements.subList(dragIndex, predIndex + 1),
					-1);
			Collections.rotate(
					this.devices.subList(dragIndexDevice, predIndexDevice + 1),
					-1);
		} else if (dragIndex > predIndex) {
			Collections.rotate(
					this.elements.subList(predIndex + 1, dragIndex + 1), 1);
			Collections.rotate(this.devices.subList(predIndexDevice + 1,
					dragIndexDevice + 1), 1);
		}
		this.viewer.refresh();
	}
}