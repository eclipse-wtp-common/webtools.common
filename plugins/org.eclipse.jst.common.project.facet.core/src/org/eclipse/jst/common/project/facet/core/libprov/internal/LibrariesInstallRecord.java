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

package org.eclipse.jst.common.project.facet.core.libprov.internal;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.*;

import static org.eclipse.wst.common.project.facet.core.util.internal.DomUtil.element;
import static org.eclipse.wst.common.project.facet.core.util.internal.DomUtil.elements;
import static org.eclipse.wst.common.project.facet.core.util.internal.DomUtil.root;
import static org.eclipse.wst.common.project.facet.core.util.internal.FileUtil.validateEdit;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.common.project.facet.core.libprov.ILibrariesProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.util.internal.XmlWriter;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LibrariesInstallRecord
{
    public static final String FILE_NAME
        = ".settings/org.eclipse.jst.common.project.facet.core.libprov.xml"; //$NON-NLS-1$
    
    private static final String EL_INSTALL_RECORD = "install-record"; //$NON-NLS-1$
    private static final String EL_ENTRY = "entry"; //$NON-NLS-1$
    private static final String EL_FACET = "facet"; //$NON-NLS-1$
    private static final String EL_PROVIDER = "provider"; //$NON-NLS-1$
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    
    public static final class Entry
    {
        private final String facetId;
        private final IProjectFacet facet;
        private final String providerId;
        
        public Entry( final String facetId,
                      final String providerId )
        {
            this.facetId = facetId;
            
            if( ProjectFacetsManager.isProjectFacetDefined( this.facetId ) )
            {
                this.facet = ProjectFacetsManager.getProjectFacet( this.facetId );
            }
            else
            {
                this.facet = null;
            }
            
            this.providerId = providerId;
        }
        
        public Entry( final IProjectFacet facet,
                      final ILibrariesProvider provider )
        {
            this.facet = facet;
            this.facetId = this.facet.getId();
            this.providerId = provider.getId();
        }
        
        public String getProjectFacetId()
        {
            return this.facet.getId();
        }
        
        public IProjectFacet getProjectFacet()
        {
            return this.facet;
        }
        
        public String getLibrariesProviderId()
        {
            return this.providerId;
        }
    }
    
    private final IProject project;
    private final IFile installRecordFile;
    private final Set<Entry> entries;
    private final Set<Entry> entriesReadOnly;
    
    public LibrariesInstallRecord( final IProject project )
    
        throws CoreException
        
    {
        this.project = project;
        this.installRecordFile = this.project.getFile( FILE_NAME );
        this.entries = new CopyOnWriteArraySet<Entry>();
        this.entriesReadOnly = Collections.unmodifiableSet( this.entries );
        
        if( this.installRecordFile.exists() )
        {
            final InputStream in = this.installRecordFile.getContents();
            boolean corrupted = false;
            
            try
            {
                final Reader reader
                    = new InputStreamReader( new BufferedInputStream( in ), XmlWriter.ENCODING );
                
                for( Element elEntry : elements( root( reader ), EL_ENTRY ) )
                {
                    final Element elFacet = element( elEntry, EL_FACET );
                    
                    if( elFacet == null )
                    {
                        corrupted = true;
                        break;
                    }
                    
                    final String facetId = elFacet.getAttribute( ATTR_ID );
                    
                    if( facetId.length() == 0 )
                    {
                        corrupted = true;
                        break;
                    }
                    
                    final Element elProvider = element( elEntry, EL_PROVIDER );
                    
                    if( elProvider == null )
                    {
                        corrupted = true;
                        break;
                    }
                    
                    final String providerId = elProvider.getAttribute( ATTR_ID );
                    
                    if( providerId.length() == 0 )
                    {
                        corrupted = true;
                        break;
                    }
                    
                    this.entries.add( new Entry( facetId, providerId ) );
                }
            }
            catch( Exception e )
            {
                final String msg
                    = Resources.bind( Resources.exceptionWhileParsing,
                                      this.installRecordFile.getLocation().toString() );
                
                throw new CoreException( createErrorStatus( msg ) );
            }
            finally
            {
                try
                {
                    in.close();
                }
                catch( IOException e ) {}
            }
            
            if( corrupted )
            {
                final String msg 
                    = Resources.bind( Resources.corruptedInstallRecordFile, 
                                      this.installRecordFile.getLocation().toString() );
                
                throw new CoreException( createErrorStatus( msg ) );
            }
        }
    }
    
    public synchronized Set<Entry> getEntries()
    {
        return this.entriesReadOnly;
    }
    
    public synchronized Entry getEntry( final IProjectFacet facet )
    {
        for( Entry entry : this.entries )
        {
            if( entry.getProjectFacet() == facet )
            {
                return entry;
            }
        }
        
        return null;
    }
    
    public synchronized void addEntry( final Entry entry )
    {
        this.entries.add( entry );
    }
    
    public synchronized void removeEntry( final Entry entry )
    {
        this.entries.remove( entry );
    }
    
    public synchronized void removeEntry( final IProjectFacet facet )
    {
        final Entry entry = getEntry( facet );
        
        if( entry != null )
        {
            removeEntry( entry );
        }
    }
    
    public synchronized void save()
    
        throws CoreException
        
    {
        final StringWriter sw = new StringWriter();
        final XmlWriter xmlw = new XmlWriter( sw );
        
        try
        {
            xmlw.startElement( EL_INSTALL_RECORD );
            
            for( Entry entry : this.entries )
            {
                xmlw.startElement( EL_ENTRY );
                
                xmlw.startElement( EL_FACET );
                xmlw.addAttribute( ATTR_ID, entry.getProjectFacetId() );
                xmlw.endElement();
                
                xmlw.startElement( EL_PROVIDER );
                xmlw.addAttribute( ATTR_ID, entry.getLibrariesProviderId() );
                xmlw.endElement();
                
                xmlw.endElement();
            }
            
            xmlw.endElement();
        }
        catch( IOException e )
        {
            // The StringWriter class does not throw IOException, so this should
            // never happen.
            
            throw new RuntimeException( e );
        }
        
        final byte[] bytes;
        
        try
        {
            bytes = sw.toString().getBytes( "UTF-8" ); //$NON-NLS-1$
        }
        catch( UnsupportedEncodingException e )
        {
            // Unexpected. All JVMs are supposed to support UTF-8.
            throw new RuntimeException( e );
        }
        
        final InputStream in = new ByteArrayInputStream( bytes );
        
        if( this.installRecordFile.exists() )
        {
            validateEdit( this.installRecordFile );
            this.installRecordFile.setContents( in, true, false, null );
        }
        else
        {
            final IFolder parent = (IFolder) this.installRecordFile.getParent();
            
            if( ! parent.exists() )
            {
                parent.create( true, true, null );
            }
            
            this.installRecordFile.create( in, true, null );
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String exceptionWhileParsing;
        public static String corruptedInstallRecordFile;
        
        static
        {
            initializeMessages( LibrariesInstallRecord.class.getName(), 
                                Resources.class );
        }
    }
    
}
