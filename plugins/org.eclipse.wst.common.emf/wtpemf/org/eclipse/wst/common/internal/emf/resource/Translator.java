/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emf.utilities.ExtendedEcoreUtil;
import org.eclipse.wst.common.internal.emf.utilities.FeatureValueConverter;
import org.w3c.dom.Node;


public class Translator {

	public final static int NO_STYLE = 0;
	public final static int DOM_ATTRIBUTE = 1;
	public final static int EMPTY_TAG = 1 << 1;
	public final static int CDATA_CONTENT = 1 << 2;
	/**
	 * Style bit that indicates that the end tag should NOT be indented; by default it is.
	 */
	public final static int END_TAG_NO_INDENT = 1 << 3;
	/**
	 * Style bit that indicates that booleans should NOT be converted as "True" and "False"; default
	 * is that they are
	 */
	public final static int BOOLEAN_LOWERCASE = 1 << 4;
	/**
	 * Style bit that indicates an enum value contains hyphens If this is true, then internally the
	 * hyphens are replaced with underscores
	 */
	public final static int ENUM_FEATURE_WITH_HYPHENS = 1 << 5;

	protected final static int OBJECT_MAP = 1 << 6;
	protected final static int BOOLEAN_FEATURE = 1 << 7;

	protected final static int SHARED_REFERENCE = 1 << 8;

	/**
	 * Indicates that the feature may be significant even if it is empty
	 */
	public final static int EMPTY_CONTENT_IS_SIGNIFICANT = 1 << 9;

	/**
	 * Used to indicate that feature is associated with a comment node
	 */
	protected final static int COMMENT_FEATURE = 1 << 10;

	/**
	 * If the value is null, then an eUnset() will be invoked on the feature
	 */
	public final static int UNSET_IF_NULL = 1 << 11;

	/**
	 * Return the element contents as a String if the feature is unresolveable (Used by the
	 * SourceLinkTranslator)
	 */
	public final static int STRING_RESULT_OK = 1 << 12;

	protected String[] fDOMNames;
	protected String fDOMPath = ""; //$NON-NLS-1$
	protected Map readAheadNames;
	protected int fStyle = NO_STYLE;
	protected EStructuralFeature feature;
	protected TranslatorPath[] fTranslatorPaths;
	protected EClass emfClass;
	protected String fNameSpace = ""; //$NON-NLS-1$
	// added by MDE
	protected String domNameAndPath = null;

	/**
	 * Indicates if any of the children of this Translator are themselves DependencyTranslators
	 */
	protected Boolean isDependencyParent;
	protected EStructuralFeature dependencyFeature;
	protected static EcorePackage ECORE_PACKAGE = EcorePackage.eINSTANCE;

	// Use this identifier for the DOMName when the attribute
	// value is to be extracted directly from the text of the node.
	// This is rare, but occurs in the web.xml in the case of a
	// WelcomeFile.
	static final public String TEXT_ATTRIBUTE_VALUE = "$TEXT_ATTRIBUTE_VALUE"; //$NON-NLS-1$

	static final public EStructuralFeature CONTAINER_FEATURE = new ContainerFeature();

	static final public EStructuralFeature ROOT_FEATURE = new RootFeature();

	protected static class ContainerFeature extends EStructuralFeatureImpl {
		protected ContainerFeature() {
			super();
		}
	}

	protected static class RootFeature extends EStructuralFeatureImpl {
		protected RootFeature() {
			super();
		}
	}

	public Translator findChild(String tagName, Object target, int versionID) {

		Translator result = null;
		Translator[] maps = getChildren(target, versionID);

		if (maps != null) {
			for (int i = 0; i < maps.length; i++) {
				Translator map = maps[i];
				if (map.isMapFor(tagName)) {
					result = map;
					break;
				}
			}
		}
		if (result == null) {
			VariableTranslatorFactory factory = getVariableTranslatorFactory();
			if (factory != null && factory.accepts(tagName)) {
				result = factory.create(tagName);
			}
		}
		return result;
	}

