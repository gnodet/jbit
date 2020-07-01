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
package org.apache.camel.jbit.translate;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.camel.jbit.runtime.Runtime;
import org.apache.camel.jbit.runtime.Substitute;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Substitution {

    private static final String SUBSTITUTE = "L" + Substitute.class.getName().replace('.', '/') + ";";

    public final String owner;
    public final String name;
    public final String descriptor;
    public final String newOwner;
    public final String newName;
    public final String newDescriptor;

    public Substitution(String owner, String name, String descriptor, String newOwner, String newName, String newDescriptor) {
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.newOwner = newOwner;
        this.newName = newName;
        this.newDescriptor = newDescriptor;
    }

    public static List<Substitution> findSubstitutions(Iterable<Class<?>> supportClasses) {
        List<Substitution> substitutions = new ArrayList<>();
        for (Class<?> supportClass : supportClasses) {
            URL res = supportClass.getClassLoader().getResource(supportClass.getName().replace('.', '/') + ".class");
            try (InputStream is = Objects.requireNonNull(res).openStream()) {
                new ClassReader(is).accept(new ClassVisitor(Opcodes.ASM8) {
                    @Override
                    public MethodVisitor visitMethod(int access, String methodName, String methodDescriptor, String signature, String[] exceptions) {
                        return new MethodVisitor(Opcodes.ASM8) {
                            final Map<String, Object> values = new HashMap<>();

                            @Override
                            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                                if (SUBSTITUTE.equals(descriptor)) {
                                    return new AnnotationVisitor(Opcodes.ASM8) {
                                        @Override
                                        public void visit(String name, Object value) {
                                            values.put(name, value);
                                        }
                                    };
                                }
                                return null;
                            }

                            @Override
                            public void visitEnd() {
                                if (!values.isEmpty()) {
                                    String owner = ((String) values.get("clazz").toString()).replaceAll("L(.*);", "$1");
                                    String name = (String) values.get("method");
                                    boolean isStatic = (Boolean) values.getOrDefault("isStatic", Boolean.FALSE);
                                    String descriptor = isStatic ? methodDescriptor : methodDescriptor.replace("(L" + owner + ";", "(");
                                    String newOwner = Runtime.class.getName().replace('.', '/');
                                    String newName = methodName;
                                    String newDescriptor = methodDescriptor;
                                    substitutions.add(new Substitution(owner, name, descriptor, newOwner, newName, newDescriptor));
                                }
                            }
                        };
                    }
                }, 0);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }
        return substitutions;
    }

    public boolean matches(String owner, String name, String descriptor) {
        return Objects.equals(this.owner, owner)
                && Objects.equals(this.name, name)
                && Objects.equals(this.descriptor, descriptor);
    }
}
