/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Apr 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.modulecore.internal.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.internal.emf.resource.Translator;
import org.eclipse.wst.common.modulecore.DependencyType;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;

/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DependencyTypeTranslator extends Translator implements WTPModulesXmlMapperI{
	
	public DependencyTypeTranslator(){
		super(DEPENDENCY_TYPE,ModuleCorePackage.eINSTANCE.getDependentModule_DependencyType());
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.etools.emf2xml.impl.Translator#convertStringToValue(java.lang.String, org.eclipse.emf.ecore.EObject)
	 */
	public Object convertStringToValue(String strValue, EObject owner) {
		String correct = strValue;
		if (strValue.toUpperCase().equals("CONSUMES")) //$NON-NLS-1$
			correct = DependencyType.CONSUMES_LITERAL.getName(); 
		else if (strValue.toUpperCase().equals("USES")) //$NON-NLS-1$
			correct = DependencyType.USES_LITERAL.getName(); 
			
		return super.convertStringToValue(correct, owner);
	}

}
