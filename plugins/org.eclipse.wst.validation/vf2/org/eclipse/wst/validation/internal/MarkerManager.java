/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.Validator;
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
	
	private Set<String> _markers = new HashSet<String>(50);
	
	public static MarkerManager getDefault(){
		if (_me == null)_me = new MarkerManager();
		return _me;
	}
	
	private MarkerManager(){
		_markers.add(ValConstants.ProblemMarker);
		_markers.add(ConfigurationConstants.VALIDATION_MARKER);
	}
	
	/**
	 * Clear any validation markers that may have been set by this validator.
	 * 
	 * @param resource
	 *            The resource that may have it's markers cleared. It can be
	 *            null, in which case the operation is a no-op.
	 * @param validator
	 *            The validator that created the marker.
	 */
	public void clearMarker(IResource resource, Validator validator) throws CoreException {
		if (resource == null)return;
		hook(resource);
		
		String id = validator.getMarkerId();
		if (id != null){
			resource.deleteMarkers(id, true, IResource.DEPTH_ZERO);
			return;
		}
				
		IMarker[] markers = resource.findMarkers(ValConstants.ProblemMarker, true, IResource.DEPTH_ZERO);
		String valId = validator.getId();
		for (IMarker marker : markers){
			id = marker.getAttribute(ValidatorMessage.ValidationId, null);
			if (valId.equals(id))marker.delete();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void createMarker(ValidatorMessage m, String id){
		try {
			IResource resource = m.getResource();
			hook(resource);
			IMarker marker = resource.createMarker(m.getType());
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
	 * Delete all the markers on this resource that were created before the
	 * operation start time.
	 * 
	 * @param resource
	 *            The resource that is having it's markers deleted.
	 * @param operationStartTime
	 *            The time as returned by System.currentTimeMillis().
	 * @param depth
	 *            The depth of the markers to clear. It is one of the
	 *            IResource.DEPTH_XXX constants.
	 */
	public void deleteMarkers(IResource resource, long operationStartTime, int depth){
		try {
			hook(resource); 
			IMarker[] markers = resource.findMarkers(null, true, depth);
			for (IMarker marker : markers){
				if (_markers.contains(marker.getType())){
					long createTime = marker.getCreationTime();
					if (createTime < operationStartTime){
						try {
							marker.delete();
						}
						catch (CoreException e){
							// eat it - there is nothing we can do about this.
						}
					}
				}
			}
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
					hook(res);
					String id = message.getMarkerId();
					if (id == null)id = ConfigurationConstants.VALIDATION_MARKER;
					IMarker marker = res.createMarker(id);
					marker.setAttributes(message.getAttributes());
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
	
	/**
	 * A debugging method. A place to put break points, so that you can figure out what sort of marker
	 * changes are happening.
	 * 
	 * @param resource
	 */
	public void hook(IResource resource){
//		String name = "first.test2x";
//		if (resource.getName().equals(name)){
//			String markers = Misc.listMarkers(resource);
//			Tracing.log("MarkerManager has hooked: ", name); //$NON-NLS-1$
//			RuntimeException rt = new RuntimeException("hooking " + name);
//			rt.printStackTrace();
//		}
	}

	public Set<String> getMarkers() {
		return _markers;
	}

}
