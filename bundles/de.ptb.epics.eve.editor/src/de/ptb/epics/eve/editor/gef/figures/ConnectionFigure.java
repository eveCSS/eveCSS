package de.ptb.epics.eve.editor.gef.figures;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ConnectionFigure extends PolylineConnection {

	/**
	 * Constructor.
	 */
	public ConnectionFigure() {
		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setTemplate(PolygonDecoration.TRIANGLE_TIP);
		this.setTargetDecoration(decoration);
		this.setAntialias(SWT.ON);
	}
}