/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleCoreSwitch.java,v 1.10 2005/02/02 22:14:45 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.modulecore.DependentModule;
import org.eclipse.wst.common.modulecore.DeployScheme;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ModuleType;
import org.eclipse.wst.common.modulecore.ProjectModules;
import org.eclipse.wst.common.modulecore.WorkbenchApplication;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage
 * @generated
 */
public class ModuleCoreSwitch {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ModuleCorePackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleCoreSwitch() {
		if (modelPackage == null) {
			modelPackage = ModuleCorePackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public Object doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch((EClass)eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case ModuleCorePackage.DEPLOY_SCHEME: {
				DeployScheme deployScheme = (DeployScheme)theEObject;
				Object result = caseDeployScheme(deployScheme);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ModuleCorePackage.WORKBENCH_MODULE: {
				WorkbenchModule workbenchModule = (WorkbenchModule)theEObject;
				Object result = caseWorkbenchModule(workbenchModule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE: {
				WorkbenchModuleResource workbenchModuleResource = (WorkbenchModuleResource)theEObject;
				Object result = caseWorkbenchModuleResource(workbenchModuleResource);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ModuleCorePackage.WORKBENCH_APPLICATION: {
				WorkbenchApplication workbenchApplication = (WorkbenchApplication)theEObject;
				Object result = caseWorkbenchApplication(workbenchApplication);
				if (result == null) result = caseWorkbenchModule(workbenchApplication);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ModuleCorePackage.MODULE_TYPE: {
				ModuleType moduleType = (ModuleType)theEObject;
				Object result = caseModuleType(moduleType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ModuleCorePackage.PROJECT_MODULES: {
				ProjectModules projectModules = (ProjectModules)theEObject;
				Object result = caseProjectModules(projectModules);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ModuleCorePackage.DEPENDENT_MODULE: {
				DependentModule dependentModule = (DependentModule)theEObject;
				Object result = caseDependentModule(dependentModule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Deploy Scheme</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Deploy Scheme</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseDeployScheme(DeployScheme object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Workbench Module</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Workbench Module</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseWorkbenchModule(WorkbenchModule object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Workbench Module Resource</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Workbench Module Resource</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseWorkbenchModuleResource(WorkbenchModuleResource object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Workbench Application</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Workbench Application</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseWorkbenchApplication(WorkbenchApplication object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Module Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Module Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseModuleType(ModuleType object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Project Modules</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Project Modules</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseProjectModules(ProjectModules object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Dependent Module</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Dependent Module</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseDependentModule(DependentModule object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public Object defaultCase(EObject object) {
		return null;
	}

} //ModuleCoreSwitch
