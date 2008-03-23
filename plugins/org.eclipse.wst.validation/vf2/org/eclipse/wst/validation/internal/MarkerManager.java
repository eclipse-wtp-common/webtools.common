/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;

/**
 * A central place to manage all of the V2 validation markers.
 * @author karasiuk
 *
 */
public class MarkerManager {
	
	private static MarkerManager _me;
	
	public static MarkerManager getDefault(){
		if (_me == null)_me = new MarkerManager();
		return _me;
	}
	
	/**
	 * Clear any validation markers that may have been set by this validator.
	 *  
	 * @param resource the resource that may have it's markers cleared. It can be null, in which case
	 * the operation is a no-op.
	 * @param validatorId the id of validator that created the marker
	 */
	public void clearMarker(IResource resource, String validatorId) throws CoreException {
		if (resource == null)return;
		IMarker[] markers = resource.findMarkers(ValConstants.ProblemMarker, false, IResource.DEPTH_ZERO);
		for (IMarker marker : markers){
			String id = marker.getAttribute(ValidatorMessage.ValidationId, null);
			if (validatorId.equals(id))marker.delete();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void createMarker(ValidatorMessage m, String id){
		try {
			IMarker marker = m.getResource().createMarker(m.getType());
			Map map = m.getAttributes();
			if (map.get(ValidatorMessage.ValidationId) == null)
				map.put(ValidatorMessage.ValidationId, id);
			marker.setAttributes(map);
		}
		catch (CoreException e){
			if (!m.getResource().exists())throw new ResourceUnavailableError(m.getResource());
			ValidationPlugin.getPlugin().handleException(e);
		}
		
	}
	
	/**
	 * Delete all the markers on this resource that were created before the operation start time.
	 * @param resource
	 * @param operationStartTime
	 */
	public void deleteMarkers(IResource resource, long operationStartTime){
		try {
			IMarker[] markers = resource.findMarkers(ValConstants.ProblemMarker, false, IResource.DEPTH_ZERO);
			for (IMarker marker : markers){
				long createTime = marker.getCreationTime();
//				long diff = createTime - operationStartTime;
				if (createTime < operationStartTime)marker.delete();
			}
			
			resource.deleteMarkers(ConfigurationConstants.VALIDATION_MARKER, false, IResource.DEPTH_ZERO);
		}
		catch (CoreException e){
			IProject project = resource.getProject();
			if (!project.exists() || !project.isOpen())throw new ProjectUnavailableError(project);
			if (!resource.exists())throw new ResourceUnavailableError(resource);
			ValidationPlugin.getPlugin().handleException(e);
		}		
	}
	public void makeMarkers(List<IMessage> list){
		for (IMessage message : list){
			IResource res = null;
			Object target = message.getTargetObject();
			if (target != null && target instanceof IResource)res = (IResource)target;
			if (res == null){
				target = message.getAttribute(IMessage.TargetResource);
				if (target != null && target instanceof IResource)res = (IResource)target;
			}
			if (res != null){
				try {
					IMarker marker = res.createMarker(ConfigurationConstants.VALIDATION_MARKER);
					marker.setAttribute(IMarker.MESSAGE, message.getText());
					int markerSeverity = IMarker.SEVERITY_INFO;
					int sev = message.getSeverity();
					if ((sev & IMessage.HIGH_SEVERITY) != 0)markerSeverity = IMarker.SEVERITY_ERROR;
					else if ((sev & IMessage.NORMAL_SEVERITY) != 0)markerSeverity = IMarker.SEVERITY_WARNING;
					marker.setAttribute(IMarker.SEVERITY, markerSeverity);
					marker.setAttribute(IMarker.LINE_NUMBER, message.getLineNumber());
				}
				catch (CoreException e){
					ValidationPlugin.getPlugin().handleException(e);
				}				
			}
		}
	}

}
