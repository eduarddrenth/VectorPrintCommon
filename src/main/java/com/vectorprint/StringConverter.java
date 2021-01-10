
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

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Turn a String into a value of another type
 *
 * @author Eduard Drenth at VectorPrint.nl
 * @param <T>
 */
public interface StringConverter<T> {

    T convert(String val);

    public static class FloatParser implements StringConverter<Float> {

        @Override
        public Float convert(String val) {
            return Float.valueOf(val);
        }
    }

    public static class LongParser implements StringConverter<Long> {

        @Override
        public Long convert(String val) {
            return Long.valueOf(val);
        }
    }

    public static class DoubleParser implements StringConverter<Double> {

        @Override
        public Double convert(String val) {
            return Double.valueOf(val);
        }
    }

    public static class BigDecimalParser implements StringConverter<BigDecimal> {

        @Override
        public BigDecimal convert(String val) {
            return new BigDecimal(val);
        }
    }

    public static class BigIntegerParser implements StringConverter<BigInteger> {

        @Override
        public BigInteger convert(String val) {
            return new BigInteger(val);
        }
    }

    public static class BooleanParser implements StringConverter<Boolean> {

        @Override
        public Boolean convert(String val) {
            return Boolean.valueOf(val);
        }
    }

    /**
     * next to decoding supports using color names like red
     */
    public static class ColorParser implements StringConverter<Color> {

        @Override
        public Color convert(String value) {
            if (value.indexOf('#') == 0) {
                return Color.decode(value);
            } else {
                Field f;
                try {
                    // assume name
                    f = Color.class.getField(value);
                } catch (NoSuchFieldException | SecurityException ex) {
                    throw new VectorPrintRuntimeException(ex);
                }
                try {
                    return (Color) f.get(null);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new VectorPrintRuntimeException(ex);
                }
            }
        }
    }

    /**
     * tries to construct a URL from a String. When a MalformedURLException is
     * thrown and a File exists the URL is created via new File.
     */
    public static class URLParser implements StringConverter<URL> {

        @Override
        public URL convert(String val) {
            try {
                return new URL(val);
            } catch (MalformedURLException ex) {
                File file = new File(val);
                if (file.exists()) {
                    try {
                        return file.toURI().toURL();
                    } catch (MalformedURLException ex1) {
                        throw new VectorPrintRuntimeException(ex);
                    }
                }
                throw new VectorPrintRuntimeException("file " + val + " does not exist, unable to construct url", ex);
            }
        }
    }

    /**
     * Constructs a File from a String.
     */
    public static class FileParser implements StringConverter<File> {

        @Override
        public File convert(String val) {
            return new File(val);
        }
    }

    public static class ClassParser implements StringConverter<Class> {

        @Override
        public Class convert(String val) {
            try {
                return Class.forName(val);
            } catch (ClassNotFoundException ex) {
                throw new VectorPrintRuntimeException(ex);
            }
        }

    }

    public static class IntParser implements StringConverter<Integer> {

        @Override
        public Integer convert(String val) {
            return Integer.valueOf(val);
        }
    }

    public static class CharParser implements StringConverter<Character> {

        @Override
        public Character convert(String val) {
            if (val == null || val.length() == 0) {
                return null;
            } else if (val.length() > 1) {
                throw new VectorPrintRuntimeException(String.format("cannot turn %s into one Character", val));
            }
            return val.charAt(0);
        }
    }

    public static class ShortParser implements StringConverter<Short> {

        @Override
        public Short convert(String val) {
            return Short.valueOf(val);
        }
    }

    public static class ByteParser implements StringConverter<Byte> {

        @Override
        public Byte convert(String val) {
            return Byte.decode(val);
        }
    }

    /**
     * uses {@link DateFormat#getInstance() }
     */
    public static class DateParser implements StringConverter<Date> {

        @Override
        public Date convert(String val) {
            try {
                return DateFormat.getInstance().parse(val);
            } catch (java.text.ParseException ex) {
                throw new VectorPrintRuntimeException(ex);
            }
        }
    }

    public static class LocalDateTimeParser implements StringConverter<LocalDateTime> {

        @Override
        public LocalDateTime convert(String val) {
            return LocalDateTime.parse(val);
        }
    }

    public static class RegexParser implements StringConverter<Pattern> {

        @Override
        public Pattern convert(String val) {
            return Pattern.compile(val);
        }
    }
    public static final IntParser INT_PARSER = new IntParser();
    public static final CharParser CHAR_PARSER = new CharParser();
    public static final ShortParser SHORT_PARSER = new ShortParser();
    public static final ByteParser BYTE_PARSER = new ByteParser();
    public static final LongParser LONG_PARSER = new LongParser();
    public static final FloatParser FLOAT_PARSER = new FloatParser();
    public static final DoubleParser DOUBLE_PARSER = new DoubleParser();
    public static final BigIntegerParser BIG_INTEGER_PARSER = new BigIntegerParser();
    public static final BigDecimalParser BIG_DECIMAL_PARSER = new BigDecimalParser();
    public static final URLParser URL_PARSER = new URLParser();
    public static final FileParser FILE_PARSER = new FileParser();
    public static final ClassParser CLASS_PARSER = new ClassParser();
    public static final BooleanParser BOOLEAN_PARSER = new BooleanParser();
    public static final ColorParser COLOR_PARSER = new ColorParser();
    public static final DateParser DATE_PARSER = new DateParser();
    public static final LocalDateTimeParser LOCAL_DATE_TIME_PARSER = new LocalDateTimeParser();
    public static final RegexParser REGEX_PARSER = new RegexParser();

    public static StringConverter forClass(Class clazz) {
        if (Integer.class.equals(clazz)) {
            return INT_PARSER;
        } else if (Character.class.equals(clazz)) {
            return CHAR_PARSER;
        } else if (Short.class.equals(clazz)) {
            return SHORT_PARSER;
        } else if (Byte.class.equals(clazz)) {
            return BYTE_PARSER;
        } else if (Long.class.equals(clazz)) {
            return LONG_PARSER;
        } else if (Float.class.equals(clazz)) {
            return FLOAT_PARSER;
        } else if (Double.class.equals(clazz)) {
            return DOUBLE_PARSER;
        } else if (BigInteger.class.equals(clazz)) {
            return BIG_INTEGER_PARSER;
        } else if (BigDecimal.class.equals(clazz)) {
            return BIG_DECIMAL_PARSER;
        } else if (URL.class.equals(clazz)) {
            return URL_PARSER;
        } else if (File.class.equals(clazz)) {
            return FILE_PARSER;
        } else if (Class.class.equals(clazz)) {
            return CLASS_PARSER;
        } else if (Boolean.class.equals(clazz)) {
            return BOOLEAN_PARSER;
        } else if (Color.class.equals(clazz)) {
            return COLOR_PARSER;
        } else if (Date.class.equals(clazz)) {
            return DATE_PARSER;
        } else if (LocalDateTime.class.equals(clazz)) {
            return LOCAL_DATE_TIME_PARSER;
        } else if (Pattern.class.equals(clazz)) {
            return REGEX_PARSER;
        }
        throw new IllegalArgumentException(clazz + " not supported");
    }
}
