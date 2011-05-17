package de.ptb.epics.eve.editor.views.motoraxisview;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
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
	
	private Combo startCombo;
	private StartComboFocusListener startComboFocusListener;
	private StartComboSelectionListener startComboSelectionListener;
	private Label startErrorLabel;
	// end of: start elements
	
	// stop elements
	private Button autoFillStopRadioButton;
	private AutoFillStopRadioButtonSelectionListener
			autoFillStopRadioButtonSelectionListener;
	
	private Combo stopCombo;
	private StopComboFocusListener stopComboFocusListener;
	private StopComboSelectionListener stopComboSelectionListener;
	private Label stopErrorLabel;
	// end of: stop elements
	
	// step width elements
	private Button autoFillStepwidthRadioButton;
	private AutoFillStepwidthRadioButtonSelectionListener
			autoFillStepwidthRadioButtonSelectionListener;
	
	private Text stepwidthText;
	private StepwidthTextModifyListener stepwidthTextModifyListener;
	private Label stepwidthErrorLabel;
	// end of: step width elements
	
	// step amount elements
	private Button autoFillStepcountRadioButton;
	private AutoFillStepcountRadioButtonSelectionListener
			autoFillStepcountRadioButtonSelectionListener;
	
	private Text stepcountText;
	private StepcountTextModifyListener stepcountTextModifyListener;
	// end of step amount elements
	
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

		this.startCombo = new Combo(this, SWT.NONE);
		this.startComboFocusListener = new StartComboFocusListener();
		this.startCombo.addFocusListener(startComboFocusListener);
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

		this.stopCombo = new Combo(this, SWT.NONE);
		this.stopComboFocusListener = new StopComboFocusListener();
		this.stopCombo.addFocusListener(stopComboFocusListener);
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
		this.stepwidthTextModifyListener = new StepwidthTextModifyListener();
		this.stepwidthText.addModifyListener(stepwidthTextModifyListener);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stepwidthText.setLayoutData(gridData);
		
		this.stepwidthErrorLabel = new Label(this, SWT.NONE);
		// end of: initialize step width elements 
		
		// initialize step amount elements
		this.autoFillStepcountRadioButton = new Button(this, SWT.RADIO);
		this.autoFillStepcountRadioButton.setText("Stepcount:");
		this.autoFillStepcountRadioButton.setToolTipText(
				"Mark to enable auto-fill for step count.");
		this.autoFillStepcountRadioButtonSelectionListener = 
				new AutoFillStepcountRadioButtonSelectionListener();
		this.autoFillStepcountRadioButton.addSelectionListener(
				autoFillStepcountRadioButtonSelectionListener);

		this.stepcountText = new Text(this, SWT.BORDER);
		this.stepcountTextModifyListener = new StepcountTextModifyListener();
		this.stepcountText.addModifyListener(stepcountTextModifyListener);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		this.stepcountText.setLayoutData(gridData);
		// end of: initialize step amount elements
		
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
				this.stopCombo.setItems(this.currentAxis.getMotorAxis().
										getGoto().getDiscreteValues().
										toArray(new String[0]));
			}
			this.startCombo.setText(this.currentAxis.getStart() != null
								    ? this.currentAxis.getStart()
								    : "");
			this.stopCombo.setText(this.currentAxis.getStop() != null
								   ? this.currentAxis.getStop()
								   : "");
			this.stepwidthText.setText(this.currentAxis.getStepwidth() != null
									   ? this.currentAxis.getStepwidth()
									   : "");

			if(stepcount != -1.0 && !axis.isMainAxis()) {
				this.stepcountText.setText(Double.toString(stepcount));
			} else {
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
						break;
						
					case START_VALUE_NOT_POSSIBLE:
						this.startErrorLabel.setImage(PlatformUI.getWorkbench().
											getSharedImages().getImage(
											ISharedImages.IMG_OBJS_ERROR_TSK));
						this.startErrorLabel.setToolTipText(
								"Start values not possible!");
						break;
						
					case STOP_NOT_SET:
						this.stopErrorLabel.setImage(PlatformUI.getWorkbench().
											getSharedImages().getImage(
											ISharedImages.IMG_OBJS_ERROR_TSK));
						this.stopErrorLabel.setToolTipText(
								"Stop values hat not been set!");
						break;
						
					case STOP_VALUE_NOT_POSSIBLE:
						this.stopErrorLabel.setImage(PlatformUI.getWorkbench().
											getSharedImages().getImage(
											ISharedImages.IMG_OBJS_ERROR_TSK));
						this.stopErrorLabel.setToolTipText(
								"Stop values not possible!");
						break;
						
					case STEPWIDTH_NOT_SET:
						this.stepwidthErrorLabel.setImage(PlatformUI.
									getWorkbench().getSharedImages().
									getImage( ISharedImages.IMG_OBJS_ERROR_TSK));
						this.stepwidthErrorLabel.setToolTipText( 
								"Stepwidth values hat not been set!");
						break;
				}
			}
		}
    }
    
    /*
     * 
     */
	private void autoFill() {
		if( this.currentAxis != null ) {
			if( this.currentAxis.getStepfunctionString().equals( "Add" ) ) {
				if( this.autoFillStartRadioButton.getSelection() ) {
					if( !this.currentAxis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepcountText.getText().equals( "" ) ) {
							try {
								final double stop = Double.parseDouble( this.stopCombo.getText() );
								final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
								final double stepamount = Double.parseDouble( this.stepcountText.getText() );
								
								this.startCombo.setText( "" + (stop - (stepwidth * stepamount) ) );
							} catch( final NumberFormatException e ) {
								logger.error(e.getMessage(),e);
							}
						}
					} else {
						if( !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepcountText.getText().equals( "" ) ) {
							try {
								List< String > values = this.currentAxis.getMotorAxis().getGoto().getDiscreteValues();
								
								final int stop = values.indexOf( this.stopCombo.getText() );
								final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );
								final int stepamount = Integer.parseInt( this.stepcountText.getText() );
								
								int index = ( stop - (stepwidth * stepamount) );
								if( index < 0 ) {
									index = 0;
								} else if( index >= values.size() ) {
									index = values.size() - 1;
								}
								
								this.startCombo.setText( values.get( index ) );
							} catch( final NumberFormatException e ) {
								logger.error(e.getMessage(),e);
							}
						}
					}
				} else if( this.autoFillStopRadioButton.getSelection() ) {
					if( !this.currentAxis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.startCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepcountText.getText().equals( "" ) ) {
							try {
								final double start = Double.parseDouble( this.startCombo.getText() );
								final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
								final double stepamount = Double.parseDouble( this.stepcountText.getText() );

								this.stopCombo.setText( "" + (start + (stepwidth * stepamount) ) );
							} catch( final NumberFormatException e ) {
								logger.error(e.getMessage(),e);
							}
						}
					} else {
						if( !this.startCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepcountText.getText().equals( "" ) ) {
							try {
								List< String > values = this.currentAxis.getMotorAxis().getGoto().getDiscreteValues();
								
								final int start = values.indexOf( this.startCombo.getText() );
								final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );
								final int stepamount = Integer.parseInt( this.stepcountText.getText() );
								
								int index = ( start + (stepwidth * stepamount) );
								if( index < 0 ) {
									index = 0;
								} else if( index >= values.size() ) {
									index = values.size() - 1;
								}
								
								this.stopCombo.setText( values.get( index ) );
							} catch( final NumberFormatException e ) {
								logger.error(e.getMessage(),e);
							}
						}
					}
				} else if( this.autoFillStepwidthRadioButton.getSelection() ) {
					if( !this.currentAxis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepcountText.getText().equals( "" ) ) {
							try {
								final double start = Double.parseDouble( this.startCombo.getText() );
								final double stop = Double.parseDouble( this.stopCombo.getText() );
								final double stepamount = Double.parseDouble( this.stepcountText.getText() );
								
								if ( !this.stepwidthText.getText().equals("")) {
									// stepwidth Eintrag schon vorhanden
									final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
									// Wenn Zähler oder Nenner gleich 0, besondere Behandlung
									if ((stop - start == 0) || (stepamount == 0)) {
										if ( stepwidth == 0) {
											// Wert wird nicht nochmal gesetzt
										}
										else {
											this.stepwidthText.setText( "0" );
										}
									}
									else if ( stepwidth == (( stop - start) / stepamount )) {
										// Wert wird nicht nochmal gesetzt
									}
									else {
											this.stepwidthText.setText( "" + (( stop - start) / stepamount ) );
									}
								}
								else
									this.stepwidthText.setText( "" + ( (stop - start ) / stepamount ) );
							} catch( final NumberFormatException e ) {
								logger.error(e.getMessage(),e);
							}
						} 
					} else {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepcountText.getText().equals( "" ) ) {
							try {
								List< String > values = this.currentAxis.getMotorAxis().getGoto().getDiscreteValues();
								
								final int start = values.indexOf( this.startCombo.getText() );
								final int stop = values.indexOf( this.stopCombo.getText() );
								final int stepamount = Integer.parseInt( this.stepcountText.getText() );

								if (stepamount != 0) {
								if ( !this.stepwidthText.getText().equals("")) {
									// stepwidth Eintrag schon vorhanden
									final double stepwidth_d = Double.parseDouble( this.stepwidthText.getText() );
									final int stepwidth = (int)stepwidth_d;
									if ( stepwidth == (( stop - start) / stepamount )) {
										// Wert wird nicht nochmal gesetzt
									}
									else
										this.stepwidthText.setText( "" + (( stop - start) / stepamount ) );
								}
								else
									this.stepwidthText.setText( "" + ( (stop - start ) / stepamount ) );

								}
							} catch( final NumberFormatException e ) {
								logger.error(e.getMessage(),e);
							}
						}
					}
				} else if( this.autoFillStepcountRadioButton.getSelection() ) {
					if( !this.currentAxis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) ) {
							try {
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
									}
								}
								if ((start - stop) < 0) {
									// stepwidth muß positiv sein!
									if (stepwidth < 0) {
										// Vorzeichen von Stepwidth umdrehen!
										stepwidth = stepwidth * -1;
										this.stepwidthText.setText( "" + (int)stepwidth );
										this.stepwidthText.setSelection(1);
									}
								}
								
								if ( !this.stepcountText.getText().equals("")) {
									// stepamount Eintrag schon vorhanden
									final double stepamount = Double.parseDouble( this.stepcountText.getText() );
									// Wenn Zähler oder Nenner gleich 0, besondere Behandlung
									if ((stop - start == 0) || (stepwidth == 0)) {
										if ( stepamount == 0) {
											// Wert wird nicht nochmal gesetzt
										}
										else {
											this.stepcountText.setText( "0" );
										}
									}
									else if ( stepamount == (( stop - start) / stepwidth )) {
										// Wert wird nicht nochmal gesetzt
									}
									else
										this.stepcountText.setText( "" + (( stop - start) / stepwidth ) );
								}
								else
									this.stepcountText.setText( "" + ( (stop - start) / stepwidth ) );
							} catch( final NumberFormatException e ) {
								logger.error(e.getMessage(),e);
							}
						}
					} else {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) ) {
							try {
								List< String > values = this.currentAxis.getMotorAxis().getGoto().getDiscreteValues();

								final int start = values.indexOf( this.startCombo.getText() );
								final int stop = values.indexOf( this.stopCombo.getText() );
								final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );

								if (stepwidth != 0) {
									if ( !this.stepcountText.getText().equals("") ) {
										// stepamount Eintrag schon vorhanden
										final double stepamount_d = Double.parseDouble( this.stepcountText.getText() );
										final int stepamount = (int)stepamount_d;
										if ( stepamount == (( stop - start) / stepwidth )) {
											// Wert wird nicht nochmal gesetzt
										}
										else
											this.stepcountText.setText( "" + (( stop - start) / stepwidth ) );
									}
									else
										this.stepcountText.setText( "" + (( stop - start) / stepwidth ) );
								}
							} catch( final NumberFormatException e ) {
								logger.error(e.getMessage(),e);
							}
						}
					} 
				}
			} else if( this.currentAxis.getStepfunctionString().equals( "Multiply" ) ) {
				
			}
		}
	}

	/**
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
						final double stepamount = Double.parseDouble( stepcountText.getText() );

						// Wenn Zähler oder Nenner gleich 0, besondere Behandlung
						if ((stop - start == 0) || (stepamount == 0)) {
							if ( stepwidth == 0) {
								// Wert wird nicht nochmal gesetzt
							}
							else {
								axis[i].setStepwidth( "0" );
							}
						}
						else if ( stepwidth == (( stop - start) / stepamount )) {
							// Wert wird nicht nochmal gesetzt
						}
						else {
							axis[i].setStepwidth("" + (( stop - start) / stepamount ) );
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
						final double stepamount_d = Double.parseDouble( stepcountText.getText() );
						final int stepamount = (int)stepamount_d;

						// Wenn Zähler oder Nenner gleich 0, besondere Behandlung
						if ((stop - start == 0) || (stepamount == 0)) {
							if ( stepwidth == 0) {
								// Wert wird nicht nochmal gesetzt
							}
							else {
								axis[i].setStepwidth( "0" );
							}
						}
						else if ( stepwidth == (( stop - start) / stepamount )) {
							// Wert wird nicht nochmal gesetzt
						}
						else {
							axis[i].setStepwidth("" + (( stop - start) / stepamount ) );
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
		startCombo.addFocusListener(startComboFocusListener);
		startCombo.addSelectionListener(startComboSelectionListener);
		
		autoFillStopRadioButton.addSelectionListener(
				autoFillStopRadioButtonSelectionListener);
		stopCombo.addFocusListener(stopComboFocusListener);
		stopCombo.addSelectionListener(stopComboSelectionListener);
		
		autoFillStepwidthRadioButton.addSelectionListener(
				autoFillStepwidthRadioButtonSelectionListener);
		stepwidthText.addModifyListener(stepwidthTextModifyListener);
		
		autoFillStepcountRadioButton.addSelectionListener(
				autoFillStepcountRadioButtonSelectionListener);
		stepcountText.addModifyListener(stepcountTextModifyListener);
		
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
		startCombo.removeFocusListener(startComboFocusListener);
		startCombo.removeSelectionListener(startComboSelectionListener);
		
		autoFillStopRadioButton.removeSelectionListener(
				autoFillStopRadioButtonSelectionListener);
		stopCombo.removeFocusListener(stopComboFocusListener);
		stopCombo.removeSelectionListener(stopComboSelectionListener);
		
		autoFillStepwidthRadioButton.removeSelectionListener(
				autoFillStepwidthRadioButtonSelectionListener);
		stepwidthText.removeModifyListener(stepwidthTextModifyListener);
		
		autoFillStepcountRadioButton.removeSelectionListener(
				autoFillStepcountRadioButtonSelectionListener);
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
	 * <code>FocusListener</code> of Start Combo from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class StartComboFocusListener implements FocusListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
			motorAxisView.suspendModelUpdateListener();
			
			// TODO Auto-generated method stub
			if( currentAxis != null ) {
				// TODO set application-wide warning background color
				String formattedText = currentAxis.formatValue(startCombo.getText());
				if (formattedText == null){
					formattedText = currentAxis.getDefaultValue();
					startCombo.setBackground(new Color(startCombo.getBackground().getDevice(),255, 255, 0));
				}
				else {
					startCombo.setBackground(new Color(startCombo.getBackground().getDevice(),255, 255, 255));
				}
				startCombo.setText(formattedText);
				currentAxis.setStart(formattedText);
				autoFill();	
			}
			motorAxisView.resumeModelUpdateListener();
		}
	}

	/**
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
			
			if( currentAxis != null ) {
				// TODO set application-wide warning background color
				String formattedText = currentAxis.formatValue(startCombo.getText());
				if (formattedText == null){
					formattedText = currentAxis.getDefaultValue();
					startCombo.setBackground(new Color(startCombo.getBackground().getDevice(),255, 255, 0));
				}
				else {
					startCombo.setBackground(new Color(startCombo.getBackground().getDevice(),255, 255, 255));
				}
				startCombo.setText(formattedText);
				currentAxis.setStart(formattedText);
				autoFill();
			}
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
	 * <code>FocusListener</code> of Stop Combo from
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class StopComboFocusListener implements FocusListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
			motorAxisView.suspendModelUpdateListener();
			
			if( currentAxis != null ) {
				// TODO set application-wide warning background color
				String formattedText = currentAxis.formatValue(stopCombo.getText());
				if (formattedText == null){
					formattedText = currentAxis.getDefaultValue();
					stopCombo.setBackground(new Color(stopCombo.getBackground().getDevice(),255, 255, 0));
				}
				else {
					stopCombo.setBackground(new Color(stopCombo.getBackground().getDevice(),255, 255, 255));
				}
				stopCombo.setText(formattedText);
				currentAxis.setStop(formattedText);
				autoFill();
			}
			
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
			
			if( currentAxis != null ) {
				// TODO set application-wide warning background color
				String formattedText = currentAxis.formatValue(stopCombo.getText());
				if (formattedText == null){
					formattedText = currentAxis.getDefaultValue();
					stopCombo.setBackground(new Color(stopCombo.getBackground().getDevice(),255, 255, 0));
				}
				else {
					stopCombo.setBackground(new Color(stopCombo.getBackground().getDevice(),255, 255, 255));
				}
				stopCombo.setText(formattedText);
				currentAxis.setStop(formattedText);
				autoFill();
			}
			
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
			
			// if Text is empty, do nothing
			if( currentAxis != null  && !stepwidthText.getText().equals("")) {
				if( currentAxis.getMotorAxis().getGoto().isDiscrete() ) {
					try {
						final int stepwidth = Integer.parseInt( stepwidthText.getText() );
						currentAxis.setStepwidth( stepwidthText.getText() );
						autoFill();
					} catch( final NumberFormatException ex ) {
						// unerlaubtes Zeichen eingegeben
						// Das Zeichen wird von der Eingabe wieder entfernt!
						int index = stepwidthText.getText().length() -1;
						stepwidthText.removeModifyListener(stepwidthTextModifyListener);
						stepwidthText.setText(stepwidthText.getText().substring(0, index));
						stepwidthText.setSelection(stepwidthText.getCharCount());
						stepwidthText.addModifyListener(stepwidthTextModifyListener);
					}
				}
				else {
					try {
						final double test = Double.parseDouble(stepwidthText.getText());
						// if last char is a f or d, cut off this char
						if (stepwidthText.getText().endsWith("d")) {
							// unerlaubtes Zeichen eingegeben
							// Das Zeichen wird von der Eingabe wieder entfernt!
							int index = stepwidthText.getText().length() -1;
							stepwidthText.removeModifyListener(stepwidthTextModifyListener);
							stepwidthText.setText(stepwidthText.getText().substring(0, index));
							stepwidthText.setSelection(stepwidthText.getCharCount());
							stepwidthText.addModifyListener(stepwidthTextModifyListener);
						}
						if (stepwidthText.getText().endsWith("f")) {
							// unerlaubtes Zeichen eingegeben
							// Das Zeichen wird von der Eingabe wieder entfernt!
							int index = stepwidthText.getText().length() -1;
							stepwidthText.removeModifyListener(stepwidthTextModifyListener);
							stepwidthText.setText(stepwidthText.getText().substring(0, index));
							stepwidthText.setSelection(stepwidthText.getCharCount());
							stepwidthText.addModifyListener(stepwidthTextModifyListener);
						}
						currentAxis.setStepwidth(stepwidthText.getText());
						autoFill();
					} catch( final NumberFormatException ex ) {
						// unerlaubtes Zeichen eingegeben
						// Das Zeichen wird von der Eingabe wieder entfernt!
						int index = stepwidthText.getText().length() -1;
						stepwidthText.removeModifyListener(stepwidthTextModifyListener);
						stepwidthText.setText(stepwidthText.getText().substring(0, index));
						stepwidthText.setSelection(stepwidthText.getCharCount());
						stepwidthText.addModifyListener(stepwidthTextModifyListener);
					}
				}
			}
			motorAxisView.resumeModelUpdateListener();
		}
	}

	/**
	 * <code>SelectionListener</code> of StepamountRadioButton from 
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
				// nur wenn main axis erlaubt ist, darf auch stepamount wieder erlaubt werden
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
			
			if( currentAxis != null  && !stepcountText.getText().equals("")) {
				if(currentAxis.getMotorAxis().getGoto().isDiscrete()) {
					try {
						currentAxis.setStepCount(Integer.parseInt(stepcountText.getText()));
						recalculateStepwidth();
						autoFill();
					} catch(final NumberFormatException ex) {
						// unerlaubtes Zeichen eingegeben
						// Das Zeichen wird von der Eingabe wieder entfernt!
						int index = stepcountText.getText().length() -1;
						stepcountText.removeModifyListener(stepcountTextModifyListener);
						stepcountText.setText(stepcountText.getText().substring(0, index));
						stepcountText.setSelection(stepcountText.getCharCount());
						stepcountText.addModifyListener(stepcountTextModifyListener);
					}
				}
				else {
					try {
						currentAxis.setStepCount(Double.parseDouble(stepcountText.getText()));
						recalculateStepwidth();
						autoFill();
					} catch( final NumberFormatException ex ) {
						// unerlaubtes Zeichen eingegeben
						// Das Zeichen wird von der Eingabe wieder entfernt!
						int index = stepcountText.getText().length() -1;
						stepcountText.removeModifyListener(stepcountTextModifyListener);
						stepcountText.setText(stepcountText.getText().substring(0, index));
						stepcountText.setSelection(stepcountText.getCharCount());
						stepcountText.addModifyListener(stepcountTextModifyListener);
					}
				}
			}
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
}