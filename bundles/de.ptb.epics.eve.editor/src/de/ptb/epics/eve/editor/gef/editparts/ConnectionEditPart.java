package de.ptb.epics.eve.editor.gef.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.editor.gef.editpolicies.ConnectionConnectionEditPolicy;
import de.ptb.epics.eve.editor.gef.figures.ConnectionFigure;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ConnectionEditPart extends AbstractConnectionEditPart {

	/**
	 * Constructor.
	 * 
	 * @param connector the model to set
	 */
	public ConnectionEditPart(Connector connector) {
		super();
		this.setModel(connector);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		return new ConnectionFigure();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Connector getModel() {
		return (Connector)super.getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, 
				new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, 
				new ConnectionConnectionEditPolicy());
	}
}