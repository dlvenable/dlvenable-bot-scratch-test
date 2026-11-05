package com.example.metrics;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Counts events over a fixed trailing time window. Events older than the
 * window are discarded lazily on access.
 *
 * <p>This class is not thread-safe; callers must provide external
 * synchronization if shared across threads.
 */
public class SlidingWindowCounter {
    private final Deque<Instant> events = new ArrayDeque<>();
    private final long windowMillis;
    private final Clock clock;

    public SlidingWindowCounter(final long windowMillis, final Clock clock) {
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("windowMillis must be positive, got: " + windowMillis);
        }
        this.windowMillis = windowMillis;
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    /** Records a single event at the current time. */
    public void record() {
        events.addLast(clock.instant());
    }

    /** Returns the number of events within the trailing window. */
    public long count() {
        evictExpired();
        return events.size();
    }

    private void evictExpired() {
        final Instant cutoff = clock.instant().minusMillis(windowMillis);
        while (!events.isEmpty() && events.peekFirst().isBefore(cutoff)) {
            events.removeFirst();
        }
    }
}
