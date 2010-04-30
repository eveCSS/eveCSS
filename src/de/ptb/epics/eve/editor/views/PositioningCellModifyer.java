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
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.editor.Activator;


public class PositioningCellModifyer implements ICellModifier {

	private final TableViewer tableViewer;
	
	public PositioningCellModifyer( final TableViewer tableViewer ) {
		this.tableViewer = tableViewer;
	}
	
	public boolean canModify( final Object element, final String property ) {
		if (property.equals("channel")) {
			// Es werden nur die Channels erlaubt die in diesem ScanModul verwendet werden
			ScanModul scanModul = (ScanModul)this.tableViewer.getInput();
			Channel[] cur_channel = scanModul.getChannels();
			String[] cur_feld = new String[cur_channel.length];
			for (int i=0; i<cur_channel.length; ++i) {
				cur_feld[i] = cur_channel[i].getDetectorChannel().getFullIdentifyer();
			}
	    	((ComboBoxCellEditor)this.tableViewer.getCellEditors()[2]).setItems(cur_feld);
			return property.equals( "channel" );
		}
		else if (property.equals("normalize")) {
			// Es werden nur die Channels erlaubt die in diesem ScanModul verwendet werden
			ScanModul scanModul = (ScanModul)this.tableViewer.getInput();
			Channel[] cur_channel = scanModul.getChannels();
			String[] cur_feld = new String[cur_channel.length + 1];
			cur_feld[0] = "none";
			for (int i=0; i<cur_channel.length; ++i) {
				cur_feld[i+1] = cur_channel[i].getDetectorChannel().getFullIdentifyer();
			}
	    	((ComboBoxCellEditor)this.tableViewer.getCellEditors()[3]).setItems(cur_feld);
			return property.equals( "normalize" );
		}
		return property.equals( "plugin" ) || property.equals( "parameter" );
	}

	public Object getValue( final Object element, final String property ) {
		final Positioning positioning = (Positioning)element;
		if( property.equals( "plugin" ) ) {
			if( positioning.getPluginController().getPlugin() != null ) {
				final String[] pluginsArray = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).getItems();
				for( int i = 0; i < pluginsArray.length; ++i ) {
					if( pluginsArray[i].equals( positioning.getPluginController().getPlugin().getName() ) ) {
						return i;
					}
				}
			}
			// mit return 0 wird der erste Wert voreingestellt
	    	return 0;
		} else if( property.equals( "channel" ) ) {
			if( positioning.getDetectorChannel() != null ) {
				final String[] channelsArray = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[2]).getItems();
				for( int i = 0; i < channelsArray.length; ++i ) {
					if( channelsArray[i].equals( positioning.getDetectorChannel().getFullIdentifyer() ) ) {
						return i;
					}
				}
			}
	    	// mit return 0 wird der erste Wert voreingestellt
	    	return 0;
		} else if( property.equals( "normalize" ) ) {
			if( positioning.getNormalization() != null ) {
				final String[] normalizeArray = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[3]).getItems();
				for( int i = 0; i < normalizeArray.length; ++i ) {
					if( normalizeArray[i].equals( positioning.getNormalization().getFullIdentifyer() ) ) {
						return i;
					}
				}
			}
	    	// mit return 0 wird der erste Wert voreingestellt
			return 0;
		} else if( property.equals( "parameter" ) ) {
			return positioning.getPluginController();
		}
		return -1;
	}

	public void modify( final Object element, final String property, final Object value ) {
		final Positioning positioning = (Positioning)((TableItem)element).getData();
		if( property.equals( "plugin" ) ) {
			final String[] pluginsArray = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).getItems();
			final PlugIn newPlugin = Activator.getDefault().getMeasuringStation().getPluginByName( pluginsArray[ (Integer)value ] );
			if( newPlugin != positioning.getPluginController().getPlugin() ) {
				positioning.getPluginController().setPlugin( newPlugin );
			}
		} else if( property.equals( "channel" ) && (Integer)value != -1 ) {
			final String[] channelsArray = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[2]).getItems();
			positioning.setDetectorChannel( (DetectorChannel)Activator.getDefault().getMeasuringStation().getAbstractDeviceByFullIdentifyer( channelsArray[ (Integer)value] ) );
		} else if( property.equals( "normalize" ) ) {
			if( ((Integer)value).equals( -1 ) ) {
				positioning.setNormalization( null );
				this.tableViewer.refresh();
				return;
			}
			final String[] normalizeArray = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[3]).getItems();
			positioning.setNormalization( (DetectorChannel)Activator.getDefault().getMeasuringStation().getAbstractDeviceByFullIdentifyer( normalizeArray[ (Integer)value] ) );
		}
		this.tableViewer.refresh();
	}

}
