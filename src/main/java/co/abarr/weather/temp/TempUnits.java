package co.abarr.weather.temp;

/**
 * Created by adam on 30/11/2020.
 */
public enum TempUnits {
    KELVIN, FAHRENHEIT, CELSIUS;

    /**
     * Single-character code for this unit.
     */
    public String shortCode() {
        return name().substring(0, 1);
    }

    /**
     * Converts a temperature to these units.
     */
    public double convert(double temp, TempUnits units) {
        if (this == units) {
            return temp;
        } else {
            return fromKelvin(toKelvin(temp, units), this);
        }
    }

    private static double toKelvin(double temp, TempUnits units) {
        return switch (units) {
            case KELVIN -> temp;
            case FAHRENHEIT -> (temp - 32) * 5.0 / 9.0 + 273.15;
            case CELSIUS -> temp + 273.15;
        };
    }

    private static double fromKelvin(double kelvin, TempUnits unit) {
        return switch (unit) {
            case KELVIN -> kelvin;
            case FAHRENHEIT -> (kelvin - 273.15) * 9.0 / 5.0 + 32;
            case CELSIUS -> kelvin - 273.15;
        };
    }

    /**
     * Helper interface for objects that have associated units.
     */
    public interface Having<T extends Having<T>> {
        /**
         * The units of this temperature.
         */
        TempUnits units();

        /**
         * Converts this temperature to the supplied units.
         */
        T to(TempUnits units);

        /**
         * Converts this temperature to Kelvins.
         */
        default T toKelvin() {
            return to(TempUnits.KELVIN);
        }

        /**
         * Converts this temperature to Fahrenheit.
         */
        default T toFahrenheit() {
            return to(TempUnits.FAHRENHEIT);
        }

        /**
         * Converts this temperature to Celsius.
         */
        default T toCelsius() {
            return to(TempUnits.CELSIUS);
        }

        /**
         * Converts this temperature to the units of the supplied temperature.
         */
        default T toUnitsOf(Having<?> o) {
            return to(o.units());
        }
    }
}
