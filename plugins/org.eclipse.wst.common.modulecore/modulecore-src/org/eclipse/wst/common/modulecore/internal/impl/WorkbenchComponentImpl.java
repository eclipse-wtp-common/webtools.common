/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchComponentImpl.java,v 1.1 2005/03/15 00:43:55 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.internal.impl;

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
import org.eclipse.wst.common.modulecore.ReferencedComponent;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ComponentType;
import org.eclipse.wst.common.modulecore.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.ComponentResource;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Workbench Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.internal.impl.WorkbenchComponentImpl#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.internal.impl.WorkbenchComponentImpl#getDeployedName <em>Deployed Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.internal.impl.WorkbenchComponentImpl#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.internal.impl.WorkbenchComponentImpl#getModuleType <em>Module Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.internal.impl.WorkbenchComponentImpl#getModules <em>Modules</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkbenchComponentImpl extends EObjectImpl implements WorkbenchComponent {
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
	protected ComponentType moduleType = null;

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

	private static final ComponentResource[] NO_MODULE_RESOURCES = new ComponentResource[0];

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected WorkbenchComponentImpl() {
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
			modules = new EObjectResolvingEList(ReferencedComponent.class, this, ModuleCorePackage.WORKBENCH_MODULE__MODULES);
		}
		return modules;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getResources() {
		if (resources == null) {
			resources = new EObjectContainmentWithInverseEList(ComponentResource.class, this, ModuleCorePackage.WORKBENCH_MODULE__RESOURCES, ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE);
		}
		return resources;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentType getModuleType() {
		if (moduleType != null && moduleType.eIsProxy()) {
			ComponentType oldModuleType = moduleType;
			moduleType = (ComponentType)eResolveProxy((InternalEObject)moduleType);
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
	public ComponentType basicGetModuleType() {
		return moduleType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setModuleType(ComponentType newModuleType) {
		ComponentType oldModuleType = moduleType;
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
				setModuleType((ComponentType)newValue);
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
				setModuleType((ComponentType)null);
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

	public ComponentResource[] findWorkbenchModuleResourceByDeployPath(URI aDeployPath) {
		// if (!isIndexedByDeployPath)
		// indexResourcesByDeployPath();
		// return (ComponentResource) resourceIndexByDeployPath.get(aDeployPath);
		IPath resourcePath = new Path(aDeployPath.path());
		ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getDeployResourceTreeRoot(this);
		return resourceTreeRoot.findModuleResources(resourcePath, false); 
	}

	public ComponentResource[] findWorkbenchModuleResourceBySourcePath(URI aSourcePath) {
		// if(!isIndexedBySourcePath)
		// indexResourcesBySourcePath();
		try {
			if (ModuleURIUtil.ensureValidFullyQualifiedPlatformURI(aSourcePath, false)) {
				IPath resourcePath = new Path(aSourcePath.path()).removeFirstSegments(1);
				ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getSourceResourceTreeRoot(this);
				return resourceTreeRoot.findModuleResources(resourcePath, false);
			}
		} catch (UnresolveableURIException e) {
			e.printStackTrace();
		}
		return NO_MODULE_RESOURCES;
	}
  

} // WorkbenchComponentImpl
