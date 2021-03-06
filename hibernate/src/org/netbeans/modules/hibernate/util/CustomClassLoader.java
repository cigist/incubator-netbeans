/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.hibernate.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.hibernate.cfg.Environment;
import org.openide.ErrorManager;

/**
 * Custom classloader for Hibernate plugin. It subclasses URLClassLoader
 * and extends the current classpath by including the given URLs.
 * 
 * @author leon (leon@hts.dev.java.net)
 * @author Vadiraj Deshpande (Vadiraj.Deshpande@Sun.COM)
 */
public class CustomClassLoader extends URLClassLoader {

    private Map<String, File> package2File = new HashMap<String, File>();
    private Logger logger = Logger.getLogger(CustomClassLoader.class.getName());

    public CustomClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        if(logger.isLoggable(Level.INFO)) {
            logger.info("Initializing Custom Classloader with classpath : ");
            for(URL url : urls) {
                logger.info(url.toExternalForm());
            }
        }
    }

    @Override
    protected Class loadClass(String name, boolean b) throws ClassNotFoundException {
       // logger.info("Load class request : " + name);
        if(name == null) {
            throw new IllegalArgumentException("class name cannot be null");
        } else if(name.startsWith("javax.validation")){
            throw new ClassNotFoundException("validation isn't supported by nb hibernate support.");//NOI18N, see #229347, support is removed even if present on classpath
        }
        Class clazz = findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }
        String packageName = null;
        int lastDotIndex = name.lastIndexOf(".");
        if (lastDotIndex != -1) {
            packageName = name.substring(0, lastDotIndex);
        }
        if (packageName != null && (packageName.startsWith("java\\.") ||
                packageName.startsWith("javax") ||
                packageName.startsWith("com.sun") ||
                packageName.startsWith("org.hibernate") ||
                packageName.startsWith("org.dom4j") ||
                packageName.startsWith("net.sf.cglib") ||
                packageName.startsWith("org.w3c") ||
                packageName.startsWith("antlr") ||
                packageName.startsWith("org.objectweb.asm") ||
                packageName.startsWith("org.apache.commons.collections") ||
                packageName.startsWith("net.sf.ehcache") ||
                packageName.startsWith("org.netbeans"))) {
            clazz = super.loadClass(name, b);
        }
        if (packageName != null && (packageName.startsWith("javassist"))) {//see #243018
            Class<?> forName = Class.forName(name);
        }
        if (clazz != null) {
            return clazz;
        }
        if (packageName != null && packageName.startsWith("org.apache.log4j")) {
            // Throw CNFE because we use java.util.logging hander in
            // the logging output
            throw new ClassNotFoundException("Log4J is forbidden");
        }
        int dotIndex = name.indexOf(".");
        String fileName = null;
        String separator = File.separator;
        if (separator.equals("\\")) {
            separator = "\\\\";
        }
        if (dotIndex != -1) {
            fileName = name.replaceAll("\\.", separator) + ".class";
        } else {
            fileName = name + ".class";
        }
        Class loadedClass = null;
        InputStream is = getLocalResourceAsStream(packageName, fileName);
        if (is != null) {
            try {
                loadedClass = loadClass(name, is);
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    // Ignore it
                }
            }
        }
        if (loadedClass != null) {
            return loadedClass;
        }
       // logger.info("loading " + name + " from super class loader " + Thread.currentThread().getContextClassLoader().getParent().getClass().getName());
        return super.loadClass(name, b);
    }

    private Class loadClass(String className, InputStream is) {
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024 * 5];
            int readBytes = 0;
            while ((readBytes = bis.read(bytes)) != -1) {
                os.write(bytes, 0, readBytes);
            }
            byte[] b = os.toByteArray();
            return defineClass(className, b, 0, b.length);
        } catch (ClassFormatError ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return null;
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return null;
        }
    }

    @Override
    protected PermissionCollection getPermissions(CodeSource codeSource) {
        Permissions perms = new Permissions();
        perms.add(new AllPermission());
        perms.setReadOnly();
        return perms;
    }

    private URL getLocalResource(String packageName, String name) {
        File preferred = null;
        if (packageName != null) {
            preferred = package2File.get(packageName);
        }
        List<File> files = new ArrayList<File>();
        if (preferred != null) {
            files.add(preferred);
        }

        for (File entry : files) {
            if (entry.isDirectory() && entry.exists()) {
                File f = new File(entry, name);
                if (f.exists()) {
                    try {
                        package2File.put(packageName, entry);
                        return f.toURL();
                    } catch (MalformedURLException ex) {
                        continue;
                    }
                }
            } else {
                if (entry.isFile() && entry.exists()) {
                    try {
                        ZipFile zf = new ZipFile(entry);
                        // Zip entries are delimited by /
                        name = name.replaceAll("\\\\", "/");
                        ZipEntry zipEntry = zf.getEntry(name);
                        if (zipEntry != null) {
                            // URL's can contain only /
                            String url = entry.getAbsolutePath().replaceAll("\\\\", "/");
                            if (!url.startsWith("/")) {
                                url = "/" + url;
                            }
                            URL r = new URL("jar:file://" + url + "!/" + name);
                            package2File.put(packageName, entry);
                            return r;
                        }
                    } catch (ZipException ex) {
                        // continue
                    } catch (IOException ex) {
                        // continue
                    }
                }
            }
        }
        return null;
    }

    private InputStream getLocalResourceAsStream(String packageName, String name) {
        URL res = getLocalResource(packageName, name);
        if (res == null) {
            return null;
        }
        try {
            return res.openStream();
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
     //   logger.info("Resource request : " + name);
        if (name != null && name.equals("hibernate.properties")) {
            return getHibernateProperties();
        }
        InputStream is = getLocalResourceAsStream(null, name);
        return is != null ? is : super.getResourceAsStream(name);
    }

    // Construct our own hibernate.properties and register ConnectionProvider.
    // Ofcourse this will override the user's version of the properties file.
    private InputStream getHibernateProperties() {
        ByteArrayInputStream bIn = new ByteArrayInputStream((Environment.CONNECTION_PROVIDER + "="+org.netbeans.modules.hibernate.util.CustomJDBCConnectionProvider.class.getName()).getBytes());
        return bIn;
    }
    @Override
    public URL findResource(String name) {
        return name.startsWith("META-INF/services") ? null : super.findResource(name); //NOI18N
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        if (name.startsWith("META-INF/services")) { //NOI18N
            return Collections.enumeration(Collections.<URL>emptyList());
        } else {
            return super.findResources(name);
        }
    }
}
