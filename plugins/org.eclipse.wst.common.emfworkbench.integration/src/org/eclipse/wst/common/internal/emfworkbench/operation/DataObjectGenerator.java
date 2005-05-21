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
package org.eclipse.wst.common.internal.emfworkbench.operation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.importer.java.builder.JavaEcoreBuilder;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;

/**
 * @author delfinoj
 * 
 * Generates SDO DataObject classes.
 *  
 */
public class DataObjectGenerator extends WTPOperation {
	protected IProject project;
	protected List xmiPackages;
	protected List javaModels;
	protected XSDEcoreBuilder xsdECoreBuilder;
	protected List ePackages;

	/**
	 * @param operationDataModel
	 */
	public DataObjectGenerator(DataObjectGeneratorModel operationDataModel) {
		super(operationDataModel);
	}

	/**
	 * Set the nsURI of the <code>ePackage</code> to be in the form
	 * "java://{fullyQualifiedPackageInterfaceName}. When this format is used, EMF will attempt to
	 * register the static package by loading the interface and calling the eINSTANCE field.
	 * 
	 * @param ePackage
	 * @param fullyQualifiedPackageInterfaceName
	 */
	public static void setStaticPackageNsURI(EPackage ePackage, String fullyQualifiedPackageInterfaceName) {
		if (ePackage != null && fullyQualifiedPackageInterfaceName != null)
			ePackage.setNsURI("java://" + fullyQualifiedPackageInterfaceName);//$NON-NLS-1$
	}

	protected IProject getProject() {
		if (project == null)
			project = (IProject) operationDataModel.getProperty(DataObjectGeneratorModel.PROJECT);
		return project;
	}

	protected XSDEcoreBuilder getXSDEcoreBuilder() {
		if (xsdECoreBuilder == null)
			xsdECoreBuilder = new XSDEcoreBuilder();
		return xsdECoreBuilder;
	}

	/**
	 * @see com.ibm.etools.ctc.ant.task.util.BaseProjectBuilder#generate(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected boolean prepareGenerate() {
		// Collect all generated EPackages
		ePackages = new ArrayList();
		// Packages generated from XSDs
		if (xsdECoreBuilder != null)
			ePackages.addAll(xsdECoreBuilder.getTargetNamespaceToEPackageMap().values());
		// Packages loaded from XMI files
		if (xmiPackages != null)
			ePackages.addAll(xmiPackages);
		if (javaModels != null)
			prepareEPackagesFromJavaModels();
		if (ePackages.isEmpty())
			return false;
		return true;
	}

	protected void prepareEPackagesFromJavaModels() {
		// Generate packages from annotated java model files
		JavaEcoreBuilder javaEcoreBuilder = new JavaEcoreBuilder(getProject().getFile(new Path("sdo.genmodel"))) { //$NON-NLS-1$
			public void getAllGenModelFiles(Collection result, IFile file) throws CoreException {
				// Ignore .genmodel files on the buildpath
				return;
			}
		};
		//TODO - Ecore builder api has changed....
		//javaEcoreBuilder.run(new NullProgressMonitor(), false);
		IStatus localStatus = javaEcoreBuilder.getStatus();
		if (localStatus.getSeverity() != IStatus.ERROR) {
			List genPackages = javaEcoreBuilder.getGenModel().getGenPackages();
			processGenPackagesFromJavaModels(genPackages);
		} else
			logProblems(localStatus);
	}

	/**
	 * @param statusArg
	 */
	private void logProblems(IStatus statusArg) {
		Logger logger = Logger.getLogger();
		logger.logError("Problems detected generating SDO objects."); //$NON-NLS-1$
		log(statusArg, logger);
	}

	private void log(IStatus statusArg, Logger logger) {
		doLog(statusArg, logger);
		if (statusArg.isMultiStatus()) {
			MultiStatus mStatus = (MultiStatus) statusArg;
			IStatus[] children = mStatus.getChildren();
			for (int i = 0; i < children.length; i++) {
				log(children[i], logger);
			}
		}
	}

	private void doLog(IStatus statusArg, Logger logger) {
		switch (statusArg.getSeverity()) {
			case IStatus.ERROR :
				logger.logError(statusArg.getMessage());
				break;
			case IStatus.WARNING :
				logger.logWarning(statusArg.getMessage());
				break;
			case IStatus.INFO :
				logger.log(statusArg.getMessage());
				break;
		}
	}

	/**
	 * @param genPackages
	 */
	protected void processGenPackagesFromJavaModels(List genPackages) {
		if (genPackages.isEmpty())
			return;
		for (Iterator i = genPackages.iterator(); i.hasNext();) {
			GenPackage genPackage = (GenPackage) i.next();
			EPackage ePackage = genPackage.getEcorePackage();
			updateEPackageFromJavaModel(ePackage, genPackage);
			ePackages.add(ePackage);
		}

	}

