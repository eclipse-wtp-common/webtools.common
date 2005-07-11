/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;

/**
 * replace with {@link org.eclipse.wst.common.frameworks.datamodel.IDataModel} and {@link org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider}
 * WTPOperationDataModel is an essential piece of both the WTP Operation and WTP Wizard frameworks.
 * WTPOPerationDataModels (DataModels) act as smart property containers used to pass various
 * properties between components. DataModels are smart property containers because they can:
 * <UL>
 * <LI>Compute default values for their properties thus saving clients from needing to populate (or
 * understand) all available properties.</LI>
 * <LI>Modify the computed default values when necessary (e.g. if the default value of property A
 * is based on property B, then A should change when B changes).</LI>
 * <LI>Notify listeners when properties change.</LI>
 * <LI>Check the validity of the of the proptery values (e.g. if a property is supposed to be an
 * Integer < 10)</LI>
 * <LI>Check the validity of the entire property set (e.g if property A is supposed to be an Iteger
 * which is a multiple of the Integer property B).</LI>
 * <LI>Supply an operation to execute.</LI>
 * <LI>Compose and decompose entire DataModels through nesting.</LI>
 * </UL>
 * 
 * <B>PropertyNames </B> Clients interact with DataModels by getting and setting properties
 * (Objects) with PropertyNames. A PropertyName is a String Object uniquely identifing a particular
 * property. The recommended practice for defining PropertyNames is to define them as static final
 * Class level Strings and to use the DataModel instance class name appended with the property name
 * as the value (this should ensure uniqueness and gives a readible value when debugging).
 * 
 * <A NAME="nestedDataModels"> <!-- --> </A>
 * <p>
 * <B>Nested DataModels </B>
 * <p>
 * 
 * 
 * The WTP Wizard framework uses DataModels to hold all the properties displayed to the user through
 * UI controls (e.g. textboxes, comboboxes, etc.). The Wizard framework also relies on DataModels
 * for validation, default values, and the operation executed on finish.
 * 
 * The WTO Operation framework uses DataModels to pass all parameters for execution. The DataModel
 * validation is used to ensure all the properties are valid prior to operation execution.
 * 
 * This class is EXPERIMENTAL and is subject to substantial changes.
 * 
 * @link org.eclipse.wst.common.frameworks.ui.WTPWizard for details on the WTP Wizard framework.
 * @link org.eclipse.wst.common.frameworks.operations.WTPOperation for details on the WTP Operation
 *       framework.
 */
public abstract class WTPOperationDataModel implements WTPOperationDataModelListener {

	/**
	 * A boolean property defaults to Boolean.TRUE. If this is set to Boolean.FALSE no extended
	 * operations will be executed
	 */
	public static final String ALLOW_EXTENSIONS = "WTPOperationDataModel.ALLOW_EXTENSIONS";

	/**
	 * A List containing String objects, defautls to an empty List. If this list contains elements
	 * and ALLOW_EXTENSIONS is set to Boolean.TRUE, then only extended operations not identified in
	 * this list will be executed. These strings should either be the operation id or the fully
	 * qualified oeration class name
	 */
	public static final String RESTRICT_EXTENSIONS = "WTPOperationDataModel.RESTRICT_EXTENSIONS";

	/**
	 * This property is used by the extended operation framework. Extended operations run within a
	 * context; e.g. they run within the scope of a project, resource, file, etc. The default value
	 * for this property is an empty List. Subclasses should configure themselves to return a list
	 * Objects. Each Object represents a Context which should be adaptable to IProject.
	 */
	protected static final String EXTENDED_CONTEXT = "WTPOperationDataModel.EXTENDED_CONTEXT"; //$NON-NLS-1$

