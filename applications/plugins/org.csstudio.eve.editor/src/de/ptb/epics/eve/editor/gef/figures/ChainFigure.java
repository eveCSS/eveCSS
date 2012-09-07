package de.ptb.epics.eve.editor.gef.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ChainFigure extends FreeformLayer {
	
	/**
	 * Constructor.
	 */
	public ChainFigure() {
		super();
		this.setBorder(new MarginBorder(3));
		this.setLayoutManager(new FreeformLayout());
		
		//this.setBounds(new Rectangle(0, 0, 1, 1));
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