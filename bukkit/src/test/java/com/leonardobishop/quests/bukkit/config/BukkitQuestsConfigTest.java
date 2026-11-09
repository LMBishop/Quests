package com.leonardobishop.quests.bukkit.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BukkitQuestsConfigTest {
    @TempDir Path directory;

    @Test void cachedTrueAndFalseKeepTheirValuesUntilReload() throws Exception {
        Path file = directory.resolve("config.yml");
        Files.writeString(file, "enabled: true\ndisabled: false\n");
        BukkitQuestsConfig config = new BukkitQuestsConfig(file.toFile());
        assertTrue(config.loadConfig());
        assertTrue(config.getBoolean("enabled"));
        assertFalse(config.getBoolean("disabled", true));
        config.getConfig().set("enabled", false);
        config.getConfig().set("disabled", true);
        assertTrue(config.getBoolean("enabled", false));
        assertFalse(config.getBoolean("disabled"));
        Files.writeString(file, "enabled: false\ndisabled: true\n");
        assertTrue(config.loadConfig());
        assertFalse(config.getBoolean("enabled"));
        assertTrue(config.getBoolean("disabled"));
    }

    @Test void theFirstCachedDefaultIsSharedByBothOverloads() throws Exception {
        Path file = directory.resolve("config.yml");
        Files.writeString(file, "{}");
        BukkitQuestsConfig config = new BukkitQuestsConfig(file.toFile());
        assertTrue(config.loadConfig());
        assertTrue(config.getBoolean("first-true", true));
        assertTrue(config.getBoolean("first-true", false));
        assertTrue(config.getBoolean("first-true"));
        assertFalse(config.getBoolean("first-false"));
        assertFalse(config.getBoolean("first-false", true));
    }
}
