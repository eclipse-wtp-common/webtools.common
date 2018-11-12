/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.core.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;

public class ProductPropertyTester extends PropertyTester {

	public ProductPropertyTester() {
	}

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		IProduct product = Platform.getProduct();
		if (product != null && args.length > 0) {
			if (expectedValue != null)
				return expectedValue.equals(product.getProperty(args[0].toString()));
			else
				return product.getProperty(args[0].toString()) == null;
		}
		return false;
	}

}
