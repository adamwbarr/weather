package co.abarr.weather.owm;

import co.abarr.weather.temp.Temp;
import co.abarr.weather.temp.TempIndexer;
import co.abarr.weather.temp.TempSeries;
import co.abarr.weather.temp.predict.TempPredictor;
import co.abarr.weather.temp.predict.TempTrainer;
import co.abarr.weather.time.DateRange;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by adam on 04/12/2020.
 */
public class CentralParkIntegrationTests {
    @Test
    void readFromCentralParkCsv_WhenExists_ShouldLoadCorrectNumberOfRows() {
        List<OwmRow> rows = FromCsv.readFromCentralParkCsv();
        assertThat(rows).hasSize(405013);
    }

    @Test
    void alatonModel_WhenDataExists_ShouldProduceDifferentHddOnEachRun() {
        TempPredictor model = TempTrainer.ALATON.train(FromCsv.readFromCentralParkCsv().maxs().toFahrenheit());
        DateRange range = DateRange.year(2020);
        Temp hdd1 = model.predict(range).apply(TempIndexer.HDD_65);
        Temp hdd2 = model.predict(range).apply(TempIndexer.HDD_65);
        assertThat(hdd1).isNotEqualTo(hdd2);
    }
}
