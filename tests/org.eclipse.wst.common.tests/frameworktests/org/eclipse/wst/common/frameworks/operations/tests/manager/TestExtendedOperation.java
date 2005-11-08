/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.operations.tests.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class TestExtendedOperation extends BaseOperation
{

  public TestExtendedOperation()
  {
    super( "TestExtendedOperation", null, null);
  }
  
  public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException 
  {
    IDataModel model = getDataModel();
    Vector     resultList = (Vector)model.getProperty( "executedOps" );
    resultList.add(this);
        
    return Status.OK_STATUS;
  }
  
  public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
    IDataModel model = getDataModel();
    Vector     resultList = (Vector)model.getProperty( "executedOps" );
    Vector     undoList = (Vector)model.getProperty( "executedUndoOps" );
    
    resultList.remove(resultList.size() - 1);
    undoList.add(this);
    return Status.OK_STATUS;
  }
  
  public Set getDataModelIDs()
  {
    HashSet ids = new HashSet();
    
    ids.add( "testExtendedProvider" );
    
    return ids;
  }
  
}
