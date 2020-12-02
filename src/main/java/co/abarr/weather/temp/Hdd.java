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
    public Temp indexFor(Temp temp) {
        temp = temp.toUnitsOf(reference);
        if (temp.compareTo(reference) < 0) {
            return reference.minus(temp);
        } else {
            return Temp.zero(reference.units());
        }
    }
}
