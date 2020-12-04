package co.abarr.weather.temp;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A keyed list of temperatures.
 * <p>
 * A vector is guaranteed to contain at most one entry per key, and all
 * temperatures in the vector are guaranteed to have the same units.
 * <p>
 * Created by adam on 01/12/2020.
 */
public final class TempVector<K> extends AbstractList<TempVector.Entry<K>> {
    private final List<K> keys;
    private final double[] values;
    private final TempUnits units;

    private TempVector(List<K> keys, double[] values, TempUnits units) {
        this.keys = keys;
        this.values = values;
        this.units = units;
    }

    /**
     * The entry at the supplied index.
     */
    @Override
    public Entry<K> get(int index) {
        return entry(keys.get(index), tempAt(index));
    }

    /**
     * The number of entries in the vector.
     */
    @Override
    public int size() {
        return keys.size();
    }

    /**
     * Groups this vector into sub-vector by function.
     */
    public <G> Map<G, TempVector<K>> groupBy(Function<K, G> grouper) {
        Map<G, List<Entry<K>>> lists = new LinkedHashMap<>();
        for (Entry<K> entry : this) {
            G group = grouper.apply(entry.key);
            lists.computeIfAbsent(group, key -> new ArrayList<>()).add(entry);
        }
        Map<G, TempVector<K>> groups = new LinkedHashMap<>();
        for (Map.Entry<G, List<Entry<K>>> entry : lists.entrySet()) {
            groups.put(entry.getKey(), of(entry.getValue()));
        }
        return groups;
    }

    /**
     * Transforms the entries in this vector.
     * <p>
     * The resulting vector will contain for the same dates as this one.
     */
    public TempVector<K> map(BiFunction<K, Temp, Temp> transform) {
        double[] mapped = new double[size()];
        for (int i = 0; i < size(); i++) {
            K key = keys.get(i);
            Temp temp = tempAt(i);
            mapped[i] = transform.apply(key, temp).to(units).doubleValue();
        }
        return new TempVector<>(keys, mapped, units);
    }

    /**
     * Sorts the the vector according to the natural order of the keys.
     */
    public TempVector<K> sortKeys() {
        List<Entry<K>> entries = new ArrayList<>(this);
        entries.sort(Comparator.comparing(entry -> (Comparable) entry.key));
        return of(entries);
    }

    /**
     * The sum of the vector.
     */
    public Temp sum() {
        double sum = 0;
        for (int i = 0; i < size(); i++) {
            sum += values[i];
        }
        return Temp.of(sum, units);
    }

    /**
     * The mean of the vector, if there is one.
     */
    public Optional<Temp> mean() {
        if (isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(sum().divideBy(size()));
        }
    }

    /**
     * The quadratic variation of the vector.
     * <p>
     * No result will be returned if there are fewer than two entries in the vector.
     */
    public Optional<Temp> qvar() {
        if (size() < 2) {
            return Optional.empty();
        } else {
            double sum = 0;
            for (int i = 1; i < size(); i++) {
                sum += Math.pow(values[i] - values[i - 1], 2);
            }
            return Optional.of(Temp.of(sum, units));
        }
    }

    private Temp tempAt(int index) {
        return Temp.of(values[index], units);
    }

    /**
     * Creates a new entry.
     * <p>
     * An exception will be thrown if the key or temperature are null.
     */
    public static <K> Entry<K> entry(K key, Temp temp) {
        return new Entry<>(key, temp);
    }

    /**
     * A key and its associated temperature.
     */
    public static final class Entry<K> {
        private final K key;
        private final Temp temp;

        private Entry(K key, Temp temp) {
            this.key = Objects.requireNonNull(key);
            this.temp = Objects.requireNonNull(temp);
        }

        /**
         * The key with which the temperature is associated.
         */
        public K key() {
            return key;
        }

        /**
         * The temperature associated with the key.
         */
        public Temp temp() {
            return temp;
        }

        /**
         * Updates the temperature for the key.
         */
        public Entry<K> temp(Temp temp) {
            return new Entry<>(key, temp);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry<?> entry = (Entry<?>) o;
            return key.equals(entry.key) && temp.equals(entry.temp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, temp);
        }

        @Override
        public String toString() {
            return key + "=" + temp;
        }
    }

    /**
     * Creates a vector containing no entries.
     */
    public static <K> TempVector<K> empty() {
        return of();
    }

    /**
     * Creates a vector from a factory function.
     */
    public static <K> TempVector<K> of(Iterable<K> keys, Function<K, Temp> temp) {
        return of(keys, key -> key, temp::apply);
    }

    /**
     * Creates a vector from a factory function.
     */
    public static <K, T> TempVector<K> of(Iterable<T> items, Function<T, K> key, Function<T, Temp> temp) {
        Map<K, Temp> map = new LinkedHashMap<>();
        for (T item : items) {
            map.put(key.apply(item), temp.apply(item));
        }
        return of(map);
    }

    /**
     * Creates a vector containing the supplied entries.
     */
    @SafeVarargs
    public static <K> TempVector<K> of(Entry<K>... entries) {
        return of(Arrays.asList(entries));
    }

    /**
     * Creates a vector containing the supplied entries.
     */
    public static <K> TempVector<K> of(Iterable<? extends Entry<K>> entries) {
        Map<K, Temp> map = new LinkedHashMap<>();
        for (Entry<K> entry : entries) {
            map.put(entry.key, entry.temp());
        }
        return of(map);
    }

    /**
     * Creates a vector containing the supplied temperatures.
     */
    public static <K> TempVector<K> of(Map<K, Temp> map) {
        List<K> keys = new ArrayList<>(map.size());
        keys.addAll(map.keySet());
        TempUnits units;
        if (keys.isEmpty()) {
            units = TempUnits.KELVIN;
        } else {
            units = map.get(keys.get(0)).units();
        }
        double[] temps = new double[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            temps[i] = map.get(keys.get(i)).to(units).doubleValue();
        }
        return new TempVector<>(keys, temps, units);
    }
}
