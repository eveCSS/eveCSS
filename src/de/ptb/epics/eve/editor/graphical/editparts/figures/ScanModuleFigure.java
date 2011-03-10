package de.ptb.epics.eve.editor.graphical.editparts.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * <code>ScanModuleFigure</code>
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ScanModuleFigure extends Figure {
	
	private int xOffset = 0;
	private int yOffset = 0;
	
	private XYAnchor targetAnchor;
	private XYAnchor appendedAnchor;
	private XYAnchor nestedAnchor;
	
	private boolean active;
	
	private String text;
	
	/**
	 * Constructs a <code>ScanModuleFigure</code>.
	 * 
	 * @param text the text content of the scan module figure
	 * @param x x coordinate of the desired location
	 * @param y y coordinate of the desired location
	 */
	public ScanModuleFigure(final String text, int x, int y) {
		this.active = false;
		this.setBackgroundColor(ColorConstants.white);
		this.setBorder(new LineBorder(ColorConstants.black, 1));
		this.setOpaque(true);
		this.setSize(70, 30);
		this.setLocation(new Point(x, y));

		this.text = text;
		this.setToolTip( new Label("Left click to edit me."));
		
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClicked(final MouseEvent me) {		
			}

			@Override
			public void mousePressed(final MouseEvent me) {
				xOffset = me.x - getLocation().x ;
				yOffset = me.y - getLocation().y;
				me.consume();
			}

			@Override
			public void mouseReleased(final MouseEvent me) {
				// if scan module reached bounds, reset it
				if (me.x - xOffset < 0)
					me.x = xOffset + 10;
				if (me.y - yOffset < 0)
					me.y = yOffset + 10;
				setLocation(new Point(me.x - xOffset, me.y - yOffset));

				Rectangle newLocation = getBounds();

				targetAnchor.setLocation(new Point(newLocation.x, newLocation.y 
												   + newLocation.height/2));
				appendedAnchor.setLocation(new Point(newLocation.x + 
													 newLocation.width, 
													 newLocation.y + 
													 newLocation.height/2));
				nestedAnchor.setLocation(new Point(newLocation.x + 
												   newLocation.width/2, 
												   newLocation.y + 
												   newLocation.height));
				xOffset = 0;
				yOffset = 0;				
			}
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(final MouseEvent me) {
				setLocation(new Point(me.x - xOffset, me.y - yOffset));
				Rectangle newLocation = getBounds();
				targetAnchor.setLocation(new Point(newLocation.x, newLocation.y 
												   + newLocation.height/2));
				appendedAnchor.setLocation(new Point(newLocation.x + 
													 newLocation.width, 
													 newLocation.y + 
													 newLocation.height/2));
				nestedAnchor.setLocation(new Point(newLocation.x + 
												   newLocation.width/2, 
												   newLocation.y + 
												   newLocation.height));
			}

			@Override
			public void mouseEntered(final MouseEvent me) {		
			}

			@Override
			public void mouseExited( final MouseEvent me ) {		
			}

			@Override
			public void mouseHover(MouseEvent me) {	
			}

			@Override
			public void mouseMoved(MouseEvent me) {
			}
			
		});
		
		Rectangle rect = this.getBounds();
		this.targetAnchor = new XYAnchor(
				new Point(rect.x, rect.y + rect.height/2));
		
		this.appendedAnchor = new XYAnchor(
				new Point(rect.x + rect.width, rect.y + rect.height/2));
		this.nestedAnchor = new XYAnchor(
				new Point(rect.x + rect.width/2, rect.y + rect.height));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final Graphics graphics) {
		super.paint(graphics);
		graphics.drawText(
				this.text, this.getLocation().x + 5, this.getLocation().y + 8);
		final Rectangle rect = new Rectangle();
		graphics.getClip(rect);
	}

	/**
	 * Returns the text shown in the scan module.
	 * 
	 * @return the text shown in the scan module
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text shown in the scan module.
	 * 
	 * @param text the text that should be shown
	 */
	public void setText(String text) {
		this.text = text;
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

	/**
	 * Checks whether the scan module is active (selected).
	 * 
	 * @return <code>true</code> if the scan module is active (selected),
	 * 		   <code>false</code> otherwise
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets whether the scan module is active (selected).
	 * If it is active, its background is painted gray (white if not).
	 * 
	 * @param active <code>true</code> to activate (select) the scan module,
	 * 				 <code>false</code> otherwise
	 */
	public void setActive(boolean active) {
		this.active = active;
		if(this.active) {
			setBackgroundColor(ColorConstants.lightGray);
		} else {
			setBackgroundColor(ColorConstants.white);
		}
	}	
}