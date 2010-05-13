/**
 * <copyright>
 * </copyright>
 *
 * $Id: ComponentResourceImpl.java,v 1.4 2010/05/13 04:04:35 canderson Exp $
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.internal.emf.utilities.ExtendedEcoreUtil.ESynchronizedAdapterList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workbench Module Resource</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentResourceImpl#getSourcePath <em>Source Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentResourceImpl#getRuntimePath <em>Runtime Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentResourceImpl#getExclusions <em>Exclusions</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentResourceImpl#getComponent <em>Component</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentResourceImpl#getResourceType <em>Resource Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentResourceImpl extends EObjectImpl implements ComponentResource {
	/**
	 * The default value of the '{@link #getSourcePath() <em>Source Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSourcePath()
	 * @generated
	 * @ordered
	 */
	protected static final IPath SOURCE_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSourcePath() <em>Source Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSourcePath()
	 * @generated
	 * @ordered
	 */
	protected IPath sourcePath = SOURCE_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getRuntimePath() <em>Runtime Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuntimePath()
	 * @generated
	 * @ordered
	 */
	protected static final IPath RUNTIME_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRuntimePath() <em>Runtime Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuntimePath()
	 * @generated
	 * @ordered
	 */
	protected IPath runtimePath = RUNTIME_PATH_EDEFAULT;

	/**
	 * The cached value of the '{@link #getExclusions() <em>Exclusions</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusions()
	 * @generated
	 * @ordered
	 */
	protected EList exclusions = null;
	
	/**
	 * The default value of the '{@link #getResourceType() <em>Resource Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResourceType()
	 * @generated
	 * @ordered
	 */
	protected static final String RESOURCE_TYPE_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getResourceType() <em>Resource Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResourceType()
	 * @generated
	 * @ordered
	 */
	protected String resourceType = RESOURCE_TYPE_EDEFAULT;

	protected IProject owningProject;
	protected static final int VIRTUAL = 0;
	protected static final int PERSISTED = 1;
	
	private int type = PERSISTED;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentResourceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ComponentcorePackage.eINSTANCE.getComponentResource();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IPath getSourcePath() {
		return sourcePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSourcePath(IPath newSourcePath) {
		IPath oldSourcePath = sourcePath;
		sourcePath = newSourcePath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.COMPONENT_RESOURCE__SOURCE_PATH, oldSourcePath, sourcePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IPath getRuntimePath() {
		return runtimePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRuntimePath(IPath newRuntimePath) {
		IPath oldRuntimePath = runtimePath;
		runtimePath = newRuntimePath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.COMPONENT_RESOURCE__RUNTIME_PATH, oldRuntimePath, runtimePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getExclusions() {
		if (exclusions == null) {
			exclusions = new EDataTypeUniqueEList(String.class, this, ComponentcorePackage.COMPONENT_RESOURCE__EXCLUSIONS);
		}
		return exclusions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WorkbenchComponent getComponent() {
		if (eContainerFeatureID != ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT) return null;
		return (WorkbenchComponent)eContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComponent(WorkbenchComponent newComponent) {
		if (newComponent != eContainer || (eContainerFeatureID != ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT && newComponent != null)) {
			if (EcoreUtil.isAncestor(this, newComponent))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eContainer != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newComponent != null)
				msgs = ((InternalEObject)newComponent).eInverseAdd(this, ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES, WorkbenchComponent.class, msgs);
			msgs = eBasicSetContainer((InternalEObject)newComponent, ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT, newComponent, newComponent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getResourceType() {
		return resourceType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setResourceType(String newResourceType) {
		String oldResourceType = resourceType;
		resourceType = newResourceType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.COMPONENT_RESOURCE__RESOURCE_TYPE, oldResourceType, resourceType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT:
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT, msgs);
				default:
					return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
			}
		}
		if (eContainer != null)
			msgs = eBasicRemoveFromContainer(msgs);
		return eBasicSetContainer(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT:
					return eBasicSetContainer(null, ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT, msgs);
				default:
					return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
			}
		}
		return eBasicSetContainer(null, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
		if (eContainerFeatureID >= 0) {
			switch (eContainerFeatureID) {
				case ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT:
					return eContainer.eInverseRemove(this, ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES, WorkbenchComponent.class, msgs);
				default:
					return eDynamicBasicRemoveFromContainer(msgs);
			}
		}
		return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.COMPONENT_RESOURCE__SOURCE_PATH:
				return getSourcePath();
			case ComponentcorePackage.COMPONENT_RESOURCE__RUNTIME_PATH:
				return getRuntimePath();
			case ComponentcorePackage.COMPONENT_RESOURCE__EXCLUSIONS:
				return getExclusions();
			case ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT:
				return getComponent();
			case ComponentcorePackage.COMPONENT_RESOURCE__RESOURCE_TYPE:
				return getResourceType();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.COMPONENT_RESOURCE__SOURCE_PATH:
				setSourcePath((IPath)newValue);
				return;
			case ComponentcorePackage.COMPONENT_RESOURCE__RUNTIME_PATH:
				setRuntimePath((IPath)newValue);
				return;
			case ComponentcorePackage.COMPONENT_RESOURCE__EXCLUSIONS:
				getExclusions().clear();
				getExclusions().addAll((Collection)newValue);
				return;
			case ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT:
				setComponent((WorkbenchComponent)newValue);
				return;
			case ComponentcorePackage.COMPONENT_RESOURCE__RESOURCE_TYPE:
				setResourceType((String)newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.COMPONENT_RESOURCE__SOURCE_PATH:
				setSourcePath(SOURCE_PATH_EDEFAULT);
				return;
			case ComponentcorePackage.COMPONENT_RESOURCE__RUNTIME_PATH:
				setRuntimePath(RUNTIME_PATH_EDEFAULT);
				return;
			case ComponentcorePackage.COMPONENT_RESOURCE__EXCLUSIONS:
				getExclusions().clear();
				return;
			case ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT:
				setComponent((WorkbenchComponent)null);
				return;
			case ComponentcorePackage.COMPONENT_RESOURCE__RESOURCE_TYPE:
				setResourceType(RESOURCE_TYPE_EDEFAULT);
				return;
		}
		eDynamicUnset(eFeature);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.COMPONENT_RESOURCE__SOURCE_PATH:
				return SOURCE_PATH_EDEFAULT == null ? sourcePath != null : !SOURCE_PATH_EDEFAULT.equals(sourcePath);
			case ComponentcorePackage.COMPONENT_RESOURCE__RUNTIME_PATH:
				return RUNTIME_PATH_EDEFAULT == null ? runtimePath != null : !RUNTIME_PATH_EDEFAULT.equals(runtimePath);
			case ComponentcorePackage.COMPONENT_RESOURCE__EXCLUSIONS:
				return exclusions != null && !exclusions.isEmpty();
			case ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT:
				return getComponent() != null;
			case ComponentcorePackage.COMPONENT_RESOURCE__RESOURCE_TYPE:
				return RESOURCE_TYPE_EDEFAULT == null ? resourceType != null : !RESOURCE_TYPE_EDEFAULT.equals(resourceType);
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (sourcePath: ");
		result.append(sourcePath);
		result.append(", runtimePath: ");
		result.append(runtimePath);
		result.append(", exclusions: ");
		result.append(exclusions);
		result.append(", resourceType: ");
		result.append(resourceType);
		result.append(')');
		return result.toString();
	}

	public int getType() {
		return type;
	}
	
	
	public void setType(int type) {
		this.type = type;
	}

	public IProject getOwningProject() {
		return owningProject;
	}

	public void setOwningProject(IProject aProject) {
		owningProject = aProject;
	}
	
	@Override
	public EList eAdapters()
	  {
	    if (eAdapters == null)
	    {
	      eAdapters =  new ESynchronizedAdapterList(this);
	    }
	    return eAdapters;
	  }
	

} //ComponentResourceImpl
