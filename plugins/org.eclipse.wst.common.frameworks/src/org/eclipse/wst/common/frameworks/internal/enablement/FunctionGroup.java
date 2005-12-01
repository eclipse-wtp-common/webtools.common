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
package org.eclipse.wst.common.frameworks.internal.enablement;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;


/**
 * @author mdelder/blancett
 */
public class FunctionGroup implements Comparable {

	public static final String GROUP_NAME_ATTR = "name"; //$NON-NLS-1$
	public static final String GROUP_DESC_ATTR = "description"; //$NON-NLS-1$
	public static final String GROUP_ENABLED_BY_DEFAULT_ATTR = "enabledByDefault"; //$NON-NLS-1$
	public static final String GROUP_PRIORITY_ATTR = "priority"; //$NON-NLS-1$
	public static final String GROUP_INTIALIZER_CLASS_ATTR = "initializerClassName"; //$NON-NLS-1$
	private static final int NEGATIVE_PRIORITY = -1;

	private String groupID;
	private String name;
	private String description;
	private String intializerClassName;
	private String declaringExtension;
	private int priority = NEGATIVE_PRIORITY;
	private IConfigurationElement element;
	private IGroupInitializer groupInterface;
	private Set functionGroupPatternBindings;
//	private transient FunctionGroupPatternBinding[] functionGroupPatternBindingsAsArray;
	private boolean errorReported = false;

	public FunctionGroup(String groupID, IConfigurationElement element) {
		this.groupID = groupID;
		this.element = element;
	}

	public String getDescription() {
		if (description == null)
			description = element.getAttribute(GROUP_DESC_ATTR);
		return description;
	}

	String getInitalizerClassName() {
		if (intializerClassName == null)
			intializerClassName = element.getAttribute(GROUP_INTIALIZER_CLASS_ATTR);
		return intializerClassName;
	}

	public String getName() {
		if (name == null)
			name = element.getAttribute(GROUP_NAME_ATTR);
		return name;
	}

	private String getDeclaringExtensionName() {
		if (declaringExtension == null) {
			if (element.getDeclaringExtension() == null)
				return ""; //$NON-NLS-1$
			declaringExtension = element.getDeclaringExtension().toString();
		}
		return declaringExtension;
	}

	public String getGroupID() {
		return groupID;
	}

	IGroupInitializer getInitializerClass() {
		if (groupInterface == null)
			try {
				groupInterface = (IGroupInitializer) element.createExecutableExtension(GROUP_INTIALIZER_CLASS_ATTR);
			} catch (CoreException e) {
				Logger.getLogger().logError(WTPResourceHandler.getString("29", new Object[]{GROUP_INTIALIZER_CLASS_ATTR, getInitalizerClassName(), getDeclaringExtensionName()}) + "\r\n"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-4$
				Logger.getLogger().logError(e);
			}
		return groupInterface;
	}

	public String toString() {
		return "\"" + getName() + "\" (" + getGroupID() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public int getPriority() {
		if (priority == NEGATIVE_PRIORITY)
			priority = Integer.parseInt(element.getAttribute(GROUP_PRIORITY_ATTR));
		return priority;
	}

	public boolean isEnabled(IProject project) {
		if (getInitializerClass() != null)
			return getInitializerClass().isGroupEnabled(project);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (this.equals(o))
			return 0;
		else if (!(o instanceof FunctionGroup))
			return 1;

		FunctionGroup group = (FunctionGroup) o;
		if (getPriority() == group.getPriority())
			return getGroupID().compareTo(group.getGroupID());

		else if (getPriority() < group.getPriority())
			return -1;
		else
			return 1;
	}

	public boolean isMatch(String string) {
		if (functionGroupPatternBindings == null) {
			if (!errorReported) {
				Logger.getLogger().logError(WTPResourceHandler.getString("30", new Object[]{getGroupID()})); //$NON-NLS-1$
				errorReported = true;
			}
			return false;
		}
		for (Iterator iterator = functionGroupPatternBindings.iterator(); iterator.hasNext();) {
			FunctionGroupPatternBinding functionGroupPatternBinding = (FunctionGroupPatternBinding) iterator.next();

			if (functionGroupPatternBinding.getPattern().matcher(string).matches())
				return true;
		}

		return false;
	}

	boolean setFunctionGroupPatternBindings(Set functionGroupPatternBindings) {
		functionGroupPatternBindings = Util.safeCopy(functionGroupPatternBindings, FunctionGroupPatternBinding.class);

		if (!Util.equals(functionGroupPatternBindings, this.functionGroupPatternBindings)) {
			this.functionGroupPatternBindings = functionGroupPatternBindings;
//			this.functionGroupPatternBindingsAsArray = (FunctionGroupPatternBinding[]) this.functionGroupPatternBindings.toArray(new FunctionGroupPatternBinding[this.functionGroupPatternBindings.size()]);

			return true;
		}

		return false;
	}

}