	/**
	 * An unsettable property used soley to trip validation for nested models. Clients only use this
	 * property for validation purposes and never get or set its value. Subclasses can override
	 * nested model validation by checking for this property in the doValidate method and not
	 * calling super with it.
	 */
	public static final String NESTED_MODEL_VALIDATION_HOOK = "WTPOperationDataModel.NESTED_MODEL_VALIDATION_HOOK"; //$NON-NLS-1$
	/**
	 * Optional, type boolean This boolean was added for users who wish to delay the operation from
	 * being run on a "finish". The operation will be cached in the CACHED_DELAYED_OPERATION which
	 * then leaves the user responsible for running this operation when they see fit.
	 */
	public static final String RUN_OPERATION = "WTPOperationDataModel.RUN_OPERATION"; //$NON-NLS-1$
	/**
	 * Internal, type WTPOperation
	 */
	public static final String CACHED_DELAYED_OPERATION = "WTPOperationDataModel.CACHED_DELAYED_OPERATION"; //$NON-NLS-1$
	/**
	 * Optional Operation handler to allow user to prompt on save defaults to NullOperationHandler()
	 * set to UIOperationHanlder() to add prompt
	 */
	public static final String UI_OPERATION_HANLDER = "WTPOperationDataModel.UI_OPERATION_HANLDER"; //$NON-NLS-1$

	/**
	 * This is a convenience static status for subclasses to return during validation when the
	 * validation is OK.
	 */
	protected static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.wst.common.frameworks.internal", 0, "OK", null); //$NON-NLS-1$ //$NON-NLS-2$

	private static final String PROPERTY_NOT_LOCATED_ = WTPResourceHandler.getString("20"); //$NON-NLS-1$
	private static final String NESTED_MODEL_NOT_LOCATED = WTPResourceHandler.getString("21"); //$NON-NLS-1$
	private static final String NESTED_MODEL_DUPLICATE = WTPResourceHandler.getString("33"); //$NON-NLS-1$
	
	private static final WTPPropertyDescriptor[] NO_DESCRIPTORS = new WTPPropertyDescriptor[0];

	private Set validProperties = new HashSet();
	private Set validBaseProperties = new HashSet();
	private Map propertyValues = new Hashtable();
	private Map nestedModels;
	private Set nestingModels;
	private List listeners;
	private boolean ignorePropertyChanges = false;
	private boolean notificationEnabled = true;
	private boolean locked = false;
	private boolean operationValidationEnabled = false;
	private boolean hasBeenExecutedAgainst = false;
	private boolean suspendValidation = false;

	/**
	 * <p>
	 * The WTPOperationDataModel constructor. This constructor will first add the base
	 * WTPOPerationDataModel properties (RUN_OPERATION, CACHED_DELAYED_OPERATION, and
	 * UI_OPERATION_HANLDER). It then invokes the following:
	 * </p>
	 * <ol>
	 * <li><b><code>initValidBaseProperties()</code> </b> subclasses should override this method
	 * to add their properties using <code>addValidBaseProperty(String)</code>.</li>
	 * <li><b><code>initNestedModels()</code> </b> subclasses should override this method to add
	 * their nested models using <code>addNestedModel(String, WTPOperationDataModel)</code>.
	 * </li>
	 * <li><b><code>init()</code> </b> subclasses should override this method to perform any
	 * final initialization.</li>
	 * </ol>
	 * 
	 * @see #initValidBaseProperties()
	 * @see #initNestedModels()
	 * @see #init()
	 */
	public WTPOperationDataModel() {
		init_internal();
	}

	private final void init_internal() {
		addValidBaseProperty(EXTENDED_CONTEXT);
		addValidBaseProperty(RUN_OPERATION);
		addValidBaseProperty(CACHED_DELAYED_OPERATION);
		addValidBaseProperty(UI_OPERATION_HANLDER);
		addValidBaseProperty(ALLOW_EXTENSIONS);
		addValidBaseProperty(RESTRICT_EXTENSIONS);
		initValidBaseProperties();
		initNestedModels();
		init();
	}

	/**
	 * Subclasses should use this method within <code>initValidBaseProperties()</code> to add
	 * properties.
	 * 
	 * @param propertyName
	 *            The property name to be added.
	 * @see #initValidBaseProperties()
	 */
	protected final void addValidBaseProperty(String propertyName) {
		validBaseProperties.add(propertyName);
		validProperties.add(propertyName);
	}

	/**
	 * Subclasses should override this method to add properties using
	 * <code>addValidBaseProperty(String)</code>.
	 * 
	 * @see #addValidBaseProperty(String)
	 * @see #WTPOperationDataModel()
	 */
	protected void initValidBaseProperties() {
	}

	/**
	 * Subclasses should override this method to add nested DataModels using
	 * <code>addNestedModel(String, WTPOperationDataModel)</code>.
	 * 
	 * @see #addNestedModel(String, WTPOperationDataModel)
	 * @see #WTPOperationDataModel()
	 */
	protected void initNestedModels() {
	}

