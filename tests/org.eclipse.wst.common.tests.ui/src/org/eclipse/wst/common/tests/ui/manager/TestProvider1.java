/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.tests.ui.manager;

import java.util.Set;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;

public class TestProvider1 extends AbstractDataModelProvider
{
  public Set getPropertyNames(){
    Set propertyNames = super.getPropertyNames();
    propertyNames.add("provider1Prop1");
    propertyNames.add("provider1Prop2");
    propertyNames.add("provider1Prop3");
    propertyNames.add("provider1Prop4");
    
    return propertyNames;
  }

  public void init()
  {
    setProperty( "provider1Prop1", "11" );
    setProperty( "provider1Prop2", "22" );
    setProperty( "provider1Prop3", "33" );
    setProperty( "provider1Prop4", "44" );
  }

  public String getID()
  {
    return "testprovider1";
  }
  

}
