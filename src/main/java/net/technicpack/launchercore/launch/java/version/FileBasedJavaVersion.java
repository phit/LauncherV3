/*
 * This file is part of Technic Launcher Core.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.technicpack.launchercore.launch.java.version;

import net.technicpack.utilslib.Utils;
import net.technicpack.launchercore.launch.java.IJavaVersion;

import java.io.File;

/**
 * An IJavaVersion based on an externally-selected java executable.
 */
public class FileBasedJavaVersion implements IJavaVersion {
    private transient boolean haveQueriedVersion = false;
    private transient String versionNumber;
    private transient boolean is64Bit;
    private File javaPath;

    public FileBasedJavaVersion() {}
    public FileBasedJavaVersion(File javaPath) {
        this.javaPath = javaPath;
    }

    @Override
    public String getVersionNumber() {
        if (!haveQueriedVersion) {
            verify();
        }

        return versionNumber;
    }

    public boolean is64Bit() {
        if (!haveQueriedVersion) {
            verify();
        }

        return is64Bit;
    }

    public File getJavaPath() { return javaPath; }

    /**
     *
     * @return True if the javaPath points to a valid version of java that can be run, false otherwise
     */
    public boolean verify() {
        if (javaPath == null || !javaPath.exists())
            return false;

        if (!haveQueriedVersion) {
            haveQueriedVersion = true;
            versionNumber = getVersionNumberFromJava();
        }

        return (versionNumber != null);
    }

    /**
     * Obtain the version number of the java executable in javaPath, by querying with java -version and mangling
     * the output
     *
     * @return The version number of the java executable in the javaPath field, or null if there was a problem with
     * the executable.
     */
    protected String getVersionNumberFromJava() {
        String data = Utils.getProcessOutput(javaPath.getAbsolutePath(), "-version");

        if (data == null)
            return null;

        is64Bit = data.contains("64-Bit");

        int versionStartIndex = data.indexOf("java version \"");

        if (versionStartIndex < 0)
            return null;
        versionStartIndex += "java version \"".length();
        int versionEndIndex = data.indexOf('\"', versionStartIndex);
        return data.substring(versionStartIndex, versionEndIndex);
    }
}
