/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 26, 2004
 */
package org.eclipse.wst.common.internal.emf.utilities;


/**
 * @author mdelder
 * 
 */
public class ResourceIsLoadingAdapterFactory {

    public static ResourceIsLoadingAdapterFactory INSTANCE = new ResourceIsLoadingAdapterFactory();
         
    public ResourceIsLoadingAdapter createResourceIsLoadingAdapter() {
        return new ResourceIsLoadingAdapter();
    }
    
}
