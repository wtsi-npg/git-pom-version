<!--
  ~ Copyright (c) 2013 Genome Research Ltd. All rights reserved.
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:pom="http://maven.apache.org/POM/4.0.0"
                xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                                    http://maven.apache.org/maven-v4_0_0.xsd">

    <!-- git version string supplied by git describe and git tags -->
    <xsl:param name="git_version" />

    <!-- Update project version -->
    <xsl:template match="pom:project/pom:version">
         <xsl:element name="{local-name(.)}">
             <xsl:value-of select="$git_version"/>
         </xsl:element>
    </xsl:template>

    <!-- Update child to match parent project version -->
    <xsl:template match="/pom:project/pom:parent/pom:version">
        <xsl:element name="{local-name(.)}">
            <xsl:value-of select="$git_version"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
