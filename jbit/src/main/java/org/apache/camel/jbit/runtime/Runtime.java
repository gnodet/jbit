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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class Runtime {

    private Runtime() {
    }

    @Substitute(clazz = String.class, method = "isBlank")
    public static boolean java_lang_String_isBlank(String value) {
        int length = value.length();
        int left = 0;
        while (left < length) {
            int codepoint = value.codePointAt(left);
            if (codepoint != ' ' && codepoint != '\t' && !Character.isWhitespace(codepoint)) {
                return false;
            }
            left += Character.charCount(codepoint);
        }
        return true;
    }

    @Substitute(clazz = Collectors.class, method = "toUnmodifiableList", isStatic = true)
    public static <T> Collector<T, ?, List<T>> java_util_stream_Collectors_toUnmodifiableList() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                Collections::java_util_List_copyOf);
    }

    @Substitute(clazz = Collectors.class, method = "toUnmodifiableSet", isStatic = true)
    public static <T> Collector<T, ?, Set<T>> java_util_stream_Collectors_toUnmodifiableSet() {
        return Collectors.collectingAndThen(
                Collectors.toSet(),
                Collections::java_util_Set_copyOf);
    }

    @Substitute(clazz = Collectors.class, method = "toUnmodifiableMap", isStatic = true)
    @SuppressWarnings({"unchecked"})
    public static <T, K, U> Collector<T, ?, Map<K,U>> java_util_stream_Collectors_toUnmodifiableMap(Function<? super T, ? extends K> keyMapper,
                                                Function<? super T, ? extends U> valueMapper) {
        Objects.requireNonNull(keyMapper, "keyMapper");
        Objects.requireNonNull(valueMapper, "valueMapper");
        return Collectors.collectingAndThen(
                Collectors.toMap(keyMapper, valueMapper),
                map -> (Map<K,U>)Collections.java_util_Map_ofEntries(map.entrySet().toArray(new Map.Entry[0])));
    }

    @Substitute(clazz = Stream.class, method = "ofNullable", isStatic = true)
    public static<T> Stream<T> java_util_stream_Stream_ofNullable(T t) {
        return t == null ? Stream.empty() : Stream.of(t);
    }

    @Substitute(clazz = Stream.class, method = "takeWhile")
    public static <T> Stream<T> java_util_stream_Stream_takeWhile(Stream<T> stream, Predicate<? super T> predicate) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Substitute(clazz = Stream.class, method = "dropWhile")
    public static <T> Stream<T> java_util_stream_Stream_dropWhile(Stream<T> stream, Predicate<? super T> predicate) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Substitute(clazz = Stream.class, method = "iterate", isStatic = true)
    public static<T> Stream<T> iterate(T seed, Predicate<? super T> hasNext, UnaryOperator<T> next) {
        Objects.requireNonNull(next);
        Objects.requireNonNull(hasNext);
        Spliterator<T> spliterator = new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE,
                Spliterator.ORDERED | Spliterator.IMMUTABLE) {
            T prev;
            boolean started, finished;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                Objects.requireNonNull(action);
                if (finished)
                    return false;
                T t;
                if (started)
                    t = next.apply(prev);
                else {
                    t = seed;
                    started = true;
                }
                if (!hasNext.test(t)) {
                    prev = null;
                    finished = true;
                    return false;
                }
                action.accept(prev = t);
                return true;
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                Objects.requireNonNull(action);
                if (finished)
                    return;
                finished = true;
                T t = started ? next.apply(prev) : seed;
                prev = null;
                while (hasNext.test(t)) {
                    action.accept(t);
                    t = next.apply(t);
                }
            }
        };
        return StreamSupport.stream(spliterator, false);
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
                int nRead1 = java_io_InputStream_readNBytes(in1, buffer1, 0, BUFFER_SIZE);
                int nRead2 = java_io_InputStream_readNBytes(in2, buffer2, 0, BUFFER_SIZE);
                int i = java_util_Arrays_mismatch(buffer1, 0, nRead1, buffer2, 0, nRead2);
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

    @Substitute(clazz = InputStream.class, method = "readNBytes")
    public static int java_io_InputStream_readNBytes(InputStream is, byte[] b, int off, int len) throws IOException {
        java_lang_Objects_checkFromIndexSize(off, len, b.length);
        int n = 0;
        while (n < len) {
            int count = is.read(b, off + n, len - n);
            if (count < 0)
                break;
            n += count;
        }
        return n;
    }

    @Substitute(clazz = Objects.class, method = "checkFromIndexSize")
    public static int java_lang_Objects_checkFromIndexSize(int fromIndex, int size, int length) {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex) {
            String str = String.format("Range [%d, %<d + %d) out of bounds for length %d", fromIndex, size, length);
            throw new IndexOutOfBoundsException(str);
        }
        return fromIndex;
    }

    @Substitute(clazz = Arrays.class, method = "mismatch")
    public static int java_util_Arrays_mismatch(byte[] a, int aFromIndex, int aToIndex,
                                                byte[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        int i = domismatch(a, aFromIndex, b, bFromIndex, length);
        return (i < 0 && aLength != bLength) ? length : i;
    }

    private static int domismatch(byte[] a, int aFromIndex, byte[] b, int bFromIndex, int length) {
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    private static void rangeCheck(int arrayLength, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }


    static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Function<A,R> finisher,
                      Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, castingIdentity(), characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }

        @SuppressWarnings("unchecked")
        private static <I, R> Function<I, R> castingIdentity() {
            return i -> (R) i;
        }
    }
}
