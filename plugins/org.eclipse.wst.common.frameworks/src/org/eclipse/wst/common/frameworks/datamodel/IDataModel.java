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
package org.eclipse.wst.common.frameworks.datamodel;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IStatus;

/**
 * <p>
 * IDataModels are the core piece of a framework used to simplify data collection, operation
 * execution, and Wizard generation.
 * </p>
 * <p>
 * IDataModels are primaryly an intelligent mechanism for managing data. Each IDataModel tracks
 * specific Objects known as "properties". Each property may be set or get using its property name.
 * A Collection of property names for an IDataModel instance may be retreived using
 * <code>getAllProperties()</code>. In addition to getting/setting properties, IDataModels may
 * also provide default values for unset properties, human readable descriptors for properties,
 * enumerations of valid property values, validation for properties, and enablement for properties.
 * </p>
 * <p>
 * IDataModels may also be nested (and unnested) recursively within another. When one IDataModel is
 * nested within another, then client code may access all properties on the former through the
 * latter. This is especially useful when the same IDataModel (tracking the same properties) may be
 * used within the context of several different broader scenarios. Nesting may apply to any
 * IDataModel, and may be abitraryly deep (even cylical if you dare). Nesting offers flexibility,
 * especially for extension by 3rd party clients.
 * </p>
 * <p>
 * Each IDataModel may also supply an IDataModelOperation (a subclass of
 * org.eclipse.core.commands.operations.IUndoableOperation) for execution. When executed within the
 * IDataModel framework all these operations are inherently and abitrarily extendable.
 * </p>
 * <p>
 * Each IDataModel may also indirectly supply a DataModelWizard. This indirection is necessary to
 * spilt UI dependencies from the core IDataModel framework. DataModelWizards are also inherently
 * extendable.
 * </p>
 * <p>
 * IDataModels are not meant to be instantiated directly, rather they are built from an
 * IDataModelProvider. Clients wishing to construct their own IDataModel must implement an
 * IDataModelProvider. Clients wishing to utilize an IDataModel must create it using the
 * DataModelFactory.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * @see org.eclipse.wst.common.frameworks.datamodel.DataModelFactory
 * 
 * @since 1.0
 */
public interface IDataModel {

	/**
	 * <p>
	 * Returns the unique ID which identifies this IDataModel instance. The same ID should be used
	 * by the default operation (if any) for clients to extend or instantiate directly, the
	 * DataModelWizard (if any) for clients to extend or instantiate directly.
	 * </p>
	 * <p>
	 * An IDataModel implementor defines this in IDataModelProvider.
	 * </p>
	 * 
	 * @see IDataModelProvider#getID()
	 * 
	 * @return the unique ID for this IDataModel
	 */
	public String getID();

	/**
	 * <p>
	 * Returns the default operation to execute against this IDataModel.
	 * </p>
	 * <p>
	 * An IDataModel implementor defines this in IDataModelProvider.
	 * </p>
	 * 
	 * @see IDataModelProvider#getDefaultOperation()
	 * 
	 * @return
	 */
	public IUndoableOperation getDefaultOperation();

	public List getExtendedContext();

	/**
	 * <p>
	 * Returns the property value for the specified propertyName.
	 * </p>
	 * <p>
	 * If the specified propertyName is not a property {@see #isProperty(String)}then a
	 * RuntimeException will be thrown.
	 * </p>
	 * <p>
	 * If the specified propertyName is a base property {@see #isBaseProperty(String)}then it is
	 * immediatly returned. If it is not a base property then a recursive search through the nested
	 * IDataModels is conducted to return the property. If more than one nested IDataModel defines
	 * the property, the returned value will be that of the first nested IDataModel found.
	 * </p>
	 * 
	 * @param propertyName
	 * @return
	 * 
	 * @see #getBooleanProperty(String)
	 * @see #getIntProperty(String)
	 * @see #getStringProperty(String)
	 */
	public Object getProperty(String propertyName);

	/**
	 * <p>
	 * Returns the default property value for the specified propertyName.
	 * </p>
	 * <p>
	 * An IDataModel implementor defines this in IDataModelProvider.
	 * </p>
	 * 
	 * @see IDataModelProvider#getDefaultProperty(String)
	 */
	public Object getDefaultProperty(String propertyName);

