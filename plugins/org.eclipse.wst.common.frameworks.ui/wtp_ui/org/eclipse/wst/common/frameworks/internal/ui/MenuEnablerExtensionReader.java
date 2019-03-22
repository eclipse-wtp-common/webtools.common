/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.core.util.RegistryReader;



public class MenuEnablerExtensionReader extends RegistryReader {

	private static MenuEnablerExtensionReader instance = null;
	private List<MenuEnablerExtension> pageExtenders = null;
	
	public MenuEnablerExtensionReader(){
		super("org.eclipse.wst.common.frameworks.ui", "MenuEnabler"); //$NON-NLS-1$ //$NON-NLS-2$ 
	}
	

	public static MenuEnablerExtensionReader getInstance() {
		if (instance == null) {
			instance = new MenuEnablerExtensionReader();
			instance.readRegistry();
		}
		return instance;
	}
	
	@Override
	public boolean readElement(IConfigurationElement element) {
		if (MenuEnablerExtension.MENU_ENABLER_EXTENSION.equals(element.getName())) {
			addExtension(element);
			return true;
		}
		return false;
	}
	
	protected void addExtension(IConfigurationElement newExtension) {
		getMenuEnabler().add(new MenuEnablerExtension(newExtension));
	}
	
	public List<MenuEnablerExtension> getMenuEnabler() {
		if (pageExtenders == null)
			pageExtenders = new ArrayList<MenuEnablerExtension>();
		return pageExtenders;
	}	

}
