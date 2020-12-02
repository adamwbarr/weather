package co.abarr.weather.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 01/12/2020.
 */
class DateRangeTest {
    private final LocalDate date1 = LocalDate.parse("2020-01-01");
    private final LocalDate date2 = LocalDate.parse("2020-01-02");
    private final LocalDate date3 = LocalDate.parse("2020-01-03");

    @Test
    void of_NullStart_ShouldThrowException() {
        assertThatThrownBy(() -> DateRange.of(null, date2)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_NullEnd_ShouldThrowException() {
        assertThatThrownBy(() -> DateRange.of(date1, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void of_StartAfterEnd_ShouldThrowException() {
        assertThatThrownBy(() -> DateRange.of(date2, date1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void year_OfIntYear_ShouldBeCorrectRange() {
        DateRange range = DateRange.year(2012);
        assertThat(range).isEqualTo(DateRange.of(LocalDate.parse("2012-01-01"), LocalDate.parse("2013-01-01")));
    }

    @Test
    void all_MultipleDates_ShouldReturnAllDates() {
        DateRange range = DateRange.of(date1, date3);
        assertThat(range.all()).containsExactly(date1, date2);
    }

    @Test
    void size_SingleDates_ShouldBeCorrect() {
        DateRange range = DateRange.of(date1, date1);
        assertThat(range.size()).isEqualTo(1);
    }

    @Test
    void size_MultipleDates_ShouldBeCorrect() {
        DateRange range = DateRange.of(date1, date3);
        assertThat(range.size()).isEqualTo(2);
    }
}