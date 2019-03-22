/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * The subset of the IEclipsePreferences interface that the framework needs to
 * be able to persist preferences.
 * @author karasiuk
 *
 */
public abstract class PreferencesWrapper {
	
	private static final WrapperManger _manager = new WrapperManger();
	
	/**
	 * Answer the preferences for the project. If project is null answer the global preferences.
	 * @param project
	 * @param persistent if null the default preferences are returned, if True the persisted 
	 * preferences are return and if False the transient preferences are returned.
	 * @return
	 */
	public static PreferencesWrapper getPreferences(IProject project, Boolean persistent){
		return _manager.get(project, persistent);
	}
		
	/**
	 * These are the names of the node entries.
	 * @return
	 * @throws BackingStoreException
	 */
	public abstract String[] childrenNames() throws BackingStoreException;
	
	public void flush() throws BackingStoreException {
	}
	
	public abstract boolean getBoolean(String key, boolean def);
	
	public abstract String get(String key, String def);
	
	public abstract int getInt(String key, int def);
	
	public abstract long getLong(String key, long def);

	public abstract String[] keys() throws BackingStoreException;

	public boolean isPersistent(){
		return false;
	}

	public boolean isTransient(){
		return false;
	}
	
	public abstract void put(String key, String value);
	
	public abstract void putBoolean(String key, boolean value);
	
	public abstract void putLong(String key, long value);
	
	public abstract void putInt(String key, int value);
	
	/**
	 * Unlike the more sophisticated org.osgi.service.prefs.Preferences support, 
	 * this is currently limited to simple node names.
	 */
	public abstract PreferencesWrapper node(String nodeName);
	
	public abstract boolean nodeExists();
	
	public abstract boolean nodeExists(String pathName)  throws BackingStoreException;
	
	public abstract void removeNode()  throws BackingStoreException;


public final static class PreferencesWrapperPersistent extends PreferencesWrapper {
	
	private final Preferences _preferences;
	
	public PreferencesWrapperPersistent(Preferences preferences){
		_preferences = preferences;
	}
	
	@Override
	public String[] childrenNames() throws BackingStoreException {
		return _preferences.childrenNames();
	}
	
	public void flush() throws BackingStoreException {
		_preferences.flush();
	}
	
	@Override
	public String get(String key, String def) {
		return _preferences.get(key, def);
	}
	
	@Override
	public boolean getBoolean(String key, boolean def) {
		return _preferences.getBoolean(key, def);
	}
	
	@Override
	public int getInt(String key, int def) {
		return _preferences.getInt(key, def);
	}
	
	@Override
	public long getLong(String key, long def) {
		return _preferences.getLong(key, def);
	}
	
	@Override
	public String[] keys() throws BackingStoreException {
		return _preferences.keys();
	}
	
	@Override
	public boolean isPersistent() {
		return true;
	}
	
	@Override
	public void put(String key, String value) {
		_preferences.put(key, value);
	}
	
	@Override
	public PreferencesWrapper node(String path) {
		Preferences prefs = _preferences.node(path);
		return new PreferencesWrapperPersistent(prefs);
	}
	
	@Override
	public boolean nodeExists() {
		try {
			return nodeExists(""); //$NON-NLS-1$
		}
		catch (BackingStoreException e){
		
		}
		return false;
	}
	
	@Override
	public boolean nodeExists(String pathName) throws BackingStoreException  {
		return _preferences.nodeExists(pathName);
	}
	
	public void putBoolean(String key, boolean value) {
		_preferences.putBoolean(key, value);	
	}
	
	public void putLong(String key, long value){
		_preferences.putLong(key, value);
	}
	
	@Override
	public void putInt(String key, int value) {
		_preferences.putInt(key, value);
	}
	
	@Override
	public void removeNode() throws BackingStoreException {
		_preferences.removeNode();
	}
}

public final static class PreferencesWrapperTransient extends PreferencesWrapper {
	
	private final PreferencesWrapperTransient _parent;
	private final Map<String, String> _children = Collections.synchronizedMap(new HashMap<String, String>(10));
	private final Map<String, PreferencesWrapperTransient> _nodes = Collections.synchronizedMap(new HashMap<String, PreferencesWrapperTransient>(10));
	
	public PreferencesWrapperTransient(PreferencesWrapperTransient parent){
		_parent = parent;
	}
	
	public PreferencesWrapperTransient(PreferencesWrapper pw, PreferencesWrapperTransient parent) {
		_parent = parent;
		try {
			for (String key : pw.keys()){
				put(key, pw.get(key, null));
			}
			
			
			for (String nodeName : pw.childrenNames()){
				PreferencesWrapper p = pw.node(nodeName);
				PreferencesWrapperTransient pwt = new PreferencesWrapperTransient(p, this);
				_nodes.put(nodeName, pwt);
			}
		}
		catch (BackingStoreException e){
			
		}
	}

	@Override
	public String[] childrenNames() throws BackingStoreException {
		Set<String> keys = _nodes.keySet();
		String names[] = new String[keys.size()];
		keys.toArray(names);
		return names;
	}
	
	@Override
	public String get(String key, String def) {
		String value = _children.get(key);
		if (value != null)return value;
		return def;
	}
	
	@Override
	public boolean getBoolean(String key, boolean def) {
		String value = _children.get(key);
		if (value == null)return def;
		value = value.toLowerCase();
		if ("true".equals(value))return true; //$NON-NLS-1$
		if ("false".equals(value))return false; //$NON-NLS-1$
		return def;
	}
	
