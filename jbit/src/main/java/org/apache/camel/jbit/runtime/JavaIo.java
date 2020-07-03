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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class JavaIo {

    @Substitute(clazz = ByteArrayOutputStream.class, method = "writeBytes")
    public static void java_io_ByteArrayOutputStream_writeBytes(ByteArrayOutputStream s, byte[] b) throws IOException  {
        s.write(b, 0, b.length);
    }

    @Substitute(clazz = ByteArrayOutputStream.class, method = "toString")
    public static String java_io_ByteArrayOutputStream_toString(ByteArrayOutputStream s, Charset charset) {
        try {
            return s.toString(charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new IOError(e);
        }
    }

    // TODO: constructors are not supported atm
    public static class FileReader extends InputStreamReader {

        @Substitute(clazz = java.io.FileReader.class, method = "<init>")
        public FileReader(String fileName, Charset charset) throws IOException {
            super(new FileInputStream(fileName), charset);
        }

        @Substitute(clazz = java.io.FileReader.class, method = "<init>")
        public FileReader(File file, Charset charset) throws IOException {
            super(new FileInputStream(file), charset);
        }

    }

    // TODO: constructors are not supported atm
    public static class FileWriter extends OutputStreamWriter {
        @Substitute(clazz = java.io.FileWriter.class, method = "<init>")
        public FileWriter(String fileName, Charset charset) throws IOException {
            super(new FileOutputStream(fileName), charset);
        }

        @Substitute(clazz = java.io.FileWriter.class, method = "<init>")
        public FileWriter(String fileName, Charset charset, boolean append) throws IOException {
            super(new FileOutputStream(fileName, append), charset);
        }

        @Substitute(clazz = java.io.FileWriter.class, method = "<init>")
        public FileWriter(File file, Charset charset) throws IOException {
            super(new FileOutputStream(file), charset);
        }

        @Substitute(clazz = java.io.FileWriter.class, method = "<init>")
        public FileWriter(File file, Charset charset, boolean append) throws IOException {
            super(new FileOutputStream(file, append), charset);
        }
    }

    @Substitute(clazz = InputStream.class, method = "nullInputStream", isStatic = true)
    public static InputStream java_io_InputStream_nullInputStream() {
        return new InputStream() {
            private volatile boolean closed;
            @Override
            public int read() throws IOException {
                if (closed) {
                    throw new IOException("Stream closed");
                }
                return -1;
            }
            @Override
            public void close() throws IOException {
                closed = true;
            }
        };
    }

    @Substitute(clazz = InputStream.class, method = "readAllBytes")
    public static byte[] java_io_InputStream_readAllBytes(InputStream is) throws IOException {
        return java_io_InputStream_readNBytes(is, Integer.MAX_VALUE);
    }

    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    @Substitute(clazz = InputStream.class, method = "readNBytes")
    public static byte[] java_io_InputStream_readNBytes(InputStream is, int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = is.read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if (nread > 0) {
                if (MAX_BUFFER_SIZE - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                    result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

    @Substitute(clazz = InputStream.class, method = "readNBytes")
    public static int java_io_InputStream_readNBytes(InputStream in, byte[] b, int off, int len) throws IOException {
        JavaLang.java_lang_Objects_checkFromIndexSize(off, len, b.length);
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0)
                break;
            n += count;
        }
        return n;
    }

    @Substitute(clazz = InputStream.class, method = "transferTo")
    public static long java_io_InputStream_transferTo(InputStream in, OutputStream out) throws IOException {
        Objects.requireNonNull(out, "out");
        long transferred = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            out.write(buffer, 0, read);
            transferred += read;
        }
        return transferred;
    }

    @Substitute(clazz = OutputStream.class, method = "nullOutputStream", isStatic = true)
    public static OutputStream java_io_OutputStream_nullOutputStream() {
        return new OutputStream() {
            private volatile boolean closed;
            @Override
            public void write(int b) throws IOException {
                if (closed) {
                    throw new IOException("Stream closed");
                }
            }
            @Override
            public void close() throws IOException {
                closed = true;
            }
        };
    }

    @Substitute(clazz = Reader.class, method = "nullReader", isStatic = true)
    public static Reader java_io_Reader_nullReader() {
        return new Reader() {
            private volatile boolean closed;
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                if (closed) {
                    throw new IOException("Stream closed");
                }
                return len == 0 ? 0 : -1;
            }
            @Override
            public void close() throws IOException {
                closed = true;
            }
        };
    }

    @Substitute(clazz = Reader.class, method = "transferTo")
    public static long java_io_Reader_transferTo(Reader in, Writer out) throws IOException {
        Objects.requireNonNull(out, "out");
        long transferred = 0;
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int nRead;
        while ((nRead = in.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            out.write(buffer, 0, nRead);
            transferred += nRead;
        }
        return transferred;
    }

    @Substitute(clazz = Writer.class, method = "nullWriter", isStatic = true)
    public static Writer java_io_Writer_nullWriter() {
        return new Writer() {
            private volatile boolean closed;
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                if (closed) {
                    throw new IOException("Stream closed");
                }
            }
            @Override
            public void flush() throws IOException {
            }
            @Override
            public void close() throws IOException {
                closed = true;
            }
        };
    }

    @Substitute(clazz = ByteArrayInputStream.class, method = "transferTo")
    public static long java_io_ByteArrayInputStream_transferTo(ByteArrayInputStream in, OutputStream out) throws IOException {
        return java_io_InputStream_transferTo(in, out);
    }

}
