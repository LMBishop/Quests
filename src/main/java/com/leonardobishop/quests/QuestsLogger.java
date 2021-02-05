package com.leonardobishop.quests;

public class QuestsLogger {

    private final Quests plugin;
    private LoggingLevel serverLoggingLevel;

    public QuestsLogger(Quests plugin, LoggingLevel serverLoggingLevel) {
        this.plugin = plugin;
        this.serverLoggingLevel = serverLoggingLevel;
    }

    public LoggingLevel getServerLoggingLevel() {
        return serverLoggingLevel;
    }

    public void setServerLoggingLevel(LoggingLevel serverLoggingLevel) {
        this.serverLoggingLevel = serverLoggingLevel;
    }

    public void log(String str, LoggingLevel level) {
        if (serverLoggingLevel.getNumericVerbosity() < level.getNumericVerbosity()) {
            return;
        }
        switch (level) {
            case DEBUG:
                plugin.getLogger().info("Debug: " + str);
                break;
            case INFO:
                plugin.getLogger().info(str);
                break;
            case ERROR:
                plugin.getLogger().severe(str);
                break;
            case WARNING:
                plugin.getLogger().warning(str);
                break;
        }
    }

    public void debug(String str) {
        log(str, LoggingLevel.DEBUG);
    }

    public void info(String str) {
        log(str, LoggingLevel.INFO);
    }

    public void warning(String str) {
        log(str, LoggingLevel.WARNING);
    }

    public void severe(String str) {
        log(str, LoggingLevel.ERROR);
    }

    public enum LoggingLevel {
        ERROR(0),
        WARNING(1),
        INFO(2),
        DEBUG(3);

        private int numericVerbosity;

        LoggingLevel(int number) {
            numericVerbosity = number;
        }

        public int getNumericVerbosity() {
            return numericVerbosity;
        }

        static LoggingLevel fromNumber(int number) {
            for (LoggingLevel level : LoggingLevel.values()) {
                if (level.getNumericVerbosity() == number) {
                    return level;
                }
            }
            return LoggingLevel.INFO;
        }
    }

}
