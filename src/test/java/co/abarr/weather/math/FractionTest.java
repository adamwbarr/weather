package co.abarr.weather.math;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 05/12/2020.
 */
class FractionTest {
    @Test
    void of_NaN_ShouldThrowException() {
        assertThatThrownBy(() -> Fraction.of(Double.NaN)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void round_To2dp_ShouldRoundCorrectly() {
        Fraction fraction = Fraction.of(2.678954322);
        assertThat(fraction.round(2)).isEqualTo(Fraction.of(2.68));
    }
}