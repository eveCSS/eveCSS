package de.ptb.epics.eve.editor.gef.figures;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ConnectionFigure extends PolylineConnection {
	private static final int LINE_WIDTH = 2;
	
	/**
	 * Constructor.
	 */
	public ConnectionFigure() {
		this.setLineWidth(LINE_WIDTH);
		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setLineWidth(LINE_WIDTH);
		decoration.setTemplate(PolygonDecoration.TRIANGLE_TIP);
		decoration.setAntialias(SWT.ON);
		this.setTargetDecoration(decoration);
		this.setAntialias(SWT.ON);
	}
}