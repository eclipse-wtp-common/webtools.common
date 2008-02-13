package org.eclipse.wst.common.frameworks.componentcore.tests;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ComponentcoreFactory;
import org.eclipse.wst.common.componentcore.internal.Property;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.componentcore.virtualpath.tests.TestWorkspace;
import org.eclipse.wst.common.tests.SimpleTestSuite;

public class StructureEditStressTest extends TestCase {
	public static String fileSep = System.getProperty("file.separator");
	public static final String PROJECT_NAME = "TestArtifactEdit";
	public static final String WEB_MODULE_NAME = "WebModule1";
	public static final URI moduleURI = URI.createURI("module:/resource/TestArtifactEdit/WebModule1");
	public static final String EDIT_MODEL_ID = "jst.web";
	public static final String EDITMODEL_STRESS = "stresstest";
	private Path zipFilePath = new Path("TestData" + fileSep + "TestArtifactEdit.zip");
	private IProject project;

	public static Test suite() {
		return new SimpleTestSuite(StructureEditStressTest.class);
	}

	// /This should be extracted out, dont have time, just trying to get coverage
	// for m4 integration....

	protected void setUp() throws Exception {
		TestWorkspace.init();
		project = TestWorkspace.getTargetProject();
	}


	public IProject getTargetProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}



	public void testMultiThreadAccess() {
		
		Job[] testJobs = new Job[50];
		for (int i = 0; i < testJobs.length; i++) {
			Job job = new Job("Job " + i)
		      {
		        
		        protected IStatus run(IProgressMonitor monitor)
		        {
		        StructureEdit moduleCore = null;
		          try
		          {
		        	moduleCore = StructureEdit.getStructureEditForWrite(project);
		        	Property prop = ComponentcoreFactory.eINSTANCE.createProperty();
		        	prop.setName("Job " + System.currentTimeMillis());
		        	prop.setValue("Blah");
		        	if (moduleCore != null) {
		        		if (moduleCore.getComponent() != null) {
		        			moduleCore.getComponent().getProperties().add(prop);
		        			System.out.println(prop.getName());
		        			moduleCore.saveIfNecessary(null);
		        		}
		        	}
		          }
		          catch (Exception e)
		          {
		        	  e.printStackTrace();
		        	  return Status.CANCEL_STATUS;
		          }
		          finally {
		        	  if (moduleCore != null)
		        		  moduleCore.dispose();
		          }
		          return Status.OK_STATUS;
		        }
		        public boolean belongsTo(Object family) {
					return EDITMODEL_STRESS.equals(family);
				}
		      };
			testJobs[i] = job;	
			}
		for (int j = 0; j < testJobs.length; j++) {
			Job job = testJobs[j];
			job.schedule();
		}
		try {
			Platform.getJobManager().join(EDITMODEL_STRESS,null);
		} catch (OperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
public void testMultiThreadComponentAccess() {
		
		Job[] testJobs = new Job[50];
		for (int i = 0; i < testJobs.length; i++) {
			Job job = new Job("Job " + i)
		      {
		        
		        protected IStatus run(IProgressMonitor monitor)
		        {
		        IVirtualComponent comp = ComponentCore.createComponent(project);
		        IPath aPath = comp.getRootFolder().getProjectRelativePath();
		        assertEquals(aPath,new Path("/WebModule1/WebContent"));
		        return Status.OK_STATUS;
		        }
		        public boolean belongsTo(Object family) {
					return EDITMODEL_STRESS.equals(family);
				}
		      };
			testJobs[i] = job;	
			}
		for (int j = 0; j < testJobs.length; j++) {
			Job job = testJobs[j];
			job.schedule();
		}
		try {
			Platform.getJobManager().join(EDITMODEL_STRESS,null);
		} catch (OperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
