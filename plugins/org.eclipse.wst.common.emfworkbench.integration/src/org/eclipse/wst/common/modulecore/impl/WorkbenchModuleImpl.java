/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchModuleImpl.java,v 1.15 2005/02/09 02:48:39 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.common.modulecore.DependentModule;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ModuleType;
import org.eclipse.wst.common.modulecore.ModuleURIUtil;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;
import org.eclipse.wst.common.modulecore.util.ModuleCore;
import org.eclipse.wst.common.modulecore.util.ResourceTreeRoot;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Workbench Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getDeployedName <em>Deployed Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getModuleType <em>Module Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getModules <em>Modules</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkbenchModuleImpl extends EObjectImpl implements WorkbenchModule {
	/**
	 * The default value of the '{@link #getHandle() <em>Handle</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getHandle()
	 * @generated
	 * @ordered
	 */
	protected static final URI HANDLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getHandle() <em>Handle</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getHandle()
	 * @generated
	 * @ordered
	 */
	protected URI handle = HANDLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDeployedName() <em>Deployed Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getDeployedName()
	 * @generated
	 * @ordered
	 */
	protected static final String DEPLOYED_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeployedName() <em>Deployed Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDeployedName()
	 * @generated
	 * @ordered
	 */
	protected String deployedName = DEPLOYED_NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getResources() <em>Resources</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getResources()
	 * @generated
	 * @ordered
	 */
	protected EList resources = null;

	/**
	 * The cached value of the '{@link #getModuleType() <em>Module Type</em>}' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getModuleType()
	 * @generated
	 * @ordered
	 */
	protected ModuleType moduleType = null;

	/**
	 * The cached value of the '{@link #getModules() <em>Modules</em>}' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getModules()
	 * @generated
	 * @ordered
	 */
	protected EList modules = null;

	private final Map resourceIndexByDeployPath = new HashMap();
	private final Map resourceIndexBySourcePath = new HashMap();

	private boolean isIndexedByDeployPath;

	private boolean isIndexedBySourcePath;

	private static final WorkbenchModuleResource[] NO_MODULE_RESOURCES = new WorkbenchModuleResource[0];

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected WorkbenchModuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getWorkbenchModule();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public URI getHandle() {
		return handle;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setHandle(URI newHandle) {
		URI oldHandle = handle;
		handle = newHandle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE__HANDLE, oldHandle, handle));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getDeployedName() {
		return deployedName;
	}

	public void setDeployedName(String newDeployedName) {
		setDeployedNameGen(newDeployedName);
		// TODO A more advanced adapter should be applied to keep the handle up to date.
		if (eResource() != null) {
			URI resourceURI = eResource().getURI();
			String safeDeployedName = getDeployedName() != null ? getDeployedName() : ""; //$NON-NLS-1$
			if (resourceURI != null && resourceURI.segmentCount() >= 2)
				setHandle(URI.createURI(PlatformURLModuleConnection.MODULE_PROTOCOL + IPath.SEPARATOR + "resource" + IPath.SEPARATOR + resourceURI.segment(1) + IPath.SEPARATOR + safeDeployedName));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setDeployedNameGen(String newDeployedName) {
		String oldDeployedName = deployedName;
		deployedName = newDeployedName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_NAME, oldDeployedName, deployedName));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getModules() {
		if (modules == null) {
			modules = new EObjectResolvingEList(DependentModule.class, this, ModuleCorePackage.WORKBENCH_MODULE__MODULES);
		}
		return modules;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getResources() {
		if (resources == null) {
			resources = new EObjectContainmentWithInverseEList(WorkbenchModuleResource.class, this, ModuleCorePackage.WORKBENCH_MODULE__RESOURCES, ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE);
		}
		return resources;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleType getModuleType() {
		if (moduleType != null && moduleType.eIsProxy()) {
			ModuleType oldModuleType = moduleType;
			moduleType = (ModuleType)eResolveProxy((InternalEObject)moduleType);
			if (moduleType != oldModuleType) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE, oldModuleType, moduleType));
			}
		}
		return moduleType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleType basicGetModuleType() {
		return moduleType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setModuleType(ModuleType newModuleType) {
		ModuleType oldModuleType = moduleType;
		moduleType = newModuleType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE, oldModuleType, moduleType));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
					return ((InternalEList)getResources()).basicAdd(otherEnd, msgs);
				default:
					return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
			}
		}
		if (eContainer != null)
			msgs = eBasicRemoveFromContainer(msgs);
		return eBasicSetContainer(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
					return ((InternalEList)getResources()).basicRemove(otherEnd, msgs);
				default:
					return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
			}
		}
		return eBasicSetContainer(null, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.WORKBENCH_MODULE__HANDLE:
				return getHandle();
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_NAME:
				return getDeployedName();
			case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
				return getResources();
			case ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE:
				if (resolve) return getModuleType();
				return basicGetModuleType();
			case ModuleCorePackage.WORKBENCH_MODULE__MODULES:
				return getModules();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.WORKBENCH_MODULE__HANDLE:
				setHandle((URI)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_NAME:
				setDeployedName((String)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
				getResources().clear();
				getResources().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE:
				setModuleType((ModuleType)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__MODULES:
				getModules().clear();
				getModules().addAll((Collection)newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.WORKBENCH_MODULE__HANDLE:
				setHandle(HANDLE_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_NAME:
				setDeployedName(DEPLOYED_NAME_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
				getResources().clear();
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE:
				setModuleType((ModuleType)null);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__MODULES:
				getModules().clear();
				return;
		}
		eDynamicUnset(eFeature);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.WORKBENCH_MODULE__HANDLE:
				return HANDLE_EDEFAULT == null ? handle != null : !HANDLE_EDEFAULT.equals(handle);
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_NAME:
				return DEPLOYED_NAME_EDEFAULT == null ? deployedName != null : !DEPLOYED_NAME_EDEFAULT.equals(deployedName);
			case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
				return resources != null && !resources.isEmpty();
			case ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE:
				return moduleType != null;
			case ModuleCorePackage.WORKBENCH_MODULE__MODULES:
				return modules != null && !modules.isEmpty();
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (handle: ");
		result.append(handle);
		result.append(", deployedName: ");
		result.append(deployedName);
		result.append(')');
		return result.toString();
	}

	public WorkbenchModuleResource[] findWorkbenchModuleResourceByDeployPath(URI aDeployPath) {
		// if (!isIndexedByDeployPath)
		// indexResourcesByDeployPath();
		// return (WorkbenchModuleResource) resourceIndexByDeployPath.get(aDeployPath);
		IPath resourcePath = new Path(aDeployPath.path());
		ResourceTreeRoot resourceTreeRoot = ModuleCore.getDeployResourceTreeRoot(this);
		return resourceTreeRoot.findModuleResources(resourcePath, false); 
	}

	public WorkbenchModuleResource[] findWorkbenchModuleResourceBySourcePath(URI aSourcePath) {
		// if(!isIndexedBySourcePath)
		// indexResourcesBySourcePath();
		try {
			if (ModuleURIUtil.ensureValidFullyQualifiedPlatformURI(aSourcePath, false)) {
				IPath resourcePath = new Path(aSourcePath.path()).removeFirstSegments(1);
				ResourceTreeRoot resourceTreeRoot = ModuleCore.getSourceResourceTreeRoot(this);
				return resourceTreeRoot.findModuleResources(resourcePath, false);
			}
		} catch (UnresolveableURIException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void indexResourcesByDeployPath() {
		if (isIndexedByDeployPath)
			return;

		synchronized (resourceIndexByDeployPath) {
			// TODO We need a resource indexing adapter to keep the index up to date
			// Adapter adapter = EcoreUtil.getAdapter(eAdapters(), ModuleIndexingAdapter.class);
			// if (adapter == null)
			// eAdapters().add((adapter = new ModuleIndexingAdapter()));

			WorkbenchModuleResource resource = null;
			for (Iterator iter = getResources().iterator(); iter.hasNext();) {
				resource = (WorkbenchModuleResource) iter.next();
				resourceIndexByDeployPath.put(resource.getDeployedPath(), resource);
			}
		}
		isIndexedByDeployPath = true;
	}

	// private void indexResourcesBySourcePath() {
	// if (isIndexedBySourcePath)
	// return;
	//
	// synchronized (resourceIndexBySourcePath) {
	// // TODO We need a resource indexing adapter to keep the index up to date
	// // Adapter adapter = EcoreUtil.getAdapter(eAdapters(), ModuleIndexingAdapter.class);
	// // if (adapter == null)
	// // eAdapters().add((adapter = new ModuleIndexingAdapter()));
	//			
	// WorkbenchModuleResource resource = null;
	// URI projectRelativePath = null;
	// for(Iterator iter = getResources().iterator(); iter.hasNext(); ) {
	// try {
	// resource = (WorkbenchModuleResource) iter.next();
	// projectRelativePath =
	// ModuleURIUtil.trimWorkspacePathToProjectRelativeURI(resource.getSourcePath());
	// resourceIndexBySourcePath.put(projectRelativePath, resource);
	// } catch (UnresolveableURIException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// isIndexedBySourcePath = true;
	// }

} // WorkbenchModuleImpl
