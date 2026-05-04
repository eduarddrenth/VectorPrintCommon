
package com.vectorprint.testing;

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


import org.opentest4j.AssertionFailedError;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that supports doing assertion in a Thread. Just do
 * <pre>
 * ThreadTester.testInThread(new Runnable() {
 *        &#64;Override
 *        public void run() {
 *                Assert.assertTrue(&lt;your condition&gt;);
 *        }
 * });
 * </pre>
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ThreadTester {

   /**
    * Runs the given Runnable in a Thread, waits for the Thread to finish and rethrows the first Throwable caught during
    * the threads execution.
    *
    * @param toRun The Thread or Runnable to run
    * @throws Throwable when something went wrong in the thread
    */
   public static void testInThread(Runnable toRun) throws Throwable {
      if (toRun == null) {
         return;
      }
      Runner r = new Runner(toRun);
      r.start();
      r.check();
   }

   /**
    * Runs the given Runnables in a Thread, waits for the Threads to finish and rethrows the first Throwable caught
    * during the threads execution. When a Throwable is caught all other threads will be interrupted.
    *
    * @param toRun The Threads or Runnables to run
    * @throws Throwable when something went wrong in one of the threads
    */
   public static void testInThread(Collection<Runnable> toRun) throws Throwable {
      int size = (toRun == null) ? 0 : toRun.size();
      Set<Runner> runners = new HashSet<>(size);
      if (toRun != null) {
         for (Runnable r : toRun) {
            Runner runner = new Runner(r);
            runners.add(runner);
            runner.start();
         }
      }
      // handle for the first failing thread
      Throwable failed = null;
      for (Runner r : runners) {
         try {
            r.check();
         } catch (Throwable t) {
            if (failed == null) {
               failed = t;
               // interrupt remaining
               for (Runner other : runners) {
                  other.interrupt();
               }
            }
         }
      }
      if (failed != null) {
         throw failed;
      }
   }

   /**
    * private class that calls the run() method of its containing Runnable in a new Thread. It captures all Throwable.
    */
   private static class Runner extends Thread {

      private Throwable failure = null;
      private final Runnable myRunnable;

      private Runner(Runnable toRun) {
         super();
         myRunnable = toRun;
      }

      @Override
      public void run() {
         try {
            myRunnable.run();
         } catch (Throwable ex) {
            failure = ex;
         }
      }

      private void check() throws Throwable {
         try {
            join();
         } catch (InterruptedException e) {
            interrupt();
            throw e;
         }
         if (failure != null) {
            throw failure;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }
         if (!(obj instanceof Runner)) {
            return false;
         }
         final Runner other = (Runner) obj;
         return other.getId() == getId();
      }

      @Override
      public int hashCode() {
         return (int) (getId() ^ (getId() >>> 32));
      }
   }
}
