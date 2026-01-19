package com.leonardobishop.quests.common;

import com.leonardobishop.quests.common.versioning.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class VersionTest {

    @Test
    public void oldVersioning() {
        assertEquals("1.8-R0.1-SNAPSHOT", Version.fromString("1.8-R0.1-SNAPSHOT").toString());
        assertEquals("1.8.3-R0.1-SNAPSHOT", Version.fromString("1.8.3-R0.1-SNAPSHOT").toString());
        assertEquals("1.8.4-R0.1-SNAPSHOT", Version.fromString("1.8.4-R0.1-SNAPSHOT").toString());
        assertEquals("1.8.5-R0.1-SNAPSHOT", Version.fromString("1.8.5-R0.1-SNAPSHOT").toString());
        assertEquals("1.8.6-R0.1-SNAPSHOT", Version.fromString("1.8.6-R0.1-SNAPSHOT").toString());
        assertEquals("1.8.7-R0.1-SNAPSHOT", Version.fromString("1.8.7-R0.1-SNAPSHOT").toString());
        assertEquals("1.8.8-R0.1-SNAPSHOT", Version.fromString("1.8.8-R0.1-SNAPSHOT").toString());
        assertEquals("1.9-R0.1-SNAPSHOT", Version.fromString("1.9-R0.1-SNAPSHOT").toString());
        assertEquals("1.9.2-R0.1-SNAPSHOT", Version.fromString("1.9.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.9.4-R0.1-SNAPSHOT", Version.fromString("1.9.4-R0.1-SNAPSHOT").toString());
        assertEquals("1.10-R0.1-SNAPSHOT", Version.fromString("1.10-R0.1-SNAPSHOT").toString());
        assertEquals("1.10.2-R0.1-SNAPSHOT", Version.fromString("1.10.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.11-R0.1-SNAPSHOT", Version.fromString("1.11-R0.1-SNAPSHOT").toString());
        assertEquals("1.11.1-R0.1-SNAPSHOT", Version.fromString("1.11.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.11.2-R0.1-SNAPSHOT", Version.fromString("1.11.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.12-pre2-SNAPSHOT", Version.fromString("1.12-pre2-SNAPSHOT").toString());
        assertEquals("1.12-pre5-SNAPSHOT", Version.fromString("1.12-pre5-SNAPSHOT").toString());
        assertEquals("1.12-pre6-SNAPSHOT", Version.fromString("1.12-pre6-SNAPSHOT").toString());
        assertEquals("1.12-R0.1-SNAPSHOT", Version.fromString("1.12-R0.1-SNAPSHOT").toString());
        assertEquals("1.12.1-R0.1-SNAPSHOT", Version.fromString("1.12.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.12.2-R0.1-SNAPSHOT", Version.fromString("1.12.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.13-pre7-R0.1-SNAPSHOT", Version.fromString("1.13-pre7-R0.1-SNAPSHOT").toString());
        assertEquals("1.13-R0.1-SNAPSHOT", Version.fromString("1.13-R0.1-SNAPSHOT").toString());
        assertEquals("1.13.1-R0.1-SNAPSHOT", Version.fromString("1.13.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.13.2-R0.1-SNAPSHOT", Version.fromString("1.13.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.14-pre5-SNAPSHOT", Version.fromString("1.14-pre5-SNAPSHOT").toString());
        assertEquals("1.14-R0.1-SNAPSHOT", Version.fromString("1.14-R0.1-SNAPSHOT").toString());
        assertEquals("1.14.1-R0.1-SNAPSHOT", Version.fromString("1.14.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.14.2-R0.1-SNAPSHOT", Version.fromString("1.14.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.14.3-SNAPSHOT", Version.fromString("1.14.3-SNAPSHOT").toString());
        assertEquals("1.14.3-pre4-SNAPSHOT", Version.fromString("1.14.3-pre4-SNAPSHOT").toString());
        assertEquals("1.14.3-R0.1-SNAPSHOT", Version.fromString("1.14.3-R0.1-SNAPSHOT").toString());
        assertEquals("1.14.4-R0.1-SNAPSHOT", Version.fromString("1.14.4-R0.1-SNAPSHOT").toString());
        assertEquals("1.15-R0.1-SNAPSHOT", Version.fromString("1.15-R0.1-SNAPSHOT").toString());
        assertEquals("1.15.1-R0.1-SNAPSHOT", Version.fromString("1.15.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.15.2-R0.1-SNAPSHOT", Version.fromString("1.15.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.16.1-R0.1-SNAPSHOT", Version.fromString("1.16.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.16.2-R0.1-SNAPSHOT", Version.fromString("1.16.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.16.3-R0.1-SNAPSHOT", Version.fromString("1.16.3-R0.1-SNAPSHOT").toString());
        assertEquals("1.16.4-R0.1-SNAPSHOT", Version.fromString("1.16.4-R0.1-SNAPSHOT").toString());
        assertEquals("1.16.5-R0.1-SNAPSHOT", Version.fromString("1.16.5-R0.1-SNAPSHOT").toString());
        assertEquals("1.17-R0.1-SNAPSHOT", Version.fromString("1.17-R0.1-SNAPSHOT").toString());
        assertEquals("1.17.1-R0.1-SNAPSHOT", Version.fromString("1.17.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.18-rc3-R0.1-SNAPSHOT", Version.fromString("1.18-rc3-R0.1-SNAPSHOT").toString());
        assertEquals("1.18-pre5-R0.1-SNAPSHOT", Version.fromString("1.18-pre5-R0.1-SNAPSHOT").toString());
        assertEquals("1.18-pre8-R0.1-SNAPSHOT", Version.fromString("1.18-pre8-R0.1-SNAPSHOT").toString());
        assertEquals("1.18-R0.1-SNAPSHOT", Version.fromString("1.18-R0.1-SNAPSHOT").toString());
        assertEquals("1.18.1-R0.1-SNAPSHOT", Version.fromString("1.18.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.18.2-R0.1-SNAPSHOT", Version.fromString("1.18.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.19-R0.1-SNAPSHOT", Version.fromString("1.19-R0.1-SNAPSHOT").toString());
        assertEquals("1.19.1-R0.1-SNAPSHOT", Version.fromString("1.19.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.19.2-R0.1-SNAPSHOT", Version.fromString("1.19.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.19.3-R0.1-SNAPSHOT", Version.fromString("1.19.3-R0.1-SNAPSHOT").toString());
        assertEquals("1.19.4-R0.1-SNAPSHOT", Version.fromString("1.19.4-R0.1-SNAPSHOT").toString());
        assertEquals("1.20-R0.1-SNAPSHOT", Version.fromString("1.20-R0.1-SNAPSHOT").toString());
        assertEquals("1.20.1-experimental-SNAPSHOT", Version.fromString("1.20.1-experimental-SNAPSHOT").toString());
        assertEquals("1.20.1-R0.1-SNAPSHOT", Version.fromString("1.20.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.20.2-experimental-SNAPSHOT", Version.fromString("1.20.2-experimental-SNAPSHOT").toString());
        assertEquals("1.20.2-R0.1-SNAPSHOT", Version.fromString("1.20.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.20.3-R0.1-SNAPSHOT", Version.fromString("1.20.3-R0.1-SNAPSHOT").toString());
        assertEquals("1.20.4-experimental-SNAPSHOT", Version.fromString("1.20.4-experimental-SNAPSHOT").toString());
        assertEquals("1.20.4-R0.1-SNAPSHOT", Version.fromString("1.20.4-R0.1-SNAPSHOT").toString());
        assertEquals("1.20.5-R0.1-SNAPSHOT", Version.fromString("1.20.5-R0.1-SNAPSHOT").toString());
        assertEquals("1.20.6-experimental-SNAPSHOT", Version.fromString("1.20.6-experimental-SNAPSHOT").toString());
        assertEquals("1.20.6-R0.1-SNAPSHOT", Version.fromString("1.20.6-R0.1-SNAPSHOT").toString());
        assertEquals("1.21-R0.1-SNAPSHOT", Version.fromString("1.21-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.1-R0.1-SNAPSHOT", Version.fromString("1.21.1-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.2-R0.1-SNAPSHOT", Version.fromString("1.21.2-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.3-R0.1-SNAPSHOT", Version.fromString("1.21.3-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.4-R0.1-SNAPSHOT", Version.fromString("1.21.4-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.5-no-moonrise-SNAPSHOT", Version.fromString("1.21.5-no-moonrise-SNAPSHOT").toString());
        assertEquals("1.21.5-R0.1-SNAPSHOT", Version.fromString("1.21.5-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.6-R0.1-SNAPSHOT", Version.fromString("1.21.6-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.7-R0.1-SNAPSHOT", Version.fromString("1.21.7-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.8-R0.1-SNAPSHOT", Version.fromString("1.21.8-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.9-pre2-R0.1-SNAPSHOT", Version.fromString("1.21.9-pre2-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.9-pre3-R0.1-SNAPSHOT", Version.fromString("1.21.9-pre3-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.9-pre4-R0.1-SNAPSHOT", Version.fromString("1.21.9-pre4-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.9-rc1-R0.1-SNAPSHOT", Version.fromString("1.21.9-rc1-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.9-R0.1-SNAPSHOT", Version.fromString("1.21.9-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.10-R0.1-SNAPSHOT", Version.fromString("1.21.10-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.11-pre3-R0.1-SNAPSHOT", Version.fromString("1.21.11-pre3-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.11-pre4-R0.1-SNAPSHOT", Version.fromString("1.21.11-pre4-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.11-pre5-R0.1-SNAPSHOT", Version.fromString("1.21.11-pre5-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.11-rc1-R0.1-SNAPSHOT", Version.fromString("1.21.11-rc1-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.11-rc2-R0.1-SNAPSHOT", Version.fromString("1.21.11-rc2-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.11-rc3-R0.1-SNAPSHOT", Version.fromString("1.21.11-rc3-R0.1-SNAPSHOT").toString());
        assertEquals("1.21.11-R0.1-SNAPSHOT", Version.fromString("1.21.11-R0.1-SNAPSHOT").toString());
    }

    @Test
    void weirdVersioning() {
        // To ensure future compatibility no matter what monster they come up with

        assertEquals("26", Version.fromString("26").toString());
        assertEquals("26-SNAPSHOT", Version.fromString("26-SNAPSHOT").toString());
        assertEquals("26-R0.1-SNAPSHOT", Version.fromString("26-R0.1-SNAPSHOT").toString());

        assertEquals("26.0", Version.fromString("26.0").toString());
        assertEquals("26.0-SNAPSHOT", Version.fromString("26.0-SNAPSHOT").toString());
        assertEquals("26.0-R0.1-SNAPSHOT", Version.fromString("26.0-R0.1-SNAPSHOT").toString());

        assertEquals("26.1", Version.fromString("26.1").toString());
        assertEquals("26.1-SNAPSHOT", Version.fromString("26.1-SNAPSHOT").toString());
        assertEquals("26.1-R0.1-SNAPSHOT", Version.fromString("26.1-R0.1-SNAPSHOT").toString());

        assertEquals("26.1.0", Version.fromString("26.1.0").toString());
        assertEquals("26.1.0-SNAPSHOT", Version.fromString("26.1.0-SNAPSHOT").toString());
        assertEquals("26.1.0-R0.1-SNAPSHOT", Version.fromString("26.1.0-R0.1-SNAPSHOT").toString());

        assertEquals("26.1.1", Version.fromString("26.1.1").toString());
        assertEquals("26.1.1-SNAPSHOT", Version.fromString("26.1.1-SNAPSHOT").toString());
        assertEquals("26.1.1-R0.1-SNAPSHOT", Version.fromString("26.1.1-R0.1-SNAPSHOT").toString());

        assertEquals("26.1.1.0", Version.fromString("26.1.1.0").toString());
        assertEquals("26.1.1.0-SNAPSHOT", Version.fromString("26.1.1.0-SNAPSHOT").toString());
        assertEquals("26.1.1.0-R0.1-SNAPSHOT", Version.fromString("26.1.1.0-R0.1-SNAPSHOT").toString());

        assertEquals("26.1.1.1", Version.fromString("26.1.1.1").toString());
        assertEquals("26.1.1.1-SNAPSHOT", Version.fromString("26.1.1.1-SNAPSHOT").toString());
        assertEquals("26.1.1.1-R0.1-SNAPSHOT", Version.fromString("26.1.1.1-R0.1-SNAPSHOT").toString());
    }

    @Test
    void compareTo() {
        assertEquals(0, Version.fromString("26.1.2.3.000.0.00000.0").compareTo(Version.fromString("26.1.2.3")));
        assertEquals(-1, Version.fromString("26.0.0").compareTo(Version.fromString("26.1")));
        assertEquals(3, Version.fromString("26.3.0").compareTo(Version.fromString("26")));
        assertEquals(-5, Version.fromString("26.0.5").compareTo(Version.fromString("26.5.0")));
    }
}
