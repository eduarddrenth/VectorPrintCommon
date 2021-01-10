package com.vectorprint;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

public class StringConverterTest {

    @Test
    public void testConvert() {
        StringConverter.forClass(LocalDateTime.class)
                .convert("2021-02-12T00:59:10");
    }
}
