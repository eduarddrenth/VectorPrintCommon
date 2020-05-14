
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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Fast NIO based helper
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class IOHelper {

   private IOHelper() {
   }

   /**
    * calls {@link #load(java.io.InputStream, java.io.OutputStream, int, boolean) } with 256000 and true.
    *
    * @param input
    * @param output
    * @throws IOException
    */
   public static <T extends OutputStream> T load(InputStream input, T output) throws IOException {
      return load(input, output, 256000, true);
   }

   /**
    *
    * @param input will be wrapped in a ReadableByteChannel
    * @param output will be wrapped in a WritableByteChannel
    * @param bufferSize
    * @param close
    * @throws IOException
    */
   public static <T extends OutputStream> T load(InputStream input, T output, int bufferSize, boolean close) throws IOException {
      ReadableByteChannel in = Channels.newChannel(input);
      WritableByteChannel out = Channels.newChannel(output);
      ByteBuffer b = ByteBuffer.allocate(bufferSize);
      try {
         int n;

         while (in.read(b) > 0) {
            b.flip();
            out.write(b);
            b.compact();
         }

      } finally {
         if (close) {
            in.close();
            out.close();
         }
      }
      return output;
   }

}