	/**
	 * <p>
	 * A convenience method for getting ints. If the property is set then this method is equavalent
	 * to:
	 * <p>
	 * <code>((Integer)getProperty(propertyName)).intValue();</code>
	 * <p>
	 * If the property is unset, <code>-1</code> will be returned.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the int value of the property
	 * @see #setProperty(String, Object)
	 * @see #setIntProperty(String, int)
	 */
	public int getIntProperty(String propertyName);

	/**
	 * <p>
	 * A convenience method for getting booleans. If the property is set then this method is
	 * equavalent to:
	 * <p>
	 * <code>((Boolean)getProperty(propertyName)).booleanValue();</code>
	 * <p>
	 * If the property is unset, <code>false</code> will be returned.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the boolean value of the property
	 * @see #setProperty(String, Object)
	 * @see #setBooleanProperty(String, int)
	 */
	public boolean getBooleanProperty(String propertyName);

	/**
	 * <p>
	 * A convenience method for getting Strings. If the property is set then this method is
	 * equavalent to:
	 * <p>
	 * <code>(String)getProperty(propertyName)</code>
	 * <p>
	 * If the property is unset, the empty String, <code>""</code>, will be returned.
	 * 
	 * @param propertyName
	 * @param value
	 * @see #setProperty(String, Object)
	 */
	public String getStringProperty(String propertyName);

	/**
	 * <p>
	 * Sets the specified propertyName to the specified propertyValue. Subsequent calls to
	 * #getProperty(String) will return the same propertyValue.
	 * <p>
	 * When a propertyValue other than <code>null</code> is set, then the property is considered
	 * "set" (see #isSet(String)), conversly, a propertyValue of <code>null</code> is considered
	 * "unset".
	 * <p>
	 * If the specified propertyName is not a property (see#isProperty(String)) then a
	 * RuntimeException will be thrown.
	 * <p>
	 * Attempting to set a propertyName when this DataModel is locked (see #isLocked()) will result
	 * in a thrown IllegalStateException. An IllegalStateException will not be thrown, however, if
	 * the propertyName is a Result Property, (see #isResultProperty(String)).
	 * <p>
	 * <p>
	 * An IDataModel implementor may define additional post set logic in IDataModelProvider.
	 * </p>
	 * 
	 * @see IDataModelProvider#propertySet(String, Object)
	 * @param propertyName
	 * @param propertyValue
	 * 
	 * 
	 * @see #getProperty(String)
	 * @see #isSet(String)
	 * @see #isProperty(String)
	 * @see #isResultProperty(String)
	 * @see #isLocked()
	 * 
	 * <p>
	 * There are also convenience methods for setting properties representing property types of
	 * boolean and int.
	 * <p>
	 * @see #setBooleanProperty(String, boolean)
	 * @see #setIntProperty(String, int)
	 */
	public void setProperty(String propertyName, Object propertyValue);

	/**
	 * <p>
	 * A convenience method for setting ints. This method is equavalent to:
	 * <p>
	 * <code>setProperty(propertyName, new Integer(value));</code>
	 * </p>
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param value
	 *            the <code>int</code> value of the property
	 * @see #setProperty(String, Object)
	 * @see #getIntProperty(String)
	 */
	public void setIntProperty(String propertyName, int propertyValue);

	/**
	 * <p>
	 * A convenience method for setting booleans. This method is equavalent to:
	 * <p>
	 * <code>setProperty(propertyName, (value) ? Boolean.TRUE : Boolean.FALSE);</code>
	 * </p>
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param value
	 *            the <code>boolean</code> value of the property
	 * @see #setProperty(String, Object)
	 * @see #getBooleanProperty(String)
	 */
	public void setBooleanProperty(String propertyName, boolean propertyValue);

	/**
	 * <p>
	 * A convenience method for setting Strings. This method is equavalent to:
	 * <p>
	 * <code>setProperty(propertyName, value);</code>
	 * </p>
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 * @see #setProperty(String, Object)
	 * @see #getBooleanProperty(String)
	 */
	public void setStringProperty(String propertyName, String propertyValue);

