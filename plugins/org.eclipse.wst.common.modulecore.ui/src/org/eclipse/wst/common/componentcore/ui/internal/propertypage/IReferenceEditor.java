/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.internal.propertypage;

import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

/**
 * WizardFragments that are providing reference wizard extensions
 * may also implement IReferenceEditor to show that this particular
 * fragment may be able to edit components it has created. 
 */
public interface IReferenceEditor {
	
	/**
	 * The wizard fragment that implements this interface 
	 * and is able to edit the provided reference is expected 
	 * to cache this reference at the time canEdit(etc) is called
	 * 
	 * @param vc
	 * @return
	 */
	public boolean canEdit(IVirtualReference reference);
}
