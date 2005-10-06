/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.tests.ui.wizard;

import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.AddablePageGroup;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.SimplePageGroup;
import org.eclipse.wst.common.frameworks.internal.ui.SimplePageGroupHandler;

public class Test2DataModelWizard extends DataModelWizard {

	protected IDataModelProvider getDefaultProvider() {
		return new Test2DataModelProvider();
	}

	protected AddablePageGroup createRootPageGroup() {
		SimplePageGroup pg = (SimplePageGroup) super.createRootPageGroup();
		pg.setExtendedPageGroupHandler(new SimplePageGroupHandler() {
			public String getNextPageGroup(String currentPageGroupID, String[] pageGroupIDs) {
				if (currentPageGroupID == null) {
					for (int i = 0; i < pageGroupIDs.length; i++) {
						if (pageGroupIDs[i].equals("bar")) {
							return "bar";
						}
					}
				}
				return super.getNextPageGroup(currentPageGroupID, pageGroupIDs);
			}
		});
		return pg;
	}

}
