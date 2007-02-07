/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.common.core.search.document.SearchDocument;
import org.eclipse.wst.common.core.search.document.SearchDocumentSet;

public class SearchDocumentSetImpl extends SearchDocumentSet
{
    public Map documentPathMap = new HashMap(); //path --> documetn
	Map documentMap = new HashMap();  // participantId - > list (document)
	
	public SearchDocumentSetImpl()
	{
		super();
	}
    
    public SearchDocument _tempGetSearchDocumetn(String resourcePath)
    {      
       return (SearchDocument)documentPathMap.get(resourcePath);      
    }
    
    public SearchDocument getSearchDocument(String resourcePath, String participantId)
	{
		if(resourcePath == null){
			return null;
		}
		SearchDocument[] documents = getSearchDocuments(participantId);
		for (int i = 0; i < documents.length; i++)
		{
			SearchDocument document = documents[i];
			if(resourcePath.equals(document.getPath())){
				return document;
			}
		}
		return null;
	}

	public SearchDocument[] getSearchDocuments(String participantId)
	{
		
		Object object = documentMap.get(participantId);
		List documentList = null;
		if(object instanceof List){
			documentList = (List)object;
		}
		else{
			documentMap.put(participantId, documentList = new ArrayList());
		}
		return (SearchDocument[]) documentList.toArray(new SearchDocument[documentList.size()]);
		
	}

	public void putSearchDocument(String participantId, SearchDocument document)
	{
		Object object = documentMap.get(participantId);
		List documentList = null;
		if(object instanceof List){
			documentList = (List)object;
		}
		else{
			documentMap.put(participantId, documentList = new ArrayList());
		}
		documentList.add(document);
        documentPathMap.put(document.getPath(), document);
	}

    
    public void dispose()
    {
      try
      {
      for (Iterator i = documentMap.values().iterator(); i.hasNext(); )
      {
        Object o = i.next();
        if (o instanceof List)
        {  
          for (Iterator j = ((List)o).iterator(); j.hasNext(); )
          {
            Object o2 = j.next();
            if (o2 instanceof SearchDocument)
            {  
               SearchDocument searchDocument = (SearchDocument)o2;
               searchDocument.dispose();
            }   
          }          
        }       
      }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

}
