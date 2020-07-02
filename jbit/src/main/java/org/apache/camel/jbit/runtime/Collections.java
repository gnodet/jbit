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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class Collections {

    private Collections() {
    }

     @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of() {
        return immutableList();
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of(E e1) {
        return immutableList(e1);
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of(E e1, E e2) {
        return immutableList(e1, e2);
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of(E e1, E e2, E e3) {
        return immutableList(e1, e2, e3);
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of(E e1, E e2, E e3, E e4) {
        return immutableList(e1, e2, e3, e4);
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of(E e1, E e2, E e3, E e4, E e5) {
        return immutableList(e1, e2, e3, e4, e5);
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of(E e1, E e2, E e3, E e4, E e5, E e6) {
        return immutableList(e1, e2, e3, e4, e5, e6);
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        return immutableList(e1, e2, e3, e4, e5, e6, e7);
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
        return immutableList(e1, e2, e3, e4, e5, e6, e7, e8);
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
        return immutableList(e1, e2, e3, e4, e5, e6, e7, e8, e9);
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    public static <E> List<E> java_util_List_of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
        return immutableList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10);
    }

    @Substitute(clazz = List.class, method = "of", isStatic = true)
    @SafeVarargs
    public static <E> List<E> java_util_List_of(E... elements) {
        return immutableList(elements);
    }

    @Substitute(clazz = List.class, method = "copyOf", isStatic = true)
    public static <E> List<E> java_util_List_copyOf(List<? extends E> list) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of() {
        return immutableSet();
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of(E e1) {
        return immutableSet(e1);
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of(E e1, E e2) {
        return immutableSet(e1, e2);
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of(E e1, E e2, E e3) {
        return immutableSet(e1, e2, e3);
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of(E e1, E e2, E e3, E e4) {
        return immutableSet(e1, e2, e3, e4);
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of(E e1, E e2, E e3, E e4, E e5) {
        return immutableSet(e1, e2, e3, e4, e5);
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of(E e1, E e2, E e3, E e4, E e5, E e6) {
        return immutableSet(e1, e2, e3, e4, e5, e6);
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        return immutableSet(e1, e2, e3, e4, e5, e6, e7);
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
        return immutableSet(e1, e2, e3, e4, e5, e6, e7, e8);
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
        return immutableSet(e1, e2, e3, e4, e5, e6, e7, e8, e9);
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    public static <E> Set<E> java_util_Set_of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
        return immutableSet(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10);
    }

    @Substitute(clazz = Set.class, method = "of", isStatic = true)
    @SafeVarargs
    public static <E> Set<E> java_util_Set_of(E... elements) {
        return immutableSet(elements);
    }

    @Substitute(clazz = Set.class, method = "copyOf", isStatic = true)
    public static <E> Set<E> java_util_Set_copyOf(Set<? extends E> list) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of() {
        return immutableMap();
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of(K k1, V v1) {
        return immutableMap(k1, v1);
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of(K k1, V v1, K k2, V v2) {
        return immutableMap(k1, v1, k2, v2);
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return immutableMap(k1, v1, k2, v2, k3, v3);
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return immutableMap(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return immutableMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                                    K k6, V v6) {
        return immutableMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
                            k6, v6);
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                                    K k6, V v6, K k7, V v7) {
        return immutableMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
                            k6, v6, k7, v7);
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                                    K k6, V v6, K k7, V v7, K k8, V v8) {
        return immutableMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
                            k6, v6, k7, v7, k8, v8);
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                                    K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        return immutableMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
                            k6, v6, k7, v7, k8, v8, k9, v9);
    }

    @Substitute(clazz = Map.class, method = "of", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
                                                    K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
        return immutableMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5,
                            k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
    }

    @SafeVarargs
    @Substitute(clazz = Map.class, method = "ofEntries", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_ofEntries(Map.Entry<? extends K, ? extends V>... entries) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Substitute(clazz = Map.class, method = "entry", isStatic = true)
    public static <K, V> Map.Entry<K, V> java_util_Map_entry(K k, V v) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Substitute(clazz = Map.class, method = "copyOf", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_copyOf(Map<? extends K, ? extends V> map) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @SuppressWarnings("Java9CollectionFactory")
    private static <E> List<E> immutableList(E... input) {
        // TODO: fix that
        if (input.length == 0) {
            return java.util.Collections.emptyList();
        } else {
            return java.util.Collections.unmodifiableList(Arrays.asList(input));
        }
    }

    @SafeVarargs
    @SuppressWarnings("Java9CollectionFactory")
    private static <E> Set<E> immutableSet(E... input) {
        // TODO: fix that
        if (input.length == 0) {
            return java.util.Collections.emptySet();
        } else {
            return java.util.Collections.unmodifiableSet(new HashSet<>(Arrays.asList(input)));
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> immutableMap(Object... input) {
        // TODO: fix that
        Map<K, V> m = new HashMap<>();
        for (int i = 0; i < input.length;) {
            K k = Objects.requireNonNull((K) input[i++]);
            V v = Objects.requireNonNull((V) input[i++]);
            if (m.put(k, v) != null) {
                throw new IllegalArgumentException("duplicate key: " + k);
            }
        }
        return java.util.Collections.unmodifiableMap(m);
    }

}
