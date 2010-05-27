package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.internal.data.EnumeratedMetaData;
import org.csstudio.platform.internal.data.EnumeratedValue;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

public class OptionConnector implements IModelUpdateProvider, PVListener {

	private final Option option;
	private String value;
	private PV pv;
	private boolean isConnected = false;
	
	private final List< IModelUpdateListener > modelUpdateListener;
	private boolean readOnly = false;
	private String[] discreteValues = null;
	private boolean isDiscrete = false;
	
	public OptionConnector( final Option option ) {
		if( option == null ) {
			throw new IllegalArgumentException( "Option Connector: 'option' must not be null!" );
		}
		this.modelUpdateListener = new ArrayList< IModelUpdateListener >();
		this.option = option;

		if ((option.getValue() == null) || (option.getValue() == null)) return;

		isDiscrete = option.isDiscrete();
		readOnly = option.getValue().isReadOnly();
		if (isDiscrete) discreteValues = option.getValue().getDiscreteValues().toArray(new String[0]);
		if( option.getValue().getAccess().getTransport() == TransportTypes.CA ) {
			option.getValue().isReadOnly();
			String pvname = "ca://" + option.getValue().getAccess().getVariableID();
			try {
				pv = PVFactory.createPV(pvname);
				pv.addListener(this);
				pv.start();
				this.value = pv.getStateInfo();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}		
	}
	
	public Option getOption() {
		return this.option;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void detach() {
        if (pv != null)
        {
        	Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.VIEWER, MessageTypes.INFO, "Disposing text for " + pv.getName() ) );
            pv.removeListener(this);
            pv.stop();
            pv = null;
        }
	}
	
	public void setValue( final String value ) {

		if ((pv == null) || !pv.isConnected() || readOnly)  return;
 
        try
        {	
        	String newValue = value;
        	if (isDiscrete){
        		int number = Integer.parseInt(value);
        		if ((number >= 0) && (number < discreteValues.length)){
        			newValue = discreteValues[number];
        		}
        	}
         	System.out.println("Now set "+pv.getName()+" to "+newValue);
        	pv.setValue(newValue);
        }
        catch (Throwable ex)
        {
        	System.err.println("Unable to set "+pv.getName()+" to "+value );
            ex.printStackTrace();
        }
	}
	
	public boolean addModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.modelUpdateListener.add( modelUpdateListener );
	}

	public boolean removeModelUpdateListener( final IModelUpdateListener modelUpdateListener ) { 
		return this.modelUpdateListener.remove( modelUpdateListener );
	}
	
	public void errorOccured( final String error ) {
		
	}

	public boolean isConnected() {
		return this.isConnected;
	}

	@Override
	public void pvDisconnected(PV pv) {
		// TODO Auto-generated method stub
		isConnected = false;
		value = pv.getStateInfo();
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}		
	}

	@Override
	public void pvValueUpdate(PV pv) {
		// TODO Auto-generated method stub
		if(pv.isConnected()) isConnected = true;
		if(!pv.isWriteAllowed()) readOnly = true;
		this.value = ValueUtil.getString(pv.getValue());
        final IMetaData meta = pv.getValue().getMetaData();
        if (meta instanceof IEnumeratedMetaData) {
        	this.discreteValues = ((IEnumeratedMetaData)meta).getStates();
        	isDiscrete  = true;
        }
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}		
	}

	public boolean isDiscrete(){
		return isDiscrete;
	}
	
	public boolean isReadOnly(){
		return readOnly;
	}
	
	public String[] getDiscreteValues(){
		return discreteValues;
	}
}
