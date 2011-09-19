package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>DeviceOptionsContentProvider</code> is the content provider of the 
 * table viewer defined in 
 * {@link de.ptb.epics.eve.viewer.views.deviceoptionsview.DeviceOptionsView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class DeviceOptionsContentProvider implements IStructuredContentProvider, 
														IModelUpdateListener {

	private static Logger logger = 
			Logger.getLogger(DeviceOptionsContentProvider.class.getName());
	
	private Viewer currentViewer;
	private AbstractDevice device;
	private List<OptionConnector> optionConnectors;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(final Object inputElement) {
		return this.optionConnectors.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		for(OptionConnector oc : optionConnectors) {
			oc.detach();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(
			final Viewer viewer, final Object oldInput, final Object newInput) {
		if (newInput == null) return;
		this.device = (AbstractDevice)newInput;
		this.currentViewer = viewer;
		
		if(this.optionConnectors != null) {
			for(OptionConnector oc : optionConnectors) {
				oc.detach();
			}
		}
		
		this.optionConnectors = new ArrayList<OptionConnector>();
		
		try {
			final Iterator<Option> it = this.device.optionIterator();
			while(it.hasNext()) {
				final OptionConnector optionConnector = 
					new OptionConnector(it.next());
				optionConnector.addModelUpdateListener(this);
				this.optionConnectors.add(optionConnector);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.currentViewer.getControl().getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				currentViewer.refresh();
			}
		});	
	}
}