	/**
	 * Subclasses should override this method to perform any final initialization not covered by
	 * either <code>initValidBaseProperties()</code> or <code>initNestedModels()</code>.
	 * 
	 * @see #initValidBaseProperties()
	 * @see #initNestedModels()
	 * @see #WTPOperationDataModel()
	 */
	protected void init() {
	}

	/**
	 * <p>
	 * This method is used to nest the specified WTPOperationDataModel within this
	 * WTPOperationDataModel. The <code>modelName</code> argument should be a unique String to
	 * identify this particular nested DataModel. The same String is required when accessing the
	 * nested DataModel using either <code>getNestedModel(String)</code> or
	 * <code>removeNestedModel(String)</code>. If this is the first nested DataModel being added,
	 * then the <code>NESTED_MODEL_VALIDATION_HOOK</code> will be added to this DataModel .
	 * </p>
	 * <p>
	 * Refer to <A HREF="#nestedDataModels"> <CODE>NestedDataModels</CODE> </A>.
	 * </p>
	 * 
	 * @param modelName
	 *            the name of the WTPOperationDataModel to be nested
	 * @param dataModel
	 *            the WTPOperationDataModel to be nested
	 * 
	 * @see #getNestedModel(String)
	 * @see #removeNestedModel(String)
	 */
	public final void addNestedModel(String modelName, WTPOperationDataModel dataModel) {
		if (dataModel == null)
			return;
		if (null == nestedModels) {
			validBaseProperties.add(NESTED_MODEL_VALIDATION_HOOK);
			validProperties.add(NESTED_MODEL_VALIDATION_HOOK);
			nestedModels = new Hashtable();
		}
		if (null == dataModel.nestingModels) {
			dataModel.nestingModels = new HashSet();
		}
		if (dataModel.nestingModels.contains(this)) {
			throw new RuntimeException(NESTED_MODEL_DUPLICATE);
		}
		dataModel.nestingModels.add(this);

		nestedModels.put(modelName, dataModel);

		addNestedProperties(dataModel.validProperties);
		dataModel.addListener(this);
	}

	private void addNestedProperties(Set nestedProperties) {
		boolean propertiesAdded = validProperties.addAll(nestedProperties);
		// Pass the new properties up the nesting chain
		if (propertiesAdded && nestingModels != null) {
			Iterator iterator = nestingModels.iterator();
			while (iterator.hasNext()) {
				((WTPOperationDataModel) iterator.next()).addNestedProperties(nestedProperties);
			}
		}
	}

	public Iterator getNestedModels() {
		return nestedModels.values().iterator();
	}

	public Iterator getNestingModels() {
		return nestingModels.iterator();
	}

	/**
	 * Subclasses should override to return a WTPOperation to execute using this DataModel instance.
	 * The goal is for clients of to be able to simple create an instance of a particular DataModel,
	 * set a few properties on it, and then execute the operation returned by getDefaultOperation()
	 * 
	 * @return an initialized and executable WTPOperation
	 */
	public abstract WTPOperation getDefaultOperation();

	public WTPOperationDataModel removeNestedModel(String modelName) {
		if (modelName == null || nestedModels == null)
			return null;
		WTPOperationDataModel model = (WTPOperationDataModel) nestedModels.remove(modelName);
		model.nestingModels.remove(this);
		removeNestedProperties(model.validProperties);
		model.removeListener(this);
		if (nestedModels.isEmpty()) {
			nestedModels = null;
			validBaseProperties.remove(NESTED_MODEL_VALIDATION_HOOK);
			validProperties.remove(NESTED_MODEL_VALIDATION_HOOK);
		}
		return model;
	}

