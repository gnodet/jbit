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
package org.apache.camel.jbit.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Example {

    public String testConcat1(TimeUnit x) {
        return "timeunit: " + x;
    }

    public String testConcat2(TimeUnit x) {
        return "timeunit: " + x + " ms";
    }

    public String testConcat3(TimeUnit x, int i) {
        return "timeunit: " + x + " ms " + i;
    }

    public boolean testBlank(String str) {
        return str.isBlank();
    }

    public String testVar() {
        var x = new HashMap<String, String>();
        x.put("test", "val");
        var m = Map.of("key", "val");
        x.putAll(m);
        return x.toString();
    }

    public String testSwitchExpression(TimeUnit x) {
        return switch (x) {
            case NANOSECONDS -> "ns";
            case MICROSECONDS -> "mms";
            case MILLISECONDS -> "ms";
            case SECONDS -> "s";
            case MINUTES -> "mn";
            case HOURS -> "h";
            case DAYS -> "d";
        };
    }

    private static void testIo() throws IOException {
        InputStream.nullInputStream().read();
        OutputStream.nullOutputStream().write(0);
        Reader.nullReader().read();
        Writer.nullWriter().write(' ');
        new ByteArrayInputStream("a string".getBytes()).transferTo(new ByteArrayOutputStream());

        Path target = Paths.get("target");
        Files.createDirectories(target);
        Path temp = Files.createTempFile(target, null, null);
        Files.writeString(temp, "Hello world!\n");
        try (InputStream is = Files.newInputStream(temp)) {
            is.transferTo(new ByteArrayOutputStream());
        }
        try (InputStream is = new FileInputStream(temp.toFile())) {
            is.transferTo(new ByteArrayOutputStream());
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Running on " + System.getProperty("java.version"));
        Example x = new Example();
        System.out.println(x.testConcat1(TimeUnit.SECONDS));
        System.out.println(x.testConcat2(TimeUnit.SECONDS));
        System.out.println(x.testConcat3(TimeUnit.SECONDS, 3));
        System.out.println(x.testBlank("ff"));
        System.out.println(x.testVar());
        System.out.println(x.testSwitchExpression(TimeUnit.SECONDS));
        testIo();
    }

}
