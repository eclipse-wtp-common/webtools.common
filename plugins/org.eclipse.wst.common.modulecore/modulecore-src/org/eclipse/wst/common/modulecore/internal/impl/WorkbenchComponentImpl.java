/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchComponentImpl.java,v 1.3 2005/03/15 02:36:13 cbridgha Exp $
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
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchComponentImpl#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchComponentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchComponentImpl#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchComponentImpl#getComponentType <em>Component Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchComponentImpl#getReferencedComponents <em>Referenced Components</em>}</li>
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
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getResources() <em>Resources</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getResources()
	 * @generated
	 * @ordered
	 */
	protected EList resources = null;

	/**
	 * The cached value of the '{@link #getComponentType() <em>Component Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponentType()
	 * @generated
	 * @ordered
	 */
	protected ComponentType componentType = null;

	/**
	 * The cached value of the '{@link #getReferencedComponents() <em>Referenced Components</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferencedComponents()
	 * @generated
	 * @ordered
	 */
	protected EList referencedComponents = null;

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
		return ModuleCorePackage.eINSTANCE.getWorkbenchComponent();
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_COMPONENT__HANDLE, oldHandle, handle));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNameGen(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_COMPONENT__NAME, oldName, name));
	}

	public void setName(String newDeployedName) {
		setNameGen(newDeployedName);
		// TODO A more advanced adapter should be applied to keep the handle up to date.
		if (eResource() != null) {
			URI resourceURI = eResource().getURI();
			String safeDeployedName = getName() != null ? getName() : ""; //$NON-NLS-1$
			if (resourceURI != null && resourceURI.segmentCount() >= 2)
				setHandle(URI.createURI(PlatformURLModuleConnection.MODULE_PROTOCOL + IPath.SEPARATOR + "resource" + IPath.SEPARATOR + resourceURI.segment(1) + IPath.SEPARATOR + safeDeployedName));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getResources() {
		if (resources == null) {
			resources = new EObjectContainmentWithInverseEList(ComponentResource.class, this, ModuleCorePackage.WORKBENCH_COMPONENT__RESOURCES, ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT);
		}
		return resources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentType getComponentType() {
		if (componentType != null && componentType.eIsProxy()) {
			ComponentType oldComponentType = componentType;
			componentType = (ComponentType)eResolveProxy((InternalEObject)componentType);
			if (componentType != oldComponentType) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModuleCorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE, oldComponentType, componentType));
			}
		}
		return componentType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentType basicGetComponentType() {
		return componentType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComponentType(ComponentType newComponentType) {
		ComponentType oldComponentType = componentType;
		componentType = newComponentType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE, oldComponentType, componentType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getReferencedComponents() {
		if (referencedComponents == null) {
			referencedComponents = new EObjectResolvingEList(ReferencedComponent.class, this, ModuleCorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS);
		}
		return referencedComponents;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.WORKBENCH_COMPONENT__RESOURCES:
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
				case ModuleCorePackage.WORKBENCH_COMPONENT__RESOURCES:
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
			case ModuleCorePackage.WORKBENCH_COMPONENT__HANDLE:
				return getHandle();
			case ModuleCorePackage.WORKBENCH_COMPONENT__NAME:
				return getName();
			case ModuleCorePackage.WORKBENCH_COMPONENT__RESOURCES:
				return getResources();
			case ModuleCorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				if (resolve) return getComponentType();
				return basicGetComponentType();
			case ModuleCorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				return getReferencedComponents();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.WORKBENCH_COMPONENT__HANDLE:
				setHandle((URI)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_COMPONENT__NAME:
				setName((String)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_COMPONENT__RESOURCES:
				getResources().clear();
				getResources().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				setComponentType((ComponentType)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				getReferencedComponents().clear();
				getReferencedComponents().addAll((Collection)newValue);
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
			case ModuleCorePackage.WORKBENCH_COMPONENT__HANDLE:
				setHandle(HANDLE_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_COMPONENT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_COMPONENT__RESOURCES:
				getResources().clear();
				return;
			case ModuleCorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				setComponentType((ComponentType)null);
				return;
			case ModuleCorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				getReferencedComponents().clear();
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
			case ModuleCorePackage.WORKBENCH_COMPONENT__HANDLE:
				return HANDLE_EDEFAULT == null ? handle != null : !HANDLE_EDEFAULT.equals(handle);
			case ModuleCorePackage.WORKBENCH_COMPONENT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ModuleCorePackage.WORKBENCH_COMPONENT__RESOURCES:
				return resources != null && !resources.isEmpty();
			case ModuleCorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				return componentType != null;
			case ModuleCorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				return referencedComponents != null && !referencedComponents.isEmpty();
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
		result.append(", name: ");
		result.append(name);
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
