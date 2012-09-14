package de.ptb.epics.eve.editor.gef.editpolicies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.commands.DeleteScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanModuleComponentEditPolicy extends ComponentEditPolicy {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getDeleteCommand(GroupRequest deleteRequest) {
		Object parent = getHost().getParent().getModel();
		Object child = getHost().getModel();
		if (parent instanceof Chain && child instanceof ScanModule) {
			return new DeleteScanModule((Chain)parent, (ScanModule)child);
		}
		return super.createDeleteCommand(deleteRequest);
	}
}
