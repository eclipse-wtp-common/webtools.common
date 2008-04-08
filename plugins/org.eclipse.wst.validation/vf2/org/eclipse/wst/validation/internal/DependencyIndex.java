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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.DependentResource;
import org.eclipse.wst.validation.IDependencyIndex;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

public class DependencyIndex implements IDependencyIndex, ISaveParticipant {
	
	/**
	 * Keep track of which resources are depended on by other resources.
	 * <p>
	 * The reason we don't store IResources in this map is because the IResource may not
	 * actually exist. (It may have been renamed or deleted before this index was restored)
	 */
	private Map<IPath, Map<IResource,Depends>>		_dependsMap;
	private Map<IProject, List<Depends>>			_projectMap;
	private boolean _dirty;
	
	private static IResource[] EmptyResources = new IResource[0];
	
	/** Version of the persistent index. */
	private static final int CurrentVersion = 1;

	public synchronized void add(String id, IResource dependent, IResource dependsOn) {
		init();
		IPath dependsOnPath = dependsOn.getFullPath();
		add(id,dependent, dependsOnPath);
	}
	
	private void add(String id, IResource dependent, IPath dependsOn){
		Map<IResource, Depends> depends = _dependsMap.get(dependsOn);
		if (depends == null){
			depends = new HashMap<IResource, Depends>(5);
			_dependsMap.put(dependsOn, depends);
		}
		
		Depends d = depends.get(dependent);
		if (d == null){
			d = new Depends();
			depends.put(dependent, d);
		}
		if (d.hasValidator(id))return;
		else {
			d.add(id);
			_dirty = true;
			List<Depends> list = _projectMap.get(dependent.getProject());
			if (list == null){
				list = new LinkedList<Depends>();
				_projectMap.put(dependent.getProject(), list);
			}
			list.add(d);
		}
		
	}

	/**
	 * Restore the dependency index.
	 * <p>
	 * The format of the index is:
	 * <pre>
	 * Version number
	 * Number of depends on entries
	 *   depends on file name
	 *   number of dependent entries
	 *     dependent file name
	 *     number of validators
	 *       validator id
	 * </pre>
	 */	
	private void init() {
		if (_dependsMap != null)return;
		
		boolean error = false;
		File f = getIndexLocation();
		if (!f.exists()){
			_dependsMap = new HashMap<IPath, Map<IResource, Depends>>(100);
			_projectMap = new HashMap<IProject, List<Depends>>(50);
		}
		else {
			DataInputStream in = null;
			try {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				in = new DataInputStream(new FileInputStream(f));
				
				int version = in.readInt();
				if (version != CurrentVersion){
					String msg = NLS.bind(ValMessages.ErrDependencyVersion, CurrentVersion);
					throw new IllegalStateException(msg);
				}
				int numDependsOn = in.readInt();
				_dependsMap = new HashMap<IPath, Map<IResource, Depends>>(numDependsOn+100);
				_projectMap = new HashMap<IProject, List<Depends>>(50);
				for (int i=0; i<numDependsOn; i++){
					String v = in.readUTF();
					IPath dependsOn = Path.fromPortableString(v);
					int numDependents = in.readInt();
					for (int j=0; j<numDependents; j++){
						v = in.readUTF();
						IResource dependent = root.findMember(v);
						if (dependent == null){
							//TODO get this message approved
							String msg = "IWAE0021E Internal error, the dependency index could not restored. Please perform a clean build. The error occurred while looking for {1}. ";
							throw new RuntimeException(NLS.bind(msg, v));
						}
						int numVal = in.readInt();
						for (int k=0; k<numVal; k++){
							String id = in.readUTF();
							add(id, dependent, dependsOn);
						}
					}					
				}				
			}
			catch (IOException e){
				error = true;
				ValidationPlugin.getPlugin().handleException(e);
			}
			finally {
				Misc.close(in);
			}
			
			if (error)f.delete();
		}
	}

	public synchronized void clear(IProject project) {
		init();
		List<Depends> list = _projectMap.get(project);
		if (list == null)return;
		
		_dirty = true;
		for (Depends d : list)d.delete();
	}

