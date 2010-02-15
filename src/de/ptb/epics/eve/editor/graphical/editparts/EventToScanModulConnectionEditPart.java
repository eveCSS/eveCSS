package de.ptb.epics.eve.editor.graphical.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;

import de.ptb.epics.eve.data.scandescription.Connector;

public class EventToScanModulConnectionEditPart extends AbstractConnectionEditPart {

	public EventToScanModulConnectionEditPart( final Connector connector ) {
		this.setModel( connector );
	}

	@Override
	protected IFigure createFigure() {
		PolylineConnection connection = new PolylineConnection();
		PolygonDecoration decoration = new PolygonDecoration();
	    decoration.setTemplate( PolygonDecoration.TRIANGLE_TIP );
	    connection.setTargetDecoration( decoration );
		return connection;
	}
	
	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}

}
