

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


import com.vectorprint.testing.ThreadTester;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class CommonTest {
   
   @Test
   public void testFind() throws IOException, FileNotFoundException, ClassNotFoundException {
      Collection<Class<?>> c = ClassHelper.fromPackage(ClassHelper.class.getPackage());
      Assert.assertTrue(c.contains(ClassHelper.class));
      Assert.assertTrue(c.contains(VectorPrintException.class));
      Assert.assertTrue(c.contains(VectorPrintRuntimeException.class));
      Assert.assertTrue(c.contains(VersionInfo.class));
      Assert.assertTrue(c.contains(CommonTest.class));
      c = ClassHelper.getFromJARFile(
          "src/test/resources/VectorPrintCommon-2.0.jar",ClassHelper.class.getPackage().getName(), Thread.currentThread().getContextClassLoader());
      Assert.assertTrue(c.contains(ClassHelper.class));
      Assert.assertTrue(c.contains(VectorPrintException.class));
      Assert.assertTrue(c.contains(VectorPrintRuntimeException.class));
      Assert.assertTrue(c.contains(VersionInfo.class));
   }
   
   @Test
   public void testVersionInfo() throws IOException {
      Map<String, VersionInfo.VersionInformation> info = VersionInfo.getVersionInfo();
      Assert.assertTrue(info.size() > 0);
      VersionInfo.printVersionInfo();
      VersionInfo.main(null);
   }
   
   @Test
   public void testRunInThread() throws AssertionError, RuntimeException, InterruptedException {
      Collection<Runnable> toRun = new ArrayList<>(1);
      MyRun r = new MyRun();
      MyRun r2 = new MyRun();
      r2.re = new RuntimeException();
      toRun.add(r);
      toRun.add(r2);
      try {
         ThreadTester.testInThread(toRun);
         Assert.fail();
      } catch (RuntimeException ex) {
         //expected
      }
      r.re = new RuntimeException("forced exception");
      try {
         ThreadTester.testInThread(r);
         Assert.fail();
      } catch (RuntimeException ex) {
         //expected
      }
   }
   
   private static class MyRun implements Runnable {
         private Map<String, VersionInfo.VersionInformation> info;
         private RuntimeException re;

         @Override
         public void run() {
            try {
               info = VersionInfo.getVersionInfo();
               if (re!=null) {
                  throw re;
               }
            } catch (IOException ex) {
               Assert.fail(ex.getMessage());
            }
         }
   }
   
   @Test
   public void testGenericParamTyping() {
      GenericInterface l1 = new DirectSub<>();
      GenericInterface l2 = new Level2Sub<>();
      GenericInterface l3 = new Level3Sub();
      
      Assert.assertEquals(Integer.class,ClassHelper.findParameterClasses(l1.getClass(), GenericInterface.class).get(2));
      
      /*
       * we cannot resolve these (yet) because the actual class of these parameters are not known 
       * in the declaration of the classes, only on the instance
       */
      Assert.assertEquals(null,ClassHelper.findParameterClasses(l1.getClass(), GenericInterface.class).get(0));
      Assert.assertEquals(null,ClassHelper.findParameterClasses(l2.getClass(), GenericInterface.class).get(1));

      Assert.assertEquals(Integer.class, ClassHelper.findParameterClasses(DirectSub.class, GenericInterface.class).get(2));
      Assert.assertEquals(Long.class, ClassHelper.findParameterClasses(Level2Sub.class, GenericInterface.class).get(0));
      
      Assert.assertEquals(Long.class, ClassHelper.findParameterClasses(Level3Sub.class, GenericInterface.class).get(0));
      Assert.assertEquals(Long.class, ClassHelper.findParameterClass(0,Level3Sub.class, GenericInterface.class));
      Assert.assertEquals(Float.class, ClassHelper.findParameterClasses(Level3Sub.class, GenericInterface.class).get(1));
      Assert.assertEquals(Integer.class, ClassHelper.findParameterClasses(Level3Sub.class, GenericInterface.class).get(2));
   }
   
   @Test
   public void testClassOfType() {
      GenericInterface l1 = new DirectSub<>();
      GenericInterface l2 = new Level2Sub();
      GenericInterface l3 = new Level3Sub();
      
      Assert.assertEquals(Integer.class, ClassHelper.getClass(
          ( (ParameterizedType) DirectSub.class.getGenericInterfaces()[0] )
          .getActualTypeArguments()[2]));
      
      Assert.assertNull(ClassHelper.getClass(
          ( (ParameterizedType) DirectSub.class.getGenericInterfaces()[0] )
          .getActualTypeArguments()[1]));

      Assert.assertEquals(Float[].class, ClassHelper.getClass(
          ( (ParameterizedType) ArraySub.class.getGenericSuperclass() )
          .getActualTypeArguments()[0]));

      Assert.assertEquals(Level2Sub.class, ClassHelper.getClass(
          ArraySub.class.getGenericSuperclass()));
            
   }
   
   private class DirectSub<P1,P2> implements GenericInterface<P1, P2, Integer> {
      
   }
   // deliberately switch parameter position
   private class Level2Sub<P1> extends DirectSub<Long, P1>{
      
   }
   private class Level3Sub extends Level2Sub<Float>{
      
   }
   private class ArraySub extends Level2Sub<Float[]>{
      
   }
   

}