	public synchronized IResource[] get(String id, IResource dependsOn) {
		init();
		Map<IResource, Depends> map = _dependsMap.get(dependsOn.getFullPath());
		if (map == null)return EmptyResources;
		
		List<IResource> list = new LinkedList<IResource>();
		for (Map.Entry<IResource, Depends> me : map.entrySet()){
			if (me.getValue().hasValidator(id))list.add(me.getKey());
		}
		if (list.size() == 0)return EmptyResources;
		IResource[] resources = new IResource[list.size()];
		list.toArray(resources);
		return resources;
	}

	
	public synchronized List<DependentResource> get(IResource dependsOn) {
		init();
		List<DependentResource> list = new LinkedList<DependentResource>();
		Map<IResource, Depends> map = _dependsMap.get(dependsOn.getFullPath());
		ValManager vm = ValManager.getDefault();
		if (map != null){
			for (Map.Entry<IResource, Depends> me : map.entrySet()){
				for (String id : me.getValue().getValidatorsEnabled()){
					IResource res = me.getKey();
					Validator v = vm.getValidator(id, res.getProject());
					if (v != null)list.add(new DependentResource(res, v));
				}
			}
		}
		return list;
	}


	public synchronized void set(String id, IResource dependent, IResource[] dependsOn) {
		for (IResource d : dependsOn)add(id, dependent, d);
	}
		
	public boolean isDependedOn(IResource resource) {
		init();
		return _dependsMap.containsKey(resource.getFullPath());
	}	

	public void doneSaving(ISaveContext context) {	
	}
	
	public void prepareToSave(ISaveContext context) throws CoreException {	
	}
	
	public void rollback(ISaveContext context) {
	}
	
	/**
	 * Persist the dependency index.
	 * <p>
	 * The format of the index is:
	 * <pre>
	 * Version number
	 * Number of depends on entries
	 *   depends on file name
	 *   number of dependent entries
	 *     dependent file name
	 *     number of validators
	 *       validator id
	 * </pre>
	 */
	public void saving(ISaveContext context) throws CoreException {
		if (!_dirty)return;
		_dirty = false;
		
		DataOutputStream out = null;
		try {
			File f = getIndexLocation();
			out = new DataOutputStream(new FileOutputStream(f));
			out.writeInt(CurrentVersion);
			out.writeInt(_dependsMap.size());
			for (Map.Entry<IPath, Map<IResource, Depends>> me : _dependsMap.entrySet()){
				IPath key = me.getKey();
				out.writeUTF(key.toString());
				Map<IResource, Depends> map = me.getValue();
				out.writeInt(map.size());
				for (Map.Entry<IResource, Depends> me2: map.entrySet()){
					int vc = me2.getValue().validatorCount();
					if (vc == 0)continue;

					IResource key2 = me2.getKey();
					out.writeUTF(key2.getFullPath().toString());
					Map<String, Boolean> map3 = me2.getValue().getValidators();
					out.writeInt(vc);
					for (Map.Entry<String, Boolean> me3 : map3.entrySet()){
						if (me3.getValue())out.writeUTF(me3.getKey());
					}
				}
			}
		}
		catch (IOException e){
			ValidationPlugin.getPlugin().handleException(e);
		}
		finally {
			Misc.close(out);
		}
	}

	private File getIndexLocation() {
		IPath path = ValidationPlugin.getPlugin().getStateLocation().append("dep.index"); //$NON-NLS-1$
		return path.toFile();
	}

private static class Depends {
	
	/** The key is the validator dependencyId.*/
	private Map<String, Boolean> 	_validators;
	
	private Depends(){
		_validators = new HashMap<String, Boolean>(5);
	}

	private Map<String, Boolean> getValidators() {
		return _validators;
	}
	
	/**
	 * Answer the validator id's that are still enabled.
	 * @return
	 */
	private List<String> getValidatorsEnabled() {
		List<String> list = new LinkedList<String>();
		for (Map.Entry<String, Boolean> me : _validators.entrySet()){
			if (me.getValue())list.add(me.getKey());
		}
		return list;
	}

	private void delete() {
		_validators.clear();
	}

	private void add(String id) {
		_validators.put(id, Boolean.TRUE);
		
	}

	private boolean hasValidator(String id) {
		Boolean v = _validators.get(id);
		if (v == null)return false;
		return v.booleanValue();
	}
	
	/** 
	 * Answer the number of active dependencies.
	 * @return
	 */
	private int validatorCount(){
		int count = 0;
		for (Boolean b : _validators.values()){
			if (b)count++;
		}
		return count;
	}
	
}


}
