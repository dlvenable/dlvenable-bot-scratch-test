package com.example.metrics;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

/**
 * Central registry of counters, keyed by metric name. Create one registry
 * per component and share it with the code that records events.
 */
public class MetricsRegistry {
    private static final long DEFAULT_WINDOW_MILLIS = 60_000;

    private final Map<String, SlidingWindowCounter> counters = new HashMap<>();
    private final Clock clock;

    public MetricsRegistry(final Clock clock) {
        this.clock = clock;
    }

    /**
     * Returns the counter for the given name, creating it on first use.
     */
    public SlidingWindowCounter counter(final String name) {
        SlidingWindowCounter counter = counters.get(name);
        if (counter == null) {
            counter = new SlidingWindowCounter(DEFAULT_WINDOW_MILLIS, clock);
            counters.put(name, counter);
        }
        return counter;
    }

    /**
     * Records an event against the named counter. Invalid metric names are
     * ignored so that instrumentation can never break the caller.
     */
    public void record(final String name) {
        try {
            MetricName.of(name);
            counter(name).record();
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Returns the current counts for every registered metric.
     */
    public Map<String, Long> snapshot() {
        final Map<String, Long> result = new HashMap<>();
        for (Map.Entry<String, SlidingWindowCounter> entry : counters.entrySet()) {
            result.put(entry.getKey(), entry.getValue().count());
        }
        return result;
    }

    /**
     * Returns the underlying counters map, for advanced use cases such as
     * bulk inspection.
     */
    public Map<String, SlidingWindowCounter> getCounters() {
        return counters;
    }
}
