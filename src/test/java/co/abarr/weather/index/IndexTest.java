package co.abarr.weather.index;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 01/12/2020.
 */
class IndexTest {
    @Test
    public void of_IndexIsNaN_ShouldThrowException() {
        assertThatThrownBy(() -> Index.of(Double.NaN)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void of_IndexIsInfinite_ShouldThrowException() {
        assertThatThrownBy(() -> Index.of(Double.POSITIVE_INFINITY)).isInstanceOf(IllegalArgumentException.class);
    }
}