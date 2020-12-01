package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.TempSeries;
import co.abarr.weather.time.DateRange;

import java.time.LocalDate;

/**
 * API for classes that can predict a temperature series.
 * <p>
 * Created by adam on 01/12/2020.
 */
public interface TempPredictor {
    /**
     * Predicts the temperature over the supplied date range.
     */
    TempSeries predict(DateRange range);

    /**
     * Predicts the temperature over the supplied date range.
     */
    default TempSeries predict(LocalDate start, LocalDate end) {
        return predict(DateRange.of(start, end));
    }

    /**
     * Predicts the mean temperature over the all dates.
     */
    default TempPredictor mean(TempSeries train) {
        return new SimpleMean(train);
    }
}
