/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.operation;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.codegen.CodeGen.StreamProgressMonitor;
import org.eclipse.emf.codegen.ecore.Generator;
import org.eclipse.wst.common.framework.operation.WTPOperation;
import org.eclipse.wst.common.framework.operation.WTPOperationDataModel;

import sun.tools.jar.Main;

import com.ibm.wtp.emf.workbench.plugin.EMFWorkbenchPlugin;

/**
 * @author DABERG
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public class DataObjectGeneratorModel extends WTPOperationDataModel implements IPlatformRunnable {
	/**
	 * The project that you want this generator to operate upon. (Required)
	 * 
	 * @link org.eclipse.core.resources.IProject
	 */
	public static final String PROJECT = "DataObjectGeneratorModel.project"; //$NON-NLS-1$
	/**
	 * This is a list of XSD Files that need to be processed. (Optional)
	 * 
	 * @link List
	 * 
	 * @see IFile
	 */
	public static final String XSD_FILES = "DataObjectGeneratorModel.xsdFiles"; //$NON-NLS-1$
	/**
	 * This is a list of ECORE Files that need to be processed. (Optional)
	 * 
	 * @link List
	 * 
	 * @see IFile
	 */
	public static final String ECORE_FILES = "DataObjectGeneratorModel.ecoreFiles"; //$NON-NLS-1$
	/**
	 * This is a list of WSDL Files that need to be processed. (Optional)
	 * 
	 * @link List
	 * 
	 * @see IFile
	 */
	public static final String WSDL_FILES = "DataObjectGeneratorModel.wsdlFiles"; //$NON-NLS-1$
	/**
	 * This is a list of Java model Files that need to be processed. (Optional)
	 * 
	 * @link List
	 * 
	 * @see IFile
	 */
	public static final String JAVA_FILES = "DataObjectGeneratorModel.javaFiles"; //$NON-NLS-1$
	/**
	 * This is the folder within the project that you want to have the generated output for the data
	 * objects to be saved. (Optional - defaults to {project}/gen/src/)
	 * 
	 * @link org.eclipse.core.resources.IFolder
	 */
	public static final String MODEL_DIR = "DataObjectGeneratorModel.modelDirectory"; //$NON-NLS-1$
	/**
	 * GenModel Dynamic Templates option. (Optional - defaults to false)
	 * 
	 * @link Boolean
	 */
	public static final String DYNAMIC_TEMPLATES = "DataObjectGeneratorModel.dynamicTemplates"; //$NON-NLS-1$
	/**
	 * GenModel Force Overwrite option. (Optional - defaults to true)
	 * 
	 * @link Boolean
	 */
	public static final String FORCE_OVERWRITE = "DataObjectGeneratorModel.forceOverwrite"; //$NON-NLS-1$
	/**
	 * GenModel CanGenerate option. (Optional - defaults to true)
	 * 
	 * @link Boolean
	 */
	public static final String CAN_GENERATE = "DataObjectGeneratorModel.canGenerate"; //$NON-NLS-1$
	/**
	 * GenModel Update Classpath option. (Optional - defaults to false)
	 * 
	 * @link Boolean
	 */
	public static final String UPDATE_CLASSPATH = "DataObjectGeneratorModel.updateClasspath"; //$NON-NLS-1$
	/**
	 * GenModel Generate Schema option. (Optional - defaults to false)
	 * 
	 * @link Boolean
	 */
	public static final String GENERATE_SCHEMA = "DataObjectGeneratorModel.generateSchema"; //$NON-NLS-1$
	/**
	 * GenModel NON NLS Markers option. (Optional - defaults to false)
	 * 
	 * @link Boolean
	 */
	public static final String NON_NLS_MARKERS = "DataObjectGeneratorModel.nonNLSMarkers"; //$NON-NLS-1$


	public static void main(String args[]) {
		new DataObjectGeneratorModel().run(args);
	}

	public Object run(Object object) {
		try {

			final String[] arguments = (String[]) object;
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor progressMonitor) throws CoreException {
					try {
						boolean keepGenerated = false;
						boolean verbose = false;

						String ecoreFileName = null;
						String outputJar = null;
						if (arguments.length == 0) {
							printUsage();
							return;
						}
						for (int i = 0; i < arguments.length; i++) {
							if (arguments[i].equalsIgnoreCase("-keepgenerated")) { //$NON-NLS-1$
								keepGenerated = true;
							} else if (arguments[i].equalsIgnoreCase("-ecore")) { //$NON-NLS-1$
								ecoreFileName = arguments[++i];
							} else if (arguments[i].equalsIgnoreCase("-output")) { //$NON-NLS-1$
								outputJar = arguments[++i];
							} else if (arguments[i].equalsIgnoreCase("-verbose")) { //$NON-NLS-1$
								verbose = true;
							}
						}

						if ((ecoreFileName == null) || (outputJar == null)) {
							printUsage();
							return;
						}

						if (!verbose)
							progressMonitor = new NullProgressMonitor();

						DataObjectGeneratorModel g = new DataObjectGeneratorModel();

						IProject proj = workspace.getRoot().getProject("TargetProject"); //$NON-NLS-1$
						IFolder srcFolder = proj.getFolder("src"); //$NON-NLS-1$
						g.setProperty(DataObjectGeneratorModel.MODEL_DIR, srcFolder);

						if (!proj.exists()) {
							IPath location = proj.getLocation();
							proj = Generator.createEMFProject(srcFolder.getFullPath(), //new
										// Path("/TargetProject/src"),
										location, Collections.EMPTY_LIST, progressMonitor, Generator.EMF_PLUGIN_PROJECT_STYLE);

						}
						g.setProperty(DataObjectGeneratorModel.PROJECT, proj);

						File ecoreFile = new File(ecoreFileName);

						IFile file = proj.getFile("datagraph.ecore"); //$NON-NLS-1$
						if (file.exists()) {
							file.delete(true, progressMonitor);
						}

						file.create(new FileInputStream(ecoreFile), true, progressMonitor);

						g.addEcoreFile(file);
						WTPOperation op = g.getDefaultOperation();
						op.run(progressMonitor);

						// Build the Project

						proj.build(IncrementalProjectBuilder.FULL_BUILD, progressMonitor);

						// Jar up the files
						sun.tools.jar.Main jartool = new Main(System.out, System.err, "jar"); //$NON-NLS-1$
						String args[] = new String[5];
						args[0] = "cf"; //$NON-NLS-1$
						args[1] = outputJar;
						args[2] = "-C"; //$NON-NLS-1$
						args[3] = proj.getFolder("runtime").getLocation().toString(); //$NON-NLS-1$
						args[4] = "."; //$NON-NLS-1$
						jartool.run(args);

						if (keepGenerated) {
							// Add java files
							jartool = new Main(System.out, System.err, "jar"); //$NON-NLS-1$
							args[0] = "uf"; //$NON-NLS-1$
							args[3] = proj.getFolder("src").getLocation().toString(); //$NON-NLS-1$
							jartool.run(args);
						}

						// Delete the old project
						proj.delete(true, true, progressMonitor);

					} catch (Exception exception) {
						exception.printStackTrace();
						throw new CoreException(new Status(IStatus.ERROR, EMFWorkbenchPlugin.ID, 0, "EMF Workbench Error", exception)); //$NON-NLS-1$
					} finally {
						progressMonitor.done();
					}
				}
			};
			workspace.run(runnable, new StreamProgressMonitor(System.out));

			return new Integer(0);
		} catch (Exception exception) {
			exception.printStackTrace();
			return new Integer(1);
		}
	}

	public void printUsage() throws Exception {
		System.out.println("Usage arguments:"); //$NON-NLS-1$
		System.out.println("  -ecore <ecore-file>"); //$NON-NLS-1$
		System.out.println("  -output <jar file name>"); //$NON-NLS-1$
		System.out.println("  [-keepGenerated]  [-verbose]"); //$NON-NLS-1$

	}

	/**
	 *  
	 */
	public DataObjectGeneratorModel() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.framework.operation.WTPOperationDataModel#getDefaultOperation()
	 */
	public WTPOperation getDefaultOperation() {
		return new DataObjectGenerator(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.framework.operation.WTPOperationDataModel#initValidBaseProperties()
	 */
	protected void initValidBaseProperties() {
		addValidBaseProperty(PROJECT);
		addValidBaseProperty(XSD_FILES);
		addValidBaseProperty(ECORE_FILES);
		addValidBaseProperty(WSDL_FILES);
		addValidBaseProperty(JAVA_FILES);
		addValidBaseProperty(MODEL_DIR);
		addValidBaseProperty(DYNAMIC_TEMPLATES);
		addValidBaseProperty(FORCE_OVERWRITE);
		addValidBaseProperty(CAN_GENERATE);
		addValidBaseProperty(UPDATE_CLASSPATH);
		addValidBaseProperty(GENERATE_SCHEMA);
		addValidBaseProperty(NON_NLS_MARKERS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.framework.operation.WTPOperationDataModel#getDefaultProperty(java.lang.String)
	 */
	protected Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(MODEL_DIR))
			return getDefaultModelDirectory();
		if (propertyName.equals(FORCE_OVERWRITE) || propertyName.equals(CAN_GENERATE))
			return Boolean.TRUE;
		if (propertyName.equals(XSD_FILES) || propertyName.equals(ECORE_FILES) || propertyName.equals(WSDL_FILES) || propertyName.equals(JAVA_FILES))
			return Collections.EMPTY_LIST;
		return super.getDefaultProperty(propertyName);
	}

	/**
	 * @return
	 */
	protected Object getDefaultModelDirectory() {
		IProject project = (IProject) getProperty(PROJECT);
		if (project == null)
			return null;
		return project.getFolder(new Path("gen/src")); //$NON-NLS-1$
	}

	private void addFile(String propertyName, IFile file) {
		if (file != null) {
			List files;
			if (!isSet(propertyName)) {
				files = new ArrayList();
				setProperty(propertyName, files);
			} else
				files = (List) getProperty(propertyName);
			files.add(file);
		}
	}

	public void addXSDFile(IFile file) {
		addFile(XSD_FILES, file);
	}

	public void addEcoreFile(IFile file) {
		addFile(ECORE_FILES, file);
	}

	public void addWSDLFile(IFile file) {
		addFile(WSDL_FILES, file);
	}

	public void addJavaFile(IFile file) {
		addFile(JAVA_FILES, file);
	}
}