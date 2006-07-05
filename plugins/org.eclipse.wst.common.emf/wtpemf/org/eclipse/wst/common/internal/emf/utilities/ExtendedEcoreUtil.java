/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;



public class ExtendedEcoreUtil extends EcoreUtil {
	private static FileNotFoundDetector FILE_NOT_FOUND_DETECTOR;
	private static String NAME_ATTRIBUTE_STRING = "name"; //$NON-NLS-1$


	public interface FileNotFoundDetector {
		boolean isFileNotFound(WrappedException wrappedEx);
	}

	public static void setFileNotFoundDetector(FileNotFoundDetector detector) {
		FILE_NOT_FOUND_DETECTOR = detector;
	}

	public static FileNotFoundDetector getFileNotFoundDetector() {
		if (FILE_NOT_FOUND_DETECTOR == null) {
			FILE_NOT_FOUND_DETECTOR = new FileNotFoundDetector() {
				public boolean isFileNotFound(WrappedException wrappedEx) {
					if (getInnerFileNotFoundException(wrappedEx) != null)
						return true;
					return false;
				}
			};
		}
		return FILE_NOT_FOUND_DETECTOR;
	}

	public static FileNotFoundException getInnerFileNotFoundException(WrappedException wrappedEx) {
		if (wrappedEx.exception() instanceof java.io.FileNotFoundException) {
			return (FileNotFoundException) wrappedEx.exception();
		} else if (wrappedEx.exception() instanceof WrappedException) {
			return getInnerFileNotFoundException((WrappedException) wrappedEx.exception());
		}
		return null;
	}

	public static void eSetOrAdd(EObject obj, EStructuralFeature feature, Object value) {
		eSetOrAdd(obj, feature, value, 0);
	}

	public static void eSetOrAdd(EObject obj, EStructuralFeature feature, Object value, int newIndex) {
		if (feature.isMany() && value != null) {
			if (newIndex >= 0)
				((List) obj.eGet(feature)).add(newIndex, value);
			else
				((List) obj.eGet(feature)).add(value);
		} else {
			obj.eSet(feature, value);
		}
	}

	public static void eUnsetOrRemove(EObject obj, EStructuralFeature feature, Object value) {
		if (feature == null || feature.isMany())
			((List) obj.eGet(feature)).remove(value);
		else
			obj.eUnset(feature);
	}

	public static boolean endsWith(URI sourceUri, URI testUri) {
		//TODO Waiting on new emf URI API
		String[] sourceSegments = sourceUri.segments();
		String[] testSegments = testUri.segments();
		int i = testSegments.length;
		int j = sourceSegments.length;
		if (j >= i) {
			boolean test = true;

			while (test && i > 0) {
				i--;
				j--;
				test = testSegments[i].equals(sourceSegments[j]);
			}
			return test;
		}
		return false;
	}

	public static String getName(EObject obj) {
		if (obj == null)
			return null;
		if (obj instanceof ENamedElement)
			return ((ENamedElement) obj).getName();
		List allAtts = obj.eClass().getEAllAttributes();
		int size = allAtts.size();
		EAttribute att, nameAttribute = null;
		for (int i = 0; i < size; i++) {
			att = (EAttribute) allAtts.get(i);
			if (NAME_ATTRIBUTE_STRING.equals(att.getName())) {
				nameAttribute = att;
				break;
			}
		}
		if (nameAttribute != null)
			return (String) obj.eGet(nameAttribute);
		return null;
	}

	/**
	 * @deprecated No longer needed.
	 */
	public static void addLoadingTag(Resource resource) {
		//resource.eAdapters().add(IsLoadingDetector.INSTANCE);
	}

	/**
	 * @deprecated No longer needed.
	 */
	public static void removeLoadingTag(Resource resource) {
		//resource.eAdapters().remove(IsLoadingDetector.INSTANCE);
	}

