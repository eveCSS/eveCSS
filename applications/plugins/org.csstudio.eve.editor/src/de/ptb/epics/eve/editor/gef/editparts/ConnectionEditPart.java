package de.ptb.epics.eve.editor.gef.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.SWT;

import de.ptb.epics.eve.data.scandescription.Connector;

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
		this.setModel(connector);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		PolylineConnection connection = new PolylineConnection();
		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setTemplate(PolygonDecoration.TRIANGLE_TIP);
		connection.setTargetDecoration(decoration);
		connection.setAntialias(SWT.ON);
		return connection;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
	}
}