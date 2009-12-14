/*******************************************************************************
 * Copyright (c) 2009 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.export;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.export.ExportModelUtil.ShouldIncludeUtilityCallback;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class ExportModel implements ShouldIncludeUtilityCallback {
	
	public static class ExportTaskModel extends HashMap<Object, Object> {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * An options key listing the export participants.
	 * The value must be a List<IExportUtilParticipant>, 
	 *  or simply an IExportUtilParticipant
	 */
	public static String PARTICIPANT_LIST = "org.eclipse.wst.common.componentcore.export.participantList";
	
	/**
	 * The ExportModel (this) being used
	 */
	public static String EXPORT_MODEL = "org.eclipse.wst.common.componentcore.export.exportModel";
	
	private ExportTaskModel dataModel;
	private IVirtualComponent component;
	private IExportParticipant[] participants;

	public ExportModel(IVirtualComponent component) {
		this(component, new ExportTaskModel());
	}
	
	public ExportModel(IVirtualComponent component, ExportTaskModel dataModel) {
		this.component = component;
		this.dataModel = dataModel;
		participants = setParticipants();
		dataModel.put(EXPORT_MODEL, this);
	}
	
	/*
	 * Kinda ugly but functional and allows the option
	 * to be set as one or a list for convenience
	 */
	protected IExportParticipant[] setParticipants() {
		Object o = dataModel.get(PARTICIPANT_LIST);
		if( o != null ) {
			if( o instanceof IExportParticipant )
				return new IExportParticipant[] { (IExportParticipant)o};
			if( o instanceof IExportParticipant[])
				return (IExportParticipant[])o;
			if( o instanceof List ) {
				List<IExportParticipant> l = (List<IExportParticipant>)o;
				return (IExportParticipant[]) l
						.toArray(new IExportParticipant[l.size()]);
			}
		}
		return new IExportParticipant[]{};
	}
	
	private List<IExportableResource> members = null;
	private List<IChildModule> children = null;
	public ExportableResource[] fetchResources() throws CoreException {
		if( members == null)
			cacheResources();
		return (ExportableResource[]) members.toArray(new ExportableResource[members.size()]);
	}
	
	public ChildModule[] getChildModules() throws CoreException {
		if( members == null )
			cacheResources();
		return (ChildModule[]) children.toArray(new ChildModule[children.size()]);
	}
	
	
	protected void cacheResources() throws CoreException {
		runInitializations();
		if( canOptimize()) {
			optimize(members);
		} else {
			treeWalk();
			runFinalizations(members);
		}
	}
	
	protected void runInitializations() {
		members = new ArrayList<IExportableResource>();
		children = new ArrayList<IChildModule>();
		for( int i = 0; i < participants.length; i++ ) {
			participants[i].initialize(component, dataModel, members);
		}
	}
	
	protected boolean canOptimize() {
		for( int i = 0; i < participants.length; i++ ) {
			if( participants[i].canOptimize(component, dataModel))
				return true;
		}
		return false;
	}

	protected void optimize(List<IExportableResource> resources) {
		for( int i = 0; i < participants.length; i++ ) {
			if( participants[i].canOptimize(component, dataModel)) {
				participants[i].optimize(component, dataModel, resources);
				return;
			}
		}
	}
	
	protected void runFinalizations(List<IExportableResource> resources) {
		for( int i = 0; i < participants.length; i++ ) {
			participants[i].finalize(component, dataModel, resources);
		}
	}
	
	protected void treeWalk() throws CoreException {
		if (component != null) {
			ExportModelUtil util = new ExportModelUtil(members, this);
			IVirtualFolder vFolder = component.getRootFolder();
			
			// actually walk the tree
			util.addMembers(component, vFolder, Path.EMPTY);

			//addRelevantOutputFolders(); // to be done in a participant later

			addConsumedReferences(util, component, new Path(""));
			addUsedReferences(component, new Path(""));
		}
	}
	
	/**
	 * Consumed references are, by definition, consumed, and should not
	 * be eligible to be exposed as child modules. They are consumed 
	 * directly into the module tree
	 * 
	 * @param vc
	 */
	protected void addConsumedReferences(ExportModelUtil util, IVirtualComponent vc, IPath root) throws CoreException {
		List consumableMembers = new ArrayList();
		IVirtualReference[] refComponents = vc.getReferences();
    	for (int i = 0; i < refComponents.length; i++) {
    		IVirtualReference reference = refComponents[i];
    		if (reference != null && reference.getDependencyType()==IVirtualReference.DEPENDENCY_TYPE_CONSUMES) {
    			IVirtualComponent consumedComponent = reference.getReferencedComponent();
				if (consumedComponent.getRootFolder()!=null) {
					IVirtualFolder vFolder = consumedComponent.getRootFolder();
					util.addMembers(consumedComponent, vFolder, root.append(reference.getRuntimePath().makeRelative()));
					addConsumedReferences(util, consumedComponent, root.append(reference.getRuntimePath().makeRelative()));
					addUsedReferences(consumedComponent, root.append(reference.getRuntimePath().makeRelative()));
				}
    		}
    	}
	}
	
	/**
	 * This checks to see if any exportable file is actually a child module,
	 * which should be exposed differently
	 */
	public boolean shouldAddComponentFile(IVirtualComponent current, IExportableFile file) {
		for( int i = 0; i < participants.length; i++ ) {
			if( participants[i].isChildModule(component, dataModel, file)) {
				ChildModule child = new ChildModule(file);
				children.add(child); 
				return false;
			} else if( !participants[i].shouldAddExportableFile(component, current, dataModel, file))
				return false;
		}
		return true;
	}

	protected void addUsedReferences(IVirtualComponent vc, IPath root) {
		IVirtualReference[] allReferences = vc.getReferences();
    	for (int i = 0; i < allReferences.length; i++) {
    		IVirtualReference reference = allReferences[i];
			IVirtualComponent virtualComp = reference.getReferencedComponent();
			if (reference.getDependencyType() == DependencyType.USES ) {
				if( shouldIgnoreReference(reference))
					continue;
				
				if( !isChildModule(reference)) {
					addUsedReference(vc, reference, root.append(reference.getRuntimePath()));
				} else {
					ChildModule cm = new ChildModule(reference, root);
					for( IChildModule tmp : children ) {
						if( tmp.getRelativeURI().equals(cm.getRelativeURI()))
							return;
					}
					children.add(cm);
				}
			}
    	}
	}
	
	/**
	 * Should we expose this used reference as a member file?
	 * 
	 * @param currentComponent the current component we're traversing
	 * @return true if it's a member file, false if it's a child module
	 */
	protected boolean isChildModule(IVirtualReference referencedComponent) {
		for( int i = 0; i < participants.length; i++ ) {
			if( participants[i].isChildModule(component, referencedComponent, dataModel))
				return true;
		}
		return false;
	}

	protected boolean shouldIgnoreReference(IVirtualReference referencedComponent) {
		for( int i = 0; i < participants.length; i++ ) {
			if( participants[i].shouldIgnoreReference(component, referencedComponent, dataModel))
				return true;
		}
		return false;
	}

	protected void addUsedReference(IVirtualComponent parent, IVirtualReference reference, IPath runtimePath) {
		ExportableFile mf = null;
		final String archiveName = reference.getArchiveName();
		final IVirtualComponent virtualComp = reference.getReferencedComponent();
		
		// Binary used references must be added as a single file unless they're child modules
		if( virtualComp.isBinary()) {
			IFile ifile = (IFile)virtualComp.getAdapter(IFile.class);
			if( ifile != null ) {
				String name = null != archiveName ? archiveName : ifile.getName();
				mf = new ExportableFile(ifile, name, runtimePath.makeRelative());
			} else {
				File extFile = (File)virtualComp.getAdapter(File.class);
				if( extFile != null ) {
					String name = null != archiveName ? archiveName : extFile.getName();
					mf = new ExportableFile(extFile, name, runtimePath.makeRelative());
				}
			}
			
			if( mf != null ) {
				IExportableResource moduleParent = ExportModelUtil.getExistingModuleResource(members, mf.getModuleRelativePath());
				if (moduleParent != null && moduleParent instanceof ExportableFolder) {
					ExportModelUtil.addMembersToModuleFolder((ExportableFolder)moduleParent, new ExportableResource[]{mf});
				} else {
					if( shouldAddComponentFile(virtualComp, mf)) {
						if (mf.getModuleRelativePath().isEmpty()) {
							for( IExportableResource tmp : members) 
								if( tmp.getName().equals(mf.getName()))
									return;
							members.add(mf);
						} else {
							if (moduleParent == null) {
								moduleParent = ExportModelUtil.ensureParentExists(members, mf.getModuleRelativePath(), (IContainer)parent.getRootFolder().getUnderlyingResource());
							}
							ExportModelUtil.addMembersToModuleFolder((ExportableFolder)moduleParent, new ExportableResource[] {mf});
						}
					} else {
						// Automatically added to children if it needed to be
					}
				}
			}
		} else /* !virtualComp.isBinary() */ {
			/*
			 * used references to non-binary components that are NOT child modules.
			 * These should be 'consumed' but maintain their name
			 * As of now I don't believe there are any such instances of this and this can be delayed
			 * I also believe in most cases, this probably is a child module that the parent just doesn't know about.
			 * Example: Ear Project consumes ESB project, Ear project does not recognize ESB project
			 * 
			 * TODO Investigate / Discuss
			 */
		}

	}
}
