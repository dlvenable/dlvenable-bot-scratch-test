package com.example.metrics;

import java.util.function.DoubleSupplier;

/**
 * A gauge samples a value on demand, e.g. queue depth or heap usage. The
 * supplier is invoked at read time; implementations should be fast and
 * side-effect free.
 */
public class Gauge {
    private final MetricName name;
    private final DoubleSupplier supplier;

    public Gauge(final MetricName name, final DoubleSupplier supplier) {
        this.name = name;
        this.supplier = supplier;
    }

    public MetricName getName() {
        return name;
    }

    public double value() {
        return supplier.getAsDouble();
    }
}
