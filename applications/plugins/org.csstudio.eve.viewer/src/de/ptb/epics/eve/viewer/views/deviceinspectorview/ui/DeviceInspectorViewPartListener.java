package de.ptb.epics.eve.viewer.views.deviceinspectorview.ui;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPartReference;

/**
 * <code>DeviceInspectorViewPartListener</code> listens to changes in parts.
 * <p>
 *  Used to update the currently active 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.ui.DeviceInspectorView}.
 * If such a part is made visible, it is set as the current one.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class DeviceInspectorViewPartListener implements IPartListener2 {

	private static Logger logger = 
			Logger.getLogger(DeviceInspectorViewPartListener.class.getName());
	
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
		if(!partRef.getId().equals("DeviceInspectorView")) {
			return;
		}
		DeviceInspectorView.activeDeviceInspectorView = 
			((IViewReference)partRef).getView(false).
			getViewSite().getSecondaryId();
		
		logger.debug("Part " + partRef.getPartName() + " became visible");
	}
}