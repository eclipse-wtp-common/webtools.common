/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMISaveImpl;

public class CompatibilityXMISaveImpl extends XMISaveImpl {

	/**
	 * Constructor for CompatibilityXMISaveImpl.
	 * 
	 * @param helper
	 */
	public CompatibilityXMISaveImpl(XMLHelper helper) {
		super(helper);
	}

	/**
	 * Constructor for CompatibilityXMISaveImpl.
	 * 
	 * @param options
	 * @param helper
	 * @param encoding
	 */
	public CompatibilityXMISaveImpl(Map options, XMLHelper helper, String encoding) {
		super(options, helper, encoding);
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMISaveImpl#init(XMLResource, Map)
	 */
	@Override
	protected void init(XMLResource resource, Map options) {
		super.init(resource, options);
		xmiType = true;
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMISaveImpl#addNamespaceDeclarations()
	 */
	@Override
	public void addNamespaceDeclarations() {
		doc.addAttribute(XMI_VER_NS, XMIResource.VERSION_VALUE);
		doc.addAttribute(XMI_XMLNS, XMIResource.XMI_URI);
		EPackage[] packages = helper.packages();
		StringBuffer xsiSchemaLocation = null;
		if (declareSchemaLocation) {
			for (int i = 0; i < packages.length; i++) {
				EPackage ePackage = packages[i];

				EObject root = EcoreUtil.getRootContainer(ePackage);
				if (root instanceof EPackage) {
					EPackage rootEPackage = (EPackage) root;
					Resource resource = rootEPackage.eResource();
					if (resource != null) {
						URI uri = resource.getURI();
						String rootNsURI = rootEPackage.getNsURI();
						if (uri == null ? rootNsURI != null : !uri.toString().equals(rootNsURI)) {
							declareXSI = true;
							if (xsiSchemaLocation == null) {
								xsiSchemaLocation = new StringBuffer();
							} else {
								xsiSchemaLocation.append(' ');
							}
							xsiSchemaLocation.append(rootNsURI);
							xsiSchemaLocation.append(' ');
							xsiSchemaLocation.append(helper.getHREF(rootEPackage));
						}
					}
				}
			}
		}

		if (declareXSI) {
			doc.addAttribute(XSI_XMLNS, XMLResource.XSI_URI);
		}

		for (int i = 0; i < packages.length; i++) {
			EPackage ePackage = packages[i];
			//Modified from superclass - dispatch back to helper for compatibility
			//String nsURI = ePackage.getNsURI();
			//String nsPrefix = ePackage.getNsPrefix();
			String[] nsInfo = ((MappedXMIHelper) helper).getNSInfo(ePackage);
			doc.addAttributeNS(XMLResource.XML_NS, nsInfo[0], nsInfo[1]);
		}

		if (xsiSchemaLocation != null) {
			doc.addAttribute(XSI_SCHEMA_LOCATION, xsiSchemaLocation.toString());
		}
	}

	@Override
	protected void saveHref(EObject remote, EStructuralFeature f) {
		String name = helper.getQName(f);
		String href = helper.getHREF(remote);
		if (href != null) {
			doc.startElement(name);
			EClass eClass = remote.eClass();

			EClass expectedType = (EClass) f.getEType();
			//Changed next line to always write type if expectedType
			//is different from eClass
			if (eClass != expectedType) {
				saveTypeAttribute(eClass);
			}

			doc.addAttribute(XMLResource.HREF, href);
			doc.endEmptyElement();
		}
	}

}