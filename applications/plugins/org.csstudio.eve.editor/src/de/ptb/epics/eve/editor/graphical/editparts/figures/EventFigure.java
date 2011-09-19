package de.ptb.epics.eve.editor.graphical.editparts.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * <code>EventFigure</code> is the graphical representation of an Event in the 
 * editor.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class EventFigure extends Figure {

	private String id;
	
	private int xOffset = 0;
	private int yOffset = 0;
	
	private XYAnchor sourceAnchor;
	
	/**
	 * Constructs an <code>EventFigure</code>.
	 * 
	 * @param id the id the <code>EventFigure</code> should get
	 */
	public EventFigure(final String id) {
		this.setSize(20, 40);
		this.id = id;
		
		// add a listener for mouse pressed and released events
		this.addMouseListener(new EventFigureMouseListener());
		
		// add a listener for mouse moved event
		this.addMouseMotionListener(new EventFigureMouseMotionListener());
		
		this.setLocation(new Point(10, 10));
		Rectangle rect = this.getBounds();
		this.sourceAnchor = new XYAnchor(new Point(rect.x + 8, rect.y + 10));	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Graphics graphics) {
		
		super.paint(graphics);
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
				this.id, this.getLocation().x + 2, this.getLocation().y + 16);
	}
	
	/**
	 * Returns the source of the event.
	 * 
	 * @return the <code>XYAnchor</code> which is the source of the event
	 */
	public XYAnchor getSourceAnchor() {
		return this.sourceAnchor;
	}	
	
	// *************************** Listener **********************************
	
	/**
	 * <code>MouseListener</code> of <code>EventFigure</code>.
	 */
	class EventFigureMouseListener implements MouseListener
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDoubleClicked(MouseEvent me) {			
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mousePressed(MouseEvent me) {
			// calculating the offset to the point of origin (0,0)
			xOffset = me.x - getLocation().x ;
			yOffset = me.y - getLocation().y;
			me.consume();			
		}
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseReleased(MouseEvent me) {
			// set the location relative to the point where the mouse was pressed
			setLocation(new Point(me.x - xOffset, me.y - yOffset));
			// reset the offset TODO: necessary ?
			xOffset = 0;
			yOffset = 0;
		}
	}
	
	/**
	 * <code>MouseMotionListener</code> of <code>EventFigure</code>
	 */
	class EventFigureMouseMotionListener implements MouseMotionListener
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDragged(MouseEvent me) {
			// (re-)set the location while dragging the event
			setLocation(new Point(me.x - xOffset, me.y - yOffset));
			Rectangle newLocation = getBounds();
			// (re-)set the starting point of the outgoing arrow
			sourceAnchor.setLocation(
					new Point(newLocation.x + 8, newLocation.y + 10));	
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseEntered(MouseEvent me) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseExited(MouseEvent me) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseHover(MouseEvent me) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseMoved(MouseEvent me) {
		}
	}
}