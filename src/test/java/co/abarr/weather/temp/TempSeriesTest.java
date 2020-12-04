package co.abarr.weather.temp;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.util.Map;

import static co.abarr.weather.temp.TempSeries.Entry;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by adam on 01/12/2020.
 */
class TempSeriesTest {
    private final LocalDate date1 = LocalDate.parse("2020-01-01");
    private final LocalDate date3 = LocalDate.parse("2020-01-03");
    private final LocalDate date2 = LocalDate.parse("2020-01-02");

    @Test
    void of_DuplicateEntries_ShouldRetainLatest() {
        Entry entry1 = TempSeries.entry(date1, Temp.fahrenheit(40));
        Entry entry2 = TempSeries.entry(date1, Temp.fahrenheit(41));
        assertThat(TempSeries.of(entry1, entry2)).containsExactly(entry2);
    }

    @Test
    void of_UnorderedEntries_ShouldOrderByDate() {
        Entry entry1 = TempSeries.entry(date1, Temp.fahrenheit(40));
        Entry entry2 = TempSeries.entry(date2, Temp.fahrenheit(41));
        TempSeries series = TempSeries.of(entry2, entry1);
        assertThat(series).containsExactly(entry1, entry2);
    }

    @Test
    void of_MismatchedUnits_ShouldConvertToSingleUnit() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(date1, Temp.celsius(6)),
            TempSeries.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(series).containsExactly(
            TempSeries.entry(date1, Temp.celsius(6)),
            TempSeries.entry(date2, Temp.celsius(5))
        );
    }

    @Test
    void size_OfEmptySeries_ShouldBeZero() {
        assertThat(TempSeries.empty()).hasSize(0);
    }

    @Test
    void size_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(40)),
            TempSeries.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(series).hasSize(2);
    }

    @Test
    void get_OfFirstEntry_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(40)),
            TempSeries.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(series.get(0)).isEqualTo(TempSeries.entry(date1, Temp.fahrenheit(40)));
    }

    @Test
    void get_OfSubsequentEntry_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(40)),
            TempSeries.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(series.get(1)).isEqualTo(TempSeries.entry(date2, Temp.fahrenheit(41)));
    }

    @Test
    void sum_OfEmptySeries_ShouldBeZero() {
        assertThat(TempSeries.empty().sum()).isEqualTo(Temp.kelvin(0));
    }

    @Test
    void sum_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(40)),
            TempSeries.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(series.sum()).isEqualTo(Temp.fahrenheit(81));
    }

    @Test
    void mean_OfEmptySeries_ShouldNotExist() {
        assertThat(TempSeries.empty().mean()).isEmpty();
    }

    @Test
    void mean_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(40)),
            TempSeries.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(series.mean()).contains(Temp.fahrenheit(40.5));
    }

    @Test
    void qvar_OfEmptySeries_ShouldNotExist() {
        assertThat(TempSeries.empty().qvar()).isEmpty();
    }

    @Test
    void qvar_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(40)),
            TempSeries.entry(date2, Temp.fahrenheit(42)),
            TempSeries.entry(date3, Temp.fahrenheit(44))
        );
        assertThat(series.qvar()).contains(Temp.fahrenheit(8));
    }

    @Test
    void groupBy_WhenEmpty_ReturnsEmptyMap() {
        assertThat(TempSeries.<LocalDate>empty().groupBy(Year::from)).isEmpty();
    }

    @Test
    void groupBy_WhenMultipleGroups_ReturnsCorrectGroups() {
        TempSeries temps = TempSeries.of(
            TempSeries.entry(LocalDate.parse("2019-01-01"), Temp.fahrenheit(66)),
            TempSeries.entry(LocalDate.parse("2019-01-02"), Temp.fahrenheit(62)),
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(60))
        );
        Map<Year, TempSeries> byYear = temps.groupBy(Year::from);
        assertThat(byYear).containsOnlyKeys(Year.of(2019), Year.of(2020));
    }

    @Test
    void byYear_WhenMultipleYears_ReturnsCorrectSeriesForYear() {
        TempSeries temps = TempSeries.of(
            TempSeries.entry(LocalDate.parse("2019-01-01"), Temp.fahrenheit(66)),
            TempSeries.entry(LocalDate.parse("2019-01-02"), Temp.fahrenheit(62)),
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(60))
        );
        Map<Year, TempSeries> byYear = temps.groupBy(Year::from);
        assertThat(byYear.get(Year.of(2019))).isEqualTo(
            TempSeries.of(
                TempSeries.entry(LocalDate.parse("2019-01-01"), Temp.fahrenheit(66)),
                TempSeries.entry(LocalDate.parse("2019-01-02"), Temp.fahrenheit(62))
            )
        );
    }

    @Test
    void map_OfEmptySeries_ShouldBeEmptySeries() {
        TempSeries series = TempSeries.<LocalDate>empty().map((date, temp) -> temp.toKelvin());
        assertThat(series).isEqualTo(TempSeries.empty());
    }

    @Test
    void map_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries raw = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(66)),
            TempSeries.entry(date2, Temp.fahrenheit(62))
        );
        TempSeries mapped = raw.map((date, temp) -> temp.plus(Temp.fahrenheit(1)));
        assertThat(mapped).isEqualTo(TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(67)),
            TempSeries.entry(date2, Temp.fahrenheit(63))
        ));
    }
}