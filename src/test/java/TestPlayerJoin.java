import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.events.EventPlayerJoin;
import junit.framework.TestCase;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.util.UUID;

public class TestPlayerJoin extends TestCase {

    private Quests quests;

    @Override
    public void setUp() throws Exception {
        FakeServer fakeServer = new FakeServer();
        Bukkit.setServer(fakeServer);
        PluginDescriptionFile pluginDescriptionFile =
                new PluginDescriptionFile("Quests", "TEST", "com.leonardobishop.quests.Quests");
        quests = new Quests(new JavaPluginLoader(fakeServer), pluginDescriptionFile, null, null);
        quests.prepareForTest();
    }

    public void testJoin() {
        FakePlayer player = new FakePlayer("bob", new UUID(0L, 0L));
        PlayerJoinEvent joinEvent = new FakePlayerJoinEvent(player, "bob joined");

        EventPlayerJoin eventToTest = new EventPlayerJoin();
        eventToTest.onEvent(joinEvent);

        assertNotNull(quests.getPlayerManager().getPlayer(player.getUniqueId()));
    }
}