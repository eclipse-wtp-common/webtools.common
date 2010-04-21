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
package org.eclipse.wst.common.componentcore.ui.internal.propertypage;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public interface IVirtualComponentLabelProvider {
	/**
	 * Return whether this label provider can provide
	 * UI elements for this component
	 * @param component
	 * @return
	 */
	public boolean canHandle(IVirtualComponent component);
	
	/**
	 * Return a string representation for this component
	 * @param component
	 * @return
	 */
	public String getSourceText(IVirtualComponent component);
	
	/**
	 * Return an image representing this component
	 * @param component
	 * @return
	 */
	public Image getSourceImage(IVirtualComponent component);
	
	/**
	 * Clean up any images you created
	 */
	public void dispose();
}
