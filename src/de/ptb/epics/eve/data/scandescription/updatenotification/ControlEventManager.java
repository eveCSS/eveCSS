/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription.updatenotification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;

public class ControlEventManager implements IControlEventProvider, IModelErrorProvider {

	private List<? extends ControlEvent> controlEventList;
	private List<IModelUpdateListener> modelUpdateListener;
	
	private Chain parentChain;
	private ScanModul parentScanModul;
	private Channel parentChannel;
	
	private ControlEventTypes controlEventType;
	
	private ControlEventManager(  final List<? extends ControlEvent> controlEventList  ) {
		if( controlEventList == null ) {
			throw new IllegalArgumentException( "ControlEventManager:  'controlEventList' must not be null!" ); 
		}
		this.controlEventList = controlEventList;
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();
	}
	
	public ControlEventManager( final Chain parentChain, final List<? extends ControlEvent> controlEventList, final ControlEventTypes controlEventType ) {
		this( controlEventList );
		if( parentChain == null ) {
			throw new IllegalArgumentException( "ControlEventManager:  'parentChain' must not be null!" );
		}
		this.parentChain = parentChain;
		this.controlEventType = controlEventType;
	}
	
	public ControlEventManager( final ScanModul parentScanModul, final List<? extends ControlEvent> controlEventList, final ControlEventTypes controlEventType ) {
		this( controlEventList );
		if( parentScanModul == null ) {
			throw new IllegalArgumentException( "ControlEventManager:  'parentScanModul' must not be null!" );
		}
		this.parentScanModul = parentScanModul;
		this.controlEventType = controlEventType;
	}	

	public ControlEventManager(final Channel parentChannel, final List<? extends ControlEvent> controlEventList,
			final ControlEventTypes controlEventType) {
		this( controlEventList );
		if( parentChannel == null ) {
			throw new IllegalArgumentException( "ControlEventManager:  'parentChannel' must not be null!" );
		}
		this.parentChannel = parentChannel;
		this.controlEventType = controlEventType;
	}

	public List<? extends ControlEvent> getControlEventsList() {
		return new ArrayList<ControlEvent>( this.controlEventList );
	}

	public void addControlEvent( final ControlEvent controlEvent ) {
		if( this.parentChain != null ) {
			if( this.parentChain.getBreakControlEventManager() == this ) {
				this.parentChain.addBreakEvent( controlEvent );
				
			} else if( this.parentChain.getRedoControlEventManager() == this ) {
				this.parentChain.addRedoEvent( controlEvent );
				
			} else if( this.parentChain.getStopControlEventManager() == this ) {
				this.parentChain.addStopEvent( controlEvent );
				
			} else if( this.parentChain.getStartControlEventManager() == this ) {
				this.parentChain.addStartEvent( controlEvent );
				
			} else if( this.parentChain.getPauseControlEventManager() == this ) {
				this.parentChain.addPauseEvent( (PauseEvent)controlEvent );
			}
		} else if( this.parentScanModul != null ) {
			if( this.parentScanModul.getBreakControlEventManager() == this ) {
				this.parentScanModul.addBreakEvent( controlEvent );
			} else if( this.parentScanModul.getRedoControlEventManager() == this ) {
				this.parentScanModul.addRedoEvent( controlEvent );
			} else if( this.parentScanModul.getPauseControlEventManager() == this ) {
				this.parentScanModul.addPauseEvent( (PauseEvent)controlEvent );
			} else if( this.parentScanModul.getTriggerControlEventManager() == this ) {
				this.parentScanModul.addTriggerEvent( controlEvent );
			}
		} else if( this.parentChannel != null ) {
			this.parentChannel.addRedoEvent( controlEvent );
		}
	}
	
	public void removeControlEvent( final ControlEvent controlEvent ) {
		if( this.parentChain != null ) {
			if( this.parentChain.getBreakControlEventManager() == this ) {
				this.parentChain.removeBreakEvent( controlEvent );
			} else if( this.parentChain.getRedoControlEventManager() == this ) {
				this.parentChain.removeRedoEvent( controlEvent );
			} else if( this.parentChain.getStartControlEventManager() == this ) {
				this.parentChain.removeStartEvent( controlEvent );
			} else if( this.parentChain.getStopControlEventManager() == this ) {
				this.parentChain.removeStopEvent( controlEvent );
			} else if( this.parentChain.getPauseControlEventManager() == this ) {
				this.parentChain.removePauseEvent( (PauseEvent)controlEvent );
			}
		} else if( this.parentScanModul != null ) {
			if( this.parentScanModul.getBreakControlEventManager() == this ) {
				this.parentScanModul.removeBreakEvent( controlEvent );
			} else if( this.parentScanModul.getRedoControlEventManager() == this ) {
				this.parentScanModul.removeRedoEvent( controlEvent );
			} else if( this.parentScanModul.getPauseControlEventManager() == this ) {
				this.parentScanModul.removePauseEvent( (PauseEvent)controlEvent );
			} else if( this.parentScanModul.getTriggerControlEventManager() == this ) {
				this.parentScanModul.removeTriggerEvent( controlEvent );
			}
		} else if( this.parentChannel != null ) {
			this.parentChannel.removeRedoEvent( controlEvent );
		}
	}
	
	public boolean addModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.modelUpdateListener.add( modelUpdateListener );
	}

	public boolean removeModelUpdateListener( final IModelUpdateListener modelUpdateListener  ) {
		return this.modelUpdateListener.remove( modelUpdateListener );
	}

	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		Iterator<IModelUpdateListener> it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( modelUpdateEvent );
		}
	}
	
	public Chain getParentChain() {
		return this.parentChain;
	}

	public ScanModul getParentScanModul() {
		return this.parentScanModul;
	}

	public ControlEventTypes getControlEventType() {
		return this.controlEventType;
	}

	public Channel getParentChannel() {
		return this.parentChannel;
	}

	@Override
	public List< IModelError > getModelErrors() {
		final List< IModelError > errorList = new ArrayList< IModelError >();
		final Iterator< ? extends ControlEvent > it = this.controlEventList.iterator();
		while( it.hasNext() ) {
			errorList.addAll( it.next().getModelErrors() );
		}
		return errorList;
	}
	
}
