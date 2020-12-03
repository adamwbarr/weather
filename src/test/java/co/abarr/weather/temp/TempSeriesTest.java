package co.abarr.weather.temp;

import co.abarr.weather.time.DateRange;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.util.Map;

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
    void of_DuplicateSamples_ShouldRetainLatest() {
        TempSample sample1 = Temp.fahrenheit(40).on(date1);
        TempSample sample2 = Temp.fahrenheit(41).on(date1);
        assertThat(TempSeries.of(sample1, sample2)).containsExactly(sample2);
    }

    @Test
    void of_UnorderedEntries_ShouldOrderByDate() {
        TempSample sample1 = Temp.fahrenheit(40).on(date1);
        TempSample sample2 = Temp.fahrenheit(41).on(date2);
        TempSeries series = TempSeries.of(sample2, sample1);
        assertThat(series).containsExactly(sample1, sample2);
    }

    @Test
    void size_OfEmptySeries_ShouldBeZero() {
        assertThat(TempSeries.empty()).hasSize(0);
    }

    @Test
    void size_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            Temp.fahrenheit(40).on(date1),
            Temp.fahrenheit(41).on(date2)
        );
        assertThat(series).hasSize(2);
    }

    @Test
    void get_OfFirstEntry_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            Temp.fahrenheit(40).on(date1),
            Temp.fahrenheit(41).on(date2)
        );
        assertThat(series.get(0)).isEqualTo(Temp.fahrenheit(40).on(date1));
    }

    @Test
    void get_OfSubsequentEntry_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            Temp.fahrenheit(40).on(date1),
            Temp.fahrenheit(41).on(date2)
        );
        assertThat(series.get(1)).isEqualTo(Temp.fahrenheit(41).on(date2));
    }

    @Test
    void sum_OfEmptySeries_ShouldBeZero() {
        assertThat(TempSeries.empty().sum()).isEqualTo(Temp.kelvin(0));
    }

    @Test
    void sum_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            Temp.fahrenheit(40).on(date1),
            Temp.fahrenheit(41).on(date2)
        );
        assertThat(series.sum()).isEqualTo(Temp.fahrenheit(81));
    }

    @Test
    void mean_OfEmptySeries_ShouldNotExist() {
        assertThatThrownBy(() -> TempSeries.empty().mean()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void mean_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(
            Temp.fahrenheit(40).on(date1),
            Temp.fahrenheit(41).on(date2)
        );
        assertThat(series.mean()).isEqualTo(Temp.fahrenheit(40.5));
    }

    @Test
    void hdd_OfEmptySeries_ShouldBeInReferenceUnits() {
        TempSeries temps = TempSeries.empty();
        TempIndexer indexer = TempIndexer.hdd(Temp.fahrenheit(65));
        Temp hdd = temps.index(indexer);
        assertThat(hdd).isEqualTo(Temp.fahrenheit(0));
    }

    @Test
    void hdd_OfValidSeries_ShouldBeCorrect() {
        TempSeries temps = TempSeries.of(
            Temp.fahrenheit(66).on(date1),
            Temp.fahrenheit(62).on(date2),
            Temp.fahrenheit(60).on(date3)
        );
        TempIndexer indexer = TempIndexer.hdd(Temp.fahrenheit(65));
        Temp hdd = temps.index(indexer);
        assertThat(hdd).isEqualTo(Temp.fahrenheit(8));
    }

    @Test
    void byYear_WhenEmpty_ReturnsEmptyMap() {
        assertThat(TempSeries.empty().groupByYear()).isEmpty();
    }

    @Test
    void byYear_WhenMultipleYears_ReturnsCorrectYears() {
        TempSeries temps = TempSeries.of(
            Temp.fahrenheit(66).on(LocalDate.parse("2019-01-01")),
            Temp.fahrenheit(62).on(LocalDate.parse("2019-01-02")),
            Temp.fahrenheit(60).on(LocalDate.parse("2020-01-01"))
        );
        Map<Year, TempSeries> byYear = temps.groupByYear();
        assertThat(byYear).containsOnlyKeys(Year.of(2019), Year.of(2020));
    }

    @Test
    void byYear_WhenMultipleYears_ReturnsCorrectSeriesForYear() {
        TempSeries temps = TempSeries.of(
            Temp.fahrenheit(66).on(LocalDate.parse("2019-01-01")),
            Temp.fahrenheit(62).on(LocalDate.parse("2019-01-02")),
            Temp.fahrenheit(60).on(LocalDate.parse("2020-01-01"))
        );
        Map<Year, TempSeries> byYear = temps.groupByYear();
        assertThat(byYear.get(Year.of(2019))).isEqualTo(
            TempSeries.of(
                Temp.fahrenheit(66).on(LocalDate.parse("2019-01-01")),
                Temp.fahrenheit(62).on(LocalDate.parse("2019-01-02"))
            )
        );
    }

    @Test
    void dates_OfEmptySeries_ShouldThrowException() {
        assertThatThrownBy(() -> TempSeries.empty().dates()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void dates_OfNonEmptySeries_ShoulBeCorrect() {
        TempSeries temps = TempSeries.of(
            Temp.fahrenheit(66).on(LocalDate.parse("2019-01-01")),
            Temp.fahrenheit(62).on(LocalDate.parse("2019-01-02")),
            Temp.fahrenheit(60).on(LocalDate.parse("2020-01-01"))
        );
        assertThat(temps.dates()).isEqualTo(DateRange.of(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-02")));
    }

    @Test
    void map_OfEmptySeries_ShouldBeEmptySeries() {
        TempSeries series = TempSeries.empty().map((date, temp) -> temp.toKelvin());
        assertThat(series).isEqualTo(TempSeries.empty());
    }

    @Test
    void map_OfNonEmptySeries_ShouldBeCorrect() {
        TempSeries raw = TempSeries.of(
            Temp.fahrenheit(66).on(date1),
            Temp.fahrenheit(62).on(date2)
        );
        TempSeries mapped = raw.map((date, temp) -> temp.plus(Temp.fahrenheit(1)));
        assertThat(mapped).isEqualTo(TempSeries.of(
            Temp.fahrenheit(67).on(date1),
            Temp.fahrenheit(63).on(date2)
        ));
    }
}