package co.abarr.weather.owm;

import java.util.Objects;

/**
 * Created by adam on 30/11/2020.
 */
public class Location implements Comparable<Location> {
    private final String name;

    private Location(String name) {
        this.name = Objects.requireNonNull(name);
        if (name.isBlank()) {
            throw new IllegalArgumentException("Invalid name: \"" + name + "\"");
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return name.equals(location.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(Location o) {
        return name.compareTo(o.name);
    }

    public static final Location CENTRAL_PARK = of("Central Park");

    /**
     * Creates a new location.
     * <p>
     * An exception will be thrown if the name is null, blank, or empty.
     */
    public static Location of(String name) {
        return new Location(name);
    }
}
