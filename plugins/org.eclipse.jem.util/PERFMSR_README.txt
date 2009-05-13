org.eclipse.perfmsr.core code can be found in the perfmsr.jar loaded as binary in this
project. Since this is optional, we needed to be able to compile without the true
plugin being available. So we created the stub jar containing just what we needed.

If there is a need to change anything in the stub jar, you will need to checkout
the folder org.eclipse.jem.util/org.eclipse.perfmsr.core.stub. This will then
be a separate project. You can then make the changes there, and then following the
README in that project to create and commit the changes. 

The jar is in this project's classpath, but it is not exported and is not in
the plugin.xml or build.properties. This means it will be available for compilation
but it won't show up in the runtime workbench.

05/13/2009
Note: the optional prereq, 
 org.eclipse.perfmsr.core;bundle-version="1.0.0";resolution:=optional,
was removed since it was confusing to build or releng teams, who want to make 
sure they _could_ get all the bundles, optional or not, if they wanted to. 
Whereas this jar is more for diagnosing or checking performance issues ... nothing 
that is done any longer on this code. The code itself hasn't been changed, so should be 
easy to resurect if ever required in the future. If this code is ever changed drastically, 
such as refactored, I'm not sure the old performance measurement code needs to be carried 
forward, as I think there are newer methods of doing similar things, that wouldn't require
a "development time only" bundle as an optional pre-req.  
