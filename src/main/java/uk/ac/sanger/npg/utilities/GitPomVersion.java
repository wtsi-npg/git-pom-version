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
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Usage: GitPomVersion <repository path>
 *
 * Recursively searches for pom files and updates their versions. Tested on
 * on projects with single pom files and those with sub-modules one level
 * deep.
 *
 * @author Keith James
 */
public class GitPomVersion {

    private static final FileFilter dirFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };

    private static FilenameFilter pomFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return "pom.xml".equals(name);
        }
    };

    private static List<File> findFiles(File root, FilenameFilter filter) {
        List<File> found = new ArrayList<File>(Arrays.asList(root.listFiles(filter)));

        for (File dir : root.listFiles(dirFilter)) {
            found.addAll(findFiles(dir, filter));
        }

        return found;
    }

    /**
     * Recursively searches for pom files and updates their versions. Tested on
     * on projects with single pom files and those with sub-modules one level
     * deep.
     *
     * @param args The path to the roo of the git repository to search
     * @throws TransformerException if an error occurs transforming a pom file.
     * @throws IOException
     */
    public static void main(String[] args)
            throws TransformerException, IOException {
        if (args.length != 1) {
            System.err.println("Usage: GitPomVersion <repository path>");
            System.exit(1);
        }

        GitPomVersioner versioner = new GitPomVersioner();

        File root = new File(args[0]);

        for (File pomFile : findFiles(root, pomFilter)) {
            File backup = new File(pomFile.getCanonicalPath() + ".backup");
            versioner.updateVersion(pomFile, backup);
        }
    }
}
