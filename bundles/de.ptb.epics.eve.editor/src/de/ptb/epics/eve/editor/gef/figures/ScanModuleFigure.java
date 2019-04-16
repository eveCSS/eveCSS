package de.ptb.epics.eve.editor.gef.figures;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.editor.Activator;

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
	
	private boolean selectedPrimary;
	private boolean containsErrors;
	
	private ScanModuleTypes type;
	
	private boolean appendedFeedback;
	private boolean nestedFeedback;
	private boolean parentFeedback;
	
	// anchor points for incoming, outgoing and nested modules
	private XYAnchor targetAnchor;
	private XYAnchor appendedAnchor;
	private XYAnchor nestedAnchor;
	
	private Shape self;
	
	/**
	 * Constructor.
	 * 
	 * @param name the name of the scan module
	 * @param type the scan module type
	 * @param x the x coordinate of its initial position
	 * @param y the y coordinate of its initial position
	 * @param width (initial) width of the figure
	 * @param height (initial) height of the figure
	 */
	public ScanModuleFigure(String name, ScanModuleTypes type, int x, int y,
			int width, int height) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.type = type;
		
		this.selectedPrimary = false;
		this.containsErrors = false;
		this.appendedFeedback = false;
		this.nestedFeedback = false;
		this.parentFeedback = false;
		
		this.setBackgroundColor(ColorConstants.white);
		this.setOpaque(true);
		this.setSize(this.width, this.height);
		this.setLocation(new Point(this.x, this.y));
		
		this.self = this;
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
	
	/**
	 * @param appendedFeedback the appended_feedback to set
	 */
	public void setAppendedFeedback(boolean appendedFeedback) {
		this.appendedFeedback = appendedFeedback;
		this.repaint();
	}

	/**
	 * @param nestedFeedback the nested_feedback to set
	 */
	public void setNestedFeedback(boolean nestedFeedback) {
		this.nestedFeedback = nestedFeedback;
		this.repaint();
	}

	/**
	 * @param parentFeedback the parent_feedback to set
	 */
	public void setParentFeedback(boolean parentFeedback) {
		this.parentFeedback = parentFeedback;
		this.repaint();
	}

	/**
	 * 
	 * @param p the point to check
	 * @return {@link de.ptb.epics.eve.data.scandescription.Connector#APPENDED} if 
	 * <code>x + 2 * width / 3 < p.x < x + width</code>, <br>
	 * {@link de.ptb.epics.eve.data.scandescription.Connector#NESTED} if 
	 * <code>y + height / 2 < p.y < y + height</code>.<br>
	 * Empty String otherwise.
	 */
	public String getConnectionType(Point p) {
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.setLocation(p.x, p.y);
		self.translateToRelative(bounds);
		
		if (logger.isDebugEnabled()) {
			logger.debug("SM Location: (" + this.x + ", " + this.y + ")");
			logger.debug("Location (absolute): (" + p.x + ", " + p.y + ")");
			logger.debug("Location (relative): (" + bounds.x + ", " + bounds.y
					+ ")");
		}
		
		if (bounds.x < this.x + this.width
				&& bounds.x > this.x + 2 * this.width / 3) {
			return Connector.APPENDED;
		} else if (bounds.y < this.y + this.height
				&& bounds.y > this.y + this.height / 2) {
			return Connector.NESTED;
		}
		return "";
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
	 * @param selected <code>true</code> if
	 */
	public void setSelected(boolean selected) {
		logger.debug(this.name + " is primary ?: " + Boolean.toString(selected));
		this.selectedPrimary = selected;
		this.repaint();
	}

	/**
	 * @param type the scan module type
	 */
	public void setType(ScanModuleTypes type) {
		this.type = type;
	}
	
	/**
	 * @param containsErrors the contains_errors to set
	 */
	public void setContainsErrors(boolean containsErrors) {
		this.containsErrors = containsErrors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		
		graphics.setAntialias(SWT.ON);
		graphics.setTextAntialias(SWT.ON);
		
		Display display = PlatformUI.getWorkbench().getDisplay();
		if(selectedPrimary) {
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
		int splineControlPoint = 8;
		
		path.moveTo(this.bounds.width + this.bounds.x - splineControlPoint, 
					this.bounds.y);
		path.quadTo(this.bounds.width + this.bounds.x, this.bounds.y, 
					this.bounds.width + this.bounds.x, this.bounds.y + 
					splineControlPoint);
		// right
		path.lineTo(this.bounds.width + this.bounds.x, 
					this.bounds.y + this.bounds.height - splineControlPoint);
		path.quadTo(this.bounds.width + this.bounds.x, 
					this.bounds.y + this.bounds.height, 
					this.bounds.width + this.bounds.x - splineControlPoint, 
					this.bounds.y + this.bounds.height);
		// bottom
		path.lineTo(this.bounds.x + splineControlPoint, 
					this.bounds.y + this.bounds.height);
		path.quadTo(this.bounds.x, this.bounds.y + this.bounds.height, 
					this.bounds.x, this.bounds.y + this.bounds.height - 
					splineControlPoint);
		// left
		path.lineTo(this.bounds.x, this.bounds.y + splineControlPoint);
		path.quadTo(this.bounds.x, this.bounds.y, 
					this.bounds.x + splineControlPoint, this.bounds.y);
		// top
		path.close();
		graphics.setClip(path);
		graphics.fillGradient(this.bounds, true);
		
		// red error bar if module contains errors
		if(containsErrors) {
			graphics.setForegroundColor(ColorConstants.white);
			graphics.setBackgroundColor(ColorConstants.red);
			graphics.fillGradient(getLocation().x, getLocation().y, 
								this.bounds.width, 5, false);
		}
		
		if (this.appendedFeedback) {
			graphics.setForegroundColor(ColorConstants.lightGray);
			graphics.setBackgroundColor(ColorConstants.black);
			graphics.fillGradient(new Rectangle(this.x + 6* this.width/7,
					this.y, this.width/7, this.height), false);
		}
		if (this.nestedFeedback) {
			if (selectedPrimary) {
				graphics.setForegroundColor(ColorConstants.white);
				graphics.setBackgroundColor(ColorConstants.darkGray);
			} else {
				graphics.setForegroundColor(ColorConstants.lightGray);
				graphics.setBackgroundColor(ColorConstants.darkGray);
			}
			graphics.fillGradient(new Rectangle(this.x,
					this.y + 3*this.height/4, this.width, this.height/4), true);
		}
		if (this.parentFeedback) {
			graphics.setForegroundColor(ColorConstants.black);
			graphics.setBackgroundColor(ColorConstants.lightGray);
			graphics.fillGradient(new Rectangle(this.x,
					this.y, this.width/7, this.height), false);
		}
		
		// draw border
		graphics.setForegroundColor(ColorConstants.black);
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.setLineWidth(2);
		graphics.drawPath(path);
		
		// draw scan module name
		if (this.type.equals(ScanModuleTypes.CLASSIC)) {
			graphics.drawText(
				this.name, this.getLocation().x + 5, this.getLocation().y + 8);
		} else if (this.type.equals(ScanModuleTypes.SAVE_AXIS_POSITIONS)) {
			Image save = Activator.getDefault().getImageRegistry().get("SAVE");
			Image axis = Activator.getDefault().getImageRegistry().get("AXIS");
			graphics.drawImage(save, 
					new Point(this.x + this.width/2 - save.getBounds().width - 3,
							this.y + this.height/2 - save.getBounds().height/2));
			graphics.drawImage(axis, 
					new Point(this.x + this.width/2 + 3, 
							this.y + this.height/2 - save.getBounds().height/2));
		} else if (this.type.equals(ScanModuleTypes.SAVE_CHANNEL_VALUES)) {
			Image save = Activator.getDefault().getImageRegistry().get("SAVE");
			Image channel = Activator.getDefault().getImageRegistry().get("CHANNEL");
			graphics.drawImage(save, 
					new Point(this.x + this.width/2 - save.getBounds().width - 3,
							this.y + this.height/2 - save.getBounds().height/2));
			graphics.drawImage(channel, 
					new Point(this.x + this.width/2 + 3, 
							this.y + this.height/2 - save.getBounds().height/2));
		} else if (this.type.equals(ScanModuleTypes.DYNAMIC_AXIS_POSITIONS)) {
			Image date = Activator.getDefault().getImageRegistry().get("DATE");
			Image axis = Activator.getDefault().getImageRegistry().get("AXIS");
			graphics.drawImage(date, 
					new Point(this.x + this.width/2 - date.getBounds().width - 3,
							this.y + this.height/2 - date.getBounds().height/2));
			graphics.drawImage(axis, 
					new Point(this.x + this.width/2 + 3, 
							this.y + this.height/2 - date.getBounds().height/2));
		} else if (this.type.equals(ScanModuleTypes.DYNAMIC_CHANNEL_VALUES)) {
			Image date = Activator.getDefault().getImageRegistry().get("DATE");
			Image channel = Activator.getDefault().getImageRegistry().get("CHANNEL");
			graphics.drawImage(date, 
					new Point(this.x + this.width/2 - date.getBounds().width - 3,
							this.y + this.height/2 - date.getBounds().height/2));
			graphics.drawImage(channel, 
					new Point(this.x + this.width/2 + 3, 
							this.y + this.height/2 - date.getBounds().height/2));
		}
		
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
					this.y + this.height / 2)) {
				@Override
				public Point getLocation(Point reference) {
					Rectangle bounds = Rectangle.SINGLETON;
					bounds.setBounds(self.getBounds());
					self.translateToAbsolute(bounds);
					return new Point(bounds.x + bounds.width, bounds.y
							+ bounds.height / 2);
				}
			};
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
					this.y + this.height)) {
				@Override
				public Point getLocation(Point reference) {
					Rectangle bounds = Rectangle.SINGLETON;
					bounds.setBounds(self.getBounds());
					self.translateToAbsolute(bounds);
				return new Point(bounds.x + bounds.width / 2, bounds.y
						+ bounds.height);
				}
			};
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
					+ this.height / 2)){
				@Override
				public Point getLocation(Point reference) {
					Rectangle bounds = Rectangle.SINGLETON;
					bounds.setBounds(self.getBounds());
					self.translateToAbsolute(bounds);
					return new Point(bounds.x, bounds.y + bounds.height/2);
				}
			};
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