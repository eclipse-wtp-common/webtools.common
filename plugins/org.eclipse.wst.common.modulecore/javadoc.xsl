<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:variable name="root">
		<xsl:choose>
			<xsl:when test="/html/head/meta[@name='root']/@content">
				<xsl:value-of select="/html/head/meta[@name='root']/@content" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>../..</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="html">
		<html>
			<head>
				<meta
					content="text/html; charset=iso-8859-1"
					http-equiv="Content-Type" />
				<link
					type="text/css"
					href="{$root}/apistyles.css"
					rel="stylesheet" />
				<xsl:apply-templates select="head/title" />
			</head>
			<body> 
				<xsl:apply-templates
					select="body/*"
					mode="body" />
			</body>
		</html>
	</xsl:template>

	<xsl:template
		match="h1"
		mode="banner">
		<table
			border="0"
			cellpadding="2"
			cellspacing="5"
			width="100%">
			<tbody>
				<tr>
					<td
						align="left"
						width="60%">
						<font class="indextop">
							<xsl:value-of select="." />
						</font>
						<br />
						<font class="indexsub">
							<xsl:value-of select="/html/head/title" />
						</font>
					</td>
					<td width="40%">
						<img
							src="{$root}/overview/Idea.jpg"
							align="middle"
							height="86"
							hspace="50"
							width="120" />
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template
		match="h1"
		mode="body" />

	<xsl:template
		match="h2"
		mode="body">

		<table
			border="0"
			cellpadding="2"
			cellspacing="5"
			width="100%">
			<tbody>
				<tr>
					<td
						colspan="2"
						align="left"
						bgcolor="#0080c0"
						valign="top">
						<b>
							<font
								color="#ffffff"
								face="Arial,Helvetica">
								<xsl:apply-templates />
							</font>
						</b>
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template
		match="h3"
		mode="body">
		<img
			src="{$root}/images/Adarrow.gif"
			border="0"
			height="16"
			width="16" />
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template
		match="*"
		mode="body">

		<xsl:copy>
			<xsl:apply-templates select="*|@*|text()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="*|@*|text()">
		<xsl:copy>
			<xsl:apply-templates select="*|@*|text()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
