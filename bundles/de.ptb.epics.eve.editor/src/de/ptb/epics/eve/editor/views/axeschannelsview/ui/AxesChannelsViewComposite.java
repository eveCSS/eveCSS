package de.ptb.epics.eve.editor.views.axeschannelsview.ui;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * AxesChannelsViewComposite contains elements common to all composites in the 
 * AxesChannelsView.
 * 
 * <ul>
 *  <li>Selections are "forwarded" as follows:
 *    <ul>
 *      <li>If the (first) selected element is a <code>ScanModuleEditPart</code> and of the correct type (determined by {@link #getType()}: calls {@link #setScanModule(ScanModule)} with the model object</li>
 *      <li>If selected element is <code>ChainEditPart</code> or <code>ScanDescriptionEditPart</code> calls {@link #setScanModule(ScanModule)} with <code>null</code></li>
 *    </ul>
 *  </li>
 * </ul>
 * 
 * @author Marcus Michalsky
 * @since 1.34
 */
public abstract class AxesChannelsViewComposite extends Composite implements 
		ISelectionListener, IModelUpdateListener {
	private AxesChannelsView parentView;

	public AxesChannelsViewComposite(AxesChannelsView parentView, 
			Composite parent, int style) {
		super(parent, style);
		this.parentView = parentView;
		this.parentView.getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof IStructuredSelection)
				|| ((IStructuredSelection) selection).size() == 0
				|| ((IStructuredSelection) selection).size() > 1) {
			return;
		}
		Object o = ((IStructuredSelection)selection).getFirstElement();
		if (o instanceof ScanModuleEditPart) {
			ScanModule scanModule = (ScanModule)((EditPart)o).getModel();
			if (scanModule.getType().equals(this.getType())) {
				this.setScanModule(scanModule);
			} else {
				setScanModule(null);
			}
		} else if (o instanceof ChainEditPart) {
			// clicking empty space in the editor
			setScanModule(null);
		} else if (o instanceof ScanDescriptionEditPart) {
			setScanModule(null);
		}
	}
	
	/**
	 * Returns the scan module view this composite belongs to.
	 * @return the scan module view this composite belongs to
	 */
	protected AxesChannelsView getParentView() {
		return this.parentView;
	}
	
	/**
	 * Returns the scanmodule type the composite is responsible for.
	 * @return the scanmodule type the composite is responsible for
	 */
	protected abstract ScanModuleTypes getType();
	
	/**
	 * Called with the selected scanmodule if current selection is a scan module and of the correct type.
	 * Called with <code>null</code> if selected element is <code>ChainEditPart</code> or <code>ScanDescriptionEditPart</code>.
	 * 
	 * @param scanModule the currently selected scanmodule
	 */
	protected abstract void setScanModule(ScanModule scanModule);
	
	/**
	 * Called by the parentView when saving memento. Composites should save the 
	 * configuration that should be persisted here. 
	 * @param memento the memento interface
	 */
	protected abstract void saveState(IMemento memento);
	
	/**
	 * Called by the parentView to indicate that persisted states can be 
	 * retrieved now. Only called if memento is not <code>null</code>.
	 * @param memento the memento interface
	 */
	protected abstract void restoreState(IMemento memento);
}