/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PrePostscanError;

public class PostscanLabelProvider implements ITableLabelProvider {

	// Names of images used to represent checkboxes
	public static final String CHECKED_IMAGE 	= "checked";
	public static final String UNCHECKED_IMAGE  = "unchecked";

	// For the checkbox images
	private static ImageRegistry imageRegistry = new ImageRegistry();

	/**
	 * Note: An image registry owns all of the image objects registered with it,
	 * and automatically disposes of them the SWT Display is disposed.
	 */ 
	
	static {
		String iconPath = "../"; 
		//TODO Frage: Kann hier das image auch irgendwo von SWT hergeholt werden?
		// Wie muß der iconPath gesetzt sein, damit auch darüberliegende Verzeichnisse
		// durchsucht werden? (Hartmut 19.4.10)
		imageRegistry.put(CHECKED_IMAGE, ImageDescriptor.createFromFile(
				PostscanComposite.class, 
				CHECKED_IMAGE + ".gif"
//				iconPath + CHECKED_IMAGE + ".gif"
				)
			);
		imageRegistry.put(UNCHECKED_IMAGE, ImageDescriptor.createFromFile(
				PostscanComposite.class, 
				UNCHECKED_IMAGE + ".gif"
//				iconPath + UNCHECKED_IMAGE + ".gif"
				)
			);	
	}
	
	/**
	 * Returns the image with the given key, or <code>null</code> if not found.
	 */
	private Image getImage(boolean isSelected) {
		String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
		return  imageRegistry.get(key);
	}
	
	public Image getColumnImage( final Object postscan, final int colIndex ) {
		// TODO Auto-generated method stub
		if (colIndex == 2) {
			// TODO Fehlerbehandlung fehlt
			Image bild = getImage(((Postscan) postscan).isReset());
			return bild;
		}
		
		final Postscan pos = (Postscan)postscan;
		if( colIndex == 1 ) {
			final Iterator< IModelError > it = pos.getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof PrePostscanError ) {
					//final PrePostscanError prePostscanError = (PrePostscanError)modelError;
					return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
				}
			}
		}
		return null;
		
	}
	
	public String getColumnText( final Object postscan, final int colIndex ) {
		final Postscan pos = (Postscan)postscan;
		switch( colIndex ) {
			case 0:
				return (pos.getAbstractPrePostscanDevice()!=null)?pos.getAbstractPrePostscanDevice().getFullIdentifyer():"";
			case 1:
				return (pos.getValue()!=null)?pos.getValue():"";
			case 2:
				break;
		}
		return "";
	}

	public void addListener( final ILabelProviderListener arg0 ) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty( final Object arg0, String arg1 ) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener( final ILabelProviderListener arg0 ) {
		// TODO Auto-generated method stub

	}

}
