package com.vectorprint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

public class ClassHelperTest {

    interface I1<T> {}
    interface I2<T> extends I1<T> {}
    static class C1<T> implements I2<T> {}
    static class C2 extends C1<String> {}

    @Test
    public void testNestedInterfaces() {
        List<Class<?>> params = ClassHelper.findParameterClasses(C2.class, I1.class);
        Assertions.assertNotNull(params);
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals(String.class, params.get(0));
    }

    interface Multi<A, B> {}
    static class MultiImpl<T> implements Multi<Integer, T> {}
    static class MultiSub extends MultiImpl<String> {}

    @Test
    public void testMultiParams() {
        List<Class<?>> params = ClassHelper.findParameterClasses(MultiSub.class, Multi.class);
        Assertions.assertNotNull(params);
        Assertions.assertEquals(2, params.size());
        Assertions.assertEquals(Integer.class, params.get(0));
        Assertions.assertEquals(String.class, params.get(1));
    }

    interface Deep<T> {}
    interface Mid<T> extends Deep<T> {}
    static class Base<T> implements Mid<T> {}
    static class Sub extends Base<Long> {}

    @Test
    public void testDeepHierarchy() {
        List<Class<?>> params = ClassHelper.findParameterClasses(Sub.class, Deep.class);
        Assertions.assertNotNull(params);
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals(Long.class, params.get(0));
    }

    interface IOther<T> {}
    static class COther<T> implements IOther<T>, I1<T> {}
    static class SOther extends COther<Double> {}

    @Test
    public void testMultipleInterfaces() {
        List<Class<?>> params = ClassHelper.findParameterClasses(SOther.class, I1.class);
        Assertions.assertNotNull(params, "Should find parameters for I1");
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals(Double.class, params.get(0));
    }

    interface Unrelated<T> {}
    static class UnrelatedImpl extends C2 implements Unrelated<Short> {}

    @Test
    public void testComplexHierarchy() {
        List<Class<?>> params = ClassHelper.findParameterClasses(UnrelatedImpl.class, I1.class);
        Assertions.assertNotNull(params, "Should find parameters for I1");
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals(String.class, params.get(0));
    }

    interface IBase<T> {}
    static class BaseC<T> implements IBase<T> {}
    static class IntermediateC<T> extends BaseC<T> {}
    static class FinalC extends IntermediateC<Boolean> {}

    @Test
    public void testIntermediateTypeVariable() {
        List<Class<?>> params = ClassHelper.findParameterClasses(FinalC.class, IBase.class);
        Assertions.assertNotNull(params);
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals(Boolean.class, params.get(0));
    }

    interface GenericWithArray<T> {}
    static class ArrayImpl extends BaseC<String[]> implements GenericWithArray<Integer> {}

    @Test
    public void testArrayType() {
        List<Class<?>> params = ClassHelper.findParameterClasses(ArrayImpl.class, IBase.class);
        Assertions.assertNotNull(params);
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals(String[].class, params.get(0));
    }

    interface IGenericArray<T> {}
    static class GenericArrayBase<T> implements IGenericArray<T[]> {}
    static class GenericArraySub extends GenericArrayBase<String> {}

    @Test
    public void testGenericArrayResolution() {
        List<Class<?>> params = ClassHelper.findParameterClasses(GenericArraySub.class, IGenericArray.class);
        Assertions.assertNotNull(params);
        Assertions.assertEquals(1, params.size());
        Assertions.assertEquals(String[].class, params.get(0), "Should resolve generic array T[] to String[]");
    }

    @Test
    public void testFindParameterClassSingular() {
        Class<?> param = ClassHelper.findParameterClass(0, GenericArraySub.class, IGenericArray.class);
        Assertions.assertEquals(String[].class, param);

        // Test NPE fix/safety
        param = ClassHelper.findParameterClass(0, String.class, String.class);
        Assertions.assertNull(param);
    }
}
