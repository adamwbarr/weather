package co.abarr.weather.temp;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 01/12/2020.
 */
class TempSeriesTest {
    @Test
    void of_DuplicateEntries_ShouldThrowException() {
        TempSeries.Entry entry1 = TempSeries.Entry.of(LocalDate.parse("2020-01-01"), Temp.kelvin(280));
        TempSeries.Entry entry2 = TempSeries.Entry.of(LocalDate.parse("2020-01-01"), Temp.kelvin(281));
        assertThatThrownBy(() -> TempSeries.of(entry1, entry2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_MissingDates_ShouldThrowException() {
        TempSeries.Entry entry1 = TempSeries.Entry.of(LocalDate.parse("2020-01-01"), Temp.kelvin(280));
        TempSeries.Entry entry2 = TempSeries.Entry.of(LocalDate.parse("2020-01-03"), Temp.kelvin(281));
        assertThatThrownBy(() -> TempSeries.of(entry1, entry2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void size_OfEmptySeries_ShouldBeZero() {
        assertThat(TempSeries.empty()).hasSize(0);
    }

    @Test
    void size_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.Entry.of(LocalDate.parse("2020-01-01"), Temp.kelvin(280)),
            TempSeries.Entry.of(LocalDate.parse("2020-01-02"), Temp.kelvin(281))
        );
        assertThat(series).hasSize(2);
    }

    @Test
    void get_OfFirstEntry_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.Entry.of(LocalDate.parse("2020-01-01"), Temp.kelvin(280)),
            TempSeries.Entry.of(LocalDate.parse("2020-01-02"), Temp.kelvin(281))
        );
        assertThat(series.get(0)).isEqualTo(TempSeries.Entry.of(LocalDate.parse("2020-01-01"), Temp.kelvin(280)));
    }

    @Test
    void get_OfSubsequentEntry_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.Entry.of(LocalDate.parse("2020-01-01"), Temp.kelvin(280)),
            TempSeries.Entry.of(LocalDate.parse("2020-01-02"), Temp.kelvin(281))
        );
        assertThat(series.get(1)).isEqualTo(TempSeries.Entry.of(LocalDate.parse("2020-01-02"), Temp.kelvin(281)));
    }
}