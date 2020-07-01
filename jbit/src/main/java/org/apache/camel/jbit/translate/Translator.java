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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class Translator {

    private static final List<Substitution> SUBSTITUTIONS = Substitution.findSubstitutions(Collections.singleton(
            org.apache.camel.jbit.runtime.Runtime.class
    ));

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
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(Opcodes.V1_8, access, name, signature, superName, interfaces);
            }

            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                super.visitInnerClass(name, outerName, innerName, access);
                if (!"java/lang/invoke/MethodHandles".equals(outerName)) {
                    classes.add(name);
                }
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM8, mv) {
                    final List<int[]> lastOpcodes = new ArrayList<>();
                    @Override
                    public void visitMethodInsn(
                            final int opcode,
                            final String owner,
                            final String name,
                            final String descriptor,
                            final boolean isInterface) {
                        pop();
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
                        if ("makeConcatWithConstants".equals(bootstrapMethodHandle.getName())
                                && "java/lang/invoke/StringConcatFactory".equals(bootstrapMethodHandle.getOwner())) {
                            List<String> args = new ArrayList<>();
                            new SignatureReader(descriptor).accept(new SignatureVisitor(Opcodes.ASM8) {
                                @Override
                                public SignatureVisitor visitParameterType() {
                                    return new SignatureVisitor(Opcodes.ASM8) {
                                        boolean array = false;
                                        @Override
                                        public SignatureVisitor visitArrayType() {
                                            array = true;
                                            return this;
                                        }
                                        @Override
                                        public void visitBaseType(char descriptor) {
                                            args.add(descriptor + (array ? "[]" : ""));
                                        }
                                        @Override
                                        public void visitClassType(String name) {
                                            args.add("L" + name + (array ? "[]" : ""));
                                        }
                                    };
                                }
                            });
                            // we have previous ALOAD / ILOAD
                            lastOpcodes.subList(0, args.size()).clear();
                            visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                            visitInsn(Opcodes.DUP);
                            visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

                            String recipe = (String) bootstrapMethodArguments[0];
                            StringBuilder sb = new StringBuilder(recipe.length());
                            int argIndex = 0;
                            int cstIndx = 0;
                            for (int i = 0; i < recipe.length(); i++) {
                                char c = recipe.charAt(i);
                                if (c == '\1' || c == '\2') {
                                    if (sb.length() > 0) {
                                        visitLdcInsn(sb.toString());
                                        visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                                        sb.setLength(0);
                                    }
                                    if (c == '\1') {
                                        String arg = args.get(argIndex);
                                        if ("I".equals(arg)) {
                                            visitIntInsn(Opcodes.ILOAD, argIndex + 1);
                                            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
                                        } else {
                                            visitIntInsn(Opcodes.ALOAD, argIndex + 1);
                                            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
                                        }
                                        argIndex++;
                                    } else if (c == '\2') {
                                        throw new UnsupportedOperationException();
                                    }
                                } else {
                                    sb.append(c);
                                }
                            }
                            if (sb.length() > 0) {
                                visitLdcInsn(sb.toString());
                                visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                                sb.setLength(0);
                            }
                            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);

                            // TODO: adjust debug info
                        } else {
                            pop();
                            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
                        }
                    }
                    private void pop() {
                        while (!lastOpcodes.isEmpty()) {
                            int[] ops = lastOpcodes.remove(0);
                            super.visitVarInsn(ops[0], ops[1]);
                        }
                    }
                    @Override
                    public void visitParameter(String name, int access) {
                        pop();
                        super.visitParameter(name, access);
                    }

                    @Override
                    public AnnotationVisitor visitAnnotationDefault() {
                        pop();
                        return super.visitAnnotationDefault();
                    }

                    @Override
                    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                        pop();
                        return super.visitAnnotation(descriptor, visible);
                    }

                    @Override
                    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                        pop();
                        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
                    }

                    @Override
                    public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
                        pop();
                        super.visitAnnotableParameterCount(parameterCount, visible);
                    }

                    @Override
                    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
                        pop();
                        return super.visitParameterAnnotation(parameter, descriptor, visible);
                    }

                    @Override
                    public void visitAttribute(Attribute attribute) {
                        pop();
                        super.visitAttribute(attribute);
                    }

                    @Override
                    public void visitCode() {
                        pop();
                        super.visitCode();
                    }

                    @Override
                    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
                        pop();
                        super.visitFrame(type, numLocal, local, numStack, stack);
                    }

                    @Override
                    public void visitInsn(int opcode) {
                        pop();
                        super.visitInsn(opcode);
                    }

                    @Override
                    public void visitIntInsn(int opcode, int operand) {
                        pop();
                        super.visitIntInsn(opcode, operand);
                    }

                    @Override
                    public void visitVarInsn(int opcode, int var) {
                        if (opcode == Opcodes.ILOAD || opcode == Opcodes.ALOAD) {
                            lastOpcodes.add(new int[] { opcode, var });
                        } else {
                            pop();
                            super.visitVarInsn(opcode, var);
                        }
                    }

                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        pop();
                        super.visitTypeInsn(opcode, type);
                    }

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                        pop();
                        super.visitFieldInsn(opcode, owner, name, descriptor);
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
                        pop();
                        super.visitMethodInsn(opcode, owner, name, descriptor);
                    }

                    @Override
                    public void visitJumpInsn(int opcode, Label label) {
                        pop();
                        super.visitJumpInsn(opcode, label);
                    }

                    @Override
                    public void visitLabel(Label label) {
                        pop();
                        super.visitLabel(label);
                    }

                    @Override
                    public void visitLdcInsn(Object value) {
                        pop();
                        super.visitLdcInsn(value);
                    }

                    @Override
                    public void visitIincInsn(int var, int increment) {
                        pop();
                        super.visitIincInsn(var, increment);
                    }

                    @Override
                    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
                        pop();
                        super.visitTableSwitchInsn(min, max, dflt, labels);
                    }

                    @Override
                    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
                        pop();
                        super.visitLookupSwitchInsn(dflt, keys, labels);
                    }

                    @Override
                    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
                        pop();
                        super.visitMultiANewArrayInsn(descriptor, numDimensions);
                    }

                    @Override
                    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                        pop();
                        return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
                    }

                    @Override
                    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                        pop();
                        super.visitTryCatchBlock(start, end, handler, type);
                    }

                    @Override
                    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                        pop();
                        return super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
                    }

                    @Override
                    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                        pop();
                        super.visitLocalVariable(name, descriptor, signature, start, end, index);
                    }

                    @Override
                    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
                        pop();
                        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
                    }

                    @Override
                    public void visitLineNumber(int line, Label start) {
                        pop();
                        super.visitLineNumber(line, start);
                    }

                    @Override
                    public void visitMaxs(int maxStack, int maxLocals) {
                        pop();
                        super.visitMaxs(maxStack, maxLocals);
                    }

                    @Override
                    public void visitEnd() {
                        pop();
                        super.visitEnd();
                    }
                };
            }
        };
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }
}
