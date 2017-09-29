package de.ptb.epics.eve.viewer.handler.messages;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.viewer.views.messagesview.ui.MessagesView;

/**
 * @author Marcus Michalsky
 * @since 1.24
 */
public class ScrollLock extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			ScrollLock.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (HandlerUtil.getActivePart(event) instanceof MessagesView) {
			boolean currentState = HandlerUtil.toggleCommandState(event.getCommand());
			((MessagesView) HandlerUtil.getActivePart(event)).setScrollLock(!currentState);
			LOGGER.debug("scroll lock" + !currentState);
		} else {
			LOGGER.warn("no active messages view detected!");
		}
		return null;
	}
}