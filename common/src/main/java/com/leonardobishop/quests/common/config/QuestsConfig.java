package com.leonardobishop.quests.common.config;

import java.util.List;

/**
 * The quests config stores configuration values for the plugin.
 */
public interface QuestsConfig {

    boolean loadConfig();

    String getString(String path);

    String getString(String path, String def);

    boolean getBoolean(String path);

    boolean getBoolean(String path, boolean def);

    int getInt(String path);

    int getInt(String path, int def);

    List<String> getStringList(String path);

    List<String> getStringList(String path, List<String> def);

}
