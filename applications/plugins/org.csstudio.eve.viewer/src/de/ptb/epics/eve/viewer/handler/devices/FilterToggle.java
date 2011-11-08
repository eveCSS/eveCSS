package de.ptb.epics.eve.viewer.handler.devices;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * <code>FilterToggle</code> is the default handler for the filter commands 
 * used in the toolbar of the 
 * {@link de.ptb.epics.eve.viewer.views.devicesview.DevicesView}.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class FilterToggle extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// get the corresponding command
		Command command = event.getCommand();
		// toggle the state and save the old value
		boolean oldValue = HandlerUtil.toggleCommandState(command);
		// notify interested parties about the toggle change
		fireHandlerChanged(new HandlerEvent(this, oldValue, !oldValue));
		return null;
	}
}