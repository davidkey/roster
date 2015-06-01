package com.dak.duty.util;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * See http://blog.iprofs.nl/2013/01/29/hibernate4-schema-generation-ddl-from-annotated-entities/.
 *
 */
@SuppressWarnings("rawtypes")
public class SchemaGenerator {
    private Configuration cfg;

    /**
     * @param args first argument is the directory to generate the dll to
     */
    public static void main(String[] args) throws Exception {
        final String packageName = args[0];
        SchemaGenerator gen = new SchemaGenerator(packageName);
        final String directory = args[1];
        gen.generate(Dialect.MYSQL, directory);
        gen.generate(Dialect.ORACLE, directory);
        gen.generate(Dialect.HSQL, directory);
        gen.generate(Dialect.H2, directory);
        gen.generate(Dialect.POSTGRES, directory);
    }

    public SchemaGenerator(String packageName) throws Exception {
        cfg = new Configuration();
        cfg.setProperty("hibernate.hbm2ddl.auto", "create");

        for (Class clazz : getClasses(packageName)) {
            cfg.addAnnotatedClass(clazz);
        }
    }

    /**
     * Utility method used to fetch Class list based on a package name.
     * @param packageName (should be the package containing your annotated beans.
     */
    private List<Class> getClasses(String packageName) throws Exception {
        File directory = null;
        try {
            ClassLoader cld = getClassLoader();
            URL resource = getResource(packageName, cld);
            directory = new File(resource.getFile());
        } catch (NullPointerException ex) {
            throw new ClassNotFoundException(packageName + " (" + directory
                    + ") does not appear to be a valid package");
        }
        return collectClasses(packageName, directory);
    }

    private ClassLoader getClassLoader() throws ClassNotFoundException {
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        if (cld == null) {
            throw new ClassNotFoundException("Can't get class loader.");
        }
        return cld;
    }

    private URL getResource(String packageName, ClassLoader cld) throws ClassNotFoundException {
        String path = packageName.replace('.', '/');
        URL resource = cld.getResource(path);
        if (resource == null) {
            throw new ClassNotFoundException("No resource for " + path);
        }
        return resource;
    }

   private List<Class> collectClasses(String packageName, File directory) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (directory.exists()) {
            String[] files = directory.list();
            for (String file : files) {
                if (file.endsWith(".class")) {
                    // removes the .class extension
                    classes.add(Class.forName(packageName + '.'
                            + file.substring(0, file.length() - 6)));
                }
            }
        } else {
            throw new ClassNotFoundException(packageName
                    + " is not a valid package");
        }
        return classes;
    }

    /**
     * Method that actually creates the file.
     *
     * @param dialect to use
     */
    private void generate(Dialect dialect, String directory) {
        cfg.setProperty("hibernate.dialect", dialect.getDialectClass());
        SchemaExport export = new SchemaExport(cfg);
        export.setDelimiter(";");
        export.setOutputFile(directory + "ddl_" + dialect.name().toLowerCase() + ".sql");
        export.setFormat(true);
        export.execute(true, false, false, false);
    }

    /**
     * Holds the classnames of hibernate dialects for easy reference.
     */
    private static enum Dialect {
        ORACLE("org.hibernate.dialect.Oracle10gDialect"),
        MYSQL("org.hibernate.dialect.MySQLDialect"),
        HSQL("org.hibernate.dialect.HSQLDialect"),
        H2("org.hibernate.dialect.H2Dialect"),
        POSTGRES("org.hibernate.dialect.PostgreSQLDialect");

        private String dialectClass;

        private Dialect(String dialectClass) {
            this.dialectClass = dialectClass;
        }

        public String getDialectClass() {
            return dialectClass;
        }
    }
}

