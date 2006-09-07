package org.eclipse.wst.common.tests;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jem.util.logger.proxy.Logger;
/**
 * @author jsholl
 * 
 * To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates. To
 * enable and disable the creation of type comments go to Window>Preferences>Java>Code Generation.
 */
public class ProjectUtility {
    public static IProject[] getAllProjects() {
    	IProject[] projects = new IProject[0];
    	try {
        projects =  ResourcesPlugin.getWorkspace().getRoot().getProjects();
    	} catch (AssertionFailedException ex) {
    		// Catch Malformed tree exception that occurs from time to time...
    	}
    	return projects;
    }
    public static boolean projectExists(String projectName) {
        return getProject(projectName) != null;
    }
    public static IProject verifyAndReturnProject(String projectName, boolean exists) {
        IProject project = getProject(projectName);
        if (exists) {
            Assert.assertTrue("Project Does Not Exist:" + projectName, project.exists());
        } else {
            Assert.assertTrue("Project Exists:" + projectName, !project.exists());
        }
        return project;
    }
    public static void verifyProject(String projectName, boolean exists) {
        IProject project = getProject(projectName);
        if (exists) {
            Assert.assertTrue("Project Does Not Exist:" + projectName, project.exists());
        } else {
            Assert.assertTrue("Project Exists:" + projectName, !project.exists());
        }
    }
    public static IProject getProject(String projectName) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        String pathString = projectName;
        if (!(workspace.getRoot().getProject(pathString) == null))
            return workspace.getRoot().getProject(pathString);
        else
            return null;
    }
    public static void verifyNoProjects() {
        IProject[] projects = getAllProjects();
        String projectNames = "";
        for (int i = 0; i < projects.length; i++) {
            projectNames += " " + projects[i].getName();
        }
        Assert.assertTrue("All projects not deleted" + projectNames, projects.length == 0);
    }
    public static void deleteProjectIfExists(String projectName) {
        if (projectName == null)
            return;
        IProject project = getProject(projectName);
        if (project != null && project.isAccessible()) {
            try {
                project.close(null);
                project.delete(true, true, null);
                ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }
    }
    public static void deleteAllProjects() throws Exception {
        //closing projects and tread work in here is a hack because of a BeanInfo bug holding
        //onto jars loaded in another VM
        
//        for (int i = 0; i < projects.length; i++) {
//            if (projects[i].exists()) {
//                projects[i].close(null); // This should signal the extra VM to kill itself
//            }
//        }
 //       Thread.yield(); // give the VM a chance to die
        IWorkspaceRunnable runnable = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) {
				IProject[] projects = getAllProjects();
				for (int i = 0; i < projects.length; i++) {
					IProject project = projects[i];
					boolean success = false;
					Exception lastException = null;
					// Don't make 2^12 is about 4 seconds which is the max we
					// will wait for the VM to die
					for (int j = 0; j < 13 && !success; j++) {
						try {
							if (project.exists()) {
								project.delete(true, true, null);
								ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
							}
							success = true;
						} catch (Exception e) {
							lastException = e;
							if (project.exists()) {
								try {
									project.close(null);
									project.open(null);
								} catch (Exception e2) {
								}
							}
							try {
								Thread.sleep((int) Math.pow(2, j));
							} catch (InterruptedException e1) {
							} // if the VM
																// isn't dead,
																// try sleeping
						}
					}
					if (!success && lastException != null) {
						Logger.getLogger().log("Problem while deleting: " + lastException.getMessage());
						// Assert.fail("Caught Exception=" +
						// lastException.getMessage() + " when deleting
						// project=" + project.getName());
					}
				}
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(runnable, null);
		} catch (CoreException ce) {
		}
        //verifyNoProjects();
    }
    /**
	 * Return the absolute path Strings to the files based on the fileSuffix and
	 * path within the plugin.
	 * 
	 * @param path
	 * @param fileSuffix
	 *            the file ending with the "." if required (the suffix will be
	 *            used as is)
	 * @return
	 */
    public static List getSpecificFilesInDirectory(Plugin plugin, String path, final String fileSuffix) {
        URL entry = null; 
            entry = plugin.getBundle().getEntry(path); 
        List result = null;
        File folder = null;
        if (entry != null) {
            try {
                entry = Platform.asLocalURL(entry);
                folder = new File(new URI(entry.toString()));
            } catch (Exception e1) {
                e1.printStackTrace();
                return Collections.EMPTY_LIST;
            }
            List files = Arrays.asList(folder.list());
            if (!files.isEmpty()) {
                String folderPath = folder.getAbsolutePath() + File.separator;
                result = new ArrayList();
                for (int i = 0; i < files.size(); i++) {
                    String fileName = (String) files.get(i);
                    if (!fileName.endsWith(fileSuffix))
                        continue;
                    result.add(folderPath + fileName);
                }
            }
        }
        if (result == null)
            result = Collections.EMPTY_LIST;
        return result;
    }
    public static List getJarsInDirectory(Plugin plugin, String path) {
        return getSpecificFilesInDirectory(plugin, path, ".jar");
    }
    public static List getRarsInDirectory(Plugin plugin, String path) {
        return getSpecificFilesInDirectory(plugin, path, ".rar");
    }
    public static List getEarsInDirectory(Plugin plugin, String path) {
        return getSpecificFilesInDirectory(plugin, path, ".ear");
    }
    public static List getWarsInDirectory(Plugin plugin, String path) {
        return getSpecificFilesInDirectory(plugin, path, ".war");
    }
    public static String getFullFileName(Plugin plugin, String pluginRelativeFileName) throws IOException {
        IPath path = new Path(pluginRelativeFileName);
        if (path.getDevice() != null)
            return pluginRelativeFileName;
        URL url = plugin.getBundle().getEntry(pluginRelativeFileName);
        if (url != null) {
            url = Platform.asLocalURL(url);
            IPath iPath = new Path(url.getPath());
            return iPath.toOSString();
        }
        return null;
    }
}