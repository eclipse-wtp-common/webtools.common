/*
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.internal.boot.PlatformURLConnection;
import org.eclipse.core.internal.boot.PlatformURLHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;

/**
 * @author mdelder
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class PlatformURLModuleConnection extends PlatformURLConnection {

	public static final String MODULE = "module"; //$NON-NLS-1$
	public static final String MODULE_PROTOCOL = MODULE + PlatformURLHandler.PROTOCOL_SEPARATOR;
	
	public static final String CLASSPATH = "classpath"; //$NON-NLS-1$

	public static final String RESOURCE_MODULE = "resource"; //$NON-NLS-1$
	public static final String BINARY_MODULE = "binary"; //$NON-NLS-1$


	public PlatformURLModuleConnection(URL aURL) {
		super(aURL);
	}

	// TODO FIX THIS --MDE
	public static URI resolve(URI aModuleResourceDeployPath) throws IOException {
		StructureEdit moduleCore = null;
		URI resolvedURI = null;
		try {
			//IResource eclipseResource = null;
			//IContainer eclipseContainer = null;
			moduleCore = StructureEdit.getStructureEditForRead(StructureEdit.getContainingProject(aModuleResourceDeployPath));			
			ComponentResource[] resources = moduleCore.findResourcesByRuntimePath(aModuleResourceDeployPath);
			URI deployPathSegment = ModuleURIUtil.trimToDeployPathSegment(aModuleResourceDeployPath);
// THIS ALGORITHM WILL NOT HANDLE URI OVERLAPS, 
//			it only works when the deploypath is wholly contained within the found resource 
// 			
//			for (int resourceIndex = 0; resourceIndex < resources.length; resourceIndex++) {
			if(resources.length == 1) {
				if (resources[0].getRuntimePath().equals(deployPathSegment))
					return URI.createPlatformResourceURI(normalizeToWorkspaceRelative(resources[0].getSourcePath(),aModuleResourceDeployPath).toString());
				return URI.createPlatformResourceURI(resources[0].getSourcePath().append(deployPathSegment.path()).toString());
			}
//
//				eclipseResource = ModuleCore.getResource(resources[resourceIndex]);
//				if (eclipseResource.getType() == IResource.FOLDER) {
//					eclipseContainer = ((IContainer) eclipseResource);
//					URI containerURI = URI.createURI(eclipseContainer.getFullPath().toString()); 
//					for(int i=0; i<containerURI.segmentCount();i++) {
//						if(containerURI.segment(i).equals(deployPathSegment.segment(deployPathSegmentIndex)))
//					}
//					System.out.println(eclipseResourceURI);
//					
//				}

//			}
		} catch (UnresolveableURIException uurie) {
			throw new IOException(uurie.toString());
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}
		return resolvedURI;
	}

	private static URI normalizeToWorkspaceRelative(IPath sourcePath, URI moduleResourceDeployPath) throws UnresolveableURIException {
		
		String projectName = sourcePath.segment(0);
		
		return URI.createURI(projectName + '/' + sourcePath.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.boot.PlatformURLConnection#resolve()
	 */
	protected URL resolve() throws IOException {
		System.out.println("URL: " + getURL());
		//IPath moduleRelativePath = new Path(getURL().toExternalForm());
		//String moduleName = moduleRelativePath.segment(1);

		IPath resolvedPath = null; // handle.getResolvedPath().append(moduleRelativePath.removeFirstSegments(2));

		int count = resolvedPath.segmentCount();
		// if there are two segments then the second is a project name.
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(resolvedPath.segment(0));
		if (!project.exists()) {
			// TODO Fix this string
			String message = "Could not resolve URL"; //Policy.bind("url.couldNotResolve", project.getName(), url.toExternalForm()); //$NON-NLS-1$
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
		PlatformURLHandler.register(PlatformURLModuleConnection.MODULE, PlatformURLModuleConnection.class);
	}

	// private IProject getRelevantProject(URI aModuleURI) {
	// aModuleURI.segment()
	// }
}
