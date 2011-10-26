package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * <code>DeviceOptionsViewPartListener</code> listens to changes in parts.
 * <p>
 *  Used to update the currently active 
 * {@link de.ptb.epics.eve.viewer.views.deviceoptionsview.DeviceOptionsView}.
 * If such a part is made visible, it is set as the current one.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class DeviceOptionsViewPartListener implements IPartListener2 {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		if(!partRef.getId().equals("DeviceOptionsView")) return;
		DeviceOptionsView.activeDeviceOptionsView = 
			((IViewReference)partRef).getView(false).
			getViewSite().getSecondaryId();
	}
}