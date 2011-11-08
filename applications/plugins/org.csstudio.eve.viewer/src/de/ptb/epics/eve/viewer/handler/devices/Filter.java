package de.ptb.epics.eve.viewer.handler.devices;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * <code>Filter</code> is the default handler of the <code>Filter</code> 
 * command, used in the toolbar of the 
 * {@link de.ptb.epics.eve.viewer.views.devicesview.DevicesView} for filtering.
 * Does nothing yet. Used as a drop down button. Future implementations maybe 
 * could trigger multiple filter options at once.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class Filter extends AbstractHandler {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return null;
	}
}