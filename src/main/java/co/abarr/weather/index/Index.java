package co.abarr.weather.index;

/**
 * A (unitless) index value.
 * <p>
 * Created by adam on 01/12/2020.
 */
public final class Index extends Number {
    private final float value;

    private Index(float value) {
        this.value = value;
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException("Invalid index: " + this);
        }
    }

    /**
     * The index as a 16-bit integer.
     */
    @Override
    public int intValue() {
        return (int) value;
    }

    /**
     * The index as an 32-bit long.
     */
    @Override
    public long longValue() {
        return (long) value;
    }

    /**
     * The index as an 16-bit float.
     */
    @Override
    public float floatValue() {
        return value;
    }

    /**
     * The index as a 32-bit double.
     */
    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return Float.compare(index.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }

    /**
     * Creates a new index value.
     * <p>
     * An exception will be thrown if the value is NaN or infinite, or the
     * units are null.
     */
    public static Index of(double value) {
        return of((float) value);
    }


    /**
     * Creates a new index value.
     * <p>
     * An exception will be thrown if the value is NaN or infinite, or the
     * units are null.
     */
    public static Index of(float value) {
        return new Index(value);
    }
}
