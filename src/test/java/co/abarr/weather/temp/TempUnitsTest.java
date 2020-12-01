package co.abarr.weather.temp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by adam on 30/11/2020.
 */
class TempUnitsTest {
    @Test
    void shortCode_ForKelvin_ShouldBeK() {
        assertThat(TempUnits.KELVIN.shortCode()).isEqualTo("K");
    }
}