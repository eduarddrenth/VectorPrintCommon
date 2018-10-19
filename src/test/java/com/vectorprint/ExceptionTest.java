

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


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ExceptionTest {
   
   @Test
   public void testException() {
      Exception deepest = new Exception();
      Exception nested = new Exception(deepest);
      Exception vpe = new VectorPrintException(nested);
      Exception vpr = new VectorPrintRuntimeException(nested);

      ByteArrayOutputStream deep = new ByteArrayOutputStream();
      ByteArrayOutputStream wrapper = new ByteArrayOutputStream();

      try {
         throw vpe;
      } catch (Exception e) {
         PrintStream p = new PrintStream(wrapper, true);
         e.printStackTrace(p);
         p.close();
         
         p = new PrintStream(deep, true);
         deepest.printStackTrace(p);
         p.close();
         
         Assert.assertEquals(deep.toString(),wrapper.toString());
      }

   }

   @Test
   public void testRuntimeException() {
      Exception deepest = new Exception();
      Exception nested = new Exception(deepest);
      Exception vpr = new VectorPrintRuntimeException(nested);

      ByteArrayOutputStream deep = new ByteArrayOutputStream();
      ByteArrayOutputStream wrapper = new ByteArrayOutputStream();

      try {
         throw vpr;
      } catch (Exception e) {
         PrintStream p = new PrintStream(wrapper, true);
         e.printStackTrace(p);
         p.close();
         
         p = new PrintStream(deep, true);
         deepest.printStackTrace(p);
         p.close();
         
         Assert.assertEquals(deep.toString(),wrapper.toString());
      }

   }
}
