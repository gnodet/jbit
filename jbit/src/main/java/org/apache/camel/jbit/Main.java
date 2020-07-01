/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.jbit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.camel.jbit.translate.Translator;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("USAGE: jbit [input.jar] [output.jar]");
            return;
        }

        Path inputPath = Paths.get(args[0]);
        Path outputPath = Paths.get(args[1]);
        try (JarInputStream in = new JarInputStream(new BufferedInputStream(Files.newInputStream(inputPath)))) {
            Manifest man = in.getManifest();
            try (JarOutputStream out = new JarOutputStream(new BufferedOutputStream(Files.newOutputStream(outputPath)), man)) {
                JarEntry entry;
                while ((entry = in.getNextJarEntry()) != null) {
                    String name = entry.getName();
                    if (name.endsWith(".class")) {
                        byte[] data = Translator.transform(in);
                        entry.setSize(data.length);
                        entry.setCompressedSize(-1);
                        out.putNextEntry(entry);
                        out.write(data);
                        out.closeEntry();
                    } else {
                        out.putNextEntry(entry);
                        in.transferTo(out);
                        out.closeEntry();
                    }
                }
                addLoadedClass(out, org.apache.camel.jbit.runtime.Runtime.class);
            }
        }
    }

    private static void addLoadedClass(JarOutputStream out, Class<?> clazz) throws IOException {
        String fileName = clazz.getName().replace('.', '/') + ".class";
        JarEntry entry = new JarEntry(fileName);
        out.putNextEntry(entry);
        try (InputStream in = clazz.getClassLoader().getResourceAsStream(fileName)) {
            byte[] data = Translator.transform(in);
            out.write(data);
        }
        out.closeEntry();
    }

}
