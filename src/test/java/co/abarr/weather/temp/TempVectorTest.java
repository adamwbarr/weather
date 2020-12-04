package co.abarr.weather.temp;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static co.abarr.weather.temp.TempVector.Entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 01/12/2020.
 */
class TempVectorTest {
    private final LocalDate date1 = LocalDate.parse("2020-01-01");
    private final LocalDate date2 = LocalDate.parse("2020-01-02");
    private final LocalDate date3 = LocalDate.parse("2020-01-03");
    private final LocalDate date4 = LocalDate.parse("2020-01-04");

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
    void of_FactoryFunction_ShouldExcludeNulls() {
        Map<LocalDate, Temp> items = new HashMap<>();
        items.put(date1, Temp.celsius(6));
        items.put(date2, null);
        TempVector<LocalDate> vector = TempVector.of(items, temp -> temp);
        assertThat(vector).containsExactly(TempVector.entry(date1, Temp.celsius(6)));
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
    void get_OfValidKey_ShouldBeCorrect() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(40)),
            TempVector.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(vector.get(date1)).contains(Temp.fahrenheit(40));
    }

    @Test
    void get_OfInvalidKey_ShouldBeEmpty() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(40)),
            TempVector.entry(date2, Temp.fahrenheit(41))
        );
        assertThat(vector.get(date3)).isEmpty();
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
            TempVector.entry(date3, Temp.fahrenheit(44)),
            TempVector.entry(date4, Temp.fahrenheit(44))
        );
        assertThat(vector.qvar()).contains(Temp.fahrenheit(2));
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

    @Test
    void to_SameUnits_ShouldReturnSelf() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(41)),
            TempVector.entry(date2, Temp.fahrenheit(50))
        );
        assertThat(vector.toFahrenheit()).isEqualTo(vector);
    }

    @Test
    void to_DifferentUnits_ShouldConvertCorrectly() {
        TempVector<LocalDate> vector = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(41)),
            TempVector.entry(date2, Temp.fahrenheit(50))
        );
        assertThat(vector.toCelsius()).containsExactly(
            TempVector.entry(date1, Temp.celsius(5)),
            TempVector.entry(date2, Temp.celsius(10))
        );
    }

    @Test
    void minus_WithKeyMismatch_ShouldThrowException() {
        TempVector<LocalDate> x = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(4))
        );
        TempVector<LocalDate> y = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(3)),
            TempVector.entry(date2, Temp.fahrenheit(2))
        );
        assertThatThrownBy(() -> x.minus(y)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void minus_WithSameKeys_ShouldSubtractCorrectly() {
        TempVector<LocalDate> x = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(4)),
            TempVector.entry(date2, Temp.fahrenheit(5))
        );
        TempVector<LocalDate> y = TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(3)),
            TempVector.entry(date2, Temp.fahrenheit(2))
        );
        assertThat(x.minus(y)).isEqualTo(TempVector.of(
            TempVector.entry(date1, Temp.fahrenheit(1)),
            TempVector.entry(date2, Temp.fahrenheit(3))
        ));
    }
}