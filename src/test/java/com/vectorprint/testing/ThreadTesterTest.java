package com.vectorprint.testing;

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
