package co.abarr.weather.temp;

import co.abarr.weather.math.Fraction;
import co.abarr.weather.math.Probability;

import java.util.*;
import java.util.function.Supplier;

/**
 * A collection of temperatures.
 * <p>
 * Created by adam on 04/12/2020.
 */
public class TempBag {
    private final double[] temps;
    private final TempUnits units;

    private TempBag(double[] temps, TempUnits units) {
        this.temps = temps;
        this.units = units;
    }

    /**
     * Where the supplied temperature fits in the distribution.
     */
    public Fraction quantileOf(Temp temp) {
        double value = temp.to(units).doubleValue();
        int index = Arrays.binarySearch(temps, value);
        if (index < 0) {
            index = -index - 1;
        }
        return Fraction.of(index / (double) temps.length);
    }

    /**
     * A quantile of the distribution.
     */
    public Temp quantile(Fraction quantile) {
        double temp;
        double index = quantile.doubleValue() * (temps.length - 1);
        if (index >= temps.length - 1) {
            temp = temps[temps.length - 1];
        } else {
            double temp0 = temps[(int) index];
            double temp1 = temps[(int) index + 1];
            temp = temp0 + (temp1 - temp0) * (index % 1);
        }
        return Temp.of(temp, units);
    }

    /**
     * Quantiles of the distribution.
     */
    public Map<Fraction, Temp> quantiles(Fraction... quantiles) {
        return quantiles(Arrays.asList(quantiles));
    }

    /**
     * Quantiles of the distribution.
     */
    public Map<Fraction, Temp> quantiles(Iterable<Fraction> quantiles) {
        Map<Fraction, Temp> result = new TreeMap<>();
        for (Fraction quantile : quantiles) {
            result.put(quantile, quantile(quantile));
        }
        return result;
    }

    /**
     * The probability of temperatures at or above some threshold.
     */
    public Probability pMoreThanOrEqualTo(Temp temp) {
        return Probability.of(1 - quantileOf(temp).doubleValue());
    }

    /**
     * The sum of the temperatures.
     */
    public Temp sum() {
        double sum = 0;
        for (int i = 0; i < temps.length; i++) {
            sum += temps[i];
        }
        return Temp.of(sum, units);
    }

    /**
     * The mean temperature, if there is one.
     */
    public Optional<Temp> mean() {
        if (temps.length == 0) {
            return Optional.empty();
        } else {
            return Optional.of(sum().divideBy(temps.length));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TempBag distribution = (TempBag) o;
        return Arrays.equals(temps, distribution.temps) && units == distribution.units;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(units) + Arrays.hashCode(temps);
    }

    /**
     * A bag containing no temperatures.
     */
    public static TempBag empty() {
        return of();
    }

    /**
     * Creates a bag from a generator function.
     */
    public static TempBag generate(int n, Supplier<Temp> generator) {
        List<Temp> temps = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            temps.add(generator.get());
        }
        return of(temps);
    }

    /**
     * Creates a bag from the supplied temperatures.
     */
    public static TempBag of(Temp... temps) {
        return of(Arrays.asList(temps));
    }

    /**
     * Creates a bag containing the supplied temperatures.
     */
    public static TempBag of(Iterable<Temp> temps) {
        List<Temp> list = new ArrayList<>();
        TempUnits units = unitsFor(temps);
        for (Temp temp : temps) {
            list.add(temp.to(units));
        }
        double[] array = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).doubleValue();
        }
        Arrays.sort(array);
        return new TempBag(array, units);
    }

    private static TempUnits unitsFor(Iterable<Temp> temps) {
        Iterator<Temp> iterator = temps.iterator();
        if (iterator.hasNext()) {
            return iterator.next().units();
        } else {
            return TempUnits.KELVIN;
        }
    }
}
