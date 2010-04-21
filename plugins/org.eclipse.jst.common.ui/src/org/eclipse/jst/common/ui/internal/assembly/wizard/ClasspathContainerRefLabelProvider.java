/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.jst.common.ui.internal.assembly.wizard;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.common.internal.modulecore.ClasspathContainerVirtualComponent;
import org.eclipse.jst.common.ui.internal.JstCommonUIPlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.IVirtualComponentLabelProvider;

public class ClasspathContainerRefLabelProvider implements
		IVirtualComponentLabelProvider {

	public ClasspathContainerRefLabelProvider() {
		// TODO Auto-generated constructor stub
	}

	public boolean canHandle(IVirtualComponent component) {
		return (component instanceof ClasspathContainerVirtualComponent);
	}

	public String getSourceText(IVirtualComponent component) {
		return ((ClasspathContainerVirtualComponent)component).getClasspathContainerPath();
	}

	private Image image;
	public Image getSourceImage(IVirtualComponent component) {
		if( image == null ) {
			try {
				URL url = new URL( JstCommonUIPlugin.getDefault().getBundle().getEntry("/"), "icons/util-wiz-icon.gif"); //$NON-NLS-1$ //$NON-NLS-2$
				ImageDescriptor imageDescriptor = null;
				if (url != null)
					imageDescriptor = ImageDescriptor.createFromURL(url);
				if( imageDescriptor != null ) 
					image = imageDescriptor.createImage();
			} catch( MalformedURLException murle ) {
				// do nothing
			}
		}
		return image;
	}
	
	public void dispose() {
		image.dispose();
	}
}
