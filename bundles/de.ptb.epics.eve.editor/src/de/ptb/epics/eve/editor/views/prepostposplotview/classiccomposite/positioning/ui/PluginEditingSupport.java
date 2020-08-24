package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PluginEditingSupport extends EditingSupport {
	private TableViewer viewer;
	private List<String> plugins;
	
	public PluginEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		plugins = new ArrayList<>();
		for (PlugIn plugin : 
				Activator.getDefault().getMeasuringStation().getPlugins()) {
			if (plugin.getType().equals(PluginTypes.POSTSCANPOSITIONING)) {
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
		if (positioning.getPluginController().getPlugin() != null) {
			for (String s : plugins) {
				if (s.equals(positioning.getPluginController().
						getPlugin().getName())) {
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
	}
}
