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
 * IDataModelProvider. Clients wishing to define their own IDataModel must do so by implementing an
 * IDataModelProvider. Clients wishing to utilize an IDataModel must create it using the
 * DataModelFactory with the associated IDataModelProvider.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * @see org.eclipse.wst.common.frameworks.datamodel.DataModelFactory
 * 
 * @plannedfor 1.0
 */
public interface IDataModel {

	/**
	 * <p>
	 * Returns the unique ID which identifies this IDataModel instance. The same ID should be used
	 * by the default operation (if any) for clients to extend or instantiate directly, the
	 * DataModelWizard (if any) for clients to extend or instantiate directly.
	 * </p>
	 * <p>
	 * Note, this is not the same as a hashcode. Multiple IDataModel instances created with the same
	 * IDataModelProvider type will all have the same ID.
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
	 * @return the default operation
	 */
	public IDataModelOperation getDefaultOperation();

	/**
	 * <p>
	 * This method only pertains to IDataModels for extedended operations. The returned extended
	 * context is used by the IDataModelOperation framework to determine whether a particular
	 * extended operation should execute. The returned list is should contain Objects adaptable to
	 * IProject. This IDataModel's function groups are looked up through the extension mechanism. If
	 * a function group is defined, it is first checked for enablement. Then each adapted IProject
	 * is inspected to verify it handles the function group. If all these conditions are met, then
	 * the extended operation associated with this IDataModel is executed; otherwise it is skipped.
	 * If no function group is defined, or no extended context is defined (i.e. this method returns
	 * an empty list, or the objects in the returned list are not adaptable to IProject) then the
	 * extended operation will execute (it will never be skipped).
	 * </p>
	 * <p>
	 * An IDataModel implementor defines this in IDataModelProvider.
	 * </p>
	 * <p>
	 * This method should not be called by clients.
	 * </p>
	 * 
	 * @return a List of Objects adaptable to IProject
	 * 
	 * @see IDataModelProvider#getExtendedContext()
	 */
	public List getExtendedContext();

	/**
	 * <p>
	 * Returns the property value for the specified propertyName.
	 * </p>
	 * <p>
	 * If the specified propertyName is not a property then a RuntimeException will be thrown.
	 * </p>
	 * <p>
	 * Before the property is returned, first the owning IDataModel must be located. If the
	 * specified propertyName is a base property {@link #isBaseProperty(String)}, then this
	 * IDataModel is the owner. Otherwise, a recursive search through the nested IDataModels is
	 * conducted to locate the owning IDataModel. If more than one nested IDataModel defines the
	 * property, then the first one located is considered the owning IDataModel.
	 * </p>
	 * <p>
	 * Once the owning IDataModel is found the property is checked to see if it is set
	 * {@link #isPropertySet(String)}. If the property is set, the set value is returned. If the
	 * property is not set, its default is returned {@link #getDefaultProperty(String)}.
	 * </p>
	 * <p>
	 * There are convenience methods for getting primitive <code>int</code> and
	 * <code>boolean</code> types as well as Strings.
	 * <ul>
	 * <li>{@link #getIntProperty(String)}</li>
	 * <li>{@link #getBooleanProperty(String)}</li>
	 * <li>{@link #getStringProperty(String)}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the property
	 * 
	 * @see #setProperty(String, Object)
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
	 * </p>
	 * <p>
	 * <code>((Integer)getProperty(propertyName)).intValue();</code>
	 * </p>
	 * <p>
	 * <code>-1</code> is returned if a call to getProperty(propertyName) returns
	 * <code>null</code>.
	 * </p>
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
	 * </p>
	 * <p>
	 * <code>((Boolean)getProperty(propertyName)).booleanValue();</code>
	 * </p>
	 * <p>
	 * <code>false</code> is returned if a call to getProperty(propertyName) returns
	 * <code>null</code>.
	 * </p>
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the boolean value of the property
	 * @see #setProperty(String, Object)
	 * @see #setBooleanProperty(String, boolean)
	 */
	public boolean getBooleanProperty(String propertyName);

	/**
	 * <p>
	 * A convenience method for getting Strings. If the property is set then this method is
	 * equavalent to:
	 * </p>
	 * <p>
	 * <code>(String)getProperty(propertyName)</code>
	 * </p>
	 * <p>
	 * <code>""</code> is returned if a call to getProperty(propertyName) returns
	 * <code>null</code>.
	 * </p>
	 * 
	 * @param propertyName
	 * @see #setProperty(String, Object)
	 */
	public String getStringProperty(String propertyName);

