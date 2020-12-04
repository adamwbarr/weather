package co.abarr.weather.temp.predict;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempSeries;
import co.abarr.weather.time.DateRange;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Generates Ornstein-Uhlenbeck paths based on some underlying predictor.
 * <p>
 * This predictor wraps another predictor, and adds random but mean-reverting
 * noise to the underlying predictor.
 * <p>
 * Created by adam on 04/12/2020.
 */
public class OrnsteinUhlenbeck implements TempPredictor {
    private final TempPredictor delegate;
    private final double alpha;
    private final Function<LocalDate, Temp> sigma;
    private final Random random;

    private OrnsteinUhlenbeck(TempPredictor delegate, double alpha, Function<LocalDate, Temp> sigma, Random random) {
        this.delegate = Objects.requireNonNull(delegate);
        this.alpha = alpha;
        this.sigma = Objects.requireNonNull(sigma);
        this.random = Objects.requireNonNull(random);
        if (Double.isNaN(alpha) || alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Invalid alpha: " + alpha);
        }
    }

    /**
     * Predicts a path over the supplied date range.
     * <p>
     * This method will generate a random Ornstein-Uhlenbeck path that mean-
     * reverts to the path produced by the underlying predictor.
     */
    @Override
    public TempSeries predict(DateRange range) {
        TempSeries mean = delegate.predict(range.offsetStart(-1));
        double[] path = new double[mean.size()];
        path[0] = mean.get(0).temp().doubleValue();
        for (int i = 1; i < path.length; i++) {
            TempSeries.Entry entry = mean.get(i - 1);
            double sigma = this.sigma.apply(entry.date()).toUnitsOf(mean).doubleValue();
            double noise = sigma * random.nextGaussian();
            double previous = path[i - 1];
            double previousResidual = entry.temp().doubleValue() - previous;
            path[i] = previous + alpha * previousResidual + noise;
        }
        Map<LocalDate, Temp> map = new HashMap<>();
        for (int i = 1; i < path.length; i++) {
            map.put(mean.get(i).date(), Temp.of(path[i], mean.units()));
        }
        return TempSeries.of(map);
    }

    /**
     * Updates the alpha parameter.
     * <p>
     * An exception will be thrown if alpha is outside of the range [0, 1].
     */
    public OrnsteinUhlenbeck alpha(double alpha) {
        return new OrnsteinUhlenbeck(delegate, alpha, sigma, random);
    }

    /**
     * Updates the (constant) sigma.
     * <p>
     * An exception will be thrown if the sigma is null.
     */
    public OrnsteinUhlenbeck sigma(Temp sigma) {
        return sigma(date -> sigma);
    }

    /**
     * Updates the (time-varying) sigma function.
     * <p>
     * An exception will be thrown if the function is null.
     */
    public OrnsteinUhlenbeck sigma(Function<LocalDate, Temp> sigma) {
        return new OrnsteinUhlenbeck(delegate, alpha, sigma, random);
    }

    /**
     * Updates the random number generator.
     */
    public OrnsteinUhlenbeck random(Random random) {
        return new OrnsteinUhlenbeck(delegate, alpha, sigma, random);
    }

    /**
     * Creates a new predictor backed by the supplied prediction method.
     * <p>
     * By default the random path will simply follow the underlying prediction.
     */
    public static OrnsteinUhlenbeck on(TempPredictor delegate) {
        return new OrnsteinUhlenbeck(delegate, 1.0, date -> Temp.kelvin(0), new Random());
    }
}
