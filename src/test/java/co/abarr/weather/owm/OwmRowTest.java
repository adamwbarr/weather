package co.abarr.weather.owm;

import co.abarr.weather.temp.Temp;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneOffset;

import static co.abarr.weather.owm.Location.CENTRAL_PARK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 30/11/2020.
 */
class OwmRowTest {
    @Test
    void of_LocationIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> OwmRow.of(null, Instant.parse("2020-01-01T00:00:00Z"), Temp.kelvin(100))).isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_TimeIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> OwmRow.of(CENTRAL_PARK, null, Temp.kelvin(100))).isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_TempIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> OwmRow.of(CENTRAL_PARK, Instant.parse("2020-01-01T00:00:00Z"), null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void date_WhenUtc_ShouldBeCorrect() {
        OwmRow row = OwmRow.of(CENTRAL_PARK, Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC, Temp.kelvin(100));
        assertThat(row.date()).isEqualTo("2020-01-01");
    }

    @Test
    void date_WhenNonUtc_ShouldBeCorrect() {
        OwmRow row = OwmRow.of(CENTRAL_PARK, Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.ofTotalSeconds(-18000), Temp.kelvin(100));
        assertThat(row.date()).isEqualTo("2019-12-31");
    }
}