package co.abarr.weather.temp;

/**
 * Pluggable logic for calculating temperature index values.
 * <p>
 * Created by adam on 02/12/2020.
 */
public interface TempIndexer {
    /**
     * Calculates the index value for a given temperature.
     */
    Temp indexFor(TempSeries series);

    /**
     * Calculates a HDD (heating-degree-day) index.
     * <p>
     * For a given temperature the HDD is the amount it is below the reference
     * temperature (in units of the reference temp).
     */
    static TempIndexer hdd(Temp reference) {
        return new Hdd(reference);
    }

    /**
     * Standard US HDD index.
     */
    TempIndexer HDD_65 = hdd(Temp.fahrenheit(65));
}
