package de.ptb.epics.eve.editor.graphical.editparts.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * <code>ScanDescriptionFigure</code>
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ScanDescriptionFigure extends Figure  {

	/**
	 * Constructs a <code>ScanDescriptionFigure</code>.
	 */
	public ScanDescriptionFigure() {
		//this.setOpaque(true);
		this.setLayoutManager(new XYLayout());
		this.setBounds(new Rectangle(0,0,1,1)); // old 2,2,300,300
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		this.getParent().add(figure, constraint, index);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(IFigure figure) {
		this.getParent().remove(figure);
	}
}
