package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStack;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.bukkit.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuestMenuElement extends MenuElement {

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig config;
    private final QPlayer owner;
    private final String questId;

    public QuestMenuElement(BukkitQuestsPlugin plugin, QPlayer owner, String questId) {
        this.plugin = plugin;
        this.config = (BukkitQuestsConfig) plugin.getQuestsConfig();
        this.owner = owner;
        this.questId = questId;
    }

    public QPlayer getOwner() {
        return owner;
    }

    public String getQuestId() {
        return questId;
    }

    @Override
    public ItemStack asItemStack() {
        Quest quest = plugin.getQuestManager().getQuestById(questId);
        QuestProgress questProgress = owner.getQuestProgressFile().getQuestProgress(quest);
        QuestStartResult status = owner.canStartQuest(quest);
        long cooldown = owner.getQuestProgressFile().getCooldownFor(quest);
        QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(quest);

        if (status == QuestStartResult.QUEST_LOCKED) {
            List<String> quests = new ArrayList<>();
            for (String requirement : quest.getRequirements()) {
                Quest requirementQuest = plugin.getQuestManager().getQuestById(requirement);
                if (!owner.getQuestProgressFile().hasQuestProgress(requirementQuest) ||
                        !owner.getQuestProgressFile().getQuestProgress(requirementQuest).isCompletedBefore()) {
                    quests.add(Chat.strip(plugin.getQItemStackRegistry().getQuestItemStack(requirementQuest).getName()));
                }
            }
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{quest}", Chat.strip(qItemStack.getName()));
            placeholders.put("{requirements}", String.join(", ", quests));
            ItemStack is = replaceItemStack(config.getItem("gui.quest-locked-display"), placeholders);
            return is;
        } else if (status == QuestStartResult.QUEST_ALREADY_COMPLETED) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{quest}", Chat.strip(qItemStack.getName()));
            ItemStack is = replaceItemStack(config.getItem("gui.quest-completed-display"), placeholders);
            return is;
        } else if (status == QuestStartResult.QUEST_NO_PERMISSION) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{quest}", Chat.strip(qItemStack.getName()));
            ItemStack is = replaceItemStack(config.getItem("gui.quest-permission-display"), placeholders);
            return is;
        } else if (cooldown > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{time}", Format.formatTime(TimeUnit.SECONDS.convert(cooldown, TimeUnit.MILLISECONDS)));
            placeholders.put("{quest}", Chat.strip(qItemStack.getName()));
            ItemStack is = replaceItemStack(config.getItem("gui.quest-cooldown-display"), placeholders);
            return is;
        } else {
            return replaceItemStack(qItemStack.toItemStack(quest, owner, questProgress));
        }
    }

    private ItemStack replaceItemStack(ItemStack is) {
        return replaceItemStack(is, Collections.emptyMap());
    }

    private ItemStack replaceItemStack(ItemStack is, Map<String, String> placeholders) {
        ItemStack newItemStack = is.clone();
        List<String> lore = newItemStack.getItemMeta().getLore();
        List<String> newLore = new ArrayList<>();
        ItemMeta ism = newItemStack.getItemMeta();
        Player player = Bukkit.getPlayer(owner.getPlayerUUID());
        if (lore != null) {
            for (String s : lore) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    s = s.replace(entry.getKey(), entry.getValue());
                    if (plugin.getPlaceholderAPIHook() != null && plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi")) {
                        s = plugin.getPlaceholderAPIHook().replacePlaceholders(player, s);
                    }
                }
                newLore.add(s);
            }
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            ism.setDisplayName(ism.getDisplayName().replace(entry.getKey(), entry.getValue()));
            if (plugin.getPlaceholderAPIHook() != null && plugin.getQuestsConfig().getBoolean("options.gui-use-placeholderapi")) {
                ism.setDisplayName(plugin.getPlaceholderAPIHook().replacePlaceholders(player, ism.getDisplayName()));
            }
        }
        ism.setLore(newLore);
        newItemStack.setItemMeta(ism);
        return newItemStack;
    }
}
