----------------------------------------------------------------------------------
--						Generating JavaDoc and Extension Point Documentation for 					--
--						org.eclipse.wst.validation.doc.isv plug-in						--
--								2008/05/20	(GRK)								--
----------------------------------------------------------------------------------

Intro:
	This tool and readme was extracted from an example done by the JSF team. It was
	then adopted to generate the JavaDocs and Ext-Pt for validation.
	
	
JavaDoc Generation Task
	Intro:
		The javadocBuild.xml file is used to create the reference javadocs. It hard codes the package
		names into the Ant script, so if API packages are added or removed this will need to be updated.   
			
	Setup:
		- ensure that the org.eclipse.wst.validation plug-in is present in the workspace
		
	Generate:
		1) select javadocBuild.xml file in project explorer
		2) Run as "Ant Build..." --> JRE Tab, ensure that "Run in same JRE as Workspace" is selected
		
	Sanity Test:
		1) Directly select the API packages in the org.eclipse.wst.validation plug-in.
		2) Use the Export->Javadoc command to generate javadoc into a temporary directory.
		3) Examine the Console for warnings and errors. Fix as necessary.
	
Extension-point References Task
	Intro:
		The listed-ext-points property in buildExtDocs.properties file is used to generate 
		the HTML documentation from the schemas.   This list must be verified for accuracy 
		and is not generated like the Javadoc API packages are.
				
	Generate:
		1) select buildExtDocs.xml file in project explorer
		2) Run as "Ant Build..." --> JRE Tab, ensure that "Run in same JRE as Workspace" is 
		selected

Checking-in:
	Check in:
	- any changes made in this project

		
