package org.eclipse.wst.common.modulecore.util;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.wst.common.internal.emf.resource.GenericTranslator;
import org.eclipse.wst.common.internal.emf.resource.IDTranslator;
import org.eclipse.wst.common.internal.emf.resource.RootTranslator;
import org.eclipse.wst.common.internal.emf.resource.Translator;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;


public class WTPModulesTranslator extends RootTranslator implements WTPModulesXmlMapperI{
	public static WTPModulesTranslator INSTANCE = new WTPModulesTranslator();
	private static Translator[] children;
	private static final ModuleCorePackage MODULE_CORE_PKG = ModuleCorePackage.eINSTANCE;
	/**
	 * @param domNameAndPath
	 * @param eClass
	 */
	public WTPModulesTranslator() {
		super(PROJECT_MODULES, ModuleCorePackage.eINSTANCE.getProjectModules());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emf.resource.Translator#getChildren(java.lang.Object, int)
	 */
	public Translator[] getChildren(Object target, int versionID) {
		return createWTPModulesTranslator();
	}
	
	private static Translator[] createWTPModulesTranslator() {
		return new Translator[] {
				IDTranslator.INSTANCE,
				createWBAppTranslator(MODULE_CORE_PKG.getProjectModules_WorkbenchApplications()),
				createWBModuleTranslator(MODULE_CORE_PKG.getProjectModules_WorkbenchModules()),
				createDeploySchemeTranslator(MODULE_CORE_PKG.getProjectModules_DeploymentSchemes())
		};
	}

	/**
	 * @return
	 */
	private static Translator createWBAppTranslator(EStructuralFeature afeature) {
		GenericTranslator result = new GenericTranslator(WBAPP, afeature);
		result.setChildren(new Translator[] {
			IDTranslator.INSTANCE,
			new Translator(DEPLOY_SCHEME, MODULE_CORE_PKG.getWorkbenchApplication_DeployScheme()),
			createModuleTypeTranslator(MODULE_CORE_PKG.getWorkbenchModule_ModuleType()),
			createWBResource(MODULE_CORE_PKG.getWorkbenchModule_Resources()),
			new Translator(MODULES, MODULE_CORE_PKG.getWorkbenchModule_Modules())
		});
		return result;
	}
	/**
	 * @return
	 */
	private static Translator createWBModuleTranslator(EStructuralFeature afeature) {
		GenericTranslator result = new GenericTranslator(WBMODULE, afeature);
		result.setChildren(new Translator[] {
			IDTranslator.INSTANCE,
			new Translator(HANDLE, MODULE_CORE_PKG.getWorkbenchModule_Handle(), DOM_ATTRIBUTE),
			createModuleTypeTranslator(MODULE_CORE_PKG.getWorkbenchModule_ModuleType()),
			createWBResource(MODULE_CORE_PKG.getWorkbenchModule_Resources()),
			createDependentModuleTranslator(MODULE_CORE_PKG.getWorkbenchModule_Modules())
		});
		return result;
	}
	/**
	 * @return
	 */
	private static Translator createDeploySchemeTranslator(EStructuralFeature afeature) {
		GenericTranslator result = new GenericTranslator(DEPLOY_SCHEME, afeature);
		result.setChildren(new Translator[] {
			IDTranslator.INSTANCE,
			new Translator(TYPE, MODULE_CORE_PKG.getDeployScheme_Type()),
			new Translator(SERVER_TARGET, MODULE_CORE_PKG.getDeployScheme_ServerTarget())
		});
		return result;
	}
	
	private static Translator createModuleTypeTranslator(EStructuralFeature afeature) {
		GenericTranslator result = new GenericTranslator(WBMODULE, afeature);
		result.setChildren(new Translator[] {
			IDTranslator.INSTANCE,			 
			new Translator(MODULE_TYPE_ID, MODULE_CORE_PKG.getModuleType_ModuleTypeId(), DOM_ATTRIBUTE),
			new Translator(META_RESOURCES, MODULE_CORE_PKG.getModuleType_MetadataResources())
		});
		return result;
	}
	
	private static Translator createDependentModuleTranslator(EStructuralFeature afeature) {
		GenericTranslator result = new GenericTranslator(DEPENDENTMODULE, afeature);
		result.setChildren(new Translator[] {
			IDTranslator.INSTANCE,			 
			new Translator(DEPLOY_PATH, MODULE_CORE_PKG.getDependentModule_DeployedPath(), DOM_ATTRIBUTE),
			new Translator(HANDLE, MODULE_CORE_PKG.getDependentModule_Handle(), DOM_ATTRIBUTE)
		});
		return result;
	}

	private static Translator createWBResource(EStructuralFeature afeature) {
		GenericTranslator result = new GenericTranslator(WBRESOURCE, afeature);
		result.setChildren(new Translator[] {
			IDTranslator.INSTANCE,
			new Translator(SOURCE_PATH, MODULE_CORE_PKG.getWorkbenchModuleResource_SourcePath(), DOM_ATTRIBUTE),
			new Translator(DEPLOY_PATH, MODULE_CORE_PKG.getWorkbenchModuleResource_DeployedPath(), DOM_ATTRIBUTE),
			new Translator(EXCLUSIONS, MODULE_CORE_PKG.getWorkbenchModuleResource_Exclusions())
		});
		return result;
	}

}
