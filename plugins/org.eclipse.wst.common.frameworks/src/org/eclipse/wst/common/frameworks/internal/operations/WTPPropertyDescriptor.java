package org.eclipse.wst.common.frameworks.internal.operations;

//TODO link to an example in the tutorial
/**
 * A WTPPropertyDescriptor provides a human readible description for a WTPDataModel propertyValue.
 *
 * This class is EXPERIMENTAL and is subject to substantial changes.
 *  
 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getPropertyDescriptor(String)
 * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getValidPropertyDescriptors(String)
 */
public class WTPPropertyDescriptor {

	/**
	 * A convenience method which returns an array of WTPPropertyDescriptors. If <code>values</code>
	 * is <code>null</code> then a 0 length array is returned. Otherwise for each
	 * <code>values[i]</code> in the array, a new WTPPropertyDescriptor is created using
	 * <code>new WTPPropertyDescriptor(values[i]);</code>.
	 * 
	 * @param values
	 *            the array of property values
	 * @return the constructed WTPPropertyDescriptor array
	 */
	public static WTPPropertyDescriptor[] createDescriptors(Object[] values) {
		if (null == values) {
			return new WTPPropertyDescriptor[0];
		}
		WTPPropertyDescriptor[] descriptors = new WTPPropertyDescriptor[values.length];
		for (int i = 0; i < descriptors.length; i++) {
			descriptors[i] = new WTPPropertyDescriptor(values[i]);
		}
		return descriptors;
	}

	/**
	 * A convenience method which returns an array of WTPPropertyDescriptors. If <code>values</code>
	 * is <code>null</code> then a 0 length array is returned. Otherwise for each
	 * <code>values[i]</code> and <code>descriptions[i]</code> in the arrays, a new
	 * WTPPropertyDescriptor is created using
	 * <code>new WTPPropertyDescriptor(values[i], descriptions[i]);</code>. Both arrays must be
	 * the same length.
	 * 
	 * @param values
	 *            the array of property values
	 * @param descriptions
	 *            the array of property descriptions cooresponding the values array
	 * @return the constructed WTPPropertyDescriptor array
	 */
	public static WTPPropertyDescriptor[] createDescriptors(Object[] values, String[] descriptions) {
		if (null == values) {
			return new WTPPropertyDescriptor[0];
		}
		WTPPropertyDescriptor[] descriptors = new WTPPropertyDescriptor[values.length];
		for (int i = 0; i < descriptors.length; i++) {
			descriptors[i] = new WTPPropertyDescriptor(values[i], descriptions[i]);
		}
		return descriptors;

	}

	private Object propertyValue;
	private String propertyDescription;

	/**
	 * This is equavalent to calling <code>WTPPropertyDescriptor(propertyValue, null);</code>
	 * 
	 * @param propertyValue
	 */
	public WTPPropertyDescriptor(Object propertyValue) {
		this.propertyValue = propertyValue;
	}

	/**
	 * Creates a new WTPPropertyDescriptor with the specified propertyValue and propertyDescription.
	 * 
	 * @param propertyValue
	 * @param propertyDescription
	 */
	public WTPPropertyDescriptor(Object propertyValue, String propertyDescription) {
		this.propertyValue = propertyValue;
		this.propertyDescription = propertyDescription;
	}

	/**
	 * Returns the property value.
	 * 
	 * @return the property value
	 */
	public Object getPropertyValue() {
		return propertyValue;
	}

	/**
	 * Returns a human readible property description. If a non null description has been specified,
	 * then it will be returned. Otherwise, the property value's <code>toString()</code> will be
	 * returned if it is not null. Otherwise the empty String (<code>""</code>) will be
	 * returned. Null will never be returned.
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