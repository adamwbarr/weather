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

    /**
     * Alaton forecast model.
     * <p>
     * Implements the model described in the paper "On Modelling and Pricing
     * Weather Derivatives" by Alaton, Djehiche and Stillberger.
     * <p>
     * See https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.198.6547&rep=rep1&type=pdf.
     * <p>
     */
    TempTrainer ALATON = new Alaton();
}
