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
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.epics.css.dal.Timestamp;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;


/**
 * This composite connects to a given motor axis and provides interactive widgets for it.
 * 
 * The shows the following properties:
 * - Name
 * - Current Value
 * - Unit
 * 
 * Over and above that is has controls to:
 * - Set a new position
 * - Set an offset
 * - Tweak for- and backward.
 * 
 * 
 * @author Stephan Rehfeld
 *
 */
public class MotorAxisComposite extends Composite implements IProcessVariableValueListener, IMeasurementDataListener, SelectionListener {

	private MotorAxis motorAxis;
	
	private Button closeButton;
	private Button expandButton;
	
	private Label motorAxisNameLabel;
	private Label valueLabel;
	private Label engineValueLabel;
	private Text targetValueText;
	private Combo targetValueCombo;
	
	private Label unitLabel;
	private Button runButton;
	private Text offsetText;
	private Button setOffsetButton;
	
	private Label emptyLabel1;
	private Label emptyLabel2;
	private Label emptyLabel3;
	private Label emptyLabel4;
	private Label emptyLabel5;
	private GridLayout gridLayout;
	private Font newFont;
	
	private Button stepLeftButton;
	private Text tweakText;
	private Button stepRightButton;
	
	// TODO define colors application-wide
	final Color colorInitial = new Color( this.getForeground().getDevice(), 0, 0, 0 );
	final Color colorAlarm = new Color( this.getForeground().getDevice(), 255, 0, 0 );
	final Color colorOk = new Color( this.getForeground().getDevice(), 0, 180, 0 );
	final Color colorUnknown = new Color( this.getForeground().getDevice(), 255, 255, 255 );
	
	public MotorAxisComposite( final Composite parent, final int style, final MotorAxis motorAxis ) {
		super( parent, style );
		this.motorAxis = motorAxis;
		
		gridLayout = new GridLayout();
		initialize();
	}
	
	private void initialize() {

		// TODO is there a way to set a font view-wide instead of setting it in each widget?
		FontData[] fontData = this.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(11);
		}
		newFont = new Font(this.getForeground().getDevice(), fontData);

		//this.setBackground( new Color( this.getBackground().getDevice(), 255, 0, 0 ) );
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 

		gridLayout.numColumns = 10;
		gridLayout.marginHeight = 0;
		this.setLayout( gridLayout );
		
