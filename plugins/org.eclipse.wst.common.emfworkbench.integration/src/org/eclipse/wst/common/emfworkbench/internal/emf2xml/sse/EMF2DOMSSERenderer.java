package org.eclipse.wst.common.emfworkbench.internal.emf2xml.sse;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jem.util.emf.workbench.ProjectResourceSet;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.internal.emf.resource.EMF2DOMAdapter;
import org.eclipse.wst.common.internal.emf.resource.EMF2DOMRenderer;
import org.eclipse.wst.common.internal.emf.resource.TranslatorResource;
import org.eclipse.wst.common.internal.emf.utilities.DOMUtilities;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.internal.emfworkbench.integration.ResourceSetWorkbenchEditSynchronizer;
import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.internal.provisional.IModelLifecycleListener;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;

public class EMF2DOMSSERenderer extends EMF2DOMRenderer implements IModelStateListener, IModelLifecycleListener {

	/** The XML DOM model */
	protected IDOMModel xmlModel;

	/** Used internally; the unique id for the xml model */
	protected String xmlModelId;

	private IModelManager modelManager;

	protected Object aboutToChangeNode = null;

	protected boolean xmlModelReverted = false;

	protected boolean isBatchChanges = false;

	private boolean isSaving = false;

	public EMF2DOMSSERenderer() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.EMF2DOMRenderer#managesDOMAdapters()
	 */
	protected boolean managesDOMAdapters() {
		return false;
	}

	protected EMF2DOMAdapter createRootDOMAdapter() {
		return new EMF2DOMSSEAdapter(getResource(), document, this, getResource().getRootTranslator());
	}

	public void deRegisterAsModelStateListener() {
		if (xmlModel != null)
			xmlModel.removeModelStateListener(this);
	}

	public void deRegisterAsModelLifecycleListener() {
		if (xmlModel != null)
			xmlModel.removeModelLifecycleListener(this);
	}

	/**
	 * Return the DOM model for this resource.
	 */
	public IDOMModel getXMLModel() {
		return xmlModel;
	}

	public String getXMLModelId() {
		return xmlModelId;
	}

	public boolean isModified() {
		return (getXMLModel() != null && getXMLModel().isDirty());
	}

	public void modelAboutToBeChanged(IStructuredModel model) {
		if (model.getStructuredDocument() != null)
			aboutToChangeNode = model.getStructuredDocument().getFirstStructuredDocumentRegion();
	}

	public void modelChanged(IStructuredModel model) {
		if (isBatchChanges)
			return;
		try {
			if (aboutToChangeNode != null && model.getStructuredDocument() != null && model.getStructuredDocument().getFirstStructuredDocumentRegion() != aboutToChangeNode) {
				modelAccessForWrite();
				try {
					xmlModelReverted = true;
					resource.unload();
				} finally {
					if (getXMLModel() != null)
						getXMLModel().releaseFromEdit();
				}
			}
		} finally {
			aboutToChangeNode = null;
		}
	}

	public void accessForRead() {
		if (!resource.isNew()) {
			String id = getModelManagerId();
			getModelManager().getExistingModelForRead(id);
		}
	}

	public void accessForWrite() {
		modelAccessForWrite();
	}

	private void modelAccessForWrite() {
		String id = getModelManagerId();
		getModelManager().getExistingModelForEdit(id);
	}

