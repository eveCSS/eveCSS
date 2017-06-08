package de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * {@link org.eclipse.jface.viewers.EditingSupport} for the plugin column.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class PluginEditingSupport extends EditingSupport {

	private static Logger logger = 
			Logger.getLogger(PluginEditingSupport.class.getName());
	
	private TableViewer viewer;
	private List<String> plugins;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer
	 */
	public PluginEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		plugins = new ArrayList<String>();
		for(PlugIn plugin : 
			Activator.getDefault().getMeasuringStation().getPlugins()) {
		if(plugin.getType().equals(PluginTypes.POSTSCANPOSITIONING)) {
			this.plugins.add(plugin.getName());
		}
	}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return new MyComboBoxCellEditor(viewer.getTable(), 
				this.plugins.toArray(new String[0]));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		final Positioning positioning = (Positioning)element;
		if(positioning.getPluginController().getPlugin() != null) {
			for(String s : plugins) {
				if(s.equals(positioning.getPluginController().getPlugin())) {
					return plugins.indexOf(s);
				}
			}
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		PlugIn plugin = Activator.getDefault().getMeasuringStation().
				getPluginByName(plugins.get((Integer)value));
		((Positioning)element).getPluginController().setPlugin(plugin);
		if(logger.isDebugEnabled()) {
			logger.debug("Set plugin " + plugin.getName() + " for positioning " 
						+ ((Positioning)element).getMotorAxis().getName());
		}
	}
}