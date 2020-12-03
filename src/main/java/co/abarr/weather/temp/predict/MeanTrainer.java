package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSeries;

/**
 * Created by adam on 01/12/2020.
 */
class MeanTrainer implements TempTrainer {
    public TempPredictor train(TempSeries train) {
        if (train.isEmpty()) {
            throw new IllegalArgumentException("Empty training series");
        } else {
            Temp mean = train.mean();
            return range -> TempSeries.of(range, date -> mean);
        }
    }
}
