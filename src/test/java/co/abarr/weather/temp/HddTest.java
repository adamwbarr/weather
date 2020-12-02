package co.abarr.weather.temp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by adam on 02/12/2020.
 */
class HddTest {
    @Test
    void indexFor_TempAboveReference_ShouldBeZero() {
        Hdd hdd = new Hdd(Temp.fahrenheit(65));
        assertThat(hdd.indexFor(Temp.fahrenheit(70))).isEqualTo(Temp.fahrenheit(0));
    }

    @Test
    void indexFor_TempAtReference_ShouldBeZero() {
        Hdd hdd = new Hdd(Temp.fahrenheit(65));
        assertThat(hdd.indexFor(Temp.fahrenheit(65))).isEqualTo(Temp.fahrenheit(0));
    }

    @Test
    void indexFor_TempBelowReference_ShouldBeCorrect() {
        Hdd hdd = new Hdd(Temp.fahrenheit(65));
        assertThat(hdd.indexFor(Temp.fahrenheit(60))).isEqualTo(Temp.fahrenheit(5));
    }

    @Test
    void indexFor_TempWithMismatchedUnits_ShouldBeInReferenceUnits() {
        Hdd hdd = new Hdd(Temp.fahrenheit(65));
        assertThat(hdd.indexFor(Temp.kelvin(280)).units()).isEqualTo(TempUnits.FAHRENHEIT);
    }
}