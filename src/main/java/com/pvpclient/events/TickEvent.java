package com.pvpclient.events;

/**
 * Fired every client game tick (20 times per second).
 */
public final class TickEvent {

    /** Tick number, incremented each tick. Wraps around at Integer.MAX_VALUE. */
    private final int tickCount;

    public TickEvent(int tickCount) {
        this.tickCount = tickCount;
    }

    public int getTickCount() {
        return tickCount;
    }
}
