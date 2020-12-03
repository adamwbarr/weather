package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.TempSeries;

/**
 * API for classes that create temperature predictos.
 * <p>
 * Created by adam on 03/12/2020.
 */
public interface TempTrainer {
    /**
     * Trains a model on the supplied training data.
     */
    TempPredictor train(TempSeries train);

    /**
     * A simplistic model that just returns the mean of the training data.
     */
    TempTrainer MEAN = new Mean();
}
