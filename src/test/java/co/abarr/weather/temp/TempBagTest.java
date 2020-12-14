package co.abarr.weather.temp;

import co.abarr.weather.math.Fraction;
import co.abarr.weather.math.Probability;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by adam on 05/12/2020.
 */
class TempBagTest {
    @Test
    void of_MixedUnits_ShouldConvertToSingleUnit() {
        TempBag temps = TempBag.of(Temp.celsius(6), Temp.fahrenheit(41));
        assertThat(temps).isEqualTo(TempBag.of(Temp.celsius(6), Temp.celsius(5)));
    }

    @Test
    void quantileOf_BelowMin_ShouldReturnZero() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3), Temp.celsius(4), Temp.celsius(5));
        assertThat(temps.quantileOf(Temp.celsius(0))).isEqualTo(Fraction.of(0));
    }

    @Test
    void quantileOf_OfMin_ShouldReturnFraction() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3), Temp.celsius(4), Temp.celsius(5));
        assertThat(temps.quantileOf(Temp.celsius(1))).isEqualTo(Fraction.of(0));
    }

    @Test
    void quantileOf_OfMax_ShouldReturnCorrectValue() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3), Temp.celsius(4));
        assertThat(temps.quantileOf(Temp.celsius(4))).isEqualTo(Fraction.of(0.75));
    }

    @Test
    void quantileOf_AboveMax_ShouldReturnOne() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3), Temp.celsius(4), Temp.celsius(5));
        assertThat(temps.quantileOf(Temp.celsius(6))).isEqualTo(Fraction.of(1));
    }

    @Test
    void quantileOf_OfKnownValueInMiddleOfDistribution_ShouldReturnCorrectValue() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3), Temp.celsius(4), Temp.celsius(5));
        assertThat(temps.quantileOf(Temp.celsius(3))).isEqualTo(Fraction.of(0.4));
    }

    @Test
    void quantileOf_OfUnknownValueInMiddleOfDistribution_ShouldReturnCorrectValue() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(4), Temp.celsius(5));
        assertThat(temps.quantileOf(Temp.celsius(3))).isEqualTo(Fraction.of(0.5));
    }

    @Test
    void quantileOf_WhenUnsortedInput_ShouldReturnCorrectValue() {
        TempBag temps = TempBag.of(Temp.celsius(4), Temp.celsius(2), Temp.celsius(5), Temp.celsius(1));
        assertThat(temps.quantileOf(Temp.celsius(3))).isEqualTo(Fraction.of(0.5));
    }

    @Test
    void pMoreThanOrEqualTo_BelowMin_ShouldReturnOne() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3), Temp.celsius(4), Temp.celsius(5));
        assertThat(temps.pMoreThanOrEqualTo(Temp.celsius(0))).isEqualTo(Probability.of(1));
    }

    @Test
    void pMoreThanOrEqualTo_OfMin_ShouldReturnOne() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3), Temp.celsius(4), Temp.celsius(5));
        assertThat(temps.pMoreThanOrEqualTo(Temp.celsius(1))).isEqualTo(Probability.of(1));
    }

    @Test
    void pMoreThanOrEqualTo_OfMax_ShouldReturnCorrectValue() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3), Temp.celsius(4));
        assertThat(temps.pMoreThanOrEqualTo(Temp.celsius(4))).isEqualTo(Probability.of(0.25));
    }

    @Test
    void pMoreThanOrEqualTo_AboveMax_ShouldReturnZero() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3), Temp.celsius(4), Temp.celsius(5));
        assertThat(temps.pMoreThanOrEqualTo(Temp.celsius(6))).isEqualTo(Probability.of(0));
    }

    @Test
    void pMoreThanOrEqualTo_OfKnownValueInMiddleOfDistribution_ShouldReturnCorrectValue() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3), Temp.celsius(4), Temp.celsius(5));
        assertThat(temps.pMoreThanOrEqualTo(Temp.celsius(3))).isEqualTo(Probability.of(0.6));
    }

    @Test
    void pMoreThanOrEqualTo_OfUnknownValueInMiddleOfDistribution_ShouldReturnCorrectValue() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(4), Temp.celsius(5));
        assertThat(temps.pMoreThanOrEqualTo(Temp.celsius(3))).isEqualTo(Probability.of(0.5));
    }

    @Test
    void pMoreThanOrEqualTo_WhenUnsortedInput_ShouldReturnCorrectValue() {
        TempBag temps = TempBag.of(Temp.celsius(4), Temp.celsius(2), Temp.celsius(5), Temp.celsius(1));
        assertThat(temps.pMoreThanOrEqualTo(Temp.celsius(3))).isEqualTo(Probability.of(0.5));
    }

    @Test
    void quantile_OfZero_ShouldReturnMin() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(4), Temp.celsius(5));
        Temp quantile = temps.quantile(Fraction.of(0));
        assertThat(quantile).isEqualTo(Temp.celsius(1));
    }

    @Test
    void quantile_OfOne_ShouldReturnMax() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(4), Temp.celsius(5));
        Temp quantile = temps.quantile(Fraction.of(1));
        assertThat(quantile).isEqualTo(Temp.celsius(5));
    }

    @Test
    void quantile_OfFiftyPercent_ShouldReturnMedianWhenPresent() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(3));
        Temp quantile = temps.quantile(Fraction.of(0.5));
        assertThat(quantile).isEqualTo(Temp.celsius(2));
    }

    @Test
    void quantile_OfFiftyPercent_ShouldReturnMedianWhenNotPresent() {
        TempBag temps = TempBag.of(Temp.celsius(1), Temp.celsius(2), Temp.celsius(4), Temp.celsius(5));
        Temp quantile = temps.quantile(Fraction.of(0.5));
        assertThat(quantile).isEqualTo(Temp.celsius(3));
    }
    
    @Test
    void sum_OfEmptyDistribution_ShouldBeZero() {
        assertThat(TempBag.empty().sum()).isEqualTo(Temp.kelvin(0));
    }

    @Test
    void sum_OfNonEmptyDistribution_ShouldBeCorrect() {
        TempBag distribution = TempBag.of(Temp.fahrenheit(40), Temp.fahrenheit(41));
        assertThat(distribution.sum()).isEqualTo(Temp.fahrenheit(81));
    }

    @Test
    void mean_OfEmptyDistribution_ShouldNotExist() {
        assertThat(TempBag.empty().mean()).isEmpty();
    }

    @Test
    void mean_OfNonEmptyDistribution_ShouldBeCorrect() {
        TempBag distribution = TempBag.of(Temp.fahrenheit(40), Temp.fahrenheit(41));
        assertThat(distribution.mean()).contains(Temp.fahrenheit(40.5));
    }

    @Test
    void mid_OfEmptyDistribution_ShouldNotExist() {
        assertThat(TempBag.empty().mid()).isEmpty();
    }

    @Test
    void mid_OfNonEmptyDistribution_ShouldBeCorrect() {
        TempBag distribution = TempBag.of(Temp.fahrenheit(40), Temp.fahrenheit(41), Temp.fahrenheit(41));
        assertThat(distribution.mid()).contains(Temp.fahrenheit(40.5));
    }
}