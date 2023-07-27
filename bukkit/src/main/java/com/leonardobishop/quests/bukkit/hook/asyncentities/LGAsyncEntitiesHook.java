package com.leonardobishop.quests.bukkit.hook.asyncentities;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.type.MobkillingTaskType;
import com.leonardobishop.quests.common.player.QPlayer;
import ltd.lemongaming.asyncentities.event.AsyncMobDropEvent;
import ltd.lemongaming.asyncentities.mobs.AsyncMob;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LGAsyncEntitiesHook implements Listener {

    private final BukkitQuestsPlugin plugin;
    private final MobkillingTaskType mobkillingTaskType;

    public LGAsyncEntitiesHook(@NotNull BukkitQuestsPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
        this.mobkillingTaskType = (MobkillingTaskType) plugin.getTaskTypeManager().getTaskType(MobkillingTaskType.TASK_TYPE_NAME);
    }

    @EventHandler
    public void onAsyncMobDeathEvent(AsyncMobDropEvent event) {
        final Entity entityKiller = event.getKiller();
        if (entityKiller == null || entityKiller.getType() != EntityType.PLAYER) {
            return;
        }

        final Player player = (Player) entityKiller;
        final AsyncMob asyncMob = event.getAsyncMob();

        if (player.hasMetadata("NPC")) {
            return;
        }

        QPlayer qPlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (qPlayer == null) {
            return;
        }

        final EntityType entityType = asyncMob.getType();

        final Class<? extends Entity> entityClass = entityType.getEntityClass();
        Objects.requireNonNull(entityClass);

        final boolean animals = entityClass.isAssignableFrom(Animals.class);
        final boolean monster = entityClass.isAssignableFrom(Monster.class);

        mobkillingTaskType.handleEntityDeath(
            player,
            qPlayer,
            entityType,
            asyncMob.getCustomName().orElse(null),
            animals,
            monster
        );
    }

}
