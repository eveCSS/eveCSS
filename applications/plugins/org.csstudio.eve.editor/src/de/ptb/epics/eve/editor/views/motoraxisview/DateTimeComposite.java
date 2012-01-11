package de.ptb.epics.eve.editor.views.motoraxisview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * <code>DateTimeComposite</code>. is a 
 * {@link org.eclipse.swt.widgets.Composite} contained in the 
 * {@link de.ptb.epics.eve.editor.views.motoraxisview.MotorAxisView}. It 
 * allows entering start, stop and stepwidth values in the time format for 
 * motor axes using add/multiply position list as step function and dates.
 * 
 * Future implementations should use this composite for date time motors...
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class DateTimeComposite extends Composite {
	
	// the underlying model the composite takes the data from
	private Axis currentAxis;
	
	// start row (declaration)
	private Button startRadioButton;
	private DateTime startDate;
	private DateTime startTime;
	private Text startMS;
	private Label startMSLabel;
	// end of: start row (declaration)
	
	// stop row (declaration)
	private Button stopRadioButton;
	private DateTime stopDate;
	private DateTime stopTime;
	private Text stopMS;
	private Label stopMSLabel;
	// end of: stop row (declaration)
	
	// stepwidth row (declaration)
	private Button stepwidthRadioButton;
	private Label stepwidthPlaceHolder;
	private DateTime stepWidthTime;
	private Text stepwidthMS;
	private Label stepwidthMSLabel;	
	// end of: stepwidth row (declaration)
	
	// stepcount row (declaration)
	private Button stepcountRadioButton;
	private Text stepcountText;
	private Label stepcountPlaceHolder;
	// end of: stepcount row (declaration)
	
	private Button mainAxisCheckButton;
	
	/**
	 * Constructs a <code>DateTimeComposite</code>.
	 * 
	 * @param parent
	 * @param style
	 */
	public DateTimeComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		this.setLayout(gridLayout);
		
		// start row (initialization)
		this.startRadioButton = new Button(this, SWT.RADIO);
		this.startRadioButton.setText("Start:");
		
		this.startDate = new DateTime(this, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		this.startTime = new DateTime(this, SWT.TIME | SWT.LONG | SWT.BORDER);
		
		this.startMS = new Text(this, SWT.RIGHT | SWT.BORDER);
		this.startMS.setText("000");
		this.startMS.setTextLimit(3);
		GridData gridData = new GridData();
		gridData.widthHint = 25;
		this.startMS.setLayoutData(gridData);
		
		this.startMSLabel = new Label(this, SWT.NONE);
		this.startMSLabel.setText("ms");
		// end of: start row (initialization)
		
		// stop row (initiliazation)
		this.stopRadioButton = new Button(this, SWT.RADIO);
		this.stopRadioButton.setText("Stop:");
		
		this.stopDate = new DateTime(this, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
		this.stopTime = new DateTime(this, SWT.TIME | SWT.LONG | SWT.BORDER);
		
		this.stopMS = new Text(this, SWT.RIGHT | SWT.BORDER);
		this.stopMS.setText("000");
		this.stopMS.setTextLimit(3);
		gridData = new GridData();
		gridData.widthHint = 25;
		this.stopMS.setLayoutData(gridData);
		
		this.stopMSLabel = new Label(this, SWT.NONE);
		this.stopMSLabel.setText("ms");
		// end of: stop row (initialization)
		
		// stepwidth row (initialization)
		this.stepwidthRadioButton = new Button(this, SWT.RADIO);
		this.stepwidthRadioButton.setText("Stepwidth:");
		
		this.stepwidthPlaceHolder = new Label(this, SWT.NONE);
		
		this.stepWidthTime = new DateTime(this, SWT.TIME | SWT.LONG | SWT.BORDER);
		
		this.stepwidthMS = new Text(this, SWT.RIGHT | SWT.BORDER);
		this.stepwidthMS.setText("000");
		this.stepwidthMS.setTextLimit(3);
		gridData = new GridData();
		gridData.widthHint = 25;
		this.stepwidthMS.setLayoutData(gridData);
		
		this.stepwidthMSLabel = new Label(this, SWT.NONE);
		this.stepwidthMSLabel.setText("ms");
		// end of: stepwidth row (initialization)
		
		// stepcount row (initialization)
		this.stepcountRadioButton = new Button(this, SWT.RADIO);
		this.stepcountRadioButton.setText("Stepcount:");
		
		this.stepcountText = new Text(this, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		this.stepcountText.setLayoutData(gridData);
		this.stepcountPlaceHolder = new Label(this, SWT.NONE);
		// end of: stepcount row (initialization)
		
		this.mainAxisCheckButton = new Button(this, SWT.CHECK);
		this.mainAxisCheckButton.setText("main axis");
		
		this.currentAxis = null;
	}
	
	/* ********************************************************************* */
	/* *************************** Listeners ******************************* */
	/* ********************************************************************* */
}