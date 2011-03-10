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
import org.eclipse.swt.graphics.Color;

/**
 * <code>EventFigure</code>
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
		
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClicked(MouseEvent me) {				
			}

			@Override
			public void mousePressed(MouseEvent me) {
				xOffset = me.x - getLocation().x ;
				yOffset = me.y - getLocation().y;
				me.consume();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				setLocation(new Point(me.x - xOffset, me.y - yOffset));
				xOffset = 0;
				yOffset = 0;
			}
			
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent me) {
				setLocation(new Point(me.x - xOffset, me.y - yOffset));
				Rectangle newLocation = getBounds();
				sourceAnchor.setLocation(
						new Point(newLocation.x + 8, newLocation.y + 10));
			}

			@Override
			public void mouseEntered(MouseEvent me) {
			}

			@Override
			public void mouseExited(MouseEvent me) {
			}

			@Override
			public void mouseHover(MouseEvent me) {	
			}

			@Override
			public void mouseMoved(MouseEvent me) {
			}
			
		});
		
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
		Color color = graphics.getBackgroundColor();
		graphics.setBackgroundColor(ColorConstants.black);
		graphics.fillOval(
				this.getLocation().x + 3, this.getLocation().y + 5, 10, 10); 
		graphics.setBackgroundColor(color);
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
}