package co.abarr.weather.math;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by adam on 05/12/2020.
 */
class ProbabilityTest {
    @Test
    void of_NaN_ShouldThrowException() {
        assertThatThrownBy(() -> Probability.of(Double.NaN)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_NegativeValue_ShouldThrowException() {
        assertThatThrownBy(() -> Probability.of(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_ValueGreaterThanOne_ShouldThrowException() {
        assertThatThrownBy(() -> Probability.of(2)).isInstanceOf(IllegalArgumentException.class);
    }
}