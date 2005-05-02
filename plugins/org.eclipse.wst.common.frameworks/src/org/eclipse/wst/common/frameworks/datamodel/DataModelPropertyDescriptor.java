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

/**
 * <p>
 * A DataModelPropertyDescriptor provides a human readible description for an IDataModel
 * propertyValue.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModel#getPropertyDescriptor(String)
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModel#getValidPropertyDescriptors(String)
 * 
 * @since 1.0
 */
public final class DataModelPropertyDescriptor {

	/**
	 * <p>
	 * A convenience method which returns an array of DataModelPropertyDescriptors. If
	 * <code>values</code> is <code>null</code> then a 0 length array is returned. Otherwise for
	 * each <code>values[i]</code> in the array, a new DataModelPropertyDescriptors is created
	 * using <code>new DataModelPropertyDescriptors(values[i]);</code>.
	 * </p>
	 * 
	 * @param values
	 *            the array of property values
	 * @return the constructed DataModelPropertyDescriptors array
	 */
	public static DataModelPropertyDescriptor[] createDescriptors(Object[] values) {
		if (null == values) {
			return new DataModelPropertyDescriptor[0];
		}
		DataModelPropertyDescriptor[] descriptors = new DataModelPropertyDescriptor[values.length];
		for (int i = 0; i < descriptors.length; i++) {
			descriptors[i] = new DataModelPropertyDescriptor(values[i]);
		}
		return descriptors;
	}

	/**
	 * <p>
	 * A convenience method which returns an array of DataModelPropertyDescriptors. If
	 * <code>values</code> is <code>null</code> then a 0 length array is returned. Otherwise for
	 * each <code>values[i]</code> and <code>descriptions[i]</code> in the arrays, a new
	 * DataModelPropertyDescriptor is created using
	 * <code>new DataModelPropertyDescriptor(values[i], descriptions[i]);</code>. Both arrays
	 * must be the same length.
	 * </p>
	 * 
	 * @param values
	 *            the array of property values
	 * @param descriptions
	 *            the array of property descriptions cooresponding the values array
	 * @return the constructed DataModelPropertyDescriptors array
	 */
	public static DataModelPropertyDescriptor[] createDescriptors(Object[] values, String[] descriptions) {
		if (null == values) {
			return new DataModelPropertyDescriptor[0];
		}
		DataModelPropertyDescriptor[] descriptors = new DataModelPropertyDescriptor[values.length];
		for (int i = 0; i < descriptors.length; i++) {
			descriptors[i] = new DataModelPropertyDescriptor(values[i], descriptions[i]);
		}
		return descriptors;

	}

	private Object propertyValue;
	private String propertyDescription;

	/**
	 * <p>
	 * This is equavalent to calling <code>DataModelPropertyDescriptor(propertyValue, null)</code>.
	 * </p>
	 * 
	 * @param propertyValue
	 *            the propery value
	 */
	public DataModelPropertyDescriptor(Object propertyValue) {
		this.propertyValue = propertyValue;
	}

	/**
	 * <p>
	 * Creates a new DataModelPropertyDescriptor with the specified propertyValue and
	 * propertyDescription.
	 * </p>
	 * 
	 * @param propertyValue
	 *            the property value
	 * @param propertyDescription
	 *            the human readible proeprty descriptionF
	 */
	public DataModelPropertyDescriptor(Object propertyValue, String propertyDescription) {
		this.propertyValue = propertyValue;
		this.propertyDescription = propertyDescription;
	}

	/**
	 * <p>
	 * Returns the property value.
	 * </p>
	 * 
	 * @return the property value
	 */
	public Object getPropertyValue() {
		return propertyValue;
	}

	/**
	 * <p>
	 * Returns a human readible property description. If a non null description has been specified,
	 * then it will be returned. Otherwise, the property value's <code>toString()</code> will be
	 * returned if it is not null. Otherwise the empty String (<code>""</code>) will be
	 * returned. <code>null</code> will never be returned.
	 * </p>
	 * 
	 * @return the human readible property description, never <code>null</code>
	 */
	public String getPropertyDescription() {
		if (null != propertyDescription) {
			return propertyDescription;
		}
		String str = (null != propertyValue) ? propertyValue.toString() : null;
		return (null != str) ? str : ""; //$NON-NLS-1$
	}
}