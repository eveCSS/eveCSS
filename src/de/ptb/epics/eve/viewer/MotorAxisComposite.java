package de.ptb.epics.eve.viewer;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ProcessVariableValueAdapter;
import org.csstudio.platform.simpledal.ValueType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
public class MotorAxisComposite extends Composite implements IMeasurementDataListener, SelectionListener {

	private MotorAxis motorAxis;
	
	private Button closeButton;
	private Button expandButton;
	
	private Label motorAxisNameLabel;
	private Control valueLabel;
	private Label engineValueLabel;
	private Text targetValueText;
	private Combo targetValueCombo;
	
	private Control unitLabel;
	private Button runButton;
	private Control offsetText;
	private Button setOffsetButton;
	
	private Label emptyLabel1;
	private Label emptyLabel2;
	private GridLayout gridLayout;
	private Font newFont;
	
	private Button stepLeftButton;
	private Control tweakText;
	private Button stepRightButton;
	
	private Boolean expanded;
	
	public MotorAxisComposite( final Composite parent, final int style, final MotorAxis motorAxis ) {
		super( parent, style );
		this.motorAxis = motorAxis;
		
		gridLayout = new GridLayout();
		expanded = false;
		initialize();
	}
	
	private void initialize() {

		newFont = Activator.getDefault().getFont("VIEWERFONT");

		gridLayout.numColumns = 9;
		gridLayout.marginHeight = 0;
		this.setLayout( gridLayout );
		
		ImageData imageData = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ).getImageData();
		Image deleteIcon = new Image(this.getForeground().getDevice(), imageData.scaledTo(12, 12));
		final Image plusIcon = Activator.getDefault().getImageRegistry().get("GREENPLUS12");
		final Image minusIcon = Activator.getDefault().getImageRegistry().get("GREENMINUS12");
		
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
				if (!expanded) {
					expanded = true;
					expandComposite();
					expandButton.setImage(minusIcon);
				}
				else {
					expanded = false;
					unexpandComposite();
					expandButton.setImage(plusIcon);
				}
				// TODO which do we need?
				expandButton.getParent().layout(true);
				expandButton.getParent().pack(true);
				expandButton.getParent().getParent().layout(true);
				expandButton.getParent().getParent().pack(true);
			}
		});
		
		// axis name
		this.motorAxisNameLabel = new Label( this, SWT.NONE );
		GridData gridData = new GridData();
		gridData.widthHint = 100;
		this.motorAxisNameLabel.setLayoutData( gridData );
		this.motorAxisNameLabel.setText( this.motorAxis.getName() );
		this.motorAxisNameLabel.setFont(newFont);

		// axis position
		if ((motorAxis.getPosition() != null) && ( motorAxis.getPosition().getAccess().getTransport() == TransportTypes.CA )) {
			this.valueLabel = new PvLabelComposite( this, SWT.NONE, this.motorAxis.getPosition().getAccess().getVariableID() );
		}			
		else {
			this.valueLabel = new Label( this, SWT.NONE );
			((Label) this.valueLabel).setText("unknown");
		}
		gridData = new GridData();
		gridData.widthHint = 80;
		this.valueLabel.setLayoutData( gridData );
		this.valueLabel.setFont(newFont);
		
		// TODO we want to see this label only if an engine is connected
		// position from engine
		this.engineValueLabel = new Label( this, SWT.NONE );
		gridData = new GridData();
		gridData.widthHint = 80;
		this.engineValueLabel.setLayoutData( gridData );
		this.engineValueLabel.setText( "-" );
		this.engineValueLabel.setFont(newFont);
		
		// goto position Box: TextInput or Combo
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.widthHint = 160;
		//gridData.grabExcessHorizontalSpace = true;
		if( this.motorAxis.getGoto() != null && this.motorAxis.getGoto().isDiscrete() ) {
			this.targetValueCombo = new Combo( this, SWT.NONE );
			this.targetValueCombo.setItems( this.motorAxis.getGoto().getDiscreteValues().toArray( new String[0] ) );
			this.targetValueCombo.setLayoutData( gridData );
			this.targetValueCombo.setFont(newFont);
		} else {
			this.targetValueText = new Text( this, SWT.BORDER );
			this.targetValueText.setLayoutData( gridData );
			this.targetValueText.setFont(newFont);
		}

		// unit Label
		if ((motorAxis.getUnit() != null) && (motorAxis.getUnit().getAccess() != null) &&( motorAxis.getUnit().getAccess().getTransport() == TransportTypes.CA )) {
			this.unitLabel = new PvLabelComposite( this, SWT.NONE, this.motorAxis.getUnit().getAccess().getVariableID() );
			((PvLabelComposite) this.unitLabel).setText("unit");
		}			
		else {
			this.unitLabel = new Label( this, SWT.NONE );
			if ((motorAxis.getUnit() != null) && (motorAxis.getUnit().getValue() != null))
				((Label) this.unitLabel).setText(this.motorAxis.getUnit().getValue());
			else
				((Label) this.unitLabel).setText("unit");
		}
		gridData = new GridData();
		gridData.widthHint = 30;
		this.unitLabel.setLayoutData( gridData );
		this.unitLabel.setFont(newFont);
		
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

				if( motorAxis.getGoto().getAccess().getTransport() == TransportTypes.CA ) {
					String prefix = "dal-epics://";
					IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
					ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 
					IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + motorAxis.getGoto().getAccess().getVariableID() );
					try {
						service.writeValueSynchronously( pv, targetValueText != null?targetValueText.getText():targetValueCombo.getText(), Helper.dataTypesToValueType( motorAxis.getGoto().getType() ) );
					} catch( final ConnectionException e1 ) {
						e1.printStackTrace();
					}
				}	
			}
			
		});
		
		if ((motorAxis.getOffset() != null) && ( motorAxis.getOffset().getAccess().getTransport() == TransportTypes.CA )){
			this.offsetText = new PvTextComposite( this, SWT.NONE, motorAxis.getOffset().getAccess().getVariableID() );
			//((PvTextComposite)offsetText).dalregister();
		}
		else {
			this.offsetText = new Text( this, SWT.BORDER );
			this.offsetText.setEnabled( false );
		}
		this.offsetText.setFont(newFont);
		gridData = new GridData();
		gridData.heightHint = 20;
		gridData.horizontalAlignment = SWT.FILL;
		//gridData.grabExcessHorizontalSpace = true;
		this.offsetText.setLayoutData( gridData );
	}
	
	
	private void expandComposite() {
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
	
		if( this.motorAxis.getTweakReverse() == null ) {
			this.stepLeftButton.setEnabled( false );
		}
		else {
			this.stepLeftButton.addSelectionListener( new SelectionListener() {

				public void widgetDefaultSelected( final SelectionEvent e ) {
								
				}

				public void widgetSelected( final SelectionEvent e ) {

					if( motorAxis.getTweakReverse().getAccess().getTransport() == TransportTypes.CA ) {
						String prefix = "dal-epics://";
				
						IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
						ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 
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
				}
			});
		} 		
		
		if ((motorAxis.getTweakValue() != null) && ( motorAxis.getTweakValue().getAccess().getTransport() == TransportTypes.CA )){
			this.tweakText = new PvTextComposite( this, SWT.NONE, motorAxis.getTweakValue().getAccess().getVariableID() );
		}
		else {
			this.tweakText = new Text( this, SWT.BORDER );
			this.tweakText.setEnabled( false );
		}
		this.tweakText.setFont(newFont);
		gridData = new GridData();
		gridData.heightHint = 20;
		gridData.horizontalAlignment = SWT.FILL;
		//gridData.grabExcessHorizontalSpace = true;
		this.tweakText.setLayoutData( gridData );

		this.stepRightButton = new Button( this, SWT.PUSH );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.heightHint = 20;
		this.stepRightButton.setLayoutData( gridData );
		this.stepRightButton.setText( ">" );
		this.stepRightButton.setFont(newFont);
		if( this.motorAxis.getTweakForward() == null ) {
			this.stepRightButton.setEnabled( false );
		}
		else {
			
			this.stepRightButton.addSelectionListener( new SelectionListener() {

				public void widgetDefaultSelected( final SelectionEvent e ) {
					
				}

				public void widgetSelected( final SelectionEvent e ) {
					
					if( motorAxis.getTweakForward().getAccess().getTransport() == TransportTypes.CA ) {

						String prefix = "dal-epics://";
						IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
						ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 
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
				}
			});
		}
		this.emptyLabel2 = new Label( this, SWT.NONE );
		gridData = new GridData();
		gridData = new GridData();
		gridData.heightHint = 20;
		gridData.horizontalSpan = 2;
		this.emptyLabel2.setLayoutData( gridData );
	}
	
	private void unexpandComposite() {
		emptyLabel1.dispose();
		stepLeftButton.dispose();
		tweakText.dispose();
		stepRightButton.dispose();
		emptyLabel2.dispose();
	}
	
	public void dispose() {
		super.dispose();
		if (expanded) unexpandComposite();
		valueLabel.dispose();
		unitLabel.dispose();
		offsetText.dispose();
		this.getParent().layout();
		this.getParent().redraw();
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
