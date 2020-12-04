package co.abarr.weather.temp;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by adam on 02/12/2020.
 */
class HddTest {
    private final Hdd hdd = new Hdd(Temp.fahrenheit(65));
    private final LocalDate date = LocalDate.parse("2020-01-01");

    @Test
    void indexFor_EmptySeries_ShouldBeZero() {
        assertThat(hdd.indexFor(TempSeries.empty())).isEqualTo(Temp.fahrenheit(0));
    }

    @Test
    void indexFor_TempAboveReference_ShouldBeZero() {
        TempSeries series = TempSeries.of(Temp.fahrenheit(70).on(date));
        assertThat(hdd.indexFor(series)).isEqualTo(Temp.fahrenheit(0));
    }

    @Test
    void indexFor_TempAtReference_ShouldBeZero() {
        TempSeries series = TempSeries.of(Temp.fahrenheit(65).on(date));
        assertThat(hdd.indexFor(series)).isEqualTo(Temp.fahrenheit(0));
    }

    @Test
    void indexFor_TempBelowReference_ShouldBeCorrect() {
        TempSeries series = TempSeries.of(Temp.fahrenheit(60).on(date));
        assertThat(hdd.indexFor(series)).isEqualTo(Temp.fahrenheit(5));
    }

    @Test
    void indexFor_TempWithMismatchedUnits_ShouldBeInReferenceUnits() {
        TempSeries series = TempSeries.of(Temp.kelvin(280).on(date));
        assertThat(hdd.indexFor(series).units()).isEqualTo(TempUnits.FAHRENHEIT);
    }
}