		// TODO create a static icon image in the view
		ImageData imageData = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ).getImageData();
		Image deleteIcon = new Image(this.getForeground().getDevice(), imageData.scaledTo(12, 12));
		final Image plusIcon = de.ptb.epics.eve.viewer.Activator.getImageDescriptor("/icons/greenPlus12.12.gif").createImage();
		final Image minusIcon = de.ptb.epics.eve.viewer.Activator.getImageDescriptor("/icons/greenMinus12.12.gif").createImage();
		
		this.closeButton = new Button( this, SWT.NONE );
		this.closeButton.setImage(deleteIcon);
		this.closeButton.addSelectionListener( this );
		
		this.expandButton = new Button( this, SWT.NONE );
		this.expandButton.setImage(plusIcon);
		this.expandButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if ((stepLeftButton == null) || (stepLeftButton.isDisposed())) {
					System.out.print("init stepLeft\n");
					initializeTweak();
					expandButton.setImage(minusIcon);
				}
				else {
					System.out.print("dispose stepLeft\n");
					expandButton.setImage(plusIcon);
					emptyLabel1.dispose();
					stepLeftButton.dispose();
					tweakText.dispose();
					stepRightButton.dispose();
					emptyLabel2.dispose();
				}
				expandButton.getParent().layout(true);
				expandButton.getParent().redraw();
				expandButton.getParent().getParent().layout(true);
				expandButton.getParent().getParent().redraw();
			}
		});

		this.motorAxisNameLabel = new Label( this, SWT.NONE );
		this.motorAxisNameLabel.setForeground(colorInitial);
		GridData gridData = new GridData();
		gridData.widthHint = 100;
		this.motorAxisNameLabel.setLayoutData( gridData );
		this.motorAxisNameLabel.setText( this.motorAxis.getName() );
		this.motorAxisNameLabel.setFont(newFont);

		this.valueLabel = new Label( this, SWT.NONE );
		gridData = new GridData();
		gridData.widthHint = 80;
		this.valueLabel.setLayoutData( gridData );
		this.valueLabel.setText( "?" );
		this.valueLabel.setFont(newFont);
		
		// TODO we want to see this label only if an engine is connected
		this.engineValueLabel = new Label( this, SWT.NONE );
		gridData = new GridData();
		gridData.widthHint = 80;
		this.engineValueLabel.setLayoutData( gridData );
		this.engineValueLabel.setText( "-" );
		this.engineValueLabel.setFont(newFont);
		
		
		if( this.motorAxis.getGoto() != null && this.motorAxis.getGoto().isDiscrete() ) {
			this.targetValueCombo = new Combo( this, SWT.NONE );
			gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			this.targetValueCombo.setLayoutData( gridData );
			this.targetValueCombo.setItems( this.motorAxis.getGoto().getDiscreteValues().toArray( new String[0] ) );
			this.targetValueCombo.setFont(newFont);
		} else {
			this.targetValueText = new Text( this, SWT.BORDER );
			gridData = new GridData();
			gridData.horizontalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			this.targetValueText.setLayoutData( gridData );
			this.targetValueText.setFont(newFont);
		}
		this.unitLabel = new Label( this, SWT.NONE );
		gridData = new GridData();
		gridData.widthHint = 30;
		this.unitLabel.setLayoutData( gridData );
		this.unitLabel.setText( "u" );
		this.unitLabel.setFont(newFont);
		
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
		this.runButton.setFont(newFont);
		gridData = new GridData();
		gridData.heightHint = 22;
		this.runButton.setLayoutData( gridData );
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
		
		
		this.offsetText = new Text( this, SWT.BORDER );
		this.offsetText.setFont(newFont);
		gridData = new GridData();
		gridData.heightHint = 20;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.offsetText.setLayoutData( gridData );
		if( this.motorAxis.getOffset() != null ) {
			
		} else {
			this.offsetText.setEnabled( false );
		}
		
		this.setOffsetButton = new Button( this, SWT.PUSH );
		this.setOffsetButton.setText( "Set" );
		this.setOffsetButton.setFont(newFont);
		gridData = new GridData();
		gridData.heightHint = 22;
		this.setOffsetButton.setLayoutData( gridData );
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

		//initializeTweak();
		
		
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

	
	
	
	
	public void initializeTweak() {
		this.emptyLabel1 = new Label( this, SWT.NONE );
		GridData gridData = new GridData();
		gridData = new GridData();
		gridData.heightHint = 20;
		gridData.horizontalSpan = 4;
		this.emptyLabel1.setLayoutData( gridData );
		
		
		this.stepLeftButton = new Button( this, SWT.PUSH );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		gridData.heightHint = 20;
		this.stepLeftButton.setLayoutData( gridData );
		this.stepLeftButton.setText( "<" );
		this.stepLeftButton.setFont(newFont);
	
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
		this.tweakText.setFont(newFont);
		gridData = new GridData();
		gridData.heightHint = 20;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.tweakText.setLayoutData( gridData );
		if( this.motorAxis.getTweakReverse() != null && this.motorAxis.getTweakValue() != null && this.motorAxis.getTweakForward() != null ) {
			
		} else {
			this.tweakText.setEnabled( false );
		}
		
		this.stepRightButton = new Button( this, SWT.PUSH );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.heightHint = 20;
		this.stepRightButton.setLayoutData( gridData );
		this.stepRightButton.setText( ">" );
		this.stepRightButton.setFont(newFont);
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
		gridData = new GridData();
		gridData = new GridData();
		gridData.heightHint = 20;
		gridData.horizontalSpan = 3;
		this.emptyLabel2.setLayoutData( gridData );
	}
	
	
	
	public void dispose() {
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		service.unregister( this );
		super.dispose();
		this.getParent().layout();
		this.getParent().redraw();
	}
	
	public void connectionStateChanged( final ConnectionState connectionState ) {
		this.motorAxisNameLabel.getDisplay().syncExec( new Runnable() {

			public void run() {
				
				String newText = "";
				Color newColor=colorInitial;
				switch( connectionState ) {
					case CONNECTED:
						newText = "connected";
						newColor = colorOk;
						break;
					case CONNECTION_FAILED:
						newText = "connection failed";
						newColor = colorAlarm;
						break;
					case CONNECTION_LOST:
						newText = "connection lost";
						newColor = colorAlarm;
						break;
					case INITIAL:
						newText = "initial";
						newColor = colorInitial;
						break;
					case UNKNOWN:
						newText = "unknown";
						newColor = colorUnknown;
				}
				Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, motorAxis.getFullIdentifyer() + " changed connection state to " + newText + "." ) );
				motorAxisNameLabel.setForeground(newColor);
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

	public void measurementDataTransmitted( final MeasurementData measurementData ) {
		if( this.motorAxis.getName().equals( measurementData.getName() ) ) {
			this.engineValueLabel.getDisplay().syncExec( new Runnable() {

				public void run() {
					engineValueLabel.setText( "(" + measurementData.getValues().get( 0 ).toString() + ")" );
					
				}
				
			});
		}
		
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		this.dispose();
		this.getParent().layout();
		this.getParent().redraw();
		
	}

	public MotorAxis getMotorAxis() {
		return this.motorAxis;
	}
}
