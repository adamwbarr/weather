package co.abarr.weather.temp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 30/11/2020.
 */
class TempTest {
    @Test
    public void of_TempIsNaN_ShouldThrowException() {
        assertThatThrownBy(() -> Temp.of(Double.NaN, TempUnits.KELVIN)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void of_TempIsInfinite_ShouldThrowException() {
        assertThatThrownBy(() -> Temp.of(Double.POSITIVE_INFINITY, TempUnits.KELVIN)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void of_UnitIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> Temp.of(100, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void toString_OfKelvinTemperature_ShouldBeFormattedCorrectly() {
        Temp temp = Temp.kelvin(120.5);
        assertThat(temp.toString()).isEqualTo("120.5K");
    }

    @Test
    void equals_WhenEqualValueAndUnits_ShouldBeTrue() {
        Temp temp1 = Temp.kelvin(120);
        Temp temp2 = Temp.kelvin(120);
        assertThat(temp1).isEqualTo(temp2);
    }

    @Test
    void equals_WhenDifferentValue_ShouldBeFalse() {
        Temp temp1 = Temp.kelvin(120);
        Temp temp2 = Temp.kelvin(150);
        assertThat(temp1).isNotEqualTo(temp2);
    }

    @Test
    void to_SameUnits_ShouldReturnSelf() {
        Temp temp = Temp.kelvin(120);
        assertThat(temp.toKelvin()).isEqualTo(temp);
    }

    @Test
    void to_KelvinFromFahrenheight_ShouldBeCorrect() {
        Temp temp = Temp.fahrenheit(41);
        assertThat(temp.toKelvin()).isEqualTo(Temp.kelvin(278.15));
    }

    @Test
    void to_FahrenheightFromKelvin_ShouldBeCorrect() {
        Temp temp = Temp.kelvin(278.15);
        assertThat(temp.toFahrenheit()).isEqualTo(Temp.fahrenheit(41));
    }

    @Test
    void to_CelsiusFromKelvin_ShouldBeCorrect() {
        Temp temp = Temp.kelvin(373.15);
        assertThat(temp.toCelsius()).isEqualTo(Temp.celsius(100));
    }

    @Test
    void to_KelvinFromCelsius_ShouldBeCorrect() {
        Temp temp = Temp.celsius(100);
        assertThat(temp.toKelvin()).isEqualTo(Temp.kelvin(373.15));
    }

    @Test
    void compareTo_WhenEqual_ShouldReturnZero() {
        Temp temp1 = Temp.kelvin(1);
        Temp temp2 = Temp.kelvin(1);
        assertThat(temp1.compareTo(temp2)).isEqualTo(0);
    }

    @Test
    void compareTo_WhenLessThan_ShouldReturnNegative() {
        Temp temp1 = Temp.kelvin(1);
        Temp temp2 = Temp.kelvin(2);
        assertThat(temp1.compareTo(temp2)).isLessThan(0);
    }

    @Test
    void compareTo_WhenoreThan_ShouldReturnPositive() {
        Temp temp1 = Temp.kelvin(2);
        Temp temp2 = Temp.kelvin(1);
        assertThat(temp1.compareTo(temp2)).isGreaterThan(0);
    }

    @Test
    void plus_TempWithSameUnits_ShouldBeCorrect() {
        Temp temp1 = Temp.kelvin(2);
        Temp temp2 = Temp.kelvin(1);
        assertThat(temp1.plus(temp2)).isEqualTo(Temp.kelvin(3));
    }

    @Test
    void plus_TempWithDifferentUnits_ShouldBeInUnitsOfTarget() {
        Temp temp1 = Temp.kelvin(2);
        Temp temp2 = Temp.celsius(0);
        assertThat(temp1.plus(temp2).units()).isEqualTo(TempUnits.KELVIN);
    }

    @Test
    void minus_TempWithSameUnits_ShouldBeCorrect() {
        Temp temp1 = Temp.kelvin(3);
        Temp temp2 = Temp.kelvin(1);
        assertThat(temp1.minus(temp2)).isEqualTo(Temp.kelvin(2));
    }

    @Test
    void minus_TempWithDifferentUnits_ShouldBeInUnitsOfTarget() {
        Temp temp1 = Temp.kelvin(2);
        Temp temp2 = Temp.celsius(0);
        assertThat(temp1.minus(temp2).units()).isEqualTo(TempUnits.KELVIN);
    }

    @Test
    void divideBy_Zero_ShouldThrowException() {
        Temp temp = Temp.kelvin(200);
        assertThatThrownBy(() -> temp.divideBy(0)).isInstanceOf(ArithmeticException.class);
    }

    @Test
    void divideBy_PositiveInt_ShouldReturnCorrectValue() {
        Temp temp = Temp.kelvin(200);
        assertThat(temp.divideBy(2)).isEqualTo(Temp.kelvin(100));
    }
}