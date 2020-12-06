package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSeries;
import co.abarr.weather.time.DateRange;

import java.util.Objects;

/**
 * Created by adam on 04/12/2020.
 */
class Constant implements TempPredictor {
    private final Temp temp;

    public Constant(Temp temp) {
        this.temp = Objects.requireNonNull(temp);
    }

    @Override
    public TempSeries predict(DateRange range) {
        return TempSeries.of(range, (int i) -> temp);
    }
}
