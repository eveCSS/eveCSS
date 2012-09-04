package de.ptb.epics.eve.editor.gef.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ChainFigure extends Figure {
	
	/**
	 * Constructor.
	 */
	public ChainFigure() {
		this.setBounds(new Rectangle(0, 0, 1, 1));
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