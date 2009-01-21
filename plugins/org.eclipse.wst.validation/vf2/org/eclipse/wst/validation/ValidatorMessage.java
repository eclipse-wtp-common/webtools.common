/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.internal.ValConstants;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * This class provides a way for a validator to return messages, that are easily
 * converted into IMarkers.
 * <p>
 * This class is completely optional for validators. A validator can choose to
 * directly manage IMarkers. However, some validators want to be used in
 * multiple contexts, for example as-you-type validation and build based
 * validation. For these types of validators it is not possible for them to use
 * only IMarkers, because often the Resource has not been saved yet.
 * </p>
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that
 * is still under development and expected to change significantly before
 * reaching stability. It is being made available at this early stage to solicit
 * feedback from pioneering adopters on the understanding that any code that
 * uses this API will almost certainly be broken (repeatedly) as the API
 * evolves.
 * </p>
 */
public class ValidatorMessage {
	
	public static final String ValidationId = "ValidationId"; //$NON-NLS-1$
	
	/** Associate some arbitrary attributes with a message. */
	private final Map<String, Object>	_map = new HashMap<String, Object>(5);
	
	private IResource	_resource;
	
	/** The type of marker. */
	private String		_type;
	
	/**
	 * Create a new validation message.
	 * 
	 * @param message
	 * 		The localized message that will be displayed to the user.
	 * 
	 * @param resource
	 * 		The resource that the message is associated with.
	 */
	public static ValidatorMessage create(String message, IResource resource){
		ValidatorMessage msg = new ValidatorMessage();
		msg._type = ValConstants.ProblemMarker;
		msg._resource = resource;
		msg.setAttribute(IMarker.MESSAGE, ValidationPlugin.getPlugin().isDebugging() ? 
			Tracing.timestampIt(message): message);
		return msg;
	}
	
	private ValidatorMessage(){}
	
	/**
	 * Answer a copy of yourself.
	 */
	public ValidatorMessage asCopy(){
		ValidatorMessage msg = new ValidatorMessage();
		msg._resource = _resource;
		msg._type = _type;
		msg._map.putAll(_map);
		return msg;
	}
	
	/**
	 * Returns the attribute with the given name. The result is an instance of
	 * one of the following classes: <code>String</code>, <code>Integer</code>,
	 * or <code>Boolean</code>. Returns <code>null</code> if the attribute is
	 * undefined.
	 * 
	 * @param attributeName
	 * 		The name of the attribute.
	 * @return the value, or <code>null</code> if the attribute is undefined.
	 */
	public Object getAttribute(String attributeName){
		return _map.get(attributeName);
	}

	/**
	 * Returns the integer valued attribute with the given name. Returns the
	 * given default value if the attribute is undefined or the marker does not
	 * exist or is not an integer value.
	 * 
	 * @param attributeName
	 * 		The name of the attribute.
	 * @param defaultValue
	 * 		The value to use if no integer value is found.
	 * @return the value or the default value if no integer value was found.
	 */
	public int getAttribute(String attributeName, int defaultValue){
		Integer value = null;
		try {
			value = (Integer)_map.get(attributeName);
		}
		catch (Exception e){
			// eat it
		}
		if (value == null)return defaultValue;
		return value.intValue();
	}

	/**
	 * Returns the string valued attribute with the given name. Returns the
	 * given default value if the attribute is undefined or the marker does not
	 * exist or is not a string value.
	 * 
	 * @param attributeName
	 * 		The name of the attribute.
	 * @param defaultValue
	 * 		The value to use if no value is found.
	 * @return the value or the default value if no value was found.
	 */
	public String getAttribute(String attributeName, String defaultValue){
		String value = null;
		try {
			value = (String)_map.get(attributeName);
		}
		catch (Exception e){
			// eat it
		}
		if (value == null)return defaultValue;
		return value;
		
	}

