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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * @author jsholl
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestCollectorGUI extends Composite implements ModifyListener {

	private TestCollectorInnerPanes innerPanes = null;
	private Combo combo = null;

	private Hashtable testSuites = new Hashtable();
	private Hashtable classLoaders = new Hashtable();

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
		TestCollectorPlugin plugin = TestCollectorPlugin.instance;
		IExtension[] suitesExtensions = plugin.suitesExtensionPoint.getExtensions();

		for (int i = 0; i < suitesExtensions.length; i++) {
			IExtension extension = suitesExtensions[i];
			IConfigurationElement[] tests = extension.getConfigurationElements();
			for (int j = 0; j < tests.length; j++) {
				try {
					IConfigurationElement element = tests[j];
					String suiteName = element.getAttribute("name");
					String suiteClass = element.getAttribute("class");
					testSuites.put(suiteName, suiteClass);
					classLoaders.put(suiteName, extension.getDeclaringPluginDescriptor().getPluginClassLoader());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
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
			String className = (String) testSuites.get(combo.getText());
			ClassLoader classLoader = (ClassLoader) classLoaders.get(combo.getText());
			TestSuite suite = (TestSuite) classLoader.loadClass(className).newInstance();

			innerPanes = new TestCollectorInnerPanes(this, SWT.NULL, new SuiteHelper(suite));

			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 1;
			innerPanes.setLayoutData(gridData);
			layout();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
