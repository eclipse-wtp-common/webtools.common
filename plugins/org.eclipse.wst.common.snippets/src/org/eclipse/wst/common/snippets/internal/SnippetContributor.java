/*******************************************************************************
 * Copyright (c) 2009, 2011 SAP AG and others.
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
package org.eclipse.wst.common.snippets.internal;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.snippets.core.ISnippetProvider;

public class SnippetContributor {

	public static final String ID_EXTENSION_POINT_PROVIDER = SnippetsPlugin.BUNDLE_ID + ".SnippetProvider"; //$NON-NLS-1$
	public static final String CLASS = "class"; //$NON-NLS-1$
	public static final String PRIORITY = "priority"; //$NON-NLS-1$
	public static final String ENABLEMENT = "enablement"; //$NON-NLS-1$

	private ISnippetProvider provider;
	private IConfigurationElement extPointElement;
	// 100 = lowest, 0 = highest
	private byte priority = 100;

	private boolean initExecuted = false;


	public SnippetContributor(IConfigurationElement extPointElement) {
		super();
		this.extPointElement = extPointElement;
		initPriority();
	}

	private void initPriority() {
		if (extPointElement.getAttribute(PRIORITY) != null) {
			try {
				priority = (byte) Integer.parseInt(extPointElement.getAttribute(PRIORITY));
				if (priority < 0 || priority > 100) {
					priority = 100;
				}
			}
			catch (NumberFormatException e) {
				priority = 100;
				Logger.logException(e);
			}
		}
	}

	private void initProvider() {
		initExecuted = true;
		try {
			provider = (ISnippetProvider) extPointElement.createExecutableExtension(CLASS);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}


	}


	public boolean isApplicable(Object o) {
		IConfigurationElement[] configurationElements = extPointElement.getChildren(ENABLEMENT);
		boolean enabled = false;
		for (int i = 0; i < configurationElements.length; i++) {
			try {
				enabled |= testEnablement(configurationElements[i], o);
			}
			catch (CoreException e) {
				// nothing to do testEnablement fails.
				return false;
			}
		}
		return enabled;
	}

	private boolean testEnablement(IConfigurationElement enable, Object o) throws CoreException {
		Expression exp = ExpressionConverter.getDefault().perform(enable);
		EvaluationContext context = new EvaluationContext(null, o);
		context.setAllowPluginActivation(true);
		return EvaluationResult.TRUE == exp.evaluate(context);
	}


	public ISnippetProvider getProvider() {
		if (!initExecuted) {
			initProvider();
		}
		return provider;
	}

	public byte getPriority() {
		return priority;
	}

	public String toString() {
		return super.toString() + ":" + extPointElement.getAttribute(CLASS); //$NON-NLS-1$
	}
}
