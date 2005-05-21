package org.eclipse.wst.common.frameworks.componentcore.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.IEditModelHandler;

public class IEditModelHandlerTest extends TestCase {
	public class EditModelHandlerTest implements IEditModelHandler {
		public EditModelHandlerTest() {
		}

		public void save(IProgressMonitor aMonitor) {
		}

		public void saveIfNecessary(IProgressMonitor aMonitor) {
		}

		public void dispose() {
		}
	}

	public void testSave() {
		IEditModelHandler handler = new EditModelHandlerTest();
		handler.save(null);
	}

	public void testSaveIfNecessary() {
		IEditModelHandler handler = new EditModelHandlerTest();
		handler.saveIfNecessary(null);
		
	}

	public void testDispose() {
		IEditModelHandler handler = new EditModelHandlerTest();
		handler.dispose();
	}

}
