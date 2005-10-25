/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.operations.tests.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.environment.ILog;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class BaseOperation extends AbstractDataModelOperation {
	private Vector resultList;
	private Vector undoList;
	private IStatus status;
  private boolean checkModels;
  private boolean modelsOK = false;

	public BaseOperation(String id, Vector resultList, Vector undoList) {
		setID(id);
		this.resultList = resultList;
		this.undoList = undoList;
        
		status = Status.OK_STATUS;
	}

	public void setStatus(IStatus status) {
		this.status = status;
	}

	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		resultList.add(this);
    
    if( checkModels ) modelsOK = checkModels();
    
    getEnvironment().getLog().log( ILog.OK, 1234, this, "BaseOperation", (Throwable)null );
    
		return status;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return Status.OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		resultList.remove(resultList.size() - 1);
		undoList.add(this);
		return Status.OK_STATUS;
	}

  public void setCheckModels( boolean checkModels )
  {
    this.checkModels = checkModels;  
  }
  
  public boolean getModelsOK()
  {
    return modelsOK;  
  }
  
  public Set getDataModelIDs()
  {
    HashSet ids = new HashSet();
    
    ids.add( "testprovider1" );
    ids.add( "testprovider2" );
    
    return ids;
  }
  
  private boolean checkModels()
  {
    IDataModel model = getDataModel();
    
    boolean containsModel1 = model.isNestedModel( "testprovider1" );
    boolean containsModel2 = model.isNestedModel( "testprovider2" );
    boolean prop1          = model.isPropertySet( "provider1Prop1" );
    boolean prop2          = model.isPropertySet( "provider1Prop2" );
    boolean prop3          = model.isPropertySet( "provider1Prop3" );
    boolean prop4          = model.isPropertySet( "provider1Prop4" );
    boolean prop5          = model.isPropertySet( "provider2Prop1" );
    boolean prop6          = model.isPropertySet( "provider2Prop2" );
    boolean prop7          = model.isPropertySet( "provider2Prop3" );
    boolean prop8          = model.isPropertySet( "provider2Prop4" );
    boolean value1         = model.getProperty( "provider1Prop1" ).equals( "11" );
    boolean value2         = model.getProperty( "provider1Prop2" ).equals( "22" );
    boolean value3         = model.getProperty( "provider1Prop3" ).equals( "33" );
    boolean value4         = model.getProperty( "provider1Prop4" ).equals( "44" );
    boolean value5         = model.getProperty( "provider2Prop1" ).equals( "1111" );
    boolean value6         = model.getProperty( "provider2Prop2" ).equals( "2222" );
    boolean value7         = model.getProperty( "provider2Prop3" ).equals( "3333" );
    boolean value8         = model.getProperty( "provider2Prop4" ).equals( "4444" );
    
    return containsModel1 && containsModel2 &&
           prop1 && prop2 && prop3 && prop4 && prop5 && prop6 && prop7 && prop8 &&
           value1 && value2 && value3 && value4 && value5 && value6 && value7 && value8;
  }
}
