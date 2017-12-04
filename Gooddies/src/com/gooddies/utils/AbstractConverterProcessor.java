package com.gooddies.utils;

import java.util.List;

/**
 * @author sad
 */
public abstract class AbstractConverterProcessor {

    public abstract Object convert(Object value);

    public static class StringToStringProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            return value;
        }
    }

    public static class StringToIntegerProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            String vstr = (String) value;
            return Integer.parseInt(vstr);
        }
    }

    public static class StringToBooleanProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            String vstr = (String) value;
            vstr = vstr.toLowerCase();
            Boolean result = null;
            if (vstr.isEmpty()) {
                result = false;
            } else if (vstr.equals("true")) {
                result = true;
            } else if (vstr.equals("false")) {
                result = false;
            } else if (vstr.equals("yes")) {
                result = true;
            } else if (vstr.equals("no")) {
                result = false;
            } else if (vstr.equals("1")) {
                result = true;
            } else if (vstr.equals("0")) {
                result = false;
            }
            return result;
        }
    }

    public static class StringToLongProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            String vstr = (String) value;
            if (vstr.isEmpty()) {
                return null;
            }
            return Long.parseLong(vstr);
        }
    }

    public static class StringToFloatProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            String vstr = (String) value;
            if (vstr.isEmpty()) {
                return null;
            }
            vstr = vstr.replace(',', '.');
            return Float.parseFloat(vstr);
        }
    }

    public static class StringToDoubleProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            String vstr = (String) value;
            if (vstr.isEmpty()) {
                return null;
            }
            vstr = vstr.replace(',', '.');
            return Double.parseDouble(vstr);
        }
    }

    public static class DoubleToIntegerProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            Double dbl = (Double) value;
            return (int) (double) dbl;
        }
    }
    
    public static class DoubleToDoubleProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            Double dbl = (Double) value;
            return dbl;
        }
    }
    
    

    public static class DoubleToFloatProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            Double dbl = (Double) value;
            return (float) (double) dbl;
        }
    }

    public static class DoubleToStringProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            Double dbl = (Double) value;
            return dbl.toString();
        }
    }

    public static class ListToArrayProcessor extends AbstractConverterProcessor {

        @Override
        public Object convert(Object value) {
            List dbl = (List) value;
            return dbl.toArray();
        }
    }
}
