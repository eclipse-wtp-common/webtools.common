/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.libprov.user.internal;


/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DownloadableLibraryComponentAttributes
{
    private final String componentPath;
    private String sourceArchivePath;
    private String javadocArchivePath;
    
    public DownloadableLibraryComponentAttributes( final String componentPath )
    {
        this.componentPath = componentPath;
    }
    
    public String getComponentPath()
    {
        return this.componentPath;
    }
    
    public String getSourceArchivePath()
    {
        return this.sourceArchivePath;
    }
    
    public void setSourceArchivePath( final String sourceArchivePath )
    {
        this.sourceArchivePath = sourceArchivePath;
    }
    
    public String getJavadocArchivePath()
    {
        return this.javadocArchivePath;
    }
    
    public void setJavadocArchivePath( final String javadocArchivePath )
    {
        this.javadocArchivePath = javadocArchivePath;
    }
    
}
