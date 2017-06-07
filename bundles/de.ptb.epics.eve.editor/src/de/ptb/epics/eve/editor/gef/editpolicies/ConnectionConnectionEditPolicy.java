package de.ptb.epics.eve.editor.gef.editpolicies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.ptb.epics.eve.editor.gef.commands.DeleteConnection;
import de.ptb.epics.eve.editor.gef.editparts.ConnectionEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class ConnectionConnectionEditPolicy extends ConnectionEditPolicy {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getDeleteCommand(GroupRequest request) {
		return new DeleteConnection(((ConnectionEditPart) this.getHost())
						.getModel());
	}
}