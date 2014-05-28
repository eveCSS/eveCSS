package de.ptb.epics.eve.editor.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.util.jface.MyComboBoxCellEditor;

/**
 * {@link org.eclipse.jface.viewers.EditingSupport} of the value column.
 * 
 * @author Hartmut Scherr
 * @since 1.9
 */
public class PluginControllerValueEditingSupport extends EditingSupport {

	private TableViewer viewer;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer
	 */
	public PluginControllerValueEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {

		System.out.println("getCellEditor für Plugin aufgerufen");
		
		Map.Entry<String, String> entry = ((Map.Entry<String, String>) element);

		System.out.println("   Entry: " + entry);

		// herausfinden welche Option (Name) den CellEditor 
		// aufgrufen hat

		System.out.println("   Entry Key: " + entry.getKey());
		System.out.println("   Entry Value: " + entry.getValue());
		System.out.println("   TableViewer: " + this.viewer);
		System.out.println("      CellModfier: " + this.viewer.getCellModifier());
		System.out.println("      CellEditor: " + this.viewer.getCellEditors());

		
//		final PluginController pluginController = (PluginController)element;
//		final Plugin plugin = (Plugin)element;


//		pluginController.getScanModule().toString()

//		Frage: Was ist richtig, im CellEditor den PluginController
//		oder nur das Plugin zu verwenden?
		
//		if(plugin.
		
		if(entry.getKey().equals("referenceaxis")) {
			final Prescan prescan = (Prescan)element;
			
			List<String> discreteValues = new ArrayList<String>();
			if (prescan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.ONOFF)) {
				discreteValues.add("On");
				discreteValues.add("Off");
			} else if (prescan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.OPENCLOSE)) {
				discreteValues.add("Open");
				discreteValues.add("Close");
			} else {
				discreteValues.addAll(prescan.getAbstractPrePostscanDevice().
						getValue().getDiscreteValues());
			}
			return new MyComboBoxCellEditor(this.viewer.getTable(), 
					discreteValues.toArray(new String[0]));
		} else {
			return new TextCellEditor(this.viewer.getTable()) {
				@Override protected void focusLost() {
					if(isActivated()) {
						fireCancelEditor();
					}
					deactivate();
				}
			};
		}
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

		System.out.println("getValue event im valueEditingSupport");

		Map.Entry<String, String> entry = 
				((Map.Entry<String, String>)element);

		System.out.println("  entry.key: " + entry.getKey());
		System.out.println("  entry.Value: " + entry.getValue());

		System.out.println("      CellEditors:"+ this.viewer.getCellEditors());
		System.out.println("      Table:"+ this.viewer.getTable());
		System.out.println("      Table:"+ this.viewer.getTable().getClass());

//		if (this.viewer.getCellEditors()[1] instanceof TextCellEditor {
//		if (this.viewer.getTable() instanceof TextCellEditor) {
//			System.out.println("      entry gehört zu einem TextCellEditor");
//		}
		
		if(entry.getKey().equals("referenceaxis")) {
			final Prescan prescan = (Prescan)element;
		} else {
			return (entry.getValue() == null) ? "" : entry.getValue();

		}
			
		final Prescan prescan = (Prescan)element;
		if(prescan.getAbstractPrePostscanDevice().isDiscrete()) {
			if (prescan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.ONOFF)) {
				if (prescan.getValue().equals("On")) {
					return 0;
				} else if (prescan.getValue().equals("Off")) {
					return 1;
				} else {
					return 0;
				}
			} else if (prescan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.OPENCLOSE)) {
				if (prescan.getValue().equals("Open")) {
					return 0;
				} else if (prescan.getValue().equals("Close")) {
					return 1;
				} else {
					return 0;
				}
			} else {
				int index = prescan.getAbstractPrePostscanDevice().getValue().
						getDiscreteValues().indexOf(prescan.getValue());
				return index == -1 ? 0 : index;
			}
		} else {
			return prescan.getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {

		System.out.println("setValue event im valueEditingSupport");

		Map.Entry<String, String> entry = 
				((Map.Entry<String, String>)element);

		System.out.println("  entry.key: " + entry.getKey());
		System.out.println("  entry.Value: " + entry.getValue());

		// Jetzt muss herausgefunden werden, zu welchem PluginController
		// der Wert gehört
		
		//Wie bestimmt man den PluginController?
		
		
		final Prescan prescan = (Prescan)element;
		if(prescan.getAbstractPrePostscanDevice().isDiscrete()) {
			if (prescan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.ONOFF)) {
				if (((Integer)value) == 0) {
					prescan.setValue("On");
				} else {
					prescan.setValue("Off");
				}
			} else if (prescan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.OPENCLOSE)) {
				if (((Integer)value) == 0) {
					prescan.setValue("Open");
				} else {
					prescan.setValue("Close");
				}
			} else {
				prescan.setValue(prescan.getAbstractPrePostscanDevice().
					getValue().getDiscreteValues().get((Integer)value));
			}
		} else {
			prescan.setValue((String)value);
		}
	}
}