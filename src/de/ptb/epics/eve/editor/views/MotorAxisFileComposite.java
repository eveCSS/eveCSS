/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.Iterator;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

public class MotorAxisFileComposite extends Composite {

	private Label filenameLabel = null;
	private Text filenameText = null;
	private Label filenameErrorLabel = null;
	private Button searchButton = null;
	private Axis axis = null;
	
	public MotorAxisFileComposite(Composite parent, int style ) {
		super( parent, style );
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.setLayout( gridLayout );
		this.filenameLabel = new Label(this, SWT.NONE);
		this.filenameLabel.setText("Filename:");
		
		this.filenameText = new Text( this, SWT.BORDER );
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.filenameText.setLayoutData( gridData );
		
		this.filenameErrorLabel = new Label(this, SWT.NONE);
		
		this.searchButton = new Button( this, SWT.NONE );
		this.searchButton.setText( "Search" );
		this.searchButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
								
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( e.widget == searchButton ) {
					Shell shell = getShell();
					String name = new FileDialog(shell, SWT.OPEN).open();
					
					if( name == null )
					      return;
					filenameText.setText( name );
				}
				
			}
			
		});
		this.filenameText.addModifyListener( new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if( axis != null ) {
					axis.setPositionfile( filenameText.getText() );
				}
			}
			
			
			
		});
	}

	public void setAxis( final Axis axis ) {
		this.axis = axis;
		if( this.axis != null ) {
			this.filenameText.setText( this.axis.getPositionfile()==null?"":this.axis.getPositionfile() );
			
			this.filenameErrorLabel.setImage( null );
			
			final Iterator< IModelError > it = this.axis.getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof AxisError ) {
					final AxisError axisError = (AxisError)modelError;
					if( axisError.getErrorType() == AxisErrorTypes.FILENAME_NOT_SET ) {
						this.filenameErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
						break;
					}
				}
			}
			
			this.filenameText.setEnabled( true );
			this.searchButton.setEnabled( true );
		} else {
			this.filenameText.setText( "" );
			this.filenameErrorLabel.setImage( null );
			this.filenameText.setEnabled( false );
			this.searchButton.setEnabled( false );
		}
	}
}