	private void removeNestedProperties(Set nestedProperties) {
		Iterator iterator = nestedProperties.iterator();
		String property = null;
		boolean keepProperty = false;
		Set nestedPropertiesToRemove = null;
		while (iterator.hasNext()) {
			keepProperty = false;
			property = (String) iterator.next();
			if (validBaseProperties.contains(property)) {
				keepProperty = true;
			}
			if (!keepProperty && nestedModels != null) {
				Iterator nestedModelsIterator = nestedModels.values().iterator();
				while (!keepProperty && nestedModelsIterator.hasNext()) {
					WTPOperationDataModel nestedModel = (WTPOperationDataModel) nestedModelsIterator.next();
					if (nestedModel.isProperty(property)) {
						keepProperty = true;
					}
				}
			}
			if (!keepProperty) {
				if (null == nestedPropertiesToRemove) {
					nestedPropertiesToRemove = new HashSet();
				}
				nestedPropertiesToRemove.add(property);
			}
		}

		if (null != nestedPropertiesToRemove) {
			validProperties.removeAll(nestedPropertiesToRemove);
			if (nestingModels != null) {
				Iterator nestingModelsIterator = nestingModels.iterator();
				while (nestingModelsIterator.hasNext()) {
					((WTPOperationDataModel) nestingModelsIterator.next()).removeNestedProperties(nestedPropertiesToRemove);
				}
			}
		}
	}


	public final WTPOperationDataModel getNestedModel(String modelName) {
		WTPOperationDataModel dataModel = (WTPOperationDataModel) nestedModels.get(modelName);
		if (null == dataModel) {
			throw new RuntimeException(NESTED_MODEL_NOT_LOCATED + modelName);
		}
		return dataModel;
	}

	/**
	 * Subclasses can override this method to determine if a given propertyName should be enabled
	 * for edit. Returning null indicates that there is no precedence on the enablement. Note that
	 * you can override this in an outer model since enablement may be different in the outer model.
	 * 
	 * @param propertyName
	 * @return the property's enablment state, null indicates there is no state
	 */
	public final Boolean isEnabled(String propertyName) {
		checkValidPropertyName(propertyName);
		return basicIsEnabled(propertyName);
	}

