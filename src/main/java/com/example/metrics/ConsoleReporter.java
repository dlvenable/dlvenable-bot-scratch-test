package com.example.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Periodically prints a snapshot of all registered metrics to standard
 * output. Intended for local development; production deployments should use
 * a real metrics backend.
 */
public class ConsoleReporter {
    private final MetricsRegistry registry;
    private final List<Gauge> gauges = new ArrayList<>();
    private final long intervalMillis;
    private Thread reporterThread;
    private volatile boolean running;

    public ConsoleReporter(final MetricsRegistry registry, final long intervalMillis) {
        this.registry = registry;
        this.intervalMillis = intervalMillis;
    }

    public void addGauge(final Gauge gauge) {
        gauges.add(gauge);
    }

    /** Starts the background reporting thread. */
    public void start() {
        running = true;
        reporterThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(intervalMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                report();
            }
        });
        reporterThread.start();
    }

    /** Stops the reporting thread. */
    public void stop() {
        running = false;
    }

    private void report() {
        final StringBuilder output = new StringBuilder();
        output.append("=== metrics ===\n");
        for (Map.Entry<String, Long> entry : registry.snapshot().entrySet()) {
            output.append(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        for (Gauge gauge : gauges) {
            output.append(gauge.getName() + "=" + gauge.value() + "\n");
        }
        System.out.print(output);
    }
}
