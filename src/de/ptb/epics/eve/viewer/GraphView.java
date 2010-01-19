/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package de.ptb.epics.eve.viewer;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModul;

/**
 * A simple view implementation, which only displays a label.
 * 
 * @author Sven Wende
 *
 */
public final class GraphView extends ViewPart implements IUpdateListener {

	private Text statusText;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl( final Composite parent ) {
		parent.setLayout( new FillLayout() );
		this.statusText = new Text( parent, SWT.MULTI );
		this.statusText.setEditable( false );
		Activator.getDefault().getChainStatusAnalyzer().addUpdateLisner( this );
		this.rebuildText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {

	}

	public void updateOccured() {
		this.rebuildText();
		
	}
	
	private void rebuildText() {
		final StringBuffer stringBuffer = new StringBuffer();
		
		final List< Chain > idleChains = Activator.getDefault().getChainStatusAnalyzer().getIdleChains();
		if( idleChains.size() > 0 ) {
			stringBuffer.append( "Idle Chains: " );
			for( int i = 0; i < idleChains.size(); ++i ) {
				stringBuffer.append( idleChains.get( i ).getId() );
				stringBuffer.append( ", " );
			}
			stringBuffer.append( '\n' );
		}
		
		final List< Chain > runningChains = Activator.getDefault().getChainStatusAnalyzer().getRunningChains();
		if( runningChains.size() > 0 ) {
			stringBuffer.append( "Running Chains: " );
			for( int i = 0; i < runningChains.size(); ++i ) {
				stringBuffer.append( runningChains.get( i ).getId() );
				stringBuffer.append( ", " );
			}
			stringBuffer.append( '\n' );
		}
		
		final List< ScanModul > initializingScanModules = Activator.getDefault().getChainStatusAnalyzer().getInitializingScanModules();
		if( initializingScanModules.size() > 0 ) {
			stringBuffer.append( "Initializing Scan Modules: " );
			for( int i = 0; i < initializingScanModules.size(); ++i ) {
				stringBuffer.append( initializingScanModules.get( i ).getId() );
				stringBuffer.append( ", " );
			}
			stringBuffer.append( '\n' );
		}
		
		final List< ScanModul > executingScanModules = Activator.getDefault().getChainStatusAnalyzer().getExecutingScanModules();
		if( executingScanModules.size() > 0 ) {
			stringBuffer.append( "Executing Scan Modules: " );
			for( int i = 0; i < executingScanModules.size(); ++i ) {
				stringBuffer.append( executingScanModules.get( i ).getId() );
				stringBuffer.append( ", " );
			}
			stringBuffer.append( '\n' );
		}
		
		final List< ScanModul > pausedScanModules = Activator.getDefault().getChainStatusAnalyzer().getPausedScanModules();
		if( pausedScanModules.size() > 0 ) {
			stringBuffer.append( "Paused Scan Modules: " );
			for( int i = 0; i < pausedScanModules.size(); ++i ) {
				stringBuffer.append( pausedScanModules.get( i ).getId() );
				stringBuffer.append( ", " );
			}
			stringBuffer.append( '\n' );
		}
		
		final List< ScanModul > waitingScanModules = Activator.getDefault().getChainStatusAnalyzer().getWaitingScanModules();
		if( waitingScanModules.size() > 0 ) {
			stringBuffer.append( "Waiting Scan Modules: " );
			for( int i = 0; i < waitingScanModules.size(); ++i ) {
				stringBuffer.append( waitingScanModules.get( i ).getId() );
				stringBuffer.append( ", " );
			}
			stringBuffer.append( '\n' );
		}
		
		this.statusText.getDisplay().syncExec( new Runnable() {

			public void run() {
				statusText.setText( stringBuffer.toString() );
				
			}
			
			
		});
	}

}
