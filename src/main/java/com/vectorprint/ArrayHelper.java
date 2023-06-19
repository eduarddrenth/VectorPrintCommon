package com.vectorprint;

/*-
 * #%L
 * VectorPrintCommon
 * %%
 * Copyright (C) 2011 - 2018 VectorPrint
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ArrayHelper {

    private ArrayHelper() {
    }

    public static void clear(byte[] bytes) {
        if (bytes == null) {
            return;
        }
        Arrays.fill(bytes, (byte) 0);
    }

    public static void clear(char[] chars) {
        if (chars == null) {
            return;
        }
        Arrays.fill(chars, (char) 0);
    }

    public static float[] unWrap(Float[] fa) {
        if (fa == null) {
            return null;
        }
        float[] rv = new float[fa.length];
        int i = -1;
        for (Float f : fa) {
            rv[++i] = f;
        }
        return rv;
    }

    public static int[] unWrap(Integer[] ia) {
        if (ia == null) {
            return null;
        }
        int[] rv = new int[ia.length];
        int i = -1;
        for (Integer ii : ia) {
            rv[++i] = ii;
        }
        return rv;
    }

    public static Float[] wrap(float[] fa) {
        if (fa == null) {
            return null;
        }
        return (Float[]) IntStream.range(0, fa.length)
                .mapToObj(i -> fa[i])
                .collect(Collectors.toList()).toArray(new Float[fa.length]);
    }

    public static Integer[] wrap(int[] ia) {
        if (ia == null) {
            return null;
        }
        return Arrays.stream(ia).boxed()
                .collect(Collectors.toList()).toArray(new Integer[ia.length]);
    }

    public static double[] unWrap(Double[] ia) {
        if (ia == null) {
            return null;
        }
        double[] rv = new double[ia.length];
        int i = -1;
        for (Double ii : ia) {
            rv[++i] = ii;
        }
        return rv;
    }

    public static Double[] wrap(double[] fa) {
        if (fa == null) {
            return null;
        }
        return Arrays.stream(fa).boxed()
                .collect(Collectors.toList()).toArray(new Double[fa.length]);
    }

    public static boolean[] unWrap(Boolean[] ia) {
        if (ia == null) {
            return null;
        }
        boolean[] rv = new boolean[ia.length];
        int i = -1;
        for (Boolean ii : ia) {
            rv[++i] = ii;
        }
        return rv;
    }

    public static Boolean[] wrap(boolean[] fa) {
        if (fa == null) {
            return null;
        }
        return (Boolean[]) IntStream.range(0, fa.length)
                .mapToObj(i -> fa[i])
                .collect(Collectors.toList()).toArray(new Boolean[fa.length]);
    }

    public static long[] unWrap(Long[] ia) {
        if (ia == null) {
            return null;
        }
        long[] rv = new long[ia.length];
        int i = -1;
        for (Long ii : ia) {
            rv[++i] = ii;
        }
        return rv;
    }

    public static Long[] wrap(long[] fa) {
        if (fa == null) {
            return null;
        }
        return Arrays.stream(fa).boxed()
                .collect(Collectors.toList()).toArray(new Long[fa.length]);
    }

    public static short[] unWrap(Short[] ia) {
        if (ia == null) {
            return null;
        }
        short[] rv = new short[ia.length];
        int i = -1;
        for (Short ii : ia) {
            rv[++i] = ii;
        }
        return rv;
    }

    public static Short[] wrap(short[] fa) {
        if (fa == null) {
            return null;
        }
        return (Short[]) IntStream.range(0, fa.length)
                .mapToObj(i -> fa[i])
                .collect(Collectors.toList()).toArray(new Short[fa.length]);
    }

    public static char[] unWrap(Character[] ia) {
        if (ia == null) {
            return null;
        }
        char[] rv = new char[ia.length];
        int i = -1;
        for (Character ii : ia) {
            rv[++i] = ii;
        }
        return rv;
    }

    public static Character[] wrap(char[] fa) {
        if (fa == null) {
            return null;
        }
        return (Character[]) IntStream.range(0, fa.length)
                .mapToObj(i -> fa[i])
                .collect(Collectors.toList()).toArray(new Character[fa.length]);
    }

    public static byte[] unWrap(Byte[] ia) {
        if (ia == null) {
            return null;
        }
        byte[] rv = new byte[ia.length];
        int i = -1;
        for (Byte ii : ia) {
            rv[++i] = ii;
        }
        return rv;
    }

    public static Byte[] wrap(byte[] fa) {
        if (fa == null) {
            return null;
        }
        return (Byte[]) IntStream.range(0, fa.length)
                .mapToObj(i -> fa[i])
                .collect(Collectors.toList()).toArray(new Byte[fa.length]);
    }

    /**
     * convert a List into an array
     *
     * @param <T>
     * @param l
     * @return an array of type T or null when the List argument is empty
     * @throws NullPointerException when the List argument is null
     */
    public static <T> T[] toArray(List<T> l) {
        if (!l.isEmpty() && l.get(0) != null) {
            T[] a = (T[]) Array.newInstance(l.get(0).getClass(), l.size());
            return l.toArray(a);
        } else {
            return null;
        }
    }
}
