package org.example.realtime_event_ticketing_system.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages locks for concurrent access to event resources, ensuring thread safety and preventing data inconsistencies.
 * Provides a way to acquire and release locks for specific events, allowing for synchronized access to event data.
 */

public class LockManager {
    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();
    public ReentrantLock getLock(Long eventId) {
        return lockMap.computeIfAbsent(eventId, id -> new ReentrantLock());
    }
}
