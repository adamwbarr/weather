package co.abarr.weather.temp;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.util.Map;

import static co.abarr.weather.temp.TempVector.Entry;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by adam on 01/12/2020.
 */
class TempVectorTest {
    private final LocalDate date1 = LocalDate.parse("2020-01-01");
    private final LocalDate date3 = LocalDate.parse("2020-01-03");
    private final LocalDate date2 = LocalDate.parse("2020-01-02");

    @Test
    void of_DuplicateEntries_ShouldRetainLatest() {
        Entry<LocalDate> entry1 = TempVector.entry(date1, Temp.fahrenheit(40));
        Entry<LocalDate> entry2 = TempVector.entry(date1, Temp.fahrenheit(41));
        assertThat(TempVector.of(entry1, entry2)).containsExactly(entry2);
    }

    @Test
    void of_MismatchedUnits_ShouldConvertToSingleUnit() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.celsius(6)),
            TempVector.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(vector).containsExactly(
            TempVector.entry(date1, Temp.celsius(6)),
            TempVector.entry(date2, Temp.celsius(5))
        );
    }

    @Test
    void size_OfEmptyVector_ShouldBeZero() {
        assertThat(TempVector.empty()).hasSize(0);
    }

    @Test
    void size_OfNonEmptyVector_ShouldBeCorrect() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(40)),
            TempVector.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(vector).hasSize(2);
    }

    @Test
    void get_OfFirstEntry_ShouldBeCorrect() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(40)),
            TempVector.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(vector.get(0)).isEqualTo(TempVector.entry(date1, Temp.fahrenheit(40)));
    }

    @Test
    void get_OfSubsequentEntry_ShouldBeCorrect() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(40)),
            TempVector.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(vector.get(1)).isEqualTo(TempVector.entry(date2, Temp.fahrenheit(41)));
    }

    @Test
    void sum_OfEmptyVector_ShouldBeZero() {
        assertThat(TempVector.empty().sum()).isEqualTo(Temp.kelvin(0));
    }

    @Test
    void sum_OfNonEmptyVector_ShouldBeCorrect() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(40)),
            TempVector.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(vector.sum()).isEqualTo(Temp.fahrenheit(81));
    }

    @Test
    void mean_OfEmptyVector_ShouldNotExist() {
        assertThat(TempVector.empty().mean()).isEmpty();
    }

    @Test
    void mean_OfNonEmptyVector_ShouldBeCorrect() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(40)),
            TempVector.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(vector.mean()).contains(Temp.fahrenheit(40.5));
    }

    @Test
    void qvar_OfEmptyVector_ShouldNotExist() {
        assertThat(TempVector.empty().qvar()).isEmpty();
    }

    @Test
    void qvar_OfNonEmptyVector_ShouldBeCorrect() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(40)),
            TempVector.entry(date2, Temp.fahrenheit(42)),
            TempVector.entry(date3, Temp.fahrenheit(44))
        );
        assertThat(vector.qvar()).contains(Temp.fahrenheit(8));
    }

    @Test
    void groupBy_WhenEmpty_ReturnsEmptyMap() {
        assertThat(TempVector.<LocalDate>empty().groupBy(Year::from)).isEmpty();
    }

    @Test
    void groupBy_WhenMultipleGroups_ReturnsCorrectGroups() {
        TempVector<LocalDate> temps = TempVector.of(
            TempVector.entry(LocalDate.parse("2019-01-01"), Temp.fahrenheit(66)),
            TempVector.entry(LocalDate.parse("2019-01-02"), Temp.fahrenheit(62)),
            TempVector.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(60))
        );
        Map<Year, TempVector<LocalDate>> byYear = temps.groupBy(Year::from);
        assertThat(byYear).containsOnlyKeys(Year.of(2019), Year.of(2020));
    }

    @Test
    void byYear_WhenMultipleYears_ReturnsCorrectVectorForYear() {
        TempVector<LocalDate> temps = TempVector.of(
            TempVector.entry(LocalDate.parse("2019-01-01"), Temp.fahrenheit(66)),
            TempVector.entry(LocalDate.parse("2019-01-02"), Temp.fahrenheit(62)),
            TempVector.entry(LocalDate.parse("2020-01-01"), Temp.fahrenheit(60))
        );
        Map<Year, TempVector<LocalDate>> byYear = temps.groupBy(Year::from);
        assertThat(byYear.get(Year.of(2019))).isEqualTo(
            TempVector.of(
                TempVector.entry(LocalDate.parse("2019-01-01"), Temp.fahrenheit(66)),
                TempVector.entry(LocalDate.parse("2019-01-02"), Temp.fahrenheit(62))
            )
        );
    }

    @Test
    void map_OfEmptyVector_ShouldBeEmptyVector() {
        TempVector<LocalDate> vector = TempVector.<LocalDate>empty().map((date, temp) -> temp.toKelvin());
        assertThat(vector).isEqualTo(TempVector.empty());
    }

    @Test
    void map_OfNonEmptyVector_ShouldBeCorrect() {
        TempVector<LocalDate> raw = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(66)),
            TempVector.entry(date2, Temp.fahrenheit(62))
        );
        TempVector<LocalDate> mapped = raw.map((date, temp) -> temp.plus(Temp.fahrenheit(1)));
        assertThat(mapped).isEqualTo(TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(67)),
            TempVector.entry(date2, Temp.fahrenheit(63))
        ));
    }

    @Test
    void sorkKeys_OnEmptyVector_ShouldReturnSelf() {
        TempVector<LocalDate> vector = TempVector.empty();
        assertThat(vector.sortKeys()).isEqualTo(vector);
    }

    @Test
    void sortKeys_OnSortedVector_ShouldReturnSelf() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(66)),
            TempVector.entry(date2, Temp.fahrenheit(62)),
            TempVector.entry(date3, Temp.fahrenheit(61))
        );
        assertThat(vector.sortKeys()).isEqualTo(vector);
    }

    @Test
    void sortKeys_OnUnsortedVector_ShouldReturnSortedVector() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date2, Temp.fahrenheit(62)),
            TempVector.entry(date3, Temp.fahrenheit(61)),
            TempVector.entry(date1, Temp.fahrenheit(66))
        );
        assertThat(vector.sortKeys()).containsExactly(
            TempVector.entry(date1, Temp.fahrenheit(66)),
            TempVector.entry(date2, Temp.fahrenheit(62)),
            TempVector.entry(date3, Temp.fahrenheit(61))
        );
    }
}