package co.abarr.weather.owm;

import co.abarr.weather.temp.Temp;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static OwmBatch readFromCentralParkCsv() {
        return FromCsv.readFrom(Paths.get("./data/central_park.csv"));
    }

    /**
     * Reads a csv of rows from a file.
     */
    public static OwmBatch readFrom(Path file) {
        long t0 = System.currentTimeMillis();
        try {
            OwmBatch rows = readFrom(Files.newBufferedReader(file));
            logger.info("Took {}ms to read {} rows from {}", System.currentTimeMillis() - t0, rows.size(), file);
            return rows;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading " + file, e);
        }
    }

    /**
     * Reads a csv of rows from a string.
     */
    public static OwmBatch readFrom(String s) {
        try {
            return readFrom(new StringReader(s));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Reads a csv of rows from an input reader.
     */
    public static OwmBatch readFrom(Reader reader) throws IOException {
        List<OwmRow> rows = new ArrayList<>();
        try (CSVReader csv = new CSVReader(reader)) {
            List<String> header = Arrays.asList(csv.readNextSilently());
            int dtIndex = columnIndexOf(header, "dt");
            int timezoneIndex = columnIndexOf(header, "timezone");
            int cityNameIndex = columnIndexOf(header, "city_name");
            int tempIndex = columnIndexOf(header, "temp");
            String[] row;
            while ((row = csv.readNextSilently()) != null) {
                try {
                    Location location = Location.of(row[cityNameIndex]);
                    Instant time = Instant.ofEpochSecond(Long.parseLong(row[dtIndex]));
                    ZoneOffset zone = ZoneOffset.ofTotalSeconds(Integer.parseInt(row[timezoneIndex]));
                    Temp temp = Temp.kelvin(Double.parseDouble(row[tempIndex]));
                    rows.add(OwmRow.of(location, time, zone, temp));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Error parsing row " + Arrays.toString(row), e);
                }
            }
        }
        return OwmBatch.of(rows);
    }

    private static int columnIndexOf(List<String> header, String column) {
        int index = header.indexOf(column);
        if (index == -1) {
            throw new IllegalArgumentException("Column \"" + column + "\" not found in " + header);
        } else {
            return index;
        }
    }

    private FromCsv() {}
}
