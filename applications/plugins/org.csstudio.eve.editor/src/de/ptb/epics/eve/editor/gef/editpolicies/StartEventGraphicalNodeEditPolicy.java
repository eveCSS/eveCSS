package de.ptb.epics.eve.editor.gef.editpolicies;

import org.apache.log4j.Logger;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.editor.gef.commands.CreateSEConnection;
import de.ptb.epics.eve.editor.gef.commands.DeleteConnection;
import de.ptb.epics.eve.editor.gef.editparts.ConnectionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.gef.editparts.StartEventEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.9
 */
public class StartEventGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	private static final Logger LOGGER = Logger
			.getLogger(StartEventGraphicalNodeEditPolicy.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		StartEvent source = ((StartEventEditPart) request
				.getTargetEditPart()).getModel();
		if (source.getConnector() != null) {
			return null;
		}
		return new CreateSEConnection(source, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		StartEventEditPart source = (StartEventEditPart) request.getTarget();
		ScanModuleEditPart target = (ScanModuleEditPart) ((ConnectionEditPart) 
				request.getConnectionEditPart()).getTarget();
		if (source.getModel().getConnector() != null && 
				source.getModel().getConnector().getChildScanModule() != null) {
			return null;
		}
		return new DeleteConnection(
				((ConnectionEditPart) request.getConnectionEditPart())
						.getModel()).chain(new CreateSEConnection(source
				.getModel(), target.getModel()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return null;
	}
}