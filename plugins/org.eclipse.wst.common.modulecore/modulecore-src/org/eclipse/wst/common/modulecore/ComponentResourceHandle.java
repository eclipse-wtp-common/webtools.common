/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class ComponentResourceHandle implements ComponentResource {

	private ComponentResource componentResource;
	private ResourceSet resourceSet;

	public ComponentResourceHandle(ResourceSet aContext) {
		resourceSet = aContext;
	}

	public EList getExclusions() {
		return getComponentResource().getExclusions();
	}


	public URI getSourcePath() {
		return getComponentResource().getSourcePath();
	}


	public void setSourcePath(URI value) {
		getComponentResource().setSourcePath(value);
	}

	public EClass eClass() {
		return getComponentResource().eClass();
	}

	public Resource eResource() {
		return getComponentResource().eResource();
	}

	public EObject eContainer() {
		return getComponentResource().eContainer();
	}

	public EStructuralFeature eContainingFeature() {
		return getComponentResource().eContainingFeature();
	}

	public EReference eContainmentFeature() {
		return getComponentResource().eContainmentFeature();
	}

	public EList eContents() {
		return getComponentResource().eContents();
	}

	public TreeIterator eAllContents() {
		return getComponentResource().eAllContents();
	}

	public boolean eIsProxy() {
		return getComponentResource().eIsProxy();
	}

	public EList eCrossReferences() {
		return getComponentResource().eCrossReferences();
	}

	public Object eGet(EStructuralFeature feature) {
		return getComponentResource().eGet(feature);
	}

	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return getComponentResource().eGet(feature, resolve);
	}

	public void eSet(EStructuralFeature feature, Object newValue) {
		getComponentResource().eSet(feature, newValue);
	}

	public boolean eIsSet(EStructuralFeature feature) {
		return getComponentResource().eIsSet(feature);
	}

	public void eUnset(EStructuralFeature feature) {
		getComponentResource().eUnset(feature);
	}

	public EList eAdapters() {
		return getComponentResource().eAdapters();
	}

	public boolean eDeliver() {
		return getComponentResource().eDeliver();
	}

	public void eSetDeliver(boolean deliver) {
		getComponentResource().eSetDeliver(deliver);
	}

	public void eNotify(Notification notification) {
		getComponentResource().eNotify(notification);
	}

	protected ComponentResource getComponentResource() {
		if (componentResource.eIsProxy()) {
			ComponentResource resolvedObject = (ComponentResource) EcoreUtil.resolve(componentResource, resourceSet);
			if (resolvedObject == componentResource) {
				// TODO Handle the case where the proxy fails to resolve
			} else {
				componentResource = resolvedObject;
			}
		}
		return componentResource;
	}

	public WorkbenchComponent getComponent() {
		return getComponentResource().getComponent();
	}

	public void setComponent(WorkbenchComponent aComponent) {
		getComponentResource().setComponent(aComponent);
	}

	public URI getRuntimePath() {
		return getComponentResource().getRuntimePath();
	}

	public void setRuntimePath(URI value) {
		getComponentResource().setRuntimePath(value);

	}



}
