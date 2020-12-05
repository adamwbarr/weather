package co.abarr.weather.math;

/**
 * A number in the range [0,1].
 * <p>
 * Created by adam on 04/12/2020.
 */
public class Probability extends Number {
    private final double value;

    private Probability(double value) {
        this.value = value;
        if (Double.isNaN(value) || value < 0 || value > 1) {
            throw new IllegalArgumentException("Invalid probability: " + this);
        }
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Probability that = (Probability) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public String toString() {
        return String.format("%.2f", value);
    }

    /**
     * Creates a new probability object.
     * <p>
     * An exception will be thrown outside of the range [0,1].
     */
    public static Probability of(double value) {
        return new Probability(value);
    }
}
