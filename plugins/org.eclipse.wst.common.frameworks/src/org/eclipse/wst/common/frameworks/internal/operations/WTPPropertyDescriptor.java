/*
 * Created on Nov 18, 2004 
 * @author jsholl
 */
package org.eclipse.wst.common.frameworks.internal.operations;

/**
 * @author jsholl
 */
public class WTPPropertyDescriptor {

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

	public WTPPropertyDescriptor(Object propertyValue) {
		this.propertyValue = propertyValue;
	}

	public WTPPropertyDescriptor(Object propertyValue, String propertyDescription) {
		this.propertyValue = propertyValue;
		this.propertyDescription = propertyDescription;
	}

	public Object getPropertyValue() {
		return propertyValue;
	}

	public String getPropertyDescription() {
		if (null != propertyDescription) {
			return propertyDescription;
		}
		String str = (null != propertyValue) ? propertyValue.toString() : null;
		return (null != str) ? str : ""; //$NON-NLS-1$
	}
}