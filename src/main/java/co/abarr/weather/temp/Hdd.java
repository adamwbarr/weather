package co.abarr.weather.temp;

import java.util.Objects;

/**
 * Created by adam on 02/12/2020.
 */
class Hdd implements TempIndexer {
    private final Temp reference;

    public Hdd(Temp reference) {
        this.reference = Objects.requireNonNull(reference);
    }

    @Override
    public Temp indexFor(TempSeries series) {
        Temp index = Temp.zero(reference.units());
        for (TempSeries.Entry entry : series) {
            if (entry.temp().compareTo(reference) < 0) {
                index = index.plus(reference.minus(entry.temp()));
            }
        }
        return index;
    }
}
