/*
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.modulecore.impl;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.internal.boot.PlatformURLConnection;
import org.eclipse.core.internal.boot.PlatformURLHandler;
import org.eclipse.core.internal.utils.Policy;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.ModuleStructuralModel;
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;
import org.eclipse.wst.common.modulecore.util.ModuleCore;


/**
 * @author mdelder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlatformURLModuleConnection extends PlatformURLConnection {
     
    public static final String MODULE = "module";  //$NON-NLS-1$
    public static final String MODULE_PROTOCOL = MODULE + PlatformURLHandler.PROTOCOL_SEPARATOR;
    
    public static final String RESOURCE_MODULE = "resource"; //$NON-NLS-1$
    public static final String BINARY_MODULE = "binary"; //$NON-NLS-1$
    
    
    public PlatformURLModuleConnection(URL aURL) {
        super(aURL);        
    }

    public static URI resolve(URI aURI) throws IOException {
    	ModuleStructuralModel structuralModel = null;
    	URI resolvedURI = null;
    	Object key = new Object();
    	try {
    		structuralModel = ModuleCore.INSTANCE.getModuleStructuralModelForRead(aURI, key);
    		WorkbenchModuleResource resource = ModuleCore.INSTANCE.findWorkbenchModuleResourceByDeployPath(structuralModel, aURI);
    		resolvedURI = resource.getSourcePath();
    	} catch (UnresolveableURIException uurie) {
    		throw new IOException(uurie.toString());
    	} finally {
    		if(structuralModel != null)
    			structuralModel.releaseAccess(key);
    	}
    	return resolvedURI;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.internal.boot.PlatformURLConnection#resolve()
     */
    protected URL resolve() throws IOException {
        System.out.println("URL: " + getURL());
        IPath moduleRelativePath = new Path(getURL().toExternalForm()); 
        String moduleName = moduleRelativePath.segment(1);        
        
        IPath resolvedPath = null; //handle.getResolvedPath().append(moduleRelativePath.removeFirstSegments(2));           

		int count = resolvedPath.segmentCount(); 
		// if there are two segments then the second is a project name.
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(resolvedPath.segment(0));
		if (!project.exists()) {
			String message = Policy.bind("url.couldNotResolve", project.getName(), url.toExternalForm()); //$NON-NLS-1$
			throw new IOException(message);
		}
		IPath result = null;
		if (count == 2)
			result = project.getLocation();
		else {
			resolvedPath = resolvedPath.removeFirstSegments(2);
			result = project.getFile(resolvedPath).getLocation();
		}
		return new URL("file", "", result.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		     
    }
    
    public static void startup() {
        PlatformURLHandler.register(PlatformURLModuleConnection.MODULE,PlatformURLModuleConnection.class);
    }
    
//    private IProject getRelevantProject(URI aModuleURI) {
//    	aModuleURI.segment()
//    }
}
 