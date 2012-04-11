package de.ptb.epics.eve.editor.views.eventcomposite;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.detectorchannelview.DetectorChannelView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanview.ScanView;

/**
 * <code>EventMenuContributionHelper</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class EventMenuContributionHelper {
	
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
		if(activePart instanceof ScanView) {
			int index = ((ScanView)activePart).eventsTabFolder.
					getSelectionIndex();
			switch(index) {
				case 0: return EventImpacts.PAUSE;
				case 1: return EventImpacts.REDO;
				case 2: return EventImpacts.BREAK;
				case 3: return EventImpacts.STOP;
			}
		} else if(activePart instanceof ScanModuleView) {
			int index = ((ScanModuleView)activePart).eventsTabFolder.
					getSelectionIndex();
			switch(index) {
				case 0: return EventImpacts.PAUSE;
				case 1: return EventImpacts.REDO;
				case 2: return EventImpacts.BREAK;
				case 3: return EventImpacts.TRIGGER;
			}
		} else if(activePart instanceof DetectorChannelView) {
				return EventImpacts.REDO;
		}
		return null;
	}
}