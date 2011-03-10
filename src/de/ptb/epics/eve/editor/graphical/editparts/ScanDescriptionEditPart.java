package de.ptb.epics.eve.editor.graphical.editparts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.graphical.editparts.figures.ScanDescriptionFigure;

/**
 * <code>ScanDescriptionEditPart</code>
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ScanDescriptionEditPart extends AbstractGraphicalEditPart {

	/**
	 * Constructs a <code>ScanDescriptionEditPart</code>
	 * 
	 * @param scanDescription the scan description
	 */
	public ScanDescriptionEditPart(final ScanDescription scanDescription) {
		this.setModel(scanDescription);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		return new ScanDescriptionFigure();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Object> getModelChildren() {
		final ScanDescription scanDescription = (ScanDescription)this.getModel();
		final List<Object> children = new ArrayList<Object>();
		children.addAll(scanDescription.getChains());
		
		final List<Object> startEvents = new ArrayList<Object>();
		final Iterator<Chain> it = scanDescription.getChains().iterator();
		
		while(it.hasNext()) {
			startEvents.add(it.next().getStartEvent());
		}
		
		children.addAll(startEvents);
		return children;
	}
}