	/**
	 * <p>
	 * Sets the specified propertyName to the specified propertyValue. Subsequent calls to
	 * {@link #getProperty(String)} will return the same propertyValue.
	 * </p>
	 * <p>
	 * When a propertyValue other than <code>null</code> is set, then the property is considered
	 * "set" (see {@link #isPropertySet(String)}), conversly, a propertyValue of <code>null</code>
	 * is considered "unset".
	 * </p>
	 * <p>
	 * If the specified propertyName is not a property (see {@link #isProperty(String)}) then a
	 * RuntimeException will be thrown.
	 * </p>
	 * <p>
	 * There are convenience methods for setting primitive <code>int</code> and
	 * <code>boolean</code> types as well as Strings.
	 * <ul>
	 * <li>{@link #setIntProperty(String, int)}</li>
	 * <li>{@link #setBooleanProperty(String, boolean)}</li>
	 * <li>{@link #setStringProperty(String, String)}</li>
	 * </ul>
	 * </p>
	 * <p>
	 * An IDataModel implementor may define additional post set logic in
	 * {@link IDataModelProvider#propertySet(String, Object)}.
	 * </p>
	 * 
	 * @param propertyName
	 *            the name of the property to set
	 * @param propertyValue
	 *            the value to set the property
	 * 
	 * 
	 * @see #getProperty(String)
	 * @see #isPropertySet(String)
	 * @see #isProperty(String)
	 * @see IDataModelProvider#propertySet(String, Object)
	 */
	public void setProperty(String propertyName, Object propertyValue);

	/**
	 * <p>
	 * A convenience method for setting ints. This method is equavalent to:
	 * <p>
	 * <code>setProperty(propertyName, new Integer(propertyValue));</code>
	 * </p>
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param propertyValue
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
	 * @param propertyValue
	 *            the <code>boolean</code> value of the property
	 * @see #setProperty(String, Object)
	 * @see #getBooleanProperty(String)
	 */
	public void setBooleanProperty(String propertyName, boolean propertyValue);

	/**
	 * <p>
	 * A convenience method for setting Strings. This method is equavalent to:
	 * <p>
	 * <code>setProperty(propertyName, propertyValue);</code>
	 * </p>
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param propertyValue
	 *            the value of the property
	 * @see #setProperty(String, Object)
	 * @see #getStringProperty(String)
	 */
	public void setStringProperty(String propertyName, String propertyValue);

	/**
	 * <p>
	 * This method is used to nest the specified IDataModel within this IDataModel. The
	 * <code>nestedModelName</code> argument should be a unique String to identify this particular
	 * nested IDataModel. The same String is required when accessing the nested IDataModel using
	 * either {@link #getNestedModel(String)} or {@link #removeNestedModel(String)}. If the
	 * specified nested IDataModel has already been nested under this IDataModel or it is the same
	 * instance as this IDataModel, then calling this method will have no effect.
	 * </p>
	 * 
	 * @param nestedModelName
	 *            the name of the IDataModel to be nested
	 * @param dataModel
	 *            the IDataModel to be nested
	 * @return <code>true</code> if the nesting was successful, <code>false</code> otherwise.
	 * 
	 * @see #getNestedModel(String)
	 * @see #removeNestedModel(String)
	 */
	public boolean addNestedModel(String nestedModelName, IDataModel dataModel);

	/**
	 * <p>
	 * Remove the specified nestedModel.
	 * </p>
	 * 
	 * @param nestedModelName
	 *            the name of the nested IDataModel to remove.
	 * @return the IDataModel removed, or <code>null</code> if the nested model does not exist or
	 *         if the specified name is null.
	 */
	public IDataModel removeNestedModel(String nestedModelName);

	/**
	 * Returns <code>true</code> if a nested model exists (at the top level only) with the
	 * specified name and <code>false</code> otherwise.
	 * 
	 * @param nestedModelName
	 *            the name of the nested IDataModel to check.
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
	 *            the name of the nested IDataModel to get.
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
	 *            the property name to check
	 * @return <code>true</code> if this property is a base property, <code>false</code>
	 *         otherwise.
	 * 
	 * @see #isProperty(String)
	 * @see #isNestedProperty(String)
	 */
	public boolean isBaseProperty(String propertyName);

	/**
	 * Returns <code>true</code> if the specified propertyName is a valid propertyName for this
	 * DataModel or any of its (recursively) nested IDataModels.
	 * 
	 * @param propertyName
	 *            the property name to check
	 * @return <code>true</code> if this is a property, <code>false</code> otherwise.
	 * 
	 * @see #isBaseProperty(String)
	 */
	public boolean isProperty(String propertyName);

