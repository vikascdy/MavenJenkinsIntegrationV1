// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.agent.launcher.classloader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Custom class loader to auto load jar files from the lib/common directory by
 * the root classloader.
 * 
 * @author willclem
 */
public class SMClassLoader extends URLClassLoader {

    private static final String PATH_SEPARATOR = ";";

    public SMClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public SMClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public SMClassLoader(URL[] urls) {
        super(urls);
    }

    public SMClassLoader(final ClassLoader parent) {
        super(new URL[0], parent);
    }

    public SMClassLoader(final ClassLoader parent, final String... classpath) {
        super(new URL[0], parent);
        if (classpath != null && classpath.length > 0) {
            for (final String cp : classpath) {
                addClasspath(cp);
            }
        }
    }

    public void addClasspath(final String cp) {
        addClasspath(cp, PATH_SEPARATOR);
    }

    public void addClasspath(final String cp, final String pathSeparator) {
        if (cp != null && cp.trim().length() > 0) {
            final StringTokenizer tokenizer = new StringTokenizer(cp, pathSeparator);
            while (tokenizer.hasMoreTokens()) {
                final String sDirName = tokenizer.nextToken();
                if ((sDirName != null) && (sDirName.length() > 0)) {
                    try {
                        registerFile(new File(sDirName));
                    } catch (final Exception ignore) {
                        // TODO: Determine what to do here
                        ignore.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Registers URL in class loader.
     * 
     * @param url
     *            URL to register in class loader
     */
    @Override
    public final void addURL(final URL url) {
        // Check if j2ee.jar is present in class path, and update system
        // variable java.class.path
        // in order to let J2EE classes determine J2EE_HOME directory.
        final String sFile = url.getPath();
        if (sFile.endsWith("/j2ee.jar")) {
            final String sClassPath = System.getProperty("java.class.path", "");
            System.setProperty("java.class.path", sClassPath + File.pathSeparator + sFile);
        }
        super.addURL(url);
    }

    /**
     * Registers path in class loader.
     * 
     * @param path
     *            jar-file or directory that houses jar-files or classes to
     *            register in class loader
     * @throws Exception
     *             Thrown is there is a problem registering a file.
     */
    public final void registerFile(final File path) throws Exception {
        if (path == null) {
            return;
        }
        if (path.exists()) {
            if (path.isDirectory()) {
                addURL(path.toURI().toURL());
                final DirTraverser dirParser = new DirTraverser(path);
                for (final File url : dirParser.getFiles()) {
                    addURL(url.toURI().toURL());
                }
            } else if (path.isFile()) {
                addURL(path.toURI().toURL());
            }

        } else {
            final File dir = path.getParentFile();
            if (dir.isDirectory()) {
                final Pattern pattern = DirTraverser.createExtendedPathPattern(path.getName(), ";, ");
                final File[] fils = dir.listFiles();
                for (final File f : fils) {
                    if (pattern.matcher(f.getName()).matches()) {
                        addURL(f.toURI().toURL());
                    }
                }
            }
        }
    }

    @Override
    protected final synchronized Class<?> loadClass(final String name, final boolean resolve)
            throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            c = super.loadClass(name, resolve);
        }
        return c;
    }

    @Override
    protected final Class<?> findClass(final String name) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            c = super.findClass(name);
        }
        return c;
    }

}
