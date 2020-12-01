package co.abarr.weather.temp;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static co.abarr.weather.location.Location.CENTRAL_PARK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 30/11/2020.
 */
class TempReadingTest {
    @Test
    void of_LocationIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> TempReading.of(null, Instant.now(), Temp.kelvin(100))).isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_TimeIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> TempReading.of(CENTRAL_PARK, null, Temp.kelvin(100))).isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_TempIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> TempReading.of(CENTRAL_PARK, Instant.now(), null)).isInstanceOf(NullPointerException.class);
    }
}