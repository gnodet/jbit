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

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class JavaUtil {

    private JavaUtil() {
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
        return new ImmutableList<>(list.toArray(new Object[0]));
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
        return immutableSet((Object[]) elements);
    }

    @Substitute(clazz = Set.class, method = "copyOf", isStatic = true)
    public static <E> Set<E> java_util_Set_copyOf(Set<? extends E> list) {
        return immutableSet(list.toArray(new Object[0]));
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
        if (entries.length == 0) { // implicit null check of entries array
            @SuppressWarnings("unchecked")
            Map<K,V> map = (Map<K,V>) ImmutableMap.EMPTY_MAP;
            return map;
        } else {
            Object[] kva = new Object[entries.length << 1];
            int a = 0;
            for (Map.Entry<? extends K, ? extends V> entry : entries) {
                // implicit null checks of each array slot
                kva[a++] = entry.getKey();
                kva[a++] = entry.getValue();
            }
            return new ImmutableMap<>(kva);
        }
    }

    @Substitute(clazz = Map.class, method = "entry", isStatic = true)
    public static <K, V> Map.Entry<K, V> java_util_Map_entry(K k, V v) {
        return new KeyValueHolder<>(k, v);
    }

    @SuppressWarnings("unchecked")
    @Substitute(clazz = Map.class, method = "copyOf", isStatic = true)
    public static <K, V> Map<K, V> java_util_Map_copyOf(Map<? extends K, ? extends V> map) {
        return (Map<K,V>) java_util_Map_ofEntries(map.entrySet().toArray(new Map.Entry[0]));
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

    private static int domismatch(byte[] a, int aFromIndex, byte[] b, int bFromIndex, int length) {
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <E> List<E> immutableList(E... input) {
        if (input.length == 0) {
            return (List<E>) ImmutableList.EMPTY_LIST;
        } else {
            return new ImmutableList<>(input);
        }
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <E> Set<E> immutableSet(Object... input) {
        if (input.length == 0) {
            return (Set<E>) ImmutableSet.EMPTY_SET;
        } else {
            return new ImmutableSet<>(input);
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> immutableMap(Object... input) {
        if (input.length == 0) {
            return (Map<K, V>) ImmutableMap.EMPTY_MAP;
        } else {
            return new ImmutableMap<>(input);
        }
    }

    static final class ImmutableList<E> extends AbstractList<E> {

        static final ImmutableList<?> EMPTY_LIST = new ImmutableList<>(new Object[0]);

        private Object[] table;

        public ImmutableList(Object[] table) {
            for (Object o : table) {
                Objects.requireNonNull(o);
            }
            this.table = table;
        }

        @Override
        public E get(int index) {
            return (E) table[index];
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                int idx = 0;
                @Override
                public boolean hasNext() {
                    return idx < table.length;
                }

                @Override
                public E next() {
                    E e = (E) table[idx];
                    idx++;
                    return e;
                }
            };
        }

        @Override
        public int size() {
            return table.length;
        }
    }

    static final class ImmutableSet<E> extends AbstractSet<E> {
        static final ImmutableSet<?> EMPTY_SET = new ImmutableSet<>(new Object[0]);
        private Object[] table;

        public ImmutableSet(Object[] table) {
            for (Object o : table) {
                Objects.requireNonNull(o);
            }
            this.table = table;
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                int idx = 0;
                @Override
                public boolean hasNext() {
                    return idx < table.length;
                }

                @Override
                public E next() {
                    E e = (E) table[idx];
                    idx++;
                    return e;
                }
            };
        }

        @Override
        public int size() {
            return table.length;
        }
    }

    static final class KeyValueHolder<K,V> implements Map.Entry<K,V> {
        final K key;
        final V value;
        KeyValueHolder(K k, V v) {
            key = Objects.requireNonNull(k);
            value = Objects.requireNonNull(v);
        }
        @Override
        public K getKey() {
            return key;
        }
        @Override
        public V getValue() {
            return value;
        }
        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("not supported");
        }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;
            return key.equals(e.getKey()) && value.equals(e.getValue());
        }
        @Override
        public int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }
        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    static class ImmutableMap<K, V> extends AbstractMap<K, V> {
        static final ImmutableMap<?, ?> EMPTY_MAP = new ImmutableMap<>(new Object[0]);

        private Object[] table;
        ImmutableMap(Object[] kva) {
            for (Object o : kva) {
                Objects.requireNonNull(o);
            }
            table = kva;
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return new AbstractSet<Entry<K, V>>() {
                @Override
                public int size() {
                    return ImmutableMap.this.table.length / 2;
                }

                @Override
                public Iterator<Entry<K,V>> iterator() {
                    return new Iterator<Entry<K,V>>() {
                        private int idx = 0;
                        @Override
                        public boolean hasNext() {
                            return idx * 2 < table.length;
                        }
                        @Override
                        public Entry<K, V> next() {
                            Entry<K, V> e = new KeyValueHolder<>((K) table[idx * 2], (V) table[idx * 2 + 1]);
                            idx++;
                            return e;
                        }
                    };
                }
            };
        }

    }



}
