package de.ptb.epics.eve.viewer;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class EveViewerPerspectiveListener implements IPerspectiveListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perspectiveActivated(IWorkbenchPage page,
			IPerspectiveDescriptor perspective) {
		if (perspective.getId().equals("EveEnginePerspective") ||
				perspective.getId().equals("EveDevicePerspective")) {
			IMeasuringStation measuringStation = Activator.getDefault()
					.getMeasuringStation();
			if (measuringStation == null) {
				page.getWorkbenchWindow().getShell().setText(
						Activator.getDefault().getDefaultWindowTitle());
				return;
			}
			final String name = measuringStation.getName() == null 
					? "" : measuringStation.getName();
			page.getWorkbenchWindow().getShell().setText(
				Activator.getDefault().getDefaultWindowTitle()
					+ " - "
					+ name
					+ " (XML v"
					+ measuringStation.getVersion()
					+ ")");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perspectiveChanged(IWorkbenchPage page,
			IPerspectiveDescriptor perspective, String changeId) {
	}
}