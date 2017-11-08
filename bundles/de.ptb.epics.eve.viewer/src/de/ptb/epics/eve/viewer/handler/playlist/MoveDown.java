package de.ptb.epics.eve.viewer.handler.playlist;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.messagesview.Levels;
import de.ptb.epics.eve.viewer.views.messagesview.ViewerMessage;

/**
 * @author Marcus Michalsky
 * @since 1.29
 */
public class MoveDown extends AbstractHandler {
	private static final Logger LOGGER = Logger
			.getLogger(MoveDown.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		for (Object o : ((IStructuredSelection)selection).toList()) {
			if (o instanceof PlayListEntry) {
				final PlayListEntry entry = (PlayListEntry)o;
				Activator.getDefault().getMessageList().add(
						new ViewerMessage(Levels.INFO, 
										  "Moved down " + 
										  entry.toString()));
				Activator.getDefault().getEcp1Client().getPlayListController().
						movePlayListEntry(entry, 1);
			} else {
				LOGGER.warn("Item " + o.toString() + " could not be moved.");
			}
		}
		return null;
	}
}