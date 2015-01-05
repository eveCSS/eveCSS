package de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite;

import org.eclipse.jface.viewers.StructuredSelection;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.views.scanmoduleview.CompositeContentProvider;

/**
 * @author Marcus Michalsky
 * @since 1.2
 * @see de.ptb.epics.eve.editor.views.scanmoduleview.CompositeContentProvider
 */
public class ContentProvider extends CompositeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		super.updateEvent(modelUpdateEvent);
		this.getTableViewer().setSelection(
				new StructuredSelection(this.getScanModule().getAxes()[this
						.getScanModule().getAxes().length - 1]), true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return ((ScanModule) inputElement).getAxes();
	}
}