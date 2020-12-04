package co.abarr.weather.temp.predict;

import co.abarr.weather.owm.OwmBatch;
import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSeries;
import co.abarr.weather.temp.TempVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Month;
import java.util.Arrays;

/**
 * Created by adam on 03/12/2020.
 */
public class Alaton implements TempTrainer {
    private static final Logger logger = LoggerFactory.getLogger(Alaton.class);
    private final TempTrainer mean = new AlatonMean();

    @Override
    public TempPredictor train(TempSeries observed) {
        TempPredictor mean = this.mean.train(observed);
        double[] qvars = qvarByMonth(observed);
        double alpha = estimateAlpha(observed, mean, qvars);
        Temp[] sigmas = new Temp[qvars.length];
        for (int i = 0; i < qvars.length; i++) {
            sigmas[i] = Temp.of(Math.sqrt(qvars[i]), observed.units());
        }
        logger.info("Estimated mean reversion {}, sigmas {}", alpha, Arrays.toString(sigmas));
        return OrnsteinUhlenbeck.on(mean).alpha(alpha).sigma(date -> sigmas[date.getMonth().ordinal()]);
    }

    private double estimateAlpha(TempSeries observed, TempPredictor mean, double[] qvars) {
        TempSeries means = mean.predict(observed.get(0).date(), observed.get(observed.size() - 1).date().plusDays(1));
        double[] residuals = new double[observed.size()];
        for (int i = 0; i < observed.size(); i++) {
            TempSeries.Entry observedi = observed.get(i);
            TempSeries.Entry meani = means.get(i);
            residuals[i] = observedi.temp().doubleValue() - meani.temp().doubleValue();
        }
        double n = 0;
        double d = 0;
        for (int i = 1; i < observed.size(); i++) {
            double qvar = qvars[observed.get(i - 1).date().getMonth().ordinal()];
            double z = residuals[i - 1] / qvar;
            if (!Double.isNaN(z)) {
                n += z * residuals[i];
                d += z * residuals[i - 1];
            }
        }
        return -Math.log(n / d);
    }

    private double[] qvarByMonth(TempSeries temps) {
        return toOrdinalArray(
            temps.groupByMonth().reduce(
                months -> months.groupByYear().reduce(
                    month -> month.qvar().orElse(null)
                ).mean().orElse(
                    null
                )
            )
        );
    }

    private static double[] toOrdinalArray(TempVector<Month> vector) {
        double[] result = new double[12];
        Arrays.fill(result, Double.NaN);
        for (TempVector.Entry<Month> entry : vector) {
            result[entry.key().ordinal()] = entry.temp().doubleValue();
        }
        return result;
    }

    public static void main(String[] args) {
        Alaton alaton = new Alaton();
        TempSeries observed = OwmBatch.centralParkCsv().maxs().toFahrenheit();
        TempPredictor predictor = alaton.train(observed);
        TempSeries predicted = predictor.predict(observed.get(0).date(), observed.get(observed.size() - 1).date().plusDays(1));
        for (int i = 0; i < predicted.size(); i++) {
            System.out.println(observed.get(i).date() + " " + observed.get(i).temp() + " " + predicted.get(i).temp());
        }
    }
}