	/**
	 * Subclasses can override this method to determine if a given propertyName should be enabled
	 * for edit. Returning null indicates that there is no precedence on the enablement. Note that
	 * you can override this in an outer model since enablement may be different in the outer model.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected Boolean basicIsEnabled(String propertyName) {
		if (isBaseProperty(propertyName)) {
			return null;
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.isEnabled(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

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
	public final WTPPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		checkValidPropertyName(propertyName);
		if (isBaseProperty(propertyName)) {
			WTPPropertyDescriptor[] descriptors = doGetValidPropertyDescriptors(propertyName);
			if(null == descriptors){
				descriptors = NO_DESCRIPTORS; 
			}
			return descriptors;
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.getValidPropertyDescriptors(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	/**
	 * Subclasses should override this method to return their set of valid WTPPropertyDescriptors.
	 * This default implementation returns: <code>new WTPPropertyDescriptor[0];</code>
	 * 
	 * @param propertyName
	 * @return
	 * 
	 * @see #getValidPropertyDescriptors(String)
	 */
	protected WTPPropertyDescriptor[] doGetValidPropertyDescriptors(String propertyName) {
		return NO_DESCRIPTORS;
	}

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
	public final WTPPropertyDescriptor getPropertyDescriptor(String propertyName) {
		checkValidPropertyName(propertyName);
		if (isBaseProperty(propertyName)) {
			return doGetPropertyDescriptor(propertyName);
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.doGetPropertyDescriptor(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	/**
	 * Subclasses should override this method as necessary. This default implementation returns
	 * <code>new WTPPropertyDescriptor(getProperty(propertyName));</code>.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected WTPPropertyDescriptor doGetPropertyDescriptor(String propertyName) {
		return new WTPPropertyDescriptor(getProperty(propertyName));
	}

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
	public final String getStringProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return ""; //$NON-NLS-1$
		return (String) prop;
	}

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
	public final boolean getBooleanProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return false;
		return ((Boolean) prop).booleanValue();
	}

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
	public final int getIntProperty(String propertyName) {
		Object prop = getProperty(propertyName);
		if (prop == null)
			return -1;
		return ((Integer) prop).intValue();
	}

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
	public final void setBooleanProperty(String propertyName, boolean value) {
		setProperty(propertyName, (value) ? Boolean.TRUE : Boolean.FALSE);
	}

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
	public void setIntProperty(String propertyName, int value) {
		setProperty(propertyName, new Integer(value));
	}

	/**
	 * Override this method to compute default property values.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(EXTENDED_CONTEXT)) {
			Object targetProject = getTargetProject(); // TODO delete this block with
			// getTargetProject()
			if (null == targetProject) {
				return Collections.EMPTY_LIST;
			} else {
				List list = new ArrayList();
				list.add(targetProject);
				return list;
			}
		} else if (propertyName.equals(RUN_OPERATION) || propertyName.equals(ALLOW_EXTENSIONS)) {
			return Boolean.TRUE;
		} else if (propertyName.equals(UI_OPERATION_HANLDER)) {
			return new NullOperationHandler();
		} else if (propertyName.equals(RESTRICT_EXTENSIONS)) {
			return Collections.EMPTY_LIST;
		}
		return null;
	}

	/**
	 * Returns <code>true</code> if the specified propertyName is a valid propertyName for this
	 * root DataModel only. Nested DataModels are not checked.
	 * 
	 * @param propertyName
	 * @return
	 * @see #isProperty(String)
	 */
	public final boolean isBaseProperty(String propertyName) {
		if (validBaseProperties != null)
			return validBaseProperties.contains(propertyName);
		return true;
	}

	/**
	 * Returns <code>true</code> if the specified propertyName is a valid propertyName for this
	 * DataModel or any of its (recursively) nested DataModels.
	 * 
	 * @param propertyName
	 * @return
	 * @see #isBaseProperty(String)
	 */
	public final boolean isProperty(String propertyName) {
		return validProperties.contains(propertyName);
	}

	private void checkValidPropertyName(String propertyName) {
		if (!validProperties.contains(propertyName)) {
			throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
		}
	}

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
	public final Object getProperty(String propertyName) {
		checkValidPropertyName(propertyName);
		if (isBaseProperty(propertyName)) {
			return doGetProperty(propertyName);
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.getProperty(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	/**
	 * <p>
	 * Though generally unecessary, implementors may override this method as they see fit.
	 * <p>
	 * 
	 * @param propertyName
	 * @return
	 */
	protected Object doGetProperty(String propertyName) {
		if (propertyValues.containsKey(propertyName)) {
			return propertyValues.get(propertyName);
		}
		return getDefaultProperty(propertyName);
	}

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
	public final void setProperty(String propertyName, Object propertyValue) {
		if (isLocked() && !isResultProperty(propertyName))
			throw new IllegalStateException(WTPResourceHandler.getString("18", new Object[]{getClass().getName()})); //$NON-NLS-1$
		if (ignorePropertyChanges)
			return; // ignoring property changes
		checkValidPropertyName(propertyName);
		boolean nestedFound = false;
		if (isBaseProperty(propertyName)) {
			internalSetProperty(propertyName, propertyValue);
			return;
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					nestedFound = true;
					dataModel.setProperty(propertyName, propertyValue);
				}
			}
		}
		if (!nestedFound) {
			throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
		}
	}

	private final void internalSetProperty(String propertyName, Object propertyValue) {
		Object oldValue = propertyValues.get(propertyName);
		if (valueChanged(propertyValue, oldValue)) {
			if (doSetProperty(propertyName, propertyValue)) {
				notifyListeners(propertyName, WTPOperationDataModelEvent.PROPERTY_CHG);
			}
		}
	}

	/*
	 * Return true to notify listeners.
	 */
	protected boolean doSetProperty(String propertyName, Object propertyValue) {
		if (null != propertyValue)
			propertyValues.put(propertyName, propertyValue);
		else if (propertyValues.containsKey(propertyName))
			propertyValues.remove(propertyName);
		return true;
	}

	private boolean valueChanged(Object o1, Object o2) {
		return o1 != o2 && ((o1 != null && !o1.equals(o2)) || !o2.equals(o1));
	}

	/**
	 * Convenience method to create a WTPOperationDataModelEvent.PROPERTY_CHG event and notify
	 * listeners.
	 * 
	 * @param propertyName
	 * @see #notifyListeners(WTPOperationDataModelEvent)
	 */
	protected void notifyListeners(String propertyName) {
		notifyListeners(propertyName, WTPOperationDataModelEvent.PROPERTY_CHG);
	}

	/**
	 * Convenience method to create a WTPOperationDataModelEvent event of the specified type and
	 * notify listeners.
	 * 
	 * @param propertyName
	 * @see WTPOperationDataModelEvent for the list of valid flag values
	 */
	protected void notifyListeners(String propertyName, int flag) {
		notifyListeners(new WTPOperationDataModelEvent(this, propertyName, flag));
	}

	/**
	 * Notifies all registerd WTPOperationDataModelListeners of the specified
	 * WTPOperationDataModelEvent.
	 * 
	 * @param event
	 */
	protected void notifyListeners(WTPOperationDataModelEvent event) {
		if (notificationEnabled && listeners != null && !listeners.isEmpty()) {
			WTPOperationDataModelListener listener;
			for (int i = 0; i < listeners.size(); i++) {
				listener = (WTPOperationDataModelListener) listeners.get(i);
				if (listener != event.getDataModel()) {
					listener.propertyChanged(event);
				}
			}
		}
	}

	public void propertyChanged(WTPOperationDataModelEvent event) {
		notifyListeners(event);
	}

	public boolean isSet(String propertyName) {
		checkValidPropertyName(propertyName);
		if (isBaseProperty(propertyName)) {
			return propertyValues.containsKey(propertyName);
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					return dataModel.isSet(propertyName);
				}
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	public final IStatus validateDataModel() {
		return validateDataModel(false);
	}

	public final IStatus validateDataModel(boolean stopOnFirstFailure) {
		if (suspendValidation)
			return OK_STATUS;
		IStatus status = null;
		if (validBaseProperties != null && !validBaseProperties.isEmpty()) {
			IStatus propStatus;
			String propName;
			Iterator it = validBaseProperties.iterator();
			while (it.hasNext()) {
				propName = (String) it.next();
				propStatus = validateProperty(propName);
				if (status == null || status.isOK())
					status = propStatus;
				else {
					if (status.isMultiStatus())
						((MultiStatus) status).merge(propStatus);
					else {
						MultiStatus multi = new MultiStatus("org.eclipse.wst.common.frameworks.internal", 0, "", null); //$NON-NLS-1$ //$NON-NLS-2$
						multi.merge(status);
						multi.merge(propStatus);
						status = multi;
					}
				}
				if (stopOnFirstFailure && status != null && !status.isOK() && status.getSeverity() == IStatus.ERROR)
					return status;
			}
		}
		if (status == null)
			return OK_STATUS;
		return status;
	}

	public void addListener(WTPOperationDataModelListener listener) {
		if (listener != null) {
			if (listeners == null) {
				listeners = new ArrayList();
				listeners.add(listener);
			} else if (!listeners.contains(listener))
				listeners.add(listener);
		}
	}

	public void removeListener(WTPOperationDataModelListener listener) {
		if (listeners != null && listener != null)
			listeners.remove(listener);
	}

	/**
	 * Return true if the model doesn't have any errors.
	 * 
	 * @return boolean
	 */
	public boolean isValid() {
		IStatus status = validateDataModel(true);
		if (status.isOK())
			return true;
		if (status.getSeverity() == IStatus.ERROR)
			return false;
		return true;
	}

	/**
	 * Use this method when the model should ignore any property set calls. Remember to always reset
	 * the value in a finally block.
	 * 
	 * @param aBoolean
	 */
	public void setIgnorePropertyChanges(boolean aBoolean) {
		ignorePropertyChanges = aBoolean;
	}

	/**
	 * Return the status for the validation of a particular property. Subclasses should override
	 * when a specific validation is required.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected IStatus doValidateProperty(String propertyName) {
		if (NESTED_MODEL_VALIDATION_HOOK.equals(propertyName)) {
			if (nestedModels != null && !nestedModels.isEmpty()) {
				IStatus modelStatus;
				WTPOperationDataModel dataModel;
				Iterator it = nestedModels.values().iterator();
				while (it.hasNext()) {
					dataModel = (WTPOperationDataModel) it.next();
					modelStatus = dataModel.validateDataModel(true);
					if (!modelStatus.isOK()) {
						return modelStatus;
					}
				}
			}
		}
		return OK_STATUS;
	}

	public final IStatus validateProperty(String propertyName) {
		if (suspendValidation)
			return OK_STATUS;
		checkValidPropertyName(propertyName);
		if (isBaseProperty(propertyName)) {
			return doValidateProperty(propertyName);
		} else if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			boolean propertyFound = false;
			IStatus status = null;
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				if (dataModel.isProperty(propertyName)) {
					propertyFound = true;
					status = dataModel.validateProperty(propertyName);
					if (!status.isOK()) {
						return status;
					}
				}
			}
			if (propertyFound) {
				return OK_STATUS;
			}
		}
		throw new RuntimeException(PROPERTY_NOT_LOCATED_ + propertyName);
	}

	// handles for validation enablement for model and nested children
	protected void enableValidation() {
		suspendValidation = false;
		if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				dataModel.enableValidation();
			}
		}
	}

	// handles for validation disablement for model and nested children
	protected void disableValidation() {
		suspendValidation = true;
		if (nestedModels != null) {
			WTPOperationDataModel dataModel = null;
			Object[] keys = nestedModels.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				dataModel = (WTPOperationDataModel) nestedModels.get(keys[i]);
				dataModel.disableValidation();
			}
		}
	}

	/**
	 * This method should be called from doSetProperty(String, Object) when a change to a
	 * propertyName will cause default values within the model to change. The passed propertyName is
	 * another property that may need to have its default value recomputed. This allows for UIs to
	 * refresh.
	 * 
	 * @param propertyName
	 */
	public void notifyDefaultChange(String propertyName) {
		if (!isSet(propertyName))
			notifyListeners(propertyName, WTPOperationDataModelEvent.PROPERTY_CHG);
	}

	/**
	 * This method should be called when the valid values for the given propertyName may need to be
	 * recaculated. This allows for UIs to refresh.
	 * 
	 * @param propertyName
	 */
	public void notifyValidValuesChange(String propertyName) {
		notifyListeners(propertyName, WTPOperationDataModelEvent.VALID_VALUES_CHG);
	}

	protected void notifyEnablementChange(String propertyName) {
		Boolean enable = isEnabled(propertyName);
		if (enable != null) {
			notifyListeners(propertyName, WTPOperationDataModelEvent.ENABLE_CHG);
		}
	}

	public void dispose() {
	}

	protected boolean isNotificationEnabled() {
		return notificationEnabled;
	}

	protected void setNotificationEnabled(boolean notificationEnabled) {
		this.notificationEnabled = notificationEnabled;
	}

	/**
	 * @return Returns the locked.
	 */
	protected final boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked
	 *            The locked to set.
	 */
	protected final void setLocked(boolean locked) {
		this.locked = locked;
		if (locked)
			hasBeenExecutedAgainst = true;
	}

	/**
	 * By Overwriting this method, you can change the model when its executing. This is only
	 * recommened when you need to set the result of the operation in the model.
	 * 
	 * @param propertyName
	 * @return
	 */
	protected boolean isResultProperty(String propertyName) {
		return false;
	}


	public final boolean isOperationValidationEnabled() {
		return operationValidationEnabled;
	}

	public final void setOperationValidationEnabled(boolean operationValidationEnabled) {
		this.operationValidationEnabled = operationValidationEnabled;
	}

	/**
	 * This method is EXPERIMENTAL and is subject to substantial changes.
	 * 
	 * Gets the specified array of properties from the source DataModel and sets them on the
	 * destination DataModel.
	 * 
	 * @param source
	 * @param destination
	 * @param properties
	 */
	public static void copyProperties(WTPOperationDataModel source, WTPOperationDataModel destination, String[] properties) {
		for (int i = 0; i < properties.length; i++) {
			destination.setProperty(properties[i], source.getProperty(properties[i]));
		}
	}


	/**
	 * Recursively removes all property values of this DataModel and all nested DataModels.
	 */
	public void clearAllValues() {
		if (propertyValues != null)
			propertyValues.clear();
		if (nestedModels != null) {
			Iterator it = nestedModels.values().iterator();
			while (it.hasNext())
				((WTPOperationDataModel) it.next()).clearAllValues();
		}
	}

	/**
	 * Use this method to determine if an operation has been run using this data model.
	 * 
	 * @return
	 */
	public boolean hasBeenExecutedAgainst() {
		return hasBeenExecutedAgainst;
	}

	// TODO delete this
	/**
	 * This will be deleted before WTP M4 If this is used for extended operations, see the property
	 * EXTENDED_CONTEXT. Otherwise, there is no replacement method.
	 * 
	 * @deprecated see #EXTENDED_CONTEXT
	 */
	public IProject getTargetProject() {
		return null;
	}
}