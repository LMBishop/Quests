package com.leonardobishop.quests.bukkit.hook.papi.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStack;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.quest.Category;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.util.Modern;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Modern(type = Modern.Type.FULL)
@NullMarked
public final class DisplayNameUtil {

    public static @Nullable String getStrippedDisplayName(final BukkitQuestsPlugin plugin, final Quest quest) {
        final QItemStack item = plugin.getQItemStackRegistry().getQuestItemStack(quest);

        if (item == null) {
            return null;
        }

        final String displayName = item.getName();
        return Chat.legacyStrip(displayName);
    }

    public static @Nullable String getStrippedDisplayName(final BukkitQuestsPlugin plugin, final Category category) {
        final ItemStack item = plugin.getQItemStackRegistry().getCategoryItemStack(category);

        if (item == null) {
            return null;
        }

        //noinspection deprecation
        final String displayName = item.getItemMeta().getDisplayName();
        return Chat.legacyStrip(displayName);
    }
}
