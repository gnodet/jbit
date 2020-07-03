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
package org.apache.camel.jbit.runtime;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import sun.misc.Unsafe;

@SuppressWarnings("unused")
public class StringConcatFactory {

    private static final char TAG_ARG = '\u0001';
    private static final char TAG_CONST = '\u0002';
    private static final String METHOD_NAME = "concat";
    private static final Unsafe UNSAFE = getUnsafe();
    private static final String STRING = "java/lang/String";
    private static final String STRING_TYPE = "L" + STRING + ";";
    private static final String STRING_BUILDER = "java/lang/StringBuilder";
    private static final String STRING_BUILDER_TYPE = "L" + STRING_BUILDER + ";";
    private static final String OBJECT = "java/lang/Object";
    private static final String OBJECT_TYPE = "L" + OBJECT + ";";

    public static CallSite makeConcat(MethodHandles.Lookup lookup,
                                      String name,
                                      MethodType concatType) throws StringConcatException {
        char[] value = new char[concatType.parameterCount()];
        Arrays.fill(value, TAG_ARG);
        String recipe = new String(value);
        return createCallSite(lookup, name, concatType, recipe);
    }

    public static CallSite makeConcatWithConstants(MethodHandles.Lookup lookup,
                                                   String name,
                                                   MethodType concatType,
                                                   String recipe,
                                                   Object... constants) throws StringConcatException {
        return createCallSite(lookup, name, concatType, recipe, constants);
    }