	/**
	 * <p>
	 * This method is used to nest the specified IDataModel within this IDataModel. The
	 * <code>modelName</code> argument should be a unique String to identify this particular
	 * nested IDataModel. The same String is required when accessing the nested IDataModel using
	 * either <code>getNestedModel(String)</code> or <code>removeNestedModel(String)</code>. If
	 * the specified nested IDataModel has already been nested under this IDataModel or it is the
	 * same instance as this IDataModel, then calling this method will have no effect.
	 * </p>
	 * <p>
	 * Refer to <A HREF="#nestedDataModels"> <CODE>NestedDataModels</CODE> </A>.
	 * </p>
	 * 
	 * @param modelName
	 *            the name of the IDataModel to be nested
	 * @param dataModel
	 *            the IDataModel to be nested
	 * 
	 * @see #getNestedModel(String)
	 * @see #removeNestedModel(String)
	 * @return <code>true</code> if the nesting was successful, <code>false</code> otherwise.
	 */
	public boolean addNestedModel(String nestedModelName, IDataModel dataModel);

	/**
	 * <p>
	 * Remove the specified nestedModel.
	 * </p>
	 * 
	 * @param nestedModelName
	 * @return the IDataModel removed, or <code>null</code> if the nested model does not exist or
	 *         if the specified name is null.
	 */
	public IDataModel removeNestedModel(String nestedModelName);

	/**
	 * Returns <code>true</code> if a nested model exists (at the top level only) with the
	 * specified name and <code>false</code> otherwise.
	 * 
	 * @param nestedModelName
	 * @return Returns <code>true</code> if a nested model exists (at the top level only) with the
	 *         specified name and <code>false</code> otherwise.
	 */
	public boolean isNestedModel(String nestedModelName);

	/**
	 * Returns the nested IDataModel identified the by the specified name. A RuntimeException is
	 * thrown if there is no such nested IDataModel (i.e. isNestedModel() would return
	 * <code>false</code>).
	 * 
	 * @param nestedModelName
	 * @return the nested IDataModel
	 */
	public IDataModel getNestedModel(String nestedModelName);

	/**
	 * Returns a Collection of all nested IDataModels, or an empty Collection if none exist.
	 * 
	 * @return a Collection of all nested IDataModels, or an empty Collection if none exist.
	 */
	public Collection getNestedModels();

	/**
	 * Returns a Collection of all nested IDataModels names, or an empty Collection if none exist.
	 * 
	 * @return a Collection of all nested IDataModels names, or an empty Collection if none exist.
	 */
	public Collection getNestedModelNames();

	/**
	 * Returns a Collection of all nesting (the inverse of nested) IDataModels, or an empty
	 * Collection if none exist.
	 * 
	 * @return a Collection of all nesting (the inverse of nested) IDataModels, or an empty
	 *         Collection if none exist.
	 */
	public Collection getNestingModels();

	/**
	 * Returns a Collection of all base properties (not including nested properties), or an empty
	 * Collection if none exist.
	 * 
	 * @return a Collection of all base properties (not including nested properties), or an empty
	 *         Collection if none exist.
	 */
	public Collection getBaseProperties();

	/**
	 * Returns a Collection of all properties of recursively nested IDataModels, or an empty
	 * Collection if none exist.
	 * 
	 * @return a Collection of all properties of recursively nested IDataModels, or an empty
	 *         Collection if none exist.
	 */
	public Collection getNestedProperties();

	/**
	 * Returns a Collection of all properties (the union of getBaseProperties() and
	 * getNestedProperties()), or an empty Collection if none exist.
	 * 
	 * @return a Collection of all properties (the union of getBaseProperties() and
	 *         getNestedProperties()), or an empty Collection if none exist.
	 */
	public Collection getAllProperties();

	/**
	 * Returns <code>true</code> if the specified propertyName is a valid propertyName for this
	 * root IDataModel only. Nested IDataModels are not checked, though it is possible for a nested
	 * IDataModel to contain the same property.
	 * 
	 * @param propertyName
	 * @return
	 * @see #isProperty(String)
	 */
	public boolean isBaseProperty(String propertyName);

