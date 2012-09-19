package de.ptb.epics.eve.editor.gef.figures;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
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
public class ScanModuleFigure extends Shape {

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
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		if (name == null) {
			this.name = "";
		} else {
			this.name = name;
		}
		this.repaint();
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
		this.refreshAnchors();
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
		this.refreshAnchors();
	}
	
	/*
	 * 
	 */
	private void refreshAnchors() {
		if (this.targetAnchor != null) {
			this.targetAnchor.setLocation(new Point(this.x, this.y
					+ this.height / 2));
		}
		if (this.appendedAnchor != null) {
			this.appendedAnchor.setLocation(new Point(this.x + this.width,
					this.y + this.height / 2));
		}
		if (this.nestedAnchor != null) {
			this.nestedAnchor.setLocation(new Point(this.x + this.width / 2,
					this.y + this.height));
		}
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
		if (this.appendedAnchor == null) {
			this.appendedAnchor = new XYAnchor(new Point(this.x + this.width,
					this.y + this.height / 2));
		}
		return appendedAnchor;
	}

	/**
	 * Returns the anchor for nested scan modules.
	 * 
	 * @return the <code>XYAnchor</code> for nested scan modules
	 */
	public XYAnchor getNestedAnchor() {
		if (this.nestedAnchor == null) {
			this.nestedAnchor = new XYAnchor(new Point(this.x + this.width / 2,
					this.y + this.height));
		}
		return nestedAnchor;
	}

	/**
	 * Returns the anchor targeting the scan module.
	 * 
	 * @return the <code>XYAnchor</code> targeting the scan module
	 */
	public XYAnchor getTargetAnchor() {
		if (this.targetAnchor == null) {
			this.targetAnchor = new XYAnchor(new Point(this.x, this.y
					+ this.height / 2));
		}
		return targetAnchor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillShape(Graphics graphics) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void outlineShape(Graphics graphics) {
	}
}