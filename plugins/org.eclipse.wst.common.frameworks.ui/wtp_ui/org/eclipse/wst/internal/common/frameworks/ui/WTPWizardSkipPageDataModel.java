/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 25, 2004
 *
 */
package org.eclipse.wst.internal.common.frameworks.ui;

import java.util.Collections;

import org.eclipse.wst.common.framework.operation.WTPOperation;
import org.eclipse.wst.common.framework.operation.WTPOperationDataModel;


/**
 * @author jsholl
 *  
 */
public class WTPWizardSkipPageDataModel extends WTPOperationDataModel {

	/**
	 * A List of Strings identifying the page id's to skip
	 */
	public static final String SKIP_PAGES = "WTPWizardSkipPageDataModel.SKIP_PAGES"; //$NON-NLS-1$

	public WTPOperation getDefaultOperation() {
		return null;
	}

	protected void initValidBaseProperties() {
		super.initValidBaseProperties();
		addValidBaseProperty(SKIP_PAGES);
	}

	protected Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(SKIP_PAGES)) {
			return Collections.EMPTY_LIST;
		}
		return super.getDefaultProperty(propertyName);
	}

}