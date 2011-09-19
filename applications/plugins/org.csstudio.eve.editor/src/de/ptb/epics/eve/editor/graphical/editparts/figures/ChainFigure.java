package de.ptb.epics.eve.editor.graphical.editparts.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ChainFigure extends Figure {

	/**
	 * the id of the <code>ChainFigure</code>.
	 */
	public int id;
	
	/**
	 * Constructs a <code>ChainFigure</code>.
	 * 
	 * @param id the id the <code>ChainFigure</code> should get
	 */
	public ChainFigure(final int id) {
		this.setBounds(new Rectangle( 0, 0, 1, 1));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final Graphics graphics) {
		super.paint(graphics);
		graphics.drawText("Chain No: " + this.id, 
						  this.getLocation().x + 5, 
						  this.getLocation().y + 5);
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