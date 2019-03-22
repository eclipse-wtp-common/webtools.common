/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.internal.MarkerManager;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;


/**
 * This is a temporary class to ease the transition from the previous validation framework.
 * <p>
 * This is not API. 
 * @author karasiuk
 *
 */
public class ReporterHelper implements IReporter {
	private IProgressMonitor 	_monitor;
	private List<IMessage>		_list = new LinkedList<IMessage>();
	
	public ReporterHelper(IProgressMonitor monitor){
		_monitor = monitor;
	}

	public void addMessage(IValidator origin, IMessage message) {
		_list.add(message);
	}

	public void displaySubtask(IValidator validator, IMessage message) {
		_monitor.subTask(message.getText(validator.getClass().getClassLoader()));
	}

	public List<IMessage> getMessages() {
		return _list;
	}

	public boolean isCancelled() {
		return _monitor.isCanceled();
	}

	public void removeAllMessages(IValidator origin) {
		_list.clear();
	}

	public void removeAllMessages(IValidator origin, Object object) {
		_list.clear();
	}

	public void removeMessageSubset(IValidator validator, Object obj, String groupName) {
		_list.clear();
	}
	
	public void makeMarkers(){
		MarkerManager.getDefault().makeMarkers(_list);
	}
	
}
