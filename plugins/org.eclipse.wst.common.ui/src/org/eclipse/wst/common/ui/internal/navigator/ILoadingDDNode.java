/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.ui.internal.navigator;

import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author 
 * An interface used by navigator contribution to display text and image
 * while loading the model 
 */
public interface ILoadingDDNode {
	
	/**
	 * 
	 * @return
	 */
	String getText();
	
	/**
	 * 
	 * @return
	 */
	Image getImage();
}