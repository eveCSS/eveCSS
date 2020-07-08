package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import javafx.collections.ListChangeListener;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AxesContentProvider implements IStructuredContentProvider, ListChangeListener<Axis> {
	private TableViewer viewer;
	private ScanModule scanModule;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer)viewer;
		if (oldInput != null) {
			((ScanModule)oldInput).removeAxisChangeListener(this);
		}
		if (newInput != null) {
			((ScanModule)newInput).addAxisChangeListener(this);
			this.scanModule = ((ScanModule)newInput);
		} else {
			this.scanModule = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return ((ScanModule) inputElement).getAxes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onChanged(Change<? extends Axis> change) {
		this.viewer.refresh();
		while (change.next()) {
			if (change.wasAdded()) {
				for (Axis axis : change.getAddedSubList()) {
					this.viewer.setSelection(new StructuredSelection(axis), true);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (this.scanModule != null) {
			this.scanModule.removeAxisChangeListener(this);
		}
	}
}
