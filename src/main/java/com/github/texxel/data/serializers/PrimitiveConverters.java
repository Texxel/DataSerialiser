package com.github.texxel.data.serializers;

import com.github.texxel.data.DataConverter;
import com.github.texxel.data.DataIn;
import com.github.texxel.data.DataOut;

public class PrimitiveConverters {

    public static class IntegerConverter implements DataConverter<Integer> {
        @Override
        public void serialize (Integer obj, DataOut bundle) {
            bundle.write("value", obj.intValue());
        }

        @Override
        public Integer create (DataIn bundle, Class<? extends Integer> stored) {
            return Integer.valueOf(bundle.readInt("value"));
        }

        @Override
        public void initialise (DataIn bundle, Integer obj) {
        }
    }

    public static class LongConverter implements DataConverter<Long> {
        @Override
        public void serialize (Long obj, DataOut bundle) {
            bundle.write("value", obj.longValue());
        }

        @Override
        public Long create (DataIn bundle, Class<? extends Long> stored) {
            return bundle.readLong("value");
        }

        @Override
        public void initialise (DataIn bundle, Long obj) {
        }
    }

    public static class FloatConverter implements DataConverter<Float> {
        @Override
        public void serialize (Float obj, DataOut bundle) {
            bundle.write("value", obj.floatValue());
        }

        @Override
        public Float create (DataIn bundle, Class<? extends Float> stored) {
            return bundle.readFloat("value");
        }

        @Override
        public void initialise (DataIn bundle, Float obj) {
        }
    }

    public static class DoubleConverter implements DataConverter<Double> {
        @Override
        public void serialize (Double obj, DataOut bundle) {
            bundle.write("value", obj.doubleValue());
        }

        @Override
        public Double create (DataIn bundle, Class<? extends Double> stored) {
            return bundle.readDouble("value");
        }

        @Override
        public void initialise (DataIn bundle, Double obj) {
        }
    }

    public static class CharConverter implements DataConverter<Character> {
        @Override
        public void serialize (Character obj, DataOut bundle) {
            bundle.write("value", obj.charValue());
        }

        @Override
        public Character create (DataIn bundle, Class<? extends Character> stored) {
            return (char)bundle.readInt("value");
        }

        @Override
        public void initialise (DataIn bundle, Character obj) {
        }
    }

    public static class ShortConverter implements DataConverter<Short> {
        @Override
        public void serialize (Short obj, DataOut bundle) {
            bundle.write("value", obj.shortValue());
        }

        @Override
        public Short create (DataIn bundle, Class<? extends Short> stored) {
            return (short)bundle.readInt("value");
        }

        @Override
        public void initialise (DataIn bundle, Short obj) {
        }
    }

    public static class ByteConverter implements DataConverter<Byte> {
        @Override
        public void serialize (Byte obj, DataOut bundle) {
            bundle.write("value", obj.byteValue());
        }

        @Override
        public Byte create (DataIn bundle, Class<? extends Byte> stored) {
            return (byte)bundle.readInt("value");
        }

        @Override
        public void initialise (DataIn bundle, Byte obj) {
        }
    }

    public static class BooleanConverter implements DataConverter<Boolean> {
        @Override
        public void serialize (Boolean obj, DataOut bundle) {
            bundle.write("value", obj.booleanValue());
        }

        @Override
        public Boolean create (DataIn bundle, Class<? extends Boolean> stored) {
            return bundle.readBoolean("value");
        }

        @Override
        public void initialise (DataIn bundle, Boolean obj) {
        }
    }
}
