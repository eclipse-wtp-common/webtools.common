/*
 * Created on Nov 18, 2004 
 * @author jsholl
 */
package org.eclipse.wst.common.frameworks.internal.operations;

/**
 * @author jsholl
 */
public class WTPPropertyDescriptor {

	private Object propertyValue;
	private String propertyDescription;

	public WTPPropertyDescriptor(Object propertyValue){
		this.propertyValue = propertyValue;
	}
	
	public WTPPropertyDescriptor(Object propertyValue, String propertyDescription){
		this.propertyValue = propertyValue;
		this.propertyDescription = propertyDescription;
	}
	
	public Object getPropertyValue(){
		return propertyValue;
	}
	
	public String getPropertyDescription(){
		if(null != propertyDescription){
			return propertyDescription;
		}
		String str = (null != propertyValue) ? propertyValue.toString() : null;
		return (null != str) ? str : ""; //$NON-NLS-1$
	}
}
