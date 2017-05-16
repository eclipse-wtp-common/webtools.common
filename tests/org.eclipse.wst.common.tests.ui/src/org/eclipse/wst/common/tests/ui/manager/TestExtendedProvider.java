/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.tests.ui.manager;

import java.util.Set;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;

public class TestExtendedProvider extends AbstractDataModelProvider
{
  public Set getPropertyNames(){
    Set propertyNames = super.getPropertyNames();
    propertyNames.add("executedOps");
    propertyNames.add("executedUndoOps");
    
    return propertyNames;
  }

  public void init()
  {
  }

  public String getID()
  {
    return "TestExtendedProvider";
  }
  

}
