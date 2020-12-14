package co.abarr.weather.owm;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSeries;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static co.abarr.weather.owm.Location.CENTRAL_PARK;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by adam on 01/12/2020.
 */
class OwmBatchTest {
    @Test
    void size_OfEmptyBatch_ShouldBeZero() {
        assertThat(OwmBatch.empty()).hasSize(0);
    }

    @Test
    void size_OfNonEmptyBatch_ShouldBeCorrect() {
        OwmBatch batch = OwmBatch.of(
            OwmRow.of(CENTRAL_PARK, Instant.parse("2020-01-01T00:00:00Z"), Temp.kelvin(100)),
            OwmRow.of(CENTRAL_PARK, Instant.parse("2020-01-01T01:00:00Z"), Temp.kelvin(101))
        );
        assertThat(batch).hasSize(2);
    }

    @Test
    void daily_OfEmptyBatch_ShouldBeEmptySeries() {
        TempSeries series = OwmBatch.empty().daily();
        assertThat(series).isEmpty();
    }

    @Test
    void daily_OfBatchWithSingleRow_ShouldBeCorrect() {
        OwmBatch batch = OwmBatch.of(
            OwmRow.of(CENTRAL_PARK, Instant.parse("2020-01-01T00:00:00Z"), Temp.kelvin(100))
        );
        TempSeries series = batch.daily();
        assertThat(series).containsExactly(TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.kelvin(100)));
    }

    @Test
    void daily_OfBatchWithMultipleDates_ShouldBeCorrect() {
        OwmBatch batch = OwmBatch.of(
            OwmRow.of(CENTRAL_PARK, Instant.parse("2020-01-01T00:00:00Z"), Temp.kelvin(100)),
            OwmRow.of(CENTRAL_PARK, Instant.parse("2020-01-02T00:00:00Z"), Temp.kelvin(120))
        );
        TempSeries series = batch.daily();
        assertThat(series).containsExactly(
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.kelvin(100)),
            TempSeries.entry(LocalDate.parse("2020-01-02"), Temp.kelvin(120))
        );
    }

    @Test
    void daily_OfBatchWithMultipleRowsForDate_ShouldBeCorrect() {
        OwmBatch batch = OwmBatch.of(
            OwmRow.of(CENTRAL_PARK, Instant.parse("2020-01-01T00:00:00Z"), Temp.kelvin(100)),
            OwmRow.of(CENTRAL_PARK, Instant.parse("2020-01-01T01:00:00Z"), Temp.kelvin(120))
        );
        TempSeries series = batch.daily();
        assertThat(series).containsExactly(
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.kelvin(110))
        );
    }
}