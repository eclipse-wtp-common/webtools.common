package org.eclipse.wst.common.tests.collector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import junit.framework.TestSuite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author jsholl
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TestCollectorInnerPanes extends Composite {

    private Table testClassTable;
    private Table testMethodTable;

    private Button launchTestButton;

    private SuiteHelper pluginTestLoader;

    private HashSet partialSetHash = new HashSet();
    private Hashtable shortToFullHashtable = new Hashtable();
    private Hashtable fullToShortHashtable = new Hashtable();

    public TestCollectorInnerPanes(Composite parent, int style, SuiteHelper loader) {
        super(parent, style);
        pluginTestLoader = loader;
        createPartControl();
    }

    public void createPartControl() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);
        GridData gridData = null;

        Group tableGroup = new Group(this, SWT.NULL);
        GridLayout tableGroupLayout = new GridLayout();
        tableGroupLayout.makeColumnsEqualWidth = true;
        tableGroupLayout.numColumns = 1;
        tableGroupLayout.marginWidth = 0;
        tableGroupLayout.marginHeight = 0;
        tableGroup.setLayout(tableGroupLayout);
        tableGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        SashForm splitView = new SashForm(tableGroup, SWT.HORIZONTAL);
        splitView.setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
        
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 2;
        splitView.setLayoutData(gridData);
        
        Composite leftComposite = new Composite(splitView, SWT.NONE);
        GridLayout leftLayout = new GridLayout();
        leftLayout.numColumns = 1;
        leftComposite.setLayout(leftLayout);
        Label label2 = new Label(leftComposite, SWT.NULL);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        label2.setLayoutData(gridData);
        label2.setText("Test Suites");

        Composite rightComposite = new Composite(splitView, SWT.NONE);
        GridLayout rightLayout = new GridLayout();
        rightLayout.numColumns = 1;
        rightComposite.setLayout(rightLayout);
        Label label3 = new Label(rightComposite, SWT.NULL);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        label3.setLayoutData(gridData);
        label3.setText("Tests");

        testClassTable = new Table(leftComposite, SWT.CHECK);
        testClassTable.setBackground(getBackground());
        gridData = new GridData(GridData.FILL_BOTH);
        testClassTable.setLayoutData(gridData);
        String[] allTests = pluginTestLoader.getAllTests();
        for (int i = 0; i < allTests.length; i++) {
            TableItem tableItem = new TableItem(testClassTable, SWT.NULL);
            tableItem.setText(allTests[i]);
        }
        testClassTable.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                TableItem item = (TableItem) e.item;
                String testName = item.getText();
                updateMethodTable(testName, pluginTestLoader.getTestMethods(testName));
                testClassTable.setSelection(new TableItem[] { item });
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            }

        });
        
        Label label = new Label(leftComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        final Button selectAllCheckbox = new Button(leftComposite, SWT.CHECK);
        selectAllCheckbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        selectAllCheckbox.setText("Select All");
        selectAllCheckbox.addSelectionListener(new SelectionAdapter(){
        	public void widgetSelected(SelectionEvent e) {
        		boolean checked = selectAllCheckbox.getSelection();
        		TableItem [] items = testClassTable.getItems();
        		for(int i=0;i<items.length; i++){
        			items[i].setChecked(checked);
        		}
        	}
        });

        testMethodTable = new Table(rightComposite, SWT.CHECK);
        testMethodTable.setBackground(getBackground());
        gridData = new GridData(GridData.FILL_BOTH);
        testMethodTable.setLayoutData(gridData);

        launchTestButton = new Button(this, SWT.PUSH);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalAlignment = GridData.CENTER;
        gridData.horizontalSpan = 2;
        launchTestButton.setLayoutData(gridData);
        launchTestButton.setText("Run Tests");
        launchTestButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                SuiteTestRunner runner = new SuiteTestRunner(buildSuite());
                runner.launch();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void storeMethodsTable() {
        TableItem[] items = testMethodTable.getItems();
        for (int i = 0; null != items && i < items.length; i++) {
            String partialTestName = (String)shortToFullHashtable.get(items[i].getText());
            if (items[i].getChecked() && !partialSetHash.contains(partialTestName)) {
                partialSetHash.add(partialTestName);
            } else if (!items[i].getChecked() && partialSetHash.contains(partialTestName)) {
                partialSetHash.remove(partialTestName);
            }
        }
    }

    private void updateMethodTable(String testName, String[] methodArray) {
        storeMethodsTable();
        testMethodTable.removeAll();
		shortToFullHashtable.clear();
		fullToShortHashtable.clear();

        for (int i = 0; null != methodArray && i < methodArray.length; i++) {
            String partialTestName = testName + "." + methodArray[i];
            int endIndex = methodArray[i].indexOf('(');
            String methodName = endIndex > 0 ? methodArray[i].substring(0, endIndex) : methodArray[i];
            shortToFullHashtable.put(methodName, partialTestName);
            fullToShortHashtable.put(partialTestName, methodName);
            TableItem tableItem = new TableItem(testMethodTable, SWT.NULL);
            tableItem.setText(methodName);
            tableItem.setChecked(partialSetHash.contains(partialTestName));
        }

    }

    private TestSuite buildSuite() {
        ArrayList completeTests = new ArrayList();
        TableItem[] items = testClassTable.getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getChecked()) {
                completeTests.add(items[i].getText());
            }
        }

        String[] completeArray = new String[completeTests.size()];
        for (int i = 0; i < completeArray.length; i++) {
            completeArray[i] = (String) completeTests.get(i);
        }

        ArrayList partialTests = new ArrayList();
        storeMethodsTable();
        Iterator iterator = partialSetHash.iterator();
        while (iterator.hasNext()) {
            partialTests.add(iterator.next());
        }

        String[] partialArray = new String[partialTests.size()];
        for (int i = 0; i < partialArray.length; i++) {
            partialArray[i] = (String) partialTests.get(i);
        }

        return pluginTestLoader.buildSuite(completeArray, partialArray);
    }

}
