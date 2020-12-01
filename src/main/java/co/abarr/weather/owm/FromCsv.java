package co.abarr.weather.owm;

import co.abarr.weather.location.Location;
import co.abarr.weather.temp.Temp;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads rows from csvs.
 * <p>
 * Created by adam on 01/12/2020.
 */
class FromCsv {
    private static final Logger logger = LoggerFactory.getLogger(FromCsv.class);

    /**
     * Rows parsed from central_park.csv download file.
     */
    public static List<OwmRow> readFromCentralParkCsv() {
        return FromCsv.readFrom(Paths.get("./data/central_park.csv"));
    }

    /**
     * Reads a csv of rows from a file.
     */
    public static List<OwmRow> readFrom(Path file) {
        long t0 = System.currentTimeMillis();
        try {
            List<OwmRow> rows = readFrom(Files.newBufferedReader(file));
            logger.info("Took {}ms to read {} rows from {}", System.currentTimeMillis() - t0, rows.size(), file);
            return rows;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading " + file, e);
        }
    }

    /**
     * Reads a csv of rows from a string.
     */
    public static List<OwmRow> readFrom(String s) {
        return readFrom(new StringReader(s));
    }

    /**
     * Reads a csv of rows from an input reader.
     */
    public static List<OwmRow> readFrom(Reader reader) {
        CsvToBean<RawRow> csv = new CsvToBeanBuilder<RawRow>(reader).withType(RawRow.class).build();
        List<OwmRow> rows = new ArrayList<>();
        for (RawRow raw : csv) {
            OwmRow row = OwmRow.of(
                Location.of(raw.city_name),
                Instant.ofEpochSecond(raw.dt),
                Temp.kelvin(raw.temp)
            );
            rows.add(row);
        }
        return rows;
    }

    public static class RawRow {
        public long dt;
        public String city_name;
        public double temp;
    }

    private FromCsv() {}
}
