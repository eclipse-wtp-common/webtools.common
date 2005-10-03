package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageGroup;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageGroupHandler;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.IDMExtendedPageHandler;
import org.eclipse.wst.common.frameworks.internal.operation.extensionui.DMWizardPageElement;

public class DMPageGroupElementImpl implements IDMExtendedPageGroup {
	private DMWizardPageElement pageElement;

	public DMPageGroupElementImpl(IConfigurationElement element) {
		pageElement = new DMWizardPageElement(element);
	}

	public boolean getAllowsExtendedPages() {
		return pageElement.allowsExtendedPagesAfter();
	}

	public String getRequiredDataOperationToRun() {
		return pageElement.getRequiresDataOperationId();
	}

	public String getDataModelID() {
		return pageElement.getDataModelID();
	}

	public IDMExtendedPageGroupHandler getExtendedPageGroupHandler(IDataModel dataModel) {
		return pageElement.createPageGroupHandler(dataModel);
	}

	public DataModelWizardPage[] getExtendedPages(IDataModel dataModel) {
		return pageElement.createPageGroup(dataModel);
	}

	public IDMExtendedPageHandler getExtendedPageHandler(IDataModel dataModel) {
		return pageElement.createPageHandler(dataModel);
	}

	public String getPageGroupID() {
		return pageElement.getPageID();
	}

	public String getPageGroupInsertionID() {
		return pageElement.getPageInsertionID();
	}

	public String getWizardID() {
		return pageElement.getWizardID();
	}

}
