/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;
import org.eclipse.wst.common.internal.emf.utilities.IDUtil;

public class MappedXMIHelper extends XMLHelperImpl {

	private static final String WORKSPACE_PROTOCOL = "workspace:/"; //$NON-NLS-1$
	private static final String PLATFORM_RESOURCE_PROTOCOL = "platform:/resource/"; //$NON-NLS-1$
	private static final EStructuralFeature NULL_FEATURE = new UnsupportedFeature();
	protected boolean usingMaps = true;
	protected Map cachedRelativeURIs;
	protected Map packageURIsToPrefixes;

	/**
	 * Constructor for MappedXMLHelper.
	 * 
	 * @param resource
	 */
	public MappedXMIHelper(XMLResource resource, Map prefixesToURIs) {
		super(resource);
		this.prefixesToURIs.putAll(prefixesToURIs);
		pushContext(); //Needed to initialize the context to zero
		Set keys = prefixesToURIs.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			String prefix = (String) iter.next();
			String uri = (String) prefixesToURIs.get(prefix);
			namespaceSupport.declarePrefix(prefix, uri);
		}

	}

	@Override
	public URI resolve(URI relative, URI base) {
		URI resolved = null;
		boolean isMapped = false;
		ResourceSet set = getResource().getResourceSet();
		if (set != null) {
			URI localresourceURI = null;
			if (relative.hasFragment())
				localresourceURI = relative.trimFragment();
			else
				localresourceURI = relative;
			isMapped = !(((URIConverterImpl.URIMap) set.getURIConverter().getURIMap()).getURI(localresourceURI).equals(localresourceURI));
		}
		if (!isMapped) {
			if (isUsingContainerRelativePaths() && set != null)
				resolved = set.getURIConverter().normalize(relative);
		} else {
			resolved = relative;
		}
		return resolved == null ? super.resolve(relative, base) : resolved;
	}

	/**
	 * Method isUsingContainerRelativePaths.
	 * 
	 * @return boolean
	 */
	private boolean isUsingContainerRelativePaths() {
		return ((CompatibilityXMIResource) resource).getFormat() == CompatibilityXMIResource.FORMAT_MOF5 || usingMaps;
	}

	@Override
	public void addPrefix(String prefix, String uri) {
		/*
		 * problem - the incoming key value pair is ejbbnd->ejbbnd.xmi; however, the map already has
		 * a key value pair of ejbbnd->http:///ejbbnd.ecore
		 */
		if (uri.endsWith(".ecore")) { //$NON-NLS-1$
			usingMaps = false;
		}
		String existing = (String) prefixesToURIs.get(prefix);
		if (existing == null) {
			prefixesToURIs.put(prefix, uri);
			namespaceSupport.declarePrefix(prefix, uri);
		} else if (!existing.equals(uri)) {
			getCompatibilityResource().addOriginalPackageURI(existing, uri);
			getCompatibilityResource().setFormat(CompatibilityXMIResource.FORMAT_MOF5);
		}
	}

	protected CompatibilityXMIResource getCompatibilityResource() {
		return (CompatibilityXMIResource) getResource();
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl#getHREF(EObject)
	 */
	@Override
	public String getHREF(EObject obj) {
		if (!getCompatibilityResource().usesDefaultFormat()) {
			URIConverter conv = getURIConverter();
			if (conv != null && conv instanceof CompatibilityURIConverter) {
				String href = getCompatibliltyHREF(obj, (CompatibilityURIConverter) conv);
				return useWorkspaceProtocolIfNecessary(href);
			}
		}
		return super.getHREF(obj);
	}


	/**
	 * @param href
	 * @return
	 */
	protected String useWorkspaceProtocolIfNecessary(String href) {
		if (href != null && href.startsWith(PLATFORM_RESOURCE_PROTOCOL))
			return WORKSPACE_PROTOCOL + href.substring(19);
		return href;
	}

	private String getCompatibliltyHREF(EObject obj, CompatibilityURIConverter conv) {
		//Implementation copied from super.getHREF(EObject)
		InternalEObject o = (InternalEObject) obj;

		URI objectURI = o.eProxyURI();
		if (objectURI == null) {
			Resource otherResource = obj.eResource();
			if (otherResource == null) {
				objectURI = handleDanglingHREF(obj);
				if (objectURI == null) {
					return null;
				}
			} else
				objectURI = otherResource.getURI().appendFragment(otherResource.getURIFragment(obj));
		}

		//Modified to dispatch back to URI Converter
		if (!objectURI.isRelative()) {
			objectURI = makeRelative(objectURI, conv);
		}
		return objectURI.toString();
	}

	protected URI makeRelative(URI objectURI, CompatibilityURIConverter conv) {
		String fragment = objectURI.fragment();
		objectURI = objectURI.trimFragment();
		URI relative = (URI) getCachedRelativeURIs().get(objectURI);
		if (relative == null) {
			relative = conv.deNormalize(objectURI);
			if (relative.isRelative())
				cachedRelativeURIs.put(objectURI, relative);
		}
		return relative.appendFragment(fragment);
	}

	protected URIConverter getURIConverter() {
		ResourceSet set = getResource().getResourceSet();
		if (set != null)
			return set.getURIConverter();
		return null;
	}

	protected Map getCachedRelativeURIs() {
		if (cachedRelativeURIs == null)
			cachedRelativeURIs = new HashMap();
		return cachedRelativeURIs;
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl#getID(EObject)
	 */
	@Override
	public String getID(EObject obj) {
		if (getCompatibilityResource().usesDefaultFormat())
			return super.getID(obj);
		return IDUtil.getOrAssignID(obj, resource);
	}

	public String[] getNSInfo(EPackage pkg) {
		String prefix = getMappedPrefix(pkg);
		if (prefix == null)
			return new String[]{pkg.getNsPrefix(), pkg.getNsURI()};
		return new String[]{prefix, prefix + ".xmi"}; //$NON-NLS-1$
	}

	protected String getMappedPrefix(EPackage pkg) {
		String nsURI = pkg.getNsURI();
		if (usingMaps || !getCompatibilityResource().usesDefaultFormat())
			return (String) packageURIsToPrefixes.get(nsURI);
		return null;
	}

	protected String getMappedPrefixOrDefault(EPackage pkg) {
		String prefix = getMappedPrefix(pkg);
		return prefix == null ? pkg.getNsPrefix() : prefix;
	}


	/**
	 * Returns the packageURIsToPrefixes.
	 * 
	 * @return Map
	 */
	public Map getPackageURIsToPrefixes() {
		return packageURIsToPrefixes;
	}

	/**
	 * Sets the packageURIsToPrefixes.
	 * 
	 * @param packageURIsToPrefixes
	 *            The packageURIsToPrefixes to set
	 */
	public void setPackageURIsToPrefixes(Map packageURIsToPrefixes) {
		this.packageURIsToPrefixes = packageURIsToPrefixes;
	}

	@Override
	public String getQName(EClass c) {
		String name = getName(c);

		if (xmlMap != null) {
			XMLResource.XMLInfo clsInfo = xmlMap.getInfo(c);

			if (clsInfo != null) {
				String targetNamespace = clsInfo.getTargetNamespace();
				return getQName(targetNamespace, name);
			}
		}

		EPackage p = c.getEPackage();
		packages.put(p, null);

		if (p.getNsPrefix().equals("")) //$NON-NLS-1$ 
			return name;

		//Modified from superclass
		//return p.getNsPrefix() + ":" + name;
		return getMappedPrefixOrDefault(p) + ":" + name; //$NON-NLS-1$ 
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl#setValue(EObject, EStructuralFeature,
	 *      Object, int)
	 */
	@Override
	public void setValue(EObject object, EStructuralFeature feature, Object value, int position) {
		if (feature == NULL_FEATURE)
			return;
		if (!feature.isTransient()) {
			if (value == null && feature.getEType().getInstanceClass() != null && feature.getEType().getInstanceClass().isPrimitive())
				//For compatibility with MOF5 where types like Integer might have been set
				//and serialized as "xsi:nil"
				return;

			super.setValue(object, feature, value, position);
		}
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.xmi.XMLHelper#getFeature(org.eclipse.emf.ecore.EClass,
	 *      java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public EStructuralFeature getFeature(EClass eClass, String namespaceURI, String name, boolean isElement) {
		if (UnsupportedFeature.isUnsupported(eClass, name))
			return NULL_FEATURE;
		return super.getFeature(eClass, namespaceURI, name, isElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.xmi.XMLHelper#getFeatureKind(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public int getFeatureKind(EStructuralFeature feature) {
		if (feature == NULL_FEATURE)
			return XMLHelper.DATATYPE_SINGLE;
		return super.getFeatureKind(feature);
	}
}
