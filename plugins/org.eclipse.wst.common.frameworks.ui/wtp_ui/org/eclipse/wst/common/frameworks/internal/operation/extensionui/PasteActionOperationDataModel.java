/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Aug 26, 2004
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.wst.common.frameworks.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel;

//TODO This class has code commented out to get compile errors cleanup for Eclipse 3.1 as the internal classes ReorgResult and PasteActions have been removed in 3.1 - VKB

/**
 * @author mdelder
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 * 
 */
public class PasteActionOperationDataModel extends IActionWTPOperationDataModel {

	/**
	 * Result Property. A java.util.Map of old-types-to-new-types
	 */
	public static final String REFACTORED_RESOURCES = "PasteActionOperationDataModel.REFACTORED_RESOURCES"; //$NON-NLS-1$
	public static final String DESTINATION = "PasteActionOperationDataModel.DESTINATION"; //$NON-NLS-1$
	private Map prepasteSnapshot = new HashMap();
	//TODO Needs Clean up of compile errors for Eclipse 3.1 - VKB
	//private ReorgResult cachedResult;

	/**
	 * @param action
	 * @param selection
	 * @param provider
	 * @return
	 */
	public static WTPOperationDataModel createDataModel(IActionDelegate action, IStructuredSelection selection, ISelectionProvider provider, Shell shell) {
		WTPOperationDataModel dataModel = new PasteActionOperationDataModel();
		dataModel.setProperty(IACTION, action);
		dataModel.setProperty(ISTRUCTURED_SELECTION, selection);
		dataModel.setProperty(ISELECTION_PROVIDER, provider);
		dataModel.setProperty(SHELL, shell);
		return dataModel;
	}

