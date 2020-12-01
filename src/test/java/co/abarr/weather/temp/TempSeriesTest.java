package co.abarr.weather.temp;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 01/12/2020.
 */
class TempSeriesTest {
    private final LocalDate date1 = LocalDate.parse("2020-01-01");
    private final LocalDate date3 = LocalDate.parse("2020-01-03");
    private final LocalDate date2 = LocalDate.parse("2020-01-02");

    @Test
    void of_DuplicateEntries_ShouldThrowException() {
        TempSeries.Entry entry1 = TempSeries.Entry.of(date1, Temp.kelvin(280));
        TempSeries.Entry entry2 = TempSeries.Entry.of(date1, Temp.kelvin(281));
        assertThatThrownBy(() -> TempSeries.of(entry1, entry2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_MissingDates_ShouldThrowException() {
        TempSeries.Entry entry1 = TempSeries.Entry.of(date1, Temp.kelvin(280));
        TempSeries.Entry entry2 = TempSeries.Entry.of(date3, Temp.kelvin(281));
        assertThatThrownBy(() -> TempSeries.of(entry1, entry2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_UnorderedEntries_ShouldOrderByDate() {
        TempSeries.Entry entry1 = TempSeries.Entry.of(date1, Temp.kelvin(280));
        TempSeries.Entry entry2 = TempSeries.Entry.of(date2, Temp.kelvin(281));
        TempSeries series = TempSeries.of(entry2, entry1);
        assertThat(series).containsExactly(entry1, entry2);
    }

    @Test
    void size_OfEmptySeries_ShouldBeZero() {
        assertThat(TempSeries.empty()).hasSize(0);
    }

    @Test
    void size_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.Entry.of(date1, Temp.kelvin(280)),
            TempSeries.Entry.of(date2, Temp.kelvin(281))
        );
        assertThat(series).hasSize(2);
    }

    @Test
    void get_OfFirstEntry_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.Entry.of(date1, Temp.kelvin(280)),
            TempSeries.Entry.of(date2, Temp.kelvin(281))
        );
        assertThat(series.get(0)).isEqualTo(TempSeries.Entry.of(date1, Temp.kelvin(280)));
    }

    @Test
    void get_OfSubsequentEntry_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.Entry.of(date1, Temp.kelvin(280)),
            TempSeries.Entry.of(date2, Temp.kelvin(281))
        );
        assertThat(series.get(1)).isEqualTo(TempSeries.Entry.of(date2, Temp.kelvin(281)));
    }

    @Test
    void mean_OfEmptySeries_ShouldNotExist() {
        TempSeries series = TempSeries.empty();
        assertThat(series.mean()).isEmpty();
    }

    @Test
    void mean_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.Entry.of(date1, Temp.kelvin(280)),
            TempSeries.Entry.of(date2, Temp.kelvin(281))
        );
        assertThat(series.mean()).contains(Temp.kelvin(280.5));
    }
}