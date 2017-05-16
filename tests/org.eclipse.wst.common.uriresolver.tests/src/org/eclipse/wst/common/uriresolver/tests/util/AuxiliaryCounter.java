/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.tests.util;

public class AuxiliaryCounter {
	
	private static AuxiliaryCounter auxiliaryCounter = null;;
	private int counter = 0;
	
	public static AuxiliaryCounter getInstance() {
		if(auxiliaryCounter == null) {
			auxiliaryCounter = new AuxiliaryCounter();
		}
		return auxiliaryCounter;
	}
	
	public void resetCounter() {
		counter = 0;
	}
	
	public void incrementCounter() {
		counter++;
	}
	
	public int getCount() {
		return counter;
	}

}
