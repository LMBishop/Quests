package com.leonardobishop.quests.common.logger;

/**
 * The quests logger allows for user customisation of what gets logged.
 * Implementations of QuestsLogger should wrap the logger of that platform.
 */
public interface QuestsLogger {

    LoggingLevel getServerLoggingLevel();

    void setServerLoggingLevel(LoggingLevel serverLoggingLevel);

    void log(String str, LoggingLevel level);

    void debug(String str);

    void info(String str);

    void warning(String str);

    void severe(String str);

    enum LoggingLevel {
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

        public static LoggingLevel fromNumber(int number) {
            for (LoggingLevel level : LoggingLevel.values()) {
                if (level.getNumericVerbosity() == number) {
                    return level;
                }
            }
            return LoggingLevel.INFO;
        }
    }
}
