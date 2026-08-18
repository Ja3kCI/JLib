package me.jackwci.jlib.commands.internal;

import me.jackwci.jlib.commands.annotations.Command;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class CommandScanner {

    private CommandScanner() {
    }

    public static List<Class<?>> scan(
            ClassLoader classLoader,
            String packageName
    ) {

        List<Class<?>> classes = new ArrayList<>();

        String packagePath =
                packageName.replace('.', '/');

        try {

            Enumeration<URL> resources =
                    classLoader.getResources(packagePath);

            while (resources.hasMoreElements()) {

                URL resource = resources.nextElement();

                switch (resource.getProtocol()) {

                    case "file" ->
                            scanDirectory(
                                    classLoader,
                                    packageName,
                                    new File(
                                            URLDecoder.decode(
                                                    resource.getFile(),
                                                    StandardCharsets.UTF_8
                                            )
                                    ),
                                    classes
                            );

                    case "jar" ->
                            scanJar(
                                    classLoader,
                                    packageName,
                                    resource,
                                    classes
                            );
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Failed to scan package: "
                            + packageName,
                    exception
            );
        }

        return classes;
    }

    private static void scanDirectory(
            ClassLoader classLoader,
            String packageName,
            File directory,
            List<Class<?>> classes
    ) {

        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {

            if (file.isDirectory()) {

                scanDirectory(
                        classLoader,
                        packageName + "." + file.getName(),
                        file,
                        classes
                );

                continue;
            }

            if (!file.getName().endsWith(".class")) {
                continue;
            }

            String className =
                    packageName
                            + "."
                            + file.getName()
                            .substring(
                                    0,
                                    file.getName().length() - 6
                            );

            loadClass(
                    classLoader,
                    className,
                    classes
            );
        }
    }

    private static void scanJar(
            ClassLoader classLoader,
            String packageName,
            URL resource,
            List<Class<?>> classes
    ) {

        try {

            JarURLConnection connection =
                    (JarURLConnection)
                            resource.openConnection();

            try (
                    JarFile jar =
                            connection.getJarFile()
            ) {

                String packagePath =
                        packageName.replace('.', '/');

                Enumeration<JarEntry> entries =
                        jar.entries();

                while (entries.hasMoreElements()) {

                    JarEntry entry =
                            entries.nextElement();

                    String name =
                            entry.getName();

                    if (!name.startsWith(packagePath)) {
                        continue;
                    }

                    if (!name.endsWith(".class")) {
                        continue;
                    }

                    if (name.contains("$")) {
                        continue;
                    }

                    String className =
                            name
                                    .substring(
                                            0,
                                            name.length() - 6
                                    )
                                    .replace('/', '.');

                    loadClass(
                            classLoader,
                            className,
                            classes
                    );
                }

            }

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Failed to scan jar.",
                    exception
            );
        }
    }

    private static void loadClass(
            ClassLoader classLoader,
            String className,
            List<Class<?>> classes
    ) {

        try {

            Class<?> clazz =
                    Class.forName(
                            className,
                            false,
                            classLoader
                    );

            if (clazz.isAnnotationPresent(Command.class)) {
                classes.add(clazz);
            }

        } catch (ClassNotFoundException exception) {

            throw new RuntimeException(
                    "Could not load class: "
                            + className,
                    exception
            );

        }
    }
}