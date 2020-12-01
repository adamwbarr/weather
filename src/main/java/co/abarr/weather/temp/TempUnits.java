package co.abarr.weather.temp;

/**
 * Created by adam on 30/11/2020.
 */
public enum TempUnits {
    KELVIN, FAHRENHEIT;

    /**
     * Single-character code for this unit.
     */
    public String shortCode() {
        return name().substring(0, 1);
    }
}
