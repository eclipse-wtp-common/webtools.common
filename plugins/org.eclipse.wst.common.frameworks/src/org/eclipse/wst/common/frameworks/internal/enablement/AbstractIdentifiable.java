/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.enablement;

public class AbstractIdentifiable implements Identifiable {

	protected String id;

	public AbstractIdentifiable(String id) {
		this.id = id;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public int getLoadOrder() {
		return 0;
	}

}
