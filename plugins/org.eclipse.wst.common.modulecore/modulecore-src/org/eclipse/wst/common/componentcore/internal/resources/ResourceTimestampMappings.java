/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.internal.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * Maps resources to cached data and remembers when the data was cached based on a timestamp
 * signature of a given resource.
 * 
 */
public class ResourceTimestampMappings {

	private static final IPath COMPONENT_MANIFEST_PATH = new Path(".settings/org.eclipse.wst.common.component");
	private static final IPath FACET_CONFIG_PATH = new Path(".settings/org.eclipse.wst.common.project.facet.core.xml");

	private static final Object NO_DATA = new Object();
	private static final Object ERROR = new Object();

	private final Map timestamps = new HashMap();
	private final Map data = new HashMap();


	/**
	 * Record a timestamp signature for the given resource.
	 * <p>
	 * Clients may use {@link #hasChanged(IResource)} to determine if the resource has changed since
	 * it was recorded.
	 * </p>
	 * 
	 * @param resource
	 *            The resource that was processed.
	 * @return True if the recording was successfull. The recording may fail if the resource does
	 *         not exist.
	 * 
	 * @see #hasChanged(IResource)
	 */
	public synchronized boolean mark(IResource resource) {
		return mark(resource, NO_DATA);
	}

	/**
	 * Cache the data that was determined when processing the resource. The a timestamp signature
	 * will be recorded for the resource as well.
	 * 
	 * <p>
	 * Clients may use {@link #hasChanged(IResource)} to determine if the cached data should be
	 * replaced.
	 * </p>
	 * 
	 * @param resource
	 *            The resource that was processed.
	 * @param cachedData
	 *            The data that was determined when the Resource was processed.
	 * 
	 * @return True if the recording was successfull. The recording may fail if the resource does
	 *         not exist.
	 * 
	 * 
	 * @see #hasChanged(IResource)
	 * @see #hasCacheData(IResource)
	 */
	public synchronized boolean mark(IResource resource, Object cachedData) {

		if (resource.getModificationStamp() != IResource.NULL_STAMP) {
			if (timestamps.containsKey(resource)) {
				TimestampSignature signature = (TimestampSignature) timestamps.get(resource);
				signature.update(resource);
			} else {
				timestamps.put(resource, createTimestampSignature(resource));
			}
			data.put(resource, cachedData);
			return true;
		}
		return false;
	}

	/**
	 * Note that an error occurred when processing this resource.
	 * 
	 * @param resource
	 *            The resource that had some sort of error while processing.
	 * @return True if the error was recorded. The error may not be recorded if the resource does
	 *         not exist.
	 * @see #hasCacheError(IResource)
	 */
	public synchronized boolean markError(IResource resource) {

		if (resource.getModificationStamp() != IResource.NULL_STAMP) {
			if (timestamps.containsKey(resource)) {
				TimestampSignature signature = (TimestampSignature) timestamps.get(resource);
				signature.update(resource);
			} else {
				timestamps.put(resource, createTimestampSignature(resource));
			}
			data.put(resource, ERROR);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param resource
	 *            The resource that was or is about to be processed.
	 * @return True if the given resource has changed in a noticeable way since it was marked.
	 * 
	 * @see #mark(IResource)
	 * @see #mark(IResource, Object)
	 * @see #markError(IResource)
	 */
	public boolean hasChanged(IResource resource) {
		TimestampSignature signature = (TimestampSignature) timestamps.get(resource);
		return signature == null || signature.hasChanged(resource);
	}

	/**
	 * 
	 * @param resource
	 *            The resource that was or is about to be processed.
	 * @return True if there is any data cached for the given resource.
	 * 
	 * @see #mark(IResource)
	 * @see #mark(IResource, Object)
	 * @see #markError(IResource)
	 */
	public boolean hasCacheData(IResource resource) {
		Object datum = data.get(resource);
		return datum != null && datum != NO_DATA;

	}


	/**
	 * 
	 * @param resource
	 *            The resource that was or is about to be processed.
	 * @return True if there is any data cached for the given resource.
	 * 
	 * @see #mark(IResource)
	 * @see #mark(IResource, Object)
	 * @see #markError(IResource)
	 */
	public boolean hasCacheError(IResource resource) {
		return data.get(resource) == ERROR;
	}

	public Object getData(IResource resource) {
		Object datum = data.get(resource);
		if (datum != NO_DATA)
			return datum;
		return null;
	}


	private TimestampSignature createTimestampSignature(IResource resource) {
		switch (resource.getType()) {
			case IResource.PROJECT :
				return new ProjectTimestamp((IProject) resource);
			default :
				return new SimpleResourceTimestamp(resource);
		}
	}

	/**
	 * Provides a point in time signature of a Resource to determine whether that resource has
	 * changed in a meaningful way since the time this signature was created or last updated.
	 * 
	 */
	public interface TimestampSignature {

		/**
		 * 
		 * @param resource
		 *            A resource related to this signature
		 * @return True if the current resource is different from this signature in a meaningful way
		 */
		boolean hasChanged(IResource resource);

		/**
		 * 
		 * @param resource
		 *            Update the signature details to the given resource's signature.
		 */
		void update(IResource resource);

	}

	/**
	 * Provides a signature based on the modificationStamp of a resource.
	 */
	public class SimpleResourceTimestamp implements TimestampSignature {
		private long timestamp = 0;

		public SimpleResourceTimestamp(IResource resource) {
			update(resource);
		}

		public boolean hasChanged(IResource resource) {
			return timestamp != resource.getModificationStamp();
		}

		public void update(IResource resource) {
			timestamp = resource.getModificationStamp();
		}
	}

	/**
	 * Provides a signature for a project based on the modificationStamp of the (1) project, (2) the
	 * component manifest, and (3) the facet configuration
	 */
	public class ProjectTimestamp implements TimestampSignature {

		private long projectTimestamp = 0;
		private long componentManifestTimestamp = 0;
		private long facetConfigTimestamp = 0;

		public ProjectTimestamp(IProject project) {
			update(project);
		}

		public boolean hasChanged(IResource resource) {
			if (resource.getType() == IResource.PROJECT) {
				IProject project = (IProject) resource;
				if (projectTimestamp != project.getModificationStamp())
					return true;

				IFile file = project.getFile(COMPONENT_MANIFEST_PATH);
				if (!file.exists() || componentManifestTimestamp != file.getModificationStamp())
					return true;

				file = project.getFile(FACET_CONFIG_PATH);
				if (!file.exists() || facetConfigTimestamp != file.getModificationStamp())
					return true;

				return false;
			}
			return true;

		}

		public void update(IResource resource) {

			if (resource instanceof IProject) {

				IProject project = (IProject) resource;

				projectTimestamp = project.getModificationStamp();

				IFile file = project.getFile(COMPONENT_MANIFEST_PATH);
				componentManifestTimestamp = file.getModificationStamp();

				file = project.getFile(FACET_CONFIG_PATH);
				facetConfigTimestamp = file.getModificationStamp();

			} else {
				projectTimestamp = componentManifestTimestamp = facetConfigTimestamp = IResource.NULL_STAMP;
			}

		}
	}

}
