package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.epics.css.dal.Timestamp;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

public class OptionConnector_dal implements IModelUpdateProvider, IProcessVariableValueListener {

	private final Option option;
	private String value;
	private IProcessVariableAddress pv;
	private ConnectionState connectionState;
	
	private final List< IModelUpdateListener > modelUpdateListener;
	
	public OptionConnector_dal( final Option option ) {
		if( option == null ) {
			throw new IllegalArgumentException( "The parameter 'option' must not be null!" );
		}
		this.modelUpdateListener = new ArrayList< IModelUpdateListener >();
		this.option = option;
		
		this.value = "?";
		
		final OptionConnector_dal own = this;
		
		final Runnable runnable = new Runnable() {

			public void run() {
				final IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
				final ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 

				String prefix = null;
				if( option.getValue().getAccess().getTransport() == TransportTypes.CA ) {
					prefix = "dal-epics://";
				} else if( option.getValue().getAccess().getTransport() == TransportTypes.LOCAL ) {
					prefix = "local://";
				}
				
				pv = pvFactory.createProcessVariableAdress( prefix + option.getValue().getAccess().getVariableID() );
				
				/*try {
					value = service.readValueSynchronously( pv, Helper.dataTypesToValueType( option.getValue().getType() ) ).toString();
				} catch (ConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				service.register( own, pv,  Helper.dataTypesToValueType( option.getValue().getType() ) );
				//service.readValueAsynchronously( pv, Helper.dataTypesToValueType( option.getValue().getType() ), own );
				
			}
			
		};
		
		final Thread thread = new Thread( runnable );
		thread.run();
	}
	
	public Option getOption() {
		return this.option;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void detach() {
		final IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		//service.unregister( this );
	}
	
	public void setValue( final String value ) {
		final IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		try {
			Object o = null;
			switch( this.option.getValue().getType() ) {
			
				case INT:
					o = Integer.parseInt( value );
					break;
					
				case DOUBLE: 
					o = Double.parseDouble( value );
					break;
				
				default:
					o = value;
			
			}
			service.writeValueSynchronously( pv, o, Helper.dataTypesToValueType( this.option.getValue().getType() ) );
		} catch( final ConnectionException e1 ) {
			
			e1.printStackTrace();
		}
	}
	
	public ConnectionState getConnectionState() {
		return this.connectionState;
	}
	
	public boolean addModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.modelUpdateListener.add( modelUpdateListener );
	}

	public boolean removeModelUpdateListener( final IModelUpdateListener modelUpdateListener ) { 
		return this.modelUpdateListener.remove( modelUpdateListener );
	}

	public void connectionStateChanged( final ConnectionState connectionState ) {
		this.connectionState = connectionState;
	}
	
	public void errorOccured( final String error ) {
		
	}

	public void valueChanged( final Object value, final Timestamp timestamp ) {
		this.value = value.toString();
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	

}
