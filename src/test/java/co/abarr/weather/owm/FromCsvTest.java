package co.abarr.weather.owm;

import co.abarr.weather.temp.Temp;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by adam on 01/12/2020.
 */
class FromCsvTest {
    @Test
    void readFrom_ValidCsv_ShouldReturnCorrectNumberOfRows() {
        List<OwmRow> rows = parseRows("""
            dt,dt_iso,timezone,city_name,temp
            283996800,1979-01-01 00:00:00 +0000 UTC,-18000,Central Park,280.32
            283996800,1979-01-01 00:00:00 +0000 UTC,-18000,Central Park,280.32
            283996800,1979-01-01 00:00:00 +0000 UTC,-18000,Central Park,280.32
            284000400,1979-01-01 01:00:00 +0000 UTC,-18000,Central Park,280.29
            284000400,1979-01-01 01:00:00 +0000 UTC,-18000,Central Park,280.29
            284000400,1979-01-01 01:00:00 +0000 UTC,-18000,Central Park,280.29
            """);
        assertThat(rows).hasSize(6);
    }

    @Test
    void readFrom_ValidCsv_ShouldParseTimeCorrectly() {
        OwmRow row = parseRow("""
            dt,dt_iso,timezone,city_name,temp
            284000400,1979-01-01 01:00:00 +0000 UTC,-18000,Central Park,280.32
            """);
        assertThat(row.time()).isEqualTo("1979-01-01T01:00:00Z");
    }

    @Test
    void readFrom_ValidCsv_ShouldParseLocationCorrectly() {
        OwmRow row = parseRow("""
            dt,dt_iso,timezone,city_name,temp
            284000400,1979-01-01 01:00:00 +0000 UTC,-18000,Central Park,280.32
            """);
        assertThat(row.location()).isEqualTo(Location.of("Central Park"));
    }

    @Test
    void readFrom_ValidCsv_ShouldParseDateCorrectly() {
        OwmRow row = parseRow("""
            dt,dt_iso,timezone,city_name,temp
            284000400,1979-01-01 01:00:00 +0000 UTC,-18000,Central Park,280.32
            """);
        assertThat(row.date()).isEqualTo("1978-12-31");
    }

    @Test
    void readFrom_ValidCsv_ShouldParseTempCorrectly() {
        OwmRow row = parseRow("""
            dt,dt_iso,timezone,city_name,temp
            284000400,1979-01-01 01:00:00 +0000 UTC,-18000,Central Park,280.32
            """);
        assertThat(row.temp()).isEqualTo(Temp.kelvin(280.32));
    }

    @Test
    void readFrom_MissingDtColumn_ShouldThrowException() {
        assertThatThrownBy(
            () -> parseRow("""
                dt_iso,timezone,city_name,temp
                1979-01-01 01:00:00 +0000 UTC,-18000,Central Park,280.32
                """)
        ).isInstanceOf(
            IllegalArgumentException.class
        );
    }

    @Test
    void readFrom_InvalidDtColumn_ShouldThrowException() {
        assertThatThrownBy(
            () -> parseRow("""
                dt,dt_iso,timezone,city_name,temp
                INVALID,1979-01-01 01:00:00 +0000 UTC,-18000,Central Park,280.32
                    """)
        ).isInstanceOf(
            IllegalArgumentException.class
        );
    }

    @Test
    void readFrom_InvalidTempColumn_ShouldThrowException() {
        assertThatThrownBy(
            () -> parseRow("""
                dt,dt_iso,timezone,city_name,temp
                284000400,1979-01-01 01:00:00 +0000 UTC,-18000,Central Park,INVALID
                    """)
        ).isInstanceOf(
            IllegalArgumentException.class
        );
    }

    @Test
    @Tag("integration")
    void readFromCentralParkCsv_WhenExists_ShouldLoadCorrectNumberOfRows() {
        List<OwmRow> rows = FromCsv.readFromCentralParkCsv();
        assertThat(rows).hasSize(405013);
    }

    private List<OwmRow> parseRows(String s) {
        return FromCsv.readFrom(s);
    }

    private OwmRow parseRow(String s) {
        return parseRows(s).get(0);
    }
}