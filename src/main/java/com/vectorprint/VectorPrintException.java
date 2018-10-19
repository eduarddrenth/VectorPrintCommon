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

import java.io.PrintStream;
import java.io.PrintWriter;

public class VectorPrintException extends Exception {

   public VectorPrintException(Throwable cause) {
      super(cause);
   }

   public VectorPrintException(String message, Throwable cause) {
      super(message, cause);
   }

   public VectorPrintException(String message) {
      super(message);
   }

   /**
    * print th e stacktace of the deepest cause found
    */
   @Override
   public void printStackTrace() {
      printStackTrace(System.out);
   }

   /**
    * print the stacktace of the deepest cause found
    */
   @Override
   public void printStackTrace(PrintStream s) {
      printStackTrace(new PrintWriter(s, true));
   }

   /**
    * print the stacktace of the deepest cause found
    */
   @Override
   public void printStackTrace(PrintWriter s) {
      Throwable current = this;
      while (current.getCause() != null) {
         current = current.getCause();
      }
      if (current.equals(this)) {
         super.printStackTrace(s);
      } else {
         current.printStackTrace(s);
      }
   }
}
