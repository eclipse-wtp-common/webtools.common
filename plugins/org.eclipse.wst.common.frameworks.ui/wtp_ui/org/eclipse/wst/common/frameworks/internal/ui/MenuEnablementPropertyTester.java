/*******************************************************************************
 * Copyright (c) 2010, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
 
package org.eclipse.wst.common.frameworks.internal.ui;

import java.util.List;

import org.eclipse.core.expressions.IPropertyTester;
import org.eclipse.core.expressions.PropertyTester;

public class MenuEnablementPropertyTester extends PropertyTester{

	static private List<MenuEnablerExtension>  list = MenuEnablerExtensionReader.getInstance().getMenuEnabler();	
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if( list != null && list.size() > 0 ){
			MenuEnablerExtension menuEnablerExtension = list.get( 0 );
			IPropertyTester tester =  menuEnablerExtension.getInstance();
			if( tester != null ){
				return tester.test( receiver, property, args, expectedValue );
			}
			return true;
		}
		return true;
	}

}
