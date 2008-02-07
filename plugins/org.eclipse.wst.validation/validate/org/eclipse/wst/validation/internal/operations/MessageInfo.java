/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.internal.operations;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;

public class MessageInfo {

	private String		messageOwnerId;	
	private IResource	resource;
	private String		location;	
	private String		text;
	private String		targetObjectName;	
	private String		markerId;	
	private IMessage	msg;
	
	public MessageInfo(){		
	}
	
	public MessageInfo(String messageOwnerId, IResource resource, String location, 
		String text, String targetObjectName, String markerId, IMessage msg){
		
		this.messageOwnerId = messageOwnerId;	
		this.resource = resource;
		this.location = location;	
		this.text = text;
		this.targetObjectName = targetObjectName;	
		this.markerId = markerId;	
		this.msg = msg;
	}	

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMarkerId() {
		return markerId;
	}

	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}

	public String getMessageOwnerId() {
		return messageOwnerId;
	}

	public void setMessageOwnerId(String messageOwnerId) {
		this.messageOwnerId = messageOwnerId;
	}

	public IMessage getMsg() {
		return msg;
	}

	public void setMsg(IMessage msg) {
		this.msg = msg;
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public String getTargetObjectName() {
		return targetObjectName;
	}

	public void setTargetObjectName(String targetObjectName) {
		this.targetObjectName = targetObjectName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		if (text != null)return "MessageInfo: " + text; //$NON-NLS-1$
		return super.toString();
	}
	
}
