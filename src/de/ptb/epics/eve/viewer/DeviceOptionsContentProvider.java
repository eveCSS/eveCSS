package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

public class DeviceOptionsContentProvider implements IStructuredContentProvider, IModelUpdateListener {

	private Viewer currentViewer;
	private AbstractDevice device;
	private List< OptionConnector > optionConnectors;
	
	
	public Object[] getElements( final Object inputElement ) {
		return this.optionConnectors.toArray();
	}

	public void dispose() {

	}

	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput ) {
		this.device = (AbstractDevice)newInput;
		this.currentViewer = viewer;
		
		if( this.optionConnectors != null ) {
			final Iterator< OptionConnector > it = this.optionConnectors.iterator();
			while( it.hasNext() ) {
				it.next().detach();
			}
		}
		
		this.optionConnectors = new ArrayList< OptionConnector >();
		
		final Iterator< Option > it = this.device.optionIterator();
		while( it.hasNext() ) {
			final OptionConnector optionConnector = new OptionConnector( it.next() );
			optionConnector.addModelUpdateListener( this );
			this.optionConnectors.add( optionConnector );
		}
	}

	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
	
		this.currentViewer.getControl().getDisplay().syncExec( new Runnable() {

			public void run() {
				currentViewer.refresh();
				
			}
			
		});
		
	}

}
