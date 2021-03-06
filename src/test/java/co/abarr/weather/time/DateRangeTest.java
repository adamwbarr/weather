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
    void yearMonth_OfSimpleMonth_ShouldBeCorrectRange() {
        DateRange range = DateRange.yearMonth(2020, 1);
        assertThat(range).isEqualTo(DateRange.of(LocalDate.parse("2020-01-01"), LocalDate.parse("2020-02-01")));
    }

    @Test
    void yearMonth_OfLeapMonth_ShouldBeCorrectRange() {
        DateRange range = DateRange.yearMonth(2012, 2);
        assertThat(range).isEqualTo(DateRange.of(LocalDate.parse("2012-02-01"), LocalDate.parse("2012-03-01")));
    }

    @Test
    void yearMonth_OfDecember_ShouldBeCorrectRange() {
        DateRange range = DateRange.yearMonth(2012, 12);
        assertThat(range).isEqualTo(DateRange.of(LocalDate.parse("2012-12-01"), LocalDate.parse("2013-01-01")));
    }

    @Test
    void of_MultipleDates_ShouldContainAllDates() {
        DateRange range = DateRange.of(date1, date3);
        assertThat(range).containsExactly(date1, date2);
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

    @Test
    void offsetStart_WithNegativeOffset_ShouldUpdateStartCorrectly() {
        DateRange range = DateRange.of(date2, date3);
        assertThat(range.offsetStart(-1).start()).isEqualTo(date1);
    }

    @Test
    void offsetStart_WithValidOffset_ShouldNotUpdateEnd() {
        DateRange range = DateRange.of(date1, date2);
        assertThat(range.offsetStart(-1).end()).isEqualTo(date2);
    }

    @Test
    void offsetEnd_WithNegativeOffset_ShouldUpdateEndCorrectly() {
        DateRange range = DateRange.of(date1, date2);
        assertThat(range.offsetEnd(1).end()).isEqualTo(date3);
    }

    @Test
    void offsetEnd_WithValidOffset_ShouldNotUpdateStart() {
        DateRange range = DateRange.of(date1, date2);
        assertThat(range.offsetEnd(1).start()).isEqualTo(date1);
    }

    @Test
    void contains_DateBeforeStart_ShouldBeFalse() {
        DateRange range = DateRange.of(LocalDate.parse("2020-02-04"), LocalDate.parse("2020-02-07"));
        assertThat(range.contains(LocalDate.parse("2020-02-03"))).isFalse();
    }

    @Test
    void contains_Start_ShouldBeTrue() {
        DateRange range = DateRange.of(LocalDate.parse("2020-02-04"), LocalDate.parse("2020-02-07"));
        assertThat(range.contains(LocalDate.parse("2020-02-04"))).isTrue();
    }

    @Test
    void contains_DateInsideRange_ShouldBeTrue() {
        DateRange range = DateRange.of(LocalDate.parse("2020-02-04"), LocalDate.parse("2020-02-07"));
        assertThat(range.contains(LocalDate.parse("2020-02-05"))).isTrue();
    }

    @Test
    void contains_End_ShouldBeFalse() {
        DateRange range = DateRange.of(LocalDate.parse("2020-02-04"), LocalDate.parse("2020-02-07"));
        assertThat(range.contains(LocalDate.parse("2020-02-07"))).isFalse();
    }

    @Test
    void contains_DateAfterEnd_ShouldBeTrue() {
        DateRange range = DateRange.of(LocalDate.parse("2020-02-04"), LocalDate.parse("2020-02-07"));
        assertThat(range.contains(LocalDate.parse("2020-02-08"))).isFalse();
    }

    @Test
    void get_NegativeIndex_ShouldThrowException() {
        DateRange range = DateRange.of(LocalDate.parse("2020-02-04"), LocalDate.parse("2020-02-07"));
        assertThatThrownBy(() -> range.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void get_ValidIndex_ShouldReturnCorrectDate() {
        DateRange range = DateRange.of(LocalDate.parse("2020-02-04"), LocalDate.parse("2020-02-07"));
        assertThat(range.get(1)).isEqualTo(LocalDate.parse("2020-02-05"));
    }

    @Test
    void get_IndexOfSize_ShouldThrowException() {
        DateRange range = DateRange.of(LocalDate.parse("2020-02-04"), LocalDate.parse("2020-02-07"));
        assertThatThrownBy(() -> range.get(3)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void get_IndexGreaterThanSize_ShouldThrowException() {
        DateRange range = DateRange.of(LocalDate.parse("2020-02-04"), LocalDate.parse("2020-02-07"));
        assertThatThrownBy(() -> range.get(8)).isInstanceOf(IndexOutOfBoundsException.class);
    }
}