
git-pom-version is a tiny Java XSLT application that updates the version element
of pom files to reflect the string returned by git describe. It saves a copy of
the original pom files as <filename>.backup

Usage:

java -cp git-pom-version.jar uk.ac.sanger.npg.utilities.GitPomVersion /path/to/git/repository

The repository is searched recursively for files called pom.xml. It has been
tested on projects with single pom files and those with sub-modules one level
deep.
