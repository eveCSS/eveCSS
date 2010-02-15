/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.graphical.editparts.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

public class EventFigure extends Figure {

	private String id;
	
	private int xOffset = 0;
	private int yOffset = 0;
	
	private XYAnchor sourceAnchor;
	
	public EventFigure( final String id ) {
		this.setSize( 20, 40 );
		this.id = id;
		
		this.addMouseListener( new MouseListener() {

			public void mouseDoubleClicked(MouseEvent me) {
				// TODO Auto-generated method stub
				
			}

			public void mousePressed(MouseEvent me) {
				xOffset = me.x - getLocation().x ;
				yOffset = me.y - getLocation().y;
				me.consume();
			}

			public void mouseReleased(MouseEvent me) {
				setLocation( new Point(  me.x - xOffset, me.y - yOffset) );
				xOffset = 0;
				yOffset = 0;
			}
			
		});
		
		this.addMouseMotionListener( new MouseMotionListener() {

			public void mouseDragged(MouseEvent me) {
				setLocation( new Point(  me.x - xOffset, me.y - yOffset) );
				Rectangle newLocation = getBounds();
				sourceAnchor.setLocation( new Point( newLocation.x + 8, newLocation.y + 10 ) );
			}

			public void mouseEntered(MouseEvent me) {
				
				
			}

			public void mouseExited(MouseEvent me) {
				
				
			}

			public void mouseHover(MouseEvent me) {
				// TODO Auto-generated method stub
				
			}

			public void mouseMoved(MouseEvent me) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		this.setLocation( new Point( 10, 10 ) );
		Rectangle rect = this.getBounds();
		this.sourceAnchor = new XYAnchor( new Point( rect.x + 8, rect.y + 10 ) );
		
		
	}
	
	
	public void paint( Graphics graphics ) {
		
		super.paint( graphics );
		Color color = graphics.getBackgroundColor();
		graphics.setBackgroundColor( ColorConstants.black );
		graphics.fillOval( this.getLocation().x + 3, this.getLocation().y + 5, 10, 10 ); 
		graphics.setBackgroundColor( color );
		graphics.drawText( this.id, this.getLocation().x + 2, this.getLocation().y + 16 );
		
	}
	
	public XYAnchor getSourceAnchor() {
		return this.sourceAnchor;
	}
	
}
