/**
 * <copyright>
 * </copyright>
 *
 * $Id: ProjectComponentsImpl.java,v 1.2 2005/08/26 16:45:31 cbridgha Exp $
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.ProjectComponents;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Project Modules</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ProjectComponentsImpl#getProjectName <em>Project Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ProjectComponentsImpl#getComponents <em>Components</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProjectComponentsImpl extends EObjectImpl implements ProjectComponents {
	/**
	 * The default value of the '{@link #getProjectName() <em>Project Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected static final String PROJECT_NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getProjectName() <em>Project Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected String projectName = PROJECT_NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getComponents() <em>Components</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponents()
	 * @generated
	 * @ordered
	 */
	protected EList components = null;

	private boolean isIndexed;

	private final Map modulesIndex = new HashMap();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected ProjectComponentsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ComponentcorePackage.eINSTANCE.getProjectComponents();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setProjectName(String newProjectName) {
		String oldProjectName = projectName;
		projectName = newProjectName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.PROJECT_COMPONENTS__PROJECT_NAME, oldProjectName, projectName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getComponents() {
		if (components == null) {
			components = new EObjectContainmentEList(WorkbenchComponent.class, this, ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS);
		}
		return components;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS:
					return ((InternalEList)getComponents()).basicRemove(otherEnd, msgs);
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
			case ComponentcorePackage.PROJECT_COMPONENTS__PROJECT_NAME:
				return getProjectName();
			case ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS:
				return getComponents();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.PROJECT_COMPONENTS__PROJECT_NAME:
				setProjectName((String)newValue);
				return;
			case ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS:
				getComponents().clear();
				getComponents().addAll((Collection)newValue);
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
			case ComponentcorePackage.PROJECT_COMPONENTS__PROJECT_NAME:
				setProjectName(PROJECT_NAME_EDEFAULT);
				return;
			case ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS:
				getComponents().clear();
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
			case ComponentcorePackage.PROJECT_COMPONENTS__PROJECT_NAME:
				return PROJECT_NAME_EDEFAULT == null ? projectName != null : !PROJECT_NAME_EDEFAULT.equals(projectName);
			case ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS:
				return components != null && !components.isEmpty();
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
		result.append(" (projectName: ");
		result.append(projectName);
		result.append(')');
		return result.toString();
	}

	public WorkbenchComponent findWorkbenchModule(String aDeployName) {
		if (getComponents().size() == 1)
			return ((WorkbenchComponent)getComponents().get(0)).getName().equals(aDeployName) ? (WorkbenchComponent)getComponents().get(0) : null;
		if (!isIndexed()) 
			indexModules(); 
		return (WorkbenchComponent) getModulesIndex().get(aDeployName);
	}

	/**
	 * @return
	 */
	private boolean isIndexed() {
		return isIndexed;
	}

	/**
	 * 
	 */
	private void indexModules() {
		if (isIndexed)
			return;

		synchronized (modulesIndex) {
			Adapter adapter = EcoreUtil.getAdapter(eAdapters(), ModuleIndexingAdapter.class);
			if (adapter == null) 
				eAdapters().add((adapter = new ModuleIndexingAdapter()));
			
			WorkbenchComponent module = null;
			for(Iterator iter = getComponents().iterator(); iter.hasNext(); ) {
				module = (WorkbenchComponent) iter.next();
				modulesIndex.put(module.getName(), module);
			}
		}
		isIndexed = true;
	}

	/**
	 * @return
	 */
	/* package */ Map getModulesIndex() {
		return modulesIndex;
	}

} // ProjectComponentsImpl
