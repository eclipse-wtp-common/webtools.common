package org.eclipse.wst.common.internal.emfworkbench;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.emf.ecore.EObject;

public class EmfPackagePropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver == null || !(receiver instanceof EObject) || expectedValue == null || !(expectedValue instanceof String))
			return false;
		
		EObject eObject = (EObject) receiver;
		String emfPackage = (String)expectedValue;
		return emfPackage.equals(eObject.eClass().getEPackage().getNsURI());
	}

}
