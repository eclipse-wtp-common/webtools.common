/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.model.FilterRule;

public class AdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof Validator.V2)return _valAdaptor;
		if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof FilterGroup)return _fgAdaptor;
		if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof FilterRule)return _ruleAdaptor;

		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

	private IWorkbenchAdapter _valAdaptor = new IWorkbenchAdapter() {

		public Object[] getChildren(Object o) {
			return ((Validator.V2) o).getGroups();
		}

		public ImageDescriptor getImageDescriptor(Object o) {
			return null;
		}

		public String getLabel(Object o) {
			return ((Validator.V2) o).getName();
		}

		public Object getParent(Object o) {
			return null;
		}

	};

	private IWorkbenchAdapter _fgAdaptor = new IWorkbenchAdapter() {

		public Object[] getChildren(Object o) {
			return ((FilterGroup) o).getRules();
		}

		public ImageDescriptor getImageDescriptor(Object o) {
			return null;
		}

		public String getLabel(Object o) {
			FilterGroup fg = (FilterGroup) o;
			return fg.getDisplayableType();
		}

		public Object getParent(Object o) {
			return null;
		}
	};

	private IWorkbenchAdapter _ruleAdaptor = new IWorkbenchAdapter() {

		public Object[] getChildren(Object o) {
			return new Object[0];
		}

		public ImageDescriptor getImageDescriptor(Object o) {
			return null;
		}

		public String getLabel(Object o) {
			FilterRule rule = (FilterRule) o;
			return rule.getName();
		}

		public Object getParent(Object o) {
			return null;
		}

	};

}
