package com.leonardobishop.quests.bukkit.hook.papi;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.util.Modern;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Modern(type = Modern.Type.FULL)
@NullMarked
public interface AbstractPlaceholderAPIHook {

    @Contract(pure = true, value = "_, null -> null; _, !null -> !null")
    @Nullable String replacePlaceholders(@Nullable Player player, @Nullable String text);

    void registerExpansion(BukkitQuestsPlugin plugin);

    void unregisterExpansion();
}