	/**
	 * Return true if the resource is currently being loaded. This is determined by checking for the
	 * IsLoadingDector in the list of eAdapters on the resource.
	 * 
	 * @param resource
	 * @return boolean
	 * 
	 * @see addLoadingTag(Resource)
	 * @see removeLoadingTag(Resource)
	 *  @deprecated use {@link Resource.Internal#isLoading()}
	 */
	public static boolean isLoading(Resource resource) {
		return resource != null ? ((Resource.Internal)resource).isLoading() : false; 
	}

	public static Adapter createAdapterForLoading(Adapter targetAdapter, EObject targetObject) {
		Resource res = targetObject.eResource();
		if (res == null || isLoading(res))
			return new IsLoadingProxyAdapter(targetAdapter, targetObject);
		return targetAdapter;
	}

	public static void preRegisterPackage(String nsPrefix, EPackage.Descriptor descriptor) {
		if (!EPackage.Registry.INSTANCE.containsKey(nsPrefix))
			EPackage.Registry.INSTANCE.put(nsPrefix, descriptor);
	}
	/**
	   * Returns the adapter of the specified type.
	 * @param anObject 
	   * @param adapters list of adapters to search.
	   * @param type the type of adapter.
	   * @return an adapter from the list or null.
	   */
	  public static Adapter getAdapter(EObject anObject, List adapters, Object type) {

		synchronized (adapters) {

			for (int i = 0, size = adapters.size(); i < size; ++i) {
				Adapter adapter = (Adapter) adapters.get(i);
				if (adapter.isAdapterForType(type)) {
					return adapter;
				}
			}
			return null;
		}
	}

	/**
	 * Remove this object from it's container, and make it and all it's children
	 * proxies
	 */
	public static void unload(EObject root) {
		Resource res = root.eResource();
		EObject container = root.eContainer();
		/* Making sure the proxy is created first before unloading */
		if (root != null && res != null)
			becomeProxy(root, res);
		if (container == null)
			if (res != null)
				res.getContents().remove(root);
			else {
				EStructuralFeature feature = root.eContainmentFeature();
				if (feature != null)
					eUnsetOrRemove(container, feature, root);
			}
		
	}

	/**
	 * Turns this object and all it's children to proxies, and removes adapters precondition: The
	 * object has been removed from it's container
	 */
	public static void becomeProxy(EObject root, Resource res) {
		Iterator iter = root.eAllContents();
		while (iter.hasNext()) {
			doBecomeProxy((InternalEObject) iter.next(), res);
		}
		doBecomeProxy((InternalEObject) root, res);
	}

	protected static void doBecomeProxy(InternalEObject p, Resource res) {
		String id = res.getURIFragment(p);
		p.eSetProxyURI(res.getURI().appendFragment(id));
		if (res instanceof XMLResource) {
			((XMLResource) res).setID(p,null);
		}
		p.eAdapters().clear();
	}

	public static void removeProxy(EObject root, Resource res) {
		Iterator iter = root.eAllContents();
		while (iter.hasNext()) {
			doRemoveProxy((InternalEObject) iter.next(), res);
		}
		doRemoveProxy((InternalEObject) root, res);
	}

	protected static void doRemoveProxy(InternalEObject p, Resource res) {
		String id = p.eProxyURI().fragment();
		if (res instanceof XMLResource) {
			((XMLResource) res).setID(p, id);
		}
		p.eSetProxyURI(null);
	}


	/**
	 * Ensures the passed object is not a proxy; if it is, throws an exception indicating the bad
	 * HREF. If the object is null, does nothing.
	 * 
	 * @param object
	 * @throws DanglingHREFException
	 */
	public static void checkProxy(EObject object) throws DanglingHREFException {
		if (object == null || !object.eIsProxy())
			return;
		String msg = WFTUtilsResourceHandler.getString(WFTUtilsResourceHandler.DANGLING_HREF_ERROR_, new Object[]{((InternalEObject) object).eProxyURI()});
		throw new DanglingHREFException(msg);
	}

	/**
	 * Return true if o1 and o2 are not the same values. This implementation takes into account that
	 * either parameter can be null.
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean valueChanged(Object o1, Object o2) {
		return (o1 == null ^ o2 == null) || (o1 == null || !o1.equals(o2));
	}

}