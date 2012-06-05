package de.ptb.epics.eve.viewer.handler.messages;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.messages.MessagesContainer;
import de.ptb.epics.eve.viewer.messages.Sources;
import de.ptb.epics.eve.viewer.views.messagesview.MessagesView;
import de.ptb.epics.eve.viewer.views.messagesview.FilterDialog;

/**
 * @author Marcus Michalsky
 * @since 1.4
 */
public class SetFilter implements IHandler {

	private static Logger logger = 
			Logger.getLogger(SetFilter.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(!(HandlerUtil.getActivePart(event) instanceof MessagesView)) {
			logger.warn("Could not clear messages. " +
						"Active part not found.");
			return null;
		}
		final FilterDialog messagesFilterDialog = 
				new FilterDialog(HandlerUtil.getActivePart(event).getSite().
						getShell());
		messagesFilterDialog.setBlockOnOpen(true);
		if(messagesFilterDialog.open() == TitleAreaDialog.OK) {
			MessagesContainer messagesContainer = Activator.getDefault().
					getMessagesContainer();
			messagesContainer.setSource(Sources.VIEWER,
					messagesFilterDialog.isShowApplicationMessages());
			messagesContainer.setSource(Sources.ENGINE, 
					messagesFilterDialog.isShowEngineMessages());
			messagesContainer.setLevel(messagesFilterDialog.getLevel());
		} else {
			logger.debug("Dialog canceled.");
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}
}