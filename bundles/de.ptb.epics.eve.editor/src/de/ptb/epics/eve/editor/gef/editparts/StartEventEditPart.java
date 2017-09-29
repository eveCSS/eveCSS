package de.ptb.epics.eve.editor.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.gef.editpolicies.StartEventGraphicalNodeEditPolicy;
import de.ptb.epics.eve.editor.gef.figures.StartEventFigure;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class StartEventEditPart extends AbstractGraphicalEditPart implements
		NodeEditPart, PropertyChangeListener, IModelUpdateListener {

	private static Logger logger = Logger.getLogger(StartEventEditPart.class
			.getName());
	
	private Label positionCountLabel;
	
	/**
	 * Constructor.
	 * 
	 * @param event the model element to set
	 */
	public StartEventEditPart(StartEvent event) {
		this.setModel(event);
		this.positionCountLabel = new Label();
		this.getModel().getChain().calculatePositionCount();
		Integer positionCount = this.getModel().getChain().getPositionCount();
		if (positionCount == null) {
			this.positionCountLabel.setText("# Positions: N/A");
		} else {
			this.positionCountLabel.setText("# Positions: " + 
					Integer.toString(positionCount));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		this.getModel().addPropertyChangeListener(StartEvent.X_PROP, this);
		this.getModel().addPropertyChangeListener(StartEvent.Y_PROP, this);
		this.getModel()
				.addPropertyChangeListener(StartEvent.CONNECT_PROP, this);
		
		this.getModel().getChain().addPropertyChangeListener(
				Chain.POSITION_COUNT_PROP, this);
		this.getModel().getChain().addModelUpdateListener(this);
		super.activate();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		this.getModel().removePropertyChangeListener(StartEvent.X_PROP, this);
		this.getModel().removePropertyChangeListener(StartEvent.Y_PROP, this);
		this.getModel().removePropertyChangeListener(StartEvent.CONNECT_PROP,
				this);
		
		this.getModel().getChain().removePropertyChangeListener(
				Chain.POSITION_COUNT_PROP, this);
		this.getModel().getChain().removeModelUpdateListener(this);
		super.deactivate();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		return new StartEventFigure(this.getModel().getEvent().getId(), 
				this.getModel().getX(), this.getModel().getY());
	}

	/**
	 * 
	 */
	@Override
	protected void refreshVisuals() {
		if (this.positionCountLabel == null) {
			this.getFigure().setToolTip(null);
		} else {
			this.getFigure().setToolTip(this.positionCountLabel);
		}
		super.refreshVisuals();
	}
	
	/**
	 * Returns the model element.
	 * 
	 * @return the model element
	 */
	public StartEvent getModel() {
		return (StartEvent)super.getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<?> getModelSourceConnections() {
		final List<Object> sourceList = new ArrayList<Object>();
		final StartEvent startEvent = this.getModel();
		
		if(startEvent.getConnector() != null) {
			sourceList.add(startEvent.getConnector());
		}
		return sourceList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new StartEventGraphicalNodeEditPolicy());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
									final ConnectionEditPart connection) {
		logger.debug("get source conn anchors");
		return ((StartEventFigure)this.getFigure()).getSourceAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(final Request request) {
		logger.debug("get source conn anchors req");
		return ((StartEventFigure)this.getFigure()).getSourceAnchor();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(StartEvent.X_PROP)) {
			((StartEventFigure)this.getFigure()).setX((Integer)e.getNewValue());
		} else if (e.getPropertyName().equals(StartEvent.Y_PROP)) {
			((StartEventFigure)this.getFigure()).setY((Integer)e.getNewValue());
		} else if (e.getPropertyName().equals(StartEvent.CONNECT_PROP)) {
			this.refreshSourceConnections();
		} else if (e.getPropertyName().equals(Chain.POSITION_COUNT_PROP)) {
			String positions = (this.getModel().getChain().getPositionCount() == null) 
					? "N/A"
					: Integer.toString(this.getModel().getChain()
							.getPositionCount());
			this.positionCountLabel.setText("# Positions: " + positions);
		}
		this.refreshVisuals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.positionCountLabel.setText("# Positions: N/A");
	}
}