package org.eclipse.wst.common.snippets.tests.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.wst.common.snippets.internal.AbstractSnippetProvider;
import org.eclipse.wst.common.snippets.internal.editors.ISnippetEditor;
import org.eclipse.wst.common.snippets.tests.TestsPlugin;
import org.eclipse.wst.common.snippets.ui.ISnippetInsertion;

public class ComplexProvider extends AbstractSnippetProvider {

	public static final String TEST_TXT = "test.txt";
	public static final String TESTING = "testing 1, 2, 3, 4, 5, 6";

	public String getId() {
		return "Complex_Example";
	}

	public ISnippetInsertion getSnippetInsertion() {
		return new ComplexInsertion();
	}

	public boolean isActionEnabled(ISelection selection) {
		return true;
	}

	public IStatus saveAdditionalContent(IPath path) {
		if (path == null){
			throw new IllegalArgumentException("path is null");
		}
		File f = new File(path.toOSString());
		if (!f.exists()){
		  f.mkdir();
		}
		File testSave = new File(f, TEST_TXT);
		try {
			saveTextFile(testSave);
		} catch (IOException e) {
			return new Status(IStatus.ERROR, TestsPlugin.PLUGIN_ID, "could not save file");
		}
		return Status.OK_STATUS;
	}

	private void saveTextFile(File testSave) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(testSave));
			bw.write(TESTING);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (bw != null){
				bw.close();
			}
		}
	}

	public ISnippetEditor getSnippetEditor() {
		// TODO Auto-generated method stub
		return null;
	}

}
