package de.ptb.epics.eve.editor.handler.prepostposplotview;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.PrePostscanEntry;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class RemovePrePostscanEntriesDefaultHandler extends AbstractHandler {
	public static String ID = 
		"de.ptb.epics.eve.editor.command.scanmodule.removeprepostscanentries";
	
	public static final Logger LOGGER = Logger.getLogger(
			RemovePrePostscanEntriesDefaultHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AbstractScanModuleView) {
			ScanModule scanModule = ((AbstractScanModuleView)activePart).
					getScanModule();
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if (selection instanceof IStructuredSelection) {
				for (Object o : ((IStructuredSelection)selection).toList()) {
					PrePostscanEntry entry = (PrePostscanEntry)o;
					LOGGER.debug("Entry is " + entry.getDevice().getName());
					if (entry.getPrescan() != null) {
						Prescan prescan = entry.getPrescan();
						LOGGER.debug("removing prescan " + prescan.
								getAbstractPrePostscanDevice().getName());
						entry.setPrescan(null);
						scanModule.remove(prescan);
					}
					if (entry.getPostscan() != null) {
						Postscan postscan = entry.getPostscan();
						LOGGER.debug("removing postscan " + postscan.
								getAbstractPrePostscanDevice().getName());
						entry.setPostscan(null);
						scanModule.remove(postscan);
					}
				}
			} else {
				String message = "Selection is not a structured selection";
				LOGGER.error(message);
				throw new ExecutionException(message);
			}
		} else {
			String message = "Active Part is not an AbstractScanModuleView";
			LOGGER.error(message);
			throw new ExecutionException(message);
		}
		return null;
	}
}
