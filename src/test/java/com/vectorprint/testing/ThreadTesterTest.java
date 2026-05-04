package com.vectorprint.testing;

/*-
 * #%L
 * VectorPrintCommon
 * %%
 * Copyright (C) 2011 - 2026 E. Drenth Software
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadTesterTest {

    @Test
    public void testSingleThreadSuccess() throws Throwable {
        AtomicInteger count = new AtomicInteger(0);
        ThreadTester.testInThread(() -> count.incrementAndGet());
        Assertions.assertEquals(1, count.get());
    }

    @Test
    public void testSingleThreadAssertionError() {
        Assertions.assertThrows(AssertionError.class, () -> {
            ThreadTester.testInThread(() -> Assertions.fail("forced failure"));
        });
    }

    @Test
    public void testSingleThreadRuntimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            ThreadTester.testInThread(() -> {
                throw new RuntimeException("forced exception");
            });
        });
    }

    @Test
    public void testMultiThreadSuccess() throws Throwable {
        AtomicInteger count = new AtomicInteger(0);
        List<Runnable> runnables = Arrays.asList(
            () -> count.incrementAndGet(),
            () -> count.incrementAndGet(),
            () -> count.incrementAndGet()
        );
        ThreadTester.testInThread(runnables);
        Assertions.assertEquals(3, count.get());
    }

    @Test
    public void testMultiThreadFailure() {
        List<Runnable> runnables = Arrays.asList(
            () -> { try { Thread.sleep(100); } catch (InterruptedException e) {} },
            () -> { throw new RuntimeException("fail"); },
            () -> { try { Thread.sleep(100); } catch (InterruptedException e) {} }
        );
        Assertions.assertThrows(RuntimeException.class, () -> {
            ThreadTester.testInThread(runnables);
        });
    }
    @Test
    public void testSingleThreadError() {
        Assertions.assertThrows(Error.class, () -> {
            ThreadTester.testInThread(() -> {
                throw new Error("forced error");
            });
        });
    }
}
