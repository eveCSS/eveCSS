package de.ptb.epics.eve.editor.gef.editpolicies;

import org.apache.log4j.Logger;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.commands.CreateSEConnection;
import de.ptb.epics.eve.editor.gef.commands.CreateSMConnection;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.gef.figures.ScanModuleFigure;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanModuleGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	private static Logger logger = Logger
			.getLogger(ScanModuleGraphicalNodeEditPolicy.class.getName());
	
	private ScanModule scanModule;
	private ScanModuleEditPart scanModuleEditPart;
	
	/**
	 * Constructor.
	 * 
	 * @param scanModuleEditPart  the corresponding edit part#
	 */
	public ScanModuleGraphicalNodeEditPolicy(ScanModuleEditPart scanModuleEditPart) {
		this.scanModule = scanModuleEditPart.getModel();
		this.scanModuleEditPart = scanModuleEditPart;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		Command startCmd = request.getStartCommand();
		if (startCmd instanceof CreateSMConnection) {
			ScanModule target = ((ScanModuleEditPart) request
					.getTargetEditPart()).getModel();
			ScanModule source = ((ScanModuleEditPart) request
					.getSourceEditPart()).getModel();
			if (source == target) {
				return null;
			}
			if (target.getParent() != null) {
				return null;
			}
			CreateSMConnection connectionCmd = (CreateSMConnection)startCmd;
			connectionCmd.setTargetModule(target);
			return connectionCmd;
		} else if (startCmd instanceof CreateSEConnection) {
			
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		String type = ((ScanModuleFigure) this.scanModuleEditPart.getFigure())
				.getConnectionType(request.getLocation());
		if (type.equals(Connector.APPENDED)) {
			if (this.scanModule.getAppended() != null) {
				return null;
			} 
			request.setStartCommand(new CreateSMConnection(this.scanModule,
					null, Connector.APPENDED));
			return request.getStartCommand();
		} else if (type.equals(Connector.NESTED)) {
			if (this.scanModule.getNested() != null) {
				return null;
			} 
			request.setStartCommand(new CreateSMConnection(this.scanModule,
					null, Connector.NESTED));
			return request.getStartCommand();
		}
		return null;
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showTargetFeedback(Request request) {
		logger.debug("Request: " + request.getType().toString());
		if (request instanceof CreateConnectionRequest) {
			ScanModuleFigure figure = (ScanModuleFigure) 
					this.scanModuleEditPart.getFigure();
			figure.setAppended_feedback(false);
			figure.setNested_feedback(false);
			figure.setParent_feedback(false);
			String type = figure.getConnectionType(
					((CreateConnectionRequest) request).getLocation());
			if (request.getType().equals(RequestConstants.REQ_CONNECTION_START)) {
				if (type.equals(Connector.APPENDED)) {
					if (this.scanModuleEditPart.getModel().getAppended() == null) {
						figure.setAppended_feedback(true);
						logger.debug("appended feedback");
					} else {
						logger.debug("no append possible");
					}
				} else if (type.equals(Connector.NESTED)) {
					if (this.scanModuleEditPart.getModel().getNested() == null) {
					figure.setNested_feedback(true);
					logger.debug("nested feedback");
					} else {
						logger.debug("no append possible");
					}
				}
			} else if (request.getType().equals(
					RequestConstants.REQ_CONNECTION_END)) {
				ScanModuleEditPart target = (ScanModuleEditPart) 
						((CreateConnectionRequest) request).getTargetEditPart();
				ScanModuleEditPart source = (ScanModuleEditPart) 
						((CreateConnectionRequest) request).getSourceEditPart();
				if (source != target && target.getModel().getParent() == null) {
					figure.setParent_feedback(true);
					logger.debug("parent feedback");
				} else {
					logger.debug("no parent possible");
				}
			}
			this.scanModuleEditPart.getFigure().repaint();
		}
		super.showTargetFeedback(request);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void eraseTargetFeedback(Request request) {
		if (request instanceof CreateConnectionRequest) {
			logger.debug("erase feedback");
			ScanModuleFigure figure = (ScanModuleFigure) 
					this.scanModuleEditPart.getFigure();
			figure.setAppended_feedback(false);
			figure.setNested_feedback(false);
			figure.setParent_feedback(false);
			this.scanModuleEditPart.getFigure().repaint();
		}
		super.eraseTargetFeedback(request);
	}
}