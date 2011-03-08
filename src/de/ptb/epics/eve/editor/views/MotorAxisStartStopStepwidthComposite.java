/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.Iterator;
import java.util.List;

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

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * <code>MotorAxisStartStopStedwidthComposite</code> is a composite to input Start, Stop,
 * Stepwidth and Stepcount of the motor axis.
 * @author Hartmut Scherr
 *
 */
public class MotorAxisStartStopStepwidthComposite extends Composite {

	private Combo startCombo;
	private Combo stopCombo;
	private Text stepwidthText;
	private Text stepamountText;
	private Axis axis;
	private Button mainAxisCheckBox;
	
	private Button autoFillStartRadioButton;
	private Button autoFillStopRadioButton;
	private Button autoFillStepwidthRadioButton;
	private Button autoFillStepamountRadioButton;
	
	private Label startErrorLabel;
	private Label stopErrorLabel;
	private Label stepwidthErrorLabel;
	private Label stepAmountErrorLabel;
	
//	private boolean recursion;
	
	public MotorAxisStartStopStepwidthComposite( final Composite parent, final int style ) {
		super( parent, style );
//		recursion = false;
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		this.setLayout( gridLayout );

		this.autoFillStartRadioButton = new Button( this, SWT.RADIO );
		this.autoFillStartRadioButton.setText( "Start:" );
		this.autoFillStartRadioButton.setToolTipText( "Mark to enable auto-fill for start value." );
		this.autoFillStartRadioButton.addSelectionListener( new AutoFillStartRadioButtonSelectionListener());

		this.startCombo = new Combo( this, SWT.NONE );
		this.startCombo.addFocusListener( new StartComboFocusListener());
		this.startCombo.addSelectionListener(new StartComboSelectionListener());
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.startCombo.setLayoutData( gridData );
		
		this.startErrorLabel = new Label( this, SWT.NONE );
		
		this.autoFillStopRadioButton = new Button( this, SWT.RADIO );
		this.autoFillStopRadioButton.setText( "Stop:" );
		this.autoFillStopRadioButton.setToolTipText( "Mark to enable auto-fill for stop value." );
		this.autoFillStopRadioButton.addSelectionListener( new AutoFillStopRadioButtonSelectionListener());

		this.stopCombo = new Combo( this, SWT.NONE );		
		this.stopCombo.addFocusListener( new StopComboFocusListener());
		this.stopCombo.addSelectionListener( new StopComboSelectionListener());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stopCombo.setLayoutData( gridData );

		this.stopErrorLabel = new Label( this, SWT.NONE );
		
		this.autoFillStepwidthRadioButton = new Button( this, SWT.RADIO );
		this.autoFillStepwidthRadioButton.setText( "Stepwidth:" );
		this.autoFillStepwidthRadioButton.setToolTipText( "Mark to enable auto-fill for stepwidth value." );
		this.autoFillStepwidthRadioButton.addSelectionListener( new AutoFillStepwidthRadioButtonSelectionListener());

		this.stepwidthText = new Text( this, SWT.BORDER );
		this.stepwidthText.addModifyListener( new StepwidthTextModifyListener());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stepwidthText.setLayoutData( gridData );
		
		this.stepwidthErrorLabel = new Label( this, SWT.NONE );
		
		this.autoFillStepamountRadioButton = new Button( this, SWT.RADIO );
		this.autoFillStepamountRadioButton.setText( "Stepcount:" );
		this.autoFillStepamountRadioButton.setToolTipText( "Mark to enable auto-fill for step count." );
		this.autoFillStepamountRadioButton.addSelectionListener( new AutoFillStepamountRadioButtonSelectionListener());

		this.stepamountText = new Text( this, SWT.BORDER );
		this.stepamountText.addModifyListener( new StepamountTextModifyListener());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		this.stepamountText.setLayoutData( gridData );
		
		this.mainAxisCheckBox = new Button( this, SWT.CHECK );
		this.mainAxisCheckBox.setText( "main axis" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		this.mainAxisCheckBox.setLayoutData( gridData );
		this.mainAxisCheckBox.addSelectionListener( new MainAxisCheckBoxSelectionListener());
		
		this.startCombo.setEnabled( false );
		this.stopCombo.setEnabled( false );
		this.stepwidthText.setEnabled( false );
		this.stepamountText.setEnabled( false );
		this.mainAxisCheckBox.setEnabled( false );
	}
	
    public void setAxis( final Axis axis, final double stepamount ) {

    	this.axis = axis;
		if( this.axis != null ) {
			if( this.axis.getMotorAxis().getGoto().isDiscrete() ) {
				this.startCombo.setItems( this.axis.getMotorAxis().getGoto().getDiscreteValues().toArray( new String[0] ) );
				this.stopCombo.setItems( this.axis.getMotorAxis().getGoto().getDiscreteValues().toArray( new String[0] ) );
			}
			this.startCombo.setText( this.axis.getStart()!=null?this.axis.getStart():"" );
			this.stopCombo.setText( this.axis.getStop()!=null?this.axis.getStop():"" );
			this.stepwidthText.setText( this.axis.getStepwidth()!=null?this.axis.getStepwidth():"" );

			if( stepamount != -1.0 && !axis.isMainAxis() ) {
				this.stepamountText.setText( "" + stepamount );
			} else {
				this.stepamountText.setText( "" + this.axis.getStepCount() );
			}

			this.mainAxisCheckBox.setSelection( this.axis.isMainAxis() );
			this.startCombo.setEnabled( true );
			this.stopCombo.setEnabled( true );
			this.stepwidthText.setEnabled( true );
			if( stepamount != -1.0 && !axis.isMainAxis() ) {
				this.stepamountText.setEnabled( false );
				this.autoFillStepamountRadioButton.setEnabled( false );
				// Stepwidth RadioButton wird voreingestellt 
				this.autoFillStepwidthRadioButton.setSelection( true );
				this.stepwidthText.setEnabled( false );
			} else {
				// Stepcount RadioButton wird voreingestellt 
				this.autoFillStepamountRadioButton.setSelection( true );
				this.autoFillStepamountRadioButton.setEnabled( false );
				this.stepamountText.setEnabled( false );
			}
			if( this.mainAxisCheckBox.getSelection() || stepamount == -1.0 ) {
				this.mainAxisCheckBox.setEnabled( true );
			}
			
			this.startErrorLabel.setImage( null );
			this.startErrorLabel.setToolTipText( "" );
			this.stopErrorLabel.setImage( null );
			this.stopErrorLabel.setToolTipText( "" );
			this.stepwidthErrorLabel.setImage( null );
			this.stepwidthErrorLabel.setToolTipText( "" );
			final Iterator< IModelError > it = this.axis.getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof AxisError ) {
					final AxisError axisError = (AxisError)modelError;
					switch( axisError.getErrorType() ) {
						case START_NOT_SET:
							this.startErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							this.startErrorLabel.setToolTipText( "Start values hat not been set!" );
							break;
							
						case START_VALUE_NOT_POSSIBLE:
							this.startErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							this.startErrorLabel.setToolTipText( "Start values not possible!" );
							break;
							
						case STOP_NOT_SET:
							this.stopErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							this.stopErrorLabel.setToolTipText( "Stop values hat not been set!" );
							break;
							
						case STOP_VALUE_NOT_POSSIBLE:
							this.stopErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							this.stopErrorLabel.setToolTipText( "Stop values not possible!" );
							break;
							
						case STEPWIDTH_NOT_SET:
							this.stepwidthErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							this.stepwidthErrorLabel.setToolTipText( "Stepwidth values hat not been set!" );
							break;
						
					}
				}
			}
			
		} else {
			this.startCombo.setEnabled( false );
			this.stopCombo.setEnabled( false );
			this.stepwidthText.setEnabled( false );
			this.stepamountText.setEnabled( false );
			this.mainAxisCheckBox.setEnabled( false );
			this.startErrorLabel.setImage( null );
			this.startErrorLabel.setToolTipText( "" );
			this.stopErrorLabel.setImage( null );
			this.stopErrorLabel.setToolTipText( "" );
			this.stepwidthErrorLabel.setImage( null );
			this.stepwidthErrorLabel.setToolTipText( "" );
		}
	}
	
	private void autoFill() {
		if( this.axis != null ) {
			if( this.axis.getStepfunctionString().equals( "Add" ) ) {
				if( this.autoFillStartRadioButton.getSelection() ) {
					if( !this.axis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								final double stop = Double.parseDouble( this.stopCombo.getText() );
								final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
								final double stepamount = Double.parseDouble( this.stepamountText.getText() );
								
								this.startCombo.setText( "" + (stop - (stepwidth * stepamount) ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					} else {
						if( !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								List< String > values = this.axis.getMotorAxis().getGoto().getDiscreteValues();
								
								final int stop = values.indexOf( this.stopCombo.getText() );
								final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );
								final int stepamount = Integer.parseInt( this.stepamountText.getText() );
								
								int index = ( stop - (stepwidth * stepamount) );
								if( index < 0 ) {
									index = 0;
								} else if( index >= values.size() ) {
									index = values.size() - 1;
								}
								
								this.startCombo.setText( values.get( index ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					}
				} else if( this.autoFillStopRadioButton.getSelection() ) {
					if( !this.axis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.startCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								final double start = Double.parseDouble( this.startCombo.getText() );
								final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
								final double stepamount = Double.parseDouble( this.stepamountText.getText() );

								this.stopCombo.setText( "" + (start + (stepwidth * stepamount) ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					} else {
						if( !this.startCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								List< String > values = this.axis.getMotorAxis().getGoto().getDiscreteValues();
								
								final int start = values.indexOf( this.startCombo.getText() );
								final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );
								final int stepamount = Integer.parseInt( this.stepamountText.getText() );
								
								int index = ( start + (stepwidth * stepamount) );
								if( index < 0 ) {
									index = 0;
								} else if( index >= values.size() ) {
									index = values.size() - 1;
								}
								
								this.stopCombo.setText( values.get( index ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					}
				} else if( this.autoFillStepwidthRadioButton.getSelection() ) {
					if( !this.axis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								final double start = Double.parseDouble( this.startCombo.getText() );
								final double stop = Double.parseDouble( this.stopCombo.getText() );
								final double stepamount = Double.parseDouble( this.stepamountText.getText() );
								
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
								
							}
						} 
					} else {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								List< String > values = this.axis.getMotorAxis().getGoto().getDiscreteValues();
								
								final int start = values.indexOf( this.startCombo.getText() );
								final int stop = values.indexOf( this.stopCombo.getText() );
								final int stepamount = Integer.parseInt( this.stepamountText.getText() );

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
								
							} catch( final NumberFormatException e ) {
								
							}
						}
					}
				} else if( this.autoFillStepamountRadioButton.getSelection() ) {
					if( !this.axis.getMotorAxis().getGoto().isDiscrete() ) {
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
								
								if ( !this.stepamountText.getText().equals("")) {
									// stepamount Eintrag schon vorhanden
									final double stepamount = Double.parseDouble( this.stepamountText.getText() );
									// Wenn Zähler oder Nenner gleich 0, besondere Behandlung
									if ((stop - start == 0) || (stepwidth == 0)) {
										if ( stepamount == 0) {
											// Wert wird nicht nochmal gesetzt
										}
										else {
											this.stepamountText.setText( "0" );
										}
									}
									else if ( stepamount == (( stop - start) / stepwidth )) {
										// Wert wird nicht nochmal gesetzt
									}
									else
										this.stepamountText.setText( "" + (( stop - start) / stepwidth ) );
								}
								else
									this.stepamountText.setText( "" + ( (stop - start) / stepwidth ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					} else {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) ) {
							try {
								List< String > values = this.axis.getMotorAxis().getGoto().getDiscreteValues();

								final int start = values.indexOf( this.startCombo.getText() );
								final int stop = values.indexOf( this.stopCombo.getText() );
								final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );

								if ( !this.stepamountText.getText().equals("")) {
									// stepamount Eintrag schon vorhanden
									final double stepamount_d = Double.parseDouble( this.stepamountText.getText() );
									final int stepamount = (int)stepamount_d;
									if ( stepamount == (( stop - start) / stepwidth )) {
										// Wert wird nicht nochmal gesetzt
									}
									else
										this.stepamountText.setText( "" + (( stop - start) / stepwidth ) );
								}
								else
									this.stepamountText.setText( "" + (( stop - start) / stepwidth ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					} 
				}
			} else if( this.axis.getStepfunctionString().equals( "Multiply" ) ) {
				
			}
		}
	}

	/**
	 * If stepcount of main axis changes, stepwidth of all other axis recalculated.
	 */
	private void recalculateStepwidth() {
		if (axis.isMainAxis()) {
			ScanModul scanModul = axis.getScanModul();
			Axis[] axis = scanModul.getAxis();
			
			for( int i = 0; i < axis.length; ++i ) {
				if( !axis[i].isMainAxis() ) {
					// Axis ist keine mainAxis und wird neu berechnet
					if( !axis[i].getMotorAxis().getGoto().isDiscrete() ) {
						// Achse i ist eine normale Achse
						final double start = Double.parseDouble(axis[i].getStart());
						final double stop = Double.parseDouble( axis[i].getStop() );
						final double stepwidth = Double.parseDouble( axis[i].getStepwidth() );
						final double stepamount = Double.parseDouble( stepamountText.getText() );

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
						final double stepamount_d = Double.parseDouble( stepamountText.getText() );
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
			// TODO Auto-generated method stub
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			if( axis != null ) {
				// TODO set application-wide warning background color
				String formattedText = axis.formatValue(startCombo.getText());
				if (formattedText == null){
					formattedText = axis.getDefaultValue();
					startCombo.setBackground(new Color(startCombo.getBackground().getDevice(),255, 255, 0));
				}
				else {
					startCombo.setBackground(new Color(startCombo.getBackground().getDevice(),255, 255, 255));
				}
				startCombo.setText(formattedText);
				axis.setStart(formattedText);
				autoFill();
			}
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
			if( axis != null ) {
				// TODO set application-wide warning background color
				String formattedText = axis.formatValue(startCombo.getText());
				if (formattedText == null){
					formattedText = axis.getDefaultValue();
					startCombo.setBackground(new Color(startCombo.getBackground().getDevice(),255, 255, 0));
				}
				else {
					startCombo.setBackground(new Color(startCombo.getBackground().getDevice(),255, 255, 255));
				}
				startCombo.setText(formattedText);
				axis.setStart(formattedText);
				autoFill();
			}
		};
	};
	
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
			if( axis != null ) {
				// TODO set application-wide warning background color
				String formattedText = axis.formatValue(stopCombo.getText());
				if (formattedText == null){
					formattedText = axis.getDefaultValue();
					stopCombo.setBackground(new Color(stopCombo.getBackground().getDevice(),255, 255, 0));
				}
				else {
					stopCombo.setBackground(new Color(stopCombo.getBackground().getDevice(),255, 255, 255));
				}
				stopCombo.setText(formattedText);
				axis.setStop(formattedText);
				autoFill();
			}
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
			// TODO Auto-generated method stub
			
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			if( axis != null ) {
				// TODO set application-wide warning background color
				String formattedText = axis.formatValue(stopCombo.getText());
				if (formattedText == null){
					formattedText = axis.getDefaultValue();
					stopCombo.setBackground(new Color(stopCombo.getBackground().getDevice(),255, 255, 0));
				}
				else {
					stopCombo.setBackground(new Color(stopCombo.getBackground().getDevice(),255, 255, 255));
				}
				stopCombo.setText(formattedText);
				axis.setStop(formattedText);
				autoFill();
			}
		};
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
			if( axis != null ) {
				if( axis.getMotorAxis().getGoto().isDiscrete() ) {
					try {
						// Es wird versucht ein Integer auszulesen, wenn das nicht klappt, wird
						// der Wert in der Oberfläche zurückgesetzt, weiter passiert dann nichts.
						final int stepwidth = Integer.parseInt( stepwidthText.getText() );
						axis.setStepwidth( stepwidthText.getText() );
						autoFill();
					} catch( final NumberFormatException ex ) {
						final double stepwidth_d = Double.parseDouble( stepwidthText.getText() );
						final int stepwidth = (int)stepwidth_d;
						stepwidthText.setText("" + stepwidth);
						stepwidthText.setSelection(stepwidthText.getCharCount());
					}
				}
				else {
					axis.setStepwidth( stepwidthText.getText() );
					autoFill();
				}
			}
		}
	}

	/**
	 * <code>SelectionListener</code> of StepamountRadioButton from 
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class AutoFillStepamountRadioButtonSelectionListener implements SelectionListener {
	
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
			if (autoFillStepamountRadioButton.getSelection()) {
				stepamountText.setEnabled( false );
			}
			else { 
				// nur wenn main axis erlaubt ist, darf auch stepamount wieder erlaubt werden
				if (mainAxisCheckBox.isEnabled()) {
					stepamountText.setEnabled( true );
					autoFillStepamountRadioButton.setEnabled( true );
				}
			}
		}
	}

	/**
	 * <code>ModifyListener</code> of Stepamount Text from 
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class StepamountTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText( final ModifyEvent e ) {
			if( axis != null ) {
				if( axis.getMotorAxis().getGoto().isDiscrete() ) {
					try {
						axis.setStepCount(Integer.parseInt(stepamountText.getText()));
						recalculateStepwidth();
						autoFill();
					} catch( final NumberFormatException ex ) {
						final double stepamount_d = Double.parseDouble( stepamountText.getText() );
						final int stepamount = (int)stepamount_d;
						stepamountText.setText("" + stepamount);
						stepamountText.setSelection(stepamountText.getCharCount());
					}
				}
				else {
					axis.setStepCount(Double.parseDouble(stepamountText.getText()));
					recalculateStepwidth();
					autoFill();
				}
			}
		}
	}

	/**
	 * <code>SelectionListener</code> of MainAxis CheckBox from 
	 * <code>MotorAxisStartStopStepwidthComposite</code>
	 */
	class MainAxisCheckBoxSelectionListener implements SelectionListener {
	
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
			if( axis != null ) {
				axis.setMainAxis( mainAxisCheckBox.getSelection() );
			}
		}
	}
	
}
