/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.io.File;
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
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>MotorAxisFileComposite</code> is a composite to input a filename
 * with positions of the motor axis.
 * @author Hartmut Scherr
 *
 */
public class MotorAxisFileComposite extends Composite implements IModelUpdateListener {

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
		this.searchButton.addSelectionListener( new SearchButtonSelectionListener());
		this.filenameText.addModifyListener( new FilenameTextModifyListener());

	}

	/**
	 * calculate the height to see all entries of this composite
	 * @return the needed height of Composite to see all entries
	 */
	public int getTargetHeight() {
		return (filenameText.getBounds().y + filenameText.getBounds().height + 5);
	}

	/**
	 * calculate the width to see all entries of this composite
	 * @return the needed width of Composite to see all entries
	 */
	public int getTargetWidth() {
		return (filenameText.getBounds().x + filenameText.getBounds().width + 5);
	}

	public void setAxis( final Axis axis ) {
		if( this.axis != null ) {
			this.axis.removeModelUpdateListener( this );
		}
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
			this.axis.addModelUpdateListener( this );
		} else {
			this.filenameText.setText( "" );
			this.filenameErrorLabel.setImage( null );
			this.filenameText.setEnabled( false );
			this.searchButton.setEnabled( false );
		}
	}

	@Override
	public void dispose() {
		if( this.axis != null ) {
			this.axis.removeModelUpdateListener( this );
		}
		super.dispose();
	}

	@Override
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
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
		
	}
	
	///////////////////////////////////////////////////////////
	// Hier kommen jetzt die verschiedenen Listener Klassen
	///////////////////////////////////////////////////////////
	/**
	 * <code>SelectionListener</code> of Search Button from
	 * <code>MotorAxisFileComposite</code>
	 */
	class SearchButtonSelectionListener implements SelectionListener {

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
			if( e.widget == searchButton ) {
				Shell shell = getShell();

				int lastSeperatorIndex;
				final String filePath;
				
				if ((axis.getPositionfile() == null) || (axis.getPositionfile().equals(""))) {
					lastSeperatorIndex = Activator.getDefault().getMeasuringStation().getLoadedFileName().lastIndexOf( File.separatorChar );
					filePath = Activator.getDefault().getMeasuringStation().getLoadedFileName().substring( 0, lastSeperatorIndex);
				}
				else {
					// als filePath wird das vorhandene Verzeichnis gesetzt
					lastSeperatorIndex = axis.getPositionfile().lastIndexOf( File.separatorChar );
					filePath = axis.getPositionfile().substring( 0, lastSeperatorIndex + 1 );
				}
				
				FileDialog fileWindow = new FileDialog(shell, SWT.SAVE);
				fileWindow.setFilterPath(filePath);
				String name = fileWindow.open();
				
				if( name == null )
				      return;
				filenameText.setText( name );
			}
			
		}
		
	};

	/**
	 * <code>ModifyListener</code> of Filename Text from
	 * <code>MotorAxisFileComposite</code>
	 */
	class FilenameTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			if( axis != null ) {
				axis.setPositionfile( filenameText.getText() );
			}
		}
	};

}
