package org.example.realtime_event_ticketing_system.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();
    public ReentrantLock getLock(Long eventId) {
        return lockMap.computeIfAbsent(eventId, id -> new ReentrantLock());
    }
}
