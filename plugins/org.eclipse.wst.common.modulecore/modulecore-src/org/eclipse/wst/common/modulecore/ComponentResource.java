/**
 * <copyright></copyright>
 * 
 * $Id: ComponentResource.java,v 1.1 2005/03/15 00:43:55 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> Provides an abstract mapping of workbench resources to deployable
 * resources.
 * <p>
 * The underlying eclipse resource could be a container or a file. However, if the
 * {@see #getSourcePath()}&nbsp; is a container, then the {@see #getDeployedPath()}&nbsp; will be
 * a container as well.
 * </p>Clients that require access to the underlying Eclipse Resource for a given
 * ComponentResource should use 
 * {@see org.eclipse.wst.common.modulecore.ModuleCore#getEclipseResource(ComponentResource)}&nbsp;
 * <p>
 * See the package overview for an <a href="package-summary.html">overview of the model components </a>.
 * </p>
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>
 * {@link org.eclipse.wst.common.modulecore.ComponentResource#getSourcePath <em>Source Path</em>}
 * </li>
 * <li>
 * {@link org.eclipse.wst.common.modulecore.ComponentResource#getDeployedPath <em>Deployed Path</em>}
 * </li>
 * <li>
 * {@link org.eclipse.wst.common.modulecore.ComponentResource#getExclusions <em>Exclusions</em>}
 * </li>
 * <li>{@link org.eclipse.wst.common.modulecore.ComponentResource#getModule <em>Module</em>}
 * </li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModuleResource()
 * @model
 * @generated
 */
public interface ComponentResource extends EObject {
	/**
	 * Returns the value of the '<em><b>Source Path</b></em>' attribute. <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Source Path</em>' attribute isn't clear, there really should
	 * be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Source Path</em>' attribute.
	 * @see #setSourcePath(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModuleResource_SourcePath()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getSourcePath();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.wst.common.modulecore.ComponentResource#getSourcePath <em>Source Path</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Source Path</em>' attribute.
	 * @see #getSourcePath()
	 * @generated
	 */
	void setSourcePath(URI value);

	/**
	 * Returns the value of the '<em><b>Deployed Path</b></em>' attribute. <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Deployed Path</em>' attribute isn't clear, there really should
	 * be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Deployed Path</em>' attribute.
	 * @see #setDeployedPath(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModuleResource_DeployedPath()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getDeployedPath();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.wst.common.modulecore.ComponentResource#getDeployedPath <em>Deployed Path</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Deployed Path</em>' attribute.
	 * @see #getDeployedPath()
	 * @generated
	 */
	void setDeployedPath(URI value);

	/**
	 * Returns the value of the '<em><b>Exclusions</b></em>' attribute list. The list contents
	 * are of type {@link org.eclipse.emf.common.util.URI}. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exclusions</em>' attribute list isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Exclusions</em>' attribute list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModuleResource_Exclusions()
	 * @model type="org.eclipse.emf.common.util.URI"
	 *        dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	EList getExclusions();

	/**
	 * Returns the value of the '<em><b>Module</b></em>' container reference. It is
	 * bidirectional and its opposite is '
	 * {@link org.eclipse.wst.common.modulecore.WorkbenchComponent#getResources <em>Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Module</em>' container reference isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Module</em>' container reference.
	 * @see #setModule(WorkbenchComponent)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getWorkbenchModuleResource_Module()
	 * @see org.eclipse.wst.common.modulecore.WorkbenchComponent#getResources
	 * @model opposite="resources" required="true"
	 * @generated
	 */
	WorkbenchComponent getModule();

	/**
	 * Sets the value of the '
	 * {@link org.eclipse.wst.common.modulecore.ComponentResource#getModule <em>Module</em>}'
	 * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Module</em>' container reference.
	 * @see #getModule()
	 * @generated
	 */
	void setModule(WorkbenchComponent value);

} // ComponentResource