	public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
		if (!isDirty && resource.isModified()) { //The XMLModel was saved
			resource.setModified(false);
			long stamp = WorkbenchResourceHelper.computeModificationStamp(resource);
			WorkbenchResourceHelper.setSynhronizationStamp(resource, stamp);
		}
	}

	public void modelResourceDeleted(IStructuredModel model) {
		//Do nothing
	}

	public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {
		//Do nothing
	}

	/**
	 * This method is called just prior to being removed from the ResourceSet. Ensure that all
	 * reference counts to the XMLModel are removed.
	 */
	public void preDelete() {
		if (resource.isLoaded())
			deregisterFromXMLModel();
	}

	public void preUnload() {
		deregisterFromXMLModel();
	}

	private void deregisterFromXMLModel() {
		deRegisterAsModelStateListener();
		deRegisterAsModelLifecycleListener();
		// This try/catch block is a hack to fix defect 204114. This occurs
		// because
		// the model manager plugin is shut down and unloaded before the j2ee
		// plugin.
		// Calling getModelManager() can result in a class cast exception that
		// should
		// be ignored.
		//ModelManager mgr = null;
		try {
			getModelManager();
		} catch (ClassCastException exc) {
			return;
		}
		if (xmlModel != null) {
			int writeCount = resource.getWriteCount();
			int readCount = resource.getReadCount();
			for (int i = 0; i < writeCount; i++)
				xmlModel.releaseFromEdit();
			for (int ii = 0; ii < readCount; ii++)
				xmlModel.releaseFromRead();
		}
		EMF2DOMAdapter adapter = (EMF2DOMAdapter) EcoreUtil.getAdapter(resource.eAdapters(), EMF2DOMAdapter.ADAPTER_CLASS);
		if (adapter != null) {
			adapter.removeAdapters(adapter.getNode());
		}
		xmlModel = null;
		xmlModelId = null;
	}

	/**
	 * Insert the method's description here. Creation date: (9/7/2001 10:49:53 AM)
	 */
	public void registerAsModelStateListener() {
		this.xmlModel.addModelStateListener(this);
	}

	public void registerAsModelLifecycleListener() {
		this.xmlModel.addModelLifecycleListener(this);
	}

	/**
	 * Return the DOM model for this resource.
	 */
	public void setXMLModel(IDOMModel xmlModel) {
		deRegisterAsModelStateListener();
		deRegisterAsModelLifecycleListener();
		this.xmlModel = xmlModel;
		registerAsModelStateListener();
		registerAsModelLifecycleListener();
	}

	public void setXMLModelId(String id) {
		xmlModelId = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#doSave(java.io.OutputStream, java.util.Map)
	 */
	public void doSave(OutputStream outputStream, Map options) throws IOException {

		try {
			isSaving = true;
			if (null != outputStream) {
				throw new RuntimeException(UIResourceHandler.getString("EMF2DOMSedRenderer_UI_0", new Object[]{this.getClass().getName()}));} //$NON-NLS-1$
			createDOMTreeIfNecessary();
			ResourceSetWorkbenchEditSynchronizer synchronizer = (ResourceSetWorkbenchEditSynchronizer) ((ProjectResourceSet) resource.getResourceSet()).getSynchronizer();
			IFile aFile = WorkbenchResourceHelper.getFile(resource);
			try {
				synchronizer.preSave(aFile);
				xmlModel.save(aFile);
			} catch (CoreException ex) {
				synchronizer.removeFromRecentlySavedList(aFile);
				Logger.getLogger().logError(ex);
			}
			cacheSynchronizationStamp();
		} finally {
			isSaving = false;
		}
	}

	/**
	 * @see com.ibm.etools.common.mof2dom.XMLDOMResource#wasReverted()
	 */
	public boolean wasReverted() {
		return xmlModelReverted;
	}

	protected IModelManager getModelManager() {
		if (modelManager == null)
			modelManager = StructuredModelManager.getModelManager();
		return modelManager;
	}

	/**
	 * Return id used to key the XML resource in the XML ModelManager.
	 */
	protected String getModelManagerId() {
		if (xmlModelId == null) {
			IFile file = WorkbenchResourceHelper.getFile(getResource());
			if (file != null) {
				xmlModelId = getModelManager().calculateId(file);
			} else {
				xmlModelId = resource.getURI() + Long.toString(System.currentTimeMillis());
			}
		}
		return xmlModelId;
	}

	public void releaseFromRead() {
		if (xmlModel != null)
			xmlModel.releaseFromRead();
	}

	public void releaseFromWrite() {
		if (xmlModel != null)
			xmlModel.releaseFromEdit();
	}

	protected ResourceSet getResourceSet() {
		return resource == null ? null : resource.getResourceSet();
	}

	public boolean isShared() {
		if (getResourceSet() == null || xmlModel == null)
			return false;
		return xmlModel.isShared();
	}

	public boolean isSharedForWrite() {
		if (getResourceSet() == null || xmlModel == null)
			return false;
		return xmlModel.isSharedForEdit();
	}

	/**
	 * Create a new Document given
	 * 
	 * @aResource.
	 */
	protected void createDocument() {
		TranslatorResource res = getResource();
		res.setDefaults();
		IFile file = WorkbenchResourceHelper.getFile(resource);
		InputStream is = DOMUtilities.createHeaderInputStream(res.getDoctype(), res.getPublicId(), res.getSystemId());
		if (is == null)
			return;
		try {
			try {
				List folders = new ArrayList();
				IContainer container = file.getParent();
				while (null != container && !container.exists() && container instanceof IFolder) {
					folders.add(container);
					container = container.getParent();
				}
				IFolder folder = null;
				for (int i = 0; i < folders.size(); i++) {
					folder = (IFolder) folders.get(i);
					folder.create(true, true, null);
				}
				file.create(is, true, null);
				file.setLocal(true, 1, null);
			} catch (CoreException e1) {
				org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError(e1);
			} finally {
				if (null != is) {
					is.close();
				}
			}
			initializeXMLModel(file, true);
		} catch (java.io.IOException ex) {
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError(UIResourceHandler.getString("Unexpected_IO_exception_occurred_creating_xml_document_1_EXC_"));//$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError(ex);
		}
	}

	protected void createDOMTreeIfNecessary() {
		if (needsToCreateDOM)
			createDOMTree();
	}

	private IDOMModel initializeXMLModel(IFile file, boolean forWrite) throws UnsupportedEncodingException, IOException {
		if (file == null || !file.exists())
			throw new FileNotFoundException((file == null) ? "null" : file.getFullPath().toOSString()); //$NON-NLS-1$
		try {
			if (forWrite) {
				setXMLModel((IDOMModel) getModelManager().getModelForEdit(file));
			}
			else {
				setXMLModel((IDOMModel) getModelManager().getModelForRead(file));
			}
			setXMLModelId(getXMLModel().getId());
			needsToCreateDOM = false;
		} catch (CoreException e) {
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError(e);
			return null;
		}
		String id = getModelManager().calculateId(file);
		syncReferenceCounts(id, forWrite);
		if (xmlModel != null)
			document = xmlModel.getDocument();
		return xmlModel;
	}

	private void syncReferenceCounts(String id, boolean forWrite) {
		int editIndex = 0, readIndex = 0;
		if (forWrite)
			editIndex++;
		else
			readIndex++;
		int writeCount = resource.getWriteCount();
		int readCount = resource.getReadCount();
		for (int i = writeCount; i > editIndex; i--)
			modelManager.getExistingModelForEdit(id);
		for (int i = readCount; i > readIndex; i--)
			modelManager.getExistingModelForRead(id);
	}

	protected void loadDocument(InputStream in, Map options) throws IOException {
		if (null != in) {
			throw new RuntimeException(UIResourceHandler.getString("EMF2DOMSedRenderer_UI_1", new Object[]{this.getClass().getName()}));} //$NON-NLS-1$
		IFile file = WorkbenchResourceHelper.getFile(resource);
		initializeXMLModel(file, (resource.getWriteCount() != 0));
		cacheSynchronizationStamp();
	}

	private void cacheSynchronizationStamp() {
		IFile file = WorkbenchResourceHelper.getFile(resource);
		if (file != null) {
			if (xmlModel != null)
				xmlModel.resetSynchronizationStamp(file);
		}
	}

	public void setBatchMode(boolean isBatch) {
		if (isBatch) {
			isBatchChanges = true;
			getXMLModel().aboutToChangeModel();
		} else {
			setRootNodeAdapterNotificationEnabled(false);
			try {
				getXMLModel().aboutToChangeModel();
			} finally {
				getXMLModel().changedModel();
				setRootNodeAdapterNotificationEnabled(true);
			}
			isBatchChanges = false;
		}
	}
	public boolean isBatchMode() {
		return isBatchChanges;
	}

	private void setRootNodeAdapterNotificationEnabled(boolean b) {
		EObject root = resource.getRootObject();
		if (root != null) {
			EMF2DOMAdapter adapter = (EMF2DOMAdapter) EcoreUtil.getExistingAdapter(root, EMF2DOMAdapter.ADAPTER_CLASS);
			if (adapter != null)
				adapter.setNotificationEnabled(b);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.EMF2DOMRenderer#getExistingDOMAdapter(org.w3c.dom.Node)
	 */
	public EMF2DOMAdapter getExistingDOMAdapter(Node node) {
		IDOMNode xNode = (IDOMNode) node;
		return (EMF2DOMSSEAdapter) xNode.getAdapterFor(EMF2DOMAdapter.ADAPTER_CLASS);
	}

	public void removeDOMAdapter(Node aNode, EMF2DOMAdapter anAdapter) {
		((IDOMNode) aNode).removeAdapter((EMF2DOMSSEAdapter) anAdapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.internal.util.emf.xml.EMF2DOMRenderer#replaceDocumentType(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void replaceDocumentType(String docTypeName, String publicId, String systemId) {
		if (document == null)
			return;
		DocumentTypeImpl docType = (DocumentTypeImpl) document.getDoctype();
		if (docType == null)
			return;
		if (publicId == null && systemId == null)
			document.removeChild(docType);
		else {
			docType.setPublicId(publicId);
			docType.setSystemId(systemId);
		}
	}

	public boolean useStreamsForIO() {
		return false;
	}

	public void processPostModelEvent(ModelLifecycleEvent event) {
		// TODO Auto-generated method stub

	}

	public void processPreModelEvent(ModelLifecycleEvent event) {
		if (!isSaving) {
			if (event.getType() == ModelLifecycleEvent.MODEL_SAVED) {
				ResourceSetWorkbenchEditSynchronizer synchronizer = (ResourceSetWorkbenchEditSynchronizer) ((ProjectResourceSet) resource.getResourceSet()).getSynchronizer();
				IFile aFile = WorkbenchResourceHelper.getFile(resource);
				synchronizer.preSave(aFile);
			}
		}
	}

	public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
		// TODO Auto-generated method stub
		
	}

	public void modelReinitialized(IStructuredModel structuredModel) {
		// TODO Auto-generated method stub
		
	}
}