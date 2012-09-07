package de.ptb.epics.eve.editor.gef.figures;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanModuleFigure extends Figure {

	private static Logger logger = Logger.getLogger(ScanModuleFigure.class
			.getName());
	
	private String name;
	private int x;
	private int y;
	private int width;
	private int height;
	
	private boolean selected_primary;
	private boolean contains_errors;
	
	// anchor points for incoming, outgoing and nested modules
	private XYAnchor targetAnchor;
	private XYAnchor appendedAnchor;
	private XYAnchor nestedAnchor;
	
	/**
	 * Constructor.
	 * 
	 * @param name the name of the scan module
	 * @param x the x coordinate of its initial position
	 * @param y the y coordinate of its initial position
	 */
	public ScanModuleFigure(String name, int x, int y, int width, int height) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.selected_primary = false;
		this.contains_errors = false;
		
		this.setBackgroundColor(ColorConstants.white);
		this.setOpaque(true);
		this.setSize(this.width, this.height);
		this.setLocation(new Point(this.x, this.y));
		
		// set anchor points
		Rectangle rect = this.getBounds();
		this.targetAnchor = new XYAnchor(new Point(rect.x, rect.y + rect.height
				/ 2));
		this.appendedAnchor = new XYAnchor(new Point(rect.x + rect.width,
				rect.y + rect.height / 2));
		this.nestedAnchor = new XYAnchor(new Point(rect.x + rect.width / 2,
				rect.y + rect.height));
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	
	/**
	 * @param active the active to set
	 */
	public void setSelected(boolean selected) {
		this.selected_primary = selected;
		this.repaint();
	}

	/**
	 * @param contains_errors the contains_errors to set
	 */
	public void setContains_errors(boolean contains_errors) {
		this.contains_errors = contains_errors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		
		graphics.setAntialias(SWT.ON);
		
		Display display = PlatformUI.getWorkbench().getDisplay();
		if(selected_primary) {
			// if it is active (selected) -> change color
			graphics.setForegroundColor(ColorConstants.titleGradient);
			graphics.setBackgroundColor(ColorConstants.white);
		} else {
			// inactive ones are colorless
			graphics.setForegroundColor(ColorConstants.white);
			graphics.setBackgroundColor(ColorConstants.lightGray);
		}
		
		// save the old clipping
		Rectangle oldClipping = graphics.getClip(new Rectangle());

		Path path = new Path(display);
		int spline_control_point = 8;
		
		path.moveTo(this.bounds.width + this.bounds.x - spline_control_point, 
					this.bounds.y);
		path.quadTo(this.bounds.width + this.bounds.x, this.bounds.y, 
					this.bounds.width + this.bounds.x, this.bounds.y + 
					spline_control_point);
		// right
		path.lineTo(this.bounds.width + this.bounds.x, 
					this.bounds.y + this.bounds.height - spline_control_point);
		path.quadTo(this.bounds.width + this.bounds.x, 
					this.bounds.y + this.bounds.height, 
					this.bounds.width + this.bounds.x - spline_control_point, 
					this.bounds.y + this.bounds.height);
		// bottom
		path.lineTo(this.bounds.x + spline_control_point, 
					this.bounds.y + this.bounds.height);
		path.quadTo(this.bounds.x, this.bounds.y + this.bounds.height, 
					this.bounds.x, this.bounds.y + this.bounds.height - 
					spline_control_point);
		// left
		path.lineTo(this.bounds.x, this.bounds.y + spline_control_point);
		path.quadTo(this.bounds.x, this.bounds.y, 
					this.bounds.x + spline_control_point, this.bounds.y);
		// top
		path.close();
		graphics.setClip(path);
		graphics.fillGradient(this.bounds, true);
		
		// red error bar if module contains errors
		if(contains_errors) {
			graphics.setForegroundColor(ColorConstants.white);
			graphics.setBackgroundColor(ColorConstants.red);
			graphics.fillGradient(getLocation().x, getLocation().y, 
								this.bounds.width, 5, false);
		}
		
		// draw border
		graphics.setForegroundColor(ColorConstants.black);
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.setLineWidth(2);
		graphics.drawPath(path);
		
		// draw scan module name
		graphics.drawText(
				this.name, this.getLocation().x + 5, this.getLocation().y + 8);
		
		// restore old clipping
		graphics.setClip(oldClipping);
		// free
		path.dispose();
		
		if(logger.isDebugEnabled()) {
			logger.debug("painted ScanModule: " + this.name);
		}
	}
	
	/**
	 * Returns the anchor for appended scan modules.
	 * 
	 * @return the <code>XYAnchor</code> for appended scan modules
	 */
	public XYAnchor getAppendedAnchor() {
		return appendedAnchor;
	}

	/**
	 * Returns the anchor for nested scan modules.
	 * 
	 * @return the <code>XYAnchor</code> for nested scan modules
	 */
	public XYAnchor getNestedAnchor() {
		return nestedAnchor;
	}

	/**
	 * Returns the anchor targeting the scan module.
	 * 
	 * @return the <code>XYAnchor</code> targeting the scan module
	 */
	public XYAnchor getTargetAnchor() {
		return targetAnchor;
	}
}