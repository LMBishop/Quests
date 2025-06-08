package com.leonardobishop.quests.bukkit.hook.papi;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.util.Modern;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Modern(type = Modern.Type.FULL)
@NullMarked
public final class PlaceholderAPIHook implements AbstractPlaceholderAPIHook {

    @Nullable
    private QuestsPlaceholders expansion;

    public PlaceholderAPIHook() {
        this.expansion = null;
    }

    @Contract(pure = true, value = "_, null -> null; _, !null -> !null")
    public @Nullable String replacePlaceholders(final @Nullable Player player, final @Nullable String text) {
        if (text == null) {
            return null;
        }

        final int firstIndex = text.indexOf('%');
        if (firstIndex == -1) {
            return text;
        }

        final int lastIndex = text.lastIndexOf('%');
        if (lastIndex == firstIndex) {
            return text;
        }

        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public void registerExpansion(final BukkitQuestsPlugin plugin) {
        this.expansion = new QuestsPlaceholders(plugin);
        this.expansion.register();
    }

    @Override
    public void unregisterExpansion() {
        if (this.expansion != null) {
            this.expansion.unregister();
        }
    }
}
