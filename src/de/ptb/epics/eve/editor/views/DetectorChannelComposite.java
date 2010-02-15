/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;

import de.ptb.epics.eve.data.scandescription.Channel;

public class DetectorChannelComposite extends Composite {

	private Label averageLabel = null;
	private Text averageText = null;
	private Button confirmCheckBox = null;
	private Channel channel;
	private ModifyListener modifyListener;
	

	public DetectorChannelComposite(Composite parent, int style, final Channel channel ) {
		super(parent, style);
		this.channel = channel;
		initialize();
	}

	private void initialize() {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		averageLabel = new Label(this, SWT.NONE);
		averageLabel.setText("Average:");
		averageText = new Text(this, SWT.BORDER);
		confirmCheckBox = new Button(this, SWT.CHECK);
		confirmCheckBox.setText("Confirm Trigger Manual");
		confirmCheckBox.setLayoutData(gridData);
		this.setLayout(gridLayout);
		setSize(new Point(300, 200));
		averageText.setText( "" + this.channel.getAverageCount() );
		this.createListener();
		this.appendListener();
		
	}
	
	private void createListener() {
		this.modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if( e.widget == averageText ) {
					System.err.println( "Hallo" );
					try {
						channel.setAverageCount( Integer.parseInt( averageText.getText() ) );
						Color oldColor = averageText.getBackground();
						averageText.setBackground( new Color( oldColor.getDevice(), 255, 255, 255 ) );
						oldColor.dispose();
						
					} catch( Exception ex ) {
						System.err.println( "Plonk" );
						channel.setAverageCount( 0 );
						Color oldColor = averageText.getBackground();
						averageText.setBackground( new Color( oldColor.getDevice(), 255, 0, 0 ) );
						oldColor.dispose();
					}
				}
				
			}
			
		};
	}
	
	private void appendListener() {
		this.averageText.addModifyListener( this.modifyListener );
	}

}
