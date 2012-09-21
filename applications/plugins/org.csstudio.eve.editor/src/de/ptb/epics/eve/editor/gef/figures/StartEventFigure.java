package de.ptb.epics.eve.editor.gef.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.6
 */
public class StartEventFigure extends Figure {

	private String id;
	
	private XYAnchor sourceAnchor;
	
	/**
	 * Constructor.
	 * 
	 * @param id the event id
	 */
	public StartEventFigure(String id) {
		this.id = id;
		this.setSize(60, 40);
		this.setLocation(new Point(20, 24));
		Rectangle rect = this.getBounds();
		this.sourceAnchor = new XYAnchor(new Point(rect.x + 8, rect.y + 10));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Graphics graphics) {
		graphics.setAntialias(SWT.ON);
		// get current background color
		Color color = graphics.getBackgroundColor();
		// set background color to black (for our event)
		graphics.setBackgroundColor(ColorConstants.black);
		// our event is oval and will be filled with black
		graphics.fillOval(
				this.getLocation().x + 3, this.getLocation().y + 5, 10, 10);
		// reset previous background color
		graphics.setBackgroundColor(color);
		// show the identifier of the event
		graphics.drawText(
				this.id, this.getLocation().x, this.getLocation().y + 16);
		super.paint(graphics);
	}
	
	/**
	 * Returns the source of the event.
	 * 
	 * @return the <code>XYAnchor</code> which is the source of the event
	 */
	public XYAnchor getSourceAnchor() {
		return this.sourceAnchor;
	}
}