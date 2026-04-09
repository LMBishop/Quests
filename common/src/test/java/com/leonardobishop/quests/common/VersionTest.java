package com.leonardobishop.quests.common;

import com.leonardobishop.quests.common.versioning.Version;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class VersionTest {

    static final List<String> ACTUAL_VERSION_LIST = List.of(
            "1.8-R0.1-SNAPSHOT",
            "1.8.3-R0.1-SNAPSHOT",
            "1.8.4-R0.1-SNAPSHOT",
            "1.8.5-R0.1-SNAPSHOT",
            "1.8.6-R0.1-SNAPSHOT",
            "1.8.7-R0.1-SNAPSHOT",
            "1.8.8-R0.1-SNAPSHOT",
            "1.9-R0.1-SNAPSHOT",
            "1.9.2-R0.1-SNAPSHOT",
            "1.9.4-R0.1-SNAPSHOT",
            "1.10-R0.1-SNAPSHOT",
            "1.10.2-R0.1-SNAPSHOT",
            "1.11-R0.1-SNAPSHOT",
            "1.11.1-R0.1-SNAPSHOT",
            "1.11.2-R0.1-SNAPSHOT",
            "1.12-pre2-SNAPSHOT",
            "1.12-pre5-SNAPSHOT",
            "1.12-pre6-SNAPSHOT",
            "1.12-R0.1-SNAPSHOT",
            "1.12.1-R0.1-SNAPSHOT",
            "1.12.2-R0.1-SNAPSHOT",
            "1.13-pre7-R0.1-SNAPSHOT",
            "1.13-R0.1-SNAPSHOT",
            "1.13.1-R0.1-SNAPSHOT",
            "1.13.2-R0.1-SNAPSHOT",
            "1.14-pre5-SNAPSHOT",
            "1.14-R0.1-SNAPSHOT",
            "1.14.1-R0.1-SNAPSHOT",
            "1.14.2-R0.1-SNAPSHOT",
            "1.14.3-SNAPSHOT",
            "1.14.3-pre4-SNAPSHOT",
            "1.14.3-R0.1-SNAPSHOT",
            "1.14.4-R0.1-SNAPSHOT",
            "1.15-R0.1-SNAPSHOT",
            "1.15.1-R0.1-SNAPSHOT",
            "1.15.2-R0.1-SNAPSHOT",
            "1.16.1-R0.1-SNAPSHOT",
            "1.16.2-R0.1-SNAPSHOT",
            "1.16.3-R0.1-SNAPSHOT",
            "1.16.4-R0.1-SNAPSHOT",
            "1.16.5-R0.1-SNAPSHOT",
            "1.17-R0.1-SNAPSHOT",
            "1.17.1-R0.1-SNAPSHOT",
            "1.18-rc3-R0.1-SNAPSHOT",
            "1.18-pre5-R0.1-SNAPSHOT",
            "1.18-pre8-R0.1-SNAPSHOT",
            "1.18-R0.1-SNAPSHOT",
            "1.18.1-R0.1-SNAPSHOT",
            "1.18.2-R0.1-SNAPSHOT",
            "1.19-R0.1-SNAPSHOT",
            "1.19.1-R0.1-SNAPSHOT",
            "1.19.2-R0.1-SNAPSHOT",
            "1.19.3-R0.1-SNAPSHOT",
            "1.19.4-R0.1-SNAPSHOT",
            "1.20-R0.1-SNAPSHOT",
            "1.20.1-experimental-SNAPSHOT",
            "1.20.1-R0.1-SNAPSHOT",
            "1.20.2-experimental-SNAPSHOT",
            "1.20.2-R0.1-SNAPSHOT",
            "1.20.3-R0.1-SNAPSHOT",
            "1.20.4-experimental-SNAPSHOT",
            "1.20.4-R0.1-SNAPSHOT",
            "1.20.5-R0.1-SNAPSHOT",
            "1.20.6-experimental-SNAPSHOT",
            "1.20.6-R0.1-SNAPSHOT",
            "1.21-R0.1-SNAPSHOT",
            "1.21.1-R0.1-SNAPSHOT",
            "1.21.2-R0.1-SNAPSHOT",
            "1.21.3-R0.1-SNAPSHOT",
            "1.21.4-R0.1-SNAPSHOT",
            "1.21.5-no-moonrise-SNAPSHOT",
            "1.21.5-R0.1-SNAPSHOT",
            "1.21.6-R0.1-SNAPSHOT",
            "1.21.7-R0.1-SNAPSHOT",
            "1.21.8-R0.1-SNAPSHOT",
            "1.21.9-pre2-R0.1-SNAPSHOT",
            "1.21.9-pre3-R0.1-SNAPSHOT",
            "1.21.9-pre4-R0.1-SNAPSHOT",
            "1.21.9-rc1-R0.1-SNAPSHOT",
            "1.21.9-R0.1-SNAPSHOT",
            "1.21.10-R0.1-SNAPSHOT",
            "1.21.11-pre3-R0.1-SNAPSHOT",
            "1.21.11-pre4-R0.1-SNAPSHOT",
            "1.21.11-pre5-R0.1-SNAPSHOT",
            "1.21.11-rc1-R0.1-SNAPSHOT",
            "1.21.11-rc2-R0.1-SNAPSHOT",
            "1.21.11-rc3-R0.1-SNAPSHOT",
            "1.21.11-R0.1-SNAPSHOT",
            "26.1.1.build.28-alpha"
    );

    @Test
    void actualVersioning() {
        for (final String actualVersion : ACTUAL_VERSION_LIST) {
            assertEquals(actualVersion, Version.fromString(actualVersion).toString());
        }
    }

    static final List<String> WEIRD_VERSION_LIST = List.of(
            "26",
            "26-SNAPSHOT",
            "26-R0.1-SNAPSHOT",

            "26.0",
            "26.0-SNAPSHOT",
            "26.0-R0.1-SNAPSHOT",

            "26.1",
            "26.1-SNAPSHOT",
            "26.1-R0.1-SNAPSHOT",

            "26.1.0",
            "26.1.0-SNAPSHOT",
            "26.1.0-R0.1-SNAPSHOT",

            "26.1.1",
            "26.1.1-SNAPSHOT",
            "26.1.1-R0.1-SNAPSHOT",

            "26.1.1.0",
            "26.1.1.0-SNAPSHOT",
            "26.1.1.0-R0.1-SNAPSHOT",

            "26.1.1.1",
            "26.1.1.1-SNAPSHOT",
            "26.1.1.1-R0.1-SNAPSHOT"
    );

    @Test
    void weirdVersioning() {
        // To ensure future compatibility no matter what monster they come up with

        for (final String actualVersion : WEIRD_VERSION_LIST) {
            assertEquals(actualVersion, Version.fromString(actualVersion).toString());
        }
    }

    @Test
    void compareTo() {
        assertEquals(0, Version.fromString("26.1.2.3.000.0.00000.0").compareTo(Version.fromString("26.1.2.3")));
        assertEquals(-1, Version.fromString("26.0.0").compareTo(Version.fromString("26.1")));
        assertEquals(3, Version.fromString("26.3.0").compareTo(Version.fromString("26")));
        assertEquals(-5, Version.fromString("26.0.5").compareTo(Version.fromString("26.5.0")));
    }
}