	/**
	 * Returns <code>true</code> if the specified propertyName is a valid propertyName for this
	 * DataModel or any of its (recursively) nested IDataModels.
	 * 
	 * @param propertyName
	 * @return
	 * @see #isBaseProperty(String)
	 */
	public boolean isProperty(String propertyName);

	/**
	 * Returns <code>true</code> if the specified propertyName is a valid propertyName any of its
	 * (recursively) nested IDataModels. The root IDataModel is not checked, though it is possible
	 * for the root IDataModel to contain the same property.
	 * 
	 * @param propertyName
	 * @return
	 * @see #isBaseProperty(String)
	 */
	public boolean isNestedProperty(String propertyName);

	/**
	 * Returns <code>true</code> if the specified property has been set on the IDataModel. If it
	 * has not been set, then a call to get the same property will return the default value.
	 * 
	 * @param propertyName
	 * @return
	 */
	public boolean isPropertySet(String propertyName);

	/**
	 * <p>
	 * Returns <code>true</code> if the specified property is enabled and <code>false</code>
	 * otherwise.
	 * </p>
	 * <p>
	 * An IDataModel implementor defines this in IDataModelProvider.
	 * </p>
	 * 
	 * @see IDataModelProvider#isPropertyEnabled(String)
	 * 
	 * @param propertyName
	 * @return <code>true</code> if the specified property is enabled and <code>false</code>
	 *         otherwise.
	 */
	public boolean isPropertyEnabled(String propertyName);

	/**
	 * <p>
	 * Returns <code>false</code> if the the IStatus returned by validateProperty(String) is ERROR
	 * and <code>true</code> otherwise.
	 * </p>
	 * 
	 * @param propertyName
	 * @return <code>false</code> if the the IStatus returned by validateProperty(String) is ERROR
	 *         and <code>true</code> otherwise.
	 */
	public boolean isPropertyValid(String propertyName);

	/**
	 * <p>
	 * Returns an IStatus for the specified property. Retuns an IStatus.OK if the returned value is
	 * valid with respect itself, other properites, and broader context of the IDataModel.
	 * IStatus.ERROR is returned if the returned value is invalid. IStatus.WARNING may also be
	 * returned if the value is not optimal.
	 * </p>
	 * <p>
	 * An IDataModel implementor defines this in IDataModelProvider.
	 * </p>
	 * 
	 * @see IDataModelProvider#validate(String)
	 */
	public IStatus validateProperty(String propertyName);

	/**
	 * <p>
	 * Returns <code>false</code> if the IStatus returned by validate(true) is ERROR and
	 * <code>true</code> otherwise.
	 * </p>
	 * 
	 * @return <code>false</code> if the IStatus returned by validate(true) is ERROR and
	 *         <code>true</code> otherwise.
	 */
	public boolean isValid();

	/**
	 * <p>
	 * Equavalent to calling validate(true)
	 * </p>
	 * 
	 * @return an IStatus
	 */
	public IStatus validate();

	/**
	 * <p>
	 * Iterates over all base properties and nested models IDs and calls validate(String). This
	 * method returns when any call to validate(String) returns an IStatus error and
	 * stopAtFirstFailure is set to true.
	 * <p>
	 * 
	 * @param stopAtFirstFailure
	 *            whether validation should stop at the first failure
	 * @return an IStatus
	 */
	public IStatus validate(boolean stopAtFirstFailure);



