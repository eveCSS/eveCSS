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
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class ChainFigure extends Figure {

	public int id;
	
	public ChainFigure( final int id ) {
		/*this.setOpaque( true );
		this.setLayoutManager( new XYLayout() );
		this.setSize( 300, 150 );
		this.setLocation( new Point( 20, 20 ) );
		this.setBorder( new LineBorder( ColorConstants.black, 1) );
		this.id = id;*/
		this.setBounds( new Rectangle( 0, 0, 1, 1 ) );
	}
	
	public void paint( final Graphics graphics ) {
		super.paint( graphics );
		graphics.drawText( "Chain No: " + this.id, this.getLocation().x + 5, this.getLocation().y + 5 );
		
	}
	
	public void add(IFigure figure, Object constraint, int index ) {
		this.getParent().add(figure, constraint, index);
		
	}
	
	public void remove( IFigure figure ) {
		this.getParent().remove( figure );
	}
}
