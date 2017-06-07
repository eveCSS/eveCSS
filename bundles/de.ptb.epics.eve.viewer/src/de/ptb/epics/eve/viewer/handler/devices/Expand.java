package de.ptb.epics.eve.viewer.handler.devices;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.viewer.views.devicesview.DevicesView;

/**
 * <code>Expand</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class Expand extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(HandlerUtil.getActivePart(event) instanceof DevicesView) {
			((DevicesView)HandlerUtil.getActivePart(event)).expandAll();
		}
		return null;
	}
}