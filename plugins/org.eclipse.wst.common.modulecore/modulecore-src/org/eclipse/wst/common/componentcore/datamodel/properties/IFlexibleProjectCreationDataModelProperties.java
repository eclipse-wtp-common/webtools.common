/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.datamodel.properties;

import org.eclipse.wst.common.frameworks.internal.DoNotUseMeThisWillBeDeletedPost15;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationProperties;
/**
 * <p>
 * IFlexibleProjectCreationDataModelProperties provides properties to the DataModel associated with the 
 * FlexibleProjectCreationDataModelProperties as well as all extending interfaces extending 
 * IFlexibleProjectCreationDataModelProperties specifically, but not limited to the Java releated creatoin in the 
 * JST layer. 
 * @see FlexibleJavaProjectCreationDataModelProvider
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider
 * @see org.eclipse.wst.common.frameworks.datamodel.DataModelFactory
 * @see org.eclipse.wst.common.frameworks.datamodel.IDataModelProperties
 * 
 * @plannedfor 1.0
 */
/**
 * This has been slated for removal post WTP 1.5. Do not use this class/interface
 * 
 * @deprecated
 * 
 * @see IFacetProjectCreationDataModelProperties
 */
public interface IFlexibleProjectCreationDataModelProperties extends IProjectCreationProperties, DoNotUseMeThisWillBeDeletedPost15 {
   
    /**
     * Required, type IDataModel. The user set IDataModel used to create the initial project.  Providers which currently exist for
     * this IDataModel include IProjectCreationProperties.
     * @see org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationProperties
     */
    public static final String NESTED_MODEL_PROJECT_CREATION = "IFlexibleProjectCreationDataModelProperties.NESTED_MODEL_PROJECT_CREATION"; //$NON-NLS-1$
}
