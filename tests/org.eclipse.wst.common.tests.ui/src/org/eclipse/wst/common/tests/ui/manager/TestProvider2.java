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

public class TestProvider2 extends AbstractDataModelProvider
{
  public Set getPropertyNames(){
    Set propertyNames = super.getPropertyNames();
    propertyNames.add("provider2Prop1");
    propertyNames.add("provider2Prop2");
    propertyNames.add("provider2Prop3");
    propertyNames.add("provider2Prop4");
    
    return propertyNames;
  }

  public void init()
  {
    setProperty( "provider2Prop1", "1111" );
    setProperty( "provider2Prop2", "2222" );
    setProperty( "provider2Prop3", "3333" );
    setProperty( "provider2Prop4", "4444" );
  }
  
  public String getID()
  {
    return "testprovider2";
  }
  
}
