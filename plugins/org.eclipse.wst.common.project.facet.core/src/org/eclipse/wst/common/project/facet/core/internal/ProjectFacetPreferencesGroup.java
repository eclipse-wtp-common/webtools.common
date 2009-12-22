/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.util.internal.DomUtil.elements;
import static org.eclipse.wst.common.project.facet.core.util.internal.DomUtil.root;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.util.internal.FileUtil;
import org.eclipse.wst.common.project.facet.core.util.internal.XmlWriter;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetPreferencesGroup
{
    private static final String PATH_IN_PROJECT 
        = ".settings/org.eclipse.wst.common.project.facet.core.prefs.xml"; //$NON-NLS-1$
    
    private static final String PATH_IN_WORKSPACE 
        = ".metadata/.plugins/org.eclipse.wst.common.project.facet.core/prefs.xml"; //$NON-NLS-1$
    
    private static final String EL_ROOT = "root"; //$NON-NLS-1$
    private static final String EL_FACET = "facet"; //$NON-NLS-1$
    private static final String EL_NODE = "node"; //$NON-NLS-1$
    private static final String EL_ATTRIBUTE = "attribute"; //$NON-NLS-1$
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_NAME = "name"; //$NON-NLS-1$
    private static final String ATTR_VALUE = "value"; //$NON-NLS-1$
    
    private final IFacetedProject project;
    private final LinkedHashMap<String,ProjectFacetPreferences> preferences;
    
    public ProjectFacetPreferencesGroup( final IFacetedProject project )
    
        throws BackingStoreException
        
    {
        this.project = project;
        this.preferences = new LinkedHashMap<String,ProjectFacetPreferences>();
        
        final InputStream in = getBackingFileContents();
        
        if( in != null )
        {
            try
            {
                final Reader reader
                    = new InputStreamReader( new BufferedInputStream( in ), XmlWriter.ENCODING );
                
                for( Element elFacet : elements( root( reader ), EL_FACET ) )
                {
                    final String facetId = elFacet.getAttribute( ATTR_ID );
                    final ProjectFacetPreferences root = new ProjectFacetPreferences( this, facetId, project );
                    
                    read( root, elFacet );
                    
                    this.preferences.put( facetId, root );
                }
            }
            catch( Exception e )
            {
                throw new BackingStoreException( e.getMessage(), e );
            }
            finally
            {
                try
                {
                    in.close();
                }
                catch( IOException e ) {}
            }
        }
    }
    
    public ProjectFacetPreferences getPreferences( final IProjectFacet facet )
    
        throws BackingStoreException
        
    {
        ProjectFacetPreferences prefs;
        
        synchronized( this )
        {
            final String facetId = facet.getId();
            prefs = this.preferences.get( facetId );
            
            if( prefs == null )
            {
                prefs = new ProjectFacetPreferences( this, facetId, this.project );
                this.preferences.put( facetId, prefs );
            }
        }
        
        return prefs;
    }
    
    public void removePreferences( final IProjectFacet facet )
    
        throws BackingStoreException
        
    {
        synchronized( this )
        {
            this.preferences.remove( facet.getId() );
        }
        
        save();
    }
    
    public void save()
    
        throws BackingStoreException
        
    {
        try
        {
            final File file = getBackingFile();
            StringWriter w = null;
            
            synchronized( this )
            {
                if( ! this.preferences.isEmpty() )
                {
                    w = new StringWriter();;
                    write( w );
                }
            }
            
            if( w == null )
            {
                FileUtil.deleteFile( file );
            }
            else
            {
                FileUtil.writeFile( file, w.toString() );
            }
        }
        catch( IOException e )
        {
            throw new BackingStoreException( e.getMessage(), e );
        }
        catch( CoreException e )
        {
            throw new BackingStoreException( e.getMessage(), e );
        }
    }
    
    private File getBackingFile()
    {
        final File file;
        
        if( this.project != null )
        {
            final IFile f = this.project.getProject().getFile( PATH_IN_PROJECT );
            file = f.getLocation().toFile();
        }
        else
        {
            final File wsroot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
            file = new File( wsroot, PATH_IN_WORKSPACE );
        }
        
        return file;
    }
    
    private InputStream getBackingFileContents()
    
        throws BackingStoreException
        
    {
        InputStream in = null;
        
        if( this.project != null )
        {
            final IFile f = this.project.getProject().getFile( PATH_IN_PROJECT );
            
            if( f.exists() )
            {
                try
                {
                    in = f.getContents();
                }
                catch( CoreException e )
                {
                    throw new BackingStoreException( e.getMessage(), e );
                }
            }
        }
        else
        {
            final File wsroot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
            final File file = new File( wsroot, PATH_IN_WORKSPACE );
            
            if( file.exists() )
            {
                try
                {
                    in = new FileInputStream( file );
                }
                catch( IOException e )
                {
                    throw new BackingStoreException( e.getMessage(), e );
                }
            }
        }
        
        return in;
    }
    
    private void read( final ProjectFacetPreferences preferences,
                       final Element node )
    {
        for( Element elAttr : elements( node, EL_ATTRIBUTE ) )
        {
            final String name = elAttr.getAttribute( ATTR_NAME );
            final String value = elAttr.getAttribute( ATTR_VALUE );
            preferences.put( name, value );
        }
        
        for( Element elNode : elements( node, EL_NODE ) )
        {
            final String name = elNode.getAttribute( ATTR_NAME );
            final ProjectFacetPreferences child = (ProjectFacetPreferences) preferences.node( name );
            read( child, elNode );
        }
    }
    
    private void write( final Writer w )
    
        throws IOException, BackingStoreException
        
    {
        final XmlWriter xml = new XmlWriter( w );
        
        xml.startElement( EL_ROOT );
        
        for( Map.Entry<String,ProjectFacetPreferences> entry : this.preferences.entrySet() )
        {
            xml.startElement( EL_FACET );
            xml.addAttribute( ATTR_ID, entry.getKey() );
            write( xml, entry.getValue() );
            xml.endElement();
        }
        
        xml.endElement();
        xml.flush();
    }
    
    private void write( final XmlWriter xml,
                        final Preferences preferences )
    
        throws IOException, BackingStoreException
        
    {
        for( String key : preferences.keys() )
        {
            xml.startElement( EL_ATTRIBUTE );
            xml.addAttribute( ATTR_NAME, key );
            xml.addAttribute( ATTR_VALUE, preferences.get( key, null ) );
            xml.endElement();
        }
        
        for( String childName : preferences.childrenNames() )
        {
            final Preferences child = preferences.node( childName );
            
            xml.startElement( EL_NODE );
            xml.addAttribute( ATTR_NAME, child.name() );
            write( xml, child );
            xml.endElement();
        }
    }
    
}
