package de.ptb.epics.eve.editor.gef.figures;

import org.apache.log4j.Logger;
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

	private static Logger logger = Logger.getLogger(StartEventFigure.class
			.getName());
	
	private String id;
	private XYAnchor sourceAnchor;
	
	private int x;
	private int y;
	private int width;
	private int height;
	private int diameter;
	
	private StartEventFigure self;
	
	/**
	 * Constructor.
	 * 
	 * @param id the event id
	 * @param x x position
	 * @param y y position
	 */
	public StartEventFigure(String id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = 60;
		this.height = 40;
		this.diameter = 10;
		this.setSize(this.width, this.height);
		this.setLocation(new Point(this.x, this.y));
		this.sourceAnchor = null;
		this.self = this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Graphics graphics) {
		graphics.setAntialias(SWT.ON);
		graphics.setTextAntialias(SWT.ON);
		// get current background color
		Color color = graphics.getBackgroundColor();
		// set background color to black (for our event)
		graphics.setBackgroundColor(ColorConstants.black);
		// our event is oval and will be filled with black
		graphics.fillOval((this.getLocation().x + width)/2, 
				this.getLocation().y,
				this.diameter, this.diameter);
		// reset previous background color
		graphics.setBackgroundColor(color);
		// show the identifier of the event
		int charWidth = graphics.getFontMetrics().getAverageCharWidth();
		int location = this.id.length() * charWidth / 2;
		graphics.drawText(
				this.id, this.x + this.width/2 - location,
				this.getLocation().y + this.diameter);
		super.paint(graphics);
	}
	
	/**
	 * Returns the source of the event.
	 * 
	 * @return the <code>XYAnchor</code> which is the source of the event
	 */
	public XYAnchor getSourceAnchor() {
		logger.debug("get Anchor");
		if (this.sourceAnchor == null) {
			this.sourceAnchor = new XYAnchor(new Point(
					(this.getLocation().x + width) / 2 + this.diameter/2,
					this.getLocation().y + this.diameter/2)) {
				@Override
				public Point getLocation(Point reference) {
					Rectangle bounds = Rectangle.SINGLETON;
					bounds.setBounds(self.getBounds());
					self.translateToAbsolute(bounds);
					return new Point(bounds.x + bounds.width/2, 
							bounds.y + diameter/2);
				}
			};
		}
		return this.sourceAnchor;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
}