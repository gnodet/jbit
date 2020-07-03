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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class JavaUtilStream {

    private JavaUtilStream() {
    }

    @Substitute(clazz = Collectors.class, method = "toUnmodifiableList", isStatic = true)
    public static <T> Collector<T, ?, List<T>> java_util_stream_Collectors_toUnmodifiableList() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                JavaUtil::java_util_List_copyOf);
    }

    @Substitute(clazz = Collectors.class, method = "toUnmodifiableSet", isStatic = true)
    public static <T> Collector<T, ?, Set<T>> java_util_stream_Collectors_toUnmodifiableSet() {
        return Collectors.collectingAndThen(
                Collectors.toSet(),
                JavaUtil::java_util_Set_copyOf);
    }

    @Substitute(clazz = Collectors.class, method = "toUnmodifiableMap", isStatic = true)
    @SuppressWarnings({"unchecked"})
    public static <T, K, U> Collector<T, ?, Map<K,U>> java_util_stream_Collectors_toUnmodifiableMap(Function<? super T, ? extends K> keyMapper,
                                                Function<? super T, ? extends U> valueMapper) {
        Objects.requireNonNull(keyMapper, "keyMapper");
        Objects.requireNonNull(valueMapper, "valueMapper");
        return Collectors.collectingAndThen(
                Collectors.toMap(keyMapper, valueMapper),
                map -> (Map<K,U>) JavaUtil.java_util_Map_ofEntries(map.entrySet().toArray(new Map.Entry[0])));
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
    public static<T> Stream<T> java_util_stream_Stream_iterate(T seed, Predicate<? super T> hasNext, UnaryOperator<T> next) {
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


}
