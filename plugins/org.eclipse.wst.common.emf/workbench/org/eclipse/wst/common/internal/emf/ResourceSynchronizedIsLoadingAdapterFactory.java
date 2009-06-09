/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.internal.emf;

import org.eclipse.wst.common.internal.emf.utilities.ResourceIsLoadingAdapter;
import org.eclipse.wst.common.internal.emf.utilities.ResourceIsLoadingAdapterFactory;


/**
 * @author mdelder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResourceSynchronizedIsLoadingAdapterFactory extends ResourceIsLoadingAdapterFactory {

    
    /* (non-Javadoc)
     * @see com.ibm.wtp.internal.emf.utilities.ResourceIsLoadingAdapterFactory#createResourceIsLoadingAdapter()
     */
    @Override
	public ResourceIsLoadingAdapter createResourceIsLoadingAdapter() { 
        return new ResourceSynchronizedIsLoadingAdapter();
    }
}
