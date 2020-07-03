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
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;

import org.apache.camel.jbit.translate.Translator;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("USAGE: jbit [input.jar] [output.jar]");
            return;
        }

        Path inputPath = Paths.get(args[0]);
        Path outputPath = Paths.get(args[1]);
        if (inputPath.equals(outputPath)) {
            Path org = Paths.get(inputPath.toString().replace(".jar", "-org.jar"));
            Files.move(inputPath, org);
            inputPath = org;
        }
        try (JarInputStream in = new JarInputStream(new BufferedInputStream(Files.newInputStream(inputPath)))) {
            Manifest man = in.getManifest();
            try (JarOutputStream out = new JarOutputStream(new BufferedOutputStream(Files.newOutputStream(outputPath)), man)) {
                out.setLevel(Deflater.BEST_COMPRESSION);
                JarEntry entry;
                while ((entry = in.getNextJarEntry()) != null) {
                    String name = entry.getName();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    copy(in, baos);
                    byte[] data = baos.toByteArray();
                    if (name.endsWith(".class")) {
                        data = Translator.transform(data);
                    }
                    entry.setMethod(ZipEntry.DEFLATED);
                    entry.setSize(data.length);
                    entry.setCompressedSize(-1);
                    out.putNextEntry(entry);
                    out.write(data);
                    out.closeEntry();
                }
                for (Class<?> cl : Translator.SUBSTITUTION_CLASSES) {
                    addLoadedClass(out, cl);
                }
                addLoadedClass(out, org.apache.camel.jbit.runtime.StringConcatFactory.class);
            }
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer, 0, buffer.length)) >= 0) {
            out.write(buffer, 0, read);
        }
    }

    private static void addLoadedClass(JarOutputStream out, Class<?> clazz) throws IOException {
        String fileName = clazz.getName().replace('.', '/') + ".class";
        JarEntry entry = new JarEntry(fileName);
        out.putNextEntry(entry);
        byte[] data;
        try (InputStream in = clazz.getClassLoader().getResourceAsStream(fileName)) {
            data = Translator.transform(in);
            out.write(data);
        }
        out.closeEntry();
        ClassReader r = new ClassReader(data);
        r.accept(new ClassVisitor(Opcodes.ASM8) {
            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                try {
                    String cn = name.replace('/', '.');
                    Class<?> cl = clazz.getClassLoader().loadClass(cn);
                    if (cl.getEnclosingClass() == clazz) {
                        addLoadedClass(out, cl);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    throw new IOError(e);
                }
            }
        }, 0);
    }

}
