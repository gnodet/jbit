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
package org.apache.camel.jbit.translator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.camel.jbit.runtime.Substitute;
import org.apache.camel.jbit.translate.Substitution;
import org.apache.camel.jbit.translate.Translator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranslatorTest {

    public static class MyClass {
        public MyClass() {
        }

        public int getId1() {
            return 0;
        }

        public static int getId2() {
            return 0;
        }

    }

    public static class TestClass {
        public static int getId1() {
            return new MyClass().getId1();
        }
        public static int getId2() {
            return MyClass.getId2();
        }
    }

    @Substitute(clazz = MyClass.class, method = "getId1")
    public static int MyClass_getId1(MyClass cl) {
        return 1;
    }

    @Substitute(clazz = MyClass.class, method = "getId2", isStatic = true)
    public static int MyClass_getId2() {
        return 2;
    }

    static class TestClassLoader extends ClassLoader {

        Translator translator = new Translator(Substitution.findSubstitutions(Arrays.asList(TranslatorTest.class)), true);

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            String resName = name.replace('.','/') + ".class";
            if (!resName.startsWith("java/") && !resName.startsWith("javax/")) {
                try (InputStream is = TestClassLoader.class.getClassLoader().getResourceAsStream(resName)) {

                    if (is != null) {
                        byte[] data = translator.doTransform(is);
                        Class<?> cl = defineClass(name, data, 0, data.length);
                        if (resolve) {
                            resolveClass(cl);
                        }
                        return cl;
                    }

                } catch (IOException e) {
                    throw new ClassNotFoundException("Unable to load class", e);
                }
            }
            return super.loadClass(name, resolve);
        }

    }

    @Test
    public void test() throws Exception {
        TestClassLoader cl = new TestClassLoader();
        Class<?> c = cl.loadClass(TestClass.class.getName());
        Object o = c.getConstructor().newInstance();
        Object id1 = c.getMethod("getId1").invoke(o);
        assertEquals(1, id1);
        Object id2 = c.getMethod("getId2").invoke(o);
        assertEquals(2, id2);
    }
}
