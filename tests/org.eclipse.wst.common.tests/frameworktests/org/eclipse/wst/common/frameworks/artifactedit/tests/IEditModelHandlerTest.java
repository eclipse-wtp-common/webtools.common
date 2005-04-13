package org.eclipse.wst.common.frameworks.artifactedit.tests;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.IEditModelHandler;

import junit.framework.TestCase;

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
		new EditModelHandlerTest().save(null);
	}

	public void testSaveIfNecessary() {
		new EditModelHandlerTest().saveIfNecessary(null);
	}

	public void testDispose() {
		new EditModelHandlerTest().dispose();
	}

}
