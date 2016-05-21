/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vectorprint;

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
