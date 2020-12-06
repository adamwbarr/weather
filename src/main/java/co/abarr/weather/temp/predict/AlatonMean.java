package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSeries;
import co.abarr.weather.temp.TempUnits;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Created by adam on 04/12/2020.
 */
class AlatonMean implements TempTrainer {
    private static final Logger logger = LoggerFactory.getLogger(AlatonMean.class);
    private static final double W = 2 * Math.PI / 365.0;

    /**
     * Fits the expected mean temperature model.
     */
    @Override
    public TempPredictor train(TempSeries observed) {
        LocalDate origin = observed.get(0).date();
        double[] a = fit(observed, origin);
        double A = a[0];
        double B = a[1];
        double C = Math.sqrt(Math.pow(a[2], 2) + Math.pow(a[3], 2));
        double Theta = Math.atan(a[3] / a[2]) - Math.PI;
        TempUnits units = observed.units();
        logger.info(
            "Fit model over {} dates [{}]: A={}, B={}, C={}, Theta={}",
            observed.size(),
            units,
            A,
            B,
            C,
            Theta
        );
        return range -> {
            int t0 = t(origin, range.start());
            return TempSeries.of(range, (int i) -> {
                int t = t0 + i;
                return Temp.of(A + B * t + C * Math.sin(W * t + Theta), units);
            });
        };
    }

    private static double[] fit(TempSeries observed, LocalDate origin) {
        DoubleMatrix X = new DoubleMatrix(observed.size(), 4);
        DoubleMatrix y = new DoubleMatrix(observed.size());
        for (int i = 0; i < observed.size(); i++) {
            TempSeries.Entry entry = observed.get(i);
            y.put(i, entry.temp().doubleValue());
            X.put(i, 0, 1);
            int t = t(origin, entry.date());
            X.put(i, 1, t);
            X.put(i, 2, Math.sin(W * t));
            X.put(i, 3, Math.cos(W * t));
        }
        return Solve.solveLeastSquares(X, y).toArray();
    }

    private static int t(LocalDate origin, LocalDate date) {
        return (int) ChronoUnit.DAYS.between(origin, date) + 1;
    }
}
