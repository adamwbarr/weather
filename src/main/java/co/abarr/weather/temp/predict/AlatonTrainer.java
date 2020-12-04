package co.abarr.weather.temp.predict;

import co.abarr.weather.owm.OwmBatch;
import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSample;
import co.abarr.weather.temp.TempSeries;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Created by adam on 03/12/2020.
 */
public class AlatonTrainer implements TempTrainer {
    private static final Logger logger = LoggerFactory.getLogger(AlatonTrainer.class);
    public static final double W = 2 * Math.PI / 365.0;

    @Override
    public TempPredictor train(TempSeries train) {
        TempSeries mean = meanFor(train);
        for (Map.Entry<Month, TempSeries> entry0 : train.groupByMonth().entrySet()) {
            Map<Year, TempSeries> years = entry0.getValue().groupByYear();
            double sum = 0;
            for (Map.Entry<Year, TempSeries> entry1 : years.entrySet()) {
                sum += entry1.getValue().qvar().get().doubleValue();
            }
        }
        return null;
    }

    private static TempSeries meanFor(TempSeries train) {
        LocalDate origin = train.get(0).date();
        DoubleMatrix X = new DoubleMatrix(train.size(), 4);
        DoubleMatrix y = new DoubleMatrix(train.size());
        for (int i = 0; i < train.size(); i++) {
            TempSample sample = train.get(i);
            y.put(i, sample.temp().toCelsius().doubleValue());
            X.put(i, 0, 1);
            int t = t(origin, sample.date());
            X.put(i, 1, t);
            X.put(i, 2, Math.sin(W * t));
            X.put(i, 3, Math.cos(W * t));
        }
        double[] a = Solve.solveLeastSquares(X, y).toArray();
        double A = a[0];
        double B = a[1];
        double C = Math.sqrt(Math.pow(a[2], 2) + Math.pow(a[3], 2));
        double Theta = Math.atan(a[3] / a[2]) - Math.PI;
        logger.info("Fit mean series to {} samples: A={}, B={}, C={}, Theta={}", train.size(), A, B, C, Theta);
        return train.map((date, temp) -> {
            int t = t(origin, date);
            return Temp.celsius(A + B * t + C * Math.sin(W * t + Theta));
        });
    }

    private static int t(LocalDate origin, LocalDate date) {
        return (int) ChronoUnit.DAYS.between(origin, date) + 1;
    }

    public static void main(String[] args) {
        AlatonTrainer alaton = new AlatonTrainer();
        alaton.train(OwmBatch.centralParkCsv().maxs());
    }
}
