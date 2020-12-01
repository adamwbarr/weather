package co.abarr.weather.owm;

import co.abarr.weather.owm.OwmRow;
import co.abarr.weather.temp.Temp;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static co.abarr.weather.location.Location.CENTRAL_PARK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 30/11/2020.
 */
class OwmRowTest {
    @Test
    void of_LocationIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> OwmRow.of(null, Instant.now(), Temp.kelvin(100))).isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_TimeIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> OwmRow.of(CENTRAL_PARK, null, Temp.kelvin(100))).isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_TempIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> OwmRow.of(CENTRAL_PARK, Instant.now(), null)).isInstanceOf(NullPointerException.class);
    }
}