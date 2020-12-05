package co.abarr.weather.math;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 05/12/2020.
 */
class FractionTest {
    @Test
    void of_NaN_ShouldThrowException() {
        assertThatThrownBy(() -> Fraction.of(Double.NaN)).isInstanceOf(IllegalArgumentException.class);
    }
}