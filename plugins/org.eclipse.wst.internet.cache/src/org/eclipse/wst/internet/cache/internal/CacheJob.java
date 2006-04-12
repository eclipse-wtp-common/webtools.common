/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.internet.cache.internal;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A cache job runs once an hour to cache any prespecified resources which
 * should be cached and any resources for which an attempt was previously 
 * made to cache them but they were unable to be cached.
 */
public class CacheJob extends Job
{
  private String[] specifiedURIsToCache = null;
  private static final long SCHEDULE_TIME = 3600000;

  /**
   * Constructor.
   */
  public CacheJob()
  {
    super(CacheMessages._UI_CACHE_MONITOR_NAME);
    //specifiedURIsToCache = ToCacheRegistryReader.getInstance().getURIsToCache();
  }

  /**
   * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
   */
  protected IStatus run(IProgressMonitor monitor)
  {
    Cache cache = Cache.getInstance();
    String[] uncachedURIs = cache.getUncachedURIs();
    int numUncachedURIs = uncachedURIs.length;
    // Special case for the first time the job is run which will attemp to 
    // cache specified resources.
    if(specifiedURIsToCache != null)
    {
      int numSpecifiedURIs = specifiedURIsToCache.length;
      String[] temp = new String[numUncachedURIs + numSpecifiedURIs];
      System.arraycopy(specifiedURIsToCache, 0, temp, 0, numSpecifiedURIs);
      System.arraycopy(uncachedURIs, 0, temp, numSpecifiedURIs, numUncachedURIs);
      uncachedURIs = temp;
      numUncachedURIs = uncachedURIs.length;
      specifiedURIsToCache = null;
    }

    cache.clearUncachedURIs();
    monitor.beginTask(CacheMessages._UI_CACHE_MONITOR_NAME, numUncachedURIs);
    try
    {
      for(int i = 0; i < numUncachedURIs; i++)
      {
        if (monitor.isCanceled())
        {
          for(int j = i; j < numUncachedURIs; j++)
          {
            cache.addUncachedURI(uncachedURIs[j]);
          }
          return Status.CANCEL_STATUS;
        }
        String uri = uncachedURIs[i];
        monitor.subTask(MessageFormat.format(CacheMessages._UI_CACHE_MONITOR_CACHING, new Object[]{uri}));
        cache.getResource(uri);
        monitor.worked(1);
        monitor.subTask("");
      }
      monitor.done();
      return Status.OK_STATUS;
    } 
    finally
    {
      schedule(SCHEDULE_TIME); // schedule the next time the job should run
    }
  }

}


