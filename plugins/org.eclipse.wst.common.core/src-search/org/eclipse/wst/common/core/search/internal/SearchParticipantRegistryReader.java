/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

public class SearchParticipantRegistryReader
{
	protected static final String EXTENSION_POINT_ID = "searchParticipants"; //$NON-NLS-1$

	protected static final String TAG_NAME = "searchParticipant"; //$NON-NLS-1$

	protected static final String ATT_CLASS = "class"; //$NON-NLS-1$

	protected static final String ATT_ID = "id"; //$NON-NLS-1$

	protected String pluginId, extensionPointId;

	protected SearchParticipantRegistry registry;

	public SearchParticipantRegistryReader(SearchParticipantRegistry registry)
	{
		this.registry = registry;
	}

	public void readRegistry()
	{
		String bundleid = "org.eclipse.wst.common.core"; //$NON-NLS-1$
		IExtensionPoint point = Platform.getExtensionRegistry()
				.getExtensionPoint(bundleid, EXTENSION_POINT_ID);
		if (point != null)
		{
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++)
			{
				readElement(elements[i]);
			}
		}
	}

	protected void readElement(IConfigurationElement element)
	{
		if (element.getName().equals(TAG_NAME))
		{
			String contributorClass = element.getAttribute(ATT_CLASS);
			String id = element.getAttribute(ATT_ID);
			if (id != null)
			{
				if (contributorClass != null)
				{
					SearchParticipantDescriptor descriptor = new SearchParticipantDescriptor(
							element);
					registry.putSearchParticipant(id, descriptor);
				}
			}
		}
	}
}
