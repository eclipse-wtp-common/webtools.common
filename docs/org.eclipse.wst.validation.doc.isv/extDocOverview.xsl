<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt">
    <xsl:param name="title"/>
    <xsl:param name="header"/>
    <xsl:param name="overview"/>
    <xsl:param name="listed-ext-points"/>
    
    <xsl:template match="components">
    	
    	<xsl:text disable-output-escaping="yes">
    	&lt;!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 TRANSITIONAL//EN"&gt;
		</xsl:text>
		<html>
		<head>
		<xsl:text disable-output-escaping="yes">
		&lt;meta name="copyright" content="Copyright (c) Oracle Corporation and others 2000, 2008. This page is made available under license. For full details see the LEGAL in the documentation book that contains this page." &gt;

   		&lt;meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"&gt;
   		</xsl:text>
   		<title><xsl:value-of select="$title"/></title>
   		<xsl:text disable-output-escaping="yes">
		&lt;LINK REL="STYLESHEET" HREF="../../book.css" CHARSET="ISO-8859-1" TYPE="text/css"&gt;
		</xsl:text>
		</head>
		<body link="#0000FF" vlink="#800080">

		<center>
		<h1><xsl:value-of select="$header"/></h1>
		</center>

		<xsl:value-of select="$overview"/>
    	<xsl:for-each select="component">
    		<xsl:sort select="@id"/>
    		<h3><a name="{@id}"></a><xsl:value-of select="@name"/></h3>
    		<ul>
    			<xsl:for-each select="document(@file)/files/file">
    				<xsl:sort select="text()"/>
    				<xsl:if test="contains($listed-ext-points, translate(substring(text(), 0, string-length(text()) - 4),'_','.'))">
  						<li>
  							<a href="{../@component}/{text()}"><xsl:value-of select="translate(substring(text(), 0, string-length(text()) - 4),'_','.')"/></a> 
  						</li>
  					</xsl:if>
    			</xsl:for-each>
    		</ul>
    	</xsl:for-each>
    	</body>
		</html>
    </xsl:template>
</xsl:stylesheet>
