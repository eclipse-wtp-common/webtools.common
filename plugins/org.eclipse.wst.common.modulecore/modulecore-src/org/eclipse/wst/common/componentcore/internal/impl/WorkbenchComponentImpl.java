/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchComponentImpl.java,v 1.9 2006/03/27 21:49:41 vbhadrir Exp $
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
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
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ComponentType;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.Property;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Workbench Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl#getComponentType <em>Component Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl#getReferencedComponents <em>Referenced Components</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.WorkbenchComponentImpl#getMetadataResources <em>Metadata Resources</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkbenchComponentImpl extends EObjectImpl implements WorkbenchComponent {
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
	 * The cached value of the '{@link #getComponentType() <em>Component Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponentType()
	 * @generated
	 * @ordered
	 */
	protected ComponentType componentType = null;

	/**
	 * The cached value of the '{@link #getReferencedComponents() <em>Referenced Components</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferencedComponents()
	 * @generated
	 * @ordered
	 */
	protected EList referencedComponents = null;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected EList properties = null;

	/**
	 * The cached value of the '{@link #getMetadataResources() <em>Metadata Resources</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMetadataResources()
	 * @generated
	 * @ordered
	 */
	protected EList metadataResources = null;

	private final Map resourceIndexByDeployPath = new HashMap();
	private final Map resourceIndexBySourcePath = new HashMap();

	private boolean isIndexedByDeployPath;

	private boolean isIndexedBySourcePath;

	private static final ComponentResource[] NO_MODULE_RESOURCES = new ComponentResource[0];

	private URI handle;
	
	private IPath defaultSourceRoot;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected WorkbenchComponentImpl() {
		super();
	}

	private IPath getFirstRootSource() {
		
		List res = getResources();
		for (Iterator iter = res.iterator(); iter.hasNext();) {
			ComponentResource element = (ComponentResource) iter.next();
			if (element.getRuntimePath().equals(new Path("/")))
				return element.getSourcePath();
			
		}	
		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ComponentcorePackage.eINSTANCE.getWorkbenchComponent();
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
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.WORKBENCH_COMPONENT__NAME, oldName, name));
	}

	public void setName(String newDeployedName) {
		setNameGen(newDeployedName);
		// TODO A more advanced adapter should be applied to keep the handle up to date.
		if (eResource() != null) {
			URI resourceURI = eResource().getURI();
			String safeDeployedName = getName() != null ? getName() : ""; //$NON-NLS-1$
			if (resourceURI != null && resourceURI.segmentCount() >= 2)
				setHandle(computeHandle());
		}
	}
	
	protected void setHandle(URI aHandle) {
		handle = aHandle;
	}
	
	public URI getHandle() {
		if(handle == null)
			handle = computeHandle();
		return handle;
	}

	private URI computeHandle() {
		return URI.createURI(PlatformURLModuleConnection.MODULE_PROTOCOL + IPath.SEPARATOR + PlatformURLModuleConnection.RESOURCE_MODULE + IPath.SEPARATOR + computeProjectName() + IPath.SEPARATOR + getName());
		
	}

	private String computeProjectName() {
		IProject project = ProjectUtilities.getProject(this);
		return (project!=null)?project.getName():"UNCONTAINED"; //$NON-NLS-1$
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getResources() {
		if (resources == null) {
			resources = new EObjectContainmentWithInverseEList(ComponentResource.class, this, ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES, ComponentcorePackage.COMPONENT_RESOURCE__COMPONENT);
		}
		return resources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentType getComponentType() {
		return componentType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetComponentType(ComponentType newComponentType, NotificationChain msgs) {
		ComponentType oldComponentType = componentType;
		componentType = newComponentType;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE, oldComponentType, newComponentType);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComponentType(ComponentType newComponentType) {
		if (newComponentType != componentType) {
			NotificationChain msgs = null;
			if (componentType != null)
				msgs = ((InternalEObject)componentType).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE, null, msgs);
			if (newComponentType != null)
				msgs = ((InternalEObject)newComponentType).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE, null, msgs);
			msgs = basicSetComponentType(newComponentType, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE, newComponentType, newComponentType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getReferencedComponents() {
		if (referencedComponents == null) {
			referencedComponents = new EObjectContainmentEList(ReferencedComponent.class, this, ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS);
		}
		return referencedComponents;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getProperties() {
		if (properties == null) {
			properties = new EObjectResolvingEList(Property.class, this, ComponentcorePackage.WORKBENCH_COMPONENT__PROPERTIES);
		}
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getMetadataResources() {
		if (metadataResources == null) {
			metadataResources = new EDataTypeUniqueEList(IPath.class, this, ComponentcorePackage.WORKBENCH_COMPONENT__METADATA_RESOURCES);
		}
		return metadataResources;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
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
				case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
					return ((InternalEList)getResources()).basicRemove(otherEnd, msgs);
				case ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
					return basicSetComponentType(null, msgs);
				case ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
					return ((InternalEList)getReferencedComponents()).basicRemove(otherEnd, msgs);
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
			case ComponentcorePackage.WORKBENCH_COMPONENT__NAME:
				return getName();
			case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
				return getResources();
			case ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				return getComponentType();
			case ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				return getReferencedComponents();
			case ComponentcorePackage.WORKBENCH_COMPONENT__PROPERTIES:
				return getProperties();
			case ComponentcorePackage.WORKBENCH_COMPONENT__METADATA_RESOURCES:
				return getMetadataResources();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.WORKBENCH_COMPONENT__NAME:
				setName((String)newValue);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
				getResources().clear();
				getResources().addAll((Collection)newValue);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				setComponentType((ComponentType)newValue);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				getReferencedComponents().clear();
				getReferencedComponents().addAll((Collection)newValue);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__PROPERTIES:
				getProperties().clear();
				getProperties().addAll((Collection)newValue);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__METADATA_RESOURCES:
				getMetadataResources().clear();
				getMetadataResources().addAll((Collection)newValue);
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
			case ComponentcorePackage.WORKBENCH_COMPONENT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
				getResources().clear();
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				setComponentType((ComponentType)null);
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				getReferencedComponents().clear();
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__PROPERTIES:
				getProperties().clear();
				return;
			case ComponentcorePackage.WORKBENCH_COMPONENT__METADATA_RESOURCES:
				getMetadataResources().clear();
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
			case ComponentcorePackage.WORKBENCH_COMPONENT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ComponentcorePackage.WORKBENCH_COMPONENT__RESOURCES:
				return resources != null && !resources.isEmpty();
			case ComponentcorePackage.WORKBENCH_COMPONENT__COMPONENT_TYPE:
				return componentType != null;
			case ComponentcorePackage.WORKBENCH_COMPONENT__REFERENCED_COMPONENTS:
				return referencedComponents != null && !referencedComponents.isEmpty();
			case ComponentcorePackage.WORKBENCH_COMPONENT__PROPERTIES:
				return properties != null && !properties.isEmpty();
			case ComponentcorePackage.WORKBENCH_COMPONENT__METADATA_RESOURCES:
				return metadataResources != null && !metadataResources.isEmpty();
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
		result.append(" (name: ");
		result.append(name);
		result.append(", metadataResources: ");
		result.append(metadataResources);
		result.append(')');
		return result.toString();
	}

	public ComponentResource[] findResourcesByRuntimePath(IPath aDeployPath) { 
		ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getDeployResourceTreeRoot(this);
		return resourceTreeRoot.findModuleResources(aDeployPath, ResourceTreeNode.CREATE_NONE); 
	}

	public ComponentResource[] findResourcesBySourcePath(IPath aSourcePath, int resourceFlag) { 
		ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getSourceResourceTreeRoot(this);
		return resourceTreeRoot.findModuleResources(aSourcePath, resourceFlag); 
	}
	public boolean exists(IPath aSourcePath, int resourceFlag) { 
		ResourceTreeRoot resourceTreeRoot = ResourceTreeRoot.getSourceResourceTreeRoot(this);
		return resourceTreeRoot.exists(aSourcePath, resourceFlag); 
	}
	
	public IPath getDefaultSourceRoot() {
		if (defaultSourceRoot == null)
			defaultSourceRoot = getFirstRootSource();
		return defaultSourceRoot;
	}
	
	public void setDefaultSourceRoot(IPath defaultSourceRoot) {
		this.defaultSourceRoot = defaultSourceRoot;
	}
  

} // WorkbenchComponentImpl
