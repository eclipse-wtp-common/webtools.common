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