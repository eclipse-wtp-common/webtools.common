/**
 * <copyright></copyright>
 * 
 * $Id: ComponentResource.java,v 1.2 2005/04/05 03:35:37 cbridgha Exp $
 */
package org.eclipse.wst.common.componentcore.internal;

import org.eclipse.core.runtime.IPath;

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
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getSourcePath <em>Source Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getRuntimePath <em>Runtime Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getExclusions <em>Exclusions</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getComponent <em>Component</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getResourceType <em>Resource Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentResource()
 * @model
 * @generated
 */
public interface ComponentResource extends EObject{
	/**
	 * Returns the value of the '<em><b>Source Path</b></em>' attribute.
	 * <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Source Path</em>' attribute isn't clear, there really should
	 * be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source Path</em>' attribute.
	 * @see #setSourcePath(IPath)
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentResource_SourcePath()
	 * @model dataType="org.eclipse.wst.common.componentcore.internal.IPath" required="true"
	 * @generated
	 */
	IPath getSourcePath();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getSourcePath <em>Source Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source Path</em>' attribute.
	 * @see #getSourcePath()
	 * @generated
	 */
	void setSourcePath(IPath value);

	/**
	 * Returns the value of the '<em><b>Runtime Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Runtime Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Runtime Path</em>' attribute.
	 * @see #setRuntimePath(IPath)
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentResource_RuntimePath()
	 * @model dataType="org.eclipse.wst.common.componentcore.internal.IPath" required="true"
	 * @generated
	 */
	IPath getRuntimePath();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getRuntimePath <em>Runtime Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Runtime Path</em>' attribute.
	 * @see #getRuntimePath()
	 * @generated
	 */
	void setRuntimePath(IPath value);

	/**
	 * Returns the value of the '<em><b>Exclusions</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exclusions</em>' attribute list isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclusions</em>' attribute list.
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentResource_Exclusions()
	 * @model type="java.lang.String" default=""
	 * @generated
	 */
	EList getExclusions();

	/**
	 * Returns the value of the '<em><b>Component</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getResources <em>Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Component</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Component</em>' container reference.
	 * @see #setComponent(WorkbenchComponent)
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentResource_Component()
	 * @see org.eclipse.wst.common.componentcore.internal.WorkbenchComponent#getResources
	 * @model opposite="resources" required="true"
	 * @generated
	 */
	WorkbenchComponent getComponent();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getComponent <em>Component</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Component</em>' container reference.
	 * @see #getComponent()
	 * @generated
	 */
	void setComponent(WorkbenchComponent value);

	/**
	 * Returns the value of the '<em><b>Resource Type</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resource Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resource Type</em>' attribute.
	 * @see #setResourceType(String)
	 * @see org.eclipse.wst.common.componentcore.internal.ComponentcorePackage#getComponentResource_ResourceType()
	 * @model default="" required="true"
	 * @generated
	 */
	String getResourceType();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getResourceType <em>Resource Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Resource Type</em>' attribute.
	 * @see #getResourceType()
	 * @generated
	 */
	void setResourceType(String value);

} // ComponentResource