	/**
	 * @param action
	 * @param selection
	 * @param provider
	 * @return
	 */
	public static WTPOperationDataModel createDataModel(Action action, IStructuredSelection selection, ISelectionProvider provider, Shell shell) {
		WTPOperationDataModel dataModel = new PasteActionOperationDataModel();
		dataModel.setProperty(IACTION, action);
		dataModel.setProperty(ISTRUCTURED_SELECTION, selection);
		dataModel.setProperty(ISELECTION_PROVIDER, provider);
		dataModel.setProperty(SHELL, shell);
		return dataModel;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.extension.ui.IActionWTPOperationDataModel#initValidBaseProperties()
	 */
	protected void initValidBaseProperties() {
		super.initValidBaseProperties();
		addValidBaseProperty(REFACTORED_RESOURCES);
		addValidBaseProperty(DESTINATION);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperationDataModel#getDefaultOperation()
	 */
	public WTPOperation getDefaultOperation() {
		return new PasteActionOperation(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.extension.ui.IActionWTPOperationDataModel#isResultProperty(java.lang.String)
	 */
	protected boolean isResultProperty(String propertyName) {
		if (REFACTORED_RESOURCES.equals(propertyName) || DESTINATION.equals(propertyName))
			return true;
		return super.isResultProperty(propertyName);
	}

	
	/*
	 * (non-Javadoc)
	 * TODO Needs Clean up of compile errors for Eclipse 3.1 - VKB
	 * @see org.eclipse.wst.common.frameworks.internal.operation.extension.ui.IActionWTPOperationDataModel#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
//	public void propertyChange(PropertyChangeEvent event) {
//		if (PasteAction.PASTE_PRE_NOTIFICATION.equals(event.getProperty())) {
//
//			ReorgResult result = (ReorgResult) event.getNewValue();
//			updatePrepasteSnapshot(result);
//		} else if (PasteAction.PASTE_RESULT_NOTIFICATION.equals(event.getProperty())) {
//			//System.out.println("Caching result");
//			cachedResult = (ReorgResult) event.getNewValue();
//		} else if (IAction.RESULT.equals(event.getProperty()) && cachedResult != null) {
//			//System.out.println("Applying cached result");
//			fillMapAsNecessary(cachedResult);
//
//			Map refactorMap = cachedResult.getNameChanges();
//			setProperty(REFACTORED_RESOURCES, refactorMap);
//
//			Object destination = cachedResult.getDestination();
//			if (cachedResult.getResourcesToCopy().length == 1) {
//				if (cachedResult.getResourcesToCopy()[0].equals(destination))
//					destination = cachedResult.getResourcesToCopy()[0].getParent();
//			} else if (cachedResult.getJavaElementsToCopy().length == 1) {
//				if (cachedResult.getJavaElementsToCopy()[0].equals(destination))
//					destination = cachedResult.getJavaElementsToCopy()[0].getParent();
//			}
//			setProperty(DESTINATION, destination);
//			cachedResult = null;
//
//		}
//		super.propertyChange(event);
//	}
	
//	TODO Needs Clean up of compile errors for Eclipse 3.1 - VKB

//	protected void updatePrepasteSnapshot(ReorgResult result) {
//
//		resetPrepasteSnapshot();
//
//		IContainer container = getContainerFromResult(result);
//		if (container == null)
//			return;
//
//		IResource resourceExisting = null;
//
//		IResource[] resources = result.getResourcesToCopy();
//		if (resources != null) {
//			for (int i = 0; i < resources.length; i++) {
//				resourceExisting = getCorrespondingFileOrFolder(resources[i], new Path(resources[i].getName()), container);
//				if (resourceExisting.exists())
//					prepasteSnapshot.put(resourceExisting, new Long(resourceExisting.getModificationStamp()));
//			}
//		}
//
//		IJavaElement[] elements = result.getJavaElementsToCopy();
//		if (elements != null) {
//			for (int i = 0; i < elements.length; i++) {
//				try {
//					resourceExisting = getCorrespondingFileOrFolder(elements[i].getCorrespondingResource(), new Path(elements[i].getCorrespondingResource().getName()), container);
//					if (resourceExisting != null && resourceExisting.exists())
//						prepasteSnapshot.put(resourceExisting, new Long(resourceExisting.getModificationStamp()));
//				} catch (JavaModelException e) {
//				}
//			}
//		}
//	}

	/**
	 *  
	 */
	private void resetPrepasteSnapshot() {
		if (prepasteSnapshot == null)
			prepasteSnapshot = new HashMap();
		else
			prepasteSnapshot.clear();
	}
	
//	TODO Needs Clean up of compile errors for Eclipse 3.1 - VKB

//	protected void fillMapAsNecessary(ReorgResult result) {
//
//		IContainer container = getContainerFromResult(result);
//
//		if (container == null)
//			return;
//
//		/*
//		 * If you Ctrl+C -> Ctrl+V a Project, you get no pre-notification and the prepasteSnapshot
//		 * is null
//		 */
//		if (prepasteSnapshot == null)
//			prepasteSnapshot = new HashMap();
//
//		Map nameChanges = result.getNameChanges();
//
//		IResource resourceExisting = null;
//
//		IResource[] resources = result.getResourcesToCopy();
//		if (resources != null) {
//			for (int i = 0; i < resources.length; i++) {
//				resourceExisting = getCorrespondingFileOrFolder(resources[i], new Path(resources[i].getName()), container);
//
//				if (prepasteSnapshot.containsKey(resourceExisting)) {
//					Long timestamp = (Long) prepasteSnapshot.get(resourceExisting);
//					if (timestamp.longValue() != resourceExisting.getModificationStamp())
//						nameChanges.put(resources[i], resourceExisting);
//				} else
//					nameChanges.put(resources[i], resourceExisting);
//			}
//		}
//
//		IJavaElement[] elements = result.getJavaElementsToCopy();
//		if (elements != null) {
//			for (int i = 0; i < elements.length; i++) {
//				try {
//					resourceExisting = getCorrespondingFileOrFolder(elements[i].getCorrespondingResource(), new Path(elements[i].getCorrespondingResource().getName()), container);
//
//					if (prepasteSnapshot.containsKey(resourceExisting)) {
//						Long timestamp = (Long) prepasteSnapshot.get(resourceExisting);
//						if (timestamp.longValue() != resourceExisting.getModificationStamp())
//							nameChanges.put(elements[i].getCorrespondingResource(), resourceExisting);
//					} else
//						nameChanges.put(elements[i].getCorrespondingResource(), resourceExisting);
//				} catch (JavaModelException e) {
//				}
//			}
//		}
//		resetPrepasteSnapshot();
//	}
//
//	private IContainer getContainerFromResult(ReorgResult result) {
//
//		Object destination = result.getDestination();
//
//		IContainer container = null;
//		if (result.getDestination() instanceof IContainer) {
//			container = (IContainer) destination;
//		} else if (destination instanceof IJavaElement) {
//			IJavaElement containerElement = (IJavaElement) destination;
//			IPackageFragment fragment = null;
//			IPackageFragmentRoot fragmentRoot = null;
//			switch (containerElement.getElementType()) {
//				case IJavaElement.PACKAGE_FRAGMENT :
//					fragment = (IPackageFragment) containerElement;
//					break;
//				case IJavaElement.PACKAGE_FRAGMENT_ROOT :
//					fragmentRoot = (IPackageFragmentRoot) containerElement;
//					break;
//
//			}
//			try {
//				if (fragmentRoot == null ^ fragment == null) {
//					container = (fragmentRoot != null) ? (IContainer) fragmentRoot.getCorrespondingResource() : (IContainer) fragment.getCorrespondingResource();
//				}
//			} catch (JavaModelException e1) {
//			}
//		}
//		return container;
//	}

	private IResource getCorrespondingFileOrFolder(IResource existing, IPath expectedPath, IContainer container) {
		IResource resourceExisting = null;
		switch (existing.getType()) {
			case IResource.FOLDER :
				resourceExisting = container.getFolder(expectedPath);
				break;
			case IResource.FILE :
				resourceExisting = container.getFile(expectedPath);
				break;
		}
		return resourceExisting;
	}


}