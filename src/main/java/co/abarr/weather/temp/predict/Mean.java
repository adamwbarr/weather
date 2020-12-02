package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSeries;
import co.abarr.weather.time.DateRange;

/**
 * Created by adam on 01/12/2020.
 */
class Mean implements TempPredictor {
    private final Temp mean;

    public Mean(TempSeries train) {
        mean = train.mean().orElse(null);
    }

    @Override
    public TempSeries predict(DateRange range) {
        if (mean == null) {
            return TempSeries.empty();
        } else {
            return TempSeries.of(range, date -> mean);
        }
    }
}
