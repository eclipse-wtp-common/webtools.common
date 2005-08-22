/*
 * Created on Mar 6, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.tests.collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.TestSuite;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author jsholl
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestCollectorGUI extends Composite implements ModifyListener {

	private static final String PLUGIN_ID = "org.eclipse.wst.common.tests.collector"; //$NON-NLS-1$
	private static final String SUITES_EXT_PT = "suites"; //$NON-NLS-1$
//	private static final String NAME = "name"; //$NON-NLS-1$
//	private static final String CLASS = "class"; //$NON-NLS-1$

	private Composite innerPanes = null;
	private Combo combo = null;

	private Hashtable testSuites = new Hashtable();

	/**
	 * @param parent
	 * @param style
	 */
	public TestCollectorGUI(Composite parent, int style) {
		super(parent, style);

		loadConfiguration();

		createPartControl();
	}

	private void loadConfiguration() {
		IExtensionPoint suiteExtPt = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, SUITES_EXT_PT);
		IExtension[] suitesExtensions = suiteExtPt.getExtensions();

		for (int i = 0; i < suitesExtensions.length; i++) {
			IExtension extension = suitesExtensions[i];
			IConfigurationElement[] tests = extension.getConfigurationElements();
			for (int j = 0; j < tests.length; j++) {
				try {
					IConfigurationElement element = tests[j];
					String suiteName = element.getAttribute("name"); //$NON-NLS-1$
					testSuites.put(suiteName, element);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void createPartControl() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		setLayout(gridLayout);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;

		combo = new Combo(this, SWT.READ_ONLY);
		Enumeration keys = testSuites.keys();
		ArrayList arrayList = new ArrayList();
		while (keys.hasMoreElements()) {
			arrayList.add(keys.nextElement());
		}

		Collections.sort(arrayList, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((String) o1).compareTo(((String) o2));
			}
		});

		for (int i = 0; i < arrayList.size(); i++) {
			combo.add((String) arrayList.get(i));
		}
		combo.setLayoutData(gridData);
		combo.addModifyListener(this);
		if (combo.getItemCount() > 0) {
			combo.select(0);
		}
	}

	public void modifyText(ModifyEvent e) {
		if (e.getSource() == combo) {
			updateCombo(e);
		}
	}

	private void updateCombo(ModifyEvent e) {
		if (null != innerPanes) {
			innerPanes.dispose();
		}
		try {
			String testName = combo.getText();
			IConfigurationElement element = (IConfigurationElement) testSuites.get(testName);
			TestSuite suite = (TestSuite) element.createExecutableExtension("class"); //$NON-NLS-1$
			innerPanes = new TestCollectorInnerPanes(this, SWT.NULL, new SuiteHelper(suite));
		} catch (Exception ex) {
			innerPanes = new Composite(this, SWT.NULL);
			innerPanes.setLayout(new GridLayout());
			innerPanes.setBackground(getBackground());
			Label errorLabel = new Label(innerPanes, SWT.NONE);
			errorLabel.setText(ex.getMessage());
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 1;
			errorLabel.setLayoutData(gridData);
		}
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 1;
		innerPanes.setLayoutData(gridData);
		layout();

	}

}
