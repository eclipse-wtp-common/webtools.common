package org.eclipse.wst.common.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * @author jsholl
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TaskViewUtility {

    public static IResource getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    public static void verifyNoNewTasks(HashSet hashSet) {
        verifyNoNewTasks(null, hashSet);
    }

    public static void verifyNoNewTasks(IResource resource, HashSet hashSet) {
        verifyNoNewTasksImpl(resource, hashSet, true);
    }

    private static void verifyNoNewTasksImpl(IResource resource, HashSet hashSet, boolean failOnFailure) {
        IResource markerSource = resource == null ? getWorkspaceRoot() : resource;
        IMarker[] markers = null;
        try {
            markers = markerSource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
        } catch (CoreException e1) {
            e1.printStackTrace();
            Assert.fail();
        }
        for (int j = 0; markers != null && j < markers.length; j++) {
            String message = markers[j].toString();
            try {
                message = (String) markers[j].getAttribute(IMarker.MESSAGE);
            } catch (Exception e) {
            }

            if (null == hashSet) {
                String failMsg = "Task in Tasks List: " + message;
                if (failOnFailure) {
                    Assert.fail(failMsg);
                } else {
                    System.out.println(failMsg);
                }
            } else if (!hashSet.contains(markers[j])) {
                String failMsg = "New Task in Tasks List: " + message;
                if (failOnFailure) {
                    Assert.fail(failMsg);
                } else {
                    System.out.println(failMsg);
                }

            }
        }
    }

    public static void verifyNoErrors() {
        verifyNoErrors(null);
    }

    public static void verifyNoErrors(IResource resource) {
        List markers = getErrors(resource);
        if (null != markers && markers.size() > 0) {
            int size = markers.size();
            String message = "" + size + " errors in tasks view:";
            IMarker marker;
            for (int i = 0; i < size; i++) {
                marker = (IMarker) markers.get(i);
                try {
                    message += "\n" + i + " " + (String) marker.getAttribute(IMarker.MESSAGE);
                } catch (Exception e) {
                }
            }
            Assert.fail(message);
        }
    }

    /**
	 * @param resource
	 * @return
	 */
	public static List getErrors(IResource resource) {
		IResource markerSource = resource == null ? getWorkspaceRoot() : resource;
        List markers = null;
        try {
            markers = findSeverityMarkers(markerSource, IMarker.SEVERITY_ERROR);
        } catch (CoreException e1) {
            e1.printStackTrace();
        }
		return markers;
	}

	private static List findSeverityMarkers(IResource markerSource, int severityLevel) throws CoreException {
        IMarker[] markers = markerSource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
        List results = null;
        Integer severity;
        for (int i = 0; i < markers.length; i++) {
            severity = (Integer) markers[i].getAttribute(IMarker.SEVERITY);
            if (severity.intValue() == severityLevel) {
                if (results == null)
                    results = new ArrayList();
                results.add(markers[i]);
            }
        }
        if (results == null)
            results = Collections.EMPTY_LIST;
        return results;
    }

    public static void verifyNoWarnings() {

    }

    public static void verifyNoTasks() {
        verifyNoTasks(null);
    }

    public static void verifyNoTasks(IResource resource) {
        verifyNoNewTasksImpl(resource, null, true);
    }

    public static void verifyNoTasks(boolean failOnFailure) {
        verifyNoTasks(null, failOnFailure);
    }

    public static void verifyNoTasks(IResource resource, boolean failOnFailure) {
        verifyNoNewTasksImpl(resource, null, failOnFailure);
    }
    public static void verifyErrors(List markerDescriptionsExpected) {
        verifyErrors(markerDescriptionsExpected,true,false);
    }

    
    public static void verifyErrors(List markerDescriptionsExpected, boolean reportIfExpectedErrorNotFound, boolean removeAllSameTypesOfError) {
        List markerDescriptionsFound = null;
        try {
            List markersFound = findSeverityMarkers(getWorkspaceRoot(), IMarker.SEVERITY_ERROR);
            markerDescriptionsFound = new ArrayList(markersFound.size());
            for (int i = 0; i < markersFound.size(); i++) {
                markerDescriptionsFound.add(((IMarker) markersFound.get(i)).getAttribute("message"));
            }
        } catch (CoreException e1) {
            e1.printStackTrace();
            Assert.fail();
        }

        ArrayList markerDescriptionsNotFound = new ArrayList();
        List markersDescriptionsToRemove = new ArrayList();
        for (int i = 0; i < markerDescriptionsExpected.size(); i++) {
        	String messageToFind = (String)markerDescriptionsExpected.get(i);
        	boolean found = false;
        	for(int j=0;j<markerDescriptionsFound.size() &&(!found || removeAllSameTypesOfError);j++){
        		if(messageToFind.equals(markerDescriptionsFound.get(j))){
        			found = true;
        			markersDescriptionsToRemove.add(markerDescriptionsFound.get(j));
        		}
            }
        	if (!found) {
                markerDescriptionsNotFound.add(messageToFind);
            } 
        }
        markerDescriptionsFound.removeAll(markersDescriptionsToRemove);
        if (markerDescriptionsNotFound.size() > 0 || markerDescriptionsFound.size() > 0) {
            String messages = "";
            if (reportIfExpectedErrorNotFound){
	            for (int i = 0; i < markerDescriptionsNotFound.size(); i++) {
	                messages += "\nError not found:\"" + markerDescriptionsNotFound.get(i)+"\"";
	            }
            }
            for (int i = 0; i < markerDescriptionsFound.size(); i++) {
                messages += "\nUnexpected error found:\"" + markerDescriptionsFound.get(i)+"\"";
            }
            if (!messages.equals(""))
                Assert.fail(messages);
        }

    }
}