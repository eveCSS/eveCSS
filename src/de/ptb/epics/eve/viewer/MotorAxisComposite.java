package de.ptb.epics.eve.viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;

public class MotorAxisComposite extends Composite {

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
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
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
				// Einheit vom Netz lesen.
			}
		}
		
		this.runButton = new Button( this, SWT.PUSH );
		this.runButton.setText( "Run" );
		
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
		
		this.setOffsetButton = new Button( this, SWT.PUSH );
		this.setOffsetButton.setText( "Set" );
		
		
		this.emptyLabel1 = new Label( this, SWT.NONE );
		
		this.stepLeftButton = new Button( this, SWT.PUSH );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		this.stepLeftButton.setLayoutData( gridData );
		this.stepLeftButton.setText( "<" );
		
		this.tweakText = new Text( this, SWT.BORDER );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.tweakText.setLayoutData( gridData );
		
		this.stepRightButton = new Button( this, SWT.PUSH );
		this.stepRightButton.setText( ">" );
		
		this.emptyLabel2 = new Label( this, SWT.NONE );
		this.emptyLabel3 = new Label( this, SWT.NONE );
		this.emptyLabel4 = new Label( this, SWT.NONE );
		this.emptyLabel5 = new Label( this, SWT.NONE );
		
	}
}
