package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSeries;

/**
 * Created by adam on 01/12/2020.
 */
class Mean implements TempTrainer {
    public TempPredictor train(TempSeries train) {
        Temp mean = train.mean().orElse(null);
        if (mean == null) {
            throw new IllegalArgumentException("Empty training series");
        } else {
            return TempPredictor.of(mean);
        }
    }
}
