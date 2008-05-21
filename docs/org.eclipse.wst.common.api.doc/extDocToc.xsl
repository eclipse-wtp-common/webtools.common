<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt"
    exclude-result-prefixes="xalan">
    <xsl:param name="listed-ext-points"/>
    
     <xsl:template match="components">
     	<xsl:text disable-output-escaping="yes">
&lt;?NLS TYPE="org.eclipse.help.toc"?&gt;
	 	</xsl:text>
	 	<toc label="Extension Points Reference">
	 		<xsl:for-each select="document(component/@file)/files/file">
	 			<xsl:sort select="text()"/>
	 			<!-- <xsl:if test="contains($listed-ext-points, translate(substring(text(), 0, string-length(text()) - 4),'_','.'))"> -->
	 				<topic label="{translate(substring(text(), 0, string-length(text()) - 4),'_','.')}" href="reference/ext/{text()}">
	 				</topic>
	 			<!-- </xsl:if> -->
	 		</xsl:for-each>
	 	</toc> 
	</xsl:template>
</xsl:stylesheet>
