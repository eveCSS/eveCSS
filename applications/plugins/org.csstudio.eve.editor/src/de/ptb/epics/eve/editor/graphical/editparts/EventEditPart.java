package de.ptb.epics.eve.editor.graphical.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.editor.graphical.editparts.figures.EventFigure;

/**
 * <code>EventEditPart</code>
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class EventEditPart extends AbstractGraphicalEditPart implements NodeEditPart {

	/**
	 * Constructs an <code>EventEditPart</code>
	 * 
	 * @param startEvent the start event
	 */
	public EventEditPart(final StartEvent startEvent) {
		this.setModel(startEvent);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		return new EventFigure(((StartEvent)this.getModel()).getEvent().getID());
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
	protected List<?> getModelSourceConnections() {
		final List<Object> sourceList = new ArrayList<Object>();
		final StartEvent startEvent = (StartEvent)this.getModel();
		
		if(startEvent.getConnector() != null) {
			sourceList.add(startEvent.getConnector());
		}
		return sourceList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
									final ConnectionEditPart connection) {
		return ((EventFigure)this.figure).getSourceAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(final Request request) {
		return ((EventFigure)this.figure).getSourceAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
										final ConnectionEditPart connection) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(final Request request) {
		return null;
	}
}