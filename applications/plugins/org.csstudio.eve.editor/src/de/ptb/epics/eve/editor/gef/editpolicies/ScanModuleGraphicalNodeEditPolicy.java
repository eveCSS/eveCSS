package de.ptb.epics.eve.editor.gef.editpolicies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.commands.CreateSMConnection;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanModuleGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	private ScanModule scanModule;
	
	/**
	 * Constructor.
	 * 
	 * @param scanModule the scanModule
	 */
	public ScanModuleGraphicalNodeEditPolicy(ScanModule scanModule) {
		this.scanModule = scanModule;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		Command startCmd = request.getStartCommand();
		if (!(startCmd instanceof CreateSMConnection)) {
			return null;
		}
		CreateSMConnection connectionCmd = (CreateSMConnection)startCmd;
		
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		request.setStartCommand(new CreateSMConnection(this.scanModule, null,
				Connector.APPENDED));
		return request.getStartCommand();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}