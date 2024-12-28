package com.vectorprint;

/*-
 * #%L
 * VectorPrintCommon
 * %%
 * Copyright (C) 2011 - 2021 E. Drenth Software
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


import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

public class StringConverterTest {

    @Test
    public void testConvert() {
        StringConverter.forClass(LocalDateTime.class)
                .convert("2021-02-12T00:59:10");
        StringConverter.forClass(Date.class)
                .convert("2024-12-23T13:07:47");
    }
}
