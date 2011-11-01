package de.ptb.epics.eve.editor.graphical.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.SWT;

import de.ptb.epics.eve.data.scandescription.Connector;

/**
 * <code>EventToScanModulConnectionEditPart</code>
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class EventToScanModulConnectionEditPart extends AbstractConnectionEditPart {

	/**
	 * Constructs an <code>EventToScanModulConnectionEditPart</code>.
	 * 
	 * @param connector the connector between scan module and event
	 */
	public EventToScanModulConnectionEditPart(final Connector connector) {
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