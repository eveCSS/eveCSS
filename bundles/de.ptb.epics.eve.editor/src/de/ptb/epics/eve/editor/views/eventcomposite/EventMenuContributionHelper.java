package de.ptb.epics.eve.editor.views.eventcomposite;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.chainview.ChainView;
import de.ptb.epics.eve.editor.views.detectorchannelview.ui.DetectorChannelView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * <code>EventMenuContributionHelper</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public final class EventMenuContributionHelper {
	
	private EventMenuContributionHelper() {
	}
	
	/**
	 * Returns the active {@link org.eclipse.ui.IViewPart} if any or
	 * <code>null</code>.
	 * 
	 * @return the active {@link org.eclipse.ui.IViewPart} if any,
	 *         <code>null</code> otherwise
	 */
	public static IViewPart getActiveViewPart() {
		IWorkbenchPart part = Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getPartService().getActivePart();
		if(part instanceof IViewPart) {
			return (IViewPart)part;
		}
		return null;
	}
	
	/**
	 * Returns the event impact (as in
	 * {@link de.ptb.epics.eve.data.EventImpacts}) if the active part is a
	 * {@link org.eclipse.ui.IViewPart} or <code>null</code>.
	 * 
	 * @return the event impact if active part is a
	 *         {@link org.eclipse.ui.IViewPart}, <code>null</code> otherwise
	 */
	public static EventImpacts determineEventImpact() {
		IViewPart activePart = getActiveViewPart();
		if(activePart instanceof ChainView) {
			int index = ((ChainView)activePart).eventsTabFolder.
					getSelectionIndex();
			switch(index) {
				case 0: return EventImpacts.REDO;
				case 1: return EventImpacts.BREAK;
				case 2: return EventImpacts.STOP;
			}
		} else if(activePart instanceof ScanModuleView) {
			int index = ((ScanModuleView) activePart).getEventsTabFolderSelectionIndex();
			switch(index) {
				case 0: return EventImpacts.REDO;
				case 1: return EventImpacts.BREAK;
				case 2: return EventImpacts.TRIGGER;
			}
		} else if(activePart instanceof DetectorChannelView) {
				return EventImpacts.REDO;
		}
		return null;
	}
}