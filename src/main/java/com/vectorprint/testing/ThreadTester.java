/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vectorprint.testing;

/*
 * #%L
 * VectorPrintCommon2.0
 * %%
 * Copyright (C) 2012 - 2013 VectorPrint
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import junit.framework.AssertionFailedError;

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
    * Runs the given Runnable in a Thread, waits for the Thread to finish and rethrows the first Exception caught during
    * the threads execution.
    *
    * @param toRun The Thread or Runnable to run
    * @throws AssertionError when a junit assertion fails in the thread
    * @throws AssertionFailedError when an
    * @throws RuntimeException when a RuntimeException occurred in the thread
    */
   public static void testInThread(Runnable toRun) throws AssertionError, RuntimeException, InterruptedException {
      Runner r = new Runner(toRun);
      r.start();
      r.check();
   }

   /**
    * Runs the given Runnables in a Thread, waits for the Threads to finish and rethrows the first Exception caught
    * during the threads execution. When an Exception is caught all other threads will be interrupted.
    *
    * @param toRun The Threads or Runnables to run
    * @throws AssertionError when a junit assertion fails in the thread
    * @throws AssertionFailedError when an
    * @throws RuntimeException when a RuntimeException occurred in the thread
    */
   public static void testInThread(Collection<Runnable> toRun) throws AssertionError, RuntimeException, InterruptedException {
      Set<Runner> runners = new HashSet<>(toRun.size());
       // start the threads
       toRun.stream().map((r) -> new Runner(r)).map((ru) -> {
           ru.start();
           return ru;
       }).forEachOrdered((ru) -> {
           runners.add(ru);
       });
      // handle for the first failing thread
      Runner failed = null;
      for (Runner r : runners) {
         if (failed != null) {
            // we've had a failure, kill the other threads
            r.interrupt();
         } else {
            try {
               // join the thread and check for failures after it has finished
               r.check();
            } catch (AssertionError | RuntimeException assertionError) {
               // remember the first failing thread
               failed = r;
            }
            // remember the first failing thread

         }
      }
      if (failed != null) {
         // all threads finished or interrupted, a failure has been caught, rethrow the Exception now
         failed.failIfNeeded();
      }
   }

   /**
    * private class that calls the run() method of its containing Runnable in a new Thread. It captures RuntimeException
    * and AssertionError.
    */
   private static class Runner extends Thread {

      private RuntimeException failure = null;
      private AssertionError fail = null;
      private final Runnable myRunnable;

      private Runner(Runnable toRun) {
         super();
         myRunnable = toRun;
      }

      @Override
      public void run() {
         try {
            myRunnable.run();
         } catch (RuntimeException ex) {
            failure = ex;
         } catch (AssertionError ex) {
            fail = ex;
         }
      }

      private void check() throws AssertionError, RuntimeException, InterruptedException {
         join();
         failIfNeeded();
      }

      private void failIfNeeded() throws AssertionError, RuntimeException, InterruptedException {
         if (failure != null) {
            throw failure;
         }
         if (fail != null) {
            throw fail;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         final Runner other = (Runner) obj;
         return other.getId() == getId();
      }

      @Override
      public int hashCode() {
         return 5;
      }
   }
}
