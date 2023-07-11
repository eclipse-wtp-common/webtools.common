/***************************************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMPageGroupHandler;

public class SimplePageGroupHandler implements IDMPageGroupHandler {
	@Override
	public String getNextPageGroup(String currentPageGroupID, String[] pageGroupIDs) {
		if (pageGroupIDs == null || pageGroupIDs.length == 0)
			return null;

		if (currentPageGroupID == null)
			return pageGroupIDs[0];

		String result = null;

		for (int index = 0; index < pageGroupIDs.length; index++) {
			if (pageGroupIDs[index].equals(currentPageGroupID)) {
				// We found the currentPageGroupID, so we want to return the next one in the
				// array if there is one.
				if (index + 1 < pageGroupIDs.length) {
					result = pageGroupIDs[index + 1];
				}
				break;
			}
		}

		return result;
	}
}
