/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Sep 22, 2003
 *
 */
package org.eclipse.wst.internal.common.emf.utilities;

/**
 * Interface that exposes the clone method, for objects that need to generically copy other
 * cloneable objects
 */
public interface CloneablePublic extends Cloneable {
	Object clone();

}