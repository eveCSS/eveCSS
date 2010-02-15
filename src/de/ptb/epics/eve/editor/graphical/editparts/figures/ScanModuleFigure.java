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
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class ScanModuleFigure extends Figure {
	
	private int xOffset = 0;
	private int yOffset = 0;
	
	private XYAnchor targetAnchor;
	private XYAnchor appendedAnchor;
	private XYAnchor nestedAnchor;
	
	private boolean active;
	
	private String text;
	
	public ScanModuleFigure( final String text, int x, int y ) {
		this.active = false;
		this.setBackgroundColor( ColorConstants.white );
		this.setBorder( new LineBorder( ColorConstants.black, 1) );
		this.setOpaque( true );
		this.setSize( 70, 30 );
		this.setLocation( new Point(  x, y  ) );
		
		this.text = text;
		this.setToolTip( new Label( "Double click to edit me." ) );
		
		this.addMouseListener( new MouseListener() {

			public void mouseDoubleClicked( final MouseEvent me ) {
				
			}

			public void mousePressed( final MouseEvent me ) {
				xOffset = me.x - getLocation().x ;
				yOffset = me.y - getLocation().y;
				me.consume();
			}

			public void mouseReleased( final MouseEvent me ) {
				setLocation( new Point(  me.x - xOffset, me.y - yOffset) );
				Rectangle newLocation = getBounds();
				targetAnchor.setLocation( new Point( newLocation.x, newLocation.y + (newLocation.height/2) ) );
				appendedAnchor.setLocation( new Point( newLocation.x + newLocation.width, newLocation.y + (newLocation.height/2) ) );
				nestedAnchor.setLocation( new Point( newLocation.x + (newLocation.width/2), newLocation.y + newLocation.height ) );
				xOffset = 0;
				yOffset = 0;
				
			}
			
		});
		
		this.addMouseMotionListener( new MouseMotionListener() {

			public void mouseDragged( final MouseEvent me ) {
				setLocation( new Point(  me.x - xOffset, me.y - yOffset) );
				Rectangle newLocation = getBounds();
				targetAnchor.setLocation( new Point( newLocation.x, newLocation.y + (newLocation.height/2) ) );
				appendedAnchor.setLocation( new Point( newLocation.x + newLocation.width, newLocation.y + (newLocation.height/2) ) );
				nestedAnchor.setLocation( new Point( newLocation.x + (newLocation.width/2), newLocation.y + newLocation.height ) );
				
			}

			public void mouseEntered( final MouseEvent me ) {
				
				
				
			}

			public void mouseExited( final MouseEvent me ) {
				
				
			}

			public void mouseHover(MouseEvent me) {
				// TODO Auto-generated method stub
				
			}

			public void mouseMoved(MouseEvent me) {
				// TODO Auto-generated method stub
				
			}
			
		});
		Rectangle rect = this.getBounds();
		this.targetAnchor = new XYAnchor( new Point( rect.x, rect.y + (rect.height/2) ) );
		
		this.appendedAnchor = new XYAnchor( new Point( rect.x + rect.width, rect.y + (rect.height/2) ) );
		this.nestedAnchor = new XYAnchor( new Point( rect.x + (rect.width /2), rect.y + rect.height ) );
	}
	
	public void paint( final Graphics graphics ) {
		super.paint( graphics );
		graphics.drawText( this.text, this.getLocation().x + 5, this.getLocation().y + 8 );
		final Rectangle rect = new Rectangle();
		graphics.getClip( rect );
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public XYAnchor getAppendedAnchor() {
		return appendedAnchor;
	}

	public XYAnchor getNestedAnchor() {
		return nestedAnchor;
	}

	public XYAnchor getTargetAnchor() {
		return targetAnchor;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		if( this.active ) {
			setBackgroundColor( ColorConstants.lightGray );
		} else {
			setBackgroundColor( ColorConstants.white );
		}
	}
	
	
	
}
