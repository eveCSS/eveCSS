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

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * A sample perspective implementation, which initially contains of 3 views.
 * 
 * @author Sven Wende
 * 
 */
public final class EveEnginePerspective implements IPerspectiveFactory {

	/**
	 * {@inheritDoc}
	 */
	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible( false );
		
		layout.addView( "MessagesView", IPageLayout.LEFT, 0.35f, IPageLayout.ID_EDITOR_AREA);
		layout.addView( "MeasuringStationView", IPageLayout.TOP, 0.8f, "MessagesView" );
		layout.addView( "PlayListView", IPageLayout.LEFT, 0.5f, "MeasuringStationView" );
		layout.addView( "GraphView", IPageLayout.BOTTOM, 0.75f, "PlayListView" );
		
		IFolderLayout folder = layout.createFolder("DeviceInspectorFolder", IPageLayout.RIGHT, 0.50f, IPageLayout.ID_EDITOR_AREA );
		folder.addPlaceholder("DeviceInspectorView:*");
		folder.addView( "DeviceInspectorView" );
		
		//layout.addView( "PlotView", IPageLayout.TOP, 0.50f, "DeviceInspectorView" );
        folder = layout.createFolder("PlotViewFolder", IPageLayout.TOP, 0.50f, "DeviceInspectorView");
        folder.addPlaceholder(PlotView.ID+":*");
        //folder.addView(PlotView.ID);
		layout.addView( "DeviceOptionsView", IPageLayout.RIGHT, 0.80f, "DeviceInspectorView" );
		
		
		layout.addActionSet( "de.ptb.epics.eve.viewer.engineControlActionSet" );
		//layout.addActionSet( "de.ptb.epics.eve.viewer.engineConnectionActionSet" );
		//layout.addActionSet( "de.ptb.epics.eve.viewer.actionSet" );
	}

}
