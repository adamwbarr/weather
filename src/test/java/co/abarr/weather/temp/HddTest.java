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
        assertThat(hdd.indexFor(TempVector.empty())).isEqualTo(Temp.fahrenheit(0));
    }

    @Test
    void indexFor_TempAboveReference_ShouldBeZero() {
        TempVector series = TempVector.of(TempVector.entry(date, Temp.fahrenheit(70)));
        assertThat(hdd.indexFor(series)).isEqualTo(Temp.fahrenheit(0));
    }

    @Test
    void indexFor_TempAtReference_ShouldBeZero() {
        TempVector series = TempVector.of(TempVector.entry(date, Temp.fahrenheit(65)));
        assertThat(hdd.indexFor(series)).isEqualTo(Temp.fahrenheit(0));
    }

    @Test
    void indexFor_TempBelowReference_ShouldBeCorrect() {
        TempVector series = TempVector.of(TempVector.entry(date, Temp.fahrenheit(60)));
        assertThat(hdd.indexFor(series)).isEqualTo(Temp.fahrenheit(5));
    }

    @Test
    void indexFor_TempWithMismatchedUnits_ShouldBeInReferenceUnits() {
        TempVector series = TempVector.of(TempVector.entry(date, Temp.kelvin(280)));
        assertThat(hdd.indexFor(series).units()).isEqualTo(TempUnits.FAHRENHEIT);
    }
}