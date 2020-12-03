package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSample;
import co.abarr.weather.temp.TempSeries;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 01/12/2020.
 */
class MeanTrainerTest {
    private final LocalDate date1 = LocalDate.parse("2020-01-01");
    private final LocalDate date2 = LocalDate.parse("2020-01-02");
    private final LocalDate date3 = LocalDate.parse("2020-01-03");
    private final LocalDate date4 = LocalDate.parse("2020-01-04");
    private final LocalDate date5 = LocalDate.parse("2020-01-05");

    private final MeanTrainer trainer = new MeanTrainer();

    @Test
    void train_OnEmptySeries_ShouldThrowException() {
        TempSeries train = TempSeries.empty();
        assertThatThrownBy(() -> trainer.train(train)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void predict_WhenTrainedOnNonEmptySeries_ShouldReturnMean() {
        TempSeries train = TempSeries.of(
            TempSample.of(date1, Temp.kelvin(200)),
            TempSample.of(date2, Temp.kelvin(210)),
            TempSample.of(date3, Temp.kelvin(250))
        );
        TempSeries prediction = trainer.train(train).predict(date4, date5);
        assertThat(prediction).containsExactly(TempSample.of(date4, Temp.kelvin(220)));
    }
}