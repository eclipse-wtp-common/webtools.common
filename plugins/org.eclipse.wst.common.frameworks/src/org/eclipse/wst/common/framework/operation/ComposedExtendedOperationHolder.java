/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.framework.operation;

import java.util.ArrayList;




public class ComposedExtendedOperationHolder {

	private ArrayList preOps = null;
	private ArrayList postOps = null;

	/**
	 *  
	 */
	protected ComposedExtendedOperationHolder() {
		super();
	}

	protected void addPreOperation(WTPOperation preOp) {
		if (preOps == null) {
			preOps = new ArrayList();
		}
		preOps.add(preOp);
	}

	protected void addPostOperation(WTPOperation postOp) {
		if (postOps == null) {
			postOps = new ArrayList();
		}
		postOps.add(postOp);
	}

	public boolean hasPreOps() {
		return preOps != null;
	}

	public boolean hasPostOps() {
		return postOps != null;
	}

	public ArrayList getPostOps() {
		return postOps;
	}

	public ArrayList getPreOps() {
		return preOps;
	}

}