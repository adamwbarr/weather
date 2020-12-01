package co.abarr.weather.owm;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocationTest {
    @Test
    public void of_NameIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> Location.of(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void of_NameIsEmpty_ShouldThrowException() {
        assertThatThrownBy(() -> Location.of("")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void of_NameIsBlank_ShouldThrowException() {
        assertThatThrownBy(() -> Location.of("   ")).isInstanceOf(IllegalArgumentException.class);
    }
}