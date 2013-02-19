/*
 * Copyright (c) 2013 Genome Research Ltd. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.sanger.npg.utilities;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Properties;

/**
 * Modifies the version element of maven pom files to reflect the version
 * reported by git-describe. Keeps one back-up copy of the original pom file.
 *
 * @author Keith James
 */
class GitPomVersioner {

    private static final String GIT_VERSION_COMMAND_KEY    = "git.version.command";
    private static final String GIT_VERSION_STYLESHEET_KEY = "git.version.xsl";
    private static final String GIT_VERSION_PARAMETER_KEY  = "git.version.xsl.param";

    private Properties         config;
    private TransformerFactory factory;

    public GitPomVersioner() throws IOException {
        factory = TransformerFactory.newInstance();
        config = new Properties();

        InputStream is = getClass().getResourceAsStream("git_version.properties");
        config.load(is);
    }

    /**
     * Saves a copy of the current pom file as a back-up and then transforms
     * the current pom file by setting its version string to that reported
     * by git-describe.
     *
     * @param pomFile The current pom file.
     * @param backupFile The new back-up file to which a copy of the current
     *                   pom file will be saved.
     * @throws IOException
     */
    void updateVersion(File pomFile, File backupFile) throws IOException {
        File repository = pomFile.getParentFile();

        String newVersion = findCurrentVersion(repository);

        try {
            copyFile(pomFile, backupFile);
        }
        catch (IOException ioe) {
            String msg = String.format("Failed to backup current .pom file " +
                                       "'%s' to '%s' because: %s",
                                       pomFile, backupFile, ioe.getMessage());
            System.err.println(msg);
            System.exit(1);
        }

        try {
            if (backupFile.exists()) {
                transformFile(backupFile, pomFile, newVersion);
            }
            else {
                String msg = String.format("Failed to find backup pom file " +
                                           "'%s'; transformation aborted");
                System.err.println(msg);
                System.exit(1);
            }
        }
        catch (TransformerException te) {
            String msg = String.format("Failed to transform current .pom file " +
                                       "'%s' to '%s' because: %s",
                                       pomFile, backupFile, te.getMessage());
            System.err.println(msg);
            System.exit(1);
        }

        String msg = String.format("Changed pom version in '%s' to '%s' " +
                                   "with backup in '%s'",
                                   pomFile, newVersion, backupFile);
        System.err.println(msg);
    }

    private String findCurrentVersion(File repository) throws IOException {
        String command = config.getProperty(GIT_VERSION_COMMAND_KEY,
                                                     "git-version");
        String version = null;

        try {
            Process process = new ProcessBuilder(command)
                                      .directory(repository).start();

            InputStream in = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            if (line != null) {
                version = line.trim();
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String msg = String.format("Command '%s' exited with exit code %d",
                                           command, exitCode);
                throw new RuntimeException(msg);
            }
        }
        catch (InterruptedException ie) {
            String msg = String.format("Command '%s' was interrupted: %s",
                                       command, ie.getMessage());
            throw new RuntimeException(msg, ie);
        }

        return version;
    }

    private void transformFile(File source, File destination, String newVersion)
            throws TransformerException {
        String versionParam = config.getProperty(GIT_VERSION_PARAMETER_KEY,
                                                 "git_version");
        String stylesheet = config.getProperty(GIT_VERSION_STYLESHEET_KEY,
                                               "git_version.xsl");

        InputStream is = getClass().getResourceAsStream(stylesheet);

        Transformer transformer = factory.newTransformer(new StreamSource(is));
        transformer.setParameter(versionParam, newVersion);

        transformer.transform(new StreamSource(source),
                              new StreamResult(destination));
    }

    private void copyFile(File source, File destination) throws IOException {
        InputStream is = null;
        OutputStream os = null;

    	try {
            is = new FileInputStream(source);
            os = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            int length;

    	    while ((length = is.read(buffer)) > 0) {
     	    	os.write(buffer, 0, length);
    	    }
    	}
        finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
