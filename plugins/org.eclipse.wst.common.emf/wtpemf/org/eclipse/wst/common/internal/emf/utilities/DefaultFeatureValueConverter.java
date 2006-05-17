/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;



import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * Insert the type's description here. Creation date: (5/10/2001 2:49:49 PM)
 * 
 * @author: Administrator
 */
public class DefaultFeatureValueConverter implements FeatureValueConverter {
	private static final String FAILED_CONVERSION_PATTERN = WFTUtilsResourceHandler.Failed_to_convert__0__to___ERROR_;

	/**
	 * DefaultFeatureValueConverter constructor comment.
	 */
	public DefaultFeatureValueConverter() {
		super();
	}

	/**
	 * Convert
	 * 
	 * @aValue to a Boolean.
	 */
	protected Object convertToBoolean(Object aValue) {
		if (aValue instanceof String)
			return Boolean.valueOf((String) aValue);
		if (aValue instanceof Boolean)
			return aValue;
		return failedToConvert(aValue, WFTUtilsResourceHandler.Boolean_UI_);
	}

	/**
	 * Convert
	 * 
	 * @aValue to a Byte.
	 */
	protected Object convertToByte(Object aValue) {
		if (aValue instanceof String)
			return Byte.valueOf((String) aValue);
		if (aValue instanceof Byte)
			return aValue;
		return failedToConvert(aValue, WFTUtilsResourceHandler.Byte_UI_);
	}

	/**
	 * Convert
	 * 
	 * @aValue to a Character.
	 */
	protected Object convertToCharacter(Object aValue) {
		if (aValue instanceof String && ((String) aValue).length() == 1)
			return new Character(((String) aValue).charAt(0));
		if (aValue instanceof Character)
			return aValue;
		return failedToConvert(aValue, WFTUtilsResourceHandler.Character_UI_);
	}

	/**
	 * Convert
	 * 
	 * @aValue to a Double.
	 */
	protected Object convertToDouble(Object aValue) {
		if (aValue instanceof String) {
			try {
				return Double.valueOf((String) aValue);
			} catch (NumberFormatException e) {
				return defaultDouble();
			}
		}
		if (aValue instanceof Double)
			return aValue;
		return failedToConvert(aValue, WFTUtilsResourceHandler.Double_UI_);
	}

	/**
	 * Convert
	 * 
	 * @aValue to a EEnumLiteral.
	 */
	protected Object convertToEnum(Object aValue, EStructuralFeature aFeature) {
		EEnumLiteral literal = null;
		EEnum eenum = (EEnum) aFeature.getEType();
		if (aValue instanceof String)
			literal = eenum.getEEnumLiteral((String) aValue);
		else if (aValue instanceof Integer)
			literal = eenum.getEEnumLiteral(((Integer) aValue).intValue());
		else if (aValue instanceof EEnumLiteral)
			literal = (EEnumLiteral) aValue;

		if (literal == null)
			return failedToConvert(aValue, WFTUtilsResourceHandler.Enumeration_UI_);

		return literal.getInstance();
	}

	/**
	 * Convert
	 * 
	 * @aValue to a Float.
	 */
	protected Object convertToFloat(Object aValue) {
		if (aValue instanceof String) {
			try {
				return Float.valueOf((String) aValue);
			} catch (NumberFormatException e) {
				return defaultFloat();
			}
		}
		if (aValue instanceof Float)
			return aValue;
		return failedToConvert(aValue, WFTUtilsResourceHandler.Float_UI_);
	}

	/**
	 * Convert
	 * 
	 * @aValue to a Integer.
	 */
	protected Object convertToInteger(Object aValue) {
		if (aValue instanceof String) {
			try {
				return Integer.valueOf((String) aValue);
			} catch (NumberFormatException e) {
				return defaultInteger();
			}
		}
		if (aValue instanceof Integer)
			return aValue;
		return failedToConvert(aValue, WFTUtilsResourceHandler.Integer_UI_);
	}

	/**
	 * Convert
	 * 
	 * @aValue to a Java Object.
	 */
	protected Object convertToJavaObject(Object aValue) {
		return aValue;
	}