	protected void updateEPackageFromJavaModel(EPackage ePackage, GenPackage genPackage) {
		// Fix the ePackage name
		ePackage.setName(ePackage.getNsPrefix());
		setStaticPackageNsURI(ePackage, genPackage);
	}

	/**
	 * Since we are generating static Packages we need to set the nsURI of each EPackage to be in
	 * the form "java://{fully qualified package interface name}. When this format is used, EMF will
	 * attempt to register the static package by loading the interface and calling the eINSTANCE
	 * field.
	 * 
	 * @param ePackage
	 * @param genPackage
	 */
	protected void setStaticPackageNsURI(EPackage ePackage, GenPackage genPackage) {
		String interfaceName = genPackage.getPackageInterfaceName();
		if (interfaceName != null) {
			StringBuffer b = new StringBuffer();
			b.append(genPackage.getPackageName()).append('.').append(interfaceName);
			setStaticPackageNsURI(ePackage, b.toString());
		}
	}

	/**
	 * @see com.ibm.etools.ctc.ant.task.util.BaseProjectBuilder#generate(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void generate(IProgressMonitor progressMonitor) throws CoreException {
		try {
			progressMonitor.beginTask(getProject().getFullPath().toString(), 100);
			// Create a GenModel
			GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
			initializeGenModel(genModel);
			setPackagesOnGenModel(genModel);
			// Validate the GenModel
			IStatus localStatus = genModel.validate();
			if (!localStatus.isOK())
				throw new CoreException(localStatus);
			doGenerate(progressMonitor, genModel);
			progressMonitor.worked(100);
		} finally {
			progressMonitor.done();
		}
	}

	/*
	 * Generate static code.
	 */
	protected void doGenerate(IProgressMonitor progressMonitor, GenModel genModel) {
		try {
			genModel.generate(new SubProgressMonitor(progressMonitor, 50));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/*
	 * Initialize the GenModel from the EPackages
	 */
	protected void setPackagesOnGenModel(GenModel genModel) {
		genModel.initialize(ePackages);
		for (Iterator i = genModel.getGenPackages().iterator(); i.hasNext();) {
			GenPackage genPackage = (GenPackage) i.next();
			genPackage.setLoadInitialization(false);
			String prefix = genPackage.getInterfacePackageName();
			prefix = prefix.substring(prefix.lastIndexOf('.') + 1);
			if (prefix.length() > 1)
				prefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1);
			else
				prefix = prefix.toUpperCase();
			genPackage.setPrefix(prefix);
		}
	}

	/**
	 * @param genModel
	 */
	protected void initializeGenModel(GenModel genModel) {
		setSDODefaults(genModel);

		genModel.setDynamicTemplates(operationDataModel.getBooleanProperty(DataObjectGeneratorModel.DYNAMIC_TEMPLATES));
		genModel.setForceOverwrite(operationDataModel.getBooleanProperty(DataObjectGeneratorModel.FORCE_OVERWRITE));
		genModel.setCanGenerate(operationDataModel.getBooleanProperty(DataObjectGeneratorModel.CAN_GENERATE));
		genModel.setUpdateClasspath(operationDataModel.getBooleanProperty(DataObjectGeneratorModel.UPDATE_CLASSPATH));
		genModel.setGenerateSchema(operationDataModel.getBooleanProperty(DataObjectGeneratorModel.GENERATE_SCHEMA));
		genModel.setNonNLSMarkers(operationDataModel.getBooleanProperty(DataObjectGeneratorModel.NON_NLS_MARKERS));

		IFolder modelDir = (IFolder) operationDataModel.getProperty(DataObjectGeneratorModel.MODEL_DIR);
		genModel.setModelDirectory(modelDir.getFullPath().toOSString());
	}

	/*
	 * Options from SetDefaultSDOOptions, recommended by Ed
	 */
	protected void setSDODefaults(GenModel genModel) {
		genModel.setRootExtendsInterface(""); //$NON-NLS-1$
		//TODO why would you expose this internal interface in the generated code, instead of
		// commonj.sdo.DataObject
		genModel.setRootImplementsInterface("org.eclipse.emf.ecore.sdo.InternalEDataObject"); //$NON-NLS-1$
		genModel.setRootExtendsClass("org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl"); //$NON-NLS-1$
		genModel.setFeatureMapWrapperInterface("commonj.sdo.Sequence"); //$NON-NLS-1$
		genModel.setFeatureMapWrapperInternalInterface("org.eclipse.emf.ecore.sdo.util.ESequence"); //$NON-NLS-1$
		genModel.setFeatureMapWrapperClass("org.eclipse.emf.ecore.sdo.util.BasicESequence"); //$NON-NLS-1$
		genModel.setSuppressEMFTypes(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		loadModelFiles(monitor);
		if (prepareGenerate())
			generate(monitor);
	}

	/**
	 * @param monitor
	 */
	protected void loadModelFiles(IProgressMonitor monitor) {
		loadXSDFiles(monitor);
		loadEcoreFiles(monitor);
		//loadWSDLFiles(monitor);
		loadJavaFiles(monitor);
	}

	/**
	 * Load an XSD and generate the eCore packages for each tracked XSD file.
	 * 
	 * @param monitor
	 */
	protected void loadXSDFiles(IProgressMonitor monitor) {
		if (operationDataModel.isSet(DataObjectGeneratorModel.XSD_FILES)) {
			List files = (List) operationDataModel.getProperty(DataObjectGeneratorModel.XSD_FILES);
			IFile file;
			for (int i = 0; i < files.size(); i++) {
				file = (IFile) files.get(i);
				Resource resource = WorkbenchResourceHelperBase.load(file);
				Collection xsdSchemas = EcoreUtil.getObjectsByType(resource.getContents(), XSDPackage.eINSTANCE.getXSDSchema());
				for (Iterator j = xsdSchemas.iterator(); j.hasNext();) {
					getXSDEcoreBuilder().generate((XSDSchema) j.next());
				}
			}
		}
	}

	/**
	 * Load an Ecore model file and generate the eCore packages for each tracked Ecore file.
	 * 
	 * @param monitor
	 */
	protected void loadEcoreFiles(IProgressMonitor monitor) {
		if (operationDataModel.isSet(DataObjectGeneratorModel.ECORE_FILES)) {
			List files = (List) operationDataModel.getProperty(DataObjectGeneratorModel.ECORE_FILES);
			IFile file;
			for (int i = 0; i < files.size(); i++) {
				file = (IFile) files.get(i);

				Resource resource = WorkbenchResourceHelperBase.getResource(file, true);
				addXmiPackages(resource.getContents());
			}
		}
	}


	protected void addXmiPackages(List packages) {
		if (xmiPackages == null)
			xmiPackages = new ArrayList();
		xmiPackages.addAll(packages);
	}

	/**
	 * Load the XSD files and generate the eCore packages for each tracked WSDL file.
	 * 
	 * @param monitor
	 */
	protected void loadWSDLFiles(IProgressMonitor monitor) {
		//		if (operationDataModel.isSet(DataObjectGeneratorModel.WSDL_FILES)) {
		//			List files = (List) operationDataModel.getProperty(DataObjectGeneratorModel.WSDL_FILES);
		//			IFile file;
		//			for (int i = 0; i < files.size(); i++) {
		//				file = (IFile) files.get(i);
		//				//TODO We can't handle WSDL in WSAD 5.1 as the WSDL model is not an EMF 2.0 model
		//				// Re-activate this function when we move to 6.0
		//				// // Load a WSDL file and collect all the references XSD schemas
		//				// Set xsdSchemas=new HashSet();
		//				// URI uri = URI.createFileURI(file.getLocation().toString());
		//				// Resource resource = WorkbenchResourceHelper.getResource(uri);
		//				// for (Iterator i=resource.getAllContents(); i.hasNext(); ) {
		//				// Object object=i.next();
		//				// if (object instanceof XSDSchemaExtensibilityElement) {
		//				// XSDSchemaExtensibilityElement
		//				// extensibilityElement=(XSDSchemaExtensibilityElement)object;
		//				// XSDSchema xsdSchema=extensibilityElement.getEXSDSchema();
		//				// if (xsdSchema!=null)
		//				// xsdSchemas.add(xsdSchema);
		//				// } else if (object instanceof Part) {
		//				// Part part=(Part)object;
		//				// XSDNamedComponent xsdComponent=part.getEXSDType();
		//				// if (xsdComponent==null)
		//				// xsdComponent=part.getEXSDElement();
		//				// if (xsdComponent!=null) {
		//				// XSDSchema xsdSchema=xsdComponent.getSchema();
		//				// if (xsdSchema!=null &&
		//				// !XSDUtil.isSchemaForSchemaNamespace(xsdSchema.getTargetNamespace())) {
		//				// xsdSchemas.add(xsdSchema);
		//				// }
		//				// }
		//				// }
		//				// }
		//				//
		//				// // Generate Ecore packages from XSDs
		//				// for (Iterator i=xsdSchemas.iterator(); i.hasNext(); )
		//				// xsdECoreBuilder.generate((XSDSchema)i.next());
		//				// }
		//			}
		//		}
	}

	/**
	 * Load an Ecore model file and generate the eCore packages for each tracked Ecore file.
	 * 
	 * @param monitor
	 */
	protected void loadJavaFiles(IProgressMonitor monitor) {
		javaModels = (List) operationDataModel.getProperty(DataObjectGeneratorModel.JAVA_FILES);
	}
}