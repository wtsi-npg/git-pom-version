
git-pom-version is a tiny Java XSLT application that updates the version element
of pom files to reflect the string returned by git describe. It saves a copy of
the original pom files as <filename>.backup

To build using Apache Maven:

mvn clean package

Usage:

java -jar git-pom-version-n.n.n.jar /path/to/git/repository

The repository is searched recursively for files called pom.xml. It has been
tested on projects with single pom files and those with sub-modules one level
deep.

The git describe command may be customised in git_version.properties under the
git.version.command key. The default is to run the command git-version (see
the bin directory) which is a wrapper script for git describe. It is possible
to configure to run git describe directly, without the wrapper, if required.
