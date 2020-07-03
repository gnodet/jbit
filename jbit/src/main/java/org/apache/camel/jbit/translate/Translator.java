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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.camel.jbit.runtime.JavaIo;
import org.apache.camel.jbit.runtime.JavaLang;
import org.apache.camel.jbit.runtime.JavaNio;
import org.apache.camel.jbit.runtime.JavaUtil;
import org.apache.camel.jbit.runtime.JavaUtilStream;
import org.apache.camel.jbit.runtime.StringConcatFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Translator {

    public static final List<Class<?>> SUBSTITUTION_CLASSES = Arrays.asList(
            JavaIo.class,
            JavaLang.class,
            JavaNio.class,
            JavaUtil.class,
            JavaUtilStream.class
    );
    private static final List<Substitution> SUBSTITUTIONS = Substitution.findSubstitutions(SUBSTITUTION_CLASSES);
    private static final String STRING_CONCAT_FACTORY = "java/lang/invoke/StringConcatFactory";
    private static final String METHOD_HANDLES = "java/lang/invoke/MethodHandles";

    /**
     * Translates a JDK 11 or JDK 14 class into a JDK 8 compatible class
     * @param classData
     * @return the translated binary class
     */
    public static byte[] transform(byte[] classData) {
        ClassReader reader = new ClassReader(classData);
        return doTransform(reader);
    }

    public static byte[] transform(InputStream input) throws IOException {
        ClassReader reader = new ClassReader(input);
        return doTransform(reader);
    }

    private static byte[] doTransform(ClassReader reader) {
        Collection<String> classes = new ArrayList<>();
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM8, writer) {
            int version;
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(Opcodes.V1_8, access, name, signature, superName, interfaces);
                this.version = version;
            }

            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                super.visitInnerClass(name, outerName, innerName, access);
                if (!METHOD_HANDLES.equals(outerName)) {
                    classes.add(name);
                }
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (version <= Opcodes.V1_8) {
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM8, mv) {
                    @Override
                    public void visitMethodInsn(
                            final int opcode,
                            final String owner,
                            final String name,
                            final String descriptor,
                            final boolean isInterface) {
                        Substitution substitution = SUBSTITUTIONS.stream()
                                .filter(s -> s.matches(owner, name, descriptor))
                                .findAny().orElse(null);
                        if (substitution != null) {
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, substitution.newOwner, substitution.newName, substitution.newDescriptor, false);
                        } else {
                            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                        }
                    }
                    @Override
                    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
                        if (STRING_CONCAT_FACTORY.equals(bootstrapMethodHandle.getOwner())) {
                            super.visitInvokeDynamicInsn(name, descriptor,
                                    new Handle(bootstrapMethodHandle.getTag(), StringConcatFactory.class.getName().replace('.', '/'),
                                            bootstrapMethodHandle.getName(), bootstrapMethodHandle.getDesc(), bootstrapMethodHandle.isInterface()),
                                    bootstrapMethodArguments);
                        } else {
                            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
                        }
                    }
                };
            }
        };
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }
}
