package com.example.metrics;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A validated metric name: dot-separated lowercase segments, e.g.
 * {@code pipeline.sink.records_written}.
 */
public final class MetricName {
    private static final Pattern VALID_NAME = Pattern.compile("[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*");

    private final String value;

    private MetricName(final String value) {
        this.value = value;
    }

    /**
     * Creates a metric name, validating the format.
     *
     * @throws IllegalArgumentException if the name is not a valid metric name
     */
    public static MetricName of(final String name) {
        Objects.requireNonNull(name, "name must not be null");
        if (!VALID_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid metric name: " + name);
        }
        return new MetricName(name);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MetricName)) {
            return false;
        }
        return value.equals(((MetricName) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
