If you are getting compile errors in PerformanceMonitorUtil and PresentPerformanceMonitor classes it is because you 
don't have org.eclipse.perfmsr.core plugin installed. This is an optional plugin that is usually never needed 
except for performance testing. 

So to get around the compile errors we've created a stub plugin that you can use just for compiling. To use this
go to CVS Repository view and check out as a project the org.eclipse.jem.util/org.eclipse.permsr.core.stub directory.
This will create a plugin project with this as a stub for the performance monitor. You can then compile and run
from the Run Runtime Workbench launches.