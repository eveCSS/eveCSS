package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Option;

/**
 * <code>ContentProvider</code> is the content provider of the table viewer 
 * defined in 
 * {@link de.ptb.epics.eve.viewer.views.deviceoptionsview.DeviceOptionsView}.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ContentProvider implements IStructuredContentProvider, PropertyChangeListener {

	private static Logger logger = 
			Logger.getLogger(ContentProvider.class.getName());
	
	// reference to the corresponding viewer
	private Viewer viewer;
	
	// the device of interest
	private AbstractDevice device;
	
	// a list of the device's options
	private List<OptionPV> options;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return this.options.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(this.device != null) {
			// clean up previous entries
			for(OptionPV o : this.options) {
				o.removePropertyChangeListener(this);
				o.disconnect();
			}
			options.clear();
		}

		// register on new input
		this.viewer = viewer;
		this.options = new ArrayList<OptionPV>();
		this.device = (AbstractDevice)newInput;
		
		if(this.device != null) {
			for(Option o : this.device.getOptions()) {
				OptionPV opv = new OptionPV(o);
				opv.addPropertyChangeListener("value", this);
				this.options.add(opv);
			}
		}
		
		// update the viewer
		this.viewer.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		for(OptionPV opv : this.options) {
			opv.disconnect();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (!((OptionPV)evt.getSource()).isConnected()) {
			logger.debug("!! DISCONNECTED !!");
			return;
		}
		logger.debug("Prop change in " + ((OptionPV)evt.getSource()).getName());
		if(!evt.getPropertyName().equals("value")) {
			return;
		}
		if(!((TableViewer)viewer).isCellEditorActive()) {
			viewer.refresh();
		}
	}
}