	/**
	 * Returns the boolean valued attribute with the given name. Returns the
	 * given default value if the attribute is undefined or the marker does not
	 * exist or is not a boolean value.
	 * 
	 * @param attributeName
	 * 		The name of the attribute.
	 * @param defaultValue
	 * 		The value to use if no value is found.
	 * @return the value or the default value if no value was found.
	 */
	public boolean getAttribute(String attributeName, boolean defaultValue){
		Boolean value = null;
		try {
			value = (Boolean)_map.get(attributeName);
		}
		catch (Exception e){
			// eat it
		}
		if (value == null)return defaultValue;
		return value.booleanValue();
		
	}

	/**
	 * Returns a map with all the attributes for the marker. If the marker has
	 * no attributes then <code>null</code> is returned.
	 * 
	 * @return a map of attribute keys and values (key type :
	 * 	<code>String</code> value type : <code>String</code>,
	 * 	<code>Integer</code>, or <code>Boolean</code>) or <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public Map getAttributes() {
		return _map;
	}
		
	/**
	 * Returns the resource with which this marker is associated. 
	 *
	 * @return the resource with which this marker is associated
	 */
	public IResource getResource(){
		return _resource;		
	}

	/**
	 * Returns the type of this marker. The returned marker type will not be 
	 * <code>null</code>.
	 *
	 * @return the type of this marker
	 */
	public String getType() {
		return _type;
	}
	
	/**
	 * Sets the marker type.
	 * 
	 * @param type The marker id to use when creating new markers.
	 */
	public void setType(String type){
		assert type != null;
		_type = type;
	}
		
	/**
	 * Sets the integer valued attribute with the given name.
	 * <p>
	 * This method changes resources; these changes will be reported in a
	 * subsequent resource change event, including an indication that this
	 * marker has been modified.
	 * </p>
	 * 
	 * @param attributeName
	 * 		The name of the attribute.
	 * @param value
	 * 		The value.
	 */
	public void setAttribute(String attributeName, int value){
		_map.put(attributeName, new Integer(value));
		
	}

	/**
	 * Sets the attribute with the given name. The value must be
	 * <code>null</code> or an instance of one of the following classes:
	 * <code>String</code>, <code>Integer</code>, or <code>Boolean</code>.
	 * 
	 * @param attributeName
	 * 		The name of the attribute.
	 * @param value
	 * 		The value, or <code>null</code> if the attribute is to be undefined.
	 */
	public void setAttribute(String attributeName, Object value){
		_map.put(attributeName, value);
	}

	/**
	 * Sets the boolean valued attribute with the given name.
	 * 
	 * @param attributeName
	 * 		The name of the attribute.
	 * @param value
	 * 		The value.
	 */
	public void setAttribute(String attributeName, boolean value){
		_map.put(attributeName, value ? Boolean.TRUE : Boolean.FALSE);		
	}

	/**
	 * Sets the given attribute key-value pairs on this marker. The values must
	 * be <code>null</code> or an instance of one of the following classes:
	 * <code>String</code>, <code>Integer</code>, or <code>Boolean</code>. If a
	 * value is <code>null</code>, the new value of the attribute is considered
	 * to be undefined.
	 * 
	 * @param attributeNames
	 * 		An array of attribute names.
	 * @param values
	 * 		An array of attribute values.
	 */
	public void setAttributes(String[] attributeNames, Object[] values){
		Assert.isTrue(attributeNames.length == values.length);
		for (int i=0; i<attributeNames.length; i++){
			setAttribute(attributeNames[i], values[i]);			
		}
	}

	/**
	 * Sets the attributes for this marker to be the ones contained in the given
	 * table. The values must be an instance of one of the following classes:
	 * <code>String</code>, <code>Integer</code>, or <code>Boolean</code>.
	 * Attributes previously set on the marker but not included in the given map
	 * are considered to be removals. This includes the text of the message.
	 * Setting the given map to be
	 * <code>null</code> is equivalent to removing all marker attributes.
	 * 
	 * @param attributes
	 * 		A map of attribute names to attribute values (key type :
	 * 		<code>String</code> value type : <code>String</code>,
	 * 		<code>Integer</code>, or <code>Boolean</code>) or <code>null</code>.
	 */
	public void setAttributes(Map<String, Object> attributes) {
		_map.clear();
		_map.putAll(attributes);
		
	}

}
