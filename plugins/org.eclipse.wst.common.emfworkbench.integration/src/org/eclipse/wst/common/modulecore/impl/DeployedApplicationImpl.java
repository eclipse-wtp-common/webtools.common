/**
 * <copyright>
 * </copyright>
 *
 * $Id: DeployedApplicationImpl.java,v 1.1 2005/01/17 21:08:17 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.wst.common.modulecore.DeployScheme;
import org.eclipse.wst.common.modulecore.DeployedApplication;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.WorkbenchApplication;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Deployed Application</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.DeployedApplicationImpl#getRoot <em>Root</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.DeployedApplicationImpl#getDeployScheme <em>Deploy Scheme</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.DeployedApplicationImpl#getApplication <em>Application</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DeployedApplicationImpl extends EObjectImpl implements DeployedApplication {
	/**
	 * The default value of the '{@link #getRoot() <em>Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRoot()
	 * @generated
	 * @ordered
	 */
	protected static final URI ROOT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRoot() <em>Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRoot()
	 * @generated
	 * @ordered
	 */
	protected URI root = ROOT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getDeployScheme() <em>Deploy Scheme</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeployScheme()
	 * @generated
	 * @ordered
	 */
	protected DeployScheme deployScheme = null;

	/**
	 * The cached value of the '{@link #getApplication() <em>Application</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getApplication()
	 * @generated
	 * @ordered
	 */
	protected WorkbenchApplication application = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DeployedApplicationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getDeployedApplication();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public URI getRoot() {
		return root;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRoot(URI newRoot) {
		URI oldRoot = root;
		root = newRoot;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.DEPLOYED_APPLICATION__ROOT, oldRoot, root));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeployScheme getDeployScheme() {
		return deployScheme;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDeployScheme(DeployScheme newDeployScheme, NotificationChain msgs) {
		DeployScheme oldDeployScheme = deployScheme;
		deployScheme = newDeployScheme;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ModuleCorePackage.DEPLOYED_APPLICATION__DEPLOY_SCHEME, oldDeployScheme, newDeployScheme);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDeployScheme(DeployScheme newDeployScheme) {
		if (newDeployScheme != deployScheme) {
			NotificationChain msgs = null;
			if (deployScheme != null)
				msgs = ((InternalEObject)deployScheme).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ModuleCorePackage.DEPLOYED_APPLICATION__DEPLOY_SCHEME, null, msgs);
			if (newDeployScheme != null)
				msgs = ((InternalEObject)newDeployScheme).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ModuleCorePackage.DEPLOYED_APPLICATION__DEPLOY_SCHEME, null, msgs);
			msgs = basicSetDeployScheme(newDeployScheme, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.DEPLOYED_APPLICATION__DEPLOY_SCHEME, newDeployScheme, newDeployScheme));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WorkbenchApplication getApplication() {
		if (application != null && application.eIsProxy()) {
			WorkbenchApplication oldApplication = application;
			application = (WorkbenchApplication)eResolveProxy((InternalEObject)application);
			if (application != oldApplication) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModuleCorePackage.DEPLOYED_APPLICATION__APPLICATION, oldApplication, application));
			}
		}
		return application;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WorkbenchApplication basicGetApplication() {
		return application;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setApplication(WorkbenchApplication newApplication) {
		WorkbenchApplication oldApplication = application;
		application = newApplication;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.DEPLOYED_APPLICATION__APPLICATION, oldApplication, application));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.DEPLOYED_APPLICATION__DEPLOY_SCHEME:
					return basicSetDeployScheme(null, msgs);
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
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.DEPLOYED_APPLICATION__ROOT:
				return getRoot();
			case ModuleCorePackage.DEPLOYED_APPLICATION__DEPLOY_SCHEME:
				return getDeployScheme();
			case ModuleCorePackage.DEPLOYED_APPLICATION__APPLICATION:
				if (resolve) return getApplication();
				return basicGetApplication();
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
			case ModuleCorePackage.DEPLOYED_APPLICATION__ROOT:
				setRoot((URI)newValue);
				return;
			case ModuleCorePackage.DEPLOYED_APPLICATION__DEPLOY_SCHEME:
				setDeployScheme((DeployScheme)newValue);
				return;
			case ModuleCorePackage.DEPLOYED_APPLICATION__APPLICATION:
				setApplication((WorkbenchApplication)newValue);
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
			case ModuleCorePackage.DEPLOYED_APPLICATION__ROOT:
				setRoot(ROOT_EDEFAULT);
				return;
			case ModuleCorePackage.DEPLOYED_APPLICATION__DEPLOY_SCHEME:
				setDeployScheme((DeployScheme)null);
				return;
			case ModuleCorePackage.DEPLOYED_APPLICATION__APPLICATION:
				setApplication((WorkbenchApplication)null);
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
			case ModuleCorePackage.DEPLOYED_APPLICATION__ROOT:
				return ROOT_EDEFAULT == null ? root != null : !ROOT_EDEFAULT.equals(root);
			case ModuleCorePackage.DEPLOYED_APPLICATION__DEPLOY_SCHEME:
				return deployScheme != null;
			case ModuleCorePackage.DEPLOYED_APPLICATION__APPLICATION:
				return application != null;
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
		result.append(" (root: ");
		result.append(root);
		result.append(')');
		return result.toString();
	}

} //DeployedApplicationImpl
