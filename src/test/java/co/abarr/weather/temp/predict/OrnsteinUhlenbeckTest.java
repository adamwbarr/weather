package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSeries;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 04/12/2020.
 */
class OrnsteinUhlenbeckTest {
    private OrnsteinUhlenbeck predictor() {
        return OrnsteinUhlenbeck.on(
            TempPredictor.of(Temp.celsius(0))
        ).random(
            new Random(0)
        );
    }

    @Test
    void alpha_IsNaN_ShouldThrowException() {
        OrnsteinUhlenbeck predictor = predictor();
        assertThatThrownBy(() -> predictor.alpha(Double.NaN)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void alpha_IsNegative_ShouldThrowException() {
        OrnsteinUhlenbeck predictor = predictor();
        assertThatThrownBy(() -> predictor.alpha(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void alpha_IsGreaterThanOne_ShouldThrowException() {
        OrnsteinUhlenbeck predictor = predictor();
        assertThatThrownBy(() -> predictor.alpha(2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void predict_WithConstantSigma_ShouldCreateCorrectPath() {
        OrnsteinUhlenbeck predictor = predictor().alpha(0.25).sigma(Temp.celsius(1));
        TempSeries series = predictor.predict(LocalDate.parse("2020-01-01"), LocalDate.parse("2020-01-10"));
        assertThat(series.round(2)).containsExactly(
            TempSeries.entry(LocalDate.parse("2020-01-01"), Temp.celsius(0.8)),
            TempSeries.entry(LocalDate.parse("2020-01-02"), Temp.celsius(-0.3)),
            TempSeries.entry(LocalDate.parse("2020-01-03"), Temp.celsius(1.86)),
            TempSeries.entry(LocalDate.parse("2020-01-04"), Temp.celsius(2.16)),
            TempSeries.entry(LocalDate.parse("2020-01-05"), Temp.celsius(2.6)),
            TempSeries.entry(LocalDate.parse("2020-01-06"), Temp.celsius(0.27)),
            TempSeries.entry(LocalDate.parse("2020-01-07"), Temp.celsius(0.17)),
            TempSeries.entry(LocalDate.parse("2020-01-08"), Temp.celsius(0.25)),
            TempSeries.entry(LocalDate.parse("2020-01-09"), Temp.celsius(-0.21))
        );
    }
}