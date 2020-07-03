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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class JavaNio {

    private JavaNio() {
    }

    @Substitute(clazz = Files.class, method = "readString", isStatic = true)
    public static String java_nio_Files_readString(Path path) throws IOException {
        return java_nio_Files_readString(path, StandardCharsets.UTF_8);
    }

    @Substitute(clazz = Files.class, method = "readString", isStatic = true)
    public static String java_nio_Files_readString(Path path, Charset cs) throws IOException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(cs);
        byte[] ba = Files.readAllBytes(path);
        return new String(ba, cs);
    }

    @Substitute(clazz = Files.class, method = "writeString", isStatic = true)
    public static Path java_nio_Files_writeString(Path path, CharSequence csq, OpenOption... options) throws IOException
    {
        return java_nio_Files_writeString(path, csq, StandardCharsets.UTF_8, options);
    }

    @SuppressWarnings("ReadWriteStringCanBeUsed")
    @Substitute(clazz = Files.class, method = "writeString", isStatic = true)
    public static Path java_nio_Files_writeString(Path path, CharSequence csq, Charset cs, OpenOption... options) throws IOException
    {
        Objects.requireNonNull(path);
        Objects.requireNonNull(csq);
        Objects.requireNonNull(cs);
        byte[] bytes = csq.toString().getBytes(cs);
        Files.write(path, bytes, options);
        return path;
    }

    private static final int BUFFER_SIZE = 8192;

    @Substitute(clazz = Files.class, method = "mismatch", isStatic = true)
    public static long java_nio_Files_mismatch(Path path, Path path2) throws IOException {
        if (Files.isSameFile(path, path2)) {
            return -1;
        }
        byte[] buffer1 = new byte[BUFFER_SIZE];
        byte[] buffer2 = new byte[BUFFER_SIZE];
        try (InputStream in1 = Files.newInputStream(path);
             InputStream in2 = Files.newInputStream(path2)) {
            long totalRead = 0;
            while (true) {
                int nRead1 = JavaIo.java_io_InputStream_readNBytes(in1, buffer1, 0, BUFFER_SIZE);
                int nRead2 = JavaIo.java_io_InputStream_readNBytes(in2, buffer2, 0, BUFFER_SIZE);
                int i = JavaUtil.java_util_Arrays_mismatch(buffer1, 0, nRead1, buffer2, 0, nRead2);
                if (i > -1) {
                    return totalRead + i;
                }
                if (nRead1 < BUFFER_SIZE) {
                    // we've reached the end of the files, but found no mismatch
                    return -1;
                }
                totalRead += nRead1;
            }
        }
    }


}
