package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.motoraxiscomposite;

import javafx.collections.ListChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.22
 */
public class ContentProvider implements IStructuredContentProvider,
		ListChangeListener<Axis> {
	private static final Logger LOGGER = Logger.getLogger(ContentProvider.class
			.getName());
	private TableViewer viewer;
	
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
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer)viewer;
		if (oldInput != null) {
			((ScanModule)oldInput).removeAxisChangeListener(this);
		}
		if (newInput != null) {
			((ScanModule)newInput).addAxisChangeListener(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onChanged(
			javafx.collections.ListChangeListener.Change<? extends Axis> change) {
		this.viewer.refresh();
		while (change.next()) {
			if (change.wasAdded()) {
				for (Axis axis : change.getAddedSubList()) {
					LOGGER.debug("set selection to: "
							+ axis.getMotorAxis().getName());
					this.viewer.setSelection(new StructuredSelection(axis),
							true);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}
}