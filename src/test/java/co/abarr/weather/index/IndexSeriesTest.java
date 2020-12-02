package co.abarr.weather.index;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by adam on 01/12/2020.
 */
class IndexSeriesTest {
    private final LocalDate date1 = LocalDate.parse("2020-01-01");
    private final LocalDate date3 = LocalDate.parse("2020-01-03");
    private final LocalDate date2 = LocalDate.parse("2020-01-02");

    @Test
    void of_DuplicateEntries_ShouldThrowException() {
        IndexSeries.Entry entry1 = IndexSeries.Entry.of(date1, Index.of(1));
        IndexSeries.Entry entry2 = IndexSeries.Entry.of(date1, Index.of(2));
        assertThatThrownBy(() -> IndexSeries.of(entry1, entry2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_MissingDates_ShouldThrowException() {
        IndexSeries.Entry entry1 = IndexSeries.Entry.of(date1, Index.of(1));
        IndexSeries.Entry entry2 = IndexSeries.Entry.of(date3, Index.of(2));
        assertThatThrownBy(() -> IndexSeries.of(entry1, entry2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void of_UnorderedEntries_ShouldOrderByDate() {
        IndexSeries.Entry entry1 = IndexSeries.Entry.of(date1, Index.of(1));
        IndexSeries.Entry entry2 = IndexSeries.Entry.of(date2, Index.of(2));
        IndexSeries series = IndexSeries.of(entry2, entry1);
        assertThat(series).containsExactly(entry1, entry2);
    }

    @Test
    void size_OfEmptySeries_ShouldBeZero() {
        assertThat(IndexSeries.empty()).hasSize(0);
    }

    @Test
    void size_OfNonEmptySeries_ShouldBeCorrect() {
        IndexSeries series = IndexSeries.of(
            IndexSeries.Entry.of(date1, Index.of(1)),
            IndexSeries.Entry.of(date2, Index.of(2))
        );
        assertThat(series).hasSize(2);
    }

    @Test
    void get_OfFirstEntry_ShouldBeCorrect() {
        IndexSeries series = IndexSeries.of(
            IndexSeries.Entry.of(date1, Index.of(1)),
            IndexSeries.Entry.of(date2, Index.of(2))
        );
        assertThat(series.get(0)).isEqualTo(IndexSeries.Entry.of(date1, Index.of(1)));
    }

    @Test
    void get_OfSubsequentEntry_ShouldBeCorrect() {
        IndexSeries series = IndexSeries.of(
            IndexSeries.Entry.of(date1, Index.of(1)),
            IndexSeries.Entry.of(date2, Index.of(2))
        );
        assertThat(series.get(1)).isEqualTo(IndexSeries.Entry.of(date2, Index.of(2)));
    }

    @Test
    void mean_OfEmptySeries_ShouldNotExist() {
        IndexSeries series = IndexSeries.empty();
        assertThat(series.mean()).isEmpty();
    }

    @Test
    void mean_OfNonEmptySeries_ShouldBeCorrect() {
        IndexSeries series = IndexSeries.of(
            IndexSeries.Entry.of(date1, Index.of(1)),
            IndexSeries.Entry.of(date2, Index.of(2))
        );
        assertThat(series.mean()).contains(Index.of(1.5));
    }
}