	/**
	 * Utility method to string together arrays of children
	 */
	public static Object[] concat(Object[] array1, Object[] array2) {
		Object[] result = (Object[]) java.lang.reflect.Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
		System.arraycopy(array1, 0, result, 0, array1.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}

	public static Object[] concat(Object[] array1, Object object2) {
		Object[] newArray = new Object[]{object2};
		return concat(array1, newArray);
	}

	public static Translator createParentAndTextAttributeTranslator(String domName, EStructuralFeature parentFeature, EStructuralFeature childFeature) {
		GenericTranslator parent = new GenericTranslator(domName, parentFeature, END_TAG_NO_INDENT);
		parent.setChildren(new Translator[]{new Translator(TEXT_ATTRIBUTE_VALUE, childFeature)});
		return parent;
	}

	public Translator(String domNameAndPath, EClass eClass) {
		initializeDOMNameAndPath(domNameAndPath);
		setEMFClass(eClass);
	}

	public Translator(String domNameAndPath, EStructuralFeature aFeature) {
		initializeDOMNameAndPath(domNameAndPath);
		setFeature(aFeature);
	}

	public Translator(String domNameAndPath, EStructuralFeature aFeature, EClass eClass) {
		this(domNameAndPath, aFeature);
		setEMFClass(eClass);
	}

	public Translator(String domNameAndPath, EStructuralFeature aFeature, TranslatorPath path) {
		this(domNameAndPath, aFeature, new TranslatorPath[]{path});
	}

	public Translator(String domNameAndPath, EStructuralFeature aFeature, TranslatorPath[] paths) {
		initializeDOMNameAndPath(domNameAndPath);
		fTranslatorPaths = paths;
		setFeature(aFeature);
	}

	public Translator(String domNameAndPath, EStructuralFeature aFeature, int style) {
		initializeDOMNameAndPath(domNameAndPath);
		fStyle = style;
		setFeature(aFeature);
	}

	public static EcorePackage getEcorePackage() {
		return EcorePackage.eINSTANCE;
	}

	public String getDOMName(Object value) {
		return fDOMNames[0];
	}

	public String[] getDOMNames() {
		return fDOMNames;
	}

	public String getDOMPath() {
		return fDOMPath;
	}

	public boolean hasDOMPath() {
		return fDOMPath != null && fDOMPath.length() != 0;
	}

	public EStructuralFeature getFeature() {
		return feature;
	}

	/**
	 * Parse the DOM names and path out of <domNameAndPath>and set the appropriate fields.
	 */
	protected void initializeDOMNameAndPath(String domNameAndPathArg) {
		if (domNameAndPathArg == null)
			return;
		int inx = domNameAndPathArg.lastIndexOf('/');
		if (inx != -1) {
			fDOMNames = parseDOMNames(domNameAndPathArg.substring(inx + 1));
			fDOMPath = domNameAndPathArg.substring(0, inx);
		} else {
			fDOMNames = parseDOMNames(domNameAndPathArg);
			fDOMPath = ""; //$NON-NLS-1$
		}
		// added by MDE
		this.domNameAndPath = domNameAndPathArg;

	}

	/**
	 * Indicates whether the node should be written as an empty tag; eg, <distributable/>
	 */
	public boolean isCDATAContent() {
		return (fStyle & CDATA_CONTENT) != 0;
	}

	/**
	 * Indicates whether the DOMName represents a sub element name or an attribute name
	 * 
	 * @return boolean True if the DOMName is an attribute name.
	 */
	public boolean isDOMAttribute() {
		return (fStyle & DOM_ATTRIBUTE) != 0;
	}

	/**
	 * Indicates whether the node should be written as an empty tag; eg, <distributable/>
	 */
	public boolean isEmptyTag() {
		return (fStyle & EMPTY_TAG) != 0;
	}

	public boolean isBooleanUppercase() {
		return (fStyle & BOOLEAN_FEATURE) != 0 && (fStyle & BOOLEAN_LOWERCASE) == 0;
	}

	public boolean isBooleanFeature() {
		return (fStyle & BOOLEAN_FEATURE) != 0;
	}

	public boolean shouldIndentEndTag() {
		return (fStyle & END_TAG_NO_INDENT) == 0;
	}

	public boolean shouldIndentEndTag(Node node) {
		return shouldIndentEndTag();
	}

	public boolean isEmptyContentSignificant() {
		return ((fStyle & EMPTY_TAG) != 0) || ((fStyle & EMPTY_CONTENT_IS_SIGNIFICANT) != 0);
	}

	/**
	 * Returns true if this map is to another MOF object (not a primitive)
	 */
	public boolean isObjectMap() {
		return (fStyle & OBJECT_MAP) != 0;
	}

	/**
	 * Returns true if this map is for a shared reference
	 */
	public boolean isShared() {
		return (fStyle & SHARED_REFERENCE) != 0;
	}

	public boolean isEnumWithHyphens() {
		return (fStyle & ENUM_FEATURE_WITH_HYPHENS) != 0;
	}

	/**
	 * Indicates whether the map represents a case where the text of the DOMNode represents the
	 * objects one and only attribute value. An example of this case is a <welcome-file>file.txt
	 * </welcome-file>.
	 */
	public boolean isDOMTextValue() {
		return fDOMNames[0] == TEXT_ATTRIBUTE_VALUE;
	}

	/**
	 * Indicates whether the id is the mof attribute that should be set.
	 */
	public boolean isIDMap() {
		return false;
	}

	/**
	 * Indicates whether the id is the mof attribute that should be set.
	 */
	public boolean isLinkMap() {
		return fTranslatorPaths != null;
	}

	public boolean isTargetLinkMap() {
		return isLinkMap() && !isObjectMap();
	}

	/**
	 * Return true if this map is the one representing a node with the name <domName>. By default
	 * this method simply compares the DOM name of the map against the <domName>parameter
	 * 
	 * @return boolean
	 * @param domName
	 *            java.lang.String
	 */
	public boolean isMapFor(String domName) {
		if (domName.equals(getDOMPath()))
			return true;
		for (int i = 0; i < fDOMNames.length; i++) {
			if (domName.equals(fDOMNames[i]))
				return true;
		}
		return false;
	}

	public boolean isMapFor(Object aFeature, Object oldValue, Object newValue) {
		return feature == aFeature;
	}

	/**
	 * Indicates whether feature being mapped is a collection.
	 * 
	 * @return boolean True if the feature is multi valued.
	 */
	public boolean isMultiValued() {
		if (feature != null)
			return feature.isMany();
		return false;
	}

	/**
	 * Parses comma separated names from <domNamesString>. Returns an array containing the names.
	 * 
	 * @return java.lang.String[]
	 * @param domNamesString
	 *            java.lang.String
	 */
	protected String[] parseDOMNames(String domNamesString) {
		int startInx = 0;
		int inx = domNamesString.indexOf(',');
		ArrayList results = new ArrayList(1);
		while (inx != -1) {
			results.add(domNamesString.substring(startInx, inx));
			startInx = inx + 1;
			inx = domNamesString.indexOf(',', startInx);
		}
		if (startInx == 0)
			results.add(domNamesString);
		else
			results.add(domNamesString.substring(startInx));
		return (String[]) results.toArray(new String[results.size()]);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String cn = getClass().getName();
		int i = cn.lastIndexOf('.');
		cn = cn.substring(++i, cn.length());
		sb.append(cn);
		sb.append('(');
		sb.append(fDOMNames[0]);
		for (int j = 1; j < fDOMNames.length; j++) {
			sb.append('|');
			sb.append(fDOMNames[j]);
		}
		sb.append(',');
		sb.append(hashCode());
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Gets the TranslatorPath.
	 * 
	 * @return Returns a TranslatorPath
	 */
	public TranslatorPath[] getTranslatorPaths() {
		return fTranslatorPaths;
	}

	/*
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Translator))
			return false;
		Translator mapInfo = (Translator) object;
		return fDOMNames.equals(mapInfo.getDOMNames()) && (feature == null && mapInfo.getFeature() == null || feature.equals(mapInfo.getFeature()));
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() +fDOMNames.hashCode();
	}

	/**
	 * Returns the isManagedByParent.
	 * 
	 * @return boolean
	 */
	public boolean isManagedByParent() {
		return getChildren(null, -1) == null;
	}

	/*
	 * In the 99% case there is only one node name to be concerned with, but subclasses can override
	 * for the cases where multiple dom names map to one feature
	 */
	public EObject createEMFObject(String nodeName, String readAheadName) {
		if (emfClass == null) {
			if (feature == null)
				return null;
			if (isObjectMap())
				return createEMFObject(feature);
		}
		return createEMFObject(emfClass);
	}

	public static EObject createEMFObject(EStructuralFeature aFeature) {
		if (aFeature == null)
			return null;

		return createEMFObject(((EReference) aFeature).getEReferenceType());
	}

	public static EObject createEMFObject(EClass anEClass) {
		if (anEClass == null)
			return null;
		return anEClass.getEPackage().getEFactoryInstance().create(anEClass);
	}

	public void setTextValueIfNecessary(String textValue, Notifier owner, int versionId) {
		Translator textTranslator = this.findChild(Translator.TEXT_ATTRIBUTE_VALUE, owner, versionId);
		if (textTranslator != null) {
			Object objectValue = textTranslator.convertStringToValue(textValue, (EObject) owner);
			textTranslator.setMOFValue(owner, objectValue);
		}
	}

	/**
	 * Check to see if feature is valid on a particular mofObject.
	 * 
	 * @return boolean Return true if the feature specified exists on the MOF object.
	 * @param emfObject
	 *            org.eclipse.emf.ecore.EObject
	 */
	public boolean featureExists(EObject emfObject) {
		if (feature == null || emfObject == null)
			return false;

		return emfObject.eClass().getEStructuralFeature(feature.getName()) != null;
	}

	/**
	 * Translators which do not have a feature should override this method with custom behavior.
	 */
	public String extractStringValue(EObject emfObject) {
		if (isEmptyTag() && feature == null)
			return ""; //Fake it out with a value //$NON-NLS-1$
		return null;
	}

	public Object convertStringToValue(String nodeName, String readAheadName, String value, Notifier owner) {
		Object result = null;
		try {

			if (!this.isManagedByParent()) {
				result = createEMFObject(nodeName, readAheadName);
			} else {
				result = convertStringToValue(value, (EObject) owner);
			}

		} catch (ClassCastException cce) {

		}
		return result;
	}

	/**
	 * Converts a string value to the appropriate type.
	 * 
	 * @return java.lang.Object The converted value
	 * @param strValue
	 *            java.lang.String The string to convert.
	 */
	public Object convertStringToValue(String strValue, EObject owner) {
		if (feature == null)
			return strValue;
		String innerStrValue = strValue;
		if (innerStrValue != null) {
			if (isEnumWithHyphens())
				innerStrValue = innerStrValue.replace('-', '_');
			if (!isCDATAContent()) {
				innerStrValue = innerStrValue.trim();
			}
		}
		Object value = FeatureValueConverter.DEFAULT.convertValue(innerStrValue, feature);
		if (value == null) {
			if (isEmptyTag() && !isDOMAttribute() && !isDOMTextValue() && isBooleanFeature())
				return Boolean.TRUE;
			EObject convertToType = feature.getEType();
			if (convertToType == null)
				value = innerStrValue;
			else if (convertToType.equals(getEcorePackage().getEString())) {
				value = ""; //$NON-NLS-1$
			}
		}
		return value;
	}

	/**
	 * Converts a value of a specified type to a string value. Subclasses may override for special
	 * cases where special conversion needs to occur based on the feature and or object type.
	 * 
	 * @return String The converted value
	 * @param value
	 *            java.lang.Object The object to convert.
	 */
	public String convertValueToString(Object value, EObject owner) {
		if (isEmptyTag() || value == null)
			return null;
		else if (isEnumWithHyphens())
			return value.toString().replace('_', '-');
		else if (isBooleanUppercase())
			return ((Boolean) value).booleanValue() ? "True" : "False"; //$NON-NLS-1$ //$NON-NLS-2$

		return value.toString();
	}

	public Translator[] getVariableChildren(Notifier target, int version) {
		Translator[] results = null;
		VariableTranslatorFactory factory = getVariableTranslatorFactory();
		if (factory != null) {
			List variableTranslators = factory.create(target);
			if (variableTranslators != null && variableTranslators.size() > 0) {
				Object[] vtoa = variableTranslators.toArray();

				results = new Translator[vtoa.length];
				for (int i = 0; i < results.length; i++)
					results[i] = (Translator) vtoa[i];
			}

		}
		if (results == null)
			results = new Translator[0];
		return results;
	}

	/**
	 * Returns null by default; subclasses should override to return specific children
	 */
	public Translator[] getChildren(Object target, int versionID) {
		return getChildren();
	}

	protected Translator[] getChildren() {
		return null;
	}

	/**
	 * Return the list of MOF children that currently exist for the values of an attribute.
	 */
	public List getMOFChildren(EObject mofObject) {
		if (feature == null)
			return Collections.EMPTY_LIST;
		Object value = getMOFValue(mofObject);
		List result = Collections.EMPTY_LIST;
		if (isMultiValued())
			result = (List) value;
		else if (value != null)
			result = Collections.singletonList(value);
		return result;
	}

	public Object getMOFValue(EObject mofObject) {
		if (feature == null)
			return null;
		return mofObject.eGet(feature);
	}

	/**
	 * Sets a value of a feature in a mof object.
	 */
	public void setMOFValue(Notifier owner, Object value, int newIndex) {
		if (feature != null) {
			if ((fStyle & UNSET_IF_NULL) != 0 && value == null)
				ExtendedEcoreUtil.eUnsetOrRemove((EObject) owner, feature, value);
			else
				ExtendedEcoreUtil.eSetOrAdd((EObject) owner, feature, value, newIndex);
		}
	}

	public void setMOFValue(Notifier owner, Object value) {
		if (owner instanceof EObject) {
			setMOFValue((EObject) owner, value);
		} else if (owner instanceof Resource) {
			setMOFValue((Resource) owner, value);
		}
	}

	public void setMOFValue(EObject emfObject, Object value) {
		//		if (feature != null)
		//			emfObject.eSet(feature, value);
		setMOFValue(emfObject, value, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#setMOFValue(org.eclipse.emf.ecore.EObject,
	 *      java.lang.Object)
	 */
	public void setMOFValue(Resource res, Object value) {
		if (res != null && value != null)
			res.getContents().add((EObject)value);
	}

	public void removeMOFValue(Notifier owner, Object value) {
		if (feature != null)
			ExtendedEcoreUtil.eUnsetOrRemove((EObject) owner, feature, value);
	}

	public boolean isSetMOFValue(EObject emfObject) {
		boolean isSet = feature != null && emfObject.eIsSet(feature);
		if (isEmptyTag())
			return isSet && ((Boolean) emfObject.eGet(feature)).booleanValue();
		return isSet;
	}

	public void unSetMOFValue(EObject emfObject) {
		if (feature != null)
			emfObject.eUnset(feature);
	}

	public void clearList(EObject mofObject) {
		if (feature != null)
			((List) mofObject.eGet(feature)).clear();
	}

	protected void setFeature(EStructuralFeature aFeature) {
		this.feature = aFeature;
		if (feature == null)
			return;
		//This way an instance check happens only once
		if (aFeature instanceof EReference) {
			fStyle |= OBJECT_MAP;
			if (!((EReference) aFeature).isContainment())
				fStyle |= SHARED_REFERENCE;
		}

		if (getEcorePackage().getEBoolean() == feature.getEType())
			fStyle |= BOOLEAN_FEATURE;
	}

	protected void setEMFClass(EClass anEClass) {
		this.emfClass = anEClass;
		if (anEClass != null)
			fStyle |= OBJECT_MAP;

	}

	public boolean hasReadAheadNames() {
		return readAheadNames != null && !readAheadNames.isEmpty();
	}

	/**
	 * Return the read ahead names, if they are defined, for a given parent node name. This is used
	 * when creation of a specific EMF object is dependent on the value of a child node.
	 */
	public ReadAheadHelper getReadAheadHelper(String parentName) {
		if (readAheadNames == null)
			return null;
		return (ReadAheadHelper) readAheadNames.get(parentName);
	}

	public void addReadAheadHelper(ReadAheadHelper helper) {
		if (readAheadNames == null)
			readAheadNames = new HashMap(3);
		readAheadNames.put(helper.getParentDOMName(), helper);
	}

	public boolean isDependencyChild() {
		return false;
	}

	/**
	 * @return
	 */
	public boolean isDependencyParent() {
		if (isDependencyParent == null) {
			isDependencyParent = Boolean.FALSE;
			Translator[] theChildren = getChildren(null, -1);
			if (theChildren != null) {
				for (int i = 0; i < theChildren.length; i++) {
					//For now we assume one
					if (theChildren[i].isDependencyChild()) {
						isDependencyParent = Boolean.TRUE;
						dependencyFeature = theChildren[i].getDependencyFeature();
					}
				}
			}
		}
		return isDependencyParent.booleanValue();
	}

	/**
	 * @return
	 */
	public EStructuralFeature getDependencyFeature() {
		return dependencyFeature;
	}

	public EObject basicGetDependencyObject(EObject parent) {
		return (EObject) parent.eGet(dependencyFeature);
	}

	/**
	 * Use when the DOM path is not null, and there are no children. Default is false, but
	 * subclasses may wish to override
	 */
	public boolean shouldRenderEmptyDOMPath(EObject eObject) {
		return isEmptyContentSignificant();
	}

	/**
	 * Use when the translator tolerates parent nodes that relate to the DOM path, and no children;
	 * default is do nothing
	 */
	public void setMOFValueFromEmptyDOMPath(EObject eObject) {

	}

	/**
	 * Namespace for the attributes
	 * 
	 * @return
	 */
	public String getNameSpace() {
		return fNameSpace;
	}

	/**
	 * Set the namespace for the dom attribute
	 * 
	 * @param string
	 */
	public void setNameSpace(String string) {
		fNameSpace = string;
	}

	public VariableTranslatorFactory getVariableTranslatorFactory() {
		if (isObjectMap())
			return DefaultTranslatorFactory.INSTANCE;
		return null;
	}

	public boolean isEnumFeature() {
		return feature != null && ECORE_PACKAGE.getEEnum().isInstance(feature.getEType());
	}

	public boolean isUnsettable() {
		return feature != null && feature.isUnsettable();
	}

	public boolean isDataType() {
		return feature != null && feature.getEType() instanceof EDataType;
	}

	/**
	 * @return
	 */
	public boolean isComment() {
		return (fStyle & COMMENT_FEATURE) != 0;
	}

}
