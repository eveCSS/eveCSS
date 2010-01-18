package de.ptb.epics.eve.viewer;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.epics.css.dal.Timestamp;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

public class MotorAxisComposite extends Composite implements IProcessVariableValueListener {

	private MotorAxis motorAxis;
	
	private Label motorAxisNameLabel;
	private Label valueLabel;
	private Text targetValueText;
	private Combo targetValueCombo;
	
	private Label unitLabel;
	private Button runButton;
	private Label currentStateLabel;
	private Text offsetText;
	private Button setOffsetButton;
	
	private Label emptyLabel1;
	private Button stepLeftButton;
	private Text tweakText;
	private Button stepRightButton;
	private Label emptyLabel2;
	private Label emptyLabel3;
	private Label emptyLabel4;
	private Label emptyLabel5;
	
	public MotorAxisComposite( final Composite parent, final int style, final MotorAxis motorAxis ) {
		super( parent, style );
		this.motorAxis = motorAxis;
		
		initialize();
	}
	
	private void initialize() {
		
		//this.setBackground( new Color( this.getBackground().getDevice(), 255, 0, 0 ) );
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
		gridLayout.marginHeight = 0;
		this.setLayout( gridLayout );
		
		
		
		this.motorAxisNameLabel = new Label( this, SWT.NONE );
		GridData gridData = new GridData();
		gridData.widthHint = 200;
		this.motorAxisNameLabel.setLayoutData( gridData );
		this.motorAxisNameLabel.setText( this.motorAxis.getFullIdentifyer() );
		
		this.valueLabel = new Label( this, SWT.NONE );
		gridData = new GridData();
		gridData.widthHint = 80;
		this.valueLabel.setLayoutData( gridData );
		this.valueLabel.setText( "?" );
		
		if( this.motorAxis.getGoto() != null && this.motorAxis.getGoto().isDiscrete() ) {
			this.targetValueCombo = new Combo( this, SWT.NONE );
			gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			this.targetValueCombo.setLayoutData( gridData );
			this.targetValueCombo.setItems( this.motorAxis.getGoto().getDiscreteValues().toArray( new String[0] ) );
		} else {
			this.targetValueText = new Text( this, SWT.BORDER );
			gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			this.targetValueText.setLayoutData( gridData );
		}
		this.unitLabel = new Label( this, SWT.NONE );
		gridData = new GridData();
		gridData.widthHint = 30;
		this.unitLabel.setLayoutData( gridData );
		if( this.motorAxis.getUnit() != null ) {
			if( this.motorAxis.getUnit().getValue() != null ) {
				this.unitLabel.setText( this.motorAxis.getUnit().getValue() );
			} else {
				String prefix = null;
				if( this.motorAxis.getUnit().getAccess().getTransport() == TransportTypes.CA ) {
					prefix = "dal-epics://";
				} else if( this.motorAxis.getUnit().getAccess().getTransport() == TransportTypes.LOCAL ) {
					prefix = "local://";
				}
				IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + this.motorAxis.getUnit().getAccess().getVariableID() );
				try {
					final String unit = service.readValueSynchronously( pv, Helper.dataTypesToValueType( this.motorAxis.getUnit().getAccess().getType() ) ).toString();
					this.unitLabel.setText( unit );
				} catch( final ConnectionException e ) {
					this.unitLabel.setText( "?" );
				}
			}
		}
		
