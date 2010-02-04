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
package org.eclipse.wst.common.componentcore.internal.flat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.flat.VirtualComponentFlattenUtility.ShouldIncludeUtilityCallback;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class FlatVirtualComponent implements IFlatVirtualComponent, ShouldIncludeUtilityCallback {
	
	public static class FlatComponentTaskModel extends HashMap<Object, Object> {
			private static final long serialVersionUID = 1L;
	}
	
	/**
	 * The datamodel, which may contain preferences, settings, or other data
	 * used by the various participants to determine how to properly 
	 * traverse this component. 
	 */
	private FlatComponentTaskModel dataModel;
	
	/**
	 * The root component being flattened. 
	 */
	private IVirtualComponent component;
	
	/**
	 * The list of participants to engage in the flattening process. 
	 */
	private IFlattenParticipant[] participants;

	/**
	 * The list of member resources for this component
	 */
	private List<IFlatResource> members = null;
	
	/**
	 * The list of child modules for this component
	 */
	private List<IChildModuleReference> children = null;
	
	
	public FlatVirtualComponent(IVirtualComponent component) {
		this(component, new FlatComponentTaskModel());
	}
	
	public FlatVirtualComponent(IVirtualComponent component, FlatComponentTaskModel dataModel) {
		this.component = component;
		this.dataModel = dataModel;
		participants = setParticipants();
		dataModel.put(EXPORT_MODEL, this);
	}
	
	/**
	 * Set the list of participants for this virtual component. 
	 * This is pulled from the datamodel. 
	 */
	protected IFlattenParticipant[] setParticipants() {
		Object o = dataModel.get(PARTICIPANT_LIST);
		if( o != null ) {
			if( o instanceof IFlattenParticipant )
				return new IFlattenParticipant[] { (IFlattenParticipant)o};
			if( o instanceof IFlattenParticipant[])
				return (IFlattenParticipant[])o;
			if( o instanceof List ) {
				List<IFlattenParticipant> l = (List<IFlattenParticipant>)o;
				return (IFlattenParticipant[]) l
						.toArray(new IFlattenParticipant[l.size()]);
			}
		}
		return new IFlattenParticipant[]{};
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.common.componentcore.internal.flat.IFlatVirtualComponent#fetchResources()
	 */
	public IFlatResource[] fetchResources() throws CoreException {
		if( members == null)
			cacheResources();
		return (FlatResource[]) members.toArray(new FlatResource[members.size()]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.common.componentcore.internal.flat.IFlatVirtualComponent#getChildModules()
	 */
	public IChildModuleReference[] getChildModules() throws CoreException {
		if( members == null )
			cacheResources();
		return (ChildModuleReference[]) children.toArray(new ChildModuleReference[children.size()]);
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
		members = new ArrayList<IFlatResource>();
		children = new ArrayList<IChildModuleReference>();
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

	protected void optimize(List<IFlatResource> resources) {
		for( int i = 0; i < participants.length; i++ ) {
			if( participants[i].canOptimize(component, dataModel)) {
				participants[i].optimize(component, dataModel, resources);
				return;
			}
		}
	}
	
	protected void runFinalizations(List<IFlatResource> resources) {
		for( int i = 0; i < participants.length; i++ ) {
			participants[i].finalize(component, dataModel, resources);
		}
	}
	
	protected void treeWalk() throws CoreException {
		if (component != null) {
			VirtualComponentFlattenUtility util = new VirtualComponentFlattenUtility(members, this);
			IVirtualFolder vFolder = component.getRootFolder();
			
			// actually walk the tree
			util.addMembers(component, vFolder, Path.EMPTY);

			//addRelevantOutputFolders(); // to be done in a participant later

			addConsumedReferences(util, component, new Path(""));
			addUsedReferences(util, component, new Path(""));
		}
	}
	
	/**
	 * Consumed references are, by definition, consumed, and should not
	 * be eligible to be exposed as child modules. They are consumed 
	 * directly into the module tree. 
	 * 
	 * The reference in question may have references of its own, both
	 * used and consumed. References of the child will be treated
	 * as references of the parent, whether consumed or used.  
	 * 
	 * A key difference in the handling of non-child USED references 
	 * as compared to CONSUMES is that CONSUMED references have their
	 * archiveName *ignored*, and its child members are directly consumed. 
	 * In contrast, a USED non-child keeps its archiveName as the folder name. 
	 *  
	 * @param vc
	 */
	protected void addConsumedReferences(VirtualComponentFlattenUtility util, IVirtualComponent vc, IPath root) throws CoreException {
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
					addUsedReferences(util, consumedComponent, root.append(reference.getRuntimePath().makeRelative()));
				}
    		}
    	}
	}
	
	/**
	 * This checks to see if any exportable file is actually a child module.
	 * Children modules will be exposed via the getChildModules() method. 
	 */
	public boolean shouldAddComponentFile(IVirtualComponent current, IFlatFile file) {
		for( int i = 0; i < participants.length; i++ ) {
			if( participants[i].isChildModule(component, dataModel, file)) {
				ChildModuleReference child = new ChildModuleReference(current.getProject(), file);
				children.add(child); 
				return false;
			} else if( !participants[i].shouldAddExportableFile(component, current, dataModel, file))
				return false;
		}
		return true;
	}

	protected void addUsedReferences(VirtualComponentFlattenUtility util, IVirtualComponent vc, IPath root) throws CoreException {
		IVirtualReference[] allReferences = vc.getReferences();
    	for (int i = 0; i < allReferences.length; i++) {
    		IVirtualReference reference = allReferences[i];
			IVirtualComponent virtualComp = reference.getReferencedComponent();
			if (reference.getDependencyType() == DependencyType.USES ) {
				if( shouldIgnoreReference(reference))
					continue;
				
				if( !isChildModule(reference)) {
					addNonChildUsedReference(util, vc, reference, root.append(reference.getRuntimePath()));
				} else {
					ChildModuleReference cm = new ChildModuleReference(reference, root);
					for( IChildModuleReference tmp : children ) {
						if(tmp.getRelativeURI().equals(cm.getRelativeURI()))
							children.remove(cm);
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

	protected void addNonChildUsedReference(VirtualComponentFlattenUtility util, IVirtualComponent parent, 
			IVirtualReference reference, IPath runtimePath) throws CoreException {
		if( reference.getReferencedComponent().isBinary()) {
			handleNonChildUsedBinaryReference(util, parent, reference, runtimePath);
		} else /* !virtualComp.isBinary() */ {
			/*
			 * used references to non-binary components that are NOT child modules.
			 * These should be 'consumed' but maintain their name
			 * As of now I don't believe there are any such instances of this.
			 * I also believe in most cases, this probably is a child module that 
			 * the parent just doesn't know about.
			 * 
			 * Example: Ear Project consumes ESB project, Ear project does not 
			 * recognize ESB project as a child However, if the server adapter 
			 * can use nested exploded deployments (folders instead of zips),
			 * then this will still work. 
			 * 
			 * TODO Investigate / Discuss
			 */
			util.addMembers(reference.getReferencedComponent(), reference.getReferencedComponent().getRootFolder(), 
					runtimePath.append(reference.getArchiveName()));
		}
	}
	
	protected void handleNonChildUsedBinaryReference(VirtualComponentFlattenUtility util, IVirtualComponent parent, 
			IVirtualReference reference, IPath runtimePath) throws CoreException {
		// Binary used references must be added as a single file unless they're child modules
		final String archiveName = reference.getArchiveName();
		final IVirtualComponent virtualComp = reference.getReferencedComponent();
		FlatFile mf = null;
		IFile ifile = (IFile)virtualComp.getAdapter(IFile.class);
		if( ifile != null ) {
			String name = null != archiveName ? archiveName : ifile.getName();
			mf = new FlatFile(ifile, name, runtimePath.makeRelative());
		} else {
			File extFile = (File)virtualComp.getAdapter(File.class);
			if( extFile != null ) {
				String name = null != archiveName ? archiveName : extFile.getName();
				mf = new FlatFile(extFile, name, runtimePath.makeRelative());
			}
		}
		
		if( mf != null ) {
			IFlatResource moduleParent = VirtualComponentFlattenUtility.getExistingModuleResource(members, mf.getModuleRelativePath());
			if (moduleParent != null && moduleParent instanceof FlatFolder) {
				VirtualComponentFlattenUtility.addMembersToModuleFolder((FlatFolder)moduleParent, new FlatResource[]{mf});
			} else {
				if( shouldAddComponentFile(virtualComp, mf)) {
					if (mf.getModuleRelativePath().isEmpty()) {
						for( IFlatResource tmp : members) 
							if( tmp.getName().equals(mf.getName()))
								return;
						members.add(mf);
					} else {
						if (moduleParent == null) {
							moduleParent = VirtualComponentFlattenUtility.ensureParentExists(members, mf.getModuleRelativePath(), (IContainer)parent.getRootFolder().getUnderlyingResource());
						}
						VirtualComponentFlattenUtility.addMembersToModuleFolder((FlatFolder)moduleParent, new FlatResource[] {mf});
					}
				} else {
					// Automatically added to children if it needed to be
				}
			}
		}
	}
}
