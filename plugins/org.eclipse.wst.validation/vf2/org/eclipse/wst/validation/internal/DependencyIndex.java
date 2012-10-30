/*******************************************************************************
 * Copyright (c) 2007, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.DependentResource;
import org.eclipse.wst.validation.IDependencyIndex;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * A simple implementation of the IDependencyIndex. This will probably be
 * replaced with a higher performance, more robust index, at some point in the
 * future.
 * <p>
 * The format of the index is:
 * 
 * <pre>
 * Version number
 * Number of depends on entries
 *   depends on file name
 *   number of dependent entries
 *     dependent file name
 *     number of validators
 *       validator id
 * </pre>
 * 
 * @author karasiuk
 */
public class DependencyIndex implements IDependencyIndex, ISaveParticipant {
	
	/**
	 * An index so that we can determine which things depend on this resource.
	 */
	private Map<IResource,Set<Depends>>		_dependsOn;
	
	/**
	 * An index so that we can determine who the resource depends on.
	 */
	private Map<IResource,Set<Depends>>		_dependents;
	private boolean _dirty;
	
	private static IResource[] EmptyResources = new IResource[0];
	
	/** Version of the persistent index. */
	private static final int CurrentVersion = 1;

	public synchronized void add(String id, IResource dependent, IResource dependsOn) {
		init();
		if (dependsOn == null || dependent == null)return;
		Depends d = getOrCreateDepends(dependent, dependsOn);
		if (d.getValidators().add(id))_dirty = true;
	}
	
	private Depends getOrCreateDepends(IResource dependent, IResource dependsOn) {
		Set<Depends> set = getSet(_dependents, dependent);
		for (Depends d : set){
			if (d.getDependsOn() != null && d.getDependsOn().equals(dependsOn)) return d;
		}
		Depends d = new Depends(dependent, dependsOn);
		_dirty = true;
		set.add(d);
		
		getSet(_dependsOn, dependsOn).add(d);
		return d;
	}

	/**
	 * Answer the set for the resource, creating it if you need to.
	 */
	private Set<Depends> getSet(Map<IResource, Set<Depends>> map, IResource resource) {
		Set<Depends> set = map.get(resource);
		if (set == null){
			set = new HashSet<Depends>(5);
			map.put(resource, set);
		}
		return set;
	}

	/**
	 * Restore the dependency index. See the class comment for the structure.
	 */	
	private void init() {
		if (_dependsOn != null)return;
		
		boolean error = false;
		File f = getIndexLocation();
		if (!f.exists() || f.length() == 0){
			_dependsOn = new HashMap<IResource,Set<Depends>>(100);
			_dependents = new HashMap<IResource,Set<Depends>>(100);
		}
		else {
			String errorMessage = ValMessages.Error21; 
			DataInputStream in = null;
			try {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				in = new DataInputStream(new FileInputStream(f));
				
				int version = in.readInt();
				if (version != CurrentVersion){
					error = true;
					String msg = NLS.bind(ValMessages.ErrDependencyVersion, CurrentVersion);
					throw new IllegalStateException(msg);
				}
				int numDependsOn = in.readInt();
				_dependsOn = new HashMap<IResource,Set<Depends>>(numDependsOn+100);
				_dependents = new HashMap<IResource,Set<Depends>>(numDependsOn+100);
				for (int i=0; i<numDependsOn; i++){
					String v = in.readUTF();
					IResource dependsOn = root.findMember(v);
					if (dependsOn == null){
						Tracing.log(NLS.bind(errorMessage, v));
					}
					int numDependents = in.readInt();
					for (int j=0; j<numDependents; j++){
						v = in.readUTF();
						IResource dependent = root.findMember(v);
						if (dependent == null){
							Tracing.log(NLS.bind(errorMessage, v));
						}
						int numVal = in.readInt();
						for (int k=0; k<numVal; k++){
							String id = in.readUTF();
							if (dependent != null && dependsOn != null)add(id, dependent, dependsOn);
						}
					}					
				}				
			}
			catch (EOFException e){
				Tracing.log("Unable to read the dependency index file because of EOF exception");  //$NON-NLS-1$
			}
			catch (IOException e){
				error = true;
				ValidationPlugin.getPlugin().handleException(e);
			}
			finally {
				Misc.close(in);
				if (error){
					_dependsOn = new HashMap<IResource,Set<Depends>>(100);
					_dependents = new HashMap<IResource,Set<Depends>>(100);
					f.delete();
				}
			}			
		}
	}

	public synchronized void clear(IProject project) {
		init();
		for (Map.Entry<IResource,Set<Depends>> me : _dependents.entrySet()){
			IResource key = me.getKey();
			if (key != null && key.getProject() == project){
				for (Depends d : me.getValue()){
					if (d.delete())_dirty = true;
				}
			}
		}
	}

