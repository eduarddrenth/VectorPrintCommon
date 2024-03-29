
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.IntStream;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ClassHelper {

   private ClassHelper() {
   }

   /**
    * Calls {@link #fromPackage(java.lang.Package, java.lang.ClassLoader) } with
    * Thread.currentThread().getContextClassLoader().
    */
   public static Set<Class<?>> fromPackage(Package pr) {
      return fromPackage(pr, Thread.currentThread().getContextClassLoader());
   }

   /**
    * Calls {@link #getClasses(java.lang.ClassLoader, java.lang.String) } with the context class loader from the current
    * thread and {@link Package#getName() }
    *
    * @param p
    * @param loader
    * @return
    */
   public static Set<Class<?>> fromPackage(Package p, ClassLoader loader) {
      return getClasses(loader, p.getName());
   }

   /**
    * looks for classes in a package either in {@link #getFromDirectory(File, String, ClassLoader)}  or
    * in {@link #getFromJARFile(String, String, ClassLoader)} .
    *
    * @param loader
    * @param packageName
    * @return
    */
   public static Set<Class<?>> getClasses(ClassLoader loader, String packageName) {
      Set<Class<?>> classes = new HashSet<>();
      String path = packageName.replace('.', File.separatorChar);
      loader.resources(path)
              .map(URL::getFile)
              .filter(Objects::nonNull)
              .forEach(filePath -> {
                 // WINDOWS HACK
                 if (filePath.indexOf("%20") > 0) {
                    filePath = filePath.replace("%20", " ");
                 }
                 if ((filePath.indexOf('!') > 0) & (filePath.indexOf(".jar") > 0)) {
                    String jarPath = filePath.substring(0, filePath.indexOf('!'))
                            .substring(filePath.indexOf(':') + 1);
                    // WINDOWS HACK
                    if (jarPath.indexOf(':') >= 0) {
                       jarPath = jarPath.substring(1);
                    }
                     try {
                         classes.addAll(getFromJARFile(jarPath, packageName, loader));
                     } catch (IOException |ClassNotFoundException e) {
                         throw new VectorPrintRuntimeException(e);
                     }
                 } else {
                    classes.addAll(
                            getFromDirectory(new File(filePath), packageName, loader));
                 }

              });
      return classes;
   }

   /**
    * looks for classes in a package in a jar
    *
    * @param jar
    * @param packageName
    * @return
    * @throws IOException
    * @throws ClassNotFoundException
    */
   public static Set<Class<?>> getFromJARFile(String jar, String packageName, ClassLoader loader) throws IOException, ClassNotFoundException {
      Set<Class<?>> classes = new HashSet<>(200, 50);
      JarInputStream jarFile = new JarInputStream(new FileInputStream(jar));
      JarEntry jarEntry;
      do {
         jarEntry = jarFile.getNextJarEntry();
         if (jarEntry != null) {
            String className = jarEntry.getName();
            if (className.endsWith(".class")) {
               // check package
               if (className.replace('/', '.').replace(packageName, "").replace(".class", "").lastIndexOf('.') == 0) {
                  classes.add(Class.forName(className.replace('/', '.').replace(".class", ""), false, loader));
               }
            }
         }
      } while (jarEntry != null);
      return classes;
   }

   /**
    * looks for classes in a package in a directory
    *
    * @param directory
    * @param packageName
    * @return
    */
   public static Set<Class<?>> getFromDirectory(File directory, String packageName, ClassLoader loader) {
      Set<Class<?>> classes = new HashSet<>(200, 50);
      if (directory.exists()) {
         Arrays.stream(Objects.requireNonNull(directory.list()))
                 .filter(file -> file.endsWith(".class"))
                 .map(file -> packageName + '.' + file.replace(".class", ""))
                 .forEach(name -> {
                     try {
                         classes.add(Class.forName(name, false, loader));
                     } catch (ClassNotFoundException e) {
                         throw new VectorPrintRuntimeException(e);
                     }
                 });
      }
      return classes;
   }

   /**
    * find the runtime class for a class parameter when you know the declaring class. Calls {@link #findParameterClasses(java.lang.Class, java.lang.Class)
    * }
    *
    * @param subclass the subclass of the parameterized class whose parameter class we want to know
    * @param paramNum the index of the parameter on the parameterized class whose class we want to know
    * @param classWithParameter the class that declares the parameter whose class we are looking for
    * @return a class or null
    */
   public static <T> Class<?> findParameterClass(int paramNum, Class<? extends T> subclass, Class<T> classWithParameter) {
      return findParameterClasses(subclass, classWithParameter).get(paramNum);
   }

   /**
    * find the runtime classes of the parameters of a class or interface. The strategy is to visit the subclass and its
    * parent classes to find the classes of the classWithParameters
    *
    *
    * @param subclass the subclass of the parameterized class whose parameter class we want to know
    * @param classWithParameter the class that declares the parameter whose class we are looking for
    * @return a List of java class for the class parameters, or null
    */
   public static <T> List<Class<?>> findParameterClasses(Class<? extends T> subclass, Class<T> classWithParameter) {
      List<Class<?>> parameterClasses = null;
      // conditions
      TypeVariable[] params = classWithParameter.getTypeParameters();
      if (params.length > 0) { // parameters have to be present

         parameterClasses = new ArrayList<>(params.length);
         // to keep track of where parameters go in the class hierarchy
         Map<TypeVariable, Class<?>> varsPrevious = new HashMap<>(3);
         Map<TypeVariable, Class<?>> varsCurrent = new HashMap<>(3);
         Type parent = subclass;
         while ((parent = getGenericSuperType((Class) parent, classWithParameter)) != null) {
            if (parent instanceof ParameterizedType pa) {
                TypeVariable[] pars = getClass(pa.getRawType()).getTypeParameters();
               int i = 0;
               for (Type t : pa.getActualTypeArguments()) {
                  /*
                   * the number of getActualTypeArguments and getTypeParameters of the parent is always the same
                   *
                   * when t is a typevariable, we may find its class in previously processed type variables
                   *
                   * working this way finds the correct type, also when order of parameters changes in the hierarchy
                   */
                  if (t instanceof TypeVariable) {
                     varsCurrent.put(pars[i++], varsPrevious.getOrDefault(t, null));
                  } else {
                     varsCurrent.put(pars[i++], getClass(t));
                  }
               }
               parent = pa.getRawType();
            }
            varsPrevious.clear();
            varsPrevious.putAll(varsCurrent);
            varsCurrent.clear();
         }
         for (TypeVariable tv : params) {
            parameterClasses.add(varsPrevious.get(tv));
         }
      }
      return parameterClasses;
   }

   /**
    * returns either the generic superclass or a generic interface, but only when it is assignable from the typed
    * classWithParameter argument
    *
    * @param <T>
    * @param subclass
    * @param classWithParameter
    * @return
    */
   public static <T> Type getGenericSuperType(Class<? extends T> subclass, Class<T> classWithParameter) {
      if (subclass.getSuperclass() != null && classWithParameter.isAssignableFrom(subclass.getSuperclass())) {
         return subclass.getGenericSuperclass();
      } else {
          Class<?>[] interfaces = subclass.getInterfaces();
          for (int j = 0; j < interfaces.length; j++) {
              Class in = interfaces[j];
              if (classWithParameter.isAssignableFrom(in)) {
                  return subclass.getGenericInterfaces()[j];
              }
          }
         return null;
      }
   }

   /**
    * when possible return the class of the type argument, otherwise null
    *
    * @param type
    * @return
    */
   public static Class<?> getClass(Type type) {
      if (type instanceof Class) {
         return (Class<?>) type;
      } else if (type instanceof ParameterizedType) {
         return (Class<?>) ((ParameterizedType) type).getRawType();
      } else if (type instanceof GenericArrayType) {
         Type componentType = ((GenericArrayType) type).getGenericComponentType();
         Class<?> componentClass = getClass(componentType);
         if (componentClass != null) {
            return Array.newInstance(componentClass, 0).getClass();
         } else {
            return null;
         }
      } else if (type instanceof TypeVariable) {
         return null;
      } else if (type instanceof WildcardType) {
         return null;
      } else {
         return null;
      }
   }

   /**
    * Calls {@link #getClasses(java.lang.ClassLoader, java.lang.String) } with the contextclassloader of the current
    * thread and the first argument which should be a package name.
    *
    * @param args
    */
   public static void main(String[] args) {
      if (args.length < 1) {
         System.out.println("pass a package name as argument");
      } else {
         System.out.println(getClasses(Thread.currentThread().getContextClassLoader(), args[0]));
      }
   }

   /**
    * find the first declared constructor accepting certain parameters.
    * @param <T>
    * @param clazz
    * @param parameters
    * @return 
    */
    public static <T> Constructor<T> findConstructor(Class<T> clazz, Class<?>... parameters) {
        for (Constructor<?> con : clazz.getDeclaredConstructors()) {
            Class<?>[] parameterTypes = con.getParameterTypes();
            if (parameters.length != parameterTypes.length) {
                continue;
            }
            if (IntStream.range(0, parameters.length).allMatch(i -> parameters[i].isAssignableFrom(parameterTypes[i]))) {
                return (Constructor<T>) con;
            }
        }
        return null;
    }
}
