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
package org.eclipse.wst.common.frameworks.datamodel.provisional;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IStatus;

/**
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IDataModel {

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
	 * If the specified propertyName is a base property {@see #isBaseProperty(String)}then it will
	 * immediatly be set and nested models will not be affected. If it is not a base property (i.e.
	 * it is a property for a nested DataModel) then a recursive search through nested DataModels
	 * will be conducted. The first nested DataModel having the property will return its value.
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
	 * 
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

	public boolean isNestedModel(String nestedModelName);

	public IDataModel getNestedModel(String nestedModelName);

	public Collection getNestedModels();

	public Collection getNestingModels();

	public Collection getBaseProperties();

	public Collection getNestedProperties();

	public Collection getAllProperties();

	/**
	 * Returns <code>true</code> if the specified propertyName is a valid propertyName for this
	 * root DataModel only. Nested DataModels are not checked.
	 * 
	 * @param propertyName
	 * @return
	 * @see #isProperty(String)
	 */
	public boolean isBaseProperty(String propertyName);

	/**
	 * Returns <code>true</code> if the specified propertyName is a valid propertyName for this
	 * DataModel or any of its (recursively) nested DataModels.
	 * 
	 * @param propertyName
	 * @return
	 * @see #isBaseProperty(String)
	 */
	public boolean isProperty(String propertyName);

	public boolean isNestedProperty(String propertyName);

	public boolean isPropertySet(String propertyName);

	public boolean isPropertyEnabled(String propertyName);

	public boolean isPropertyValid(String propertyName);

	public IStatus validateProperty(String propertyName);

	public boolean isValid();

	public IStatus validate();

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
