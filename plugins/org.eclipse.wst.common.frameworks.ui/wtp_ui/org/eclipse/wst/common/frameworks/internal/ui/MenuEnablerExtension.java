/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.core.expressions.IPropertyTester;
import org.eclipse.core.runtime.IConfigurationElement;


public class MenuEnablerExtension {

	public static final String ATT_ID = "id"; //$NON-NLS-1$
	public static final String MENU_ENABLER_EXTENSION = "menuenabler"; //$NON-NLS-1$
	
	private String id = null;
	private IConfigurationElement element;
	//private IMenuEnabler instance;
	private IPropertyTester instance;
	private boolean errorCondition = false;
	
	public MenuEnablerExtension(){
		super();
	}
	
	private void init() {
		id = element.getAttribute(ATT_ID);
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public MenuEnablerExtension(IConfigurationElement element) {
		this.element = element;
		init();
	}
	
	public IPropertyTester getInstance() {
		try {
			if (instance == null && !errorCondition)
				instance = (IPropertyTester) element.createExecutableExtension("className"); //$NON-NLS-1$
		} catch (Throwable e) {
			errorCondition = true;
		}
		return instance;
	}	
}