    private static CallSite createCallSite(MethodHandles.Lookup lookup,
                                           String name,
                                           MethodType concatType,
                                           String rec,
                                           Object... constants) throws StringConcatException {
        try {
            String className = lookup.lookupClass().getName().replace('.', '/') + "$$StringConcat";
            MethodType args = adaptType(concatType);
            List<RecipeElement> elements = createRecipe(rec, constants);
            // Define class
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
            cw.visit(Opcodes.V1_8, Opcodes.ACC_SUPER + Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_SYNTHETIC,
                    className, null, OBJECT, null);
            // Define method
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
                    METHOD_NAME, args.toMethodDescriptorString(), null, null);
            mv.visitAnnotation("Ljdk/internal/vm/annotation/ForceInline;", true);
            mv.visitCode();
            // Create code
            Class<?>[] arr = args.parameterArray();
            mv.visitTypeInsn(Opcodes.NEW, STRING_BUILDER);
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, STRING_BUILDER, "<init>", "()V", false);
            int off = 0;
            for (RecipeElement el : elements) {
                String desc;
                if (el.getTag() == TAG_CONST) {
                    mv.visitLdcInsn(el.getValue());
                    desc = getSBAppendDesc(String.class);
                } else {
                    Class<?> cl = arr[el.getArgPos()];
                    mv.visitVarInsn(getLoadOpcode(cl), off);
                    off += getParameterSize(cl);
                    desc = getSBAppendDesc(cl);
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "append", desc, false);
            }
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, STRING_BUILDER, "toString", "()L" + STRING + ";", false );
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(-1, -1);
            mv.visitEnd();
            cw.visitEnd();

            byte[] classBytes = cw.toByteArray();
            Class<?> hostClass = lookup.lookupClass();
            Class<?> innerClass = UNSAFE.defineAnonymousClass(hostClass, classBytes, null);
            UNSAFE.ensureClassInitialized(innerClass);
            MethodHandle mh = MethodHandles.lookup().findStatic(innerClass, METHOD_NAME, args);
            return new ConstantCallSite(mh.asType(concatType));
        } catch (Error e) {
            throw e;
        } catch (Throwable t) {
            throw new StringConcatException("Generator failed", t);
        }
    }

    private static MethodType adaptType(MethodType args) {
        Class<?>[] ptypes = args.parameterArray();
        for (int i = 0; i < args.parameterCount(); i++) {
            Class<?> ptype = args.parameterType(i);
            if (!ptype.isPrimitive() && ptype != String.class && ptype != Object.class) {
                ptypes[i] = Object.class;
            }
        }
        return MethodType.methodType(args.returnType(), ptypes);
    }

    private static List<RecipeElement> createRecipe(String src, Object[] constants) {
        List<RecipeElement> el = new ArrayList<>();
        int constC = 0;
        int argC = 0;
        StringBuilder acc = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (c == TAG_CONST || c == TAG_ARG) {
                if (acc.length() > 0) {
                    el.add(new RecipeElement(acc.toString()));
                    acc.setLength(0);
                }
                el.add(c == TAG_CONST ? new RecipeElement(constants[constC++]) : new RecipeElement(argC++));
            } else {
                acc.append(c);
            }
        }
        if (acc.length() > 0) {
            el.add(new RecipeElement(acc.toString()));
        }
        return el;
    }

    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(Unsafe.class);
        } catch (Exception e) {
            throw new RuntimeException("exception while trying to get Unsafe", e);
        }
    }

    private static String getSBAppendDesc(Class<?> cl) {
        if (cl.isPrimitive()) {
            if (cl == Integer.TYPE || cl == Byte.TYPE || cl == Short.TYPE) {
                return "(I)" + STRING_BUILDER_TYPE;
            } else if (cl == Boolean.TYPE) {
                return "(Z)" + STRING_BUILDER_TYPE;
            } else if (cl == Character.TYPE) {
                return "(C)" + STRING_BUILDER_TYPE;
            } else if (cl == Double.TYPE) {
                return "(D)" + STRING_BUILDER_TYPE;
            } else if (cl == Float.TYPE) {
                return "(F)" + STRING_BUILDER_TYPE;
            } else if (cl == Long.TYPE) {
                return "(J)" + STRING_BUILDER_TYPE;
            } else {
                throw new IllegalStateException("Unhandled primitive StringBuilder.append: " + cl);
            }
        } else if (cl == String.class) {
            return "(" + STRING_TYPE + ")" + STRING_BUILDER_TYPE;
        } else {
            return "(" + OBJECT_TYPE + ")" + STRING_BUILDER_TYPE;
        }
    }

    private static int getLoadOpcode(Class<?> c) {
        if (c.isPrimitive()) {
            if (c == Void.TYPE) {
                throw new InternalError("Unexpected void type of load opcode");
            } else if (c == Long.TYPE) {
                return Opcodes.LLOAD;
            } else if (c == Float.TYPE) {
                return Opcodes.FLOAD;
            } else if (c == Double.TYPE) {
                return Opcodes.DLOAD;
            } else {
                return Opcodes.ILOAD;
            }
        }  else {
            return Opcodes.ALOAD;
        }
    }

    private static int getParameterSize(Class<?> c) {
        if (c == Void.TYPE) {
            return 0;
        } else if (c == Long.TYPE || c == Double.TYPE) {
            return 2;
        } else {
            return 1;
        }
    }

    public static class StringConcatException extends Exception {
        public StringConcatException() {
        }

        public StringConcatException(String message) {
            super(message);
        }

        public StringConcatException(String message, Throwable cause) {
            super(message, cause);
        }

        public StringConcatException(Throwable cause) {
            super(cause);
        }
    }

    static final class RecipeElement {
        private final String value;
        private final int argPos;
        private final char tag;

        public RecipeElement(Object cnst) {
            this.value = String.valueOf(Objects.requireNonNull(cnst));
            this.argPos = -1;
            this.tag = TAG_CONST;
        }

        public RecipeElement(int arg) {
            this.value = null;
            this.argPos = arg;
            this.tag = TAG_ARG;
        }

        public String getValue() {
            assert (tag == TAG_CONST);
            return value;
        }

        public int getArgPos() {
            assert (tag == TAG_ARG);
            return argPos;
        }

        public char getTag() {
            return tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RecipeElement that = (RecipeElement) o;

            if (this.tag != that.tag) return false;
            if (this.tag == TAG_CONST && (!value.equals(that.value))) return false;
            if (this.tag == TAG_ARG && (argPos != that.argPos)) return false;
            return true;
        }

        @Override
        public int hashCode() {
            return (int) tag;
        }
    }

}
