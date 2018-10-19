
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
      Float[] rv = new Float[fa.length];
      int i = -1;
      for (float f : fa) {
         rv[++i] = f;
      }
      return rv;
   }

   public static Integer[] wrap(int[] ia) {
      if (ia == null) {
         return null;
      }
      Integer[] rv = new Integer[ia.length];
      int i = -1;
      for (int ii : ia) {
         rv[++i] = ii;
      }
      return rv;
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
      Double[] rv = new Double[fa.length];
      int i = -1;
      for (double f : fa) {
         rv[++i] = f;
      }
      return rv;
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
      Boolean[] rv = new Boolean[fa.length];
      int i = -1;
      for (boolean f : fa) {
         rv[++i] = f;
      }
      return rv;
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
      Long[] rv = new Long[fa.length];
      int i = -1;
      for (long f : fa) {
         rv[++i] = f;
      }
      return rv;
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
      Short[] rv = new Short[fa.length];
      int i = -1;
      for (short f : fa) {
         rv[++i] = f;
      }
      return rv;
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
      Character[] rv = new Character[fa.length];
      int i = -1;
      for (char f : fa) {
         rv[++i] = f;
      }
      return rv;
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
      Byte[] rv = new Byte[fa.length];
      int i = -1;
      for (byte f : fa) {
         rv[++i] = f;
      }
      return rv;
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
      if (l.size() > 0 && l.get(0) != null) {
         T[] a = (T[]) Array.newInstance(l.get(0).getClass(), l.size());
         return l.toArray(a);
      } else {
         return null;
      }
   }
}
