package co.abarr.weather.temp;

import co.abarr.weather.time.DateRange;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.util.Map;

import static co.abarr.weather.temp.TempSeries.Entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 01/12/2020.
 */
class TempSeriesTest {
    private final LocalDate date1 = LocalDate.parse("2020-01-01");
    private final LocalDate date2 = LocalDate.parse("2020-01-02");
    private final LocalDate date3 = LocalDate.parse("2020-01-03");
    private final LocalDate date4 = LocalDate.parse("2020-01-04");

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
            TempSeries.entry(date3, Temp.fahrenheit(44)),
            TempSeries.entry(date4, Temp.fahrenheit(44))
        );
        assertThat(series.qvar()).contains(Temp.fahrenheit(2));
    }

    @Test
    void groupBy_WhenEmpty_ReturnsEmptyMap() {
        assertThat(TempSeries.empty().groupByYear()).isEmpty();
    }

    @Test
    void groupByYear_WhenMultipleGroups_ReturnsCorrectGroups() {
        TempSeries temps = TempSeries.of(
            TempSeries.entry(LocalDate.parse("2019-01-01"), Temp.fahrenheit(66)),
            TempSeries.entry(LocalDate.parse("2019-01-02"), Temp.fahrenheit(62)),
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(60))
        );
        Map<Year, TempSeries> byYear = temps.groupByYear();
        assertThat(byYear).containsOnlyKeys(Year.of(2019), Year.of(2020));
    }

    @Test
    void groupByYear_WhenMultipleYears_ReturnsCorrectSeriesForYear() {
        TempSeries temps = TempSeries.of(
            TempSeries.entry(LocalDate.parse("2019-01-01"), Temp.fahrenheit(66)),
            TempSeries.entry(LocalDate.parse("2019-01-02"), Temp.fahrenheit(62)),
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(60))
        );
        Map<Year, TempSeries> byYear = temps.groupByYear();
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

    @Test
    void head_OfEmptySeries_ShouldReturnSelf() {
        TempSeries series = TempSeries.empty().head(LocalDate.parse("2020-01-04"));
        assertThat(series).isEmpty();
    }

    @Test
    void head_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(61)),
            TempSeries.entry(LocalDate.parse("2020-01-02"), Temp.fahrenheit(62)),
            TempSeries.entry(LocalDate.parse("2020-01-03"), Temp.fahrenheit(63)),
            TempSeries.entry(LocalDate.parse("2020-01-04"), Temp.fahrenheit(64)),
            TempSeries.entry(LocalDate.parse("2020-01-05"), Temp.fahrenheit(65))
        ).head(
            LocalDate.parse("2020-01-04")
        );
        assertThat(series).isEqualTo(TempSeries.of(
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(61)),
            TempSeries.entry(LocalDate.parse("2020-01-02"), Temp.fahrenheit(62)),
            TempSeries.entry(LocalDate.parse("2020-01-03"), Temp.fahrenheit(63))
        ));
    }

    @Test
    void subSeries_OfEmptySeries_ShouldReturnSelf() {
        TempSeries series = TempSeries.empty().subSeries(
            DateRange.of(LocalDate.parse("2020-01-02"), LocalDate.parse("2020-01-04"))
        );
        assertThat(series).isEmpty();
    }

    @Test
    void subSeries_ByDateRange_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(61)),
            TempSeries.entry(LocalDate.parse("2020-01-02"), Temp.fahrenheit(62)),
            TempSeries.entry(LocalDate.parse("2020-01-03"), Temp.fahrenheit(63)),
            TempSeries.entry(LocalDate.parse("2020-01-04"), Temp.fahrenheit(64)),
            TempSeries.entry(LocalDate.parse("2020-01-05"), Temp.fahrenheit(65))
        ).subSeries(
            DateRange.of(LocalDate.parse("2020-01-02"), LocalDate.parse("2020-01-04"))
        );
        assertThat(series).isEqualTo(TempSeries.of(
            TempSeries.entry(LocalDate.parse("2020-01-02"), Temp.fahrenheit(62)),
            TempSeries.entry(LocalDate.parse("2020-01-03"), Temp.fahrenheit(63))
        ));
    }

    @Test
    void reduce_OfEmptyGrouping_ShouldReturnEmptyVector() {
        TempVector<Year> vector = TempSeries.empty().groupByYear().reduce(TempSeries::sum);
        assertThat(vector).isEmpty();
    }

    @Test
    void reduce_OfNonEmptyGrouping_ShouldReturnValidValues() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(LocalDate.parse("2019-01-01"), Temp.fahrenheit(1)),
            TempSeries.entry(LocalDate.parse("2019-01-02"), Temp.fahrenheit(2)),
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(4))
        );
        TempVector<Year> vector = series.groupByYear().reduce(TempSeries::sum);
        assertThat(vector).containsExactly(
            TempVector.entry(Year.of(2019), Temp.fahrenheit(3)),
            TempVector.entry(Year.of(2020), Temp.fahrenheit(4))
        );
    }

    @Test
    void reduce_OfNonEmptyGrouping_ShouldExcludeNulls() {
        TempSeries series = TempSeries.of(
            TempSeries.entry(LocalDate.parse("2019-01-01"), Temp.fahrenheit(1)),
            TempSeries.entry(LocalDate.parse("2019-01-02"), Temp.fahrenheit(2)),
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(4))
        );
        TempVector<Year> vector = series.groupByYear().reduce(year -> year.qvar().orElse(null));
        assertThat(vector.get(Year.of(2020))).isEmpty();
    }


    @Test
    void minus_WithKeyMismatch_ShouldThrowException() {
        TempSeries x = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(4))
        );
        TempSeries y = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(3)),
            TempSeries.entry(date2, Temp.fahrenheit(2))
        );
        assertThatThrownBy(() -> x.minus(y)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void minus_WithSameKeys_ShouldSubtractCorrectly() {
        TempSeries x = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(4)),
            TempSeries.entry(date2, Temp.fahrenheit(5))
        );
        TempSeries y = TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(3)),
            TempSeries.entry(date2, Temp.fahrenheit(2))
        );
        assertThat(x.minus(y)).isEqualTo(TempSeries.of(
            TempSeries.entry(date1, Temp.fahrenheit(1)),
            TempSeries.entry(date2, Temp.fahrenheit(3))
        ));
    }
}