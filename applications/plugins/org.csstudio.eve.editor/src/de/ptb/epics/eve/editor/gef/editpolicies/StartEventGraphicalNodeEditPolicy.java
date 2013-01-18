package de.ptb.epics.eve.editor.gef.editpolicies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.editor.gef.commands.CreateSEConnection;
import de.ptb.epics.eve.editor.gef.editparts.StartEventEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.9
 */
public class StartEventGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest arg0) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		StartEvent startEvent = ((StartEventEditPart) request
				.getTargetEditPart()).getModel();
		return new CreateSEConnection(startEvent, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest arg0) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest arg0) {
		return null;
	}
}