		this.runButton = new Button( this, SWT.PUSH );
		this.runButton.setText( "Run" );
		this.runButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
				ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 
				String prefix = null;
				if( motorAxis.getGoto().getAccess().getTransport() == TransportTypes.CA ) {
					prefix = "dal-epics://";
				} else if( motorAxis.getGoto().getAccess().getTransport() == TransportTypes.LOCAL ) {
					prefix = "local://";
				}
				IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + motorAxis.getGoto().getAccess().getVariableID() );
				try {
					Object o = null;
					switch( motorAxis.getGoto().getType() ) {
					
						case INT:
							o = Integer.parseInt( targetValueText != null?targetValueText.getText():targetValueCombo.getText() );
							break;
							
						case DOUBLE: 
							o = Double.parseDouble( targetValueText != null?targetValueText.getText():targetValueCombo.getText() );
							break;
						
						default:
							o = targetValueText != null?targetValueText.getText():targetValueCombo.getText();
					
					}
					service.writeValueSynchronously( pv, o, Helper.dataTypesToValueType( motorAxis.getGoto().getType() ) );

				} catch( final ConnectionException e1 ) {
					
					e1.printStackTrace();
				}
				
			}
			
		});
		
		this.currentStateLabel = new Label( this, SWT.NONE );
		gridData = new GridData();
		gridData.widthHint = 150;
		this.currentStateLabel.setLayoutData( gridData );
		this.currentStateLabel.setText( "disconnected" );
		
		this.offsetText = new Text( this, SWT.BORDER );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.offsetText.setLayoutData( gridData );
		if( this.motorAxis.getOffset() != null ) {
			
		} else {
			this.offsetText.setEnabled( false );
		}
		
		this.setOffsetButton = new Button( this, SWT.PUSH );
		this.setOffsetButton.setText( "Set" );
		if( this.motorAxis.getOffset() != null ) {
			this.setOffsetButton.addSelectionListener( new SelectionListener() {

				public void widgetDefaultSelected( final SelectionEvent e ) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected( final SelectionEvent e ) {
					IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
					ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 
					String prefix = null;
					if( motorAxis.getOffset().getAccess().getTransport() == TransportTypes.CA ) {
						prefix = "dal-epics://";
					} else if( motorAxis.getOffset().getAccess().getTransport() == TransportTypes.LOCAL ) {
						prefix = "local://";
					}
					IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + motorAxis.getOffset().getAccess().getVariableID() );
					
					try {
						Object o = null;
						switch( motorAxis.getOffset().getType() ) {
						
							case INT:
								o = Integer.parseInt( offsetText.getText() );
								break;
								
							case DOUBLE: 
								o = Double.parseDouble( offsetText.getText() );
								break;
							
							default:
								o = offsetText.getText();
						
						}
						service.writeValueSynchronously( pv, o, Helper.dataTypesToValueType( motorAxis.getOffset().getType() ) );
					} catch( final ConnectionException e1 ) {
						
						e1.printStackTrace();
					}

					
				}
				
			});
		} else {
			this.setOffsetButton.setEnabled( false );
		}
		
		this.emptyLabel1 = new Label( this, SWT.NONE );
		
		this.stepLeftButton = new Button( this, SWT.PUSH );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		this.stepLeftButton.setLayoutData( gridData );
		this.stepLeftButton.setText( "<" );
	
		if( this.motorAxis.getTweakReverse() != null && this.motorAxis.getTweakValue() != null && this.motorAxis.getTweakForward() != null ) {
			this.stepLeftButton.addSelectionListener( new SelectionListener() {

				public void widgetDefaultSelected( final SelectionEvent e ) {
					
					
				}

				public void widgetSelected( final SelectionEvent e ) {
					IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
					ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 
					String prefix = null;
					if( motorAxis.getTweakValue().getAccess().getTransport() == TransportTypes.CA ) {
						prefix = "dal-epics://";
					} else if( motorAxis.getTweakValue().getAccess().getTransport() == TransportTypes.LOCAL ) {
						prefix = "local://";
					}
					IProcessVariableAddress tweakValuePv = pvFactory.createProcessVariableAdress( prefix + motorAxis.getTweakValue().getAccess().getVariableID() );
					try {
						Object o = null;
						switch( motorAxis.getTweakValue().getType() ) {
					
							case INT:
								o = Integer.parseInt( tweakText != null?tweakText.getText():tweakText.getText() );
								break;
							
							case DOUBLE: 
								o = Double.parseDouble( tweakText != null?tweakText.getText():tweakText.getText() );
								break;
						
							default:
								o = tweakText != null?tweakText.getText():tweakText.getText();
					
						}
						service.writeValueSynchronously( tweakValuePv, o, Helper.dataTypesToValueType( motorAxis.getTweakValue().getType() ) );
					} catch( final ConnectionException e1 ) {
					
						e1.printStackTrace();
					}
				
					if( motorAxis.getTweakValue().getAccess().getTransport() == TransportTypes.CA ) {
						prefix = "dal-epics://";
					} else if( motorAxis.getTweakValue().getAccess().getTransport() == TransportTypes.LOCAL ) {
						prefix = "local://";
					}
				
					IProcessVariableAddress tweakLeftPv = pvFactory.createProcessVariableAdress( prefix + motorAxis.getTweakReverse().getAccess().getVariableID() );
					try {
						Object o = null;
						switch( motorAxis.getTweakReverse().getType() ) {
					
							case INT:
								o = Integer.parseInt( motorAxis.getTweakReverse().getValue().getValues() );
								break;
							
							case DOUBLE: 
								o = Double.parseDouble( motorAxis.getTweakReverse().getValue().getValues() );
								break;
						
							default:
								o = motorAxis.getTweakReverse().getValue().getValues();
					
						}
						service.writeValueSynchronously( tweakLeftPv, o, Helper.dataTypesToValueType( motorAxis.getTweakReverse().getType() ) );
					} catch( final ConnectionException e1 ) {
					
						e1.printStackTrace();
					}
				
				
				}
			
			});
		} else {
			this.stepLeftButton.setEnabled( false );
		}
		
		
		this.tweakText = new Text( this, SWT.BORDER );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.tweakText.setLayoutData( gridData );
		if( this.motorAxis.getTweakReverse() != null && this.motorAxis.getTweakValue() != null && this.motorAxis.getTweakForward() != null ) {
			
		} else {
			this.tweakText.setEnabled( false );
		}
		
		this.stepRightButton = new Button( this, SWT.PUSH );
		this.stepRightButton.setText( ">" );
		if( this.motorAxis.getTweakReverse() != null && this.motorAxis.getTweakValue() != null && this.motorAxis.getTweakForward() != null ) {
			
			this.stepRightButton.addSelectionListener( new SelectionListener() {

				public void widgetDefaultSelected( final SelectionEvent e ) {
				
					
				
				}

				public void widgetSelected( final SelectionEvent e ) {
					IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
					ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 
					String prefix = null;
					if( motorAxis.getTweakValue().getAccess().getTransport() == TransportTypes.CA ) {
						prefix = "dal-epics://";
					} else if( motorAxis.getTweakValue().getAccess().getTransport() == TransportTypes.LOCAL ) {
						prefix = "local://";
					}
					IProcessVariableAddress tweakValuePv = pvFactory.createProcessVariableAdress( prefix + motorAxis.getTweakValue().getAccess().getVariableID() );
					try {
						Object o = null;
						switch( motorAxis.getTweakValue().getType() ) {
					
							case INT:
								o = Integer.parseInt( tweakText != null?tweakText.getText():tweakText.getText() );
								break;
							
							case DOUBLE: 
								o = Double.parseDouble( tweakText != null?tweakText.getText():tweakText.getText() );
								break;
						
							default:
								o = tweakText != null?tweakText.getText():tweakText.getText();
					
						}
						service.writeValueSynchronously( tweakValuePv, o, Helper.dataTypesToValueType( motorAxis.getTweakValue().getType() ) );
					} catch( final ConnectionException e1 ) {
					
						e1.printStackTrace();
					}
				
					if( motorAxis.getTweakValue().getAccess().getTransport() == TransportTypes.CA ) {
						prefix = "dal-epics://";
					} else if( motorAxis.getTweakValue().getAccess().getTransport() == TransportTypes.LOCAL ) {
						prefix = "local://";
					}
				
					IProcessVariableAddress tweakRightPv = pvFactory.createProcessVariableAdress( prefix + motorAxis.getTweakForward().getAccess().getVariableID() );
					try {
						Object o = null;
						switch( motorAxis.getTweakForward().getType() ) {
					
							case INT:
								o = Integer.parseInt( motorAxis.getTweakForward().getValue().getValues() );
								break;
							
							case DOUBLE: 
								o = Double.parseDouble( motorAxis.getTweakForward().getValue().getValues() );
								break;
						
							default:
								o = motorAxis.getTweakForward().getValue().getValues();
					
						}
						service.writeValueSynchronously( tweakRightPv, o, Helper.dataTypesToValueType( motorAxis.getTweakForward().getType() ) );
					} catch( final ConnectionException e1 ) {
					
						e1.printStackTrace();
					}
				
				}
			
			});
		} else {
			this.stepRightButton.setEnabled( false );
		}

		
		
		this.emptyLabel2 = new Label( this, SWT.NONE );
		this.emptyLabel3 = new Label( this, SWT.NONE );
		this.emptyLabel4 = new Label( this, SWT.NONE );
		this.emptyLabel5 = new Label( this, SWT.NONE );
		
		String prefix = null;
		if( this.motorAxis.getPosition().getAccess().getTransport() == TransportTypes.CA ) {
			prefix = "dal-epics://";
		} else if( this.motorAxis.getPosition().getAccess().getTransport() == TransportTypes.LOCAL ) {
			prefix = "local://";
		}
		
		IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + this.motorAxis.getPosition().getAccess().getVariableID() );
	
		/*try {
			this.valueLabel.setText( service.readValueSynchronously( pv, Helper.dataTypesToValueType( this.motorAxis.getPosition().getType() ) ).toString() );
		} catch( final ConnectionException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		service.register( this, pv,  Helper.dataTypesToValueType( this.motorAxis.getPosition().getType() ) );
		//service.readValueAsynchronously( pv, Helper.dataTypesToValueType( this.motorAxis.getPosition().getType() ), this );
	
	}

	public void dispose() {
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		service.unregister( this );
		super.dispose();
	}
	
	public void connectionStateChanged( final ConnectionState connectionState ) {
		this.currentStateLabel.getDisplay().syncExec( new Runnable() {

			public void run() {
				
				String newText = "";
				switch( connectionState ) {
				
					case CONNECTED:
						newText = "connected";
						break;
						
					case CONNECTION_FAILED:
						newText = "connection failed";
						break;
						
					case CONNECTION_LOST:
						newText = "connection lost";
						break;
						
					case INITIAL:
						newText = "initial";
						break;
						
					case UNKNOWN:
						newText = "unknown";
						
				}
				
				currentStateLabel.setText( newText );
				
			}
			
		});
		
	}

	public void errorOccured( final String error ) {
		System.err.println( "!!!!!!!!!!!!!!!!" + error );
		
	}

	public void valueChanged( final Object value, final Timestamp timestamp ) {
		
		this.valueLabel.getDisplay().syncExec( new Runnable() {

			public void run() {
				valueLabel.setText( value.toString() );
			}
			
		});
		
	}
}
