package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.Temp;
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
     * Predicts the same temperature for all dates.
     */
    static TempPredictor of(Temp temp) {
        return new Constant(temp);
    }
}
