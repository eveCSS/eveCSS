/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class MotorAxisPluginComposite extends Composite implements IModelUpdateListener {

	private Label pluginLabel;
	private Combo pluginCombo;
	private Label pluginErrorLabel;
	private Label parameterLabel;
	private PluginControllerComposite pluginControllerComposite;
	private Axis axis;
	private ScanModul scanModul;
	
	public MotorAxisPluginComposite( final Composite parent, int style ) {
		super( parent, style );
		initialize();
		
	}

	private void initialize() {
		
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		this.setLayout( gridLayout );
		
		this.pluginLabel = new Label( this, SWT.NONE );
		
		this.pluginLabel.setText( "Plug-In:" );
		
		this.pluginCombo = new Combo( this, SWT.READ_ONLY );
		
		PlugIn[] plugins = Activator.getDefault().getMeasuringStation().getPlugins().toArray( new PlugIn[0] );
		List<String> pluginNames = new ArrayList<String>();
		for( int i = 0; i < plugins.length; ++i ) {
			if( plugins[i].getType() == PluginTypes.POSITION ) {
				pluginNames.add( plugins[i].getName() );
			}
		}
		this.pluginCombo.setItems( pluginNames.toArray( new String[0] ) );
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.pluginCombo.setLayoutData( gridData );
		this.pluginCombo.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				if( axis != null ) { 
					PlugIn plugin = Activator.getDefault().getMeasuringStation().getPluginByName( pluginCombo.getText() );
					if( axis.getPositionPluginController().getPlugin() != plugin ) {
						axis.getPositionPluginController().setPlugin( plugin );
						pluginControllerComposite.setPluginController( axis.getPositionPluginController() );
						// TODO 29.1.10: wird dieser Aufruf von setScanModul wirklich benötigt?
						pluginControllerComposite.setScanModul(scanModul);
					}
				}
			}
			
		});
		
		this.pluginErrorLabel = new Label(this, SWT.NONE );
		this.pluginErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		
		this.parameterLabel = new Label( this, SWT.NONE );
		this.parameterLabel.setText( "Parameter:" );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		this.parameterLabel.setLayoutData(  gridData );
		
		this.pluginControllerComposite = new PluginControllerComposite( this, SWT.None );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.pluginControllerComposite.setLayoutData( gridData );
		
		this.pluginCombo.setEnabled( false );
		this.pluginControllerComposite.setEnabled( false );
	}

	public void setAxis( final Axis axis, final ScanModul scanModul ) {
		if( this.axis != null ) {
			this.axis.removeModelUpdateListener( this );
		}
		this.axis = axis;
		this.scanModul = scanModul;
		if( this.axis != null ) {
			if( this.axis.getPositionPluginController().getModelErrors().size() > 0 ) {
				this.pluginErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
			} else {
				this.pluginErrorLabel.setImage( null );
			}
			if( this.axis.getPositionPluginController().getPlugin() != null ) {
				this.pluginCombo.setText( this.axis.getPositionPluginController().getPlugin().getName() );
			} else {
				this.pluginCombo.setText( "" );
			}
			this.pluginControllerComposite.setPluginController( axis.getPositionPluginController() );
			pluginControllerComposite.setScanModul(scanModul);
			
			this.pluginCombo.setEnabled( true );
			this.pluginControllerComposite.setEnabled( true );
			this.axis.addModelUpdateListener( this );
		} else {
			this.pluginCombo.setText( "" );
			this.pluginControllerComposite.setPluginController( null );
			this.pluginCombo.setEnabled( false );
			this.pluginControllerComposite.setEnabled( false );
		}
	}

	@Override
	public void dispose() {
		this.axis.removeModelUpdateListener( this );
		super.dispose();
	}
	@Override
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		if( this.axis.getPositionPluginController().getModelErrors().size() > 0 ) {
			this.pluginErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		} else {
			this.pluginErrorLabel.setImage( null );
		}
		
	}


	
}
