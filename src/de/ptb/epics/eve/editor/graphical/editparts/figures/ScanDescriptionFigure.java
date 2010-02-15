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
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

public class ScanDescriptionFigure extends Figure  {

	public ScanDescriptionFigure() {
		//this.setOpaque( true );
		this.setLayoutManager( new XYLayout() );
		//this.setBounds( new Rectangle( 2, 2, 300, 300 ) );
		this.setBounds( new Rectangle( 0, 0, 1, 1 ) );
	}
	public void add(IFigure figure, Object constraint, int index ) {
		this.getParent().add(figure, constraint, index);
		
	}
	
	public void remove( IFigure figure ) {
		this.getParent().remove( figure );
	}
}
