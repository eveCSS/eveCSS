package de.ptb.epics.eve.editor.gef.editpolicies;

import org.apache.log4j.Logger;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.editor.gef.commands.CreateSEConnection;
import de.ptb.epics.eve.editor.gef.commands.CreateSMConnection;
import de.ptb.epics.eve.editor.gef.commands.DeleteConnection;
import de.ptb.epics.eve.editor.gef.editparts.ConnectionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.gef.editparts.StartEventEditPart;
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
		EditPart sourceEditPart = request.getSourceEditPart();
		if (sourceEditPart instanceof ScanModuleEditPart) {
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
			CreateSMConnection connectionCmd = (CreateSMConnection) request
					.getStartCommand();
			connectionCmd.setTargetModule(target);
			return connectionCmd;
		} else if (sourceEditPart instanceof StartEventEditPart) {
			ScanModule target = ((ScanModuleEditPart) request
					.getTargetEditPart()).getModel();
			StartEvent source = ((StartEventEditPart) request
					.getSourceEditPart()).getModel();
			if (target.getParent() != null) {
				return null;
			}
			return new CreateSEConnection(source, target);
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
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		ConnectionEditPart conn = (ConnectionEditPart)
				request.getConnectionEditPart();
		ScanModuleEditPart source = (ScanModuleEditPart)request.getTarget();
		ScanModuleEditPart target = (ScanModuleEditPart)conn.getTarget();
		String type = ((ScanModuleFigure) this.scanModuleEditPart.getFigure())
				.getConnectionType(request.getLocation());
		if (source == target || type.equals(Connector.APPENDED)
				&& source.getModel().getAppended() != null
				|| type.equals(Connector.NESTED)
				&& source.getModel().getNested() != null) {
			return null;
		}
		if (type.equals(Connector.APPENDED)) {
			return new DeleteConnection(conn.getModel())
					.chain(new CreateSMConnection(source.getModel(), target
							.getModel(), Connector.APPENDED));
		} else if (type.equals(Connector.NESTED)) {
			return new DeleteConnection(conn.getModel())
			.chain(new CreateSMConnection(source.getModel(), target
					.getModel(), Connector.NESTED));
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		if (!(request.getTarget() instanceof ScanModuleEditPart)) {
			return null;
		}
		ConnectionEditPart conn = (ConnectionEditPart)
				request.getConnectionEditPart();
		ScanModuleEditPart target = (ScanModuleEditPart)request.getTarget();
		if (target.getModel().getParent() != null) {
			return null;
		}
		if (conn.getModel().getParentEvent() != null) {
			return new DeleteConnection(conn.getModel())
					.chain(new CreateSEConnection(conn.getModel()
							.getParentEvent(), target.getModel()));
		} else {
			ScanModuleEditPart source = (ScanModuleEditPart) conn.getSource();
			if (target == source) {
				return null;
			}
			if (conn.getModel().getParentScanModule().getAppended() != null
					&& conn.getModel().getParentScanModule().getAppended() == 
						conn.getModel()) {
				return new DeleteConnection(conn.getModel())
						.chain(new CreateSMConnection(conn.getModel()
								.getParentScanModule(), target.getModel(),
								Connector.APPENDED));
			}
			return new DeleteConnection(conn.getModel())
					.chain(new CreateSMConnection(conn.getModel()
							.getParentScanModule(), target.getModel(),
							Connector.NESTED));
		}
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
			figure.setAppendedFeedback(false);
			figure.setNestedFeedback(false);
			figure.setParentFeedback(false);
			String type = figure.getConnectionType(
					((CreateConnectionRequest) request).getLocation());
			if (request.getType().equals(RequestConstants.REQ_CONNECTION_START)) {
				if (type.equals(Connector.APPENDED)) {
					if (this.scanModuleEditPart.getModel().getAppended() == null) {
						figure.setAppendedFeedback(true);
						logger.debug("appended feedback");
					} else {
						logger.debug("no append possible");
					}
				} else if (type.equals(Connector.NESTED)) {
					if (this.scanModuleEditPart.getModel().getNested() == null) {
					figure.setNestedFeedback(true);
					logger.debug("nested feedback");
					} else {
						logger.debug("no append possible");
					}
				}
			} else if (request.getType().equals(
					RequestConstants.REQ_CONNECTION_END)) {
				ScanModuleEditPart target = (ScanModuleEditPart) 
						((CreateConnectionRequest) request).getTargetEditPart();
				EditPart source = ((CreateConnectionRequest) request)
						.getSourceEditPart();
				if (source instanceof ScanModuleEditPart) {
					if (source != target && target.getModel().getParent() == null) {
						figure.setParentFeedback(true);
						logger.debug("parent feedback");
					} else {
						logger.debug("no parent possible");
					}
				} else if (source instanceof StartEventEditPart) {
					if (target.getModel().getParent() == null) {
						figure.setParentFeedback(true);
						logger.debug("parent feedback");
					} else {
						logger.debug("no parent possible");
					}
				}
			}
			this.scanModuleEditPart.getFigure().repaint();
		} else if (request instanceof ReconnectRequest) {
			if (request.getType().equals(RequestConstants.REQ_RECONNECT_SOURCE)) {
				EditPart source = ((ReconnectRequest) request)
						.getConnectionEditPart().getTarget();
				EditPart target = ((ReconnectRequest) request).getTarget();
				if (source == target) {
					return;
				}
				ScanModuleFigure figure = (ScanModuleFigure) 
						this.scanModuleEditPart.getFigure();
				figure.setAppendedFeedback(false);
				figure.setNestedFeedback(false);
				figure.setParentFeedback(false);
				String type = figure.getConnectionType(
						((ReconnectRequest) request).getLocation());
				if (type.equals(Connector.APPENDED)) {
					if (this.scanModuleEditPart.getModel().getAppended() == null) {
						figure.setAppendedFeedback(true);
						logger.debug("appended feedback");
					} else {
						logger.debug("no append possible");
					}
				} else if (type.equals(Connector.NESTED)) {
					if (this.scanModuleEditPart.getModel().getNested() == null) {
					figure.setNestedFeedback(true);
					logger.debug("nested feedback");
					} else {
						logger.debug("no append possible");
					}
				}
			} else if (request.getType().equals(
					RequestConstants.REQ_RECONNECT_TARGET)) {
				EditPart source = ((ReconnectRequest) request)
						.getConnectionEditPart().getSource();
				EditPart target = ((ReconnectRequest) request).getTarget();
				if (target instanceof ScanModuleEditPart) {
					ScanModuleEditPart smep = (ScanModuleEditPart) target;
					if (target != source && smep.getModel().getParent() == null) {
						// new target is not the source and is not connected
						((ScanModuleFigure) smep.getFigure())
								.setParentFeedback(true);
					}
				}
			}
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
			figure.setAppendedFeedback(false);
			figure.setNestedFeedback(false);
			figure.setParentFeedback(false);
			this.scanModuleEditPart.getFigure().repaint();
		} else if (request instanceof ReconnectRequest) {
			if (request.getType().equals(RequestConstants.REQ_RECONNECT_SOURCE)) {
				EditPart target = ((ReconnectRequest) request).getTarget();
				if (target instanceof ScanModuleEditPart) {
					ScanModuleFigure figure = (ScanModuleFigure) 
							((ScanModuleEditPart) target).getFigure();
					figure.setAppendedFeedback(false);
					figure.setNestedFeedback(false);
					figure.setParentFeedback(false);
				}
			} else if (request.getType().equals(
					RequestConstants.REQ_RECONNECT_TARGET)) {
				ScanModuleFigure figure = (ScanModuleFigure) 
						((ScanModuleEditPart) ((ReconnectRequest) request)
						.getTarget()).getFigure();
				figure.setAppendedFeedback(false);
				figure.setNestedFeedback(false);
				figure.setParentFeedback(false);
			}
		}
		super.eraseTargetFeedback(request);
	}
}