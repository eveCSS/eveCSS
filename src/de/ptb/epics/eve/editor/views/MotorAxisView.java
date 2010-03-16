/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.editor.Activator;

public class MotorAxisView extends ViewPart {

	public static final String ID = "de.ptb.epics.eve.editor.views.MotorAxisView"; // TODO Needs to be whatever is mentioned in plugin.xml  //  @jve:decl-index=0:

	private Composite top = null;

	private Label stepfunctionLabel;
	private Combo stepFunctionCombo;
	private Label stepfunctionErrorLabel;
	
	private Label positionModeLabel;
	private Combo positionModeCombo;
	private Label positionModeErrorLabel;
	
	private Composite holderComposite;
	
	private Axis axis;
	private double stepamount;
	private String[] stepfunctions;
	
	private ScanModul scanModul;

	@Override
	public void createPartControl( final Composite parent ) {
		
		parent.setLayout(new FillLayout());
		
		if( Activator.getDefault().getMeasuringStation() == null ) {
			final Label errorLabel = new Label( parent, SWT.NONE );
			errorLabel.setText( "No Measuring Station has been loaded. Please check Preferences!" );
			return;
		}
		this.stepfunctions = Activator.getDefault().getMeasuringStation().getSelections().getStepfunctions();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		GridData gridData;
		
		this.top = new Composite( parent, SWT.NONE );
		this.top.setLayout( gridLayout );
		
		this.stepfunctionLabel = new Label( this.top, SWT.NONE );
		this.stepfunctionLabel.setText( "Step function: " );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.stepfunctionLabel.setLayoutData( gridData );
		
		this.stepFunctionCombo = new Combo( this.top, SWT.READ_ONLY );
		this.stepFunctionCombo.setItems( this.stepfunctions );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stepFunctionCombo.setLayoutData( gridData );
		this.stepFunctionCombo.addModifyListener( new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if( axis != null ) {
					axis.setStepfunction( stepFunctionCombo.getText() );
					if( axis.getStepfunctionString().equals( "File" ) ) {
						holderComposite.dispose();
						holderComposite = new MotorAxisFileComposite( top, SWT.NONE );
						((MotorAxisFileComposite)holderComposite).setAxis( axis );
						GridData gridData = new GridData();
						gridData.horizontalAlignment = GridData.FILL;
						gridData.verticalAlignment = GridData.FILL;
						gridData.grabExcessHorizontalSpace = true;
						gridData.grabExcessVerticalSpace = true;
						gridData.horizontalSpan = 3;
						holderComposite.setLayoutData( gridData );
						holderComposite.layout();
						holderComposite.setVisible( true );
						top.layout();
					} else if( axis.getStepfunctionString().equals( "Plugin" ) ) { 
						if( axis.getPositionPluginController() == null ) {
							axis.setPositionPluginController( new PluginController() );
						}
						holderComposite.dispose();
						holderComposite = new MotorAxisPluginComposite( top, SWT.NONE );
						((MotorAxisPluginComposite)holderComposite).setAxis( axis, scanModul );
						GridData gridData = new GridData();
						gridData.horizontalAlignment = GridData.FILL;
						gridData.verticalAlignment = GridData.FILL;
						gridData.grabExcessHorizontalSpace = true;
						gridData.grabExcessVerticalSpace = true;
						gridData.horizontalSpan = 3;
						holderComposite.setLayoutData( gridData );
						holderComposite.layout();
						holderComposite.setVisible( true );
						top.layout();
					} else if( axis.getStepfunctionString().equals( "Positionlist" ) ) {
						holderComposite.dispose();
						holderComposite = new MotorAxisPositionlistComposite( top, SWT.NONE );
						((MotorAxisPositionlistComposite)holderComposite).setAxis( axis );
						GridData gridData = new GridData();
						gridData.horizontalAlignment = GridData.FILL;
						gridData.verticalAlignment = GridData.FILL;
						gridData.grabExcessHorizontalSpace = true;
						gridData.grabExcessVerticalSpace = true;
						gridData.horizontalSpan = 3;
						holderComposite.setLayoutData( gridData );
						holderComposite.layout();
						holderComposite.setVisible( true );
						top.layout();
					} else {
						holderComposite.dispose();
						holderComposite = new MotorAxisStartStopStepwidthComposite( top, SWT.NONE );
						((MotorAxisStartStopStepwidthComposite)holderComposite).setAxis( axis, stepamount );
						GridData gridData = new GridData();
						gridData.horizontalAlignment = GridData.FILL;
						gridData.verticalAlignment = GridData.FILL;
						gridData.grabExcessHorizontalSpace = true;
						gridData.grabExcessVerticalSpace = true;
						gridData.horizontalSpan = 3;
						holderComposite.setLayoutData( gridData );
						holderComposite.layout();
						holderComposite.setVisible( true );
						top.layout();
					}
				}
			}
			
		});
		
		this.stepfunctionErrorLabel = new Label( this.top, SWT.NONE );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.stepfunctionErrorLabel.setLayoutData( gridData );
		
		this.positionModeLabel = new Label( this.top, SWT.NONE );
		this.positionModeLabel.setText( "Position mode: " );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.positionModeLabel.setLayoutData( gridData );
		
		this.positionModeCombo = new Combo( this.top, SWT.READ_ONLY );
		this.positionModeCombo.setItems( PositionMode.getPossiblePositionModes() );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.positionModeCombo.setLayoutData( gridData );
		
		this.positionModeErrorLabel = new Label( this.top, SWT.NONE );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.positionModeErrorLabel.setLayoutData( gridData );
		
		this.holderComposite = new Composite( top, SWT.NONE );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		this.holderComposite.setLayoutData( gridData );
		
		this.stepFunctionCombo.setEnabled( false );
		this.positionModeCombo.setEnabled( false );
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void setAxis( final Axis axis, final double stepamount ) {
		this.axis = axis;
		this.stepamount = stepamount;
		if( this.axis != null ) {
			this.setPartName( this.axis.getMotorAxis().getFullIdentifyer() );
			this.stepFunctionCombo.setText( this.axis.getStepfunctionString() );
			this.positionModeCombo.setText( PositionMode.typeToString( this.axis.getPositionMode() ) );
			
			this.stepFunctionCombo.setEnabled( true );
			this.positionModeCombo.setEnabled( true );
		} else {
			this.setPartName( "Motor Axis View" );
			this.holderComposite.dispose();
			
			this.holderComposite = new Composite( top, SWT.NONE );
			GridData gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			gridData.horizontalSpan = 2;
			this.holderComposite.setLayoutData( gridData );
			this.stepFunctionCombo.setText( "" );
			this.positionModeCombo.setText( "" );
			this.stepFunctionCombo.setEnabled( false );
			this.positionModeCombo.setEnabled( false );
		}
	}

	public void setAxis( final Axis axis, final double stepamount, final ScanModul scanModul ) {
		this.scanModul = scanModul;
		setAxis(axis, stepamount);
	}
	
}  //  @jve:decl-index=0:visual-constraint="-17,-49,363,149"
