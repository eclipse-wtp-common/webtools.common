/*******************************************************************************
 * Copyright (c) 2009 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.core.ISnippetProvider;
import org.eclipse.wst.common.snippets.internal.SnippetContributor;
import org.eclipse.wst.common.snippets.ui.TextSnippetProvider;


public class SnippetProviderManager {

	private static ISnippetProvider[] getProviders() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(SnippetContributor.ID_EXTENSION_POINT_PROVIDER);
		IExtension[] extensions = extensionPoint.getExtensions();
		List providerList = new ArrayList();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] configurationElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configurationElements.length; j++) {
				SnippetContributor sc = new SnippetContributor(configurationElements[j]);
				if (sc.getProvider() != null) {
					providerList.add(sc.getProvider());
				}
			}

		}
		return (ISnippetProvider[]) providerList.toArray(new ISnippetProvider[providerList.size()]);
	}

	public static ISnippetProvider findProvider(String id) {
		Assert.isNotNull(id);
		ISnippetProvider[] providers = getProviders();
		for (int i = 0; i < providers.length; i++) {
			if (id.equals(providers[i].getId()))
				return providers[i];
		}

		return new TextSnippetProvider();
	}

	public static ISnippetProvider getApplicableProvider(IEditorPart targetEditor) {
		SnippetContributor applicableContributor = null;
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(SnippetContributor.ID_EXTENSION_POINT_PROVIDER);
		if (extensionPoint != null) {
			IExtension[] extensions = extensionPoint.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configurationElements = extensions[i].getConfigurationElements();
				for (int j = 0; j < configurationElements.length; j++) {
					SnippetContributor sc = new SnippetContributor(configurationElements[j]);
					if (sc.isApplicable(targetEditor)) {
						if (applicableContributor == null || applicableContributor.getPriority() > sc.getPriority()) {
							applicableContributor = sc;
						}
					}
				}
			}
		}
		if (applicableContributor == null) {
			return null;
		}
		ISnippetProvider provider = applicableContributor.getProvider();
		// a null provider is an error condition
		if (provider != null) {
			provider.setEditor(targetEditor);
		}
		return provider;
	}



}
