package de.ptb.epics.eve.editor;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * <code>EveEditorPerspectiveListener</code> listens to perspective changes of
 * the {@link EveEditorPerspective}.
 * <p>
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class EveEditorPerspectiveListener implements IPerspectiveListener {

	private static Logger logger = Logger
			.getLogger(EveEditorPerspectiveListener.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perspectiveActivated(IWorkbenchPage page,
			IPerspectiveDescriptor perspective) {
		if (logger.isDebugEnabled()) {
			logger.debug("Perspective enabled: " + perspective.getId());
		}
		if (perspective.getId().equals("EveEditorPerpective")) {
			final String name = Activator.getDefault().getMeasuringStation()
					.getName() == null ? "" : Activator.getDefault()
					.getMeasuringStation().getName();
			page.getWorkbenchWindow().getShell().setText(
				Activator.getDefault().getDefaultWindowTitle()
					+ " - "
					+ name
					+ " (XML v"
					+ Activator.getDefault()
					.getMeasuringStation().getVersion()
					+ ")");
			
			IViewReference[] references = page.getViewReferences();
			for (IViewReference reference : references) {
				try {
					page.showView(reference.getId(), null, 
							IWorkbenchPage.VIEW_CREATE);
				} catch (PartInitException e) {
					logger.error(e.getMessage(), e);
				}
			}
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