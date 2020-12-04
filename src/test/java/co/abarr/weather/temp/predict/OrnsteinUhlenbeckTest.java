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
    private final LocalDate date1 = LocalDate.parse("2020-01-01");
    private final LocalDate date2 = LocalDate.parse("2020-01-02");
    private final LocalDate date3 = LocalDate.parse("2020-01-03");

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
        OrnsteinUhlenbeck predictor = predictor().alpha(0.5).sigma(Temp.celsius(1));
        TempSeries series = predictor.predict(date1, date3);
        assertThat(series.round(2)).containsExactly(
            TempSeries.entry(date1, Temp.celsius(0.8)),
            TempSeries.entry(date2, Temp.celsius(-0.5))
        );
    }
}