	/**
	 * <p>
	 * Returns a WTPPropertyDescriptor for the specified property. The
	 * <code>getPropertyValue()</code> method on the returned WTPPropertyDescriptor will be the
	 * same value as returned by <code>getPropertyValue(propertyName)</code>.
	 * </p>
	 * <p>
	 * Following the example introduced in {@see #getValidPropertyDescriptors(String)}, suppose the
	 * <code>SHIRT_SIZE</code> property is currently set to 1. A call to this method would return
	 * a WTPPropertyDescriptor whose <code>getPropertyValue()</code> returns <code>1</code> and
	 * whose <code>getPropertyDescription()</code> returns <code>small</code>.
	 * </p>
	 * <p>
	 * Also, note that even if a particular property is not confined to a finite set of values as
	 * defined by {@see #getValidPropertyDescriptors(String)}this method will always return a valid
	 * WTPPropertyDescriptor.
	 * </p>
	 * <p>
	 * Subclasses should should override {@see #doGetPropertyDescriptor(String)}as necessary.
	 * </p>
	 * 
	 * @param propertyName
	 * @return the WTPPropertyDescriptor for the specified property
	 * 
	 * @see #doGetValidPropertyDescriptors(String)
	 * @see #doGetPropertyDescriptor(String)
	 */
	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName);

	/**
	 * <p>
	 * Returns a WTPPropertyDescriptor array consisting of all valid WTPPropertyDescriptors for the
	 * specified property. Each WTPPropertyDescriptor {@see WTPPropertyDescriptor for details}
	 * contains a value and a human readible description for the value. The set of all values in the
	 * returned array are those values which are valid for the DataModel. This value set only makes
	 * sense when valid property values conform to a well defined finite set. If no such value set
	 * exists for the property, the a 0 length array is returned. <code>null</code> is never
	 * returned.
	 * </p>
	 * <p>
	 * As an example, suppose there is a property called <code>SHIRT_SIZE</code> which is an
	 * <code>Integer</code> type. Also suppse that valid shirt sizes are only small, medium, or
	 * large. However, the actual values representing small, medium, and large are 1, 2, and 3
	 * respectively. A call to <code>getValidPropertyDescriptors(SHIRT_SIZE)</code> would return a
	 * WTPPropertyDescriptor array where the value, description pairs would be {(1, small), (2,
	 * medium), (3, large)}.
	 * </p>
	 * <p>
	 * Subclasses should override {@see #doGetValidPropertyDescriptors(String)}as necessary.
	 * </p>
	 * 
	 * @param propertyName
	 * @return the array of valid WTPPropertyDescriptors
	 * @see #getPropertyDescriptor(String)
	 * @see #doGetValidPropertyDescriptors(String)
	 */
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName);

	public void addListener(IDataModelListener dataModelListener);

	public void removeListener(IDataModelListener dataModelListener);

	/**
	 * <p>
	 * A constant used for notification.
	 * </p>
	 * 
	 * @see DataModelEvent#VALUE_CHG
	 * @see #notifyPropertyChange(String, int)
	 */
	public static final int VALUE_CHG = DataModelEvent.VALUE_CHG;
	/**
	 * <p>
	 * A constant used for notification. This contant is different from the others because it does
	 * not map to an even type on DataModelEvent. When notifying with this type, a check is done to
	 * see whether the property is set. If the property is set, then a <code>VALUE_CHG</code> is
	 * fired, otherwise nothing happens.
	 * </p>
	 * 
	 * @see #notifyPropertyChange(String, int)
	 */
	public static final int DEFAULT_CHG = DataModelEvent.DEFAULT_CHG;
	/**
	 * <p>
	 * A constant used for notification.
	 * </p>
	 * 
	 * @see DataModelEvent#ENABLE_CHG
	 * @see #notifyPropertyChange(String, int)
	 */
	public static final int ENABLE_CHG = DataModelEvent.ENABLE_CHG;
	/**
	 * <p>
	 * A constant used for notification.
	 * </p>
	 * 
	 * @see DataModelEvent#VALID_VALUES_CHG
	 * @see #notifyPropertyChange(String, int)
	 */
	public static final int VALID_VALUES_CHG = DataModelEvent.VALID_VALUES_CHG;

	/**
	 * <p>
	 * Notify all listeners of a property change. <code>eventType</code> specifies the type of
	 * change. Acceptible values for eventType are <code>VALUE_CHG</code>,
	 * <code>DEFAULT_CHG</code>, <code>ENABLE_CHG</code>, <code>VALID_VALUES_CHG</code>. If
	 * the eventType is <code>DEFAULT_CHG</code> and the specified property is set, then this
	 * method will do nothing.
	 * </p>
	 * 
	 * @param propertyName
	 *            the name of the property changing
	 * @param eventType
	 *            the type of event to fire
	 * 
	 * @see #VALUE_CHG
	 * @see #DEFAULT_CHG
	 * @see #ENABLE_CHG
	 * @see #VALID_VALUES_CHG
	 * @see DataModelEvent
	 */
	public void notifyPropertyChange(String propertyName, int eventType);

	public void dispose();

}