	/**
	 * Returns <code>true</code> if the specified propertyName is a valid propertyName for any of
	 * its (recursively) nested IDataModels. The root IDataModel is not checked, though it is
	 * possible for the root IDataModel to contain the same property.
	 * 
	 * @param propertyName
	 *            the property name to check
	 * @return <code>true</code> if the property is nested, <code>false</code> otherwise.
	 * @see #isBaseProperty(String)
	 */
	public boolean isNestedProperty(String propertyName);

	/**
	 * Returns <code>true</code> if the specified property has been set on the IDataModel. If it
	 * has not been set, then a call to get the same property will return the default value.
	 * 
	 * @param propertyName
	 *            the property name to check
	 * @return <code>true</code> if the property is set, <code>false</code> otherwise.
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
	 *            the property name to check
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
	 *            the property name to check
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
	 * Equavalent to calling <code>validate(true)</code>.
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
	 * Returns a DataModelPropertyDescriptor for the specified property. The
	 * <code>getPropertyValue()</code> method on the returned DataModelPropertyDescriptor will be
	 * the same value as returned by <code>getPropertyValue(propertyName)</code>.
	 * </p>
	 * <p>
	 * Following the example introduced in {@link #getValidPropertyDescriptors(String)}, suppose
	 * the <code>SHIRT_SIZE</code> property is currently set to 1. A call to this method would
	 * return a DataModelPropertyDescriptor whose <code>getPropertyValue()</code> returns
	 * <code>1</code> and whose <code>getPropertyDescription()</code> returns <code>small</code>.
	 * </p>
	 * <p>
	 * Also, note that even if a particular property is not confined to a finite set of values as
	 * defined by {@link #getValidPropertyDescriptors(String)}this method will always return a
	 * valid DataModelPropertyDescriptor.
	 * </p>
	 * 
	 * <p>
	 * An IDataModel implementor defines this in IDataModelProvider.
	 * </p>
	 * 
	 * @param propertyName
	 * @return the DataModelPropertyDescriptor for the specified property
	 * 
	 * @see #getValidPropertyDescriptors(String)
	 */
	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName);

	/**
	 * <p>
	 * Returns a DataModelPropertyDescriptor array consisting of all valid
	 * DataModelPropertyDescriptors for the specified property. Each
	 * {@link DataModelPropertyDescriptor} contains a value and a human readible description for the
	 * value. The set of all values in the returned array are those values which are valid for the
	 * IDataModel. This value set only makes sense when valid property values conform to a well
	 * defined finite set. If no such value set exists for the property, then a 0 length array is
	 * returned. <code>null</code> is never returned.
	 * </p>
	 * <p>
	 * As an example, suppose there is a property called <code>SHIRT_SIZE</code> which is an
	 * <code>Integer</code> type. Also suppse that valid shirt sizes are only small, medium, or
	 * large. However, the actual values representing small, medium, and large are 1, 2, and 3
	 * respectively. A call to <code>getValidPropertyDescriptors(SHIRT_SIZE)</code> would return a
	 * DataModelPropertyDescriptor array where the value, description pairs would be {(1, small),
	 * (2, medium), (3, large)}.
	 * </p>
	 * <p>
	 * An IDataModel implementor defines this in IDataModelProvider.
	 * </p>
	 * 
	 * 
	 * @param propertyName
	 *            then name of the property to check
	 * @return the array of valid DataModelPropertyDescriptors
	 * @see #getPropertyDescriptor(String)
	 */
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName);

	/**
	 * <p>
	 * Adds the specified IDataModelListener to listen for DataModelEvents. If the specified
	 * listener has already been added, calling this method will have no effect.
	 * </p>
	 * 
	 * @param dataModelListener
	 *            the new listener to add.
	 * 
	 * @see #removeListener(IDataModelListener)
	 */
	public void addListener(IDataModelListener dataModelListener);

	/**
	 * <p>
	 * Remove the specified IDataModelListener. If the specified listener is not a registered
	 * listenr on this IDataModel, then calling this method will have no effect.
	 * </p>
	 * 
	 * @param dataModelListener
	 *            the listener to remove.
	 * @see #addListener(IDataModelListener)
	 */
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
	 * not map to an event type on DataModelEvent. When notifying with this type, a check is first
	 * done to see whether the property is set. If the property is <empf>NOT</emph> set, then a
	 * <code>VALUE_CHG</code> is fired, otherwise nothing happens.
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
	 * <p>
	 * Typically this method should only be invoked by an IDataModelProvider from its propertySet
	 * implementation.
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

	/**
	 * <p>
	 * A typical dispose method used to clean up any resources not handled by general garbage
	 * collection.
	 * </p>
	 * 
	 */
	public void dispose();

}