	public synchronized IResource[] get(String validatorId, IResource dependsOn) {
		init();
		List<IResource> list = new LinkedList<IResource>();
		Set<Depends> set = getSet(_dependsOn, dependsOn);
		for (Depends d : set){
			for (String id : d.getValidators()){
				if (validatorId.equals(id))list.add(d.getDependent());
			}
		}
		
		if (list.size() == 0)return EmptyResources;
		IResource[] resources = new IResource[list.size()];
		list.toArray(resources);
		return resources;
	}

	
	public synchronized List<DependentResource> get(IResource dependsOn) {
		init();
		List<DependentResource> list = new LinkedList<DependentResource>();
		Set<Depends> set = getSet(_dependsOn, dependsOn);
		ValManager vm = ValManager.getDefault();
		for (Depends d : set){
			for (String id : d.getValidators()){
				Validator v = vm.getValidator(id, d.getDependent().getProject());
				if (v != null)list.add(new DependentResource(d.getDependent(), v));
			}
		}
		return list;
	}


	public synchronized void set(String id, IResource dependent, IResource[] dependsOn) {
		init();
		Set<Depends> set = getSet(_dependents, dependent);
		for (Depends d : set){
			if (d.delete(id))_dirty = true;
		}
		if (dependsOn != null){
			for (IResource d : dependsOn)add(id, dependent, d);
		}
	}
		
	public synchronized boolean isDependedOn(IResource resource) {
		init();
		Set<Depends> set = _dependsOn.get(resource);
		if (set == null || set.size() == 0)return false;
		return true;
	}

	public void doneSaving(ISaveContext context) {	
	}
	
	public void prepareToSave(ISaveContext context) throws CoreException {	
	}
	
	public void rollback(ISaveContext context) {
	}
	
	/**
	 * Persist the dependency index. See the class comment for the structure.
	 */
	public synchronized void saving(ISaveContext context) throws CoreException {
		if (!_dirty)return;
		_dirty = false;
		boolean error = false;
		DataOutputStream out = null;
		File f = null;
		try {
			f = getIndexLocation();
			out = new DataOutputStream(new FileOutputStream(f));
			out.writeInt(CurrentVersion);
			Map<String, Set<DependsResolved>> map = compress(_dependsOn);
			out.writeInt(map.size());
			for (Map.Entry<String, Set<DependsResolved>> me : map.entrySet()){
				out.writeUTF(me.getKey());
				Set<DependsResolved> set = me.getValue();
				out.writeInt(set.size());
				for (DependsResolved d : set){
					out.writeUTF(d.resource);
					out.writeInt(d.validators.size());
					for (String id : d.validators){
						out.writeUTF(id);
					}
				}
			}
		}
		catch (IOException e){
			error = true;
			ValidationPlugin.getPlugin().handleException(e);
		}
		finally {		
			Misc.close(out);
			if (error)f.delete();
		}
	}

	private Map<String, Set<DependsResolved>> compress(Map<IResource, Set<Depends>> dependsOn) {
		Map<String, Set<DependsResolved>> map = new HashMap<String, Set<DependsResolved>>(dependsOn.size());
		for (Map.Entry<IResource, Set<Depends>> me : dependsOn.entrySet()){
			Set<DependsResolved> set = new HashSet<DependsResolved>(me.getValue().size());
			for (Depends d : me.getValue()){
				IPath path = d.getDependent().getFullPath();
				if (path != null){
					DependsResolved dr = new DependsResolved(path.toPortableString(), d.getValidators());
					if (dr.validators.size() > 0){
						set.add(dr);
					}
				}				
			}
			if (set.size() > 0){
				IResource res = me.getKey();
				if (res != null){
					IPath path = res.getFullPath();
					if (path != null)map.put(path.toPortableString(), set);
				}
			}
		}
		return map;
	}

	private File getIndexLocation() {
		IPath path = ValidationPlugin.getPlugin().getStateLocation().append("dep.index"); //$NON-NLS-1$
		return path.toFile();
	}

	/**
	 * Keep track of a relationship between a dependent and the thing that it
	 * depends on.
	 * 
	 * @author karasiuk
	 * 
	 */
	private final static class Depends {

		/** The resource that is being depended on, for example a.xsd */
		private final IResource _dependsOn;

		/** The resource that is dependent, for example a.xml */
		private final IResource _dependent;

		/** The id's of the validators that have asserted the dependency. */
		private final Set<String> _validators;

		public Depends(IResource dependent, IResource dependsOn) {
			_dependent = dependent;
			_dependsOn = dependsOn;
			_validators = new HashSet<String>(5);
		}

		/**
		 * Answer true if the id was deleted.
		 */
		public boolean delete(String id) {
			return _validators.remove(id);
		}

		/**
		 * Delete all the dependency assertions for all of your validators.
		 * @return false if there was nothing to delete
		 */
		public boolean delete() {
			boolean deleted = _validators.size() > 0;
			if (deleted)_validators.clear();
			return deleted;
		}

		public IResource getDependsOn() {
			return _dependsOn;
		}

		public IResource getDependent() {
			return _dependent;
		}

		public Set<String> getValidators() {
			return _validators;
		}
}

	private final static class DependsResolved {
		final String 		resource;
		final Set<String> validators;
		
		DependsResolved(String resource, Set<String> validators){
			this.resource = resource;
			this.validators = validators;
			
		}
	}


}