	/**
	 * Convert
	 * 
	 * @aValue to a Long.
	 */
	protected Object convertToLong(Object aValue) {
		if (aValue instanceof String) {
			try {
				return Long.valueOf((String) aValue);
			} catch (NumberFormatException e) {
				return defaultLong();
			}
		}
		if (aValue instanceof Long)
			return aValue;
		return failedToConvert(aValue, WFTUtilsResourceHandler.Long_UI_);
	}

	/**
	 * Convert
	 * 
	 * @aValue to a MofObject.
	 */
	protected Object convertToMofObject(Object aValue) {
		if (aValue instanceof EObject)
			return aValue;
		return failedToConvert(aValue, WFTUtilsResourceHandler.MofObject_UI_);
	}

	/**
	 * Convert
	 * 
	 * @aValue to a Short.
	 */
	protected Object convertToShort(Object aValue) {
		if (aValue instanceof String) {
			try {
				return Short.valueOf((String) aValue);
			} catch (NumberFormatException e) {
				return defaultShort();
			}
		}
		if (aValue instanceof Short)
			return aValue;
		return failedToConvert(aValue, WFTUtilsResourceHandler.Short_UI_);
	}

	/**
	 * Convert
	 * 
	 * @aValue to a String.
	 */
	protected Object convertToString(Object aValue) {
		return aValue.toString();
	}

	/**
	 * Convert
	 * 
	 * @aValue to the type of
	 * @anAttribute.
	 */
	protected Object convertValue(Object aValue, org.eclipse.emf.ecore.EAttribute anAttribute) {

		EClassifier meta = anAttribute.getEType();
		if (meta.eClass() == EcorePackage.eINSTANCE.getEEnum())
			return convertToEnum(aValue, anAttribute);
		switch (meta.getClassifierID()) {
			case EcorePackage.ESTRING :
				return convertToString(aValue);
			case EcorePackage.EBOOLEAN_OBJECT :
			case EcorePackage.EBOOLEAN :
				return convertToBoolean(aValue);
			case EcorePackage.EINTEGER_OBJECT :
			case EcorePackage.EINT :
				return convertToInteger(aValue);
			case EcorePackage.EFLOAT_OBJECT :
			case EcorePackage.EFLOAT :
				return convertToFloat(aValue);
			case EcorePackage.ECHARACTER_OBJECT :
			case EcorePackage.ECHAR :
				return convertToCharacter(aValue);
			case EcorePackage.ELONG_OBJECT :
			case EcorePackage.ELONG :
				return convertToLong(aValue);
			case EcorePackage.EBYTE_OBJECT :
			case EcorePackage.EBYTE :
				return convertToByte(aValue);
			case EcorePackage.EDOUBLE_OBJECT :
			case EcorePackage.EDOUBLE :
				return convertToDouble(aValue);
			case EcorePackage.ESHORT_OBJECT :
			case EcorePackage.ESHORT :
				return convertToShort(aValue);
			//		case EcorePackage.EENUM:
			//			return convertToEnum(aValue, anAttribute);
			case EcorePackage.EOBJECT :
				return convertToMofObject(aValue);
			case EcorePackage.EJAVA_OBJECT :
				return convertToJavaObject(aValue);
		}
		return aValue;
	}

	/**
	 * Convert
	 * 
	 * @aValue to the type of
	 * @aFeature.
	 */
	public Object convertValue(Object aValue, org.eclipse.emf.ecore.EStructuralFeature aFeature) {
		if (aValue == null || aFeature == null || !(aFeature instanceof EAttribute))
			return aValue; //nothing to convert
		return convertValue(aValue, (EAttribute) aFeature);
	}

	/**
	 * Return the default Double value.
	 */
	protected Double defaultDouble() {
		return null;
	}

	/**
	 * Return the default Float value.
	 */
	protected Float defaultFloat() {
		return null;
	}

	/**
	 * Return the default Integer value.
	 */
	protected Integer defaultInteger() {
		return null;
	}

	/**
	 * Return the default Long value.
	 */
	protected Long defaultLong() {
		return null;
	}

	/**
	 * Return the default Short value.
	 */
	protected Short defaultShort() {
		return null;
	}

	/**
	 * Failed to convert
	 * 
	 * @aValue.
	 */
	protected Object failedToConvert(Object aValue, String aString) {
		String errorString = java.text.MessageFormat.format(FAILED_CONVERSION_PATTERN, new String[]{aValue.toString(), aString});
		throw new FeatureValueConversionException(errorString);
	}
}