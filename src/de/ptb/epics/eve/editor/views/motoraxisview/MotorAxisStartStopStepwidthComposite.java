package de.ptb.epics.eve.editor.views.motoraxisview;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * <code>MotorAxisStartStopStedwidthComposite</code> is a composite to define
 * Start, Stop, Stepwidth and Stepcount of the motor axis. (Shown if Step 
 * Function is either Add or Multiply)
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class MotorAxisStartStopStepwidthComposite extends Composite {

	// logging
	private static Logger logger = 
		Logger.getLogger(MotorAxisStartStopStepwidthComposite.class.getName());	

	// the underlying model the composite takes the data from
	private Axis currentAxis;

	private MotorAxisView motorAxisView;
	
	// start elements
	private Button autoFillStartRadioButton;
	private AutoFillStartRadioButtonSelectionListener
			autoFillStartRadioButtonSelectionListener;
	
	private CCombo startCombo;
	private ComboVerifyListener startComboVerifyListener;
	private StartComboModifyListener startComboModifyListener;
	private StartComboSelectionListener startComboSelectionListener;
	private Label startErrorLabel;
	// end of: start elements
	
	// stop elements
	private Button autoFillStopRadioButton;
	private AutoFillStopRadioButtonSelectionListener
			autoFillStopRadioButtonSelectionListener;
	
	private CCombo stopCombo;
	private ComboVerifyListener stopComboVerifyListener;
	private StopComboModifyListener stopComboModifyListener;
	private StopComboSelectionListener stopComboSelectionListener;
	private Label stopErrorLabel;
	// end of: stop elements
	
	// step width elements
	private Button autoFillStepwidthRadioButton;
	private AutoFillStepwidthRadioButtonSelectionListener
			autoFillStepwidthRadioButtonSelectionListener;
	
	private Text stepwidthText;
	private TextDoubleVerifyListener stepwidthTextVerifyListener;
	private StepwidthTextModifyListener stepwidthTextModifyListener;
	private Label stepwidthErrorLabel;
	// end of: step width elements
	
	// step count elements
	private Button autoFillStepcountRadioButton;
	private AutoFillStepcountRadioButtonSelectionListener
			autoFillStepcountRadioButtonSelectionListener;
	
	private Text stepcountText;
	private TextNumberVerifyListener stepcountTextVerifyListener;
	private StepcountTextModifyListener stepcountTextModifyListener;
	private Label stepcountErrorLabel;
	// end of step count elements
	
	private Button mainAxisCheckBox;
	private MainAxisCheckBoxSelectionListener mainAxisCheckBoxSelectionListener;
	
	/**
	 * Constructs a <code>MotorAxisStartStopStepwidthComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param parentView the view this composite is contained in
	 */
	public MotorAxisStartStopStepwidthComposite(final Composite parent, 
												final int style, 
												final ViewPart parentView) {
		super(parent, style);
		
		this.motorAxisView = (MotorAxisView) parentView;
		
		// the composite gets a 3 column grid
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		this.setLayout(gridLayout);

		// initialize start elements
		this.autoFillStartRadioButton = new Button(this, SWT.RADIO);
		this.autoFillStartRadioButton.setText("Start:");
		this.autoFillStartRadioButton.setToolTipText(
				"Mark to enable auto-fill for start value.");
		this.autoFillStartRadioButtonSelectionListener = 
				new AutoFillStartRadioButtonSelectionListener();
		this.autoFillStartRadioButton.addSelectionListener(
				autoFillStartRadioButtonSelectionListener);

		this.startCombo = new CCombo(this, SWT.BORDER);
		this.startComboVerifyListener = new ComboVerifyListener();
		this.startCombo.addVerifyListener(startComboVerifyListener);
		this.startComboModifyListener = new StartComboModifyListener();
		this.startCombo.addModifyListener(startComboModifyListener);
		this.startComboSelectionListener = new StartComboSelectionListener();
		this.startCombo.addSelectionListener(startComboSelectionListener);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.startCombo.setLayoutData(gridData);
		
		this.startErrorLabel = new Label(this, SWT.NONE);
		// end of: initialize start elements
		
		// initialize stop elements
		this.autoFillStopRadioButton = new Button(this, SWT.RADIO);
		this.autoFillStopRadioButton.setText("Stop:");
		this.autoFillStopRadioButton.setToolTipText(
				"Mark to enable auto-fill for stop value.");
		this.autoFillStopRadioButtonSelectionListener = 
				new AutoFillStopRadioButtonSelectionListener();
		this.autoFillStopRadioButton.addSelectionListener(
				autoFillStartRadioButtonSelectionListener);

		this.stopCombo = new CCombo(this, SWT.BORDER);
		this.stopComboVerifyListener = new ComboVerifyListener();
		this.stopCombo.addVerifyListener(stopComboVerifyListener);
		this.stopComboModifyListener = new StopComboModifyListener();
		this.stopCombo.addModifyListener(stopComboModifyListener);
		this.stopComboSelectionListener = new StopComboSelectionListener();
		this.stopCombo.addSelectionListener(stopComboSelectionListener);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stopCombo.setLayoutData(gridData);
		this.stopErrorLabel = new Label(this, SWT.NONE);
		// end of: initialize stop elements
		
		// initialize step width elements
		this.autoFillStepwidthRadioButton = new Button(this, SWT.RADIO);
		this.autoFillStepwidthRadioButton.setText("Stepwidth:");
		this.autoFillStepwidthRadioButton.setToolTipText(
				"Mark to enable auto-fill for stepwidth value.");
		this.autoFillStepwidthRadioButtonSelectionListener = 
				new AutoFillStepwidthRadioButtonSelectionListener();
		this.autoFillStepwidthRadioButton.addSelectionListener(
				autoFillStepwidthRadioButtonSelectionListener);

		this.stepwidthText = new Text(this, SWT.BORDER);
		this.stepwidthTextVerifyListener = new TextDoubleVerifyListener();
		this.stepwidthText.addVerifyListener(stepwidthTextVerifyListener);
		this.stepwidthTextModifyListener = new StepwidthTextModifyListener();
		this.stepwidthText.addModifyListener(stepwidthTextModifyListener);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stepwidthText.setLayoutData(gridData);
		this.stepwidthErrorLabel = new Label(this, SWT.NONE);
		// end of: initialize step width elements 
		
		// initialize step count elements
		this.autoFillStepcountRadioButton = new Button(this, SWT.RADIO);
		this.autoFillStepcountRadioButton.setText("Stepcount:");
		this.autoFillStepcountRadioButton.setToolTipText(
				"Mark to enable auto-fill for step count.");
		this.autoFillStepcountRadioButtonSelectionListener = 
				new AutoFillStepcountRadioButtonSelectionListener();
		this.autoFillStepcountRadioButton.addSelectionListener(
				autoFillStepcountRadioButtonSelectionListener);

		this.stepcountText = new Text(this, SWT.BORDER);
		this.stepcountTextVerifyListener = new TextNumberVerifyListener();
		this.stepcountText.addVerifyListener(stepcountTextVerifyListener);
		this.stepcountTextModifyListener = new StepcountTextModifyListener();
		this.stepcountText.addModifyListener(stepcountTextModifyListener);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stepcountText.setLayoutData(gridData);
		this.stepcountErrorLabel = new Label(this, SWT.NONE);
		// end of: initialize step count elements
		
		this.mainAxisCheckBox = new Button(this, SWT.CHECK);
		this.mainAxisCheckBox.setText("main axis");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		this.mainAxisCheckBox.setLayoutData(gridData);
		this.mainAxisCheckBoxSelectionListener = 
				new MainAxisCheckBoxSelectionListener();
		this.mainAxisCheckBox.addSelectionListener(
				mainAxisCheckBoxSelectionListener);
		
		this.startCombo.setEnabled(false);
		this.stopCombo.setEnabled(false);
		this.stepwidthText.setEnabled(false);
		this.stepcountText.setEnabled(false);
		this.mainAxisCheckBox.setEnabled(false);
	}

	/**
	 * calculate the height to see all entries of this composite
	 * 
	 * @return the needed height of Composite to see all entries
	 */
	public int getTargetHeight() {
		return (mainAxisCheckBox.getBounds().y + 
				mainAxisCheckBox.getBounds().height + 5);
	}

	/**
	 * calculate the width to see all entries of this composite
	 * 
	 * @return the needed width of Composite to see all entries
	 */
	public int getTargetWidth() {
		return (mainAxisCheckBox.getBounds().x + 
				mainAxisCheckBox.getBounds().width + 5);
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.Axis}.
	 *  
	 * @param axis the {@link de.ptb.epics.eve.data.scandescription.Axis} that 
	 * 		  should be set
	 * @param stepcount the step count (generally the step count of the axis, 
	 * 		  except when a main axis is set which is then used instead)
	 * @param stepcount the step count of the main axis 
	 */
    public void setCurrentAxis(final Axis axis, final double stepcount) {

    	
    	if(axis != null)
    		logger.debug("set axis to: " + axis.getMotorAxis().getID());
    	else
    		logger.debug("set axis to: null");
    		
    	removeListeners();
    	
    	this.currentAxis = axis;
    	
		if(this.currentAxis != null) {
			
			if(this.currentAxis.getMotorAxis().getGoto().isDiscrete()) {
				this.startCombo.setItems(this.currentAxis.getMotorAxis().
										 getGoto().getDiscreteValues().
										 toArray(new String[0]));
				this.startCombo.setEditable(false);
				this.stopCombo.setItems(this.currentAxis.getMotorAxis().
										getGoto().getDiscreteValues().
										toArray(new String[0]));
				this.stopCombo.setEditable(false);
			}
			else {
				this.startCombo.setEditable(true);
				this.startCombo.setVisibleItemCount(0);
				this.stopCombo.setEditable(true);
				this.stopCombo.setVisibleItemCount(0);
			}
			this.startCombo.setText(this.currentAxis.getStart() != null
								    ? this.currentAxis.getStart()
								    : "");
			this.startCombo.setSelection(new Point(0,0));
			this.stopCombo.setText(this.currentAxis.getStop() != null
								   ? this.currentAxis.getStop()
								   : "");
			this.stopCombo.setSelection(new Point(0,0));
			this.stepwidthText.setText(this.currentAxis.getStepwidth() != null
									   ? this.currentAxis.getStepwidth()
									   : "");

			if(stepcount != -1.0 && !axis.isMainAxis()) {
				if(this.currentAxis.getMotorAxis().getGoto().isDiscrete()) {
					Integer stepInt = (int)stepcount;
					this.stepcountText.setText(Integer.toString(stepInt));
				} else
				   this.stepcountText.setText(Double.toString(stepcount));
			} else {
				if(this.currentAxis.getMotorAxis().getGoto().isDiscrete()) {
					Integer stepInt = (int)currentAxis.getStepCount();
					this.stepcountText.setText(Integer.toString(stepInt));
				} else
					this.stepcountText.setText(
						Double.toString(currentAxis.getStepCount()));
			}

			this.mainAxisCheckBox.setSelection(this.currentAxis.isMainAxis());
			
			this.startCombo.setEnabled(true);
			this.stopCombo.setEnabled(true);
			this.stepwidthText.setEnabled(true);

			if(stepcount != -1.0 && !axis.isMainAxis()) {
				this.stepcountText.setEnabled(false);
				this.autoFillStepcountRadioButton.setEnabled(false);
				// Stepwidth RadioButton wird voreingestellt 
				this.autoFillStepwidthRadioButton.setSelection(true);
				this.stepwidthText.setEnabled(false);
			} else {
				// Stepcount RadioButton wird voreingestellt 
				this.autoFillStepcountRadioButton.setSelection(true);
				this.autoFillStepcountRadioButton.setEnabled(false);
				this.stepcountText.setEnabled(false);
			}
			if(this.mainAxisCheckBox.getSelection() || stepcount == -1.0) {
				this.mainAxisCheckBox.setEnabled(true);
			}
			checkForErrors();
		}
		
		addListeners();
	}
	
    /*
     * 
     */
    private void checkForErrors()
    {
    	// reset errors
    	this.startErrorLabel.setImage(null);
		this.startErrorLabel.setToolTipText("");
		this.stopErrorLabel.setImage(null);
		this.stopErrorLabel.setToolTipText("");
		this.stepwidthErrorLabel.setImage(null);
		this.stepwidthErrorLabel.setToolTipText("");	
		this.stepcountErrorLabel.setImage(null);
		this.stepcountErrorLabel.setToolTipText("");	
		
		final Iterator<IModelError> it = 
				this.currentAxis.getModelErrors().iterator();
		
		while(it.hasNext()) {
			final IModelError modelError = it.next();
			if(modelError instanceof AxisError) {
				final AxisError axisError = (AxisError)modelError;
	
				switch(axisError.getErrorType()) {
					case START_NOT_SET:
						this.startErrorLabel.setImage(PlatformUI.getWorkbench().
											getSharedImages().getImage(
											ISharedImages.IMG_OBJS_ERROR_TSK));
						this.startErrorLabel.setToolTipText(
								"Start values has not been set!");
						// update and resize View with getParent().layout()
						this.startErrorLabel.getParent().layout();
						break;
						
					case START_VALUE_NOT_POSSIBLE:
						this.startErrorLabel.setImage(PlatformUI.getWorkbench().
											getSharedImages().getImage(
											ISharedImages.IMG_OBJS_ERROR_TSK));
						this.startErrorLabel.setToolTipText(
								"Start values not possible!");
						this.startErrorLabel.getParent().layout();
						break;
						
					case STOP_NOT_SET:
						this.stopErrorLabel.setImage(PlatformUI.getWorkbench().
											getSharedImages().getImage(
											ISharedImages.IMG_OBJS_ERROR_TSK));
						this.stopErrorLabel.setToolTipText(
								"Stop values hat not been set!");
						this.stopErrorLabel.getParent().layout();
						break;
						
					case STOP_VALUE_NOT_POSSIBLE:
						this.stopErrorLabel.setImage(PlatformUI.getWorkbench().
											getSharedImages().getImage(
											ISharedImages.IMG_OBJS_ERROR_TSK));
						this.stopErrorLabel.setToolTipText(
								"Stop values not possible!");
						this.stopErrorLabel.getParent().layout();
						break;
						
					case STEPWIDTH_NOT_SET:
						this.stepwidthErrorLabel.setImage(PlatformUI.
									getWorkbench().getSharedImages().
									getImage( ISharedImages.IMG_OBJS_ERROR_TSK));
						this.stepwidthErrorLabel.setToolTipText( 
								"Stepwidth values hat not been set!");
						this.stepwidthErrorLabel.getParent().layout();
						break;

					case STEPCOUNT_NOT_SET:
						this.stepcountErrorLabel.setImage(PlatformUI.
									getWorkbench().getSharedImages().
									getImage( ISharedImages.IMG_OBJS_ERROR_TSK));
						this.stepcountErrorLabel.setToolTipText( 
								"Stepwidth values hat not been set!");
						this.stepcountErrorLabel.getParent().layout();
						break;
				}
			}
		}
    }
    
    /*
     * 
     */
	private void autoFill() {

		boolean startOk = true;
		boolean stopOk = true;
		boolean stepwidthOk = true;
		boolean stepcountOk = true;
		
		for (IModelError error: this.currentAxis.getModelErrors()) {
			if( error instanceof AxisError) {
				final AxisError axisError = (AxisError)error;
				switch(axisError.getErrorType()) {
					case START_NOT_SET:
					case START_VALUE_NOT_POSSIBLE:
						startOk = false;
						break;
					case STOP_NOT_SET:
					case STOP_VALUE_NOT_POSSIBLE:
						stopOk = false;
						break;
					case STEPWIDTH_NOT_SET:
						stepwidthOk = false;
						break;
					case STEPCOUNT_NOT_SET:
						stepcountOk = false;
						break;
				}
			}
		}
		
		
		if( this.currentAxis != null ) {
			if( this.autoFillStartRadioButton.getSelection() ) {
				if ( stopOk && stepwidthOk && stepcountOk) {
					// Alle Werte OK, Start-Wert kann berechnet werden
					if( !this.currentAxis.getMotorAxis().getGoto().isDiscrete() ) {
						final double stop = Double.parseDouble( this.startCombo.getText() );
						final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
						final double stepcount = Double.parseDouble( this.stepcountText.getText() );

						this.startCombo.setText( "" + (stop - (stepwidth * stepcount) ) );
						currentAxis.setStart(this.startCombo.getText());
					} else {
						List< String > values = this.currentAxis.getMotorAxis().getGoto().getDiscreteValues();
								
						final int stop = values.indexOf( this.stopCombo.getText() );
						final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );
						final int stepcount = Integer.parseInt( this.stepcountText.getText() );
								
						int index = ( stop - (stepwidth * stepcount) );
						if( index < 0 ) {
							this.startCombo.deselectAll();
							currentAxis.setStart(null);
						} else if( index >= values.size() ) {
							this.startCombo.deselectAll();
							currentAxis.setStart(null);
						} else {
							this.startCombo.setText( values.get( index ) );
							this.startCombo.setSelection(new Point(0,0));
							currentAxis.setStart(this.startCombo.getText());
						}
					}
				}
			} else if( this.autoFillStopRadioButton.getSelection() ) {
				if ( startOk && stepwidthOk && stepcountOk) {
					if( !this.currentAxis.getMotorAxis().getGoto().isDiscrete() ) {
						final double start = Double.parseDouble( this.startCombo.getText() );
						final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
						final double stepcount = Double.parseDouble( this.stepcountText.getText() );

						this.stopCombo.setText( "" + (start + (stepwidth * stepcount) ) );
						currentAxis.setStop(this.startCombo.getText());
					} else {
						List< String > values = this.currentAxis.getMotorAxis().getGoto().getDiscreteValues();
								
						final int start = values.indexOf( this.startCombo.getText() );
						final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );
						final int stepcount = Integer.parseInt( this.stepcountText.getText() );
								
						int index = ( start + (stepwidth * stepcount) );
						if( index < 0 ) {
							this.stopCombo.deselectAll();
							currentAxis.setStop(null);
						} else if( index >= values.size() ) {
							this.stopCombo.deselectAll();
							currentAxis.setStop(null);
						} else {
							this.stopCombo.setText( values.get( index ) );
							this.stopCombo.setSelection(new Point(0,0));
							currentAxis.setStop(this.stopCombo.getText());
						}
					}
				}
			} else if( this.autoFillStepwidthRadioButton.getSelection() ) {
				if ( startOk && stopOk && stepcountOk) {
					if( !this.currentAxis.getMotorAxis().getGoto().isDiscrete() ) {
						final double start = Double.parseDouble( this.startCombo.getText() );
						final double stop = Double.parseDouble( this.stopCombo.getText() );
						final double stepcount = Double.parseDouble( this.stepcountText.getText() );
								
						if ( !this.stepwidthText.getText().equals("")) {
							// stepwidth Eintrag schon vorhanden
							final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
							// Wenn Zähler oder Nenner gleich 0, besondere Behandlung
							if ((stop - start == 0) || (stepcount == 0)) {
								if ( stepwidth == 0) {
									// Wert wird nicht nochmal gesetzt
								}
								else {
									this.stepwidthText.setText( "0" );
									currentAxis.setStepwidth(this.stepwidthText.getText());
								}
							}
							else if ( stepwidth == (( stop - start) / stepcount )) {
								// Wert wird nicht nochmal gesetzt
							}
							else {
								this.stepwidthText.setText( "" + (( stop - start) / stepcount ) );
								currentAxis.setStepwidth(this.stepwidthText.getText());
							}
						}
						else {
							this.stepwidthText.setText( "" + ( (stop - start ) / stepcount ) );
							currentAxis.setStepwidth(this.stepwidthText.getText());
							}
					} else {
						List< String > values = this.currentAxis.getMotorAxis().getGoto().getDiscreteValues();
								
						final int start = values.indexOf( this.startCombo.getText() );
						final int stop = values.indexOf( this.stopCombo.getText() );
						final int stepcount = Integer.parseInt( this.stepcountText.getText() );

						if (stepcount != 0) {
							if ( !this.stepwidthText.getText().equals("")) {
								// stepwidth Eintrag schon vorhanden
								final double stepwidth_d = Double.parseDouble( this.stepwidthText.getText() );
								final int stepwidth = (int)stepwidth_d;
								if ( stepwidth == (( stop - start) / stepcount )) {
									// Wert wird nicht nochmal gesetzt
								}
								else {
									this.stepwidthText.setText( "" + (( stop - start) / stepcount ) );
									currentAxis.setStepwidth(this.stepwidthText.getText());
								}
							}
							else {
								this.stepwidthText.setText( "" + ( (stop - start ) / stepcount ) );
								currentAxis.setStepwidth(this.stepwidthText.getText());
							}
						}
					}
				}
			} else if( this.autoFillStepcountRadioButton.getSelection() ) {
				if ( startOk && stopOk && stepwidthOk) {
					if( !this.currentAxis.getMotorAxis().getGoto().isDiscrete() ) {
						final double start = Double.parseDouble( this.startCombo.getText() );
						final double stop = Double.parseDouble( this.stopCombo.getText() );
						double stepwidth = Double.parseDouble( this.stepwidthText.getText() );

						if ((start - stop) > 0) {
							// stepwidth muß negativ sein!
							if (stepwidth > 0) {
								// Vorzeichen von Stepwidth umdrehen!
								stepwidth = stepwidth * -1;
								this.stepwidthText.setText( "" + (int)stepwidth );
								this.stepwidthText.setSelection(2);
								currentAxis.setStepwidth(this.stepwidthText.getText());
							}
						}
						if ((start - stop) < 0) {
							// stepwidth muß positiv sein!
							if (stepwidth < 0) {
								// Vorzeichen von Stepwidth umdrehen!
								stepwidth = stepwidth * -1;
								this.stepwidthText.setText( "" + (int)stepwidth );
								this.stepwidthText.setSelection(1);
								currentAxis.setStepwidth(this.stepwidthText.getText());
							}
						}
						if ( !this.stepcountText.getText().equals("")) {
							// stepcount Eintrag schon vorhanden
							final double stepcount = Double.parseDouble( this.stepcountText.getText() );
							// Wenn Zähler oder Nenner gleich 0, besondere Behandlung
							if ((stop - start == 0) || (stepwidth == 0)) {
								if ( stepcount == 0) {
									// Wert wird nicht nochmal gesetzt
								}
								else {
									this.stepcountText.setText( "0" );
									currentAxis.setStepCount(0);
								}
							}
							else if ( stepcount == (( stop - start) / stepwidth )) {
								// Wert wird nicht nochmal gesetzt
							}
							else {
								this.stepcountText.setText( "" + (( stop - start) / stepwidth ) );
								currentAxis.setStepCount(Double.parseDouble( this.stepcountText.getText() ));
							}
						}
						else {
							this.stepcountText.setText( "" + ( (stop - start) / stepwidth ) );
							currentAxis.setStepCount(Double.parseDouble( this.stepcountText.getText() ));
						}
					} else {
						List< String > values = this.currentAxis.getMotorAxis().getGoto().getDiscreteValues();

						final int start = values.indexOf( this.startCombo.getText() );
						final int stop = values.indexOf( this.stopCombo.getText() );
						int stepwidth = Integer.parseInt( this.stepwidthText.getText() );

						if ((start - stop) > 0) {
							// stepwidth muß negativ sein!
							if (stepwidth > 0) {
								// Vorzeichen von Stepwidth umdrehen!
								stepwidth = stepwidth * -1;
								this.stepwidthText.setText( "" + (int)stepwidth );
								this.stepwidthText.setSelection(2);
								currentAxis.setStepwidth(this.stepwidthText.getText());
							}
						}
						if ((start - stop) < 0) {
							// stepwidth muß positiv sein!
							if (stepwidth < 0) {
								// Vorzeichen von Stepwidth umdrehen!
								stepwidth = stepwidth * -1;
								this.stepwidthText.setText( "" + (int)stepwidth );
								this.stepwidthText.setSelection(1);
								currentAxis.setStepwidth(this.stepwidthText.getText());
							}
						}
						
						if (stepwidth != 0) {
							if ( !this.stepcountText.getText().equals("") ) {
								// stepcount Eintrag schon vorhanden
								final double stepcount_d = Double.parseDouble( this.stepcountText.getText() );
								final int stepcount = (int)stepcount_d;
								if ( stepcount == (( stop - start) / stepwidth )) {
									// Wert wird nicht nochmal gesetzt
								}
								else {
									this.stepcountText.setText( "" + (( stop - start) / stepwidth ) );
									currentAxis.setStepCount(Double.parseDouble(this.stepcountText.getText()));
								}
							}
							else {
								this.stepcountText.setText( "" + (( stop - start) / stepwidth ) );
								currentAxis.setStepCount(Double.parseDouble(this.stepcountText.getText()));
							}
						}
					}
				} 
			}
		}
	}

	/*
	 * If stepcount of main axis changes, stepwidth of all other axis recalculated.
	 */
	private void recalculateStepwidth() {
		if (currentAxis.isMainAxis()) {
			ScanModule scanModul = currentAxis.getScanModule();
			Axis[] axis = scanModul.getAxis();
			
			for( int i = 0; i < axis.length; ++i ) {
				if( !axis[i].isMainAxis() ) {
					// Axis ist keine mainAxis und wird neu berechnet
					if( !axis[i].getMotorAxis().getGoto().isDiscrete() ) {
						// Achse i ist eine normale Achse
						final double start = Double.parseDouble(axis[i].getStart());
						final double stop = Double.parseDouble( axis[i].getStop() );
						final double stepwidth = Double.parseDouble( axis[i].getStepwidth() );
						final double stepcount = Double.parseDouble( stepcountText.getText() );

						// Wenn Zähler oder Nenner gleich 0, besondere Behandlung
						if ((stop - start == 0) || (stepcount == 0)) {
							if ( stepwidth == 0) {
								// Wert wird nicht nochmal gesetzt
							}
							else {
								axis[i].setStepwidth( "0" );
							}
						}
						else if ( stepwidth == (( stop - start) / stepcount )) {
							// Wert wird nicht nochmal gesetzt
						}
						else {
							axis[i].setStepwidth("" + (( stop - start) / stepcount ) );
						}
					} else {
						// Achse i ist eine diskrete Achse
						int start = 0;
						int stop = 0;
							
						String[] werte = axis[i].getMotorAxis().getGoto().getDiscreteValues().toArray( new String[0] );
						// Schleife über Werte durchlaufen lassen
						for( int j = 0; j < werte.length; ++j ) {
							if (werte[j].equals(axis[i].getStart())) {
								start = j;
							}
							if (werte[j].equals(axis[i].getStop())) {
								stop = j; 
							}
						}
						final double stepwidth_d = Double.parseDouble( axis[i].getStepwidth() );
						final int stepwidth = (int)stepwidth_d;
						final double stepcount_d = Double.parseDouble( stepcountText.getText() );
						final int stepcount = (int)stepcount_d;

						// Wenn Zähler oder Nenner gleich 0, besondere Behandlung
						if ((stop - start == 0) || (stepcount == 0)) {
							if ( stepwidth == 0) {
								// Wert wird nicht nochmal gesetzt
							}
							else {
								axis[i].setStepwidth( "0" );
							}
						}
						else if ( stepwidth == (( stop - start) / stepcount )) {
							// Wert wird nicht nochmal gesetzt
						}
						else {
							axis[i].setStepwidth("" + (( stop - start) / stepcount ) );
						}
					}
				}
			}
		}
	}

	/*
	 * 
	 */
	private void addListeners()
	{
		autoFillStartRadioButton.addSelectionListener(
				autoFillStartRadioButtonSelectionListener);
		autoFillStopRadioButton.addSelectionListener(
				autoFillStopRadioButtonSelectionListener);
		autoFillStepwidthRadioButton.addSelectionListener(
				autoFillStepwidthRadioButtonSelectionListener);
		autoFillStepcountRadioButton.addSelectionListener(
				autoFillStepcountRadioButtonSelectionListener);

		if( this.currentAxis.getMotorAxis().getGoto().isDiscrete()) {
			startCombo.addSelectionListener(startComboSelectionListener);
			stopCombo.addSelectionListener(stopComboSelectionListener);
		}
		else {
			startCombo.addModifyListener(startComboModifyListener);
			startCombo.addVerifyListener(startComboVerifyListener);
			stopCombo.addModifyListener(stopComboModifyListener);
			stopCombo.addVerifyListener(stopComboVerifyListener);
		}

		stepwidthText.addModifyListener(stepwidthTextModifyListener);
		stepwidthText.addVerifyListener(stepwidthTextVerifyListener);
		
		stepcountText.addModifyListener(stepcountTextModifyListener);
		stepcountText.addVerifyListener(stepcountTextVerifyListener);
		
		mainAxisCheckBox.addSelectionListener(
				mainAxisCheckBoxSelectionListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners()
	{
		autoFillStartRadioButton.removeSelectionListener(
				autoFillStartRadioButtonSelectionListener);
		startCombo.removeVerifyListener(startComboVerifyListener);
		startCombo.removeModifyListener(startComboModifyListener);
		startCombo.removeSelectionListener(startComboSelectionListener);
		
		autoFillStopRadioButton.removeSelectionListener(
				autoFillStopRadioButtonSelectionListener);
		stopCombo.removeVerifyListener(stopComboVerifyListener);
		stopCombo.removeModifyListener(stopComboModifyListener);
		stopCombo.removeSelectionListener(stopComboSelectionListener);
		
		autoFillStepwidthRadioButton.removeSelectionListener(
				autoFillStepwidthRadioButtonSelectionListener);
		stepwidthText.removeVerifyListener(stepwidthTextVerifyListener);
		stepwidthText.removeModifyListener(stepwidthTextModifyListener);
		
		autoFillStepcountRadioButton.removeSelectionListener(
				autoFillStepcountRadioButtonSelectionListener);
		stepcountText.removeVerifyListener(stepcountTextVerifyListener);
		stepcountText.removeModifyListener(stepcountTextModifyListener);
		
		mainAxisCheckBox.removeSelectionListener(
				mainAxisCheckBoxSelectionListener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		removeListeners();
		super.dispose();
	}
	
	///////////////////////////////////////////////////////////
	// Hier kommen jetzt die verschiedenen Listener Klassen
	///////////////////////////////////////////////////////////
	/**
	 * <code>SelectionListener</code> of AutoFillStart RadioButton from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class AutoFillStartRadioButtonSelectionListener implements SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected( final SelectionEvent e ) {
			}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected( final SelectionEvent e ) {
			if (autoFillStartRadioButton.getSelection()) {
				startCombo.setEnabled( false );
			}
			else
			   startCombo.setEnabled( true );
		}
	};

	/**
	 * <code>ModifyListener</code> of Start Combo from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class StartComboModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {

			motorAxisView.suspendModelUpdateListener();
			removeListeners();
			if( currentAxis != null ) {
				// if string is a double set value in model
				// else set null in model
				try {
					Double.parseDouble( startCombo.getText() );
					currentAxis.setStart(startCombo.getText());
					autoFill();	
				} catch( final NumberFormatException ex ) {
					// string is not a double
					System.out.println("String ist leer oder kein double");
					currentAxis.setStart(null);
				}
			}
			checkForErrors();
			addListeners();
			motorAxisView.resumeModelUpdateListener();
		}
	}

	/**
	 * 
	 * <code>SelectionListener</code> of Start Combo from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class StartComboSelectionListener implements SelectionListener {
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			motorAxisView.suspendModelUpdateListener();
			removeListeners();
			
			if( currentAxis != null ) {
				currentAxis.setStart(startCombo.getText());
				startCombo.setSelection(new Point(0,0));
				autoFill();
			}

			checkForErrors();
			addListeners();
			motorAxisView.resumeModelUpdateListener();
		}
	}
	
	/**
	 * <code>SelectionListener</code> of AutoFillStop RadioButton from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class AutoFillStopRadioButtonSelectionListener implements SelectionListener {
	 
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected( final SelectionEvent e ) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected( final SelectionEvent e ) {
			if (autoFillStopRadioButton.getSelection()) {
				stopCombo.setEnabled( false );
			}
			else
			   stopCombo.setEnabled( true );
		}
	}

	/**
	 * <code>ModifyListener</code> of Stop Combo from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class StopComboModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			motorAxisView.suspendModelUpdateListener();
			removeListeners();
			if( currentAxis != null ) {
				// if string is a double set value in model
				// else set null in model
				try {
					Double.parseDouble( stopCombo.getText() );
					currentAxis.setStop(stopCombo.getText());
					autoFill();	
				} catch( final NumberFormatException ex ) {
					// string is not a double
					currentAxis.setStop(null);
				}
			}
			checkForErrors();
			addListeners();
			motorAxisView.resumeModelUpdateListener();
		}
	}

	/**
	 * <code>SelectionListener</code> of Stop Combo from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class StopComboSelectionListener implements SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			motorAxisView.suspendModelUpdateListener();
			removeListeners();

			if( currentAxis != null ) {
				currentAxis.setStop(stopCombo.getText());
				stopCombo.setSelection(new Point(0,0));
				autoFill();
			}

			checkForErrors();
			addListeners();
			motorAxisView.resumeModelUpdateListener();
		}
	}

	/**
	 * <code>SelectionListener</code> of AutoFillStepwidth RadioButton from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class AutoFillStepwidthRadioButtonSelectionListener implements SelectionListener {
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected( final SelectionEvent e ) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected( final SelectionEvent e ) {
			if (autoFillStepwidthRadioButton.getSelection()) {
				stepwidthText.setEnabled( false );
			}
			else {
			   stepwidthText.setEnabled( true );
			}
		}
	}

	/**
	 * <code>ModifyListener</code> of Stepwidth Text from 
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class StepwidthTextModifyListener implements ModifyListener {
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText( final ModifyEvent e ) {
			motorAxisView.suspendModelUpdateListener();
			removeListeners();
			
			// if Text is empty, do nothing
			if( currentAxis != null ) {
				if( currentAxis.getMotorAxis().getGoto().isDiscrete() ) {
					try {
						Integer.parseInt( stepwidthText.getText() );
						currentAxis.setStepwidth( stepwidthText.getText() );
						autoFill();
					} catch( final NumberFormatException ex ) {
						// string is not an integer
						currentAxis.setStepwidth(null);
					}
				}
				else {
					try {
						Double.parseDouble( stepwidthText.getText() );
						currentAxis.setStepwidth(stepwidthText.getText());
						autoFill();	
					} catch( final NumberFormatException ex ) {
						// string is not a double
						currentAxis.setStepwidth(null);
					}
				}
			}
			checkForErrors();
			addListeners();
			motorAxisView.resumeModelUpdateListener();
		}
	}


	/**
	 * <code>SelectionListener</code> of stepcountRadioButton from 
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class AutoFillStepcountRadioButtonSelectionListener implements SelectionListener {
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected( final SelectionEvent e ) {
			if (autoFillStepcountRadioButton.getSelection()) {
				stepcountText.setEnabled( false );
			}
			else { 
				// nur wenn main axis erlaubt ist, darf auch stepcount wieder erlaubt werden
				if (mainAxisCheckBox.isEnabled()) {
					stepcountText.setEnabled( true );
					autoFillStepcountRadioButton.setEnabled( true );
				}
			}
		}
	}

	/**
	 * <code>ModifyListener</code> of <code>stepcountText</code>.
	 */
	class StepcountTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(final ModifyEvent e) {
			motorAxisView.suspendModelUpdateListener();
			removeListeners();
			
			if( currentAxis != null) {
				if(currentAxis.getMotorAxis().getGoto().isDiscrete()) {
					try {
						Integer.parseInt( stepcountText.getText() );
						currentAxis.setStepCount(Integer.parseInt(stepcountText.getText()));
						recalculateStepwidth();
						autoFill();
					} catch(final NumberFormatException ex) {
						// string is not an integer
						currentAxis.setStepCount(-1.0);
					}
				}
				else {
					try {
						Double.parseDouble( stepcountText.getText() );
						currentAxis.setStepCount(Double.parseDouble(stepcountText.getText()));
						recalculateStepwidth();
						autoFill();
					} catch( final NumberFormatException ex ) {
						// string is not a double
						currentAxis.setStepCount(-1.0);
					}
				}
			}
			checkForErrors();
			addListeners();
			motorAxisView.resumeModelUpdateListener();
		}
	}

	/**
	 * <code>SelectionListener</code> of <code>mainAxisCheckBox</code>.
	 */
	class MainAxisCheckBoxSelectionListener implements SelectionListener {
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(final SelectionEvent e) {
			motorAxisView.suspendModelUpdateListener();
			
			if(currentAxis != null) {
				currentAxis.setMainAxis(mainAxisCheckBox.getSelection());
			}
			
			motorAxisView.resumeModelUpdateListener();
		}
	}

	/**
	 * <code>VerifyListener</code> of Combo Widget from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class ComboVerifyListener implements VerifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void verifyText(VerifyEvent e) {

			switch (e.keyCode) {  
            	case SWT.BS:           // Backspace  
            	case SWT.DEL:          // Delete  
			    case SWT.HOME:         // Home  
			    case SWT.END:          // End  
			    case SWT.ARROW_LEFT:   // Left arrow  
			    case SWT.ARROW_RIGHT:  // Right arrow  
			    case 0:
			    	return;  
			}  

			String oldText = ((CCombo)(e.widget)).getText();
			
			if (!Character.isDigit(e.character)) {  
				if (e.character == '.') {
					// charecter . is a valid character, if he is not in the old string
					if (oldText.contains("."))
						e.doit = false;
				} 
				else if (e.character == '-') {
					// charecter - is a valid character as first sign and after an e
					if (oldText.isEmpty()) {
						// oldText is emtpy, - is valid
					}
					else {
						// wenn das letzte Zeichen von oldText ein e ist, ist das minus auch erlaubt
						int index = oldText.length();
						if (oldText.substring(index-1).equals("e")) {
							// letzte Zeichen ist ein e und damit erlaubt
						}
						else
							e.doit = false;
					}
				} 
				else if (e.character == 'e') {
					// charecter e is a valid character, if he is not in the old string
					if (oldText.contains("e"))
						e.doit = false;
				} 
				else {
					e.doit = false;  // disallow the action  
		        }
		    }  			
		}
	}

	/**
	 * <code>VerifyListener</code> of Text Widget from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class TextDoubleVerifyListener implements VerifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void verifyText(VerifyEvent e) {

			switch (e.keyCode) {  
            	case SWT.BS:           // Backspace  
            	case SWT.DEL:          // Delete  
			    case SWT.HOME:         // Home  
			    case SWT.END:          // End  
			    case SWT.ARROW_LEFT:   // Left arrow  
			    case SWT.ARROW_RIGHT:  // Right arrow  
			    	return;  
			}  

			String oldText = ((Text)(e.widget)).getText();

			if (!Character.isDigit(e.character)) {  
				if (e.character == '.') {
					// character . is a valid character, if he is not in the old string
					if (oldText.contains("."))
						e.doit = false;
				} 
				else if (e.character == '-') {
					// character - is a valid character as first sign and after an e
					if (oldText.isEmpty()) {
						// oldText is emtpy, - is valid
					}
					else {
						// wenn das letzte Zeichen von oldText ein e ist, ist das minus auch erlaubt
						int index = oldText.length();
						if (oldText.substring(index-1).equals("e")) {
							// letzte Zeichen ist ein e und damit erlaubt
						}
						else
							e.doit = false;
					}
				} 
				else if (e.character == 'e') {
					// character e is a valid character, if he is not in the old string
					if (oldText.contains("e"))
						e.doit = false;
				} 
				else {
					e.doit = false;  // disallow the action  
		        }
		    }  			
		}
	}


	/**
	 * <code>VerifyListener</code> of Text Widget from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class TextNumberVerifyListener implements VerifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void verifyText(VerifyEvent e) {

			switch (e.keyCode) {  
            	case SWT.BS:           // Backspace  
            	case SWT.DEL:          // Delete  
			    case SWT.HOME:         // Home  
			    case SWT.END:          // End  
			    case SWT.ARROW_LEFT:   // Left arrow  
			    case SWT.ARROW_RIGHT:  // Right arrow  
			    	return;  
			}  

			if (!Character.isDigit(e.character)) {  
					e.doit = false;  // disallow the action  
		    }  			
		}
	}

}