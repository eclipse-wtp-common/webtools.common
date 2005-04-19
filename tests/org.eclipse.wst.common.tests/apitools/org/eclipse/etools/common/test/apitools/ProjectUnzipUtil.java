package org.eclipse.etools.common.test.apitools;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.internal.resources.ProjectDescriptionReader;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

public class ProjectUnzipUtil {

	private IPath zipLocation;
	private String[] projectNames;
	private IPath rootLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
	private static final String META_PROJECT_NAME = ".project";



	public ProjectUnzipUtil(IPath aZipLocation, String[] aProjectNames) {
		zipLocation = aZipLocation;
		projectNames = aProjectNames;

	}

	public boolean createProjects() {
		try {
			expandZip();
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			buildProjects();
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	private IProgressMonitor getProgessMonitor() {
		return new NullProgressMonitor();
	}

	private void expandZip() throws CoreException, IOException {
		IProgressMonitor monitor = getProgessMonitor();
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipLocation.toFile());
		} catch (IOException e1) {
			throw e1;
		}
		Enumeration entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			monitor.subTask(entry.getName());
			File aFile = computeLocation(entry.getName()).toFile();
			File parentFile = null;
			try {
				if (entry.isDirectory()) {
					aFile.mkdirs();
				} else {
					parentFile = aFile.getParentFile();
					if (!parentFile.exists())
						parentFile.mkdirs();
					if (!aFile.exists())
						aFile.createNewFile();
					copy(zipFile.getInputStream(entry), new FileOutputStream(aFile));
					if (entry.getTime() > 0)
						aFile.setLastModified(entry.getTime());
				}
			} catch (IOException e) {
				throw e;
			}
			monitor.worked(1);
		}
	}

	private IPath computeLocation(String name) {
		return rootLocation.append(name);
	}


	public static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		try {
			int n = in.read(buffer);
			while (n > 0) {
				out.write(buffer, 0, n);
				n = in.read(buffer);
			}
		} finally {
			in.close();
			out.close();
		}
	}

	public void setRootLocation(IPath rootLocation) {
		this.rootLocation = rootLocation;
	}

	private void buildProjects() throws IOException, CoreException {
		for (int i = 0; i < projectNames.length; i++) {
			ProjectDescriptionReader pd = new ProjectDescriptionReader();
			IPath projectPath = new Path("/" + projectNames[i] + "/" + META_PROJECT_NAME);
			IPath path = rootLocation.append(projectPath);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectNames[i]);
			ProjectDescription description;
			try {
				description = pd.read(path);
				project.create(description, (getProgessMonitor()));
				project.open(getProgessMonitor());

			} catch (IOException e) {
				throw e;
			} catch (CoreException e) {
				throw e;
			}
		}
	}



}
