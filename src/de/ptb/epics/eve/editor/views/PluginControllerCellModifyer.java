/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;

import de.ptb.epics.eve.data.PluginDataType;
import de.ptb.epics.eve.data.measuringstation.PluginParameter;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.editor.Activator;

import java.util.Iterator;
import java.util.Map;

public class PluginControllerCellModifyer implements ICellModifier {

	private TableViewer tableViewer;
	
	public PluginControllerCellModifyer( final TableViewer tableViewer ) {
		if( tableViewer == null ) {
			throw new IllegalArgumentException( "The parameter 'tableViewer' must not be null!" );
		}
		this.tableViewer = tableViewer;
	}
	
	public boolean canModify( final Object element, final String property ) {
		if( property.equals( "option" ) ) {
			return false;
		} else if( property.equals( "value" ) ) {
			Map.Entry<String, String> entry = ((Map.Entry< String, String >)element);
			PluginController pluginController = (PluginController)this.tableViewer.getInput();
			final Iterator< PluginParameter > it = pluginController.getPlugin().getParameters().iterator();
			PluginParameter pluginParameter = null;
			while( it.hasNext() ) {
				pluginParameter = it.next();
				if( pluginParameter.getName().equals( entry.getKey() ) ) {
					break;
				}
				pluginParameter = null;
			}
			
			if( pluginParameter != null ) {
				if( pluginParameter.getType() == PluginDataType.AXISID ) {
					this.tableViewer.getCellEditors()[1].dispose();
					// hier kommen nur die Achsen zur Auswahl die im scanModul gewählt sind
					ScanModul scanModul = (ScanModul)((PluginController)this.tableViewer.getInput()).getScanModul();
					if ( scanModul != null) {
						Axis[] cur_axis = scanModul.getAxis();
						String[] cur_feld = new String[cur_axis.length];
						for (int i=0; i<cur_axis.length; ++i) {
							cur_feld[i] = cur_axis[i].getMotorAxis().getFullIdentifyer();
						}
						this.tableViewer.getCellEditors()[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), cur_feld, SWT.READ_ONLY );
					}
					else {
						// Falls kein scanModul gesetzt ist, werden alle Achsen zur Auswahl gestellt
						this.tableViewer.getCellEditors()[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), Activator.getDefault().getMeasuringStation().getAxisFullIdentifyer().toArray( new String[0] ), SWT.READ_ONLY );
					}
				} else if( pluginParameter.getType() == PluginDataType.CHANNELID ) {
					this.tableViewer.getCellEditors()[1].dispose();
					// hier kommen nur die Channels zur Auswahl die im scanModul gewählt sind
					ScanModul scanModul = (ScanModul)((PluginController)this.tableViewer.getInput()).getScanModul();
					if ( scanModul != null) {
						Channel[] cur_channel = scanModul.getChannels();
						String[] cur_feld = new String[cur_channel.length];
						for (int i=0; i<cur_channel.length; ++i) {
							cur_feld[i] = cur_channel[i].getDetectorChannel().getFullIdentifyer();
						}
						this.tableViewer.getCellEditors()[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), cur_feld, SWT.READ_ONLY );
					}
					else {
						// Falls kein scanModul gesetzt ist, werden alle Channels zur Auswahl gestellt
						this.tableViewer.getCellEditors()[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), Activator.getDefault().getMeasuringStation().getChannelsFullIdentifyer().toArray( new String[0] ), SWT.READ_ONLY );
					}
				} else if( pluginParameter.getType() == PluginDataType.DEVICEID ) {
					this.tableViewer.getCellEditors()[1].dispose();
					this.tableViewer.getCellEditors()[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), Activator.getDefault().getMeasuringStation().getPrePostScanDevicesFullIdentifyer().toArray( new String[0] ), SWT.READ_ONLY );
				} else if( pluginParameter.isDiscrete() ) {
					this.tableViewer.getCellEditors()[1].dispose();
					this.tableViewer.getCellEditors()[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), pluginParameter.getDiscreteValues().toArray( new String[0] ), SWT.READ_ONLY );
					//System.out.print( pluginParameter.getDiscreteValues().toArray( new String[0] ).length );
					
				} else {
					this.tableViewer.getCellEditors()[1].dispose();
					this.tableViewer.getCellEditors()[1] = new TextCellEditor( this.tableViewer.getTable() );
				}
			} else {
				return false;
			}
			return true;
		}
		return false;
	}

	public Object getValue( final Object element, final String property ) {
		if( property.equals( "value" ) ) {
			Map.Entry<String, String> entry = ((Map.Entry< String, String >)element);
			if( this.tableViewer.getCellEditors()[1] instanceof TextCellEditor ) {
				return (entry.getValue() == null)?"":entry.getValue();
			} else if( this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor ) {
				String[] items = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).getItems();
				for( int i = 0; i < items.length; ++i ) {
					if( items[i].equals( entry.getValue() ) ) {
						return i;
					}
				}
				return 0;
			}
		}
		
		
		return "";
	}

	public void modify( final Object element, final String property, final Object value ) {
		if( property.equals( "value" ) ) {
			Map.Entry<String, String> entry = ((Map.Entry< String, String >)((Item)element).getData());
			final PluginController pluginController = (PluginController)this.tableViewer.getInput();
			if( value instanceof String ) {
				pluginController.set( entry.getKey(), (String)value );
			} else if( value instanceof Integer ) {
				pluginController.set( entry.getKey(), ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).getItems()[(Integer)value] );
			}
			
			//this.tableViewer.refresh();
		}
	}

}