	@Override
	public int getInt(String key, int def) {
		String value = _children.get(key);
		if (value == null)return def;
		try {
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e){
		}
		return def;
	}
	
	@Override
	public long getLong(String key, long def) {
		String value = _children.get(key);
		if (value == null)return def;
		try {
			return Long.parseLong(value);
		}
		catch (NumberFormatException e){
		}
		return def;
	}
	
	@Override
	public boolean isTransient() {
		return true;
	}
	
	@Override
	public synchronized String[] keys() throws BackingStoreException {
		String[] keys = new String[_children.size()];
		_children.keySet().toArray(keys);
		return keys;
	}
	
	@Override
	public synchronized PreferencesWrapper node(String name) {
		PreferencesWrapperTransient pw  = _nodes.get(name);
		if (pw != null)return pw;
		pw = new PreferencesWrapperTransient(this);
		_nodes.put(name, pw);
		return pw;
	}
	
	@Override
	public boolean nodeExists() {
		return true;
	}
	
	@Override
	public boolean nodeExists(String key) throws BackingStoreException {
		PreferencesWrapperTransient pw = _nodes.get(key);
		if (pw != null)return true;
		return false;
	}
	
	@Override
	public void put(String key, String value) {
		_children.put(key, value);
	}
	
	@Override
	public void putBoolean(String key, boolean bool) {
		String value = bool ? "true" : "false";  //$NON-NLS-1$//$NON-NLS-2$
		_children.put(key, value);
	}
	
	@Override
	public void putInt(String key, int value) {
		_children.put(key, String.valueOf(value));
	}
	
	@Override
	public void putLong(String key, long value) {
		_children.put(key, String.valueOf(value));
	}
	
	@Override
	public void removeNode() throws BackingStoreException {
		if (_parent == null)return;
		_parent.removeNode(this);
	}
	
	private synchronized void removeNode(PreferencesWrapperTransient node){
		String key = null;
		for (Map.Entry<String, PreferencesWrapperTransient> me : _nodes.entrySet()){
			if (me.getValue().equals(node)){
				key = me.getKey();
				break;
			}
		}
		if (key != null)_nodes.remove(key);
	}
}

private final static class WrapperManger implements IProjectChangeListener {
	
	private final Map<IProject, PreferencesWrapper> _map = new HashMap<IProject, PreferencesWrapper>(20); 
	private final AtomicReference<PreferencesWrapper> _global = new AtomicReference<PreferencesWrapper>();
	
	private WrapperManger(){
		EventManager.getManager().addProjectChangeListener(this);
	}
	
	/**
	 * Currently this object never goes away, but if that was ever to change then we would need to dispose it.
	 */
	@Override
	protected void finalize() throws Throwable {
		dispose();
	}
	
	public void dispose(){
		EventManager.getManager().removeProjectChangeListener(this);
	}

	public PreferencesWrapper get(IProject project, Boolean persistent) {
		if (project == null)return globalPreferences(persistent);
		PreferencesWrapper pw = null;
		synchronized(_map){
			pw = _map.get(project);
		}
		
		if (pw != null && (persistent == null || persistent == pw.isPersistent()))return pw;
		
		if (pw == null)pw = new PreferencesWrapperPersistent(ValidationPlugin.getPreferences(project));
		if (persistent != null && persistent && pw.isTransient())pw = new PreferencesWrapperPersistent(ValidationPlugin.getPreferences(project));
		if (persistent != null && !persistent && pw.isPersistent())pw = new PreferencesWrapperTransient(pw, null);
		
		synchronized(_map){
			_map.put(project, pw);
		}
		
		return pw;
	}

	/**
		 * Answer the appropriate global preferences.
		 * 
		 * @param persistent
		 *            If null then answer the current saved global preferences,
		 *            creating a new persistent one if there is none. If True,
		 *            then ensure that the preferences are persistent. If False,
		 *            ensure that the preferences are transient.
		 * @return
		 */
	private PreferencesWrapper globalPreferences(Boolean persistent) {
		PreferencesWrapper pw = _global.get();
		
		while(pw == null){
			PreferencesWrapper newPW = createGlobal(persistent);
			if (_global.compareAndSet(null, newPW))pw = newPW;
			else pw = _global.get();
		}
		
		while (persistent != null && !persistent && !pw.isTransient()){
			PreferencesWrapper newPW = new PreferencesWrapperTransient(pw, null);
			if (_global.compareAndSet(pw, newPW))pw = newPW;
			else pw = _global.get();
		}
		
		while (persistent != null && persistent && !pw.isPersistent()){
			PreferencesWrapper newPW = new PreferencesWrapperPersistent(ValidationFramework.getDefault().getPreferenceStore());
			if (_global.compareAndSet(pw, newPW))pw = newPW;
			else pw = _global.get();			
		}
		return pw;
	}
	
	private PreferencesWrapper createGlobal(Boolean persistent){
		PreferencesWrapper pw = new PreferencesWrapperPersistent(ValidationFramework.getDefault().getPreferenceStore());
		if (persistent == null || persistent)return pw;
		return new PreferencesWrapperTransient(pw, null);
	}

	public void projectChanged(IProject project, int type) {
		int interested = IProjectChangeListener.ProjectClosed | IProjectChangeListener.ProjectDeleted;
		if ((type & interested) != 0){
			synchronized (_map) {
				_map.remove(project);
			}
		}
		
	}
	
}


}
