/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vectorprint;

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
   public static void load(InputStream input, OutputStream output) throws IOException {
      load(input, output, 256000, true);
   }

   /**
    *
    * @param input will be wrapped in a ReadableByteChannel
    * @param output will be wrapped in a WritableByteChannel
    * @param bufferSize
    * @param close
    * @throws IOException
    */
   public static void load(InputStream input, OutputStream output, int bufferSize, boolean close) throws IOException {
      ReadableByteChannel in = Channels.newChannel(input);
      WritableByteChannel out = Channels.newChannel(output);
      ByteBuffer b = ByteBuffer.allocate(bufferSize);
      try {
         int n;

         while ((n = in.read(b)) > 0) {
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
   }

}
