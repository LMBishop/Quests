package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.common.logger.QuestsLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class LogHistory {

    private boolean enabled;

    private final List<LogEntry> entries = new ArrayList<>();

    public LogHistory(boolean enabled) {
        this.enabled = enabled;
    }

    public void record(QuestsLogger.LoggingLevel type, Supplier<String> supplier) {
        if (enabled) {
            String entry = supplier.get();
            String thread = Thread.currentThread().getName();
            long time = System.currentTimeMillis();

            LogEntry logEntry = new LogEntry(entry, type, thread, time);

            synchronized (this) {
                entries.add(logEntry);
            }
        }
    }

    public synchronized List<LogEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static class LogEntry {
        private final String entry;
        private final QuestsLogger.LoggingLevel type;
        private final String thread;
        private final long time;

        public LogEntry(String entry, QuestsLogger.LoggingLevel type, String thread, long time) {
            this.entry = entry;
            this.type = type;
            this.thread = thread;
            this.time = time;
        }

        public String getEntry() {
            return entry;
        }

        public QuestsLogger.LoggingLevel getType() {
            return type;
        }

        public String getThread() {
            return thread;
        }

        public long getTime() {
            return time;
        }
    }
}
